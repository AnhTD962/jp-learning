<template>
  <div class="bg-white shadow-md rounded p-6 max-w-sm mx-auto">
    <h2 class="text-xl font-bold mb-4">Forgot Password</h2>

    <form @submit.prevent="onSubmit">
      <input
        v-model="form.email"
        type="email"
        placeholder="Email"
        class="input"
        required
      />
      <p v-if="message" :class="['text-sm mt-2', message.includes('sent') ? 'text-green-600' : 'text-red-500']">
        {{ message }}
      </p>
      <button type="submit" class="btn-primary w-full mt-4">Reset Password</button>
    </form>

    <div class="mt-4 text-center">
      <p>
        Remembered your password?
        <button type="button" class="text-blue-600 underline" @click="$emit('switch-to-login')">
          Back to Login
        </button>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue"
import authService from "../services/authService"

const emit = defineEmits(['switch-to-login'])
const form = ref({ email: "" })
const message = ref("")

async function onSubmit() {
  try {
    await authService.forgotPassword(form.value.email)
    message.value = "Password reset link sent to your email!"
  } catch (err) {
    message.value = err.response?.data?.message || "Failed to send reset link"
  }
}
</script>

<style scoped>
.input {
  @apply w-full p-2 border rounded mb-2;
}
.btn-primary {
  @apply bg-orange-600 text-white p-2 rounded hover:bg-orange-700;
}
</style>
