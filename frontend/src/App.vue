<template>
  <div class="app-shell">
    <SplashScreen />
    <header class="app-navbar navbar navbar-expand-lg bg-body">
      <div class="container">
        <RouterLink class="navbar-brand fw-semibold d-flex align-items-center" to="/">
          <img :src="logoImg" alt="Aurora Concert" class="logo-img" />
        </RouterLink>

        <nav class="d-flex gap-3 align-items-center flex-wrap">
          <RouterLink class="nav-link" to="/">Главная</RouterLink>
        </nav>
      </div>
    </header>
    <main class="flex-grow-1">
      <RouterView />
    </main>
    <footer class="py-4 border-top bg-body-tertiary footer-with-menu">
      <div class="container text-center small text-body-secondary">
        <div>© {{ new Date().getFullYear() }}. Все права защищены.</div>
        <div class="mt-2">
          Created by 
          <a 
            href="https://t.me/ismailovmunir" 
            target="_blank" 
            rel="noopener noreferrer"
            class="footer-creator-link"
          >
            Munir Ismoilov
          </a>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router';
import logoImg from '@/assets/log.svg';
import SplashScreen from '@/components/SplashScreen.vue';
</script>

<style scoped>
.app-shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.app-navbar {
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 10;
  padding: 0.5rem 0;
  min-height: auto;
}

.app-navbar .container {
  padding-top: 0.25rem;
  padding-bottom: 0.25rem;
}

.logo-img {
  height: 35px;
  width: auto;
}

.nav-link {
  font-size: 0.9rem;
  padding: 0.25rem 0.5rem;
}

.footer-creator-link {
  color: #18723F;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
  border-bottom: 1px solid transparent;
}

.footer-creator-link:hover {
  color: #6f42c1;
  border-bottom-color: #6f42c1;
}

.footer-with-menu {
  margin-bottom: 100px; /* Отступ для нижнего меню навигации */
  position: relative;
  z-index: 1;
}
</style>

