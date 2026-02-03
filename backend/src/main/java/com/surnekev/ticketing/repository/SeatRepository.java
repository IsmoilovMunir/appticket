package com.surnekev.ticketing.repository;

import com.surnekev.ticketing.domain.Seat;
import com.surnekev.ticketing.domain.SeatStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    @Modifying
    @Query(value = "DELETE FROM reservation_seats WHERE seat_id = ?1", nativeQuery = true)
    void deleteReservationSeatsBySeatId(Long seatId);

    @Modifying
    @Query(value = "DELETE FROM reservation_seats WHERE seat_id IN ?1", nativeQuery = true)
    void deleteReservationSeatsBySeatIds(Collection<Long> seatIds);

    List<Seat> findAllByConcertId(Long concertId);

    List<Seat> findByConcertIdAndCategory_IdAndStatusOrderByIdAsc(Long concertId, Long categoryId, SeatStatus status, Pageable pageable);

    Set<Seat> findByIdIn(Set<Long> ids);

    long countByIdInAndStatus(Set<Long> ids, SeatStatus status);

    Optional<Seat> findByIdAndConcertId(Long id, Long concertId);

    List<Seat> findByConcertIdOrderByTableNumberAscChairNumberAsc(Long concertId);

    List<Seat> findByConcertIdAndTableNumberIn(Long concertId, Collection<Integer> tableNumbers);

    List<Seat> findByConcertIdAndTableNumber(Long concertId, Integer tableNumber);

    Optional<Seat> findByConcertIdAndTableNumberAndChairNumber(Long concertId, Integer tableNumber, Integer chairNumber);
}

