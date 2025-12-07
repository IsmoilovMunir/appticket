import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
  server: {
    port: 4173,
    // Отключаем кеширование в режиме разработки
    headers: {
      'Cache-Control': 'no-store, no-cache, must-revalidate, proxy-revalidate',
      'Pragma': 'no-cache',
      'Expires': '0'
    },
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/ws-seat-status': {
        target: 'http://localhost:8080',
        ws: true
      }
    }
  },
  build: {
    // Добавляем хеши к именам файлов для предотвращения кеширования
    rollupOptions: {
      output: {
        entryFileNames: `assets/[name].[hash].js`,
        chunkFileNames: `assets/[name].[hash].js`,
        assetFileNames: `assets/[name].[hash].[ext]`
      }
    },
    // Отключаем минификацию для отладки (можно включить обратно для продакшена)
    // minify: 'terser',
    // Чистим выходную директорию перед сборкой
    emptyOutDir: true
  },
  // Отключаем кеширование в режиме разработки
  optimizeDeps: {
    force: true
  }
});

