<template>
  <table class="w-full table-auto bg-white rounded shadow overflow-hidden">
    <thead class="bg-orange-200">
    <tr>
      <th class="p-2">Rank</th>
      <th class="p-2">Username</th>
      <th class="p-2">XP</th>
      <th class="p-2">Streak</th>
    </tr>
    </thead>
    <tbody>
    <tr v-for="(user, index) in users" :key="user.id" class="text-center border-b">
      <td class="p-2">{{ index + 1 }}</td>
      <td class="p-2">{{ user.username }}</td>
      <td class="p-2">{{ user.xp }}</td>
      <td class="p-2">{{ user.streak }}</td>
    </tr>
    </tbody>
  </table>
</template>

<script setup>
defineProps({ users: Array })
</script>
