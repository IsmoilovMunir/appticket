package com.surnekev.ticketing.dto;

import jakarta.validation.constraints.NotBlank;

public record CancelReservationRequest(
        @NotBlank String operator,
        String reason
) {
}

