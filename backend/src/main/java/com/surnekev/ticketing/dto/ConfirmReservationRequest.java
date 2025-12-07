package com.surnekev.ticketing.dto;

import jakarta.validation.constraints.NotBlank;

public record ConfirmReservationRequest(
        @NotBlank String operator
) {
}

