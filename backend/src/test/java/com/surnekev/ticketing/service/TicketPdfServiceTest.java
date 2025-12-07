package com.surnekev.ticketing.service;

import com.surnekev.ticketing.domain.Concert;
import com.surnekev.ticketing.domain.Reservation;
import com.surnekev.ticketing.domain.Seat;
import com.surnekev.ticketing.domain.SeatCategory;
import com.surnekev.ticketing.domain.Ticket;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TicketPdfServiceTest {

    @Test
    void rendersTicketPdfForValidData() {
        TicketPdfService service = new TicketPdfService();

        SeatCategory category = SeatCategory.builder()
                .name("VIP")
                .priceCents(1_000_000)
                .colorHex("#FFFFFF")
                .build();

        Seat seat = Seat.builder()
                .tableNumber(1)
                .chairNumber(1)
                .category(category)
                .build();

        Concert concert = Concert.builder()
                .title("«Test & Friends» — best hits")
                .venue("Клуб «Совы & Co» <LOFT>")
                .concertDate(Instant.now())
                .build();

        Reservation reservation = Reservation.builder()
                .buyerName("ООО «Лучший & Партнеры»")
                .concert(concert)
                .build();

        Ticket ticket = Ticket.builder()
                .id(UUID.randomUUID())
                .reservation(reservation)
                .seat(seat)
                .ticketToken("test-token")
                .build();

        QrCodeService qrCodeService = new QrCodeService();
        byte[] qr = qrCodeService.generatePng(ticket.getTicketToken());
        byte[] pdf = service.renderTicket(ticket, qr);

        assertThat(pdf).isNotEmpty();
    }
}

