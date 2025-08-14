<template>
  <div class="p-4">
    <QuizQuestion
        v-if="quiz && currentQuestionIndex < quiz.questions.length"
        :question="quiz.questions[currentQuestionIndex]"
        @answer-selected="submitAnswer"
    />
    <router-link
        v-else
        to="/quiz-result"
        class="btn-primary mt-4 inline-block"
    >
      View Results
    </router-link>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useQuizStore } from '../store/quizStore'
import QuizQuestion from '../components/QuizQuestion.vue'

const store = useQuizStore()
const quiz = store.currentQuiz
const currentQuestionIndex = ref(0)
const userAnswers = ref([])

onMounted(() => {
  if (!quiz) store.generateQuiz(1, 5) // Default deckId for demo
})

const submitAnswer = (answer) => {
  userAnswers.value.push(answer)
  currentQuestionIndex.value++
  if (currentQuestionIndex.value >= quiz.questions.length) {
    store.submitQuiz(quiz.id, userAnswers.value)
  }
}
</script>
