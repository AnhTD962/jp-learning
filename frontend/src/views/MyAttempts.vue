<template>
  <div class="max-w-4xl mx-auto p-6">
    <h1 class="text-2xl font-bold mb-6">My Quiz Attempts</h1>
    <div v-if="!attempts.length" class="text-gray-500">No attempts yet.</div>
    <ul class="space-y-4">
      <li v-for="a in attempts" :key="a.attemptId"
        class="p-4 border rounded shadow hover:bg-gray-50 cursor-pointer"
        @click="goToResult(a.attemptId)">
        <p class="font-semibold">Quiz: {{ a.deckName }}</p>
        <p>Score: {{ a.score ?? 'In progress' }}</p>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import quizService from '../services/quizService.js'
import { useRouter } from 'vue-router'

const attempts = ref([])
const router = useRouter()

onMounted(async () => {
  attempts.value = await quizService.getMyAttempts()
})

const goToResult = (id) => {
  router.push({ path: `/attempts/${id}/result` })
}
</script>
