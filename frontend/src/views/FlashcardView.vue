<template>
    <div class="max-w-4xl mx-auto p-6 space-y-8">
        <!-- Back -->
        <button @click="$router.push('/flashcards')" class="text-blue-600 hover:underline mb-4">
            ← Back to Decks
        </button>

        <!-- Deck Title -->
        <h1 class="text-3xl font-bold text-gray-800">{{ currentDeck?.name }}</h1>

        <!-- Flashcards Viewer -->
        <div v-if="!currentDeck?.flashcards?.length" class="text-gray-500 italic">
            No flashcards in this deck yet.
        </div>

        <div v-else class="flex flex-col items-center space-y-4">
            <div
                v-if="currentCard"
                class="flip-card"
                @click="toggleCard"
            >
                <div class="flip-card-inner" :class="{ flipped: isFlipped }">
                    <div class="flip-card-front p-4 bg-white rounded shadow flex items-center justify-center text-xl font-bold">
                        {{ currentCard.front }}
                    </div>
                    <div class="flip-card-back p-4 bg-orange-100 rounded shadow flex items-center justify-center text-xl font-bold">
                        {{ currentCard.back }}
                    </div>
                </div>
            </div>
            <div class="flex space-x-2 mt-4">
                <button
                    @click="prevCard"
                    :disabled="cardIndex === 0"
                    class="px-4 py-2 bg-gray-200 rounded hover:bg-gray-300 disabled:opacity-50"
                >
                    Previous
                </button>
                <button
                    @click="nextCard"
                    :disabled="cardIndex === currentDeck.flashcards.length - 1"
                    class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50"
                >
                    Next
                </button>
            </div>
            <div class="text-sm text-gray-500 mt-2">
                Card {{ cardIndex + 1 }} of {{ currentDeck.flashcards.length }}
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useFlashcardStore } from '../store/flashcardStore.js'
import { storeToRefs } from 'pinia'

const store = useFlashcardStore()
const { currentDeck } = storeToRefs(store)
const route = useRoute()

const cardIndex = ref(0)
const isFlipped = ref(false)

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
</script>

<style scoped>
.flip-card {
    cursor: pointer;
    width: 100%;
    max-width: 400px;
    aspect-ratio: 1 / 1;
}
.flip-card-inner {
    position: relative;
    width: 100%;
    height: 100%;
    transform-style: preserve-3d;
    transition: transform 0.6s;
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
    border-radius: 0.5rem;
}
.flip-card-back {
    transform: rotateY(180deg);
}
</style>
