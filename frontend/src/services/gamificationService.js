import apiClient from './apiClient'

export default {
    async getLeaderboard() {
        const res = await apiClient.get('/gamification/leaderboard')
        return res.data
    },

    async getAchievements() {
        const res = await apiClient.get('/gamification/achievements')
        return res.data
    },
}
