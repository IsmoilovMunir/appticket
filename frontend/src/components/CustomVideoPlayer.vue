<template>
  <div class="custom-video-player-wrapper" ref="playerWrapperRef" v-if="videoSrc">
    <div class="video-container">
      <!-- Если есть прямая ссылка на видео, используем HTML5 video -->
      <video
        v-if="isDirectVideo"
        ref="videoRef"
        :src="videoSrc"
        class="custom-video"
        @loadedmetadata="onVideoLoaded"
        @timeupdate="onTimeUpdate"
        @play="onPlay"
        @pause="onPause"
        @volumechange="onVolumeChange"
        @error="onVideoError"
      ></video>
      
      <!-- Если это VK iframe, используем iframe с кастомными элементами управления -->
      <iframe
        v-else-if="videoSrc"
        ref="iframeRef"
        :src="videoSrc"
        :key="`vk-iframe-${videoSrc}`"
        class="vk-video-iframe"
        frameborder="0"
        allow="autoplay; encrypted-media; fullscreen"
        allowfullscreen
        loading="lazy"
        @load="onIframeLoad"
        @error="onIframeError"
        referrerpolicy="no-referrer-when-downgrade"
      ></iframe>
      
      <!-- Overlay для скрытия элементов VK (только для iframe) -->
      <div v-if="!isDirectVideo && videoSrc" class="vk-overlay">
        <div class="vk-overlay-top"></div>
        <div class="vk-overlay-bottom"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';

interface Props {
  videoSrc: string;
  width?: string;
  height?: string;
}

const props = withDefaults(defineProps<Props>(), {
  width: '1080',
  height: '1920'
});

const videoRef = ref<HTMLVideoElement | null>(null);
const iframeRef = ref<HTMLIFrameElement | null>(null);
const playerWrapperRef = ref<HTMLElement | null>(null);
const isPlaying = ref(false);
const isMuted = ref(false);
const volume = ref(1);
const currentTime = ref(0);
const duration = ref(0);
const showControls = ref(true);
let hideControlsTimer: NodeJS.Timeout | null = null;

// Определяем, является ли видео прямой ссылкой или VK iframe
const isDirectVideo = computed(() => {
  if (!props.videoSrc) return false;
  
  // Если это VK ссылка, это не прямое видео
  if (props.videoSrc.includes('vk.com') || props.videoSrc.includes('video_ext.php')) {
    return false;
  }
  
  // Проверяем расширения файлов
  return (
    props.videoSrc.endsWith('.mp4') ||
    props.videoSrc.endsWith('.webm') ||
    props.videoSrc.endsWith('.ogg') ||
    props.videoSrc.endsWith('.mov') ||
    props.videoSrc.endsWith('.m3u8') ||
    (props.videoSrc.includes('/video/') && !props.videoSrc.includes('vk.com'))
  );
});

const volumeIcon = computed(() => {
  if (isMuted.value || volume.value === 0) {
    return 'bi bi-volume-mute-fill';
  } else if (volume.value < 0.5) {
    return 'bi bi-volume-down-fill';
  } else {
    return 'bi bi-volume-up-fill';
  }
});

const onVideoLoaded = () => {
  if (videoRef.value) {
    duration.value = videoRef.value.duration;
    videoRef.value.volume = volume.value;
  }
};

const onTimeUpdate = () => {
  if (videoRef.value) {
    currentTime.value = videoRef.value.currentTime;
  }
};

const onPlay = () => {
  isPlaying.value = true;
  hideControlsTimeout();
};

const onPause = () => {
  isPlaying.value = false;
  showControls.value = true;
};

const onVolumeChange = (event: Event) => {
  const target = event.target as HTMLInputElement;
  const newVolume = parseFloat(target.value);
  volume.value = newVolume;
  
  if (videoRef.value) {
    videoRef.value.volume = newVolume;
    isMuted.value = newVolume === 0;
  }
};

const togglePlayPause = () => {
  if (isDirectVideo.value && videoRef.value) {
    // Управление прямым видео
    if (videoRef.value.paused) {
      videoRef.value.play();
    } else {
      videoRef.value.pause();
    }
    showControlsTemporarily();
  } else if (iframeRef.value) {
    // Для VK iframe кликаем на iframe для play/pause
    const iframe = iframeRef.value;
    const rect = iframe.getBoundingClientRect();
    const centerX = rect.left + rect.width / 2;
    const centerY = rect.top + rect.height / 2;
    
    // Создаем клик в центре iframe
    const clickEvent = new MouseEvent('click', {
      view: window,
      bubbles: true,
      cancelable: true,
      clientX: centerX,
      clientY: centerY
    });
    
    iframe.contentWindow?.document.dispatchEvent(clickEvent);
    
    // Альтернативный способ - клик на iframe напрямую
    iframe.click();
    
    // Переключаем состояние
    isPlaying.value = !isPlaying.value;
    showControlsTemporarily();
  }
};

const toggleMute = () => {
  if (!videoRef.value || !isDirectVideo.value) return;
  
  if (isMuted.value) {
    videoRef.value.volume = volume.value || 0.5;
    isMuted.value = false;
  } else {
    videoRef.value.volume = 0;
    isMuted.value = true;
  }
  
  showControlsTemporarily();
};

const showControlsTemporarily = () => {
  showControls.value = true;
  hideControlsTimeout();
};

const hideControlsTimeout = () => {
  if (hideControlsTimer) {
    clearTimeout(hideControlsTimer);
  }
  
  if (isPlaying.value && isDirectVideo.value) {
    hideControlsTimer = setTimeout(() => {
      showControls.value = false;
    }, 3000);
  }
};

const onVideoError = (event: Event) => {
  console.error('Video loading error:', event);
  // Можно добавить fallback или сообщение пользователю
};

const onIframeError = (event: Event) => {
  console.error('Iframe loading error:', event);
  // Ошибки загрузки iframe VK - это нормально, так как VK блокирует некоторые запросы
  // Особенно ошибки 400 от CDN vkvd596.okcdn.ru - это внутренние проблемы VK
};

const onIframeLoad = () => {
  // Попытка скрыть элементы VK через CSS
  if (iframeRef.value) {
    try {
      const iframeDoc = iframeRef.value.contentDocument || iframeRef.value.contentWindow?.document;
      if (iframeDoc) {
        const style = iframeDoc.createElement('style');
        style.textContent = `
          /* Скрываем все элементы управления VK */
          .videoplayer_controls,
          .videoplayer_top_panel,
          .videoplayer_bottom_panel,
          .videoplayer_share,
          .videoplayer_fullscreen,
          .videoplayer_quality,
          .videoplayer_settings,
          .videoplayer_subtitle,
          .videoplayer_pip,
          .videoplayer_volume,
          .videoplayer_time,
          .videoplayer_progress,
          a[href*="vk.com"],
          a[href*="telegram"],
          .videoplayer_header,
          .videoplayer_footer,
          .videoplayer_top,
          .videoplayer_bottom,
          [class*="watch"],
          [class*="Watch"],
          [class*="clip"],
          [class*="Clip"],
          [class*="vk"],
          [class*="VK"],
          [id*="watch"],
          [id*="Watch"],
          [id*="clip"],
          [id*="Clip"],
          [id*="vk"],
          [id*="VK"],
          a[title*="VK"],
          a[title*="vk"],
          a[title*="watch"],
          a[title*="Watch"],
          div[class*="logo"],
          div[class*="Logo"],
          div[class*="brand"],
          div[class*="Brand"],
          .videoplayer_top_left,
          .videoplayer_top_right,
          .videoplayer_branding,
          .videoplayer_watermark {
            display: none !important;
            visibility: hidden !important;
            opacity: 0 !important;
            pointer-events: none !important;
          }
        `;
        iframeDoc.head.appendChild(style);
      }
    } catch (e) {
      // CORS ограничения - это нормально для VK iframe
    }
  }
};

onMounted(() => {
  if (videoRef.value && isDirectVideo.value) {
    videoRef.value.volume = volume.value;
  }
  
  // Скрываем стандартные элементы управления браузера для video
  const style = document.createElement('style');
  style.textContent = `
    video::-webkit-media-controls {
      display: none !important;
    }
    video::-webkit-media-controls-enclosure {
      display: none !important;
    }
    video::-webkit-media-controls-panel {
      display: none !important;
    }
  `;
  document.head.appendChild(style);
});

onUnmounted(() => {
  if (hideControlsTimer) {
    clearTimeout(hideControlsTimer);
  }
});
</script>

<style scoped>
.custom-video-player-wrapper {
  position: relative;
  display: inline-block;
  overflow: hidden;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  background-color: #000;
  width: 100%;
  max-width: 540px;
  margin: 0 auto;
}

.video-container {
  position: relative;
  width: 100%;
  padding-bottom: 177.78%; /* 9:16 для вертикального видео */
  overflow: hidden;
  background-color: #000;
}

.custom-video {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: contain;
  cursor: pointer;
  -webkit-appearance: none;
}

.custom-video::-webkit-media-controls {
  display: none !important;
}

.custom-video::-webkit-media-controls-enclosure {
  display: none !important;
}

.custom-video::-webkit-media-controls-panel {
  display: none !important;
}

.vk-video-iframe {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border: none;
  pointer-events: auto;
  z-index: 1;
}

/* Overlay для скрытия элементов VK */
.vk-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 2;
}

.vk-overlay-top {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 20%;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.9) 0%, rgba(0, 0, 0, 0.6) 50%, rgba(0, 0, 0, 0.3) 80%, transparent 100%);
  border-radius: 12px 12px 0 0;
  pointer-events: auto;
  cursor: not-allowed;
}

.vk-overlay-top::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 35%;
  height: 100%;
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.95) 0%, rgba(0, 0, 0, 0.7) 50%, transparent 100%);
  border-radius: 12px 0 0 0;
}

.vk-overlay-top::after {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 35%;
  height: 100%;
  background: linear-gradient(225deg, rgba(0, 0, 0, 0.95) 0%, rgba(0, 0, 0, 0.7) 50%, transparent 100%);
  border-radius: 0 12px 0 0;
}

.vk-overlay-bottom {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 15%;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.5) 0%, transparent 100%);
  border-radius: 0 0 12px 12px;
  pointer-events: auto;
  cursor: not-allowed;
}

/* Кастомные элементы управления */
.video-controls {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: opacity 0.3s ease;
}

.video-controls.controls-visible {
  opacity: 1;
}

.video-controls:not(.controls-visible) {
  opacity: 0;
}

/* Центральная кнопка Play/Pause */
.play-pause-btn {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.7);
  border: 3px solid rgba(255, 255, 255, 0.9);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  pointer-events: auto;
  transition: all 0.3s ease;
  z-index: 11;
}

.play-pause-btn:hover {
  background: rgba(0, 0, 0, 0.9);
  transform: translate(-50%, -50%) scale(1.1);
  border-color: #fff;
}

.play-pause-btn i {
  font-size: 2.5rem;
  margin-left: 4px; /* Смещение для иконки play */
}

.play-pause-btn:not(:hover) {
  opacity: 0.8;
}

/* Нижняя панель управления */
.controls-bottom {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 1rem;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.8) 0%, transparent 100%);
  pointer-events: auto;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 1rem;
}

/* Контроль громкости */
.volume-control {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  background: rgba(0, 0, 0, 0.6);
  padding: 0.5rem 1rem;
  border-radius: 25px;
  backdrop-filter: blur(10px);
}

.volume-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: transparent;
  border: 2px solid rgba(255, 255, 255, 0.7);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.volume-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: #fff;
}

.volume-btn i {
  font-size: 1.25rem;
}

.volume-slider-wrapper {
  width: 100px;
  height: 4px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 2px;
  position: relative;
  cursor: pointer;
}

.volume-slider {
  width: 100%;
  height: 100%;
  -webkit-appearance: none;
  appearance: none;
  background: transparent;
  cursor: pointer;
  outline: none;
}

.volume-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: #fff;
  cursor: pointer;
  transition: all 0.2s ease;
}

.volume-slider::-webkit-slider-thumb:hover {
  transform: scale(1.2);
  box-shadow: 0 0 8px rgba(255, 255, 255, 0.8);
}

.volume-slider::-moz-range-thumb {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: #fff;
  cursor: pointer;
  border: none;
  transition: all 0.2s ease;
}

.volume-slider::-moz-range-thumb:hover {
  transform: scale(1.2);
  box-shadow: 0 0 8px rgba(255, 255, 255, 0.8);
}

.control-button {
  border: none;
  outline: none;
  padding: 0;
  margin: 0;
}

/* Адаптивность */
@media (max-width: 1200px) {
  .custom-video-player-wrapper {
    max-width: 450px;
  }
}

@media (max-width: 768px) {
  .custom-video-player-wrapper {
    max-width: 100%;
  }
  
  .play-pause-btn {
    width: 70px;
    height: 70px;
  }
  
  .play-pause-btn i {
    font-size: 2rem;
  }
  
  .volume-control {
    padding: 0.4rem 0.8rem;
  }
  
  .volume-btn {
    width: 36px;
    height: 36px;
  }
  
  .volume-slider-wrapper {
    width: 80px;
  }
}

@media (max-width: 576px) {
  .custom-video-player-wrapper {
    border-radius: 8px;
  }
  
  .play-pause-btn {
    width: 60px;
    height: 60px;
  }
  
  .play-pause-btn i {
    font-size: 1.75rem;
  }
  
  .controls-bottom {
    padding: 0.75rem;
  }
  
  .volume-slider-wrapper {
    width: 60px;
  }
}
</style>
