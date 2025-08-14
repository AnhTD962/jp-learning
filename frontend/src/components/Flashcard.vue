<template>
  <div class="flashcard" :class="{ flipped }" @click="flipped = !flipped">
    <div class="card front">{{ flashcard.front }}</div>
    <div class="card back">{{ flashcard.back }}</div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const flipped = ref(false)
defineProps({ flashcard: Object })
</script>

<style scoped>
.flashcard {
  width: 100%;
  height: 160px;
  perspective: 1000px;
  cursor: pointer;
}
.card {
  height: 100%;
  padding: 1rem;
  text-align: center;
  border-radius: 10px;
  position: absolute;
  width: 100%;
  backface-visibility: hidden;
  transition: transform 0.6s;
}
.front {
  background: #fef3c7;
}
.back {
  background: #fcd34d;
  transform: rotateY(180deg);
}
.flipped .front {
  transform: rotateY(180deg);
}
.flipped .back {
  transform: rotateY(0deg);
}
</style>
