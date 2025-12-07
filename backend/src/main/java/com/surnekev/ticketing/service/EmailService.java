package com.surnekev.ticketing.service;

import com.surnekev.ticketing.domain.Reservation;
import com.surnekev.ticketing.domain.Seat;
import com.surnekev.ticketing.domain.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.lang.NonNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${mail.from:no-reply@surnekevents.ru}")
    private String fromAddress;

    public void sendTickets(Reservation reservation, List<TicketAttachment> attachments, List<Ticket> confirmedTickets) {
        if (attachments == null || attachments.isEmpty()) {
            return;
        }
        String buyerEmail = reservation.getBuyerEmail();
        if (!StringUtils.hasText(buyerEmail)) {
            log.debug("Reservation {} does not have email, skipping ticket sending", reservation.getId());
            return;
        }
        if (!isConfigured()) {
            log.debug("Mail sender host is not configured, skipping email delivery");
            return;
        }
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
            String from = StringUtils.hasText(fromAddress) ? fromAddress : "no-reply@surnekevents.ru";
            helper.setFrom(Objects.requireNonNull(from));
            helper.setTo(Objects.requireNonNull(buyerEmail));
            String subjectTitle = safe(reservation.getConcert().getTitle());
            String subject = "Ваши билеты на " + subjectTitle;
            helper.setSubject(Objects.requireNonNull(subject));
            helper.setText(buildBody(reservation, confirmedTickets), false);

            for (TicketAttachment attachment : attachments) {
                helper.addAttachment(
                        attachment.fileName(),
                        new ByteArrayResource(attachment.data()),
                        "application/pdf");
            }

            mailSender.send(mimeMessage);
        } catch (Exception ex) {
            log.error("Failed to send tickets for reservation {}", reservation.getId(), ex);
        }
    }

    private boolean isConfigured() {
        if (mailSender instanceof JavaMailSenderImpl impl) {
            return StringUtils.hasText(impl.getHost());
        }
        return true;
    }

    private String buildBody(Reservation reservation, List<Ticket> confirmedTickets) {
        String concertTitle = safe(reservation.getConcert().getTitle());
        String dateTime = reservation.getConcert().getConcertDate() != null
                ? DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
                .withZone(ZoneId.systemDefault())
                .format(reservation.getConcert().getConcertDate())
                : "Уточняется";
        StringBuilder seats = new StringBuilder();
        if (confirmedTickets != null && !confirmedTickets.isEmpty()) {
            confirmedTickets.forEach(ticket -> appendSeatLine(seats, ticket.getSeat()));
        } else {
            reservation.getSeats().forEach(seat -> appendSeatLine(seats, seat));
        }

        return """
                Здравствуйте, %s!

                Ваши билеты на концерт "%s" подтверждены.
                Дата и время: %s
                Место проведения: %s

                Забронированные места:
                %s

                Во вложении находятся файлы в формате PDF. Покажите билет на входе или распечатайте заранее.

                С уважением,
                команда Surnek Events
                """.formatted(
                safe(reservation.getBuyerName()),
                concertTitle,
                dateTime,
                safe(reservation.getConcert().getVenue()),
                seats.toString().isBlank() ? "—" + System.lineSeparator() : seats);
    }

    private void appendSeatLine(StringBuilder builder, Seat seat) {
        if (seat == null) {
            return;
        }
        builder.append("- Стол ")
                .append(seat.getTableNumber())
                .append(", место ")
                .append(seat.getChairNumber())
                .append(System.lineSeparator());
    }

    private String safe(String value) {
        return value == null ? "—" : value;
    }

    public record TicketAttachment(@NonNull String fileName, @NonNull byte[] data) {
    }
}


