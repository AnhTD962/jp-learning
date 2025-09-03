<template>
  <div class="max-w-3xl mx-auto p-6">
    <h1 class="text-3xl font-bold text-center mb-6">Quiz</h1>

    <!-- Quiz in progress -->
    <div v-if="quiz && !showResult">
      <div v-for="(q, index) in quiz.questionsSnapshot" :key="q.flashcardId"
        class="mb-6 p-4 border rounded-lg bg-white shadow">
        <p class="font-semibold mb-3">Question {{ index + 1 }}: {{ q.questionText }}</p>
        <div class="space-y-2">
          <button v-for="opt in q.options" :key="opt" @click="selectAnswer(q.questionId, opt)"
            class="w-full px-4 py-2 border rounded hover:bg-orange-100"
            :class="answers[q.questionId] === opt ? 'bg-orange-200' : ''">
            {{ opt }}
          </button>
        </div>
      </div>

      <button @click="openConfirmModal" class="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700">
        Submit Quiz
      </button>
    </div>

    <!-- Quiz completed -->
    <div v-else-if="showResult" class="text-center">
      <h2 class="text-2xl font-bold mb-4">Your Score: {{ quiz.score }}</h2>
      <ul class="space-y-2 text-left max-w-md mx-auto">
        <li v-for="res in quiz.questionResults" :key="res.flashcardId">
          <span class="font-semibold">Your Answer:</span> {{ res.userAnswer }} |
          <span class="font-semibold">Correct:</span> {{ res.correctAnswer }}
          <span v-if="res.correct" class="text-green-600 ml-2">✔</span>
          <span v-else class="text-red-600 ml-2">✘</span>
        </li>
      </ul>
      <div class="flex justify-center gap-4">
        <button @click="restartQuiz" class="px-4 py-2 bg-orange-500 text-white rounded hover:bg-orange-600">
          Làm lại
        </button>
        <button @click="goBack" class="px-4 py-2 border rounded hover:bg-gray-100">
          Trở về
        </button>
      </div>
    </div>

    <!-- Confirm Modal -->
    <div v-if="showModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
      <div class="bg-white rounded-lg shadow-lg p-6 max-w-sm w-full">
        <h3 class="text-lg font-semibold mb-4">Confirm Submit</h3>
        <p class="mb-6">Are you sure you want to submit your quiz?</p>
        <div class="flex justify-end space-x-3">
          <button @click="showModal = false" class="px-4 py-2 border rounded hover:bg-gray-100">
            No
          </button>
          <button @click="confirmSubmit" class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700">
            Yes
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import quizService from '../services/quizService.js'

const route = useRoute()
const router = useRouter()
const quiz = ref(null)
const answers = ref({})
const showModal = ref(false)
const showResult = ref(false)
let started = false  

onMounted(async () => {
  if (started) return
  started = true
  quiz.value = await quizService.startAttempt(route.params.quizId)
})

const selectAnswer = (questionId, answer) => {
  answers.value[questionId] = answer
}

const openConfirmModal = () => {
  showModal.value = true
}

const confirmSubmit = async () => {
  showModal.value = false
  quiz.value = await quizService.submitAttempt(
    quiz.value.attemptId,
    answers.value
  )
  showResult.value = true
}

const restartQuiz = async () => {
  answers.value = {}
  quiz.value = await quizService.startAttempt(route.params.quizId)
  showResult.value = false
}

const goBack = () => {
  router.back() // hoặc router.push('/quizzes') nếu có route danh sách quiz
}
</script>
