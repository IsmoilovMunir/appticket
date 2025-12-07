package com.surnekev.ticketing.dto;

import com.surnekev.ticketing.domain.TicketStatus;

import java.time.Instant;
import java.util.UUID;

public record TicketDto(
        UUID id,
        UUID reservationId,
        TicketStatus status,
        SeatDto seat,
        String buyerName,
        String buyerPhone,
        String qrCodeUrl,
        Instant issuedAt,
        Instant checkedInAt
) {
}

