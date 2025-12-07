package com.surnekev.ticketing.dto;

import com.surnekev.ticketing.domain.ReservationStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ReservationResponse(
        UUID reservationId,
        ReservationStatus status,
        Instant expiresAt,
        List<SeatDto> seats
) {
}

