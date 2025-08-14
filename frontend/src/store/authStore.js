import { defineStore } from 'pinia'
import authService from '../services/authService'

export const useAuthStore = defineStore('auth', {
    state: () => ({
        user: null,
        accessToken: '',
        refreshToken: '',
        isAuthenticated: false,
        isLoading: false,
        error: null,
    }),
    getters: {
        isLoggedIn: (state) => !!state.accessToken,
        isAdmin: (state) => state.user?.roles?.includes('ADMIN'),
        currentUser: (state) => state.user,
    },
    actions: {
        async login(credentials) {
            this.isLoading = true
            try {
                const data = await authService.login(credentials)
                this.accessToken = data.accessToken
                this.refreshToken = data.refreshToken
                this.user = data.user
                this.isAuthenticated = true
                localStorage.setItem('token', data.accessToken)
            } catch (e) {
                this.error = e
            } finally {
                this.isLoading = false
            }
        },
        async register(data) {
            return await authService.register(data)
        },
        logout() {
            this.accessToken = ''
            this.refreshToken = ''
            this.user = null
            this.isAuthenticated = false
            localStorage.removeItem('token')
        },
        async refreshTokens() {
            const res = await authService.refreshToken(this.refreshToken)
            this.accessToken = res.accessToken
        },
        checkAuth() {
            this.accessToken = localStorage.getItem('token')
            this.isAuthenticated = !!this.accessToken
        },
    },
})
