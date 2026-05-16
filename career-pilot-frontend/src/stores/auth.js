import { defineStore } from 'pinia';
import { getCurrentUserApi, loginApi, registerApi, } from '@/api/auth';
import { clearToken, getToken, setToken } from '@/utils/storage';
export const useAuthStore = defineStore('auth', {
    state: () => ({
        token: getToken(),
        user: null,
    }),
    getters: {
        isAuthenticated: (state) => Boolean(state.token),
    },
    actions: {
        async login(payload) {
            const response = await loginApi(payload);
            const token = response.data.access_token;
            setToken(token);
            this.token = token;
            this.user = response.data.user;
        },
        async register(payload) {
            const response = await registerApi(payload);
            const token = response.data.access_token;
            setToken(token);
            this.token = token;
            this.user = response.data.user;
        },
        async fetchCurrentUser() {
            if (!this.token)
                return;
            const response = await getCurrentUserApi();
            this.user = response.data;
        },
        logout() {
            clearToken();
            this.token = null;
            this.user = null;
        },
    },
});
