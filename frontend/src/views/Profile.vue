<template>
  <div class="max-w-lg mx-auto mt-10 p-6 bg-white rounded shadow">
    <h1 class="text-2xl font-bold mb-4">👤 Profile</h1>

    <!-- Thông báo -->
    <div
      v-if="message"
      :class="[
        'p-3 mb-4 rounded',
        messageType === 'success' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
      ]"
    >
      {{ message }}
    </div>

    <div v-if="user">
      <p class="mb-2"><span class="font-semibold">Username:</span> {{ user.username }}</p>
      <p class="mb-2"><span class="font-semibold">Email:</span> {{ user.email }}</p>

      <!-- Form cập nhật -->
      <form @submit.prevent="updateProfile" class="mt-6 space-y-4">
        <div>
          <label class="block font-semibold mb-1">Name</label>
          <input v-model="form.username" type="text" class="w-full border rounded px-3 py-2" />
        </div>

        <div>
          <label class="block font-semibold mb-1">Email</label>
          <input v-model="form.email" type="email" class="w-full border rounded px-3 py-2" />
        </div>

        <button
          type="submit"
          class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
          :disabled="loading"
        >
          {{ loading ? 'Saving...' : 'Save Changes' }}
        </button>
      </form>
    </div>

    <div v-else-if="error">
      <p class="text-red-600">⚠️ Failed to load profile</p>
    </div>

    <div v-else>
      <p class="text-gray-600">Loading profile...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import userService from '../services/userService'
import { useAuthStore } from '../store/authStore'

const auth = useAuthStore()
const router = useRouter()

const user = ref(null)
const form = ref({ username: '', email: '' })
const loading = ref(false)
const error = ref(null)
const message = ref('')
const messageType = ref('success') // 'success' hoặc 'error'

onMounted(async () => {
  try {
    if (auth.isLoggedIn) {
      user.value = await userService.getUserProfile()
      form.value.username = user.value.username || ''
      form.value.email = user.value.email || ''
    } else {
      error.value = 'Not logged in'
    }
  } catch (err) {
    console.error('Failed to fetch profile:', err)
    error.value = err.message || 'Failed to load profile'
  }
})

const updateProfile = async () => {
  try {
    loading.value = true
    await userService.updateUserProfile(form.value)

    message.value = '✅ Profile updated! Please log in again.'
    messageType.value = 'success'

    // logout sau vài giây
    setTimeout(() => {
      auth.logout()
      router.push('/')
    }, 2000)
  } catch (err) {
    console.error('Failed to update profile:', err)
    message.value = '❌ Update failed!'
    messageType.value = 'error'
  } finally {
    loading.value = false
  }
}
</script>
