<template>
  <div class="home-page">
    <div class="container py-4">
      <h1 class="home-page__title mb-4">Концерты</h1>

      <div v-if="loading" class="text-center py-5">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Загрузка...</span>
        </div>
      </div>

      <div v-else-if="error" class="alert alert-danger">
        {{ error }}
      </div>

      <div v-else-if="concerts.length === 0" class="text-center py-5 text-body-secondary">
        Нет доступных концертов
      </div>

      <div v-else class="concert-cards-grid">
        <ConcertCard
          v-for="concert in concerts"
          :key="concert.id"
          :concert="concert"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import type { Concert } from '../types';
import { fetchConcertsList } from '../services/api';
import ConcertCard from '../components/ConcertCard.vue';

const concerts = ref<Concert[]>([]);
const loading = ref(true);
const error = ref('');

const loadConcerts = async () => {
  loading.value = true;
  error.value = '';
  try {
    concerts.value = await fetchConcertsList();
  } catch (err: any) {
    error.value = err.response?.data?.message || err.message || 'Не удалось загрузить концерты';
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadConcerts();
});
</script>

<style scoped>
.home-page {
  min-height: 60vh;
}

.home-page__title {
  font-size: 1.75rem;
  font-weight: 700;
  color: #1a1a1a;
}

.concert-cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}
</style>
