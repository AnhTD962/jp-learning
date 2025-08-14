import { defineStore } from 'pinia'
import gamificationService from '../services/gamificationService'

export const useGamificationStore = defineStore('gamification', {
    state: () => ({
        leaderboard: [],
        achievements: [],
        isLoading: false,
        error: null,
    }),
    actions: {
        async fetchLeaderboard() {
            this.leaderboard = await gamificationService.getLeaderboard()
        },
        async fetchAchievements() {
            this.achievements = await gamificationService.getAchievements()
        },
    },
})
