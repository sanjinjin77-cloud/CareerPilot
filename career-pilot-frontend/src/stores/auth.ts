import { defineStore } from 'pinia';

import {
  getCurrentUserApi,
  loginApi,
  registerApi,
  type AuthUser,
  type LoginPayload,
  type RegisterPayload,
} from '@/api/auth';
import { clearToken, getToken, setToken } from '@/utils/storage';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: getToken(),
    user: null as AuthUser | null,
    initialized: false,
  }),
  getters: {
    hasToken: (state) => Boolean(state.token),
    isAuthenticated: (state) => Boolean(state.token && state.user),
  },
  actions: {
    async login(payload: LoginPayload) {
      const response = await loginApi(payload);
      const token = response.data.access_token;

      setToken(token);
      this.token = token;
      this.user = response.data.user;
      this.initialized = true;
    },
    async register(payload: RegisterPayload) {
      const response = await registerApi(payload);
      const token = response.data.access_token;

      setToken(token);
      this.token = token;
      this.user = response.data.user;
      this.initialized = true;
    },
    async fetchCurrentUser() {
      if (!this.token) {
        this.initialized = true;
        return null;
      }

      try {
        const response = await getCurrentUserApi();
        this.user = response.data;
        this.initialized = true;
        return this.user;
      } catch (error) {
        this.logout();
        this.initialized = true;
        return null;
      }
    },
    logout() {
      clearToken();
      this.token = null;
      this.user = null;
      this.initialized = true;
    },
  },
});
