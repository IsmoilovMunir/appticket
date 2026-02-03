package com.surnekev.ticketing.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CategoryQuantityRequest(
        @NotNull Long categoryId,
        @Min(1) int quantity
) {
}
