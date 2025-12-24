<template>
  <section class="pb-5 bg-gradient position-relative overflow-hidden text-white hero-section">
    <div class="container">
      <div class="row align-items-center g-4">
        <div class="col-lg-7">
          <h1 class="display-3 fw-bold mb-3 hero-title">
            Новогодний банкет-вечер с Сафармухаммадом в Москве
          </h1>
          <p class="fs-4 text-light mb-4 hero-subtitle">
            3 часа живой родной музыки, сытный банкет и атмосфера, ради которой приходят со своими
          </p>
          <div class="hero-utp mb-4">
            <span class="utp-badge">Еда, напитки, шоу и концерт — всё включено</span>
          </div>
          
          <div class="mt-3 mb-4">
            <button
              class="discount-badge-hero btn px-3 py-2"
              type="button"
              @click="$emit('cta')"
            >
              <span class="fw-semibold d-block">Акция действует до: 29.12.2025</span>
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
          
          <div class="hero-facts mb-4">
            <div class="fact-item">
              <i class="bi bi-calendar3"></i>
              <span>3 января 2026</span>
            </div>
            <div class="fact-item">
              <i class="bi bi-clock"></i>
              <span>Начало в 19:00</span>
            </div>
            <div class="fact-item">
              <i class="bi bi-geo-alt"></i>
              <span>Банкетный зал Асаки, Москва</span>
            </div>
            <div class="fact-item">
              <i class="bi bi-people"></i>
              <span>Вход 16+</span>
            </div>
          </div>

          <div class="hero-price mb-4">
            <span class="price-label">Билеты от</span>
            <span class="price-value">5 000 ₽</span>
          </div>

          <div class="d-flex gap-3 flex-wrap mt-4 mb-3 buttons-container">
            <button class="btn btn-light btn-lg px-5 py-3 buy-button-hero" @click="$emit('cta')">
              👉 Купить билет сейчас
            </button>
            <a href="https://t.me/surnek_events" class="btn btn-outline-light btn-lg px-4">
              Подробнее
            </a>
          </div>
        </div>
        <div class="col-lg-5">
          <div class="glass-card text-center parallax-container">
            <div class="parallax-wrapper">
              <img 
                ref="parallaxImage" 
                :src="posterImage" 
                alt="Poster" 
                class="img-fluid parallax-image" 
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, onUnmounted } from 'vue';
import safarImage from '@/assets/safar.png';

const props = defineProps<{
  title: string;
  description: string;
  date: string;
  venue: string;
  poster?: string;
}>();

// Разделяем title на две части: "Новогодний вечер с" и "SAFARMUHAMMAD"
const titleParts = computed(() => {
  const fullTitle = props.title;
  // Ищем "SAFARMUHAMMAD" в любом регистре
  const match = fullTitle.match(/^(.*?)\s+(SAFARMUHAMMAD.*)$/i);
  if (match) {
    return {
      prefix: match[1].trim(), // "Новогодний вечер с"
      main: match[2].trim()    // "SAFARMUHAMMAD"
    };
  }
  // Если не найдено, возвращаем весь title как main
  return {
    prefix: '',
    main: fullTitle
  };
});

const titlePrefix = computed(() => titleParts.value.prefix);
const titleMain = computed(() => titleParts.value.main);
const posterImage = computed(() => props.poster || safarImage);

// Parallax effect
const parallaxImage = ref<HTMLImageElement | null>(null);
let animationFrameId: number | null = null;

const handleScroll = () => {
  if (!parallaxImage.value) return;
  
  // Отменяем предыдущий кадр анимации для оптимизации
  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId);
  }
  
  animationFrameId = requestAnimationFrame(() => {
    if (!parallaxImage.value) return;
    
    const scrolled = window.pageYOffset;
    const heroSection = parallaxImage.value.closest('.hero-section');
    
    if (!heroSection) return;
    
    const heroRect = heroSection.getBoundingClientRect();
    const heroHeight = heroRect.height;
    const heroTop = heroRect.top;
    
    // Параллакс работает только когда секция видна
    if (heroTop + heroHeight > 0 && heroTop < window.innerHeight) {
      // Вычисляем прогресс скролла относительно секции
      const scrollProgress = Math.max(0, Math.min(1, -heroTop / heroHeight));
      
      // Параллакс эффект - изображение движется медленнее
      const parallaxOffset = scrolled * 0.25; // Скорость параллакса
      const scale = 1.05 + scrollProgress * 0.05; // Легкое увеличение при скролле
      
      parallaxImage.value.style.transform = `translateY(${parallaxOffset}px) scale(${scale})`;
    }
  });
};

onMounted(() => {
  window.addEventListener('scroll', handleScroll, { passive: true });
  window.addEventListener('resize', handleScroll, { passive: true });
  handleScroll(); // Инициализация при монтировании
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
  window.removeEventListener('resize', handleScroll);
  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId);
  }
});

// Убираем "Новогодний вечер с SAFARMUHAMMAD" и "Сбор гостей..." из описания
const filteredDescription = computed(() => {
  let desc = props.description;
  // Убираем строку с "Новогодний вечер с SAFARMUHAMMAD" (в любом регистре)
  desc = desc.replace(/^.*?новогодний вечер с\s+safarmuhammad.*?\n?/i, '');
  // Убираем "Сбор гостей в 18:00, начало в 19:00" (в любом регистре)
  desc = desc.replace(/сбор гостей в\s+\d{2}:\d{2}.*?начало в\s+\d{2}:\d{2}.*?\.?/gi, '');
  // Убираем пустые строки в начале
  desc = desc.replace(/^\s*\n+/, '');
  return desc.trim();
});

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

onMounted(() => {
  discountTimerId = window.setInterval(updateDiscountTime, 1000);
});

onUnmounted(() => {
  if (discountTimerId !== null) {
    clearInterval(discountTimerId);
  }
});

defineEmits(['cta']);
</script>

<style scoped>
@font-face {
  font-family: 'Honkenia';
  src: url('@/assets/font/honkenia/honkenia.otf') format('opentype'),
       url('@/assets/font/honkenia/honkenia.ttf') format('truetype');
  font-weight: normal;
  font-style: normal;
  font-display: swap;
}

.hero-section {
  background: #18723F;
  margin-top: -4rem;
  padding-top: 6rem;
  overflow: visible;
}

.buttons-container {
  position: relative;
  z-index: 10;
}

.buy-button {
  position: relative;
  z-index: 10;
}

.hero-title {
  font-size: 2.5rem;
  line-height: 1.2;
  font-weight: 800;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.hero-subtitle {
  font-size: 1.35rem;
  line-height: 1.5;
  opacity: 0.95;
}

.hero-utp {
  display: flex;
  align-items: center;
}

.utp-badge {
  display: inline-block;
  padding: 0.75rem 1.5rem;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border-radius: 50px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  font-weight: 600;
  font-size: 1.1rem;
  letter-spacing: 0.3px;
}

.hero-facts {
  display: flex;
  flex-wrap: wrap;
  gap: 1.5rem;
  margin-top: 1.5rem;
}

.fact-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1.1rem;
  font-weight: 500;
}

.fact-item i {
  font-size: 1.3rem;
  opacity: 0.9;
}

.hero-price {
  display: flex;
  align-items: baseline;
  gap: 1rem;
  margin-top: 1rem;
}

.price-label {
  font-size: 1.2rem;
  opacity: 0.9;
}

.price-value {
  font-size: 2.5rem;
  font-weight: 800;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.buy-button-hero {
  font-size: 1.2rem;
  font-weight: 700;
  padding: 1rem 2.5rem;
  border-radius: 50px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
}

.buy-button-hero:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.4);
}

.glass-card {
  background: transparent;
  border-radius: 0;
  padding: 0;
  backdrop-filter: none;
  border: none;
  box-shadow: none;
}

.glass-card img {
  max-width: 90%;
  height: auto;
  border-radius: 0;
  box-shadow: none;
}

/* Parallax Effect Styles */
.parallax-container {
  position: relative;
  overflow: visible;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.parallax-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.parallax-image {
  will-change: transform;
  filter: drop-shadow(0 25px 50px rgba(0, 0, 0, 0.4));
  transition: filter 0.3s ease;
  animation: floatAnimation 8s ease-in-out infinite;
  transform-origin: center center;
}

@keyframes floatAnimation {
  0%, 100% {
    transform: translateY(0) scale(1.05) rotate(0deg);
  }
  25% {
    transform: translateY(-8px) scale(1.06) rotate(0.5deg);
  }
  50% {
    transform: translateY(-12px) scale(1.07) rotate(0deg);
  }
  75% {
    transform: translateY(-8px) scale(1.06) rotate(-0.5deg);
  }
}

.parallax-image:hover {
  filter: drop-shadow(0 30px 60px rgba(0, 0, 0, 0.5));
  animation-play-state: paused;
}

.discount-badge-hero {
  border-radius: 999px;
  background: #18723F;
  color: #fff;
  font-size: 1rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3), 0 2px 4px rgba(0, 0, 0, 0.2);
  border: 2px solid #ffffff;
  text-transform: uppercase;
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: left;
  margin: 0;
  cursor: pointer;
  outline: none;
}

.discount-badge-hero .fw-semibold {
  font-size: 1.05rem;
  color: #fff;
}

.discount-badge-hero .small {
  font-size: 0.95rem;
  color: #fff;
}

.discount-badge-hero strong {
  color: #fff;
  font-weight: 700;
}

@media (max-width: 576px) {
  .discount-badge-hero {
    font-size: 0.9rem;
    padding: 0.5rem 1rem;
    margin: 0 auto;
    text-align: center;
  }
  
  .discount-badge-hero .fw-semibold {
    font-size: 0.95rem;
  }
  
  .discount-badge-hero .small {
    font-size: 0.85rem;
  }
}

/* Мобильные стили для планшетов и меньше */
@media (max-width: 991px) {
  section.hero-section.hero-section {
    margin-top: -2rem !important;
    padding-top: 2rem !important;
    padding-bottom: 3rem !important;
  }

  .hero-title {
    font-size: 2rem;
  }

  .hero-subtitle {
    font-size: 1.15rem;
  }

  section.hero-section .glass-card.glass-card img {
    max-width: 100% !important;
  }
  
  .parallax-image {
    filter: drop-shadow(0 10px 20px rgba(0, 0, 0, 0.2));
  }

  .hero-facts {
    gap: 1rem;
  }

  .fact-item {
    font-size: 1rem;
  }

  .price-value {
    font-size: 2rem;
  }
}

/* Мобильные стили для планшетов */
@media (max-width: 768px) {
  section.hero-section.hero-section {
    margin-top: -2rem !important;
    padding-top: 2rem !important;
    padding-bottom: 3rem !important;
    overflow: visible !important;
  }

  .hero-title {
    font-size: 1.75rem !important;
  }

  .hero-subtitle {
    font-size: 1.05rem !important;
  }

  .utp-badge {
    font-size: 0.95rem;
    padding: 0.6rem 1.2rem;
  }

  section.hero-section .glass-card.glass-card img {
    max-width: 100% !important;
  }

  .buy-button-hero {
    font-size: 1rem !important;
    padding: 0.875rem 2rem !important;
    width: 100%;
  }

  section.hero-section .buttons-container {
    display: flex !important;
    visibility: visible !important;
    opacity: 1 !important;
    margin-top: 1rem !important;
    margin-bottom: 1rem !important;
    width: 100%;
  }

  .hero-facts {
    gap: 0.75rem;
  }

  .fact-item {
    font-size: 0.9rem;
  }

  .fact-item i {
    font-size: 1.1rem;
  }

  .price-value {
    font-size: 1.75rem !important;
  }
}

/* Мобильные стили для телефонов */
@media (max-width: 576px) {
  section.hero-section.hero-section {
    margin-top: -1rem !important;
    padding-top: 1.5rem !important;
    padding-bottom: 2rem !important;
    overflow: visible !important;
  }

  .hero-title {
    font-size: 1.5rem !important;
    line-height: 1.3 !important;
  }

  .hero-subtitle {
    font-size: 1rem !important;
    line-height: 1.4 !important;
  }

  .utp-badge {
    font-size: 0.85rem !important;
    padding: 0.5rem 1rem !important;
  }

  .buy-button-hero {
    font-size: 0.95rem !important;
    padding: 0.875rem 1.75rem !important;
    width: 100% !important;
    margin-bottom: 0.5rem !important;
    display: block !important;
    visibility: visible !important;
    opacity: 1 !important;
    z-index: 100 !important;
    position: relative !important;
  }

  section.hero-section .buttons-container {
    display: flex !important;
    flex-direction: column !important;
    visibility: visible !important;
    opacity: 1 !important;
    margin-top: 1rem !important;
    margin-bottom: 1rem !important;
    width: 100% !important;
    gap: 0.5rem !important;
  }

  .hero-facts {
    flex-direction: column;
    gap: 0.75rem;
  }

  .fact-item {
    font-size: 0.9rem !important;
  }

  .fact-item i {
    font-size: 1rem !important;
  }

  .price-value {
    font-size: 1.75rem !important;
  }

  .price-label {
    font-size: 1rem !important;
  }

  section.hero-section .glass-card.glass-card img {
    max-width: 100% !important;
  }
}

/* Мобильные стили для очень маленьких экранов */
@media (max-width: 480px) {
  section.hero-section.hero-section {
    margin-top: -0.5rem !important;
    padding-top: 1rem !important;
    padding-bottom: 1.5rem !important;
    overflow: visible !important;
  }

  .hero-title {
    font-size: 1.35rem !important;
  }

  .hero-subtitle {
    font-size: 0.95rem !important;
  }

  .utp-badge {
    font-size: 0.8rem !important;
    padding: 0.45rem 0.9rem !important;
  }

  section.hero-section .buttons-container {
    display: flex !important;
    flex-direction: column !important;
    visibility: visible !important;
    opacity: 1 !important;
    margin-top: 1rem !important;
    margin-bottom: 1rem !important;
  }

  .buy-button-hero {
    display: block !important;
    visibility: visible !important;
    opacity: 1 !important;
    width: 100% !important;
    font-size: 0.9rem !important;
    padding: 0.75rem 1.5rem !important;
  }

  .price-value {
    font-size: 1.5rem !important;
  }
}
</style>

