package com.surnekev.ticketing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record CreateConcertRequest(
    @NotBlank String title,
    @NotBlank String slug,
    String description,
    @NotNull Instant concertDate,
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
    Boolean simpleMode,
    String telegramManagerChatIds
) {
}