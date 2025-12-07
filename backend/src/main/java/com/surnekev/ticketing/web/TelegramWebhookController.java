package com.surnekev.ticketing.web;

import com.surnekev.ticketing.dto.CancelReservationRequest;
import com.surnekev.ticketing.dto.TelegramCallbackRequest;
import com.surnekev.ticketing.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/telegram")
@RequiredArgsConstructor
public class TelegramWebhookController {

    private final ReservationService reservationService;

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleCallback(@RequestBody @Valid TelegramCallbackRequest request) {
        UUID reservationId = UUID.fromString(request.reservationId());
        if ("confirm".equalsIgnoreCase(request.action())) {
            reservationService.confirmReservation(reservationId, "telegram");
        } else if ("cancel".equalsIgnoreCase(request.action())) {
            reservationService.cancelReservation(reservationId,
                    new CancelReservationRequest("telegram", "telegram cancel"), false);
        }
        return ResponseEntity.ok().build();
    }
}

