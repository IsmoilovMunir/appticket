package com.surnekev.ticketing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterManagerRequest(
        @NotBlank(message = "Имя пользователя обязательно")
        @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
        String username,
        
        @NotBlank(message = "Пароль обязателен")
        @Size(min = 8, message = "Пароль должен содержать минимум 8 символов")
        String password
) {
}

