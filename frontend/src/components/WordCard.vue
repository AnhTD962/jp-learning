<template>
  <div v-if="word" class="max-w-2xl mx-auto bg-white border border-gray-200 rounded-lg shadow-lg p-6 space-y-4">

    <!-- Word Title -->
    <div class="text-center">
      <h3 class="text-4xl font-bold text-gray-800">{{ word.japanese }}</h3>
    </div>

    <!-- Vietnamese Translation -->
    <p class="text-lg text-gray-700">
      <span class="font-semibold">Vietnamese:</span> {{ word.vietnamese }}
    </p>

    <!-- On & Kun readings -->
    <div class="flex flex-wrap gap-4">
      <span v-if="word.on" class="bg-red-100 text-red-600 px-3 py-1 rounded-full text-sm font-medium">
        On: {{ word.on }}
      </span>
      <span v-if="word.kun" class="bg-green-100 text-green-600 px-3 py-1 rounded-full text-sm font-medium">
        Kun: {{ word.kun }}
      </span>
    </div>

    <!-- Kanji Stroke Order (KanjiVG) -->
    <div v-if="svgContent" class="flex justify-center">
      <div v-html="svgContent" class="w-40 kanji-svg"></div>
    </div>
    <p v-else class="text-center text-gray-400">Loading stroke order...</p>

    <!-- Example sentence with Furigana -->
    <div v-if="word.exampleSentence" class="bg-gray-50 p-4 rounded-lg border border-gray-200">
      <p class="text-sm font-semibold text-gray-500 mb-1">Example</p>
      <p class="text-xl leading-relaxed" v-html="word.exampleSentence"></p>
    </div>

    <!-- Example translation -->
    <div v-if="word.exampleSentenceTranslation" class="bg-yellow-50 p-4 rounded-lg border border-yellow-200">
      <p class="text-sm font-semibold text-yellow-700 mb-1">Meaning</p>
      <p class="text-gray-800">{{ word.exampleSentenceTranslation }}</p>
    </div>

    <!-- Action buttons -->
    <div v-if="canEdit" class="flex justify-end gap-3 pt-4 border-t border-gray-200">
      <button @click="router.push(`/words`)"
        class="px-4 py-2 rounded bg-green-500 text-white hover:bg-green-600 transition">
        Forward
      </button>
      <button @click="router.push({ name: 'WordEdit', params: { id: word.id } })"
        class="px-4 py-2 rounded bg-blue-500 text-white hover:bg-blue-600 transition">
        Edit
      </button>
      <button @click="showDeleteConfirm = true"
        class="px-4 py-2 rounded bg-red-500 text-white hover:bg-red-600 transition">
        Delete
      </button>
    </div>
  </div>

  <!-- Loading state -->
  <p v-else class="text-center text-gray-500 py-10">Loading...</p>

  <!-- Delete Confirmation Modal -->
  <div v-if="showDeleteConfirm" class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
    <div class="bg-white rounded-lg shadow-lg p-6 w-80 space-y-4">
      <h3 class="text-lg font-bold text-gray-800">Confirm Delete</h3>
      <p class="text-gray-600">Are you sure you want to delete <b>{{ word?.japanese }}</b>?</p>
      <div class="flex justify-end gap-3">
        <button @click="showDeleteConfirm = false"
          class="px-4 py-2 rounded bg-gray-300 hover:bg-gray-400 transition">
          Cancel
        </button>
        <button @click="confirmDelete"
          class="px-4 py-2 rounded bg-red-500 text-white hover:bg-red-600 transition">
          Yes, Delete
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import wordService from '../services/wordService'

const route = useRoute()
const router = useRouter()

const word = ref(null)
const canEdit = ref(true) // change based on role
const svgContent = ref(null)
const showDeleteConfirm = ref(false)

function getRandomColor() {
  const letters = '0123456789ABCDEF'
  let color = '#'
  for (let i = 0; i < 6; i++) {
    color += letters[Math.floor(Math.random() * 16)]
  }
  return color
}

async function loadKanjiVG(kanjiChar) {
  try {
    const codePoint = kanjiChar.codePointAt(0).toString(16).padStart(5, '0')
    const url = `https://raw.githubusercontent.com/KanjiVG/kanjivg/master/kanji/${codePoint}.svg`
    const res = await fetch(url)
    svgContent.value = await res.text()

    // Wait for DOM to update with the SVG
    await nextTick()

    // Apply random colors to each stroke path
    const paths = document.querySelectorAll('.kanji-svg path')
    paths.forEach((path, index) => {
      const randomColor = getRandomColor()
      path.setAttribute('stroke', randomColor)
      path.style.strokeDasharray = path.getTotalLength()
      path.style.strokeDashoffset = path.getTotalLength()
      path.style.animation = `drawStroke 0.8s ease forwards`
      path.style.animationDelay = `${index * 0.8}s`
    })
  } catch (err) {
    console.error('Error loading KanjiVG:', err)
  }
}

async function confirmDelete() {
  try {
    await wordService.deleteWord(word.value.id)
    showDeleteConfirm.value = false
    router.push('/words')
  } catch (err) {
    console.error('Delete failed:', err)
  }
}

onMounted(async () => {
  try {
    word.value = await wordService.getWordById(route.params.id)
    if (word.value && word.value.japanese) {
      loadKanjiVG(word.value.japanese[0]) // only first kanji
    }
  } catch (err) {
    console.error('Error loading word:', err)
    router.push('/')
  }
})
</script>

<style>
.kanji-svg svg {
  width: 100%;
  height: auto;
  fill: none;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
  background-image: url('../assets/whitebackground.png');
  /* example squared paper */
  background-size: contain;
  background-repeat: no-repeat;
  background-position: center;
}

@keyframes drawStroke {
  to {
    stroke-dashoffset: 0;
  }
}
</style>
