<template>
  <div v-if="open" class="modal-backdrop-custom" @click.self="close">
    <div class="modal-panel p-4">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
          <h2 class="h4 mb-1">Выбор мест</h2>
          <p class="text-body-secondary mb-0">Выберите до 10 мест на схеме зала.</p>
        </div>
        <button class="btn btn-outline-secondary" @click="close">
          <i class="bi bi-x-lg me-1" /> Закрыть
        </button>
      </div>
      <div class="row g-4 flex-grow-1 seat-map-layout">
        <div class="col-lg-8 seat-map-column">
          <div class="price-filter btn-group mb-3 flex-nowrap flex-shrink-0">
            <button
              v-for="option in priceOptions"
              :key="option.label"
              type="button"
              class="btn"
              :class="priceFilter === option.value ? 'btn-primary' : 'btn-outline-primary'"
              @click="selectPriceFilter(option.value)"
            >
              {{ option.label }}
            </button>
          </div>
          <div class="seat-map border rounded-4 p-3 bg-body flex-grow-1 d-flex flex-column position-relative">
            <HallMap class="flex-grow-1" :scheme-url="props.schemeUrl" @danceFloorClicked="onDanceFloorClicked" />
            <!-- Кнопка "Купить X билет" внизу карты для мобильных -->
            <div class="d-lg-none buy-button-toolbar">
              <button
                class="buy-btn-toolbar"
                :disabled="selectedSeats.length === 0 && seatStore.danceFloorQuantity === 0"
                @click="showCheckoutPanel = true"
              >
                <span v-if="selectedSeats.length > 0 || seatStore.danceFloorQuantity > 0">
                  Купить {{ getTicketText(selectedSeats.length + seatStore.danceFloorQuantity) }}
                </span>
                <span v-else>Выберите места</span>
              </button>
            </div>
          </div>
          <LegendPanel class="mt-3 flex-shrink-0" />
        </div>
        <div class="col-lg-4 cart-column">
          <div class="cart-panel border rounded-4 p-3 bg-light-subtle h-100 d-flex flex-column">
            <h3 class="h5 mb-2">Корзина</h3>
            <p class="text-body-secondary small mb-2" style="font-size: 0.75rem;">
              Выбранные места будут удерживаться 30 минут после отправки заявки.
            </p>
            <div class="checkout-summary sticky-checkout mb-2">
              <div class="d-flex justify-content-between align-items-center mb-2">
                <span class="fw-semibold small">Сумма: {{ formatPrice(total) }}</span>
                <small class="text-body-secondary" style="font-size: 0.75rem;">{{ selected.size + seatStore.danceFloorQuantity }}/10 мест</small>
              </div>
              <div class="form-check mb-2">
                <input
                  class="form-check-input"
                  type="checkbox"
                  id="consentCheckbox"
                  v-model="consentToProcessing"
                  style="margin-top: 0.2rem;"
                />
                <label class="form-check-label small" for="consentCheckbox" style="font-size: 0.75rem; line-height: 1.3;">
                  Я согласен на обработку персональных данных
                </label>
              </div>
              <button
                class="btn btn-primary w-100 btn-sm btn-buy-accent"
                :disabled="(selectedSeats.length === 0 && seatStore.danceFloorQuantity === 0) || reservationLoading || !consentToProcessing"
                @click="submitReservation"
              >
                <span v-if="reservationLoading" class="spinner-border spinner-border-sm me-2"></span>
                Отправить менеджеру
              </button>
              <p v-if="successMessage" class="text-success small mt-2 mb-0" style="font-size: 0.75rem;">
                {{ successMessage }}
              </p>
              <p v-else class="text-body-secondary small mt-2 mb-0" style="font-size: 0.7rem;">
                После отправки менеджер свяжется с вами, чтобы подтвердить заказ.
              </p>
            </div>
            <div class="contact-form-block mb-2">
              <label class="form-label small text-uppercase text-body-secondary mb-1">Имя</label>
              <input
                v-model="reservationStore.contact.name"
                class="form-control form-control-sm mb-1"
                placeholder="Имя и фамилия"
              />
              <label class="form-label small text-uppercase text-body-secondary mb-1">Телефон</label>
              <input
                v-model="reservationStore.contact.phone"
                class="form-control form-control-sm mb-1"
                placeholder="+7"
              />
              <label class="form-label small text-uppercase text-body-secondary mb-1">
                Email (билет отправим на вашу почту)
              </label>
              <input
                v-model="reservationStore.contact.email"
                class="form-control form-control-sm mb-1"
                placeholder="example@site.com"
              />
              <small class="text-body-secondary small">Укажите почту, чтобы получить билеты на email.</small>
            </div>
            <div
              class="selected-seats-container flex-grow-1"
              :class="{ 'selected-seats-scroll': (selectedSeats.length + (seatStore.danceFloorQuantity > 0 ? 1 : 0)) > 2 }"
            >
              <div v-if="selectedSeats.length === 0 && seatStore.danceFloorQuantity === 0" class="text-center text-body-secondary py-3">
                Выберите места на схеме
              </div>
              <div v-else class="list-group list-group-flush">
                <!-- Обычные места -->
                <div v-for="seat in selectedSeats" :key="seat.id" class="list-group-item border-0 px-0 py-2 d-flex justify-content-between align-items-center">
                  <div class="flex-grow-1">
                    <div class="fw-semibold small">
                      Стол {{ seat.tableNumber }}, Место {{ seat.chairNumber }}
                    </div>
                    <div class="text-body-secondary" style="font-size: 0.75rem;">{{ getCategoryDisplayName(seat.categoryName) }}</div>
                  </div>
                  <div class="fw-semibold small ms-2">{{ formatPrice(seat.priceCents) }}</div>
                </div>

                <!-- Танцпол -->
                <div v-if="seatStore.danceFloorQuantity > 0" class="list-group-item border-0 px-0 py-2 d-flex justify-content-between align-items-center">
                  <div class="flex-grow-1">
                    <div class="fw-semibold small">
                      Танцпол × {{ seatStore.danceFloorQuantity }}
                    </div>
                    <div class="text-body-secondary" style="font-size: 0.75rem;">Стоячие места</div>
                  </div>
                  <div class="d-flex align-items-center">
                    <div class="fw-semibold small me-2">{{ formatPrice(seatStore.danceFloorQuantity * seatStore.danceFloorPrice) }}</div>
                    <button
                      class="btn btn-sm btn-outline-danger p-1"
                      @click="removeDanceFloorTickets"
                      title="Удалить билеты на танцпол"
                    >
                      <i class="bi bi-x"></i>
                    </button>
                  </div>
                </div>
              </div>
            </div>
            <p v-if="reservationError" class="text-danger small mt-2">{{ reservationError }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Выдвижная панель справа для мобильных -->
    <div
      v-if="showCheckoutPanel"
      class="checkout-sidebar-wrapper"
    >
      <div class="checkout-sidebar-backdrop" @click="showCheckoutPanel = false"></div>
      <div class="checkout-sidebar">
        <div class="checkout-sidebar-content">
        <div class="checkout-sidebar-header d-flex justify-content-between align-items-center mb-3">
          <h3 class="h5 mb-0">Оформление заказа</h3>
          <button class="btn btn-sm btn-outline-secondary" @click="showCheckoutPanel = false">
            <i class="bi bi-x-lg"></i>
          </button>
        </div>

        <!-- Информация о билетах -->
        <div class="mb-3">
          <h4 class="h6 mb-2">Выбранные места</h4>
          <div class="list-group list-group-flush">
            <div
              v-for="seat in selectedSeats"
              :key="seat.id"
              class="list-group-item border-0 px-0 py-2 d-flex justify-content-between align-items-center"
            >
              <div class="flex-grow-1">
                <div class="fw-semibold small">
                  Стол {{ seat.tableNumber }}, Место {{ seat.chairNumber }}
                </div>
                <div class="text-body-secondary" style="font-size: 0.75rem;">
                  {{ seat.categoryName }}
                </div>
              </div>
              <div class="fw-semibold small ms-2">{{ formatPrice(seat.priceCents) }}</div>
            </div>

            <!-- Танцпол в выдвижной панели -->
            <div v-if="seatStore.danceFloorQuantity > 0" class="list-group-item border-0 px-0 py-2 d-flex justify-content-between align-items-center">
              <div class="flex-grow-1">
                <div class="fw-semibold small">
                  Танцпол × {{ seatStore.danceFloorQuantity }}
                </div>
                <div class="text-body-secondary" style="font-size: 0.75rem;">
                  Стоячие места
                </div>
              </div>
              <div class="d-flex align-items-center">
                <div class="fw-semibold small me-2">{{ formatPrice(seatStore.danceFloorQuantity * seatStore.danceFloorPrice) }}</div>
                <button
                  class="btn btn-sm btn-outline-danger p-1"
                  @click="removeDanceFloorTickets"
                  title="Удалить билеты на танцпол"
                >
                  <i class="bi bi-x"></i>
                </button>
              </div>
            </div>
          </div>
          <div class="mt-3 p-2 bg-light rounded">
            <div class="d-flex justify-content-between align-items-center">
              <span class="fw-semibold">Итого:</span>
              <span class="fw-bold text-primary fs-5">{{ formatPrice(total) }}</span>
            </div>
            <div class="text-body-secondary small mt-1">
              {{ selectedSeats.length }} {{ getTicketText(selectedSeats.length) }}
            </div>
          </div>
        </div>

        <!-- Форма контактов -->
        <div class="contact-form-block mb-3">
          <h4 class="h6 mb-2">Контактная информация</h4>
          <label class="form-label small text-uppercase text-body-secondary mb-1">Имя</label>
          <input
            v-model="reservationStore.contact.name"
            class="form-control form-control-sm mb-2"
            placeholder="Имя и фамилия"
          />
          <label class="form-label small text-uppercase text-body-secondary mb-1">Телефон</label>
          <input
            v-model="reservationStore.contact.phone"
            class="form-control form-control-sm mb-2"
            placeholder="+7"
          />
          <label class="form-label small text-uppercase text-body-secondary mb-1">
            Email (билет отправим на вашу почту)
          </label>
          <input
            v-model="reservationStore.contact.email"
            class="form-control form-control-sm mb-2"
            placeholder="example@site.com"
          />
          <small class="text-body-secondary">Укажите почту, чтобы получить билеты на email.</small>
        </div>

        <!-- Согласие и кнопка отправки -->
        <div class="checkout-actions">
          <div class="form-check mb-3">
            <input
              class="form-check-input"
              type="checkbox"
              id="consentCheckboxSidebar"
              v-model="consentToProcessing"
            />
            <label class="form-check-label small" for="consentCheckboxSidebar" style="font-size: 0.75rem; line-height: 1.3;">
              Я согласен на обработку персональных данных
            </label>
          </div>
          <button
            class="btn btn-primary w-100 btn-lg"
            :disabled="selectedSeats.length === 0 || reservationLoading || !consentToProcessing"
            @click="submitReservation"
          >
            <span v-if="reservationLoading" class="spinner-border spinner-border-sm me-2"></span>
            Отправить менеджеру
          </button>
          <p v-if="successMessage" class="text-success small mt-2 mb-0">
            {{ successMessage }}
          </p>
          <p v-else-if="reservationError" class="text-danger small mt-2 mb-0">
            {{ reservationError }}
          </p>
          <p v-else class="text-body-secondary small mt-2 mb-0">
            После отправки менеджер свяжется с вами, чтобы подтвердить заказ.
          </p>
        </div>
        </div>
      </div>
    </div>

    <!-- Quantity Selector для танцпола -->
    <div
      v-if="showDanceFloorSelector"
      class="dance-floor-modal-backdrop"
      @click.self="showDanceFloorSelector = false"
    >
      <div class="dance-floor-modal">
        <!-- Заголовок -->
        <div class="dance-floor-modal-header">
          <div class="dance-floor-icon">
            <i class="bi bi-music-note-beamed"></i>
          </div>
          <h3 class="dance-floor-modal-title">Танцпол</h3>
          <button
            class="dance-floor-close-btn"
            @click="showDanceFloorSelector = false"
          >
            <i class="bi bi-x"></i>
          </button>
        </div>

        <!-- Описание -->
        <div class="dance-floor-modal-body">
          <p class="dance-floor-description">
            Стоячие места для свободного движения, пения и танцев!
          </p>

          <!-- Цена -->
          <div class="dance-floor-price">
            <div class="price-amount">{{ formatPrice(danceFloorPrice) }}</div>
            <div class="price-label">за билет</div>
          </div>

          <!-- Доступность -->
          <div class="availability-info">
            <i class="bi bi-check-circle-fill"></i>
            Доступно {{ danceFloorAvailableSeats }} мест
          </div>

          <!-- Quantity Selector -->
          <div class="quantity-section">
            <div class="quantity-controls">
              <button
                class="quantity-btn quantity-minus"
                @click="seatStore.setDanceFloorQuantity(Math.max(0, seatStore.danceFloorQuantity - 1))"
                :disabled="seatStore.danceFloorQuantity <= 0"
              >
                <i class="bi bi-dash"></i>
              </button>

              <div class="quantity-display">
                <input
                  type="number"
                  class="quantity-input"
                  v-model.number="seatStore.danceFloorQuantity"
                  @input="seatStore.setDanceFloorQuantity(Number(($event.target as HTMLInputElement).value) || 0)"
                  min="0"
                  :max="Math.min(danceFloorAvailableSeats, 10 - seatStore.selected.size)"
                />
                <div class="quantity-label">билетов</div>
              </div>

              <button
                class="quantity-btn quantity-plus"
                @click="seatStore.setDanceFloorQuantity(Math.min(danceFloorAvailableSeats, 10 - seatStore.selected.size, seatStore.danceFloorQuantity + 1))"
                :disabled="seatStore.danceFloorQuantity >= Math.min(danceFloorAvailableSeats, 10 - seatStore.selected.size)"
              >
                <i class="bi bi-plus"></i>
              </button>
            </div>

            <div class="quantity-limits">
              Макс: {{ Math.min(danceFloorAvailableSeats, 10 - seatStore.selected.size) }} билетов
            </div>
          </div>

          <!-- Итого -->
          <div v-if="seatStore.danceFloorQuantity > 0" class="total-section">
            <div class="total-row">
              <span class="total-label">Итого:</span>
              <span class="total-amount">{{ formatPrice(seatStore.danceFloorQuantity * danceFloorPrice) }}</span>
            </div>
          </div>
        </div>

        <!-- Кнопки действий -->
        <div class="dance-floor-modal-footer">
          <button
            class="btn-add"
            @click="addDanceFloorTickets(seatStore.danceFloorQuantity)"
            :disabled="seatStore.danceFloorQuantity <= 0"
          >
            <i class="bi bi-cart-plus"></i>
            <span class="btn-text">Добавить</span>
            <span class="btn-quantity">{{ seatStore.danceFloorQuantity || 0 }}</span>
          </button>
          <button
            class="btn-cancel"
            @click="showDanceFloorSelector = false"
          >
            Отмена
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, watch, ref } from 'vue';
import HallMap from './HallMap.vue';
import LegendPanel from './LegendPanel.vue';
import { useSeatStore } from '../stores/seatStore';
import { useReservationStore } from '../stores/reservationStore';

const props = defineProps<{
  open: boolean;
  concertId: number;
  schemeUrl?: string | null;
}>();

const emit = defineEmits(['close']);

const seatStore = useSeatStore();
const reservationStore = useReservationStore();
const consentToProcessing = ref(false);
const showCheckoutPanel = ref(false);
const showDanceFloorSelector = ref(false);
const danceFloorAvailableSeats = ref(0);
const danceFloorPrice = ref(0);

// Функция форматирования цены
const formatPrice = (cents: number) =>
  new Intl.NumberFormat('ru-RU', {
    style: 'currency',
    currency: 'RUB'
  }).format(cents / 100);

// Динамически формируем опции фильтров по ценам из загруженных мест
const priceOptions = computed(() => {
  const options: Array<{ label: string; value: number | null }> = [
    { label: 'Все категории', value: null }
  ];
  
  // Извлекаем уникальные цены из мест
  const uniquePrices = new Set<number>();
  seatStore.seats.forEach((seat) => {
    if (seat.priceCents) {
      uniquePrices.add(seat.priceCents);
    }
  });
  
  // Сортируем цены по возрастанию и создаем опции
  const sortedPrices = Array.from(uniquePrices).sort((a, b) => a - b);
  sortedPrices.forEach((priceCents) => {
    options.push({
      label: formatPrice(priceCents),
      value: priceCents
    });
  });
  
  return options;
});

const selectedSeats = computed(() => seatStore.selectedSeats);
const selected = computed(() => seatStore.selected);
const total = computed(() => seatStore.totalSelectedPrice);
const reservationLoading = computed(() => reservationStore.loading);
const reservationError = computed(() => reservationStore.error);
const successMessage = computed(() => reservationStore.successMessage);
const priceFilter = computed(() => seatStore.priceFilter);

onMounted(() => {
  if (props.open) {
    seatStore.init(props.concertId);
  }
});

watch(
  () => props.open,
  (open) => {
    if (open) {
      seatStore.init(props.concertId);
      consentToProcessing.value = false; // Сбрасываем согласие при открытии модального окна
      showCheckoutPanel.value = false; // Закрываем панель при открытии модального окна
    }
  }
);

watch(
  () => selectedSeats.value.length,
  (newLength) => {
    if (newLength === 0) {
      showCheckoutPanel.value = false; // Закрываем панель, если все места сняты
    }
  }
);

const selectPriceFilter = (value: number | null) => {
  if (seatStore.priceFilter === value) {
    seatStore.setPriceFilter(null);
  } else {
    seatStore.setPriceFilter(value);
  }
};

const submitReservation = async () => {
  if (!consentToProcessing.value) {
    reservationStore.error = 'Необходимо дать согласие на обработку персональных данных';
    return;
  }
  await reservationStore.submitReservation(props.concertId, consentToProcessing.value);
  if (reservationStore.successMessage) {
    // Закрываем панель после успешной отправки
    setTimeout(() => {
      showCheckoutPanel.value = false;
    }, 2000);
  }
};

const close = () => emit('close');

const onDanceFloorClicked = (data: { availableSeats: number; price: number }) => {
  danceFloorAvailableSeats.value = data.availableSeats;
  danceFloorPrice.value = data.price;
  showDanceFloorSelector.value = true;
};

const addDanceFloorTickets = (quantity: number) => {
  seatStore.setDanceFloorQuantity(quantity);
  seatStore.danceFloorPrice = danceFloorPrice.value;
  showDanceFloorSelector.value = false;
};

const removeDanceFloorTickets = () => {
  seatStore.setDanceFloorQuantity(0);
};

const getCategoryDisplayName = (categoryName: string) => {
  // Убираем цену из названия категории (удаляем все цифры, пробелы и символ ₽)
  return categoryName.replace(/\s*\d+[\s₽]*/g, '').trim() || categoryName;
};

const getTicketText = (count: number): string => {
  const lastDigit = count % 10;
  const lastTwoDigits = count % 100;
  
  if (lastTwoDigits >= 11 && lastTwoDigits <= 19) {
    return `${count} билетов`;
  }
  
  if (lastDigit === 1) {
    return `${count} билет`;
  } else if (lastDigit >= 2 && lastDigit <= 4) {
    return `${count} билета`;
  } else {
    return `${count} билетов`;
  }
};
</script>

<style scoped>
/* Выдвижная панель справа для мобильных */
.checkout-sidebar-wrapper {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 9999;
  pointer-events: all;
}

.checkout-sidebar-backdrop {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  animation: fadeIn 0.3s ease;
}

.checkout-sidebar {
  position: absolute;
  top: 0;
  right: 0;
  width: 100%;
  max-width: 400px;
  height: 100%;
  background: white;
  box-shadow: -2px 0 10px rgba(0, 0, 0, 0.1);
  animation: slideInRight 0.3s ease;
  overflow-y: auto;
}

.checkout-sidebar-content {
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.checkout-sidebar-header {
  flex-shrink: 0;
  border-bottom: 1px solid #dee2e6;
  padding-bottom: 1rem;
}

.contact-form-block {
  flex-shrink: 0;
}

.checkout-actions {
  flex-shrink: 0;
  margin-top: auto;
  padding-top: 1rem;
  border-top: 1px solid #dee2e6;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideInRight {
  from {
    transform: translateX(100%);
  }
  to {
    transform: translateX(0);
  }
}

/* На десктопе панель не показывается */
@media (min-width: 992px) {
  .checkout-sidebar-wrapper {
    display: none;
  }
}

/* Модальное окно Quantity Selector для танцпола */
.dance-floor-modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(8px);
  z-index: 1070;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.dance-floor-modal {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border-radius: 20px;
  box-shadow:
    0 16px 48px rgba(0, 0, 0, 0.15),
    0 6px 24px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 380px;
  max-height: 90vh;
  overflow: hidden;
  animation: slideUp 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
}

@keyframes slideUp {
  from {
    transform: translateY(50px) scale(0.9);
    opacity: 0;
  }
  to {
    transform: translateY(0) scale(1);
    opacity: 1;
  }
}

.dance-floor-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.25rem 1.25rem 0.75rem;
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  position: relative;
}

.dance-floor-modal-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  opacity: 0.9;
}

.dance-floor-icon {
  width: 36px;
  height: 36px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.dance-floor-icon i {
  font-size: 1.1rem;
  color: white;
}

.dance-floor-modal-title {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 700;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  position: relative;
  z-index: 1;
}

.dance-floor-close-btn {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  cursor: pointer;
  transition: all 0.2s ease;
  backdrop-filter: blur(10px);
}

.dance-floor-close-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: scale(1.05);
}

.dance-floor-modal-body {
  padding: 1rem;
}

.dance-floor-description {
  color: #6c757d;
  font-size: 0.85rem;
  line-height: 1.4;
  margin-bottom: 1rem;
  text-align: center;
}

.dance-floor-price {
  text-align: center;
  margin-bottom: 0.5rem;
}

.price-amount {
  font-size: 2.2rem;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 0.25rem;
  text-shadow: 0 2px 4px rgba(102, 126, 234, 0.1);
}

.price-label {
  color: #8e9297;
  font-size: 0.8rem;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.availability-info {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%);
  color: #155724;
  padding: 0.4rem 0.8rem;
  border-radius: 16px;
  font-size: 0.8rem;
  font-weight: 600;
  margin-bottom: 1rem;
  border: 1px solid rgba(21, 87, 36, 0.1);
}

.availability-info i {
  font-size: 0.9rem;
}

.quantity-section {
  margin-bottom: 1rem;
}

.quantity-controls {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.4rem;
  margin-bottom: 0.5rem;
}

.quantity-btn {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  border: 2px solid #e9ecef;
  background: white;
  color: #6c757d;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 1.1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.quantity-btn:hover:not(:disabled) {
  border-color: #667eea;
  color: #667eea;
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.quantity-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.quantity-minus:hover:not(:disabled) {
  border-color: #dc3545;
  color: #dc3545;
  box-shadow: 0 4px 12px rgba(220, 53, 69, 0.2);
}

.quantity-plus:hover:not(:disabled) {
  border-color: #28a745;
  color: #28a745;
  box-shadow: 0 4px 12px rgba(40, 167, 69, 0.2);
}

.quantity-display {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 70px;
}

.quantity-input {
  width: 100%;
  text-align: center;
  border: 2px solid #e9ecef;
  border-radius: 10px;
  padding: 0.4rem;
  font-size: 1.1rem;
  font-weight: 700;
  color: #495057;
  background: white;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.2s ease;
}

.quantity-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.quantity-label {
  font-size: 0.7rem;
  color: #8e9297;
  margin-top: 0.2rem;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.quantity-limits {
  text-align: center;
  color: #8e9297;
  font-size: 0.75rem;
  font-weight: 500;
}

.total-section {
  background: linear-gradient(135deg, #f8f9ff 0%, #e8f2ff 100%);
  border-radius: 12px;
  padding: 0.8rem;
  border: 1px solid rgba(102, 126, 234, 0.1);
  margin-bottom: 0.5rem;
}

.total-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.total-label {
  color: #495057;
  font-weight: 600;
  font-size: 0.9rem;
}

.total-amount {
  font-size: 1.1rem;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.dance-floor-modal-footer {
  display: flex;
  flex-direction: row;
  gap: 0.5rem;
  padding: 1rem 1.25rem 1.5rem;
  margin-top: 1rem;
  border-top: 1px solid rgba(0, 0, 0, 0.08);
  background: #f8f9fa;
}

.btn-add {
  flex: 1;
  padding: 0.8rem 1rem;
  border-radius: 12px;
  border: none;
  background: #1a1a1a;
  color: white;
  font-weight: 700;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.4rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  order: -1;
}

.btn-add:hover:not(:disabled),
.btn-add:active:not(:disabled),
.btn-add:focus:not(:disabled) {
  background: #DBFF06;
  color: #000;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(219, 255, 6, 0.4);
}

.btn-add:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.btn-add i {
  font-size: 1rem;
}

.btn-text {
  line-height: 1;
}

.btn-quantity {
  font-size: 1.1rem;
  font-weight: 800;
  margin-left: 0.25rem;
}

.btn-cancel {
  flex: 1;
  padding: 0.7rem 1rem;
  border-radius: 10px;
  border: 2px solid #1a1a1a;
  background: transparent;
  color: #1a1a1a;
  font-weight: 600;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-cancel:hover,
.btn-cancel:active,
.btn-cancel:focus {
  background: #DBFF06;
  border-color: #DBFF06;
  color: #000;
  transform: translateY(-1px);
  box-shadow: 0 3px 10px rgba(219, 255, 6, 0.3);
}

/* Адаптивность */
@media (max-width: 480px) {
  .dance-floor-modal {
    margin: 0.5rem;
    max-width: calc(100vw - 1rem);
    max-height: calc(100vh - 2rem);
  }

  .dance-floor-modal-header {
    padding: 1rem 1rem 0.5rem;
  }

  .dance-floor-modal-body {
    padding: 0.8rem;
  }

  .dance-floor-modal-footer {
    padding: 0.8rem 1rem 1.2rem;
    margin-top: 1rem;
    flex-direction: row;
    gap: 0.4rem;
  }

  .btn-cancel,
  .btn-add {
    padding: 0.8rem 0.8rem;
    font-size: 0.85rem;
  }

  .price-amount {
    font-size: 1.8rem;
  }

  .quantity-controls {
    gap: 0.3rem;
  }

  .quantity-btn {
    width: 40px;
    height: 40px;
    font-size: 1rem;
  }

  .dance-floor-modal-title {
    font-size: 1rem;
  }

  .dance-floor-description {
    font-size: 0.8rem;
  }
}
</style>

