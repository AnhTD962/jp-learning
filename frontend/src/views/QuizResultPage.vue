<template>
  <div class="p-4">
    <QuizResultSummary :result="result" />
    <router-link to="/" class="btn-primary mt-4 inline-block">Back to Dashboard</router-link>
  </div>
</template>

<script setup>
import { useQuizStore } from '../store/quizStore'
import QuizResultSummary from '../components/QuizResultSummary.vue'

const store = useQuizStore()
const result = store.quizResult
</script>
