package com.surnekev.ticketing.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Request for creating seats in simple mode (no seat map).
 * Each category gets a number of "slots" - seats with tableNumber=categoryIndex, chairNumber=1..quantity.
 */
public record SimpleCreateSeatsRequest(
        @NotNull Long concertId,
        @NotEmpty @Valid List<CategoryQuantityItem> categories
) {
    public record CategoryQuantityItem(
            @NotNull Long categoryId,
            @NotNull @Min(1) Integer quantity
    ) {}
}
