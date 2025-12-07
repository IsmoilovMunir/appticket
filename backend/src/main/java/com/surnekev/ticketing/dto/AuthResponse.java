package com.surnekev.ticketing.dto;

public record AuthResponse(
        String accessToken,
        long expiresIn
) {
}

