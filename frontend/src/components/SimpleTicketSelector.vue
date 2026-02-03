<template>
  <div v-if="open" class="modal-backdrop-custom" @click.self="close">
    <div class="modal-panel simple-ticket-panel p-4">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
          <h2 class="h4 mb-1">{{ selectedCategory ? 'Оформление заказа' : 'Купить билеты' }}</h2>
          <p class="text-body-secondary mb-0">
            {{ selectedCategory ? `${selectedCategory.name} — ${formatPrice(selectedCategory.priceCents)} за билет` : 'Выберите категорию билетов' }}
          </p>
        </div>
        <button class="btn btn-outline-secondary" @click="handleClose">
          <i class="bi bi-x-lg me-1" /> Закрыть
        </button>
      </div>

      <div v-if="loading" class="text-center py-4">
        <div class="spinner-border" role="status">
          <span class="visually-hidden">Загрузка...</span>
        </div>
      </div>

      <div v-else-if="categories.length === 0" class="alert alert-warning">
        Нет доступных категорий билетов.
      </div>

      <!-- Шаг 1: Выбор категории -->
      <div v-else-if="!selectedCategory" class="simple-ticket-content">
        <!-- Карточки категорий -->
        <div class="category-cards">
          <div
            v-for="cat in categories"
            :key="cat.id"
            class="category-card border rounded-4 p-4 mb-3 cursor-pointer"
            :style="cat.colorHex ? { borderLeft: `4px solid ${cat.colorHex}` } : {}"
            @click="selectCategory(cat)"
          >
            <div class="d-flex justify-content-between align-items-center">
              <div class="flex-grow-1">
                <h3 class="h5 mb-2">{{ cat.name }}</h3>
                <p v-if="cat.description" class="text-body-secondary mb-2">{{ cat.description }}</p>
                <div class="d-flex align-items-center gap-3 mb-2">
                  <div>
                    <span class="fw-bold text-primary fs-4">{{ formatPrice(cat.priceCents) }}</span>
                    <span class="text-body-secondary small"> за билет</span>
                  </div>
                </div>
              </div>
              <div class="text-end">
                <button class="btn btn-primary btn-lg">
                  Выбрать
                  <i class="bi bi-arrow-right ms-2"></i>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Шаг 2: Выбор количества и оформление -->
      <div v-else class="simple-ticket-content">
        <button class="btn btn-outline-secondary btn-sm mb-3" @click="selectedCategory = null">
          <i class="bi bi-arrow-left me-1"></i>
          Назад к выбору категории
        </button>

        <!-- Информация о выбранной категории -->
        <div class="selected-category-info border rounded-4 p-3 mb-4" 
             :style="selectedCategory.colorHex ? { borderLeft: `4px solid ${selectedCategory.colorHex}` } : {}">
          <h4 class="h6 mb-2">{{ selectedCategory.name }}</h4>
          <div class="d-flex align-items-center gap-3 mb-2">
            <div>
              <span class="fw-bold text-primary fs-5">{{ formatPrice(selectedCategory.priceCents) }}</span>
              <span class="text-body-secondary small"> за билет</span>
            </div>
          </div>
        </div>

        <!-- Выбор количества -->
        <div class="quantity-section mb-4">
          <label class="form-label fw-semibold">Количество билетов</label>
          <div class="quantity-controls-large d-flex align-items-center gap-3">
            <button
              type="button"
              class="btn btn-outline-secondary"
              :disabled="selectedQuantity <= 1"
              @click="selectedQuantity = Math.max(1, selectedQuantity - 1)"
            >
              <i class="bi bi-dash-lg"></i>
            </button>
            <input
              type="number"
              class="form-control form-control-lg quantity-input-large text-center"
              v-model.number="selectedQuantity"
              :min="1"
              :max="Math.min(selectedCategory.availableCount, 10)"
            />
            <button
              type="button"
              class="btn btn-outline-primary"
              :disabled="selectedQuantity >= Math.min(selectedCategory.availableCount, 10)"
              @click="selectedQuantity = Math.min(selectedCategory.availableCount, 10, selectedQuantity + 1)"
            >
              <i class="bi bi-plus-lg"></i>
            </button>
          </div>
          <small class="text-body-secondary">Максимум 10 билетов</small>
        </div>

        <!-- Итого -->
        <div class="total-section border rounded-4 p-3 bg-light mb-4">
          <div class="d-flex justify-content-between align-items-center">
            <span class="fw-semibold">Итого:</span>
            <span class="fw-bold text-primary fs-4">{{ formatPrice(selectedQuantity * selectedCategory.priceCents) }}</span>
          </div>
          <small class="text-body-secondary">{{ selectedQuantity }} {{ ticketText(selectedQuantity) }}</small>
        </div>

        <!-- Контактная форма -->
        <div class="contact-form-block mb-3">
          <h5 class="h6 mb-3">Контактная информация</h5>
          <div class="mb-3">
            <label class="form-label">Имя *</label>
            <input
              v-model="contact.name"
              class="form-control"
              placeholder="Имя и фамилия"
              required
            />
          </div>
          <div class="mb-3">
            <label class="form-label">Телефон *</label>
            <input
              v-model="contact.phone"
              class="form-control"
              placeholder="+7"
              required
            />
          </div>
          <div class="mb-3">
            <label class="form-label">Email *</label>
            <input
              v-model="contact.email"
              type="email"
              class="form-control"
              placeholder="example@site.com"
              required
            />
            <small class="text-body-secondary">Билеты будут отправлены на эту почту</small>
          </div>
        </div>

        <!-- Согласие и кнопка -->
        <div class="form-check mb-3">
          <input
            class="form-check-input"
            type="checkbox"
            id="consentCheckboxSimple"
            v-model="consentToProcessing"
          />
          <label class="form-check-label" for="consentCheckboxSimple">
            Я согласен на обработку персональных данных
          </label>
        </div>

        <button
          class="btn btn-primary btn-lg w-100 btn-buy-accent"
          :disabled="!canSubmit || reservationLoading"
          @click="submitReservation"
        >
          <span v-if="reservationLoading" class="spinner-border spinner-border-sm me-2"></span>
          Купить {{ selectedQuantity }} {{ ticketText(selectedQuantity) }}
        </button>

        <p v-if="successMessage" class="text-success mt-3 mb-0">
          <i class="bi bi-check-circle me-1"></i>{{ successMessage }}
        </p>
        <p v-else-if="error" class="text-danger mt-3 mb-0">
          <i class="bi bi-exclamation-circle me-1"></i>{{ error }}
        </p>
        <p v-else class="text-body-secondary small mt-3 mb-0">
          После отправки менеджер свяжется с вами для подтверждения заказа.
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import type { SeatCategoryWithAvailability } from '../types';
import { fetchConcertCategoriesWithAvailabilityBySlug, createReservation } from '../services/api';

const props = defineProps<{
  open: boolean;
  concertId: number;
  concertSlug: string;
}>();

const emit = defineEmits(['close']);

const loading = ref(false);
const categories = ref<SeatCategoryWithAvailability[]>([]);
const selectedCategory = ref<SeatCategoryWithAvailability | null>(null);
const selectedQuantity = ref(1);
const consentToProcessing = ref(false);
const reservationLoading = ref(false);
const successMessage = ref<string | null>(null);
const error = ref<string | null>(null);

const contact = ref({
  name: '',
  phone: '',
  email: ''
});

const canSubmit = computed(() => 
  selectedQuantity.value > 0 && 
  contact.value.name.trim() && 
  (contact.value.phone.trim() || contact.value.email.trim()) &&
  consentToProcessing.value
);

const formatPrice = (cents: number) =>
  new Intl.NumberFormat('ru-RU', {
    style: 'currency',
    currency: 'RUB'
  }).format(cents / 100);

const selectCategory = (cat: SeatCategoryWithAvailability) => {
  selectedCategory.value = cat;
  selectedQuantity.value = 1;
  error.value = null;
};

const ticketText = (count: number): string => {
  const lastDigit = count % 10;
  const lastTwoDigits = count % 100;
  if (lastTwoDigits >= 11 && lastTwoDigits <= 19) return 'билетов';
  if (lastDigit === 1) return 'билет';
  if (lastDigit >= 2 && lastDigit <= 4) return 'билета';
  return 'билетов';
};

const loadCategories = async () => {
  if (!props.concertSlug) return;
  loading.value = true;
  error.value = null;
  try {
    categories.value = await fetchConcertCategoriesWithAvailabilityBySlug(props.concertSlug);
  } catch (e) {
    error.value = 'Не удалось загрузить категории';
    categories.value = [];
  } finally {
    loading.value = false;
  }
};

const submitReservation = async () => {
  if (!selectedCategory.value || selectedQuantity.value === 0) return;
  if (!contact.value.name.trim()) {
    error.value = 'Укажите имя';
    return;
  }
  if (!contact.value.phone.trim() && !contact.value.email.trim()) {
    error.value = 'Укажите телефон или email';
    return;
  }
  if (!consentToProcessing.value) {
    error.value = 'Необходимо дать согласие на обработку персональных данных';
    return;
  }

  reservationLoading.value = true;
  error.value = null;
  successMessage.value = null;

  try {
    const res = await createReservation({
      concertId: props.concertId,
      categoryQuantities: [{
        categoryId: selectedCategory.value.id,
        quantity: selectedQuantity.value
      }],
      buyerName: contact.value.name.trim(),
      buyerPhone: contact.value.phone.trim(),
      buyerEmail: contact.value.email.trim()
    });

    successMessage.value = `Бронь #${res.reservationId} создана. В скором времени с вами свяжется менеджер.`;
    
    // Сброс формы после успеха
    setTimeout(() => {
      selectedCategory.value = null;
      selectedQuantity.value = 1;
      contact.value = { name: '', phone: '', email: '' };
      consentToProcessing.value = false;
      loadCategories(); // Обновляем доступность
    }, 3000);
  } catch (e) {
    error.value = 'Не удалось создать бронь';
  } finally {
    reservationLoading.value = false;
  }
};

const handleClose = () => {
  selectedCategory.value = null;
  selectedQuantity.value = 1;
  emit('close');
};

const close = () => emit('close');

watch(
  () => [props.open, props.concertSlug],
  () => {
    if (props.open && props.concertSlug) {
      loadCategories();
      selectedCategory.value = null;
      selectedQuantity.value = 1;
      consentToProcessing.value = false;
      successMessage.value = null;
      error.value = null;
    }
  },
  { immediate: true }
);
</script>

<style scoped>
.modal-backdrop-custom {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  z-index: 1050;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 20px;
  overflow-y: auto;
}

.simple-ticket-panel {
  background: white;
  border-radius: 16px;
  max-width: 700px;
  width: 100%;
  max-height: calc(100vh - 40px);
  overflow-y: auto;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  margin: auto 0;
}

.category-card {
  transition: all 0.2s ease;
  cursor: pointer;
}

.category-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.cursor-pointer {
  cursor: pointer;
}

.quantity-input-large {
  width: 120px;
  font-size: 1.5rem;
  font-weight: 600;
}

.quantity-controls-large .btn {
  width: 50px;
  height: 50px;
}

.availability-badge {
  font-size: 0.85rem;
  color: #155724;
}

.selected-category-info {
  background: #f8f9fa;
}

/* Mobile responsive */
@media (max-width: 576px) {
  .modal-backdrop-custom {
    padding: 10px;
  }
  
  .simple-ticket-panel {
    max-height: calc(100vh - 20px);
    border-radius: 12px;
  }
  
  .quantity-input-large {
    width: 80px;
    font-size: 1.2rem;
  }
  
  .quantity-controls-large .btn {
    width: 44px;
    height: 44px;
  }
}
</style>
