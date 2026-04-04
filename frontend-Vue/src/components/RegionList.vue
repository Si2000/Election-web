<script setup lang="ts">
import { defineProps } from 'vue';

interface Region {
  id: number;
  name: string;
}

const props = defineProps<{
  regions?: Region[];
}>();
</script>

<template>
  <div id="RegionsList" class="regions-section">
    <h2>Kieskringen Overzicht</h2>
    <p class="regions-intro">Overzicht van de kieskringen (regions) die beschikbaar zijn in de dataset:</p>
    <ul class="region-list">
      <li v-for="region in props.regions || []" :key="region.id" class="region-item">
        {{ region.name }}
      </li>
    </ul>
  </div>
</template>

<style scoped>
/* REGIONS SECTION STYLING (Hergeld) */
.regions-section {
  padding: 20px;
}

.regions-section h2 {
  font-size: 2rem;
  color: #2c3e50;
  margin-bottom: 10px;
}

.regions-intro {
  margin-bottom: 20px;
  color: #666;
}

.region-list {
  list-style: none;
  padding: 0;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.region-item {
  background-color: #fff;
  border: 1px solid #ddd;
  padding: 8px 15px;
  border-radius: 6px;
  font-weight: 500;
  color: #333;
  transition: background-color 0.2s ease;
}

.region-item:hover {
  background-color: #f0f4ff;
}
</style>
