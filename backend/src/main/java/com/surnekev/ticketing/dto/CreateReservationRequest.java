package com.surnekev.ticketing.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateReservationRequest(
        @NotNull Long concertId,
        @Size(max = 10) List<Long> seatIds,
        @Valid @Size(max = 10) List<CategoryQuantityRequest> categoryQuantities,
        @NotEmpty String buyerName,
        String buyerPhone,
        @Email String buyerEmail
) {
    /**
     * Either seatIds or categoryQuantities must be provided (for seat map vs simple mode).
     */
    public boolean hasSeatSelection() {
        boolean hasSeats = seatIds != null && !seatIds.isEmpty();
        boolean hasCategories = categoryQuantities != null && !categoryQuantities.isEmpty()
                && categoryQuantities.stream().anyMatch(cq -> cq.quantity() > 0);
        return hasSeats || hasCategories;
    }
}

