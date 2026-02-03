package com.surnekev.ticketing.dto;

import java.time.Instant;

public record ConcertDto(
        Long id,
        String title,
        String slug,
        String description,
        Instant concertDate,
        Instant eventStartTime,
        Instant guestStartTime,
        String venue,
        String city,
        Double venueLat,
        Double venueLon,
        String currency,
        String ageRestriction,
        String eventType,
        String posterUrl,
        String salesSchemeUrl,
        Integer minTicketPriceCents,
        boolean simpleMode,
        String telegramManagerChatIds
) {
}

