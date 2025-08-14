<template>
  <div class="p-4">
    <h2 class="text-2xl font-bold mb-4">Leaderboard</h2>
    <LeaderboardTable :users="leaderboard" />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useGamificationStore } from '../store/gamificationStore'
import LeaderboardTable from '../components/LeaderboardTable.vue'

const store = useGamificationStore()
const leaderboard = store.leaderboard

onMounted(() => {
  store.fetchLeaderboard()
})
</script>
