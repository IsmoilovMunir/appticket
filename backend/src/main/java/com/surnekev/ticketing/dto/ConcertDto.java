package com.surnekev.ticketing.dto;

import java.time.Instant;

public record ConcertDto(
        Long id,
        String title,
        String description,
        Instant concertDate,
        String venue,
        String posterUrl
) {
}

