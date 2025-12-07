package com.surnekev.ticketing.dto;

import com.surnekev.ticketing.domain.SeatStatus;

import java.time.Instant;

public record SeatStatusChangeEvent(
        Long seatId,
        SeatStatus oldStatus,
        SeatStatus newStatus,
        Instant expiresAt
) {
}

