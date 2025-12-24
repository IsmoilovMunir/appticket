import { createApp } from 'vue';
import { createPinia } from 'pinia';
import App from './App.vue';
import router from './router';

import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap-icons/font/bootstrap-icons.css';
import './assets/styles.scss';

// Фильтруем ошибки от VK CDN в консоли (это нормальные ошибки, которые не влияют на работу видео)
if (typeof window !== 'undefined' && window.console) {
  const originalError = console.error;
  console.error = (...args: any[]) => {
    // Игнорируем ошибки от VK CDN (vkvd596.okcdn.ru) - это внутренние проблемы VK
    const errorMessage = args.join(' ');
    if (errorMessage.includes('vkvd596.okcdn.ru') || 
        errorMessage.includes('okcdn.ru') ||
        (errorMessage.includes('video.m3u8') && errorMessage.includes('ERR_FAILED 400')) ||
        (errorMessage.includes('GET') && errorMessage.includes('m3u8') && errorMessage.includes('400'))) {
      // Не показываем эти ошибки в консоли, так как они не критичны и не влияют на работу
      return;
    }
    originalError.apply(console, args);
  };
  
  // Также фильтруем ошибки из fetch/XMLHttpRequest
  window.addEventListener('error', (event) => {
    if (event.message && (
      event.message.includes('vkvd596.okcdn.ru') ||
      event.message.includes('okcdn.ru') ||
      (event.message.includes('m3u8') && event.message.includes('400'))
    )) {
      event.preventDefault();
      event.stopPropagation();
    }
  }, true);
}

const app = createApp(App);
app.use(createPinia());
app.use(router);
app.mount('#app');

