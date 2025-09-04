<template>
  <div class="p-6 max-w-2xl mx-auto">
    <div class="flex justify-between items-center mb-4">
      <h1 class="text-xl font-bold">Notifications</h1>
      <button
        v-if="store.unreadCount > 0"
        @click="markAllAsRead"
        class="text-sm text-orange-600 hover:underline"
      >
        Mark all as read
      </button>
    </div>

    <div v-if="sortedNotifications.length === 0" class="text-gray-500">
      No notifications yet.
    </div>

    <ul class="space-y-3">
      <li
        v-for="n in sortedNotifications"
        :key="n.id"
        class="p-4 border rounded-lg flex justify-between items-center cursor-pointer"
        :class="n.read ? 'bg-gray-100' : 'bg-white shadow-sm'"
        @click="openDetail(n)"
      >
        <div>
          <p class="font-medium">{{ n.message }}</p>
          <p class="text-xs text-gray-500">{{ formatDate(n.createdAt) }}</p>
        </div>

        <button
          v-if="!n.read"
          @click.stop="store.markAsRead(n.id)"
          class="text-sm text-orange-600 hover:underline"
        >
          Mark as read
        </button>
      </li>
    </ul>

    <!-- Detail Modal -->
    <div
      v-if="selectedNotif"
      class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50"
    >
      <div class="bg-white rounded-lg p-6 max-w-md w-full shadow-lg">
        <h2 class="text-lg font-semibold mb-2">Notification Detail</h2>
        <p class="mb-4">{{ selectedNotif.message }}</p>
        <p class="text-sm text-gray-500">{{ formatDate(selectedNotif.createdAt) }}</p>

        <div class="flex justify-end space-x-2 mt-4">
          <button
            @click="closeDetail"
            class="px-3 py-1 bg-gray-200 rounded hover:bg-gray-300"
          >
            Close
          </button>
          <button
            v-if="!selectedNotif.read"
            @click="markAndClose(selectedNotif.id)"
            class="px-3 py-1 bg-orange-600 text-white rounded hover:bg-orange-700"
          >
            Mark as read
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onBeforeUnmount } from "vue"
import { useNotificationStore } from "../store/notificationStore"

const store = useNotificationStore()
const selectedNotif = ref(null)

onMounted(() => {
  store.loadNotifications()
  store.connectToSSE()
})

onBeforeUnmount(() => {
  store.closeSSE()
})

const formatDate = (dateStr) => new Date(dateStr * 1000).toLocaleString()

// Sort: unread trước, rồi theo createdAt giảm dần
const sortedNotifications = computed(() => {
  return [...store.notifications].sort((a, b) => {
    if (a.read === b.read) {
      return new Date(b.createdAt * 1000) - new Date(a.createdAt * 1000)
    }
    return a.read ? 1 : -1
  })
})

const openDetail = (notif) => {
  selectedNotif.value = notif
}
const closeDetail = () => {
  selectedNotif.value = null
}
const markAndClose = async (id) => {
  await store.markAsRead(id)
  closeDetail()
}
const markAllAsRead = async () => {
  for (const n of store.notifications.filter((n) => !n.read)) {
    await store.markAsRead(n.id)
  }
}
</script>
