package com.surnekev.ticketing.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ReservationCreatedEvent(
        UUID reservationId,
        List<Long> seatIds,
        Instant expiresAt
) {
}

