<template>
  <div class="max-w-3xl mx-auto p-6">
    <h1 class="text-2xl font-bold text-center mb-6">Quiz Result</h1>

    <div v-if="result">
      <p class="text-lg font-semibold mb-6 text-center">Score: {{ result.score }}</p>

      <ul class="space-y-4">
        <li
          v-for="r in result.questionResults"
          :key="r.flashcardId"
          class="p-4 border rounded-lg bg-white shadow"
        >
          <p class="font-semibold mb-2">Q: {{ r.questionText }}</p>

          <!-- Câu trả lời của user -->
          <p>
            <span class="font-semibold">Your Answer:</span>
            <span :class="r.correct ? 'text-green-600' : 'text-red-600'">
              {{ r.userAnswer }}
            </span>
            <span v-if="r.correct" class="text-green-600 ml-2">✔</span>
            <span v-else class="text-red-600 ml-2">✘</span>
          </p>

          <!-- Nếu sai thì hiện thêm đáp án đúng -->
          <p v-if="!r.correct" class="mt-2">
            <span class="font-semibold">Correct Answer:</span>
            <span class="text-green-600">{{ r.correctAnswer }}</span>
          </p>
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
