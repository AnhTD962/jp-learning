<template>
  <form @submit.prevent="onSubmit" class="bg-white shadow-md rounded p-6 max-w-sm mx-auto">
    <h2 class="text-xl font-bold mb-4">Register</h2>

    <input v-model="form.username" placeholder="Username" class="input" required />
    <input v-model="form.email" type="email" placeholder="Email" class="input" required />
    <input v-model="form.password" type="password" placeholder="Password" class="input" required />

    <p v-if="error" :class="['text-sm mt-2', error.includes('created') ? 'text-green-600' : 'text-red-500']">
      {{ error }}
    </p>

    <button type="submit" class="btn-primary w-full mt-4">Register</button>

    <div class="mt-4 text-center">
      <p>
        Already have an account?
        <button type="button" class="text-blue-600 underline" @click="$emit('switch-to-login')">
          Login
        </button>
      </p>
    </div>
  </form>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({ error: String })
const emit = defineEmits(['submit', 'switch-to-login'])

const form = ref({ username: '', email: '', password: '' })

function onSubmit() {
  emit('submit', form.value)
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
