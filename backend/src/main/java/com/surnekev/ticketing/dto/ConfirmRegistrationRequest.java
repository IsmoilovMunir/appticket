package com.surnekev.ticketing.dto;

import jakarta.validation.constraints.NotBlank;

public record ConfirmRegistrationRequest(
        @NotBlank(message = "Имя пользователя обязательно")
        String username,
        
        @NotBlank(message = "Код подтверждения обязателен")
        String verificationCode
) {
}

