package com.surnekev.ticketing.web;

import com.surnekev.ticketing.dto.CreateReservationRequest;
import com.surnekev.ticketing.dto.ReservationResponse;
import com.surnekev.ticketing.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ReservationResponse create(@RequestBody @Valid CreateReservationRequest request) {
        return reservationService.createReservation(request);
    }
}

