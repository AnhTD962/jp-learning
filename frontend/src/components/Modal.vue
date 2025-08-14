<template>
  <div class="fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center z-50" @click.self="$emit('close')">
    <div class="bg-white rounded shadow-lg p-8 w-full max-w-md relative">
      <button class="absolute top-2 right-2 text-gray-400 hover:text-gray-600 text-2xl" @click="$emit('close')">&times;</button>
      <slot />
    </div>
  </div>
</template>