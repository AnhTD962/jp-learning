import { defineStore } from 'pinia'
import quizService from '../services/quizService'

export const useQuizStore = defineStore('quiz', {
    state: () => ({
        currentQuiz: null,
        quizResult: null,
        isLoading: false,
        error: null,
    }),
    actions: {
        async generateQuiz(deckId, numQuestions = 10) {
            this.currentQuiz = await quizService.generateQuiz(deckId, numQuestions)
        },
        async submitQuiz(quizId, answers) {
            this.quizResult = await quizService.submitQuiz(quizId, answers)
        },
        async fetchQuizResult(quizId) {
            this.quizResult = await quizService.getQuizResult(quizId)
        },
    },
})
