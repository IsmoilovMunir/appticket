package com.surnekev.ticketing.dto;

public record SeatCategoryDto(
        Long id,
        String name,
        int priceCents,
        String description,
        String colorHex
) {
}

