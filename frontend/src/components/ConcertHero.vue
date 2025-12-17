<template>
  <section class="pt-0 pb-5 bg-gradient position-relative overflow-hidden text-white hero-section">
    <div class="container">
      <div class="row align-items-center g-4">
        <div class="col-lg-7">
          <p class="text-uppercase small text-light mb-0">Эксклюзивное событие</p>
          <h1 class="display-4 fw-bold mb-1">
            <span class="title-prefix">{{ titlePrefix }}</span>
            <span class="title-main">{{ titleMain }}</span>
          </h1>
          <p class="fs-5 text-light mb-1">
            {{ filteredDescription }}
          </p>
          <div class="d-flex gap-4 flex-wrap my-0 text-light">
            <div>
              <div class="text-uppercase small text-opacity-75">Дата</div>
              <div class="h4 mb-0">{{ date }}</div>
            </div>
            <div>
              <div class="text-uppercase small text-opacity-75">Локация</div>
              <div class="h4 mb-0 mt-3">{{ venue }}</div>
            </div>
          </div>
          <div class="d-flex gap-3 flex-wrap mt-3 mb-3 buttons-container">
            <button class="btn btn-light btn-lg px-4 buy-button" @click="$emit('cta')">Купить билет</button>
            <a href="https://t.me/surnekevents" class="btn btn-outline-light btn-lg px-4">
              Подробнее
            </a>
          </div>
          <p class="mt-3 text-light mb-0">
            Выберите лучший стол, подтвердите бронь с менеджером и получите QR‑билеты.
          </p>
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

.title-prefix {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  font-weight: 700;
  font-style: normal;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.title-main {
  font-family: 'Honkenia', serif;
  font-weight: normal;
  display: block;
  margin: 0;
  margin-bottom: -0.1em;
  font-size: 3.8em;
  line-height: 1;
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

/* Мобильные стили для планшетов и меньше */
@media (max-width: 991px) {
  section.hero-section.hero-section {
    margin-top: -2rem !important;
    padding-top: 2rem !important;
    padding-bottom: 3rem !important;
  }

  section.hero-section .title-main.title-main {
    font-size: 3.5em !important;
    line-height: 1 !important;
  }

  section.hero-section .title-prefix.title-prefix {
    font-size: 0.9em !important;
  }

  section.hero-section .glass-card.glass-card img {
    max-width: 100% !important;
  }
  
  .parallax-image {
    filter: drop-shadow(0 10px 20px rgba(0, 0, 0, 0.2));
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

  section.hero-section .title-main.title-main {
    font-size: 3.5em !important;
    line-height: 1 !important;
  }

  section.hero-section .title-prefix.title-prefix {
    font-size: 0.9em !important;
  }

  section.hero-section .glass-card.glass-card img {
    max-width: 100% !important;
  }

  section.hero-section .btn.btn {
    font-size: 0.9rem !important;
    padding: 0.5rem 1.5rem !important;
    display: block !important;
    visibility: visible !important;
    opacity: 1 !important;
  }

  section.hero-section .buttons-container {
    display: flex !important;
    visibility: visible !important;
    opacity: 1 !important;
    margin-top: 1rem !important;
    margin-bottom: 1rem !important;
  }

  section.hero-section .h4.h4 {
    font-size: 1.1rem !important;
  }

  section.hero-section h1.display-4.display-4 {
    font-size: 2rem !important;
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

  section.hero-section .title-main.title-main {
    font-size: 3em !important;
    line-height: 1 !important;
    margin-bottom: -0.1em !important;
  }

  section.hero-section .title-prefix.title-prefix {
    font-size: 0.85em !important;
  }

  section.hero-section .btn.btn {
    font-size: 0.85rem !important;
    padding: 0.6rem 1.5rem !important;
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

  section.hero-section .h4.h4 {
    font-size: 1rem !important;
  }

  section.hero-section .fs-5.fs-5 {
    font-size: 0.95rem !important;
  }

  section.hero-section h1.display-4.display-4 {
    font-size: 1.5rem !important;
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

  section.hero-section .title-main.title-main {
    font-size: 5.6em !important;
  }

  section.hero-section .title-prefix.title-prefix {
    font-size: 0.8em !important;
  }

  section.hero-section .buttons-container {
    display: flex !important;
    flex-direction: column !important;
    visibility: visible !important;
    opacity: 1 !important;
    margin-top: 1rem !important;
    margin-bottom: 1rem !important;
  }

  section.hero-section .btn.btn {
    display: block !important;
    visibility: visible !important;
    opacity: 1 !important;
    width: 100% !important;
  }
}
</style>

