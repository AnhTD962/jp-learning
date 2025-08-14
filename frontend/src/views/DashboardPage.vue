<template>
  <div class="p-4">
    <h1 class="text-2xl font-bold mb-4">Welcome, {{ user?.username }}</h1>
    <p>XP: {{ user?.xp }}</p>
    <p>Streak: {{ user?.streak }} days</p>

    <div class="mt-4 flex gap-4">
      <router-link to="/quiz" class="btn-primary">Start Quiz</router-link>
      <router-link to="/flashcards" class="btn-primary">Review Flashcards</router-link>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from '../store/authStore'
import { useNotificationStore } from '../store/notificationStore'

const auth = useAuthStore()
const { notifications } = useNotificationStore()
const user = auth.user
</script>
