package com.surnekev.ticketing.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record TicketBulkActionRequest(
        @NotEmpty List<UUID> ticketIds
) {
}

