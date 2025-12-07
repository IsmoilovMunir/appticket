package com.surnekev.ticketing.dto;

public record SeatTableAssignmentDto(
        int tableNumber,
        Long seatCategoryId,
        String seatCategoryName,
        int basePriceCents,
        boolean hasOverrides,
        Integer overridePriceCents,
        String seatCategoryColorHex
) {
}

