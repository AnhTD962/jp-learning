<template>
  <aside class="w-60 bg-gray-100 h-full fixed top-0 left-0 pt-16 shadow-lg z-50">
    <nav class="flex flex-col p-4 space-y-2">

      <router-link to="/" class="nav-link" exact-active-class="active">
        <HomeIcon class="w-5 h-5 mr-1" />
        <span class="ml-2">Home</span>
      </router-link>

      <router-link to="/attempts" class="nav-link" exact-active-class="active" v-if="auth.isLoggedIn">
        <FolderOpenIcon class="w-5 h-5 mr-1" />
        <span class="ml-2">Attempts</span>
      </router-link>

      <router-link to="/notifications" class="nav-link" exact-active-class="active" v-if="auth.isLoggedIn">
        <div class="relative flex items-center">
          <div class="relative">
            <BellAlertIcon class="w-5 h-5 mr-1" />
            <span v-if="unreadCount > 0"
              class="absolute -top-1 -right-1 bg-red-600 text-white text-[10px] font-bold px-1.5 py-0.5 rounded-full leading-none">
              {{ unreadCount }}
            </span>
          </div>
          <span class="ml-2">Notifications</span>
        </div>
      </router-link>

      <router-link to="/flashcards" class="nav-link" exact-active-class="active" v-if="auth.isLoggedIn">
        <Squares2X2Icon class="w-5 h-5 mr-1" />
        <span class="ml-2">Flashcards</span>
      </router-link>

      <router-link to="/words" class="nav-link" exact-active-class="active">
        <LanguageIcon class="w-5 h-5 mr-1" />
        <span class="ml-2">Words</span>
      </router-link>
    </nav>
  </aside>
</template>

<script setup>
import {
  HomeIcon,
  Squares2X2Icon,
  LanguageIcon,
  FolderOpenIcon,
  BellAlertIcon
} from '@heroicons/vue/24/outline'

import { useAuthStore } from '../store/authStore'
import { storeToRefs } from 'pinia'
import { useNotificationStore } from '../store/notificationStore'

const auth = useAuthStore()
const notificationStore = useNotificationStore()

// Lấy getter ra dưới dạng reactive ref
const { unreadCount } = storeToRefs(notificationStore)
</script>

<style scoped>
.nav-link {
  @apply flex items-center px-3 py-2 rounded text-gray-700 hover:text-orange-600 hover:bg-orange-50 transition;
}

.active {
  @apply font-semibold text-orange-700 bg-orange-100 border-l-4 border-orange-600 pl-2;
}
</style>
