package com.surnekev.ticketing.dto;

public record SeatCategoryWithAvailabilityDto(
        Long id,
        String name,
        int priceCents,
        String description,
        String colorHex,
        int availableCount
) {
}
