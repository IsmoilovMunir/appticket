package com.surnekev.ticketing.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record BulkCreateSeatsRequest(
    @NotNull Long concertId,
    @NotNull Long categoryId,
    @NotEmpty List<TableSeatsConfig> tables,
    DanceFloorConfig danceFloor
) {
    public record TableSeatsConfig(
        @NotNull Integer tableNumber,
        @NotNull Integer chairsCount
    ) {}

    public record DanceFloorConfig(
        @NotNull Integer capacity
    ) {}
}