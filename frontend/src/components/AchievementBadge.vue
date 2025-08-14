<template>
  <div class="bg-yellow-100 border rounded p-4 flex items-center gap-4">
    <img :src="badge.icon" class="w-12 h-12" />
    <div>
      <h4 class="font-bold">{{ badge.name }}</h4>
      <p class="text-sm">{{ badge.description }}</p>
    </div>
  </div>
</template>

<script setup>
defineProps({ badge: Object })
</script>
