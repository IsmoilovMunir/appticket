package com.surnekev.ticketing.dto;

import com.surnekev.ticketing.domain.AdminRole;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record AdminUserDto(
        UUID id,
        String username,
        Set<AdminRole> roles,
        boolean enabled,
        Instant createdAt
) {
}

