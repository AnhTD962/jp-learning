<template>
  <form @submit.prevent="onSubmit" class="bg-white shadow-md rounded p-6 max-w-sm mx-auto mt-12">
    <input v-model="form.username" placeholder="Username" class="input" required />
    <input v-model="form.password" placeholder="Password" type="password" class="input" required />
    <p v-if="error" :class="['text-sm mt-2', error.includes('created') ? 'text-green-600' : 'text-red-500']">
      {{ error }}
    </p>
    <button type="submit" class="btn-primary w-full mt-4">Login</button>
  </form>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({ error: String })
const emit = defineEmits(['submit'])

const form = ref({
  username: '',
  password: ''
})

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