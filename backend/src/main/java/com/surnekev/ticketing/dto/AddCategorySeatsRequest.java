package com.surnekev.ticketing.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request for adding seats for a single category to existing seats.
 */
public record AddCategorySeatsRequest(
        @NotNull Long concertId,
        @NotNull Long categoryId,
        @NotNull @Min(1) Integer quantity
) {}
