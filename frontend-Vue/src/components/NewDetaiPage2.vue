<template>
  <div>
    <input
      v-for="(item, i) in items"
      :key="i"
      v-model="items[i]"
      style="display:block;margin-bottom:4px"
    />
    <button @click="addToFront">Add to front</button>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const items = ref(['Alice', 'Bob', 'Charlie'])

function addToFront() {
  items.value.unshift('NewGuy')
}
</script>
