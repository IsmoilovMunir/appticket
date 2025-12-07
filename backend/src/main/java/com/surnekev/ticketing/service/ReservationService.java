package com.surnekev.ticketing.service;

import com.surnekev.ticketing.domain.Concert;
import com.surnekev.ticketing.domain.Reservation;
import com.surnekev.ticketing.domain.ReservationStatus;
import com.surnekev.ticketing.domain.Seat;
import com.surnekev.ticketing.domain.SeatStatus;
import com.surnekev.ticketing.domain.Ticket;
import com.surnekev.ticketing.domain.TicketStatus;
import com.surnekev.ticketing.dto.CancelReservationRequest;
import com.surnekev.ticketing.dto.CreateReservationRequest;
import com.surnekev.ticketing.dto.ReservationCreatedEvent;
import com.surnekev.ticketing.dto.ReservationResponse;
import com.surnekev.ticketing.dto.SeatDto;
import com.surnekev.ticketing.dto.SeatStatusChangeEvent;
import com.surnekev.ticketing.mapper.SeatMapper;
import com.surnekev.ticketing.repository.ConcertRepository;
import com.surnekev.ticketing.repository.ReservationRepository;
import com.surnekev.ticketing.repository.SeatRepository;
import com.surnekev.ticketing.repository.TicketRepository;
import com.surnekev.ticketing.websocket.SeatEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ConcertRepository concertRepository;
    private final SeatRepository seatRepository;
    private final SeatHoldService seatHoldService;
    private final SeatEventPublisher seatEventPublisher;
    private final SeatMapper seatMapper;
    private final TelegramService telegramService;
    private final TicketService ticketService;
    private final TicketRepository ticketRepository;
    private final JdbcTemplate jdbcTemplate;
    private final EmailService emailService;

    @Value("${seatmap.hold-ttl-seconds:1800}")
    private long holdTtlSeconds;

    @Transactional
    public ReservationResponse createReservation(CreateReservationRequest request) {
        if (request.seatIds().size() > 10) {
            throw new IllegalArgumentException("Maximum of 10 seats per reservation");
        }

        Concert concert = concertRepository.findById(request.concertId())
                .orElseThrow(() -> new IllegalArgumentException("Concert not found"));

        Set<Long> seatIds = new HashSet<>(request.seatIds());
        Set<Seat> seats = new HashSet<>(seatRepository.findByIdIn(seatIds));
        if (seats.size() != seatIds.size()) {
            throw new IllegalArgumentException("One or more seats not found");
        }

        if (seats.stream().anyMatch(seat -> !seat.getConcert().getId().equals(concert.getId()))) {
            throw new IllegalArgumentException("Seat does not belong to concert");
        }

        if (seats.stream().anyMatch(seat -> seat.getStatus() == SeatStatus.SOLD || seat.getStatus() == SeatStatus.BLOCKED)) {
            throw new IllegalStateException("Seat already sold or blocked");
        }

        UUID reservationId = UUID.randomUUID();
        boolean locked = seatHoldService.acquire(seatIds, reservationId.toString());
        if (!locked) {
            return reuseExistingHold(request, seatIds);
        }

        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(holdTtlSeconds);

        Reservation reservation = Reservation.builder()
                .id(reservationId)
                .concert(concert)
                .buyerName(request.buyerName())
                .buyerPhone(request.buyerPhone())
                .buyerEmail(request.buyerEmail())
                .status(ReservationStatus.HELD)
                .expiresAt(expiresAt)
                .createdAt(now)
                .seats(seats)
                .build();

        var previousStatuses = seats.stream().collect(Collectors.toMap(Seat::getId, Seat::getStatus));
        seats.forEach(seat -> seat.setStatus(SeatStatus.HELD));
        Reservation saved = reservationRepository.save(reservation);
        seatRepository.saveAll(seats);
        ticketService.createDraftTickets(saved);

        publishSeatEvents(concert.getId(), seats, previousStatuses, SeatStatus.HELD, expiresAt);
        seatEventPublisher.publishReservation(concert.getId(), new ReservationCreatedEvent(saved.getId(),
                seatIds.stream().toList(), expiresAt));

        telegramService.notifyReservationHold(saved);
        return toResponse(saved);
    }

    @Transactional
    public ReservationResponse confirmReservation(UUID reservationId, String operator) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            return toResponse(reservation);
        }
        if (reservation.getStatus() == ReservationStatus.CANCELLED || reservation.getStatus() == ReservationStatus.EXPIRED) {
            throw new IllegalStateException("Reservation cannot be confirmed in status " + reservation.getStatus());
        }

        List<UUID> reservedTicketIds = ticketRepository.findAllByReservationId(reservationId).stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.RESERVED)
                .map(Ticket::getId)
                .toList();
        if (reservedTicketIds.isEmpty()) {
            return toResponse(reservation);
        }

        confirmTickets(reservedTicketIds, operator);
        Reservation updated = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        return toResponse(updated);
    }

    @Transactional
    public ReservationResponse cancelReservation(UUID reservationId, CancelReservationRequest request, boolean expired) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        if (reservation.getStatus() == ReservationStatus.CANCELLED || reservation.getStatus() == ReservationStatus.EXPIRED) {
            return toResponse(reservation);
        }
        if (reservation.getStatus() != ReservationStatus.HELD
                && reservation.getStatus() != ReservationStatus.PARTIALLY_CONFIRMED
                && reservation.getStatus() != ReservationStatus.PARTIALLY_CANCELLED) {
            throw new IllegalStateException("Reservation not cancellable in status " + reservation.getStatus());
        }

        List<UUID> reservedTicketIds = ticketRepository.findAllByReservationId(reservationId).stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.RESERVED)
                .map(Ticket::getId)
                .toList();
        if (reservedTicketIds.isEmpty()) {
            return toResponse(reservation);
        }

        cancelTickets(reservedTicketIds, request.operator(), request.reason(), expired);
        Reservation updated = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        return toResponse(updated);
    }

    @Transactional
    public List<Ticket> confirmTickets(List<UUID> ticketIds, String operator) {
        List<Ticket> tickets = loadTickets(ticketIds);
        List<Ticket> actionable = tickets.stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.RESERVED)
                .toList();
        if (actionable.isEmpty()) {
            return List.of();
        }
        var grouped = actionable.stream().collect(Collectors.groupingBy(Ticket::getReservation));
        grouped.forEach((reservation, reservationTickets) -> {
            Set<Seat> seats = reservationTickets.stream()
                    .map(Ticket::getSeat)
                    .collect(Collectors.toSet());
            Map<Long, SeatStatus> previousStatuses = seats.stream()
                    .collect(Collectors.toMap(Seat::getId, Seat::getStatus));
            seats.forEach(seat -> seat.setStatus(SeatStatus.SOLD));
            seatRepository.saveAll(seats);
            seatHoldService.release(seats.stream().map(Seat::getId).collect(Collectors.toSet()));
            publishSeatEvents(reservation.getConcert().getId(), seats, previousStatuses, SeatStatus.SOLD, null);
        });

        ticketService.confirmTickets(actionable);
        grouped.keySet().forEach(reservation -> recalculateReservationStatus(reservation, operator, false));
        return actionable;
    }

    @Transactional
    public List<Ticket> cancelTickets(List<UUID> ticketIds, String operator, String reason, boolean expired) {
        List<Ticket> tickets = loadTickets(ticketIds);
        List<Ticket> actionable = tickets.stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.RESERVED)
                .toList();
        if (actionable.isEmpty()) {
            return List.of();
        }
        var grouped = actionable.stream().collect(Collectors.groupingBy(Ticket::getReservation));
        grouped.forEach((reservation, reservationTickets) -> {
            Set<Seat> seats = reservationTickets.stream()
                    .map(Ticket::getSeat)
                    .collect(Collectors.toSet());
            Map<Long, SeatStatus> previousStatuses = seats.stream()
                    .collect(Collectors.toMap(Seat::getId, Seat::getStatus));
            seats.forEach(seat -> seat.setStatus(SeatStatus.AVAILABLE));
            seatRepository.saveAll(seats);
            seatHoldService.release(seats.stream().map(Seat::getId).collect(Collectors.toSet()));
            publishSeatEvents(reservation.getConcert().getId(), seats, previousStatuses, SeatStatus.AVAILABLE, null);
        });

        ticketService.cancelTickets(actionable);
        grouped.keySet().forEach(reservation -> recalculateReservationStatus(reservation, operator, expired));
        return actionable;
    }

    @Transactional
    public List<Ticket> refundTickets(List<UUID> ticketIds, String operator, String reason) {
        List<Ticket> tickets = loadTickets(ticketIds);
        List<Ticket> actionable = tickets.stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.SOLD)
                .toList();
        if (actionable.isEmpty()) {
            return List.of();
        }
        var grouped = actionable.stream().collect(Collectors.groupingBy(Ticket::getReservation));
        grouped.forEach((reservation, reservationTickets) -> {
            Set<Seat> seats = reservationTickets.stream()
                    .map(Ticket::getSeat)
                    .collect(Collectors.toSet());
            Map<Long, SeatStatus> previousStatuses = seats.stream()
                    .collect(Collectors.toMap(Seat::getId, Seat::getStatus));
            seats.forEach(seat -> seat.setStatus(SeatStatus.AVAILABLE));
            seatRepository.saveAll(seats);
            seatHoldService.release(seats.stream().map(Seat::getId).collect(Collectors.toSet()));
            publishSeatEvents(reservation.getConcert().getId(), seats, previousStatuses, SeatStatus.AVAILABLE, null);
        });

        ticketService.cancelTickets(actionable);
        grouped.keySet().forEach(reservation -> recalculateReservationStatus(reservation, operator, false));
        
        // Отправляем email-уведомления о возврате для каждой резервации
        grouped.forEach((reservation, reservationTickets) -> {
            try {
                emailService.sendRefundNotification(reservation, reservationTickets, reason);
            } catch (Exception e) {
                log.warn("Failed to send refund notification email for reservation {}: {}", 
                        reservation.getId(), e.getMessage());
            }
        });
        
        log.info("Refunded {} tickets by operator {}: {}", actionable.size(), operator, reason);
        return actionable;
    }

    @Transactional
    public void expireReservation(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElse(null);
        if (reservation == null) {
            return;
        }
        List<UUID> reservedTicketIds = ticketRepository.findAllByReservationId(reservationId).stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.RESERVED)
                .map(Ticket::getId)
                .toList();
        if (reservedTicketIds.isEmpty()) {
            recalculateReservationStatus(reservation, "system", true);
            return;
        }
        cancelTickets(reservedTicketIds, "system", "expired", true);
    }

    private void publishSeatEvents(Long concertId,
                                   Set<Seat> seats,
                                   java.util.Map<Long, SeatStatus> previousStatuses,
                                   SeatStatus newStatus,
                                   Instant expiresAt) {
        seats.forEach(seat -> {
            SeatStatus oldStatus = previousStatuses.getOrDefault(seat.getId(), seat.getStatus());
            seatEventPublisher.publishSeatStatus(concertId,
                    new SeatStatusChangeEvent(seat.getId(), oldStatus, newStatus, expiresAt));
        });
    }

    private ReservationResponse toResponse(Reservation reservation) {
        List<SeatDto> seats = reservation.getSeats().stream()
                .map(seat -> seatMapper.toDto(seat, null))
                .toList();
        return new ReservationResponse(reservation.getId(), reservation.getStatus(), reservation.getExpiresAt(), seats);
    }

    private ReservationResponse reuseExistingHold(CreateReservationRequest request, Set<Long> seatIds) {
        Long sampleSeatId = seatIds.iterator().next();
        return seatHoldService.currentReservationForSeat(sampleSeatId)
                .flatMap(reservationRepository::findById)
                .filter(res -> res.getStatus() == ReservationStatus.HELD)
                .filter(res -> seatIdsMatch(res, seatIds))
                .filter(res -> buyersMatch(res, request))
                .map(res -> refreshExistingHold(res))
                .orElseThrow(() -> new IllegalStateException("Seats already held"));
    }

    private boolean seatIdsMatch(Reservation reservation, Set<Long> seatIds) {
        Set<Long> existing = reservation.getSeats().stream()
                .map(Seat::getId)
                .collect(Collectors.toSet());
        return existing.equals(seatIds);
    }

    private boolean buyersMatch(Reservation reservation, CreateReservationRequest request) {
        return equalsIgnoreBlank(reservation.getBuyerPhone(), request.buyerPhone())
                || equalsIgnoreBlank(reservation.getBuyerEmail(), request.buyerEmail());
    }

    private boolean equalsIgnoreBlank(String existing, String incoming) {
        String left = normalize(existing);
        String right = normalize(incoming);
        return left != null && left.equals(right);
    }

    private String normalize(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private ReservationResponse refreshExistingHold(Reservation reservation) {
        Instant newExpiry = Instant.now().plusSeconds(holdTtlSeconds);
        reservation.setExpiresAt(newExpiry);
        reservationRepository.save(reservation);
        Set<Long> ids = reservation.getSeats().stream()
                .map(Seat::getId)
                .collect(Collectors.toSet());
        seatHoldService.refresh(ids);
        return toResponse(reservation);
    }

    private List<Ticket> loadTickets(List<UUID> ticketIds) {
        if (ticketIds == null || ticketIds.isEmpty()) {
            return List.of();
        }
        List<UUID> uniqueIds = ticketIds.stream().distinct().toList();
        List<Ticket> tickets = ticketRepository.findAllByIdIn(uniqueIds);
        if (tickets.size() != uniqueIds.size()) {
            throw new IllegalArgumentException("One or more tickets not found");
        }
        return tickets;
    }

    private void ensureReservationStatusConstraint() {
        try {
            String fixConstraint = """
                    DO $$
                    BEGIN
                        ALTER TABLE reservations DROP CONSTRAINT IF EXISTS reservations_status_check;
                        ALTER TABLE reservations
                        ADD CONSTRAINT reservations_status_check
                        CHECK (status IN (
                            'HELD',
                            'PARTIALLY_CONFIRMED',
                            'PARTIALLY_CANCELLED',
                            'CONFIRMED',
                            'CANCELLED',
                            'EXPIRED'
                        ));
                    END $$;
                    """;
            jdbcTemplate.execute(fixConstraint);
            log.debug("Reservation status constraint ensured to include partial states.");
        } catch (Exception e) {
            log.warn("Failed to ensure reservation status constraint: {}", e.getMessage());
            // Не бросаем исключение, чтобы не прервать основную операцию
        }
    }

    private void recalculateReservationStatus(Reservation reservation, String operator, boolean expired) {
        List<Ticket> allTickets = ticketRepository.findAllByReservationId(reservation.getId());
        boolean hasReserved = allTickets.stream().anyMatch(ticket -> ticket.getStatus() == TicketStatus.RESERVED);
        boolean hasSold = allTickets.stream().anyMatch(ticket ->
                ticket.getStatus() == TicketStatus.SOLD || ticket.getStatus() == TicketStatus.USED);
        boolean hasCancelled = allTickets.stream().anyMatch(ticket -> ticket.getStatus() == TicketStatus.CANCELLED);

        ReservationStatus newStatus;
        if (hasReserved) {
            if (hasSold) {
                newStatus = ReservationStatus.PARTIALLY_CONFIRMED;
            } else if (hasCancelled) {
                newStatus = ReservationStatus.PARTIALLY_CANCELLED;
            } else {
                newStatus = ReservationStatus.HELD;
            }
        } else if (hasSold) {
            newStatus = ReservationStatus.CONFIRMED;
        } else {
            newStatus = expired ? ReservationStatus.EXPIRED : ReservationStatus.CANCELLED;
        }

        if (newStatus != reservation.getStatus()) {
            // Если новый статус требует PARTIALLY_CONFIRMED или PARTIALLY_CANCELLED,
            // убеждаемся, что ограничение в базе данных позволяет эти значения
            if (newStatus == ReservationStatus.PARTIALLY_CONFIRMED || newStatus == ReservationStatus.PARTIALLY_CANCELLED) {
                ensureReservationStatusConstraint();
            }
            
            reservation.setStatus(newStatus);
            if (newStatus == ReservationStatus.CONFIRMED
                    || newStatus == ReservationStatus.CANCELLED
                    || newStatus == ReservationStatus.EXPIRED) {
                reservation.setExpiresAt(Instant.now());
            }
            if (StringUtils.hasText(operator)) {
                reservation.setCreatedBy(operator);
            }
            reservationRepository.save(reservation);
            telegramService.notifyReservationStatus(reservation);
        } else {
            reservationRepository.save(reservation);
        }
    }
}

