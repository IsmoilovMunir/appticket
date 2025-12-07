import { defineStore } from 'pinia';
import type { ReservationResponse, Seat, SeatStatusEvent } from '../types';
import { fetchSeats } from '../services/api';
import { connectSeatChannel } from '../services/socket';

export const useSeatStore = defineStore('seats', {
  state: () => ({
    seats: [] as Seat[],
    selected: new Set<number>(),
    seatClient: null as ReturnType<typeof connectSeatChannel> | null,
    concertId: 1,
    realtimeEnabled: false,
    holdExpires: {} as Record<number, number>,
    now: Date.now(),
    ticker: null as ReturnType<typeof setInterval> | null,
    priceFilter: null as number | null
  }),
  getters: {
    selectedSeats(state) {
      return state.seats.filter((seat) => state.selected.has(seat.id));
    },
    totalSelectedPrice(): number {
      return this.selectedSeats.reduce((acc, seat) => acc + seat.priceCents, 0);
    },
    holdCountdowns(state) {
      const result: Record<number, number> = {};
      Object.entries(state.holdExpires).forEach(([seatId, expiry]) => {
        const remaining = Math.ceil((expiry - state.now) / 1000);
        if (remaining > 0) {
          result[Number(seatId)] = remaining;
        }
      });
      return result;
    },
    filteredSeats(state) {
      if (!state.priceFilter) {
        return state.seats;
      }
      return state.seats.filter((seat) => seat.priceCents === state.priceFilter);
    }
  },
  actions: {
    async init(concertId: number) {
      this.concertId = concertId;
      await this.loadSeats();
      this.bindRealtime();
    },
    async loadSeats() {
      this.seats = await fetchSeats(this.concertId);
      this.seats.forEach((seat) => {
        if (seat.status === 'HELD' && seat.holdExpiresAt) {
          this.holdExpires[seat.id] = new Date(seat.holdExpiresAt).getTime();
          this.ensureTicker();
        }
      });
    },
    toggleSeat(seatId: number) {
      const seat = this.seats.find((s) => s.id === seatId);
      if (!seat || seat.status !== 'AVAILABLE') {
        return;
      }
      if (this.selected.has(seatId)) {
        this.selected.delete(seatId);
      } else {
        if (this.selected.size >= 10) {
          return;
        }
        this.selected.add(seatId);
      }
      this.selected = new Set(this.selected);
    },
    clearSelection() {
      this.selected.clear();
      this.selected = new Set();
    },
    setPriceFilter(price: number | null) {
      this.priceFilter = price;
      if (price) {
        const next = new Set<number>();
        this.selected.forEach((id) => {
          const seat = this.seats.find((s) => s.id === id);
          if (seat && seat.priceCents === price) {
            next.add(id);
          }
        });
        this.selected = next;
      }
    },
    applySeatEvent(event: SeatStatusEvent) {
      const seat = this.seats.find((s) => s.id === event.seatId);
      if (!seat) return;
      seat.status = event.newStatus;
      if (event.newStatus !== 'AVAILABLE') {
        this.selected.delete(event.seatId);
      }
      if (event.newStatus === 'HELD' && event.expiresAt) {
        this.holdExpires[event.seatId] = new Date(event.expiresAt).getTime();
        this.ensureTicker();
      } else {
        delete this.holdExpires[event.seatId];
      }
    },
    bindRealtime() {
      if (this.realtimeEnabled) return;
      this.realtimeEnabled = true;
      this.seatClient = connectSeatChannel(this.concertId, {
        onSeatStatus: (event) => this.applySeatEvent(event),
        onReservation: () => undefined
      });
    },
    ensureTicker() {
      if (this.ticker) return;
      this.ticker = setInterval(() => {
        this.now = Date.now();
        if (Object.keys(this.holdExpires).length === 0 && this.ticker) {
          clearInterval(this.ticker);
          this.ticker = null;
        }
      }, 1000);
    },
    applyReservationHold(reservation: ReservationResponse) {
      if (!reservation.expiresAt) return;
      const expiry = new Date(reservation.expiresAt).getTime();
      reservation.seats.forEach((seatDto) => {
        const seat = this.seats.find((s) => s.id === seatDto.id);
        if (seat) {
          seat.status = 'HELD';
        }
        this.holdExpires[seatDto.id] = expiry;
      });
      this.ensureTicker();
    }
  }
});

