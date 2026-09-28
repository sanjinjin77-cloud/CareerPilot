import axios from 'axios';

import { clearToken } from '@/utils/storage';

export const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000,
});

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('career_pilot_token');

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

request.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      clearToken();
      if (window.location.pathname !== '/login') {
        const redirect = window.location.pathname + window.location.search;
        window.location.href = `/login?redirect=${encodeURIComponent(redirect)}`;
      }
    }

    if (error.response?.status === 429) {
      const message = error.response.data?.message;
      if (typeof message === 'string' && message.trim()) {
        error.message = message;
      }
    }

    return Promise.reject(error);
  },
);
