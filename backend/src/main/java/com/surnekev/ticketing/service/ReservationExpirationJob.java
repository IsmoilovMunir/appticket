package com.surnekev.ticketing.service;

import com.surnekev.ticketing.domain.ReservationStatus;
import com.surnekev.ticketing.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationExpirationJob {

    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;

    private static final List<ReservationStatus> EXPIRABLE_STATUSES = List.of(
            ReservationStatus.HELD,
            ReservationStatus.PARTIALLY_CONFIRMED,
            ReservationStatus.PARTIALLY_CANCELLED
    );

    @Scheduled(fixedDelayString = "PT1M")
    public void releaseExpiredHolds() {
        var expired = reservationRepository.findByStatusInAndExpiresAtBefore(EXPIRABLE_STATUSES, Instant.now());
        expired.forEach(reservation -> {
            log.info("Expiring reservation {}", reservation.getId());
            reservationService.expireReservation(reservation.getId());
        });
    }
}

