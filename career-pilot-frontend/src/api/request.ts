import axios from 'axios';

export const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
});

export const javaRequest = axios.create({
  baseURL: import.meta.env.VITE_JAVA_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 15000,
});

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('career_pilot_token');

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

javaRequest.interceptors.request.use((config) => {
  const token = localStorage.getItem('career_pilot_token');

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});
