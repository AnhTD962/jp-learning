<template>
  <div class="max-w-lg mx-auto mt-10 p-6 bg-white rounded shadow">
    <h1 class="text-2xl font-bold mb-4">🔑 Change Password</h1>

    <form @submit.prevent="handleChangePassword" class="space-y-4">
      <div>
        <label class="block text-sm font-medium">Current Password</label>
        <input v-model="currentPassword" type="password" class="w-full border rounded px-3 py-2" required />
      </div>

      <div>
        <label class="block text-sm font-medium">New Password</label>
        <input v-model="newPassword" type="password" class="w-full border rounded px-3 py-2" required />
      </div>

      <div>
        <label class="block text-sm font-medium">Confirm New Password</label>
        <input v-model="confirmPassword" type="password" class="w-full border rounded px-3 py-2" required />
      </div>

      <p v-if="error" class="text-red-600">{{ error }}</p>
      <p v-if="success" class="text-green-600">{{ success }}</p>

      <button type="submit" class="w-full bg-orange-500 text-white py-2 rounded hover:bg-orange-600 transition">
        Update Password
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import authService from '../services/authService'
import { useAuthStore } from '../store/authStore'
import { useRouter } from 'vue-router'

const currentPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const error = ref('')
const success = ref('')

const authStore = useAuthStore()
const router = useRouter()

const handleChangePassword = async () => {
  error.value = ''
  success.value = ''

  if (newPassword.value !== confirmPassword.value) {
    error.value = "Passwords do not match!"
    return
  }

  try {
    const res = await authService.changePassword(
      localStorage.getItem("accessToken"),
      currentPassword.value,
      newPassword.value,
      confirmPassword.value
    )
    success.value = res || "Password updated successfully!"

    // ⛔ Đăng xuất user ngay sau khi đổi mật khẩu
    setTimeout(() => {
      authStore.logout()
      router.push('/')
    }, 1500) // chờ 1.5s để user thấy message
  } catch (e) {
    error.value = e.response?.data || e.message
  }
}
</script>