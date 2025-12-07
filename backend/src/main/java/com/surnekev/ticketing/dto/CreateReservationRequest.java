package com.surnekev.ticketing.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateReservationRequest(
        @NotNull Long concertId,
        @NotEmpty @Size(max = 10) List<Long> seatIds,
        @NotEmpty String buyerName,
        @NotEmpty String buyerPhone,
        @Email String buyerEmail
) {
}

