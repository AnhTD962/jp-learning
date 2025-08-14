import axios from 'axios'
import { useAuthStore } from '../store/authStore'

const apiClient = axios.create({
    baseURL: 'http://localhost:8080', // ⛳ Replace with real API base URL
    headers: {
        'Content-Type': 'application/json',
    },
})

apiClient.interceptors.request.use((config) => {
    const authStore = useAuthStore()
    if (authStore.accessToken) {
        config.headers.Authorization = `Bearer ${authStore.accessToken}`
    }
    return config
})

apiClient.interceptors.response.use(
    response => response,
    async (error) => {
        const authStore = useAuthStore()

        if (error.response?.status === 401) {
            try {
                await authStore.refreshTokens()
                return apiClient(error.config)
            } catch (e) {
                authStore.logout()
                throw e
            }
        }

        return Promise.reject(error)
    }
)

export default apiClient
