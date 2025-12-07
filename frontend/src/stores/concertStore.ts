import { defineStore } from 'pinia';
import type { Concert } from '../types';
import { fetchConcert } from '../services/api';

export const useConcertStore = defineStore('concert', {
  state: () => ({
    concert: null as Concert | null,
    loading: false,
    error: '' as string | null
  }),
  actions: {
    async load(concertId: number) {
      this.loading = true;
      this.error = null;
      try {
        this.concert = await fetchConcert(concertId);
      } catch (error: unknown) {
        this.error = 'Не удалось загрузить концерт';
        throw error;
      } finally {
        this.loading = false;
      }
    }
  }
});

