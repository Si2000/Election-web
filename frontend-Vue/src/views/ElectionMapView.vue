<template>
  <div class="map-container">
    <ElectionMap />
  </div>
</template>

<script setup lang="ts">
import ElectionMap from '@/components/ElectionMap.vue'
</script>

<style scoped>
.map-container {
  width: 100%;
  height: 100vh;
}
</style>
