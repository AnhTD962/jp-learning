<template>
  <div class="max-w-2xl mx-auto bg-white p-6 rounded shadow space-y-4">
    <h2 class="text-2xl font-bold text-gray-800">Create Word</h2>

    <!-- Type -->
    <div>
      <label class="block text-sm font-medium text-gray-700">Type</label>
      <select v-model="word.type" class="w-full p-2 border rounded">
        <option value="">-- Select Type --</option>
        <option value="kanji">Kanji</option>
        <option value="alphabet">Alphabet</option>
      </select>
    </div>

    <!-- SubType select (only for Alphabet) -->
    <div v-if="word.type === 'alphabet'">
      <label class="block text-sm font-medium text-gray-700">SubType</label>
      <select v-model="word.subType" class="w-full p-2 border rounded">
        <option value="">-- Select SubType --</option>
        <option value="hiragana">Hiragana</option>
        <option value="katakana">Katakana</option>
      </select>
    </div>

    <!-- Japanese -->
    <div>
      <label class="block text-sm font-medium text-gray-700">Japanese</label>
      <input v-model="word.japanese" class="w-full p-2 border rounded" />
    </div>

    <!-- Vietnamese -->
    <div>
      <label class="block text-sm font-medium text-gray-700">Vietnamese</label>
      <input v-model="word.vietnamese" class="w-full p-2 border rounded" />
    </div>

    <!-- On & Kun readings (only for Kanji) -->
    <template v-if="word.type === 'kanji'">
      <div>
        <label class="block text-sm font-medium text-gray-700">On reading</label>
        <input v-model="word.on" class="w-full p-2 border rounded" />
      </div>

      <div>
        <label class="block text-sm font-medium text-gray-700">Kun reading</label>
        <input v-model="word.kun" class="w-full p-2 border rounded" />
      </div>
    </template>

    <!-- Example Sentence -->
    <div>
      <label class="block text-sm font-medium text-gray-700">Example Sentence (with Furigana HTML)</label>
      <textarea v-model="word.exampleSentence" class="w-full p-2 border rounded"></textarea>
    </div>

    <!-- Example Translation -->
    <div>
      <label class="block text-sm font-medium text-gray-700">Example Sentence Translation</label>
      <textarea v-model="word.exampleSentenceTranslation" class="w-full p-2 border rounded"></textarea>
    </div>

    <!-- Action buttons -->
    <div class="flex justify-between pt-4 border-t">
      <button @click="createWord" class="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600">
        Create Word
      </button>

      <button @click="router.push('/words')" class="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600">
        Cancel
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import wordService from '../services/wordService'

const router = useRouter()

const word = ref({
  type: '',
  japanese: '',
  vietnamese: '',
  on: '',
  kun: '',
  subType: '',
  exampleSentence: '',
  exampleSentenceTranslation: ''
})

async function createWord() {
  try {
    await wordService.createWord(word.value)
    alert('Word created successfully!')
    router.push('/words')
  } catch (err) {
    console.error('Error creating word:', err)
    alert('Failed to create word.')
  }
}
</script>
