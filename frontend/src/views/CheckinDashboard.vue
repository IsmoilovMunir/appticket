<template>
  <section class="py-5 bg-body">
    <div class="container">
      <div class="row g-4">
        <div class="col-lg-6">
          <div class="card shadow-sm border-0 rounded-4 h-100">
            <div class="card-body">
              <h2 class="h5 mb-3">Сканирование билета</h2>
              <p class="text-body-secondary">
                Введите или отсканируйте QR‑токен, система сразу покажет статус билета.
              </p>
              <form @submit.prevent="checkTicket">
                <label class="form-label">Ticket token</label>
                <textarea v-model="token" class="form-control mb-3" rows="3" placeholder="ticket-token" required />
                <button class="btn btn-primary w-100" :disabled="loading">
                  <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
                  Проверить
                </button>
              </form>
              <p v-if="error" class="text-danger mt-3">{{ error }}</p>
            </div>
          </div>
        </div>
        <div class="col-lg-6">
          <div class="card shadow-sm border-0 rounded-4 h-100">
            <div class="card-body">
              <h2 class="h5 mb-3">Результат</h2>
              <div v-if="result" class="p-3 rounded-4" :class="resultClass">
                <p class="fw-semibold mb-1">{{ resultText }}</p>
                <p class="mb-1">Место: {{ seatLabel }}</p>
                <p class="mb-0 text-body-secondary small">
                  Время отметки: {{ result.checkedInAt ? formatDate(result.checkedInAt) : '—' }}
                </p>
              </div>
              <p v-else class="text-body-secondary">Здесь появится статус билета.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { apiClient } from '../services/api';

interface CheckinResponse {
  result: 'APPROVED' | 'DUPLICATE' | 'INVALID';
  buyerName: string;
  seat: { tableNumber: number; chairNumber: number; categoryName: string } | null;
  checkedInAt: string | null;
}

const token = ref('');
const loading = ref(false);
const error = ref('');
const result = ref<CheckinResponse | null>(null);

const checkTicket = async () => {
  loading.value = true;
  error.value = '';
  result.value = null;
  try {
    const { data } = await apiClient.post<CheckinResponse>('/checkin', { ticketToken: token.value });
    result.value = data;
  } catch (err: unknown) {
    error.value = 'Не удалось проверить билет';
  } finally {
    loading.value = false;
  }
};

const resultClass = computed(() => {
  if (!result.value) return 'bg-body-tertiary';
  switch (result.value.result) {
    case 'APPROVED':
      return 'bg-success-subtle text-success';
    case 'DUPLICATE':
      return 'bg-warning-subtle text-warning';
    default:
      return 'bg-danger-subtle text-danger';
  }
});

const resultText = computed(() => {
  if (!result.value) return '';
  if (result.value.result === 'APPROVED') {
    return `Добро пожаловать, ${result.value.buyerName}!`;
  }
  if (result.value.result === 'DUPLICATE') return 'Билет уже использован';
  return 'QR-код невалиден';
});

const seatLabel = computed(() => {
  if (!result.value?.seat) return '-';
  return `Стол ${result.value.seat.tableNumber}, место ${result.value.seat.chairNumber}`;
});

const formatDate = (value: string) =>
  new Intl.DateTimeFormat('ru-RU', { dateStyle: 'short', timeStyle: 'short' }).format(new Date(value));
</script>

