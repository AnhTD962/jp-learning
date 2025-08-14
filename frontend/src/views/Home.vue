<template>
  <div class="p-4 space-y-10 max-w-6xl mx-auto">
    <!-- DECKS -->
    <section>
      <h2 class="text-2xl font-semibold mb-4">📚 Decks</h2>
      <div class="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
        <div
          v-for="deck in decks"
          :key="deck.id"
          class="p-4 bg-white rounded shadow cursor-pointer"
          @click="$router.push(`/flashcards/${deck.id}`)"
        >
          <h3 class="font-bold">{{ deck.name }}</h3>
          <p class="text-sm text-gray-600">
            {{ deck.flashcards.length }} flashcards
          </p>
          <p class="text-xs text-gray-500 mt-2">Made by: {{ deck.userId }}</p>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import flashcardService from '../services/flashcardService.js'

const decks = ref([])

onMounted(async () => {
  decks.value = await flashcardService.getDecks()
})
</script>
