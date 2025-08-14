<template>
  <div class="max-w-4xl mx-auto p-6 space-y-8">
    <!-- Back -->
    <button @click="$router.push('/flashcards')" class="text-blue-600 hover:underline mb-4">
      ← Back to Decks
    </button>

    <!-- Deck Title -->
    <h1 class="text-3xl font-bold text-gray-800">{{ currentDeck?.name }}</h1>

    <!-- Create Flashcard -->
    <form @submit.prevent="addFlashcard" class="bg-white shadow-md rounded-lg p-4 space-y-4">
      <h2 class="text-xl font-semibold">➕ Add Flashcard</h2>
      <input v-model="front" type="text" placeholder="Front text"
        class="w-full border rounded-lg p-2 focus:ring-2 focus:ring-orange-400 focus:outline-none" />
      <input v-model="back" type="text" placeholder="Back text"
        class="w-full border rounded-lg p-2 focus:ring-2 focus:ring-orange-400 focus:outline-none" />
      <button type="submit" class="bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded-lg shadow-md">
        Add
      </button>
    </form>

    <!-- Flashcards List -->
    <div v-if="!currentDeck?.flashcards?.length" class="text-gray-500 italic">
      No flashcards in this deck yet.
    </div>
    <ul v-else class="space-y-2">
      <li v-for="card in currentDeck.flashcards" :key="card.id"
        class="p-3 border rounded-lg bg-white shadow-sm flex justify-between items-center">
        <!-- Edit Mode -->
        <div v-if="editingCardId === card.id" class="flex-1 space-y-2">
          <input v-model="editFront" type="text" class="w-full border rounded-lg p-2" />
          <input v-model="editBack" type="text" class="w-full border rounded-lg p-2" />
          <div class="flex gap-2 mt-2">
            <button @click="saveFlashcard(card.id)"
              class="bg-blue-500 hover:bg-blue-600 text-white px-3 py-1 rounded-lg">
              Save
            </button>
            <button @click="cancelEdit" class="bg-gray-400 hover:bg-gray-500 text-white px-3 py-1 rounded-lg">
              Cancel
            </button>
          </div>
        </div>

        <!-- View Mode -->
        <div v-else class="flex-1">
          <p class="font-semibold">{{ card.front }}</p>
          <p class="text-gray-500">{{ card.back }}</p>
        </div>

        <!-- Action Buttons -->
        <div class="flex gap-2 ml-4">
          <button @click="startEdit(card)" class="bg-yellow-400 hover:bg-yellow-500 text-white px-3 py-1 rounded-lg">
            ✏ Edit
          </button>
          <button @click="openDeleteModal(card.id)" class="bg-red-500 hover:bg-red-600 text-white px-3 py-1 rounded-lg">
            🗑 Delete
          </button>
        </div>
      </li>
    </ul>
  </div>

  <!-- Delete Flashcard Modal -->
  <div v-if="showDeleteModal" class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center px-4">
    <div class="bg-white p-6 rounded-lg shadow-lg w-full max-w-md mx-auto">
      <h2 class="text-xl font-bold mb-4">Delete Flashcard</h2>
      <p class="mb-4">Are you sure you want to delete this flashcard?</p>
      <div class="flex justify-end gap-2">
        <button @click="closeDeleteModal" class="px-4 py-2 border rounded-lg">Cancel</button>
        <button @click="confirmDeleteFlashcard" class="px-4 py-2 bg-red-500 text-white rounded-lg">Delete</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useFlashcardStore } from '../store/flashcardStore'
import { storeToRefs } from 'pinia'

const store = useFlashcardStore()
const { currentDeck } = storeToRefs(store)

const route = useRoute()

// Create form
const front = ref('')
const back = ref('')

// Edit state
const editingCardId = ref(null)
const editFront = ref('')
const editBack = ref('')

const showDeleteModal = ref(false)
const selectedCardId = ref(null)

onMounted(() => {
  store.fetchDeck(route.params.id)
})

// Create
const addFlashcard = async () => {
  if (!front.value.trim() || !back.value.trim()) {
    return alert('Please fill in both fields.')
  }
  await store.addFlashcard(route.params.id, { front: front.value, back: back.value })
  front.value = ''
  back.value = ''
}

// Edit
const startEdit = (card) => {
  editingCardId.value = card.id
  editFront.value = card.front
  editBack.value = card.back
}

const cancelEdit = () => {
  editingCardId.value = null
  editFront.value = ''
  editBack.value = ''
}

const saveFlashcard = async (cardId) => {
  if (!editFront.value.trim() || !editBack.value.trim()) {
    return alert('Please fill in both fields.')
  }
  await store.updateFlashcard(route.params.id, cardId, {
    front: editFront.value,
    back: editBack.value,
  })
  cancelEdit()
}

// Delete
const openDeleteModal = (cardId) => {
  selectedCardId.value = cardId
  showDeleteModal.value = true
}

const closeDeleteModal = () => {
  showDeleteModal.value = false
  selectedCardId.value = null
}

const confirmDeleteFlashcard = async () => {
  try {
    await store.deleteFlashcard(route.params.id, selectedCardId.value)
    console.log('success')
  } catch (error) {
    console.error('Delete error:', error)
  } finally {
    closeDeleteModal()
    store.fetchDeck(route.params.id) // refresh flashcards
  }
}
</script>
