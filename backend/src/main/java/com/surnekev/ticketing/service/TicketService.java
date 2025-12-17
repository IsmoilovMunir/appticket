package com.surnekev.ticketing.service;

import com.surnekev.ticketing.domain.CheckinResult;
import com.surnekev.ticketing.domain.Reservation;
import com.surnekev.ticketing.domain.Seat;
import com.surnekev.ticketing.domain.Ticket;
import com.surnekev.ticketing.domain.TicketStatus;
import com.surnekev.ticketing.dto.CheckinResponse;
import com.surnekev.ticketing.dto.SeatDto;
import com.surnekev.ticketing.dto.TicketDto;
import com.surnekev.ticketing.mapper.SeatMapper;
import com.surnekev.ticketing.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final TicketRepository ticketRepository;
    private final SeatMapper seatMapper;
    private final QrCodeService qrCodeService;
    private final TelegramService telegramService;
    private final TicketPdfService ticketPdfService;
    private final EmailService emailService;

    public void createDraftTickets(Reservation reservation) {
        if (ticketRepository.countByReservationId(reservation.getId()) > 0) {
            return;
        }
        Instant now = Instant.now();
        List<Ticket> drafts = reservation.getSeats().stream()
                .map(seat -> Ticket.builder()
                        .reservation(reservation)
                        .seat(seat)
                        .buyerName(reservation.getBuyerName())
                        .buyerPhone(reservation.getBuyerPhone())
                        .ticketToken(UUID.randomUUID().toString())
                        .status(TicketStatus.RESERVED)
                        .issuedAt(now)
                        .build())
                .toList();
        ticketRepository.saveAll(drafts);
    }

    @Transactional
    public void confirmTickets(List<Ticket> tickets) {
        if (tickets == null || tickets.isEmpty()) {
            return;
        }
        tickets.stream()
                .collect(Collectors.groupingBy(Ticket::getReservation))
                .forEach((reservation, reservationTickets) -> {
                    List<EmailService.TicketAttachment> attachments = new java.util.ArrayList<>();
                    List<Ticket> processed = new java.util.ArrayList<>();
                    reservationTickets.forEach(ticket -> {
                        if (ticket.getStatus() != TicketStatus.RESERVED) {
                            return;
                        }
                        byte[] qrBytes = qrCodeService.generatePng(ticket.getTicketToken());
                        ticket.setStatus(TicketStatus.SOLD);
                        ticket.setQrCodeUrl(qrCodeService.asDataUrl(qrBytes));
                        byte[] pdfBytes = ticketPdfService.renderTicket(ticket, qrBytes);
                        ticket.setPdfDocument(pdfBytes);
                        ticketRepository.save(ticket);
                        telegramService.sendTicketQr(ticket, qrBytes);
                        telegramService.sendTicketPdf(ticket, pdfBytes);
                        attachments.add(new EmailService.TicketAttachment(buildAttachmentName(ticket), pdfBytes));
                        processed.add(ticket);
                    });
                    if (!attachments.isEmpty()) {
                        try {
                            emailService.sendTickets(reservation, attachments, processed);
                        } catch (Exception e) {
                            log.warn("Failed to send ticket e-mail for reservation {}: {}", reservation.getId(), e.getMessage());
                        }
                    }
                });
    }

    @Transactional
    public void cancelTickets(List<Ticket> tickets) {
        if (tickets == null || tickets.isEmpty()) {
            return;
        }
        tickets.forEach(ticket -> {
            if (ticket.getStatus() == TicketStatus.CANCELLED) {
                return;
            }
            ticket.setStatus(TicketStatus.CANCELLED);
            ticketRepository.save(ticket);
        });
    }

    public List<Ticket> findTickets(Optional<TicketStatus> status) {
        List<Ticket> tickets = status
                .map(ticketRepository::findAllByStatus)
                .orElseGet(ticketRepository::findAll);
        return tickets.stream()
                .sorted(Comparator.comparing(Ticket::getStatus).thenComparing(Ticket::getIssuedAt).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TicketDto> findTicketDtos(Optional<TicketStatus> status) {
        return findTickets(status).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<TicketDto> searchTickets(String phone, String name, String ticketId) {
        List<Ticket> tickets;
        if (ticketId != null && !ticketId.trim().isEmpty()) {
            try {
                UUID id = UUID.fromString(ticketId.trim());
                Ticket ticket = ticketRepository.findDetailedById(id).orElse(null);
                tickets = ticket != null ? List.of(ticket) : List.of();
            } catch (IllegalArgumentException e) {
                tickets = List.of();
            }
        } else if (phone != null && !phone.trim().isEmpty()) {
            tickets = ticketRepository.findAllByBuyerPhoneContainingIgnoreCase(phone.trim());
        } else if (name != null && !name.trim().isEmpty()) {
            tickets = ticketRepository.findAllByBuyerNameContainingIgnoreCase(name.trim());
        } else {
            tickets = List.of();
        }
        return tickets.stream()
                .sorted(Comparator.comparing(Ticket::getIssuedAt).reversed())
                .map(this::toDto)
                .toList();
    }

    public Ticket getTicket(UUID ticketId) {
        return ticketRepository.findDetailedById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
    }

    /**
     * Предварительная проверка билета без изменения его статуса
     */
    @Transactional(readOnly = true)
    public CheckinResponse previewCheckIn(String token) {
        var ticket = ticketRepository.findByTicketToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        if (ticket.getStatus() == TicketStatus.CANCELLED) {
            return new CheckinResponse(CheckinResult.INVALID, ticket.getBuyerName(),
                    seatMapper.toDto(ticket.getSeat(), null), ticket.getCheckedInAt());
        }

        if (ticket.getStatus() == TicketStatus.RESERVED) {
            return new CheckinResponse(CheckinResult.INVALID, ticket.getBuyerName(),
                    seatMapper.toDto(ticket.getSeat(), null), ticket.getCheckedInAt());
        }

        if (ticket.getStatus() == TicketStatus.USED) {
            return new CheckinResponse(CheckinResult.DUPLICATE, ticket.getBuyerName(),
                    seatMapper.toDto(ticket.getSeat(), null), ticket.getCheckedInAt());
        }

        if (ticket.getStatus() != TicketStatus.SOLD) {
            return new CheckinResponse(CheckinResult.INVALID, ticket.getBuyerName(),
                    seatMapper.toDto(ticket.getSeat(), null), ticket.getCheckedInAt());
        }

        // Билет валиден и готов к регистрации, но статус не меняем
        SeatDto seatDto = seatMapper.toDto(ticket.getSeat(), null);
        return new CheckinResponse(CheckinResult.APPROVED, ticket.getBuyerName(), seatDto, ticket.getCheckedInAt());
    }

    @Transactional
    public CheckinResponse checkIn(String token, String operator) {
        var ticket = ticketRepository.findByTicketToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        if (ticket.getStatus() == TicketStatus.CANCELLED) {
            return new CheckinResponse(CheckinResult.INVALID, ticket.getBuyerName(),
                    seatMapper.toDto(ticket.getSeat(), null), ticket.getCheckedInAt());
        }

        if (ticket.getStatus() == TicketStatus.RESERVED) {
            return new CheckinResponse(CheckinResult.INVALID, ticket.getBuyerName(),
                    seatMapper.toDto(ticket.getSeat(), null), ticket.getCheckedInAt());
        }

        if (ticket.getStatus() == TicketStatus.USED) {
            return new CheckinResponse(CheckinResult.DUPLICATE, ticket.getBuyerName(),
                    seatMapper.toDto(ticket.getSeat(), null), ticket.getCheckedInAt());
        }

        if (ticket.getStatus() != TicketStatus.SOLD) {
            throw new IllegalStateException("Ticket not eligible for check-in");
        }

        ticket.setStatus(TicketStatus.USED);
        ticket.setCheckedInAt(Instant.now());
        ticket.setCheckedInBy(operator);
        ticketRepository.save(ticket);
        SeatDto seatDto = seatMapper.toDto(ticket.getSeat(), null);
        return new CheckinResponse(CheckinResult.APPROVED, ticket.getBuyerName(), seatDto, ticket.getCheckedInAt());
    }

    public TicketDto toDto(Ticket ticket) {
        return new TicketDto(
                ticket.getId(),
                ticket.getReservation().getId(),
                ticket.getStatus(),
                seatMapper.toDto(ticket.getSeat(), null),
                ticket.getBuyerName(),
                ticket.getBuyerPhone(),
                ticket.getQrCodeUrl(),
                ticket.getIssuedAt(),
                ticket.getCheckedInAt()
        );
    }

    /**
     * Получить список использованных билетов (только для админа)
     */
    @Transactional(readOnly = true)
    public List<TicketDto> getUsedTickets() {
        return ticketRepository.findAllByStatus(TicketStatus.USED).stream()
                .sorted(Comparator.comparing(Ticket::getCheckedInAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::toDto)
                .toList();
    }

    /**
     * Вернуть билет из статуса USED обратно в SOLD (только для админа)
     */
    @Transactional
    public TicketDto revertTicketStatus(UUID ticketId, String operator) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        if (ticket.getStatus() != TicketStatus.USED) {
            throw new IllegalStateException("Ticket is not in USED status. Current status: " + ticket.getStatus());
        }

        ticket.setStatus(TicketStatus.SOLD);
        ticket.setCheckedInAt(null);
        ticket.setCheckedInBy(null);
        ticketRepository.save(ticket);

        return toDto(ticket);
    }

    private String buildAttachmentName(Ticket ticket) {
        Seat seat = ticket.getSeat();
        return "ticket-table-%d-seat-%d.pdf".formatted(seat.getTableNumber(), seat.getChairNumber());
    }
}

