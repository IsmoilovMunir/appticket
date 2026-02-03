import { defineStore } from 'pinia';
import type { ReservationResponse, Seat } from '../types';
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
      if (seatStore.selected.size === 0 && seatStore.danceFloorQuantity === 0) {
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
        // Получаем все выбранные seatIds плюс места танцпола
        let allSeatIds = Array.from(seatStore.selected.values());

        // Автоматически резервируем места танцпола
        if (seatStore.danceFloorQuantity > 0) {
          const availableDanceFloorSeats = seatStore.seats.filter((seat: Seat) => seat.tableNumber === 0 && seat.status === 'AVAILABLE');
          const danceFloorSeatsToReserve = availableDanceFloorSeats.slice(0, seatStore.danceFloorQuantity);
          allSeatIds = allSeatIds.concat(danceFloorSeatsToReserve.map((seat: Seat) => seat.id));
        }

        this.lastReservation = await createReservation({
          concertId,
          seatIds: allSeatIds,
          buyerName: this.contact.name.trim(),
          buyerPhone: this.contact.phone.trim(),
          buyerEmail: this.contact.email.trim()
        });
        seatStore.applyReservationHold(this.lastReservation);
        seatStore.clearSelection();
        // Очищаем выбор танцпола после успешного бронирования
        seatStore.setDanceFloorQuantity(0);
        const reservationNumber = this.lastReservation?.reservationId;
        const prefix = reservationNumber ? `Бронь #${reservationNumber} создана.` : 'Заявка отправлена.';
        this.successMessage = `${prefix} В скором времени с вами свяжется менеджер.`;
      } catch (error: unknown) {
        const axiosError = error as { response?: { data?: { message?: string }; status?: number } };
        const apiMessage = axiosError.response?.data?.message;
        if (apiMessage) {
          this.error = apiMessage.startsWith('Seat already sold') || apiMessage.startsWith('Seats already held')
            ? 'Выбранные места уже заняты. Обновите схему и выберите другие.'
            : apiMessage.startsWith('Not enough available seats')
              ? 'Недостаточно свободных мест в выбранной категории. Обновите схему.'
              : apiMessage;
        } else {
          this.error = 'Не удалось создать бронь';
        }
        this.successMessage = null;
        if (axiosError.response?.status === 409) {
          seatStore.loadSeats();
        }
        throw error;
      } finally {
        this.loading = false;
      }
    }
  }
});

