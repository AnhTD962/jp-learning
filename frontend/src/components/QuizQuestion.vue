<template>
  <div class="p-4 bg-white shadow rounded">
    <p class="text-lg font-semibold mb-2">{{ question.text }}</p>
    <div v-for="(option, index) in question.options" :key="index" class="mb-2">
      <label>
        <input type="radio" :value="option" v-model="selected" />
        {{ option }}
      </label>
    </div>
    <button @click="submit" class="btn-primary mt-2">Submit Answer</button>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({ question: Object })
const emit = defineEmits(['answer-selected'])

const selected = ref(null)

function submit() {
  if (selected.value) emit('answer-selected', selected.value)
}
</script>
