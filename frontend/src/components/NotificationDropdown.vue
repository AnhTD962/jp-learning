<template>
  <div class="relative">
    <button @click="open = !open">
      🔔 <span v-if="unreadCount" class="text-red-500 font-bold">({{ unreadCount }})</span>
    </button>
    <div v-if="open" class="absolute right-0 mt-2 w-64 bg-white shadow rounded">
      <div v-for="notification in notifications" :key="notification.id" class="p-2 border-b">
        {{ notification.message }}
        <button @click="markAsRead(notification.id)" class="text-xs text-blue-500">Mark as read</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useNotificationStore } from '../store/notificationStore'

const store = useNotificationStore()
const open = ref(false)
const notifications = store.notifications
const unreadCount = store.unreadCount

function markAsRead(id) {
  store.markAsRead(id)
}
</script>
