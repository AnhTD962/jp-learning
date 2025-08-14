<template>
  <div class="bg-white shadow p-4 rounded">
    <h3 class="text-xl font-bold">Quiz Results</h3>
    <p>Score: {{ result.score }}/{{ result.total }}</p>
    <p>XP Earned: {{ result.xp }}</p>
    <ul class="mt-2">
      <li v-for="(item, index) in result.answers" :key="index">
        Q{{ index + 1 }}: <span :class="item.correct ? 'text-green-600' : 'text-red-600'">
          {{ item.correct ? 'Correct' : 'Incorrect' }}
        </span>
      </li>
    </ul>
  </div>
</template>

<script setup>
defineProps({ result: Object })
</script>
