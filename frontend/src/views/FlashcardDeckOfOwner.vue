<template>
  <div class="max-w-4xl mx-auto p-6 space-y-8">
    <h1 class="text-3xl font-bold text-gray-800">📚 Decks by {{ ownerName }}</h1>

    <div v-if="!decks || decks.length === 0" class="text-gray-500 italic text-center py-10">
      No decks yet.
    </div>

    <ul v-else class="grid sm:grid-cols-2 gap-4">
      <li v-for="deck in decks" :key="deck.id"
        class="bg-white shadow-md rounded-lg p-4 hover:shadow-lg transition-all duration-200">
        <div @click="goToDeck(deck.id)" class="cursor-pointer">
          <h2 class="text-lg font-semibold text-orange-600">{{ deck.name }}</h2>
          <p class="text-gray-500 text-sm">{{ deck.flashcards?.length || 0 }} cards</p>
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import flashcardService from '../services/flashcardService.js'

const route = useRoute()
const router = useRouter()

const ownerName = ref('')
const decks = ref([])

onMounted(async () => {
  const userId = route.params.userId
  const res = await flashcardService.getDecksByUserId(userId)

  // res is the array of decks
  decks.value = res || []
  ownerName.value = userId // or fetch separately if you want display name
})

const goToDeck = (deckId) => {
  router.push(`/flashcards/view/${deckId}`)
}
</script>
