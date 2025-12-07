package com.surnekev.ticketing.web;

import com.surnekev.ticketing.dto.CancelReservationRequest;
import com.surnekev.ticketing.dto.ReservationResponse;
import com.surnekev.ticketing.service.ReservationService;
import com.surnekev.ticketing.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/admin/reservations")
@RequiredArgsConstructor
public class AdminReservationController {

    private final ReservationService reservationService;
    private final TicketService ticketService;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/{id}/confirm")
    public ReservationResponse confirm(@PathVariable UUID id, Authentication authentication) {
        try {
            return reservationService.confirmReservation(id, authentication.getName());
        } catch (IllegalArgumentException ex) {
            if ("Reservation not found".equals(ex.getMessage())) {
                var ticket = ticketService.getTicket(id);
                return reservationService.confirmReservation(ticket.getReservation().getId(), authentication.getName());
            }
            throw ex;
        }
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ReservationResponse cancel(@PathVariable UUID id,
                                      @RequestBody @Valid CancelReservationRequest request) {
        try {
            return reservationService.cancelReservation(id, request, false);
        } catch (IllegalArgumentException ex) {
            if ("Reservation not found".equals(ex.getMessage())) {
                var ticket = ticketService.getTicket(id);
                return reservationService.cancelReservation(ticket.getReservation().getId(), request, false);
            }
            throw ex;
        }
    }
}

