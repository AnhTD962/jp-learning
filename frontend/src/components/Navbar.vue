<template>
  <nav class="bg-white shadow-md p-4 flex justify-between items-center">
    <div class="flex items-center gap-4">
      <!-- Mobile hamburger -->
      <button class="md:hidden text-xl" @click="$emit('toggle-sidebar')">☰</button>
      <router-link to="/" class="text-xl font-bold text-orange-600">JapaneseApp</router-link>
    </div>

    <div class="flex items-center gap-4">
      <!-- 🔍 Search box -->
      <div class="relative" ref="searchWrapper">
        <input 
          v-model="keyword" 
          type="text" 
          placeholder="Search..."
          class="border border-gray-300 rounded px-2 py-1 hidden sm:block" 
        />

        <!-- Dropdown kết quả -->
        <transition name="fade-slide">
          <div 
            v-if="results && (results.words.length > 0 || results.decks.length > 0)" 
            class="absolute mt-1 w-64 bg-white shadow-lg rounded border z-50"
          >
            <!-- Words -->
            <div v-if="results.words.length && (expandedSection === null || expandedSection === 'words')">
              <h4 class="px-3 py-2 font-semibold bg-gray-100 flex justify-between items-center">
                <span>Words</span>
                <button v-if="results.words.length > 5 && expandedSection !== 'words'"
                        @click.stop="expandedSection = 'words'" 
                        class="text-blue-600 text-sm hover:underline">
                  Xem thêm
                </button>
                <button v-if="expandedSection === 'words'" 
                        @click.stop="expandedSection = null"
                        class="text-red-600 text-sm hover:underline">
                  Thu gọn
                </button>
              </h4>
              <ul>
                <li v-for="w in expandedSection === 'words' ? results.words : results.words.slice(0, 5)" :key="w.id">
                  <router-link 
                    :to="`/word/${w.id}`" 
                    class="block px-3 py-1 hover:bg-gray-50 cursor-pointer"
                    @click.native="closeDropdown"
                  >
                    {{ w.japanese }} - {{ w.vietnamese }}
                  </router-link>
                </li>
              </ul>
            </div>

            <!-- Decks -->
            <div v-if="results.decks.length && (expandedSection === null || expandedSection === 'decks')">
              <h4 class="px-3 py-2 font-semibold bg-gray-100 flex justify-between items-center">
                <span>Flashcard Decks</span>
                <button v-if="results.decks.length > 5 && expandedSection !== 'decks'"
                        @click.stop="expandedSection = 'decks'" 
                        class="text-blue-600 text-sm hover:underline">
                  Xem thêm
                </button>
                <button v-if="expandedSection === 'decks'" 
                        @click.stop="expandedSection = null"
                        class="text-red-600 text-sm hover:underline">
                  Thu gọn
                </button>
              </h4>
              <ul>
                <li v-for="d in expandedSection === 'decks' ? results.decks : results.decks.slice(0, 5)" :key="d.id">
                  <router-link 
                    :to="`/flashcards/view/${d.id}`" 
                    class="block px-3 py-1 hover:bg-gray-50 cursor-pointer"
                    @click.native="closeDropdown"
                  >
                    {{ d.name }}
                  </router-link>
                </li>
              </ul>
            </div>
          </div>
        </transition>
      </div>

      <!-- 👤 Auth -->
      <div v-if="isLoggedIn" class="relative" ref="authWrapper">
        <button @click="showDropdown = !showDropdown" class="ml-2 text-gray-700">
          {{ user?.username }} ⬇
        </button>
        <div v-if="showDropdown" class="absolute right-0 mt-2 w-40 bg-white shadow rounded z-50 border">
          <router-link to="/profile" class="block px-4 py-2 hover:bg-gray-100 w-full text-left">
            👤 Profile
          </router-link>
          <router-link to="/change-password" class="block px-4 py-2 hover:bg-gray-100 w-full text-left">
            🔑 Change Password
          </router-link>
          <button @click="logout" class="block px-4 py-2 text-red-600 hover:bg-red-50 w-full text-left">
            🚪 Logout
          </button>
        </div>
      </div>

      <button v-if="!isLoggedIn" @click="$emit('open-modal', 'login')"
        class="text-blue-600 font-semibold px-4 py-2 rounded hover:bg-blue-50 transition">
        Login
      </button>
    </div>
  </nav>
</template>

<script setup>
import { ref, computed, watch, onMounted, onBeforeUnmount } from 'vue'
import { debounce } from 'lodash-es'
import { useAuthStore } from '../store/authStore'
import { searchService } from '../services/searchService'

const keyword = ref('')
const results = ref({ words: [], decks: [] })
const expandedSection = ref(null)
const searchWrapper = ref(null)
const authWrapper = ref(null)

const showDropdown = ref(false)
const auth = useAuthStore()
const user = computed(() => auth.user)
const isLoggedIn = computed(() => auth.isLoggedIn)
auth.checkAuth()

// ✅ fetch search (debounced 300ms)
const fetchSearch = debounce(async (q) => {
  if (!q) {
    results.value = { words: [], decks: [] }
    return
  }
  try {
    results.value = await searchService.globalSearch(q)
  } catch (err) {
    console.error('Search error:', err)
    results.value = { words: [], decks: [] }
  }
}, 300)

watch(keyword, (newVal) => {
  fetchSearch(newVal)
})

// ✅ đóng dropdown search
const closeDropdown = () => {
  results.value = { words: [], decks: [] }
  expandedSection.value = null
  keyword.value = '' 
}

// ✅ click outside cho cả search + auth
const handleClickOutside = (event) => {
  // search
  if (searchWrapper.value && !searchWrapper.value.contains(event.target)) {
    closeDropdown()
  }
  // auth
  if (authWrapper.value && !authWrapper.value.contains(event.target)) {
    showDropdown.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})
onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside)
})

const logout = () => {
  auth.logout()
  window.location.href = '/'
}
</script>

<style scoped>
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.2s ease;
}
.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-5px);
}
</style>
