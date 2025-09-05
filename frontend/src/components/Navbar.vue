<template>
  <nav class="bg-white shadow-md p-4 flex justify-between items-center">
    <div class="flex items-center gap-4">
      <!-- Mobile hamburger -->
      <button class="md:hidden text-xl" @click="$emit('toggle-sidebar')">☰</button>

      <router-link to="/" class="text-xl font-bold text-orange-600">JapaneseApp</router-link>
    </div>

    <div class="flex items-center gap-4">
      <input
        type="text"
        placeholder="Search..."
        class="border border-gray-300 rounded px-2 py-1 hidden sm:block"
      />
      <div v-if="isLoggedIn" class="relative">
        <button @click="showDropdown = !showDropdown" class="ml-2 text-gray-700">
          {{ user?.username }} ⬇
        </button>
        <div
          v-if="showDropdown"
          class="absolute right-0 mt-2 w-40 bg-white shadow rounded z-50 border"
        >
          <router-link
            to="/profile"
            class="block px-4 py-2 hover:bg-gray-100 w-full text-left"
          >
            👤 Profile
          </router-link>

          <router-link
            to="/change-password"
            class="block px-4 py-2 hover:bg-gray-100 w-full text-left"
          >
            🔑 Change Password
          </router-link>

          <button
            @click="logout"
            class="block px-4 py-2 text-red-600 hover:bg-red-50 w-full text-left"
          >
            🚪 Logout
          </button>
        </div>
      </div>
      <button
        v-if="!isLoggedIn"
        @click="$emit('open-modal', 'login')"
        class="text-blue-600 font-semibold px-4 py-2 rounded hover:bg-blue-50 transition"
      >
        Login
      </button>
    </div>
  </nav>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useAuthStore } from '../store/authStore'

const showDropdown = ref(false)
const auth = useAuthStore()
const user = computed(() => auth.user)
const isLoggedIn = computed(() => auth.isLoggedIn)
auth.checkAuth()

const logout = () => {
  auth.logout()
  window.location.href = '/'
}
</script>
