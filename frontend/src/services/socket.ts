import { Client } from '@stomp/stompjs';
import type { ReservationCreatedEvent, SeatStatusEvent } from '../types';

export type SeatEventHandlers = {
  onSeatStatus: (event: SeatStatusEvent) => void;
  onReservation: (event: ReservationCreatedEvent) => void;
};

export const connectSeatChannel = (concertId: number, handlers: SeatEventHandlers) => {
  // В режиме разработки используем текущий хост (Vite прокси обработает запрос)
  // В продакшене используем полный URL или переменную окружения
  let wsUrl: string;
  
  if (import.meta.env.VITE_WS_URL) {
    wsUrl = import.meta.env.VITE_WS_URL;
  } else {
    // Используем текущий хост - Vite прокси в dev режиме или прямой доступ в prod
    const protocol = window.location.protocol === 'https:' ? 'wss' : 'ws';
    wsUrl = `${protocol}://${window.location.host}/ws-seat-status`;
  }
  
  console.log('[WebSocket] Подключение к:', wsUrl);
  
  const client = new Client({
    brokerURL: undefined,
    webSocketFactory: () => {
      const ws = new WebSocket(wsUrl);
      ws.onerror = (error) => {
        console.error('[WebSocket] Ошибка подключения:', error);
        console.error('[WebSocket] URL:', wsUrl);
        console.error('[WebSocket] Убедитесь, что бэкенд запущен на порту 8080');
      };
      ws.onopen = () => {
        console.log('[WebSocket] Соединение установлено');
      };
      return ws;
    },
    reconnectDelay: 5000,
    onStompError: (frame) => {
      console.error('[STOMP] Ошибка:', frame);
    }
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

