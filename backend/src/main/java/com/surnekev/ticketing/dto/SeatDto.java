package com.surnekev.ticketing.dto;

import com.surnekev.ticketing.domain.SeatStatus;

import java.time.Instant;

public record SeatDto(
        Long id,
        int tableNumber,
        int chairNumber,
        String categoryName,
        String categoryColorHex,
        int priceCents,
        Integer priceOverrideCents,
        SeatStatus status,
        String blockedReason,
        Instant holdExpiresAt
) {
}

