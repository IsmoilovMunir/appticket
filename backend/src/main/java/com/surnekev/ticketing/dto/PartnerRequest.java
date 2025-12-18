package com.surnekev.ticketing.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PartnerRequest(
        @NotBlank(message = "ФИО обязательно")
        String fullName,
        
        @NotBlank(message = "Название компании обязательно")
        String company,
        
        @NotBlank(message = "Номер телефона обязателен")
        String phone,
        
        @NotBlank(message = "Email обязателен")
        @Email(message = "Некорректный email")
        String email
) {
}
