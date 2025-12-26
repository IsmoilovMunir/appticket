<template>
  <div 
    v-if="isVisible" 
    class="splash-overlay" 
    role="dialog" 
    aria-modal="true"
    @click.self="close"
  >
    <div class="splash-card" role="document">
      <button 
        class="splash-close splash-close-mobile" 
        aria-label="Закрыть афишу"
        @click="close"
      >
        ✕
      </button>
      <div class="splash-partners">
        <div class="splash-partners-age">16+</div>
        <div class="splash-partners-container">
          <div class="splash-partners-track">
            <div class="splash-partners-item">
              <img :src="chidLogo" alt="CHID" class="splash-partners-logo" />
            </div>
            <div class="splash-partners-item">
              <img :src="asakiLogo" alt="Асаки" class="splash-partners-logo splash-partners-logo-asaki" />
            </div>
            <div class="splash-partners-item">
              <img :src="drnematboevLogo" alt="Dr. Nematboev" class="splash-partners-logo" />
            </div>
            <div class="splash-partners-item">
              <img :src="smartxLogo" alt="SmartX" class="splash-partners-logo" />
            </div>
            <div class="splash-partners-item">
              <img :src="jxnLogo" alt="JXN" class="splash-partners-logo" />
            </div>
            <!-- Duplicate for seamless loop -->
            <div class="splash-partners-item">
              <img :src="chidLogo" alt="CHID" class="splash-partners-logo" />
            </div>
            <div class="splash-partners-item">
              <img :src="asakiLogo" alt="Асаки" class="splash-partners-logo splash-partners-logo-asaki" />
            </div>
            <div class="splash-partners-item">
              <img :src="drnematboevLogo" alt="Dr. Nematboev" class="splash-partners-logo" />
            </div>
            <div class="splash-partners-item">
              <img :src="smartxLogo" alt="SmartX" class="splash-partners-logo" />
            </div>
            <div class="splash-partners-item">
              <img :src="jxnLogo" alt="JXN" class="splash-partners-logo" />
            </div>
          </div>
        </div>
      </div>
      <div class="splash-media">
        <img 
          :src="posterImage" 
          alt="Новогодний банкет-вечер с Сафармухаммадом"
          class="splash-poster"
        />
      </div>
      <div class="splash-promo">
        <button class="discount-badge-hero btn px-3 py-2" type="button">
          <span class="fw-semibold d-block">
            Акция действует до: <span class="discount-date">29.12.2025</span>
          </span>
          <span v-if="!isDiscountExpired" class="small d-block">
            Осталось:
            <strong>{{ discountDaysLeft }}</strong> д
            <strong>{{ discountHoursLeft }}</strong> ч
            <strong>{{ discountMinutesLeft }}</strong> м
            <strong>{{ discountSecondsLeft }}</strong> с
          </span>
          <span v-else class="small d-block">
            Акция завершена
          </span>
        </button>
      </div>
      <div class="splash-body">
        <div class="splash-title">
          Новогодний банкет-вечер с Сафармухаммадом в Москве
        </div>
        <div class="splash-desc">
          3 часа живой родной музыки, сытный банкет и атмосфера, ради которой приходят со своими
        </div>
        <div class="splash-included">
          Еда, напитки, шоу и концерт — всё включено
        </div>
        <div class="splash-meta">
          <div class="date-pill">3 января 2026</div>
          <div class="time-pill">Начало в 19:00</div>
          <div class="location-pill">
            <i class="bi bi-geo-alt-fill"></i>
            <span>Банкетный зал Асаки, Москва</span>
          </div>
          <div class="age-pill">Вход 16+</div>
        </div>
        <div class="splash-price">
          <span class="price-label">Билеты от</span>
          <span class="price-value">5 000 ₽</span>
        </div>
        <button 
          class="btn-buy" 
          @click="handleBuyClick"
        >
          Купить билет
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, computed } from 'vue';
import postImage from '@/assets/post.png';
import chidLogo from '@/assets/chid.svg';
import asakiLogo from '@/assets/asaki.svg';
import drnematboevLogo from '@/assets/drnematboev.svg';
import smartxLogo from '@/assets/smartx.svg';
import jxnLogo from '@/assets/jxnlogo.svg';

const props = defineProps<{
  onBuyClick?: () => void;
}>();

const emit = defineEmits<{
  close: [];
  buy: [];
}>();

const isVisible = ref(false);
const posterImage = postImage;

// Таймер для автоматического закрытия через 7 секунд
let autoCloseTimerId: number | null = null;

// Обратный отсчёт скидки до 29.12.2025 23:59:59
const discountTargetDate = new Date('2025-12-29T23:59:59');
const discountNow = ref(new Date());
let discountTimerId: number | null = null;

const updateDiscountTime = () => {
  discountNow.value = new Date();
};

const discountDiffMs = computed(
  () => discountTargetDate.getTime() - discountNow.value.getTime()
);

const isDiscountExpired = computed(() => discountDiffMs.value <= 0);

const discountDaysLeft = computed(() => {
  if (isDiscountExpired.value) return 0;
  return Math.floor(discountDiffMs.value / (1000 * 60 * 60 * 24));
});

const discountHoursLeft = computed(() => {
  if (isDiscountExpired.value) return 0;
  const remaining = discountDiffMs.value % (1000 * 60 * 60 * 24);
  return Math.floor(remaining / (1000 * 60 * 60));
});

const discountMinutesLeft = computed(() => {
  if (isDiscountExpired.value) return 0;
  const remaining = discountDiffMs.value % (1000 * 60 * 60);
  return Math.floor(remaining / (1000 * 60));
});

const discountSecondsLeft = computed(() => {
  if (isDiscountExpired.value) return 0;
  const remaining = discountDiffMs.value % (1000 * 60);
  return Math.floor(remaining / 1000);
});

// Отслеживаем изменение видимости и управляем меню
watch(isVisible, (newValue) => {
  // Небольшая задержка для гарантии, что обработчики зарегистрированы
  setTimeout(() => {
    if (newValue) {
      // Скрываем меню при открытии splash
      window.dispatchEvent(new CustomEvent('hideNavigationMenu'));
    } else {
      // Показываем меню обратно при закрытии splash
      window.dispatchEvent(new CustomEvent('showNavigationMenu'));
    }
  }, 100);
}, { immediate: false });

const close = () => {
  // Очищаем таймер автоматического закрытия, если он активен
  if (autoCloseTimerId !== null) {
    clearTimeout(autoCloseTimerId);
    autoCloseTimerId = null;
  }
  isVisible.value = false;
  emit('close');
};

const handleBuyClick = () => {
  // Отправляем событие для открытия модального окна покупки билетов
  window.dispatchEvent(new CustomEvent('openTicketModal'));
  
  if (props.onBuyClick) {
    props.onBuyClick();
  }
  emit('buy');
  close();
};

onMounted(() => {
  // Запускаем таймер обратного отсчета
  discountTimerId = window.setInterval(updateDiscountTime, 1000);
  
  // Показываем splash при каждой загрузке страницы с небольшой задержкой для плавности
  setTimeout(() => {
    isVisible.value = true;
    
    // Запускаем таймер автоматического закрытия через 7 секунд
    autoCloseTimerId = window.setTimeout(() => {
      if (isVisible.value) {
        close();
      }
    }, 7000);
  }, 300);
});

onUnmounted(() => {
  if (discountTimerId !== null) {
    clearInterval(discountTimerId);
  }
  if (autoCloseTimerId !== null) {
    clearTimeout(autoCloseTimerId);
  }
});
</script>

<style scoped>
.splash-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(8px);
  z-index: 10000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
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

.splash-card {
  background: transparent;
  border-radius: 16px;
  max-width: 450px;
  width: 100%;
  max-height: 98vh;
  height: fit-content;
  overflow-y: auto;
  overflow-x: hidden;
  position: relative;
  box-shadow: none;
  animation: slideUp 0.4s ease-out;
  display: flex;
  flex-direction: column;
}

@keyframes slideUp {
  from {
    transform: translateY(30px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.splash-close {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  background: rgba(255, 255, 255, 0.2);
  color: #ffffff;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
  transition: all 0.2s ease;
  backdrop-filter: blur(4px);
}

.splash-close:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: scale(1.1);
}

.splash-partners {
  width: 100%;
  padding: 0 0 8px 0;
  margin-top: 0;
  background: transparent;
  overflow: hidden;
  flex-shrink: 0;
  position: relative;
  transform: translateY(20px);
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
}

.splash-partners-age {
  position: absolute;
  left: 0;
  bottom: 0;
  font-size: 18px;
  font-weight: 700;
  color: #ffffff;
  padding: 4px 8px;
  background: #18723F;
  border-radius: 6px;
  z-index: 5;
}

.splash-partners-container {
  width: 100%;
  overflow: hidden;
  position: relative;
}

.splash-partners-track {
  display: flex;
  align-items: center;
  gap: 30px;
  animation: splashPartnersScroll 20s linear infinite;
  width: fit-content;
}

@keyframes splashPartnersScroll {
  0% {
    transform: translateX(0) translateY(0);
  }
  100% {
    transform: translateX(-50%) translateY(0);
  }
}

.splash-partners-item {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0.7;
  transition: opacity 0.3s ease;
}

.splash-partners-item:hover {
  opacity: 1;
}

.splash-partners-logo {
  height: 30px;
  width: auto;
  max-width: 120px;
  object-fit: contain;
  filter: brightness(0) invert(1);
  transition: opacity 0.3s ease;
  opacity: 0.9;
}

.splash-partners-logo:hover {
  opacity: 1;
}

.splash-partners-logo-asaki {
  height: 35px;
}

.splash-media {
  width: 100%;
  overflow: hidden;
  border-radius: 16px 16px 0 0;
  flex-shrink: 0;
  display: block;
  background: transparent;
  max-height: 25vh;
  padding: 0;
  padding-top: 15px;
  margin: 0 auto;
}

.splash-poster {
  width: 90%;
  height: auto;
  max-height: 25vh;
  display: block;
  object-fit: contain;
  object-position: center;
  margin: 0 auto;
}

.splash-promo {
  padding: 8px 0;
  text-align: center;
  flex-shrink: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 0;
  background: linear-gradient(135deg, rgba(24, 114, 63, 0.05) 0%, rgba(31, 157, 108, 0.05) 100%);
  width: 100%;
}

.discount-badge-hero {
  border-radius: 12px;
  background: linear-gradient(135deg, #18723F 0%, #1f9d6c 100%);
  color: #fff;
  font-size: 0.75rem;
  box-shadow: 0 4px 16px rgba(24, 114, 63, 0.4), 0 2px 8px rgba(0, 0, 0, 0.15);
  border: 2px solid rgba(255, 255, 255, 0.3);
  text-transform: none;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 10px 16px;
  margin: 0;
  cursor: default;
  gap: 4px;
  width: calc(100% - 40px);
  max-width: 100%;
  transition: all 0.3s ease;
}

.discount-badge-hero:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(24, 114, 63, 0.5), 0 3px 10px rgba(0, 0, 0, 0.2);
}

.discount-badge-hero .fw-semibold {
  font-size: 0.85rem;
  color: #fff;
  line-height: 1.3;
  font-weight: 600;
  letter-spacing: 0.2px;
}

.discount-badge-hero .discount-date {
  font-size: 1rem;
  font-weight: 800;
  color: #fff;
  background: rgba(255, 255, 255, 0.25);
  padding: 2px 6px;
  border-radius: 6px;
  display: inline-block;
  margin-left: 4px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  border: 2px solid rgba(255, 255, 255, 0.4);
  letter-spacing: 0.3px;
}

.discount-badge-hero .small {
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.95);
  line-height: 1.4;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
  flex-wrap: wrap;
  justify-content: center;
}

.discount-badge-hero strong {
  color: #fff;
  font-weight: 700;
  font-size: 0.85rem;
  background: rgba(255, 255, 255, 0.25);
  padding: 2px 5px;
  border-radius: 5px;
  margin: 0 2px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.splash-body {
  padding: 16px 20px 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex: 0 0 auto;
  background: #ffffff;
  border-radius: 0 0 16px 16px;
}

.splash-title {
  font-size: 26px !important;
  font-weight: 700;
  color: #1d1f23;
  line-height: 1.3;
  margin: 0;
}

.splash-desc {
  font-size: 18px !important;
  color: #4a5568;
  line-height: 1.4;
  margin: 0;
}

.splash-included {
  font-size: 16px !important;
  color: #18723F;
  font-weight: 600;
  padding: 8px 12px;
  background: rgba(24, 114, 63, 0.1);
  border-radius: 8px;
  display: inline-block;
  margin: 0;
}

.splash-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  margin: 0;
}

.date-pill,
.time-pill,
.age-pill {
  font-size: 16px !important;
  color: #1d1f23;
  font-weight: 500;
  padding: 8px 12px;
  background: #f8fbff;
  border-radius: 8px;
  display: inline-flex;
  align-items: center;
  white-space: nowrap;
}

.location-pill {
  font-size: 16px !important;
  color: #1d1f23;
  font-weight: 500;
  padding: 8px 12px;
  background: #f8fbff;
  border-radius: 8px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
}

.location-pill i {
  color: #18723F;
  font-size: 17px !important;
  flex-shrink: 0;
}

.splash-price {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin: 0;
  padding: 14px 18px;
  background: linear-gradient(135deg, #18723F 0%, #1f9d6c 100%);
  border-radius: 12px;
}

.price-label {
  font-size: 17px !important;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
}

.price-value {
  font-size: 36px !important;
  color: #ffffff;
  font-weight: 700;
}

.btn-buy {
  width: 100%;
  padding: 16px 28px;
  background: #18723F;
  color: #ffffff;
  border: none;
  border-radius: 12px;
  font-size: 19px !important;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 4px 12px rgba(24, 114, 63, 0.3);
  margin-top: 4px;
}

.btn-buy:hover {
  background: #145a32;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(24, 114, 63, 0.4);
}

.btn-buy:active {
  transform: translateY(0);
}

/* Мобильная адаптация */
@media (max-width: 576px) {
  .splash-overlay {
    padding: 5px;
  }

  .splash-card {
    max-width: 100%;
    border-radius: 16px;
    max-height: 100vh;
    min-height: auto;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
  }

  .splash-close {
    position: absolute !important;
    top: 8px !important;
    right: 8px !important;
    width: 28px;
    height: 28px;
    font-size: 16px;
    margin: 0;
    order: 1;
  }

  .splash-partners {
    padding: 4px 0 8px;
    order: 2;
  }

  .splash-partners-track {
    gap: 16px;
  }

  .splash-partners-logo {
    height: 20px;
    max-width: 75px;
  }

  .splash-partners-logo-asaki {
    height: 24px;
  }

  .splash-partners-age {
    font-size: 12px;
    padding: 2px 6px;
  }

  .splash-media {
    width: 100%;
    border-radius: 16px 16px 0 0;
    max-height: 25vh;
    padding-top: 8px;
    order: 3;
    margin: 0 auto;
  }

  .splash-poster {
    width: 90%;
    max-height: 25vh;
    object-fit: contain;
    margin: 0 auto;
  }

  .splash-promo {
    padding: 6px 0;
    order: 4;
  }

  .discount-badge-hero {
    font-size: 0.7rem;
    padding: 8px 12px;
    width: calc(100% - 24px);
    gap: 4px;
    border-radius: 10px;
  }

  .discount-badge-hero .fw-semibold {
    font-size: 0.8rem;
    line-height: 1.2;
  }

  .discount-badge-hero .discount-date {
    font-size: 0.95rem;
    padding: 1px 5px;
    margin-left: 3px;
  }

  .discount-badge-hero .small {
    font-size: 0.7rem;
    line-height: 1.3;
    gap: 3px;
  }

  .discount-badge-hero strong {
    font-size: 0.8rem;
    padding: 1px 4px;
    margin: 0 2px;
  }

  .splash-body {
    padding: 10px 12px 12px;
    gap: 6px;
    order: 5;
    flex: 1;
    display: flex;
    flex-direction: column;
  }

  .splash-title {
    font-size: 18px !important;
    line-height: 1.3;
    margin-bottom: 4px;
  }

  .splash-desc {
    font-size: 13px !important;
    margin-bottom: 4px;
  }

  .splash-included {
    font-size: 12px !important;
    padding: 5px 8px;
    margin-bottom: 4px;
  }

  .splash-meta {
    gap: 4px;
    margin-bottom: 4px;
  }

  .date-pill,
  .time-pill,
  .age-pill,
  .location-pill {
    font-size: 11px !important;
    padding: 4px 8px;
  }

  .location-pill i {
    font-size: 12px !important;
  }

  .splash-price {
    padding: 8px 12px;
    margin-bottom: 8px;
    margin-top: auto;
  }

  .price-label {
    font-size: 12px !important;
  }

  .price-value {
    font-size: 22px !important;
  }

  .btn-buy {
    padding: 10px 18px;
    font-size: 15px !important;
    margin-top: 0;
    order: 6;
  }
}

</style>

