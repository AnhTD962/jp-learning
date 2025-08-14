<template>
  <div class="max-w-4xl mx-auto p-6">
    <h1 class="text-3xl font-bold mb-6 text-center">Word Viewer</h1>

    <div class="flex justify-end mb-4" v-if="auth.isLoggedIn">
      <router-link to="/word/create"
        class="bg-indigo-600 text-white px-4 py-2 rounded font-semibold hover:bg-indigo-700 transition">
        + Create Word
      </router-link>
    </div>

    <!-- Main Selection -->
    <div class="flex justify-center gap-4 mb-6">
      <button @click="selectKanji"
        :class="['px-4 py-2 rounded font-semibold transition', selectedType === 'kanji' ? 'bg-red-500 text-white' : 'bg-gray-200 hover:bg-gray-300']">
        Kanji
      </button>
      <button @click="selectAlphabet"
        :class="['px-4 py-2 rounded font-semibold transition', selectedType === 'alphabet' ? 'bg-blue-500 text-white' : 'bg-gray-200 hover:bg-gray-300']">
        Alphabet
      </button>
    </div>

    <!-- Alphabet Sub Selection -->
    <div v-if="selectedType === 'alphabet'" class="flex justify-center gap-4 mb-6">
      <button @click="selectedSubType = 'hiragana'"
        :class="['px-4 py-2 rounded font-semibold transition', selectedSubType === 'hiragana' ? 'bg-green-500 text-white' : 'bg-gray-200 hover:bg-gray-300']">
        Hiragana
      </button>
      <button @click="selectedSubType = 'katakana'"
        :class="['px-4 py-2 rounded font-semibold transition', selectedSubType === 'katakana' ? 'bg-purple-500 text-white' : 'bg-gray-200 hover:bg-gray-300']">
        Katakana
      </button>
    </div>

    <!-- Word Grid -->
    <div v-if="displayWords.length" class="grid grid-cols-5 md:grid-cols-5 gap-4">
      <template v-for="(word, index) in displayWords" :key="word.id || index">
        <!-- Clickable only if japanese exists -->
        <router-link v-if="word.japanese" :to="`/word/${word.id}`"
          class="bg-white shadow rounded p-6 text-center hover:shadow-lg transition block">
          <span class="text-3xl font-bold">{{ word.japanese }}</span>
          <p class="text-gray-600 mt-2">{{ word.vietnamese }}</p>
        </router-link>

        <!-- Non-clickable placeholder -->
        <div v-else class="bg-gray-100 rounded p-6"></div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useAuthStore } from '../store/authStore'
import wordService from "../services/wordService"; // <-- adjust path if needed

const auth = useAuthStore();
const kanji = ref([]);
const alphabet = {
  hiragana: ref([]),
  katakana: ref([])
};

// UI State
const selectedType = ref("kanji");
const selectedSubType = ref("");

// Fetch data from DB
async function fetchData() {
  kanji.value = await wordService.getWordByType("kanji");
  alphabet.hiragana.value = await wordService.getWordByTypeAndSubType("alphabet", "hiragana");
  alphabet.katakana.value = await wordService.getWordByTypeAndSubType("alphabet", "katakana");
}

// Actions
function selectKanji() {
  selectedType.value = "kanji";
  selectedSubType.value = "";
}

function selectAlphabet() {
  selectedType.value = "alphabet";
  if (!selectedSubType.value) {
    selectedSubType.value = "hiragana"; // default
  }
}

// Computed
const displayWords = computed(() => {
  if (selectedType.value === "kanji") {
    return kanji.value;
  }
  if (selectedType.value === "alphabet") {
    return alphabet[selectedSubType.value].value || [];
  }
  return [];
});

// Load data when page opens
onMounted(fetchData);
</script>
