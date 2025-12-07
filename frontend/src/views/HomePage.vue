<template>
  <div class="landing-page">
    <ConcertHero
      v-if="concert"
      :title="concert.title"
      :description="concert.description"
      :date="formattedDate"
      :venue="concert.venue"
      @cta="openModal"
    />

    <!-- Features Section -->
    <section class="py-5 bg-body">
      <div class="container">
        <div class="text-center mb-5">
          <h2 class="display-5 fw-bold mb-3">Почему выбирают нас</h2>
          <p class="lead text-body-secondary">Современная система бронирования с удобным интерфейсом</p>
        </div>
        <div class="row g-4">
          <div class="col-lg-4" v-for="card in infoCards" :key="card.title">
            <div class="card h-100 shadow-sm border-0 rounded-4 feature-card">
              <div class="card-body p-4">
                <div class="feature-icon mb-3">
                  <i :class="['bi', card.icon, 'fs-1 text-primary']"></i>
                </div>
                <h3 class="h5 fw-semibold mb-3">{{ card.title }}</h3>
                <p class="text-body-secondary mb-0">{{ card.text }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="py-5 bg-primary text-white">
      <div class="container text-center">
        <h2 class="display-6 fw-bold mb-3">Готовы выбрать место?</h2>
        <p class="lead mb-4">Выберите лучший стол на интерактивной карте зала</p>
        <button class="btn btn-light btn-lg px-5" @click="openModal">
          <i class="bi bi-ticket-perforated me-2"></i>
          Выбрать место
        </button>
      </div>
    </section>

    <SeatMapModal :open="modalOpen" :concert-id="concertId" @close="modalOpen = false" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import ConcertHero from '../components/ConcertHero.vue';
import SeatMapModal from '../components/SeatMapModal.vue';
import { useConcertStore } from '../stores/concertStore';
import { storeToRefs } from 'pinia';

const concertId = 1;
const modalOpen = ref(false);

const concertStore = useConcertStore();
const { concert } = storeToRefs(concertStore);

onMounted(() => {
  concertStore.load(concertId);
});

const openModal = () => {
  modalOpen.value = true;
};

const formattedDate = computed(() => {
  if (!concert.value) return '';
  return new Intl.DateTimeFormat('ru-RU', {
    dateStyle: 'full',
    timeStyle: 'short'
  }).format(new Date(concert.value.concertDate));
});

const infoCards = [
  {
    title: 'Интерактивная карта зала',
    text: 'Выбирайте места на интерактивной карте в реальном времени. Статусы мест обновляются мгновенно для всех пользователей.',
    icon: 'bi-grid-3x3-gap'
  },
  {
    title: 'Персональный менеджер',
    text: 'После бронирования наш менеджер свяжется с вами в Telegram, подтвердит резерв и вышлет QR‑билеты.',
    icon: 'bi-chat-dots-fill'
  },
  {
    title: 'Быстрый вход по QR',
    text: 'В день концерта просто покажите QR‑код на входе. Система автоматически отметит ваш вход и защитит от дублирования.',
    icon: 'bi-qr-code-scan'
  }
];
</script>

<style scoped>
.landing-page {
  min-height: 100vh;
}

.feature-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 0.5rem 1.5rem rgba(0, 0, 0, 0.15) !important;
}

.feature-icon {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(24, 114, 63, 0.1), rgba(111, 66, 193, 0.1));
  border-radius: 20px;
}
</style>

