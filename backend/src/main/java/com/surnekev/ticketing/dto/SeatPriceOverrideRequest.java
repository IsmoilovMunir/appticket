package com.surnekev.ticketing.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SeatPriceOverrideRequest(
        Long concertId,
        @NotNull
        @Min(1)
        Integer tableNumber,
        @Min(1)
        Integer chairNumber,
        Integer priceCents
) {
}

