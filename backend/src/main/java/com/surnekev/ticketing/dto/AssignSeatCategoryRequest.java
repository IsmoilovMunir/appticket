package com.surnekev.ticketing.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AssignSeatCategoryRequest(
        @NotNull
        Long seatCategoryId,
        Long concertId,
        @NotEmpty
        List<@Min(1) Integer> tableNumbers
) {
}

