<template>
  <div class="admin-layout">
    <header class="admin-header navbar navbar-expand-lg bg-dark border-bottom border-secondary">
      <div class="container-fluid">
        <RouterLink class="navbar-brand text-white fw-semibold d-flex align-items-center" to="/admin">
          <img :src="logoImg" alt="App Ticket" class="admin-logo-img" />
        </RouterLink>
        <nav class="d-flex gap-3 align-items-center">
          <RouterLink v-if="isAuthenticated" class="nav-link text-white-50" to="/admin">Панель управления</RouterLink>
          <RouterLink v-if="isAuthenticated" class="nav-link text-white-50" to="/admin/concerts">Концерты</RouterLink>
          <RouterLink v-if="isAuthenticated" class="nav-link text-white-50" to="/checkin">Чек-ин</RouterLink>
          <RouterLink class="nav-link text-white-50" to="/">← На сайт</RouterLink>
          <button v-if="isAuthenticated" class="btn btn-outline-light btn-sm" @click="handleSignOut">Выйти</button>
        </nav>
      </div>
    </header>
    <main class="admin-main">
      <RouterView />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { RouterLink, RouterView, useRouter } from 'vue-router';
import { useAdminStore } from '../stores/adminStore';
import logoImg from '@/assets/logo.svg';

const adminStore = useAdminStore();
const router = useRouter();

const isAuthenticated = computed(() => !!adminStore.token);

const handleSignOut = () => {
  adminStore.signOut();
  router.push('/admin');
};
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f8f9fa;
}

.admin-header {
  position: sticky;
  top: 0;
  z-index: 1000;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.admin-main {
  flex: 1;
  padding: 0;
}

.nav-link {
  text-decoration: none;
  transition: color 0.2s;
}

.nav-link:hover {
  color: #fff !important;
}

.admin-logo-img {
  height: 60px;
  width: auto;
  filter: brightness(0) invert(1);
}
</style>

