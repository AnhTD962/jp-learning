<template>
    <div v-if="word" class="max-w-2xl mx-auto bg-white p-6 rounded shadow space-y-4">
        <h2 class="text-2xl font-bold text-gray-800">Edit Word</h2>

        <!-- Type -->
        <div>
            <label class="block text-sm font-medium text-gray-700">Type</label>
            <input v-model="word.type" class="w-full p-2 border rounded" readonly />
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

        <!-- SubType select (only for Alphabet) -->
        <div v-if="word.type === 'alphabet'">
            <label class="block text-sm font-medium text-gray-700">SubType</label>
            <select v-model="word.subType" class="w-full p-2 border rounded">
                <option value="">-- Select SubType --</option>
                <option value="hiragana">Hiragana</option>
                <option value="katakana">Katakana</option>
            </select>
        </div>

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
            <button @click="updateWord" class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">
                Save Changes
            </button>

            <button @click="router.push(`/word/${route.params.id}`)"
                class="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600">
                Cancel
            </button>
        </div>
    </div>

    <p v-else class="text-center text-gray-500 py-10">Loading...</p>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import wordService from '../services/wordService'

const route = useRoute()
const router = useRouter()
const word = ref(null)

onMounted(async () => {
    try {
        word.value = await wordService.getWordById(route.params.id)
    } catch (err) {
        console.error('Error loading word:', err)
        router.push(`/word/${route.params.id}`)
    }
})

async function updateWord() {
    try {
        await wordService.updateWord(route.params.id, word.value)
        alert('Word updated successfully!')
        router.push(`/word/${route.params.id}`)
    } catch (err) {
        console.error('Error updating word:', err)
        alert('Failed to update word.')
    }
}
</script>
