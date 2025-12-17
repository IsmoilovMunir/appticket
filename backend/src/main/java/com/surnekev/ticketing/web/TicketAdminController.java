package com.surnekev.ticketing.web;

import com.surnekev.ticketing.domain.TicketStatus;
import com.surnekev.ticketing.dto.CancelReservationRequest;
import com.surnekev.ticketing.dto.TicketBulkActionRequest;
import com.surnekev.ticketing.dto.TicketBulkCancelRequest;
import com.surnekev.ticketing.dto.TicketDto;
import com.surnekev.ticketing.service.ReservationService;
import com.surnekev.ticketing.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/tickets")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public class TicketAdminController {

    private final TicketService ticketService;
    private final ReservationService reservationService;

    @GetMapping
    public List<TicketDto> listTickets(@RequestParam(value = "status", required = false) TicketStatus status) {
        return ticketService.findTicketDtos(Optional.ofNullable(status));
    }

    @PostMapping("/{id}/confirm")
    public TicketDto confirmTicket(@PathVariable UUID id, Authentication authentication) {
        reservationService.confirmTickets(List.of(id), authentication.getName());
        return ticketService.toDto(ticketService.getTicket(id));
    }

    @PostMapping("/{id}/cancel")
    public TicketDto cancelTicket(@PathVariable UUID id,
                                  Authentication authentication,
                                  @RequestBody(required = false) @Valid CancelReservationRequest request) {
        String reason = request != null && request.reason() != null ? request.reason() : "ticket cancel";
        reservationService.cancelTickets(List.of(id), authentication.getName(), reason, false);
        return ticketService.toDto(ticketService.getTicket(id));
    }

    @PostMapping("/confirm")
    public List<TicketDto> confirmTickets(@RequestBody @Valid TicketBulkActionRequest request,
                                          Authentication authentication) {
        reservationService.confirmTickets(request.ticketIds(), authentication.getName());
        return toDtos(request.ticketIds());
    }

    @PostMapping("/cancel")
    public List<TicketDto> cancelTickets(@RequestBody @Valid TicketBulkCancelRequest request,
                                         Authentication authentication) {
        String reason = request.reason() != null ? request.reason() : "ticket cancel";
        reservationService.cancelTickets(request.ticketIds(), authentication.getName(), reason, false);
        return toDtos(request.ticketIds());
    }

    @PostMapping("/{id}/refund")
    public TicketDto refundTicket(@PathVariable UUID id,
                                  Authentication authentication,
                                  @RequestBody(required = false) Map<String, String> request) {
        String reason = request != null && request.get("reason") != null && !request.get("reason").isBlank() 
                ? request.get("reason") 
                : "ticket refund";
        reservationService.refundTickets(List.of(id), authentication.getName(), reason);
        return ticketService.toDto(ticketService.getTicket(id));
    }

    @PostMapping("/refund")
    public List<TicketDto> refundTickets(@RequestBody @Valid TicketBulkCancelRequest request,
                                         Authentication authentication) {
        String reason = request.reason() != null ? request.reason() : "ticket refund";
        reservationService.refundTickets(request.ticketIds(), authentication.getName(), reason);
        return toDtos(request.ticketIds());
    }

    @GetMapping("/search")
    public List<TicketDto> searchTickets(@RequestParam(required = false) String phone,
                                         @RequestParam(required = false) String name,
                                         @RequestParam(required = false) String ticketId) {
        return ticketService.searchTickets(phone, name, ticketId);
    }

    /**
     * Получить список использованных билетов (только для админа)
     */
    @GetMapping("/used")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TicketDto> getUsedTickets() {
        return ticketService.getUsedTickets();
    }

    /**
     * Вернуть билет из статуса USED обратно в SOLD (только для админа)
     */
    @PostMapping("/{id}/revert")
    @PreAuthorize("hasRole('ADMIN')")
    public TicketDto revertTicketStatus(@PathVariable UUID id, Authentication authentication) {
        return ticketService.revertTicketStatus(id, authentication.getName());
    }

    private List<TicketDto> toDtos(List<UUID> ticketIds) {
        return ticketIds.stream()
                .distinct()
                .map(ticketService::getTicket)
                .map(ticketService::toDto)
                .toList();
    }
}

