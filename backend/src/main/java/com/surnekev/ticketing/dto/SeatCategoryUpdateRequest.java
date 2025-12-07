package com.surnekev.ticketing.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SeatCategoryUpdateRequest(
        @NotBlank
        String name,
        @Min(0)
        Integer priceCents,
        String description,
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be in #RRGGBB format")
        String colorHex
) {
}

