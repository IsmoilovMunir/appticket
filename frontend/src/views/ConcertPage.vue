<template>
  <div class="concert-page">
    <div v-if="loading" class="concert-page__content container py-5 text-center">
      <div class="spinner-border" role="status">
        <span class="visually-hidden">Загрузка...</span>
      </div>
    </div>

    <div v-else-if="error" class="concert-page__content container py-5">
      <div class="alert alert-danger" role="alert">
        {{ error }}
      </div>
    </div>

    <div v-else-if="concert" class="concert-page__content landing-page">
      <div class="concert-page__card-wrapper">
        <RouterLink to="/" class="concert-page__close" aria-label="К списку концертов">
          <i class="bi bi-x-lg"></i>
        </RouterLink>
        <ConcertDetailCard
          :concert="concert"
          :as-modal="false"
          @buy="openModal"
        />
      </div>

      <!-- Simple mode: категории + количество -->
      <SimpleTicketSelector
        v-if="concert.simpleMode && showModal"
        :open="showModal"
        :concert-id="concert.id"
        :concert-slug="concert.slug"
        @close="closeModal"
      />
      <!-- Seat Map Modal (схема зала) -->
      <SeatMapModal
        v-else-if="showModal"
        :open="showModal"
        :concert-id="concert.id"
        :scheme-url="concert.salesSchemeUrl"
        @close="closeModal"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useRoute, RouterLink } from 'vue-router';
import type { Concert } from '../types';
import { fetchConcertBySlug } from '../services/api';
import ConcertDetailCard from '../components/ConcertDetailCard.vue';
import SeatMapModal from '../components/SeatMapModal.vue';
import SimpleTicketSelector from '../components/SimpleTicketSelector.vue';

const route = useRoute();
const concert = ref<Concert | null>(null);

const loading = ref(false);
const error = ref<string | null>(null);
const showModal = ref(false);

const loadConcert = async () => {
  const slug = route.params.slug as string;
  if (!slug) {
    error.value = 'Slug не указан';
    return;
  }

  loading.value = true;
  error.value = null;
  concert.value = null; // Очищаем старый концерт перед загрузкой нового

  try {
    console.log('Loading concert with slug:', slug);
    const loadedConcert = await fetchConcertBySlug(slug);
    console.log('Loaded concert:', loadedConcert);
    concert.value = loadedConcert;
  } catch (err) {
    error.value = 'Концерт не найден';
    console.error('Failed to load concert:', err);
  } finally {
    loading.value = false;
  }
};

const openModal = () => {
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
};

onMounted(() => {
  loadConcert();
});

// Перезагружаем концерт при изменении slug или полного пути
watch(
  () => [route.params.slug, route.fullPath],
  () => {
    loadConcert();
  },
  { immediate: false }
);

// Открыть модалку покупки при переходе по ?buy=1
watch(
  () => [route.query.buy, concert.value],
  ([buy, c]) => {
    if (buy === '1' && c) showModal.value = true;
  },
  { immediate: true }
);
</script>

<style scoped>
.concert-page {
  padding: 20px 0 40px;
}

.concert-page__content {
  position: relative;
  background: #fff;
  border-radius: 16px;
  max-width: 680px;
  width: 100%;
  margin: 0 auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.25);
}

.concert-page__card-wrapper {
  position: relative;
  padding: 24px 16px 40px;
}

.concert-page__close {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  border-radius: 50%;
  text-decoration: none;
  font-size: 1.25rem;
  z-index: 10;
  transition: background 0.2s;
}

.concert-page__close:hover {
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
}
</style>
