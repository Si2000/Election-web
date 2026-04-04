<template>
  <aside>
    <div class="search-wrapper">
      <input
        class="search-input"
        :value="searchQuery"
        placeholder="Zoek gemeente..."
        @input="(e) => $emit('update:searchQuery', (e.target as HTMLInputElement).value)"
      />
    </div>

    <div class="search-wrapper postcode-wrapper">
      <input
        class="search-input"
        v-model="localPostcode"
        placeholder="1234AB of 1234AA - 1234ZZ"
        @input="onPostcodeInput"
      />
    </div>

    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>

    <div
      v-if="loading && !filteredConstituencies.length"
      class="loading-text"
    >
      Laden...
    </div>

    <ul class="location-list">
      <li
        v-for="c in filteredConstituencies"
        :key="c.id"
        class="location-item"
        :class="{ active: selectedConstituencyId === c.id }"
      >
        <div class="loc-name" @click="toggleConstituency(c)">
          {{ c.name }}
          <span>{{ expandedConstituencyId === c.id ? '▼' : '►' }}</span>
        </div>

        <ul v-if="expandedConstituencyId === c.id" class="sub-list">
          <li
            v-for="g in c.children"
            :key="g.id"
            class="location-sub-item"
            :class="{ active: selectedMunicipalityId === g.id }"
            @click.stop="selectMunicipality(c.id, g)"
          >
            {{ g.name }}
            <span v-if="g.totalVotes > 0" class="small-votes">
              {{ g.totalVotes }} Votes
            </span>
          </li>
        </ul>
      </li>
    </ul>
  </aside>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import type { ResultNodeDTO } from '@/services/MunicipalityService.ts';

const props = defineProps<{
  constituencies: ResultNodeDTO[];
  searchQuery: string;
  postcodeQuery: string;
  loading: boolean;
  errorMessage: string | null;
  selectedConstituencyId: string | null;
  selectedMunicipalityId: string | null;
}>();

const emit = defineEmits<{
  (e: 'update:searchQuery', value: string): void;
  (e: 'update:postcodeQuery', value: string): void;
  (e: 'select-constituency', node: ResultNodeDTO): void;
  (e: 'select-municipality', payload: { municipality: ResultNodeDTO; constituencyId: string }): void;
}>();

const localPostcode = ref(props.postcodeQuery);

function onPostcodeInput() {
  emit('update:postcodeQuery', localPostcode.value);
}

watch(() => props.postcodeQuery, (newVal) => {
  if (newVal !== localPostcode.value) {
    localPostcode.value = newVal;
  }
});


const expandedConstituencyId = ref<string | null>(null);

function toggleConstituency(node: ResultNodeDTO): void {
  if (expandedConstituencyId.value === node.id) {
    expandedConstituencyId.value = null;
  } else {
    expandedConstituencyId.value = node.id;
    emit('select-constituency', node);
  }
}

function selectMunicipality(constituencyId: string, municipality: ResultNodeDTO): void {
  emit('select-municipality', { municipality, constituencyId });
}

function computeFilteredConstituencies(): ResultNodeDTO[] {
  if (!props.searchQuery) {
    return props.constituencies;
  }
  const q = props.searchQuery.toLowerCase();
  return props.constituencies.filter(c => {
    if (c.name.toLowerCase().includes(q)) return true;
    if (c.children && c.children.length > 0) {
      return c.children.some(g => g.name.toLowerCase().includes(q));
    }
    return false;
  });
}

const filteredConstituencies = computed(computeFilteredConstituencies);
</script>

<style scoped>
aside {
  width: 340px;
  background: #ffffff;
  border-right: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.search-wrapper {
  padding: 0.9rem 1rem;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
}

.postcode-wrapper {
  padding-top: 0.75rem;
  padding-bottom: 0.75rem;
}

.search-input {
  width: 100%;
  padding: 9px 12px;
  border: 1px solid #d0d5dd;
  border-radius: 10px;
  font-size: 0.95rem;
  background: #fafafa;
  transition: border-color 0.2s, box-shadow 0.2s, background 0.2s;
}

.search-input::placeholder {
  color: #9aa3b2;
}

.search-input:focus {
  outline: none;
  border-color: #4f7cff;
  box-shadow: 0 0 0 3px rgba(85, 138, 255, 0.22);
  background: #fff;
}

.error-message {
  margin: 0.75rem 1rem 0;
  padding: 0.65rem 0.75rem;
  border-radius: 10px;
  background: #fee2e2;
  border: 1px solid #fecaca;
  color: #991b1b;
  font-weight: 600;
  font-size: 0.9rem;
}

.loading-text {
  padding: 0.75rem 1rem;
  font-size: 0.9rem;
  color: #6b7280;
}

.location-list {
  list-style: none;
  margin: 0;
  padding: 0;
  overflow-y: auto;
}

.location-item {
  padding: 12px 14px;
  cursor: pointer;
  background: #ffffff;
  border-bottom: 1px solid #f1f1f1;
  transition: background 0.18s ease;
}

.location-item:hover {
  background: #f5f7fb;
}

.location-item.active {
  background: #eef2ff;
}

.loc-name {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 0.75rem;
  color: #2b2f38;
  font-weight: 700;
  font-size: 0.95rem;
  user-select: none;
}

.sub-list {
  list-style: none;
  padding-left: 16px;
  margin: 0.6rem 0 0;
  border-left: 2px solid #dde3ee;
}

.location-sub-item {
  padding: 10px 10px;
  font-size: 0.9rem;
  border-radius: 10px;
  margin: 4px 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 0.75rem;
  transition: background 0.18s ease, color 0.18s ease;
}

.location-sub-item:hover {
  background: #eef3ff;
}

.location-sub-item.active {
  background: #dce7ff;
  color: #003eaa;
  font-weight: 700;
}

.small-votes {
  font-size: 12px;
  color: #6b7280;
  flex: 0 0 auto;
}

@media (max-width: 820px) {
  aside {
    width: 100%;
    border-right: none;
  }
}
</style>

