package com.surnekev.ticketing.web;

import com.surnekev.ticketing.dto.CheckinRequest;
import com.surnekev.ticketing.dto.CheckinResponse;
import com.surnekev.ticketing.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @PostMapping
    public CheckinResponse checkIn(@RequestBody @Valid CheckinRequest request, Authentication authentication) {
        String operator = authentication != null ? authentication.getName() : "public-checkin";
        return ticketService.checkIn(request.ticketToken(), operator);
    }
}

