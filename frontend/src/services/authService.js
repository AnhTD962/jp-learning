import apiClient from './apiClient'

export default {
    async login(credentials) {
        const res = await apiClient.post('/auth/login', credentials)
        return res.data
    },

    async register(data) {
        const res = await apiClient.post('/auth/register', data)
        return res.data
    },

    async refreshToken(refreshToken) {
        const res = await apiClient.post('/auth/refresh', { refreshToken })
        return res.data
    },
}
