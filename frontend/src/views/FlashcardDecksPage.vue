<template>
  <div class="max-w-4xl mx-auto p-6 space-y-8">
    <!-- Header -->
    <div class="flex justify-between items-center">
      <h1 class="text-3xl font-bold text-gray-800">📚 My Flashcard Decks</h1>
    </div>

    <!-- Create Deck Form -->
    <form @submit.prevent="createDeck" class="flex gap-2">
      <input v-model="newDeckName" type="text" placeholder="New deck name"
        class="flex-1 border rounded-lg p-2 focus:ring-2 focus:ring-orange-400 focus:outline-none" />
      <button type="submit" class="bg-orange-500 hover:bg-orange-600 text-white px-4 py-2 rounded-lg shadow-md">
        ➕ {{ editingDeck ? 'Update' : 'Create' }}
      </button>
    </form>

    <!-- Deck List -->
    <div v-if="decks.length === 0" class="text-gray-500 text-center italic py-10">
      No decks yet — create one above.
    </div>

    <ul v-else class="grid sm:grid-cols-2 gap-4">
      <li v-for="deck in decks" :key="deck.id"
        class="bg-white shadow-md rounded-lg p-4 hover:shadow-lg transition-all duration-200">
        <div class="flex justify-between items-center">
          <div @click="goToDeck(deck.id)" class="cursor-pointer">
            <h2 class="text-lg font-semibold text-orange-600">{{ deck.name }}</h2>
            <p class="text-gray-500 text-sm">{{ deck.flashcards?.length || 0 }} cards</p>
          </div>
          <div class="flex gap-2">
            <button @click.stop="openEditModal(deck)" class="text-blue-500 hover:underline">✏️</button>
            <button @click.stop="openDeleteModal(deck)" class="text-red-500 hover:underline">🗑️</button>
          </div>
        </div>
      </li>
    </ul>

    <!-- Edit Deck Modal -->
    <div v-if="showEditModal" class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center px-4">
      <div class="bg-white p-6 rounded-lg shadow-lg w-full max-w-md mx-auto">
        <h2 class="text-xl font-bold mb-4">Edit Deck</h2>
        <input v-model="editName" type="text" class="border rounded-lg p-2 w-full mb-4" />
        <div class="flex justify-end gap-2">
          <button @click="closeEditModal" class="px-4 py-2 border rounded-lg">Cancel</button>
          <button @click="updateDeckName" class="px-4 py-2 bg-orange-500 text-white rounded-lg">Save</button>
        </div>
      </div>
    </div>

    <!-- Delete Deck Modal -->
    <div v-if="showDeleteModal" class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center px-4">
      <div class="bg-white p-6 rounded-lg shadow-lg w-full max-w-md mx-auto">
        <h2 class="text-xl font-bold mb-4">Delete Deck</h2>
        <p class="mb-4">Are you sure you want to delete <strong>{{ selectedDeck?.name }}</strong>?</p>
        <div class="flex justify-end gap-2">
          <button @click="closeDeleteModal" class="px-4 py-2 border rounded-lg">Cancel</button>
          <button @click="confirmDeleteDeck" class="px-4 py-2 bg-red-500 text-white rounded-lg">Delete</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useFlashcardStore } from '../store/flashcardStore'
import { storeToRefs } from 'pinia'

const store = useFlashcardStore()
const { decks } = storeToRefs(store)
const router = useRouter()

const newDeckName = ref('')
const editingDeck = ref(null)

// Modal state
const showEditModal = ref(false)
const showDeleteModal = ref(false)
const selectedDeck = ref(null)
const editName = ref('')

onMounted(() => {
  store.fetchCurrentDecks()
})

const createDeck = async () => {
  if (!newDeckName.value.trim()) return

  if (editingDeck.value) {
    await store.updateDeck(editingDeck.value.id, { name: newDeckName.value })
    editingDeck.value = null
  } else {
    await store.createDeck({ name: newDeckName.value })
  }

  newDeckName.value = ''
}

const openEditModal = (deck) => {
  selectedDeck.value = deck
  editName.value = deck.name
  showEditModal.value = true
}

const closeEditModal = () => {
  showEditModal.value = false
}

const updateDeckName = async () => {
  if (!editName.value.trim()) return
  await store.updateDeck(selectedDeck.value.id, { name: editName.value })
  closeEditModal()
}

const openDeleteModal = (deck) => {
  selectedDeck.value = deck
  showDeleteModal.value = true
}

const closeDeleteModal = () => {
  showDeleteModal.value = false
}

const confirmDeleteDeck = async () => {
  try {
    await store.deleteDeck(selectedDeck.value.id)
  } catch (error) {
    console.error('Delete error:', error)
    // vẫn reload dù lỗi
  } finally {
    closeDeleteModal()
    console.log('success')
    window.location.reload()
  }
}

const goToDeck = (deckId) => {
  router.push(`/flashcards/${deckId}`)
}
</script>
