<template>
  <div class="app-shell">
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
    <footer class="py-4 border-top bg-body-tertiary">
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
}

.logo-img {
  height: 50px;
  width: auto;
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
</style>

