package com.surnekev.ticketing.repository;

import com.surnekev.ticketing.domain.Reservation;
import com.surnekev.ticketing.domain.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    List<Reservation> findByConcertId(Long concertId);

    List<Reservation> findByStatusAndExpiresAtBefore(ReservationStatus status, Instant cutoff);

    List<Reservation> findByStatusInAndExpiresAtBefore(Iterable<ReservationStatus> statuses, Instant cutoff);

    Optional<Reservation> findByIdAndStatus(UUID id, ReservationStatus status);
}

