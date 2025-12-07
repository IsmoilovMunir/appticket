import { Client } from '@stomp/stompjs';
import type { ReservationCreatedEvent, SeatStatusEvent } from '../types';

export type SeatEventHandlers = {
  onSeatStatus: (event: SeatStatusEvent) => void;
  onReservation: (event: ReservationCreatedEvent) => void;
};

export const connectSeatChannel = (concertId: number, handlers: SeatEventHandlers) => {
  const wsUrl =
    import.meta.env.VITE_WS_URL || `${window.location.protocol === 'https:' ? 'wss' : 'ws'}://${window.location.host}/ws-seat-status`;
  const client = new Client({
    brokerURL: undefined,
    webSocketFactory: () => new WebSocket(wsUrl),
    reconnectDelay: 5000
  });

  client.onConnect = () => {
    client.subscribe(`/topic/concert/${concertId}/seat-status`, (message) => {
      handlers.onSeatStatus(JSON.parse(message.body));
    });
    client.subscribe(`/topic/concert/${concertId}/reservations`, (message) => {
      handlers.onReservation(JSON.parse(message.body));
    });
  };

  client.activate();
  return client;
};

