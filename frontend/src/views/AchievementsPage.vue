<template>
  <div class="p-4 grid grid-cols-1 sm:grid-cols-2 gap-4">
    <AchievementBadge
        v-for="badge in achievements"
        :key="badge.id"
        :badge="badge"
    />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useGamificationStore } from '../store/gamificationStore'
import AchievementBadge from '../components/AchievementBadge.vue'

const store = useGamificationStore()
const achievements = store.achievements

onMounted(() => {
  store.fetchAchievements()
})
</script>
