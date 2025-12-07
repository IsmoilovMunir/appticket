package com.surnekev.ticketing.websocket;

import com.surnekev.ticketing.dto.ReservationCreatedEvent;
import com.surnekev.ticketing.dto.SeatStatusChangeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void publishSeatStatus(Long concertId, SeatStatusChangeEvent event) {
        messagingTemplate.convertAndSend("/topic/concert/" + concertId + "/seat-status", event);
    }

    public void publishReservation(Long concertId, ReservationCreatedEvent event) {
        messagingTemplate.convertAndSend("/topic/concert/" + concertId + "/reservations", event);
    }
}

