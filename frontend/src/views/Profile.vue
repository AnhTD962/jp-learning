<template>
  <div class="max-w-lg mx-auto mt-10 p-6 bg-white rounded shadow">
    <h1 class="text-2xl font-bold mb-4">👤 Profile</h1>

    <div v-if="user">
      <p class="mb-2"><span class="font-semibold">Username:</span> {{ user.username }}</p>
      <p class="mb-2"><span class="font-semibold">Email:</span> {{ user.email }}</p>
    </div>

    <div v-else>
      <p class="text-gray-600">Loading profile...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import userService from '../services/userService'
import { useAuthStore } from '../store/authStore'

const auth = useAuthStore()
const user = ref(null)

onMounted(async () => {
  if (auth.isLoggedIn) {
    try {
      user.value = await userService.getUserProfile()
    } catch (err) {
      console.error('Failed to fetch profile:', err)
    }
  }
})
</script>
