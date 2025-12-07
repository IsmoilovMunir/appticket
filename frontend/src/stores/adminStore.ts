import { defineStore } from 'pinia';
import { login, apiClient } from '../services/api';

const TOKEN_KEY = 'ticketing-token';
const initialToken = localStorage.getItem(TOKEN_KEY) || '';
if (initialToken) {
  apiClient.defaults.headers.common.Authorization = `Bearer ${initialToken}`;
}

export const useAdminStore = defineStore('admin', {
  state: () => ({
    token: initialToken,
    error: '' as string | null,
    loading: false
  }),
  actions: {
    setToken(token: string) {
      this.token = token;
      localStorage.setItem(TOKEN_KEY, token);
      apiClient.defaults.headers.common.Authorization = `Bearer ${token}`;
    },
    async signIn(username: string, password: string) {
      this.loading = true;
      this.error = null;
      try {
        const response = await login({ username, password });
        this.setToken(response.accessToken);
      } catch (error: unknown) {
        this.error = 'Неверный логин или пароль';
        throw error;
      } finally {
        this.loading = false;
      }
    },
    signOut() {
      this.token = '';
      localStorage.removeItem(TOKEN_KEY);
      delete apiClient.defaults.headers.common.Authorization;
    }
  }
});

