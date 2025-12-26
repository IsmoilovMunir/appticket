<template>
  <section class="pb-5 position-relative overflow-hidden text-white hero-section">
    <div class="container">
      <div class="hero-content">
        <div class="hero-poster-wrapper">
          <div class="hero-poster-image">
            <img :src="postImage" alt="Poster" class="hero-poster-img" />
          </div>
          <div class="hero-info-overlay">
            <div class="hero-info">
              <h1 class="display-3 fw-bold mb-3 hero-title">
                Новогодний банкет-вечер с Сафармухаммадом в Москве
              </h1>
              <p class="fs-4 text-light mb-4 hero-subtitle">
                3 часа живой родной музыки, сытный банкет и атмосфера, ради которой приходят со своими
              </p>
              <div class="hero-utp mb-4">
                <span class="utp-badge">Еда, напитки, шоу и концерт — всё включено</span>
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
                <a href="https://t.me/surnek_events" class="btn btn-outline-light btn-lg px-4 btn-details">
                  Подробнее
                </a>
              </div>
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
import postImage from '@/assets/post.png';

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
  background: linear-gradient(135deg, #18723F 0%, #1f9d6c 100%);
  margin-top: -4rem;
  padding-top: 6rem;
  padding-bottom: 4rem;
  overflow: visible;
  position: relative;
  min-height: auto;
  display: flex;
  align-items: center;
}

.hero-section .container {
  position: relative;
  z-index: 2;
  height: 100%;
}

.hero-content {
  display: flex;
  align-items: center;
  justify-content: center;
  padding-top: 2rem;
  padding-bottom: 2rem;
}

.hero-poster-wrapper {
  position: relative;
  width: fit-content;
  max-width: 100%;
  margin: 0 auto;
}

.hero-poster-image {
  width: auto;
  max-width: 100%;
  height: auto;
  position: relative;
  overflow: hidden;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
  display: inline-block;
}

.hero-poster-img {
  width: auto;
  max-width: 100%;
  height: auto;
  display: block;
  object-fit: contain;
  object-position: center;
}

.hero-info-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 65%;
  bottom: 0;
  display: flex;
  align-items: flex-start;
  justify-content: flex-start;
  padding: 2rem;
  z-index: 2;
  background: linear-gradient(
    to right,
    rgba(24, 114, 63, 0.85) 0%,
    rgba(24, 114, 63, 0.7) 60%,
    transparent 100%
  );
  border-radius: 16px 0 0 16px;
  pointer-events: none;
}

.hero-info {
  max-width: 100%;
  width: 100%;
  position: relative;
  z-index: 3;
  pointer-events: auto;
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
  font-size: 2.75rem;
  line-height: 1.2;
  font-weight: 800;
  color: #ffffff;
  text-shadow: 0 3px 15px rgba(0, 0, 0, 0.5), 0 1px 3px rgba(0, 0, 0, 0.3);
  margin-bottom: 1rem;
  word-wrap: break-word;
  word-break: break-word;
  hyphens: auto;
}

.hero-subtitle {
  font-size: 1.4rem;
  line-height: 1.6;
  color: #ffffff;
  opacity: 1;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.4);
  font-weight: 500;
}

.hero-utp {
  display: flex;
  align-items: center;
}

.utp-badge {
  display: inline-block;
  padding: 1rem 2rem;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(12px);
  border-radius: 50px;
  border: 2px solid rgba(255, 255, 255, 1);
  font-weight: 700;
  font-size: 1.2rem;
  letter-spacing: 0.3px;
  color: #18723F;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.3), 0 2px 8px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.utp-badge:hover {
  background: rgba(255, 255, 255, 1);
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.4), 0 4px 12px rgba(0, 0, 0, 0.3);
}

.hero-facts {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
  margin-top: 1.5rem;
  margin-bottom: 1.5rem;
}

.fact-item {
  display: flex;
  align-items: center;
  gap: 0.875rem;
  font-size: 1.1rem;
  font-weight: 600;
  padding: 1rem 1.25rem;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(12px);
  border-radius: 14px;
  border: 2px solid rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
  color: #18723F;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.25);
}

.fact-item:hover {
  background: rgba(255, 255, 255, 1);
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.35);
  border-color: rgba(255, 255, 255, 1);
}

.fact-item span {
  color: #18723F;
  font-weight: 600;
}

.fact-item i {
  font-size: 1.5rem;
  opacity: 1;
  color: #18723F;
  flex-shrink: 0;
}

.hero-price {
  display: inline-flex;
  align-items: baseline;
  gap: 1.25rem;
  margin-top: 1.5rem;
  margin-bottom: 1.5rem;
  padding: 1.25rem 2rem;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(12px);
  border-radius: 18px;
  border: 3px solid rgba(255, 255, 255, 1);
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.3), 0 2px 8px rgba(0, 0, 0, 0.2);
}

.price-label {
  font-size: 1.25rem;
  opacity: 1;
  font-weight: 600;
  color: #18723F;
}

.price-value {
  font-size: 3rem;
  font-weight: 900;
  text-shadow: none;
  color: #18723F;
  letter-spacing: -0.5px;
}

.buy-button-hero {
  font-size: 1.3rem;
  font-weight: 800;
  padding: 1.125rem 2.75rem;
  border-radius: 50px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  background: linear-gradient(135deg, #18723F 0%, #1f9d6c 100%);
  color: #ffffff;
  border: 3px solid #18723F;
  box-shadow: 0 8px 28px rgba(24, 114, 63, 0.4), 0 4px 12px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
}

.buy-button-hero:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 36px rgba(24, 114, 63, 0.5), 0 6px 16px rgba(0, 0, 0, 0.4);
  background: linear-gradient(135deg, #145a32 0%, #18723F 100%);
  color: #ffffff;
  border-color: #145a32;
}

.btn-outline-light.btn-details {
  border: 3px solid rgba(255, 255, 255, 0.9);
  color: #ffffff;
  font-weight: 700;
  font-size: 1.1rem;
  padding: 1rem 2rem;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50px;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.btn-outline-light.btn-details:hover {
  background: rgba(255, 255, 255, 0.95);
  color: #18723F;
  border-color: #ffffff;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.3);
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
    min-height: 80vh;
  }

  .hero-poster-wrapper {
    width: 100%;
  }
  
  .hero-info-overlay {
    width: 75%;
    padding: 1.5rem;
    background: linear-gradient(
      to right,
      rgba(24, 114, 63, 0.9) 0%,
      rgba(24, 114, 63, 0.8) 60%,
      rgba(24, 114, 63, 0.6) 100%
    );
  }

  .hero-content {
    padding-top: 1rem;
  }

  .hero-info {
    max-width: 100%;
  }

  .hero-title {
    font-size: 2rem;
  }

  .hero-subtitle {
    font-size: 1.15rem;
  }

  .hero-facts {
    grid-template-columns: 1fr;
    gap: 0.875rem;
  }

  .fact-item {
    font-size: 0.95rem;
    padding: 0.625rem 0.875rem;
  }

  .price-value {
    font-size: 2rem;
  }
}

/* Мобильные стили для планшетов */
@media (max-width: 768px) {
  section.hero-section.hero-section {
    margin-top: -2rem !important;
    padding-top: 1.5rem !important;
    padding-bottom: 2rem !important;
    overflow: visible !important;
    min-height: auto;
  }

  .hero-poster-wrapper {
    display: flex;
    flex-direction: column;
    width: 100%;
  }

  .hero-poster-image {
    width: 100%;
    max-width: 100%;
    border-radius: 16px 16px 0 0;
  }

  .hero-info-overlay {
    position: relative;
    width: 100%;
    background: linear-gradient(
      to bottom,
      #ffffff 0%,
      #ffffff 45%,
      rgba(255, 255, 255, 0.8) 50%,
      rgba(255, 255, 255, 0.4) 60%,
      transparent 100%
    );
    border-radius: 0 0 16px 16px;
    padding: 1.5rem;
    margin-top: 0;
  }

  .hero-facts {
    position: relative;
    z-index: 2;
  }

  .hero-price {
    position: relative;
    z-index: 2;
  }

  .buttons-container {
    background: transparent;
    padding-top: 0.5rem;
  }

  .hero-info {
    color: #1d1f23;
  }

  .hero-title {
    font-size: 1.75rem !important;
    color: #1d1f23 !important;
    text-shadow: none !important;
  }

  .hero-subtitle {
    font-size: 1.05rem !important;
    color: #4a5568 !important;
  }

  .utp-badge {
    font-size: 0.95rem;
    padding: 0.6rem 1.2rem;
    background: rgba(24, 114, 63, 0.1);
    color: #18723F;
    border-color: rgba(24, 114, 63, 0.3);
  }

  .fact-item {
    background: #f8fbff;
    color: #1d1f23;
  }

  .fact-item span {
    color: #1d1f23;
  }

  .fact-item i {
    color: #18723F;
  }

  .hero-price {
    background: rgba(255, 255, 255, 1) !important;
    border: 2px solid rgba(24, 114, 63, 0.2);
  }

  .price-label {
    color: #18723F !important;
  }

  .price-value {
    color: #18723F !important;
  }

  .buy-button-hero {
    font-size: 1rem !important;
    padding: 0.875rem 2rem !important;
    width: 100%;
    background: linear-gradient(135deg, #18723F 0%, #1f9d6c 100%) !important;
    color: #ffffff !important;
    border-color: #18723F !important;
  }

  .btn-details {
    display: inline-block !important;
    background: rgba(255, 255, 255, 0.1) !important;
    border: 3px solid rgba(255, 255, 255, 0.9) !important;
    color: #ffffff !important;
    border-radius: 50px !important;
    backdrop-filter: blur(10px);
  }

  .btn-details:hover {
    background: rgba(255, 255, 255, 0.95) !important;
    color: #18723F !important;
    border-color: #ffffff !important;
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
    grid-template-columns: 1fr;
    gap: 0.75rem;
  }

  .fact-item {
    font-size: 0.9rem;
    padding: 0.625rem 0.875rem;
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
    padding-top: 1rem !important;
    padding-bottom: 1.5rem !important;
    overflow: visible !important;
    min-height: auto;
  }

  .hero-poster-wrapper {
    width: 100%;
    display: flex;
    flex-direction: column;
  }
  
  .hero-info-overlay {
    position: relative;
    width: 100%;
    padding: 1.25rem;
    background: linear-gradient(
      to bottom,
      #ffffff 0%,
      #ffffff 45%,
      rgba(255, 255, 255, 0.8) 50%,
      rgba(255, 255, 255, 0.4) 60%,
      transparent 100%
    );
    border-radius: 0 0 16px 16px;
    margin-top: 0;
  }

  .hero-facts {
    position: relative;
    z-index: 2;
  }

  .hero-price {
    position: relative;
    z-index: 2;
  }

  .buttons-container {
    background: transparent;
    padding-top: 0.5rem;
  }
  
  .hero-info {
    max-width: 100%;
    color: #1d1f23;
  }

  .hero-content {
    padding-top: 0;
  }

  .hero-title {
    color: #1d1f23 !important;
    text-shadow: none !important;
  }

  .hero-subtitle {
    color: #4a5568 !important;
  }

  .btn-details {
    display: inline-block !important;
    background: rgba(255, 255, 255, 0.1) !important;
    border: 3px solid rgba(255, 255, 255, 0.9) !important;
    color: #ffffff !important;
    border-radius: 50px !important;
    backdrop-filter: blur(10px);
  }

  .btn-details:hover {
    background: rgba(255, 255, 255, 0.95) !important;
    color: #18723F !important;
    border-color: #ffffff !important;
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
    background: linear-gradient(135deg, #18723F 0%, #1f9d6c 100%) !important;
    color: #ffffff !important;
    border-color: #18723F !important;
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
    grid-template-columns: 1fr;
    gap: 0.75rem;
  }

  .fact-item {
    font-size: 0.9rem !important;
    padding: 0.625rem 0.875rem;
  }

  .fact-item i {
    font-size: 1rem !important;
  }

  .hero-price {
    background: rgba(255, 255, 255, 1) !important;
    border: 2px solid rgba(24, 114, 63, 0.2);
  }

  .price-label {
    color: #18723F !important;
    font-size: 1rem !important;
  }

  .price-value {
    color: #18723F !important;
    font-size: 1.75rem !important;
  }

}

/* Мобильные стили для очень маленьких экранов */
@media (max-width: 480px) {
  section.hero-section.hero-section {
    margin-top: -0.5rem !important;
    padding-top: 0.75rem !important;
    padding-bottom: 1rem !important;
    overflow: visible !important;
    min-height: 55vh;
  }

  .hero-poster-wrapper {
    width: 100%;
    display: flex;
    flex-direction: column;
  }
  
  .hero-info-overlay {
    position: relative;
    width: 100%;
    padding: 1rem;
    background: linear-gradient(
      to bottom,
      #ffffff 0%,
      #ffffff 45%,
      rgba(255, 255, 255, 0.8) 50%,
      rgba(255, 255, 255, 0.4) 60%,
      transparent 100%
    );
    border-radius: 0 0 16px 16px;
    margin-top: 0;
  }

  .hero-facts {
    position: relative;
    z-index: 2;
  }

  .hero-price {
    position: relative;
    z-index: 2;
  }

  .buttons-container {
    background: transparent;
    padding-top: 0.5rem;
  }
  
  .hero-info {
    max-width: 100%;
    color: #1d1f23;
  }

  .hero-title {
    color: #1d1f23 !important;
    text-shadow: none !important;
  }

  .hero-subtitle {
    color: #4a5568 !important;
  }

  .btn-details {
    display: inline-block !important;
    background: rgba(255, 255, 255, 0.1) !important;
    border: 3px solid rgba(255, 255, 255, 0.9) !important;
    color: #ffffff !important;
    border-radius: 50px !important;
    backdrop-filter: blur(10px);
  }

  .btn-details:hover {
    background: rgba(255, 255, 255, 0.95) !important;
    color: #18723F !important;
    border-color: #ffffff !important;
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
    background: linear-gradient(135deg, #18723F 0%, #1f9d6c 100%) !important;
    color: #ffffff !important;
    border-color: #18723F !important;
  }

  .hero-price {
    background: rgba(255, 255, 255, 1) !important;
    border: 2px solid rgba(24, 114, 63, 0.2);
  }

  .price-label {
    color: #18723F !important;
  }

  .price-value {
    color: #18723F !important;
    font-size: 1.5rem !important;
  }
}
</style>

