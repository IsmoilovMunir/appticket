package com.surnekev.ticketing.service;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Interface for seat holding operations in Redis
 */
public interface SeatHoldServiceInterface {

    /**
     * Acquire hold on seats for a reservation
     * @param seatIds Set of seat IDs to hold
     * @param reservationId Reservation identifier
     * @return true if all seats were successfully acquired, false otherwise
     */
    boolean acquire(Set<Long> seatIds, String reservationId);

    /**
     * Release hold on seats
     * @param seatIds Set of seat IDs to release
     */
    void release(Set<Long> seatIds);

    /**
     * Get remaining hold seconds for a seat
     * @param seatId Seat ID
     * @return remaining seconds or -1 if not held
     */
    long remainingHoldSeconds(Long seatId);

    /**
     * Get current reservation ID for a held seat
     * @param seatId Seat ID
     * @return Optional reservation UUID
     */
    Optional<UUID> currentReservationForSeat(Long seatId);

    /**
     * Refresh hold expiration for seats
     * @param seatIds Set of seat IDs to refresh
     */
    void refresh(Set<Long> seatIds);
}