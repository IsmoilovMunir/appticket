package com.surnekev.ticketing.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SeatPriceOverrideRequest(
        Long concertId,
        Integer tableNumber,
        Integer chairNumber,
        Integer priceCents,
        Long seatId
) {
}

