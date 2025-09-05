<template>
  <form @submit.prevent="onSubmit" class="bg-white shadow-md rounded p-6 max-w-sm mx-auto">
    <h2 class="text-xl font-bold mb-4">Login</h2>

    <input v-model="form.username" placeholder="Username" class="input" required />
    <input v-model="form.password" type="password" placeholder="Password" class="input" required />

    <p v-if="error" :class="['text-sm mt-2', error.includes('created') ? 'text-green-600' : 'text-red-500']">
      {{ error }}
    </p>

    <button type="submit" class="btn-primary w-full mt-4">Login</button>

    <div class="mt-4 text-center">
      <button type="button" class="text-sm text-orange-600" @click="$emit('switch-to-forgot')">
        Forgot Password?
      </button>
      <p class="mt-2">
        Don't have an account?
        <button type="button" class="text-blue-600 underline" @click="$emit('switch-to-register')">
          Register
        </button>
      </p>
    </div>
  </form>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({ error: String })
const emit = defineEmits(['submit', 'switch-to-register', 'switch-to-forgot'])

const form = ref({ username: '', password: '' })

function onSubmit() {
  emit('submit', { username: form.value.username, password: form.value.password })
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
