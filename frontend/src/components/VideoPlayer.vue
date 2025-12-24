<template>
  <div class="video-player-wrapper" ref="wrapperRef">
    <div class="video-container">
      <iframe
        :src="videoUrl"
        :width="width"
        :height="height"
        class="vk-video-iframe"
        frameborder="0"
        allow="autoplay; encrypted-media"
        :sandbox="sandboxAttributes"
        @load="onIframeLoad"
        @click="preventClick"
      ></iframe>
      <!-- Overlay для блокировки кликов на определенных областях и скрытия надписи VK -->
      <div class="video-overlay" @click="preventClick">
        <div class="video-overlay-top"></div>
        <div class="video-overlay-bottom"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';

interface Props {
  videoUrl: string;
  width?: string;
  height?: string;
}

const props = withDefaults(defineProps<Props>(), {
  width: '1080',
  height: '1920'
});

const wrapperRef = ref<HTMLElement | null>(null);

// Ограничиваем sandbox для предотвращения переходов
const sandboxAttributes = 'allow-scripts allow-same-origin allow-presentation';

const onIframeLoad = (event: Event) => {
  const iframe = event.target as HTMLIFrameElement;
  
  // Попытка скрыть элементы управления через CSS
  try {
    const iframeDoc = iframe.contentDocument || iframe.contentWindow?.document;
    if (iframeDoc) {
      // Добавляем стили для скрытия лишних элементов
      const style = iframeDoc.createElement('style');
      style.textContent = `
        /* Скрываем кнопки навигации и другие элементы управления */
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
        /* Скрываем надпись "watch clip on VK" */
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
        
        /* Оставляем только базовые элементы управления */
        .videoplayer_play_button,
        .videoplayer_pause_button {
          display: block !important;
        }
      `;
      iframeDoc.head.appendChild(style);
    }
  } catch (e) {
    // Cross-origin ограничения могут не позволить доступ к содержимому
    // Это нормально для VK видео
  }
};

// Предотвращаем клики на ссылки и переходы
const preventClick = (event: MouseEvent) => {
  // Разрешаем клики только в центральной области видео (для play/pause)
  const target = event.target as HTMLElement;
  const rect = (wrapperRef.value || target).getBoundingClientRect();
  const clickX = event.clientX - rect.left;
  const clickY = event.clientY - rect.top;
  
  // Блокируем клики в верхней и нижней частях (где обычно находятся ссылки)
  const topThreshold = rect.height * 0.1;
  const bottomThreshold = rect.height * 0.9;
  
  if (clickY < topThreshold || clickY > bottomThreshold) {
    event.preventDefault();
    event.stopPropagation();
    return false;
  }
};

// Перехватываем сообщения от iframe
const handleMessage = (event: MessageEvent) => {
  // Блокируем попытки открыть новые окна или переходы
  if (event.data && typeof event.data === 'string') {
    if (event.data.includes('vk.com') || event.data.includes('telegram')) {
      event.preventDefault();
      event.stopPropagation();
    }
  }
};

onMounted(() => {
  // Добавляем обработчик сообщений
  window.addEventListener('message', handleMessage);
  
  // Блокируем контекстное меню на iframe
  const iframe = wrapperRef.value?.querySelector('iframe');
  if (iframe) {
    iframe.addEventListener('contextmenu', (e) => {
      e.preventDefault();
      return false;
    });
  }
});

onUnmounted(() => {
  window.removeEventListener('message', handleMessage);
});
</script>

<style scoped>
.video-player-wrapper {
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
  padding-bottom: 177.78%; /* 9:16 для вертикального видео (1920/1080 = 1.778) */
  overflow: hidden;
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

/* Overlay для блокировки кликов на верхней и нижней частях и скрытия надписи VK */
.video-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 2;
  background: transparent;
}

/* Блокируем клики в верхней части (где обычно ссылки VK) и скрываем надпись "watch clip on VK" */
.video-overlay-top {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 18%;
  pointer-events: auto;
  cursor: not-allowed;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.85) 0%, rgba(0, 0, 0, 0.5) 40%, rgba(0, 0, 0, 0.2) 70%, transparent 100%);
  z-index: 3;
  border-radius: 12px 12px 0 0;
}

/* Дополнительный overlay для скрытия надписи "watch clip on VK" в левом верхнем углу */
.video-overlay-top::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 30%;
  height: 100%;
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.95) 0%, rgba(0, 0, 0, 0.7) 40%, rgba(0, 0, 0, 0.3) 70%, transparent 100%);
  border-radius: 12px 0 0 0;
}

/* Дополнительный overlay для правого верхнего угла (на случай если надпись там) */
.video-overlay-top::after {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 30%;
  height: 100%;
  background: linear-gradient(225deg, rgba(0, 0, 0, 0.95) 0%, rgba(0, 0, 0, 0.7) 40%, rgba(0, 0, 0, 0.3) 70%, transparent 100%);
  border-radius: 0 12px 0 0;
}

/* Блокируем клики в нижней части */
.video-overlay-bottom {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 10%;
  pointer-events: auto;
  cursor: not-allowed;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.3) 0%, transparent 100%);
  z-index: 3;
  border-radius: 0 0 12px 12px;
}

/* Стили для адаптивности */
@media (max-width: 1200px) {
  .video-player-wrapper {
    max-width: 450px;
  }
}

@media (max-width: 768px) {
  .video-player-wrapper {
    max-width: 100%;
  }
  
  .video-container {
    padding-bottom: 177.78%; /* Сохраняем пропорции 9:16 */
  }
}

@media (max-width: 576px) {
  .video-player-wrapper {
    max-width: 100%;
    border-radius: 8px;
  }
}
</style>

