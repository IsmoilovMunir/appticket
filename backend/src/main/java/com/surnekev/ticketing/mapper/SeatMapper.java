package com.surnekev.ticketing.mapper;

import com.surnekev.ticketing.domain.Seat;
import com.surnekev.ticketing.dto.SeatDto;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class SeatMapper {

    public SeatDto toDto(Seat seat, @Nullable Instant holdExpiresAt) {
        int resolvedPrice = seat.getPriceOverrideCents() != null
                ? seat.getPriceOverrideCents()
                : seat.getCategory().getPriceCents();
        return new SeatDto(
                seat.getId(),
                seat.getTableNumber(),
                seat.getChairNumber(),
                seat.getCategory().getName(),
                seat.getCategory().getColorHex(),
                resolvedPrice,
                seat.getPriceOverrideCents(),
                seat.getStatus(),
                seat.getBlockedReason(),
                holdExpiresAt
        );
    }
}

