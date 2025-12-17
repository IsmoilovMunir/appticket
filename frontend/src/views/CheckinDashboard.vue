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
              
              <!-- Кнопка сканирования камерой -->
              <div class="mb-3">
                <button 
                  v-if="!isScanning" 
                  @click="startScanning" 
                  class="btn btn-outline-primary w-100 mb-3"
                  :disabled="loading"
                >
                  <i class="bi bi-camera me-2"></i>
                  Сканировать QR-код
                </button>
                <button 
                  v-else 
                  @click="stopScanning" 
                  class="btn btn-outline-danger w-100 mb-3"
                >
                  <i class="bi bi-camera-video-off me-2"></i>
                  Остановить сканирование
                </button>
              </div>

              <!-- Квадратный экран с видео камеры -->
              <div v-if="isScanning" class="mb-3">
                <div class="camera-preview-container">
                  <div class="camera-preview-wrapper">
                    <video 
                      ref="videoElement" 
                      class="camera-preview-video"
                      autoplay
                      playsinline
                      muted
                    ></video>
                    <!-- Индикатор загрузки -->
                    <div v-if="!videoReady" class="camera-loading-overlay">
                      <div class="spinner-border text-light" role="status">
                        <span class="visually-hidden">Загрузка камеры...</span>
                      </div>
                      <p class="text-white mt-3 mb-0 small">Инициализация камеры...</p>
                    </div>
                    <!-- Рамка для наведения на QR-код -->
                    <div v-if="videoReady" class="qr-scanner-overlay">
                      <div class="qr-scanner-frame"></div>
                      <p class="qr-scanner-hint text-white text-center small mb-0">
                        Наведите камеру на QR-код билета
                      </p>
                    </div>
                  </div>
                  <canvas ref="canvasElement" class="d-none"></canvas>
                  <div v-if="scanningError" class="alert alert-danger mt-3 mb-0">
                    {{ scanningError }}
                  </div>
                </div>
              </div>

              <form @submit.prevent="previewTicket">
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

    <!-- Модальное окно подтверждения -->
    <div v-if="showConfirmationModal" class="modal-overlay" @click.self="cancelCheckIn">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Подтверждение регистрации</h5>
            <button type="button" class="btn-close" @click="cancelCheckIn" aria-label="Close"></button>
          </div>
          <div class="modal-body text-center">
            <div v-if="previewResult" class="mb-4">
              <div class="mb-3">
                <i class="bi bi-person-circle" style="font-size: 4rem; color: #18723F;"></i>
              </div>
              <h4 class="mb-2">{{ previewResult.buyerName }}</h4>
              <p class="text-muted mb-3" v-if="previewResult.seat">
                Стол {{ previewResult.seat.tableNumber }}, место {{ previewResult.seat.chairNumber }}
              </p>
              <div v-if="previewResult.result === 'DUPLICATE'" class="alert alert-warning mb-3">
                <i class="bi bi-exclamation-triangle me-2"></i>
                Этот билет уже был использован ранее
              </div>
            </div>
            <div v-else class="mb-4">
              <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Загрузка...</span>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button 
              type="button" 
              class="btn btn-outline-danger btn-lg" 
              @click="cancelCheckIn"
              :disabled="loading"
            >
              <i class="bi bi-x-circle me-2"></i>
              Не пропускать
            </button>
            <button 
              type="button" 
              class="btn btn-success btn-lg" 
              @click="confirmCheckIn"
              :disabled="loading"
            >
              <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
              <i v-else class="bi bi-check-circle me-2"></i>
              {{ previewResult?.result === 'DUPLICATE' ? 'Понятно' : 'Пропустить' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Модальное окно успешной регистрации -->
    <div v-if="showSuccessModal" class="modal-overlay" @click.self="closeSuccessModal">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-body text-center p-5">
            <div class="mb-4">
              <i class="bi bi-check-circle-fill text-success" style="font-size: 5rem;"></i>
            </div>
            <h3 class="mb-3 text-success">Добро пожаловать!</h3>
            <h4 class="mb-4">{{ result?.buyerName || previewResult?.buyerName }}</h4>
            <button 
              type="button" 
              class="btn btn-primary btn-lg" 
              @click="closeSuccessModal"
            >
              Закрыть
            </button>
          </div>
        </div>
      </div>
    </div>

  </section>
</template>

<script setup lang="ts">
import { ref, computed, onUnmounted, onMounted, nextTick } from 'vue';
import { apiClient } from '../services/api';
import jsQR from 'jsqr';

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
const isScanning = ref(false);
const scanningError = ref('');
const videoReady = ref(false);
const videoElement = ref<HTMLVideoElement | null>(null);
const canvasElement = ref<HTMLCanvasElement | null>(null);
const showConfirmationModal = ref(false);
const showSuccessModal = ref(false);
const previewResult = ref<CheckinResponse | null>(null);

let stream: MediaStream | null = null;
let scanInterval: number | null = null;

onMounted(() => {
  // Инициализация при монтировании
});

onUnmounted(() => {
  stopScanning();
});

const startScanning = async () => {
  try {
    scanningError.value = '';
    
    // Проверяем поддержку getUserMedia
    if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
      scanningError.value = 'Ваш браузер не поддерживает доступ к камере';
      return;
    }

    // Сначала показываем элемент в DOM
    isScanning.value = true;
    
    // Ждем, пока DOM обновится и элемент появится
    await nextTick();
    
    // Проверяем, что элемент существует
    if (!videoElement.value) {
      scanningError.value = 'Ошибка инициализации видео элемента';
      isScanning.value = false;
      return;
    }

    // Запрашиваем доступ к камере
    // Сначала пробуем заднюю камеру (для мобильных)
    let videoConstraints: MediaTrackConstraints = {
      facingMode: 'environment',
      width: { ideal: 1280 },
      height: { ideal: 720 }
    };

    try {
      stream = await navigator.mediaDevices.getUserMedia({
        video: videoConstraints
      });
    } catch (err) {
      // Если не получилось с задней камерой, пробуем любую доступную
      console.log('Не удалось получить доступ к задней камере, пробуем любую доступную');
      videoConstraints = {
        width: { ideal: 1280 },
        height: { ideal: 720 }
      };
      stream = await navigator.mediaDevices.getUserMedia({
        video: videoConstraints
      });
    }

    // Устанавливаем поток в видео элемент
    const video = videoElement.value;
    if (!video) {
      scanningError.value = 'Видео элемент не найден';
      isScanning.value = false;
      return;
    }

    console.log('Устанавливаем поток в видео элемент');
    console.log('Поток активен:', stream.active);
    console.log('Треки:', stream.getTracks().map(t => ({ 
      kind: t.kind, 
      enabled: t.enabled, 
      readyState: t.readyState,
      muted: t.muted,
      settings: t.getSettings()
    })));
    
    // Убеждаемся, что треки активны
    stream.getTracks().forEach(track => {
      if (!track.enabled) {
        console.log('Включаем трек:', track.kind);
        track.enabled = true;
      }
      if (track.muted) {
        console.log('Трек приглушен:', track.kind);
      }
    });
    
    // Проверяем, что поток действительно активен
    if (!stream.active) {
      scanningError.value = 'Поток камеры не активен';
      isScanning.value = false;
      return;
    }
    
    video.srcObject = stream;
    console.log('Поток установлен в видео элемент, srcObject:', video.srcObject);
    
    // Убеждаемся, что видео элемент настроен правильно
    video.setAttribute('playsinline', '');
    video.setAttribute('webkit-playsinline', '');
    video.muted = true; // Явно устанавливаем muted для autoplay
    
    // Настраиваем обработчики событий
    const handleLoadedMetadata = () => {
      console.log('Метаданные видео загружены');
      if (video) {
        video.play()
          .then(() => {
            console.log('Видео начало воспроизводиться');
            videoReady.value = true;
            startQRScanning();
          })
          .catch((err) => {
            console.error('Ошибка воспроизведения видео:', err);
            scanningError.value = 'Не удалось запустить камеру';
            stopScanning();
          });
      }
    };

    const handlePlay = () => {
      console.log('Видео воспроизводится');
      console.log('Размеры видео:', video.videoWidth, 'x', video.videoHeight);
      videoReady.value = true;
    };

    const handlePlaying = () => {
      console.log('Видео играет');
      videoReady.value = true;
    };

    const handleError = (e: Event) => {
      console.error('Ошибка видео элемента:', e);
      const videoError = video.error;
      if (videoError) {
        console.error('Код ошибки видео:', videoError.code, 'Сообщение:', videoError.message);
      }
      scanningError.value = 'Ошибка при загрузке видео потока';
      stopScanning();
    };

    video.addEventListener('loadedmetadata', handleLoadedMetadata, { once: true });
    video.addEventListener('play', handlePlay, { once: true });
    video.addEventListener('playing', handlePlaying, { once: true });
    video.addEventListener('error', handleError);
    
    // Пытаемся запустить воспроизведение сразу
    let tryCount = 0;
    const maxTries = 50; // Максимум 5 секунд ожидания
    
    const tryPlay = () => {
      tryCount++;
      if (video.readyState >= 1) {
        console.log('Пытаемся запустить видео программно, readyState:', video.readyState);
        video.play()
          .then(() => {
            console.log('Видео запущено программно');
            videoReady.value = true;
            if (!scanInterval) {
              startQRScanning();
            }
          })
          .catch((err) => {
            console.error('Ошибка программного запуска видео:', err);
            // Пробуем еще раз через небольшую задержку
            if (tryCount < maxTries) {
              setTimeout(() => {
                if (video.paused && isScanning.value) {
                  video.play().catch(console.error);
                }
              }, 200);
            } else {
              scanningError.value = 'Не удалось запустить видео поток. Проверьте, что камера доступна.';
            }
          });
      } else if (tryCount < maxTries) {
        // Если видео еще не готово, ждем еще
        setTimeout(tryPlay, 100);
      } else {
        scanningError.value = 'Видео поток не загружается. Проверьте подключение камеры.';
        console.error('Видео не загрузилось после', maxTries * 100, 'мс');
      }
    };
    
    setTimeout(tryPlay, 100);
  } catch (err: unknown) {
    console.error('Ошибка доступа к камере:', err);
    if (err instanceof Error) {
      if (err.name === 'NotAllowedError' || err.name === 'PermissionDeniedError') {
        scanningError.value = 'Доступ к камере запрещен. Разрешите доступ к камере в настройках браузера.';
      } else if (err.name === 'NotFoundError' || err.name === 'DevicesNotFoundError') {
        scanningError.value = 'Камера не найдена. Убедитесь, что камера подключена и доступна.';
      } else if (err.name === 'NotReadableError' || err.name === 'TrackStartError') {
        scanningError.value = 'Камера уже используется другим приложением.';
      } else {
        scanningError.value = `Ошибка доступа к камере: ${err.message}`;
      }
    } else {
      scanningError.value = 'Не удалось получить доступ к камере. Проверьте разрешения браузера.';
    }
    isScanning.value = false;
  }
};

const stopScanning = () => {
  if (scanInterval !== null) {
    cancelAnimationFrame(scanInterval);
    scanInterval = null;
  }
  
  if (stream) {
    stream.getTracks().forEach(track => track.stop());
    stream = null;
  }
  
  if (videoElement.value) {
    // Останавливаем видео
    videoElement.value.pause();
    videoElement.value.srcObject = null;
    videoElement.value.load(); // Сбрасываем видео элемент
  }
  
  isScanning.value = false;
  videoReady.value = false;
  scanningError.value = '';
};

const startQRScanning = () => {
  if (!videoElement.value || !canvasElement.value) return;

  const video = videoElement.value;
  const canvas = canvasElement.value;
  const context = canvas.getContext('2d');

  if (!context) return;

  const scan = () => {
    if (!video || !canvas || !context || !isScanning.value) return;

    if (video.readyState === video.HAVE_ENOUGH_DATA) {
      canvas.width = video.videoWidth;
      canvas.height = video.videoHeight;
      context.drawImage(video, 0, 0, canvas.width, canvas.height);

      const imageData = context.getImageData(0, 0, canvas.width, canvas.height);
      const code = jsQR(imageData.data, imageData.width, imageData.height);

      if (code) {
        // QR-код найден!
        token.value = code.data;
        stopScanning();
        // Предварительно проверяем билет
        previewTicket();
      }
    }

    if (isScanning.value) {
      scanInterval = requestAnimationFrame(scan);
    }
  };

  scan();
};

const previewTicket = async () => {
  if (!token.value || !token.value.trim()) {
    error.value = 'Введите токен билета';
    return;
  }

  loading.value = true;
  error.value = '';
  previewResult.value = null;
  result.value = null;
  
  try {
    const { data } = await apiClient.post<CheckinResponse>('/checkin/preview', { ticketToken: token.value.trim() });
    previewResult.value = data;
    
    // Всегда показываем модальное окно для APPROVED и DUPLICATE
    if (data.result === 'APPROVED' || data.result === 'DUPLICATE') {
      console.log('Показываем модальное окно подтверждения для:', data.buyerName, 'Результат:', data.result);
      // Небольшая задержка для правильного отображения
      await nextTick();
      showConfirmationModal.value = true;
    } else {
      // Если билет невалиден, показываем ошибку
      error.value = 'QR-код невалиден';
      result.value = data;
    }
  } catch (err: unknown) {
    error.value = 'Не удалось проверить билет';
    console.error('Ошибка предварительной проверки:', err);
    if (err instanceof Error) {
      console.error('Детали ошибки:', err.message);
    }
  } finally {
    loading.value = false;
  }
};

const confirmCheckIn = async () => {
  // Если билет уже использован (DUPLICATE), просто закрываем модальное окно и показываем информацию
  if (previewResult.value?.result === 'DUPLICATE') {
    showConfirmationModal.value = false;
    result.value = previewResult.value;
    // Очищаем токен для возможности нового сканирования
    token.value = '';
    previewResult.value = null;
    return;
  }

  // Для новых билетов (APPROVED) - регистрируем
  loading.value = true;
  error.value = '';
  try {
    const { data } = await apiClient.post<CheckinResponse>('/checkin', { ticketToken: token.value });
    result.value = data;
    showConfirmationModal.value = false;
    
    // Показываем модальное окно успеха только если регистрация прошла успешно
    if (data.result === 'APPROVED') {
      showSuccessModal.value = true;
    } else {
      // Если что-то пошло не так, показываем результат
      result.value = data;
    }
  } catch (err: unknown) {
    error.value = 'Не удалось зарегистрировать билет';
    console.error('Ошибка регистрации:', err);
  } finally {
    loading.value = false;
  }
};

const cancelCheckIn = () => {
  showConfirmationModal.value = false;
  previewResult.value = null;
  token.value = '';
  result.value = null;
  error.value = '';
  // Можно заново сканировать
};

const closeSuccessModal = () => {
  showSuccessModal.value = false;
  token.value = '';
  result.value = null;
  previewResult.value = null;
  error.value = '';
};

// Старая функция checkTicket - теперь не используется, заменена на previewTicket
// Оставлена для обратной совместимости, если где-то еще используется
const checkTicket = async () => {
  // Используем previewTicket для показа модального окна
  await previewTicket();
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

<style scoped>
.qr-scanner-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  pointer-events: none;
  padding: 1rem;
}

.qr-scanner-frame {
  width: 250px;
  height: 250px;
  border: 3px solid #18723F;
  border-radius: 12px;
  box-shadow: 0 0 0 9999px rgba(0, 0, 0, 0.5);
  position: relative;
  animation: pulse 2s ease-in-out infinite;
}

.qr-scanner-frame::before,
.qr-scanner-frame::after {
  content: '';
  position: absolute;
  width: 30px;
  height: 30px;
  border: 4px solid #18723F;
}

.qr-scanner-frame::before {
  top: -4px;
  left: -4px;
  border-right: none;
  border-bottom: none;
  border-top-left-radius: 8px;
}

.qr-scanner-frame::after {
  bottom: -4px;
  right: -4px;
  border-left: none;
  border-top: none;
  border-bottom-right-radius: 8px;
}

.qr-scanner-hint {
  margin-top: 1rem;
  background: rgba(0, 0, 0, 0.7);
  padding: 0.5rem 1rem;
  border-radius: 8px;
  backdrop-filter: blur(4px);
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.02);
  }
}

/* Квадратный экран с видео камеры */
.camera-preview-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.camera-preview-wrapper {
  position: relative;
  width: 100%;
  max-width: 400px;
  aspect-ratio: 1;
  min-height: 300px;
  background: #000;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.camera-preview-video {
  width: 100%;
  height: 100%;
  min-height: 300px;
  object-fit: cover;
  display: block;
  background: #000;
  transform: scaleX(1); /* Убираем зеркальное отображение для фронтальной камеры */
  position: relative;
  z-index: 1;
}

/* Если видео не загружено, показываем черный фон */
.camera-preview-video:not([src]):not([srcObject]) {
  background: #000;
}

/* Убеждаемся, что видео видно */
.camera-preview-video[srcObject] {
  opacity: 1;
  visibility: visible;
}

.camera-loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.7);
  z-index: 10;
}

@media (max-width: 576px) {
  .qr-scanner-frame {
    width: 200px;
    height: 200px;
  }
  
  .qr-scanner-hint {
    font-size: 0.75rem;
    padding: 0.4rem 0.8rem;
  }
  
  .camera-preview-wrapper {
    max-width: 100%;
  }
}

/* Модальные окна */
.modal-overlay {
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  background: rgba(0, 0, 0, 0.5) !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  z-index: 9999 !important;
  padding: 1rem;
}

.modal-overlay .modal-dialog {
  max-width: 500px;
  width: 100%;
  margin: 0;
  position: relative;
  z-index: 10000;
}

.modal-overlay .modal-content {
  border-radius: 16px;
  border: none;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  position: relative;
  z-index: 10001;
  background: white;
}

.modal-overlay .modal-header {
  border-bottom: 1px solid #e9ecef;
  padding: 1.5rem;
}

.modal-overlay .modal-body {
  padding: 2rem 1.5rem;
}

.modal-overlay .modal-footer {
  border-top: 1px solid #e9ecef;
  padding: 1.5rem !important;
  display: flex !important;
  justify-content: center !important;
  align-items: center !important;
  gap: 1rem;
  flex-wrap: wrap;
}

.modal-overlay .modal-footer .btn {
  flex: 0 0 auto;
  min-width: 160px;
  font-size: 1rem;
  padding: 0.75rem 1.5rem;
  visibility: visible !important;
  opacity: 1 !important;
  display: inline-block !important;
}
</style>

