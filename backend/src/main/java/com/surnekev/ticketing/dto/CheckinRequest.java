package com.surnekev.ticketing.dto;

import jakarta.validation.constraints.NotBlank;

public record CheckinRequest(
        @NotBlank String ticketToken
) {
}

