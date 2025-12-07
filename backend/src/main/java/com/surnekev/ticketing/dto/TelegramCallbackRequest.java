package com.surnekev.ticketing.dto;

import jakarta.validation.constraints.NotBlank;

public record TelegramCallbackRequest(
        @NotBlank String reservationId,
        @NotBlank String action,
        Long telegramMessageId
) {
}

