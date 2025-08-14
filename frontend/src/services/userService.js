import apiClient from './apiClient'

export default {
    async getUserProfile() {
        const res = await apiClient.get('/users/me')
        return res.data
    },
}
