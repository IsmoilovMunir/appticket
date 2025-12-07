import { defineStore } from 'pinia';
import type { ReservationResponse } from '../types';
import { createReservation } from '../services/api';
import { useSeatStore } from './seatStore';

export const useReservationStore = defineStore('reservations', {
  state: () => ({
    lastReservation: null as ReservationResponse | null,
    loading: false,
    error: '' as string | null,
    successMessage: null as string | null,
    contact: {
      name: '',
      phone: '',
      email: ''
    },
    consentToProcessing: false
  }),
  actions: {
    async submitReservation(concertId: number, consentToProcessing: boolean = false) {
      const seatStore = useSeatStore();
      this.error = null;
      this.successMessage = null;
      if (seatStore.selected.size === 0) {
        this.error = 'Выберите места';
        return;
      }
      if (!this.contact.name.trim()) {
        this.error = 'Укажите имя';
        return;
      }
      if (!this.contact.phone.trim() && !this.contact.email.trim()) {
        this.error = 'Укажите телефон или email';
        return;
      }
      if (!consentToProcessing) {
        this.error = 'Необходимо дать согласие на обработку персональных данных';
        return;
      }

      this.loading = true;
      try {
        this.lastReservation = await createReservation({
          concertId,
          seatIds: Array.from(seatStore.selected.values()),
          buyerName: this.contact.name.trim(),
          buyerPhone: this.contact.phone.trim(),
          buyerEmail: this.contact.email.trim()
        });
        seatStore.applyReservationHold(this.lastReservation);
        seatStore.clearSelection();
        const reservationNumber = this.lastReservation?.reservationId;
        const prefix = reservationNumber ? `Бронь #${reservationNumber} создана.` : 'Заявка отправлена.';
        this.successMessage = `${prefix} В скором времени с вами свяжется менеджер.`;
      } catch (error: unknown) {
        this.error = 'Не удалось создать бронь';
        this.successMessage = null;
        throw error;
      } finally {
        this.loading = false;
      }
    }
  }
});

