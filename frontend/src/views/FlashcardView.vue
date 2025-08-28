<template>
    <div class="max-w-4xl mx-auto p-6 space-y-10">
        <!-- Back -->
        <!-- <button @click="$router.push('/flashcards')"
            class="flex items-center text-blue-600 hover:text-blue-800 transition">
            <span class="mr-1">←</span> Back to Decks
        </button> -->

        <!-- Deck Title -->
        <h1 class="text-4xl font-extrabold text-center text-gray-900 relative">
            {{ currentDeck?.name }}
            <span class="block w-16 h-1 bg-orange-500 mx-auto mt-2 rounded"></span>
        </h1>

        <!-- Flashcards Viewer -->
        <div v-if="!currentDeck?.flashcards?.length" class="text-gray-500 italic text-center">
            No flashcards in this deck yet.
        </div>

        <div v-else class="flex flex-col items-center space-y-6">
            <!-- Flip Card -->
            <div v-if="currentCard" class="flip-card shadow-lg hover:shadow-2xl transition rounded-lg overflow-hidden"
                @click="toggleCard">
                <div class="flip-card-inner" :class="{ flipped: isFlipped }">
                    <div class="flip-card-front flex items-center justify-center text-2xl font-bold bg-white p-6">
                        {{ currentCard.front }}
                    </div>
                    <div class="flip-card-back flex items-center justify-center text-2xl font-bold bg-orange-200 p-6">
                        {{ currentCard.back }}
                    </div>
                </div>
            </div>

            <!-- Navigation -->
            <div class="flex space-x-4">
                <button @click="prevCard" :disabled="cardIndex === 0"
                    class="px-5 py-2 bg-gray-200 rounded-lg hover:bg-gray-300 disabled:opacity-40 disabled:cursor-not-allowed transition">
                    ⬅ Previous
                </button>
                <button @click="nextCard" :disabled="cardIndex === currentDeck.flashcards.length - 1"
                    class="px-5 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-40 disabled:cursor-not-allowed transition">
                    Next ➡
                </button>
            </div>

            <!-- Counter -->
            <div class="text-sm text-gray-600 mt-2 tracking-wide">
                Card <span class="font-semibold">{{ cardIndex + 1 }}</span> of
                <span class="font-semibold">{{ currentDeck.flashcards.length }}</span>
            </div>
        </div>

        <!-- Take Quiz Section -->
        <div class="mt-8 text-center">
            <button @click="showQuizForm = true"
                class="px-6 py-3 bg-orange-500 text-white font-bold rounded-lg hover:bg-orange-600 transition">
                🎯 Take Quiz
            </button>
        </div>

        <!-- Quiz Setup Modal -->
        <div v-if="showQuizForm" class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50">
            <div class="bg-white p-6 rounded-lg shadow-lg w-96 space-y-4">
                <h2 class="text-xl font-bold">Create a Quiz</h2>
                <label class="block text-sm text-gray-600 mb-1">Number of Questions</label>
                <input v-model="numQuestions" type="number" min="1" :max="currentDeck.flashcards.length"
                    class="w-full px-3 py-2 border rounded-lg focus:ring focus:ring-orange-200" />

                <div class="flex justify-end space-x-3">
                    <button @click="showQuizForm = false"
                        class="px-4 py-2 bg-gray-200 rounded hover:bg-gray-300">Cancel</button>
                    <button @click="createQuiz"
                        class="px-4 py-2 bg-orange-500 text-white rounded hover:bg-orange-600">Create Quiz</button>
                </div>
            </div>
        </div>

        <!-- Owner -->
        <div v-if="currentDeck?.userId"
            class="mt-6 text-gray-600 text-sm flex items-center justify-center cursor-pointer hover:text-blue-600 transition"
            @click="goToOwner(currentDeck.userId)">
            <span class="mr-2">👤</span>
            <span>Owner: <span class="font-medium underline">{{ currentDeck.userId }}</span></span>
        </div>

        <!-- All Flashcards -->
        <div v-if="currentDeck?.flashcards?.length" class="mt-10">
            <h2 class="text-2xl font-bold mb-6 text-gray-800">All Flashcards</h2>
            <div class="grid gap-6 sm:grid-cols-2">
                <div v-for="flashcard in currentDeck.flashcards" :key="flashcard.id"
                    class="p-4 border rounded-lg bg-white shadow hover:shadow-md transition">
                    <p class="font-bold text-lg text-gray-800 mb-2">{{ flashcard.front }}</p>
                    <div class="w-full h-px bg-gray-200 my-2"></div>
                    <p class="text-gray-700 text-lg">{{ flashcard.back }}</p>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useFlashcardStore } from '../store/flashcardStore.js'
import { storeToRefs } from 'pinia'
import { useAuthStore } from '../store/authStore.js'
import quizService from '../services/quizService.js'

const store = useFlashcardStore()
const { currentDeck } = storeToRefs(store)
const route = useRoute()
const router = useRouter()

const auth = useAuthStore()
const { user } = storeToRefs(auth)

const cardIndex = ref(0)
const isFlipped = ref(false)

const showQuizForm = ref(false)
const numQuestions = ref(5)

onMounted(() => {
    store.fetchDeck(route.params.id)
})

const currentCard = computed(() => {
    return currentDeck.value?.flashcards?.[cardIndex.value] || null
})

const toggleCard = () => {
    isFlipped.value = !isFlipped.value
}

const nextCard = () => {
    if (cardIndex.value < currentDeck.value.flashcards.length - 1) {
        cardIndex.value++
        isFlipped.value = false
    }
}

const prevCard = () => {
    if (cardIndex.value > 0) {
        cardIndex.value--
        isFlipped.value = false
    }
}

const goToOwner = (userId) => {
    router.push(`/users/${userId}/decks`)
}

const createQuiz = async () => {
  const quiz = await quizService.generateQuiz(currentDeck.value.id, numQuestions.value)
  router.push({ name: 'QuizView', params: { quizId: quiz.id } })
}
</script>

<style scoped>
.flip-card {
    cursor: pointer;
    width: 100%;
    max-width: 420px;
    aspect-ratio: 1 / 1;
    perspective: 1000px;
}

.flip-card-inner {
    position: relative;
    width: 100%;
    height: 100%;
    transform-style: preserve-3d;
    transition: transform 0.6s ease-in-out;
}

.flip-card-inner.flipped {
    transform: rotateY(180deg);
}

.flip-card-front,
.flip-card-back {
    position: absolute;
    width: 100%;
    height: 100%;
    backface-visibility: hidden;
    border-radius: 0.75rem;
}

.flip-card-back {
    transform: rotateY(180deg);
}
</style>
