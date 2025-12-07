package com.surnekev.ticketing.dto;

import com.surnekev.ticketing.domain.CheckinResult;
import com.surnekev.ticketing.dto.SeatDto;

import java.time.Instant;

public record CheckinResponse(
        CheckinResult result,
        String buyerName,
        SeatDto seat,
        Instant checkedInAt
) {
}

