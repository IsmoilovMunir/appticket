package com.surnekev.ticketing.repository;

import com.surnekev.ticketing.domain.Seat;
import com.surnekev.ticketing.domain.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findAllByConcertId(Long concertId);

    Set<Seat> findByIdIn(Set<Long> ids);

    long countByIdInAndStatus(Set<Long> ids, SeatStatus status);

    Optional<Seat> findByIdAndConcertId(Long id, Long concertId);

    List<Seat> findByConcertIdOrderByTableNumberAscChairNumberAsc(Long concertId);

    List<Seat> findByConcertIdAndTableNumberIn(Long concertId, Collection<Integer> tableNumbers);

    List<Seat> findByConcertIdAndTableNumber(Long concertId, Integer tableNumber);

    Optional<Seat> findByConcertIdAndTableNumberAndChairNumber(Long concertId, Integer tableNumber, Integer chairNumber);
}

