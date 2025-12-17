package com.surnekev.ticketing.web;

import com.surnekev.ticketing.dto.CheckinRequest;
import com.surnekev.ticketing.dto.CheckinResponse;
import com.surnekev.ticketing.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkin")
@RequiredArgsConstructor
public class CheckinController {

    private final TicketService ticketService;

    @PostMapping("/preview")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CHECKIN')")
    public CheckinResponse previewCheckIn(@RequestBody @Valid CheckinRequest request) {
        return ticketService.previewCheckIn(request.ticketToken());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CHECKIN')")
    public CheckinResponse checkIn(@RequestBody @Valid CheckinRequest request, Authentication authentication) {
        String operator = authentication != null ? authentication.getName() : "unknown";
        return ticketService.checkIn(request.ticketToken(), operator);
    }
}

