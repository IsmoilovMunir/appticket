import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '../views/HomePage.vue';
import AdminDashboard from '../views/AdminDashboard.vue';
import CheckinDashboard from '../views/CheckinDashboard.vue';
import AdminLayout from '../layouts/AdminLayout.vue';
import ConcertPage from '../views/ConcertPage.vue';
import ConcertsManagement from '../views/ConcertsManagement.vue';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomePage
    },
    {
      path: '/admin',
      component: AdminLayout,
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'admin',
          component: AdminDashboard
        },
        {
          path: 'concerts',
          name: 'admin-concerts',
          component: ConcertsManagement
        }
      ]
    },
    {
      path: '/checkin',
      component: AdminLayout,
      meta: { requiresAuth: false }, // Чек-ин доступен без авторизации
      children: [
        {
          path: '',
          name: 'checkin',
          component: CheckinDashboard
        }
      ]
    },
    // Важно: этот маршрут должен быть последним, чтобы не перехватывать другие маршруты
    {
      path: '/:slug',
      name: 'concert',
      component: ConcertPage,
      props: true,
      meta: {
        key: (route: any) => route.params.slug // Уникальный ключ для каждого slug
      }
    }
  ]
});

// Navigation guard для защиты админских маршрутов
router.beforeEach((to, from, next) => {
  // Получаем токен из localStorage напрямую, так как store может быть не инициализирован
  const token = localStorage.getItem('ticketing-token');
  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth);

  if (requiresAuth && !token) {
    // Если требуется авторизация, но токена нет - разрешаем доступ к /admin
    // (там будет форма входа)
    if (to.path === '/admin' || to.path.startsWith('/admin/')) {
      next();
    } else {
      // Перенаправляем на страницу входа в админку
      next('/admin');
    }
  } else {
    next();
  }
});

export default router;
