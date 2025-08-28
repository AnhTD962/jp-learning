<template>
  <div class="max-w-3xl mx-auto p-6">
    <h1 class="text-2xl font-bold text-center mb-6">Quiz Result</h1>

    <div v-if="result">
      <p class="text-lg mb-4">Score: {{ result.score }}</p>
      <ul class="space-y-2">
        <li v-for="r in result.questionResults" :key="r.flashcardId">
          Q: {{ r.flashcardId }} -
          <span :class="r.correct ? 'text-green-600' : 'text-red-600'">
            {{ r.userAnswer }} (correct: {{ r.correctAnswer }})
          </span>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import quizService from '../services/quizService.js'

const route = useRoute()
const result = ref(null)

onMounted(async () => {
  result.value = await quizService.getAttemptResult(route.params.attemptId)
})
</script>
