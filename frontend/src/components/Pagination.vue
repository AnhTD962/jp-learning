<template>
  <div class="mt-4 flex justify-center items-center gap-2">
    <button
      class="px-3 py-1 bg-orange-100 text-orange-700 rounded hover:bg-orange-200"
      :disabled="currentPage === 1"
      @click="$emit('page-change', currentPage - 1)"
    >
      Prev
    </button>

    <span class="text-gray-700">Page {{ currentPage }} of {{ totalPages }}</span>

    <button
      class="px-3 py-1 bg-orange-100 text-orange-700 rounded hover:bg-orange-200"
      :disabled="currentPage === totalPages"
      @click="$emit('page-change', currentPage + 1)"
    >
      Next
    </button>
  </div>
</template>

<script setup>
defineProps({
  currentPage: Number,
  totalPages: Number
})
defineEmits(['page-change'])
</script>
