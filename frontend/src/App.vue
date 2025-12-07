<template>
  <div class="app-shell">
    <header class="app-navbar navbar navbar-expand-lg bg-body">
      <div class="container">
        <RouterLink class="navbar-brand fw-semibold d-flex align-items-center" to="/">
          <img :src="logoImg" alt="Aurora Concert" class="logo-img" />
        </RouterLink>
        <nav class="d-flex gap-3">
          <RouterLink class="nav-link" to="/">Главная</RouterLink>
        </nav>
      </div>
    </header>
    <main class="flex-grow-1">
      <RouterView />
    </main>
    <footer class="py-4 border-top bg-body-tertiary">
      <div class="container text-center small text-body-secondary">
        © {{ new Date().getFullYear() }}. Все права защищены.
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
</style>

