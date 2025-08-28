<template>
  <div class="max-w-3xl mx-auto p-6">
    <h1 class="text-3xl font-bold text-center mb-6">Quiz</h1>

    <!-- Quiz in progress -->
    <div v-if="quiz && !quiz.completed">
      <div
        v-for="(q, index) in quiz.questionsSnapshot"
        :key="q.flashcardId"
        class="mb-6 p-4 border rounded-lg bg-white shadow"
      >
        <p class="font-semibold mb-3">Question {{ index + 1 }}</p>
        <div class="space-y-2">
          <button
            v-for="opt in q.options"
            :key="opt"
            @click="selectAnswer(q.flashcardId, opt)"
            class="w-full px-4 py-2 border rounded hover:bg-orange-100"
            :class="answers[q.flashcardId] === opt ? 'bg-orange-200' : ''"
          >
            {{ opt }}
          </button>
        </div>
      </div>

      <button
        @click="submitQuiz"
        class="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
      >
        Submit Quiz
      </button>
    </div>

    <!-- Quiz completed -->
    <div v-else-if="quiz?.completed" class="text-center">
      <h2 class="text-2xl font-bold mb-4">Your Score: {{ quiz.score }}</h2>
      <ul class="space-y-2 text-left max-w-md mx-auto">
        <li v-for="res in quiz.questionResults" :key="res.flashcardId">
          <span class="font-semibold">Your Answer:</span> {{ res.userAnswer }} |
          <span class="font-semibold">Correct:</span> {{ res.correctAnswer }}
          <span v-if="res.correct" class="text-green-600 ml-2">✔</span>
          <span v-else class="text-red-600 ml-2">✘</span>
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
const quiz = ref(null)
const answers = ref({})

onMounted(async () => {
  quiz.value = await quizService.startAttempt(route.params.quizId)
})

const selectAnswer = (flashcardId, answer) => {
  answers.value[flashcardId] = answer
}

const submitQuiz = async () => {
  quiz.value = await quizService.submitAttempt(
    quiz.value.attemptId,
    answers.value
  )
}
</script>
