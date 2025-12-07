package com.surnekev.ticketing.repository;

import com.surnekev.ticketing.domain.Ticket;
import com.surnekev.ticketing.domain.TicketStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    Optional<Ticket> findByTicketToken(String ticketToken);

    long countByStatus(TicketStatus status);

    long countByReservationId(UUID reservationId);

    java.util.List<Ticket> findAllByReservationId(UUID reservationId);

    java.util.List<Ticket> findAllByStatus(TicketStatus status);

    @EntityGraph(attributePaths = {"reservation", "seat", "seat.category"})
    Optional<Ticket> findDetailedById(UUID id);

    @EntityGraph(attributePaths = {"reservation", "reservation.concert", "seat", "seat.category"})
    java.util.List<Ticket> findAllByIdIn(java.util.Collection<UUID> ids);

    java.util.List<Ticket> findAllByBuyerPhoneContainingIgnoreCase(String phone);

    java.util.List<Ticket> findAllByBuyerNameContainingIgnoreCase(String name);
}

