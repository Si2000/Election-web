<script setup lang="ts">
import { ref, watch, onMounted } from 'vue';
import CandidateList from '@/components/CandidatesPage.vue';
import { getAllCandidates } from '@/services/MunicipalityService';

// Definieer lokaal wat het CandidateList component verwacht
interface ComponentCandidate {
  dbId: number;
  firstname: string;
  initials: string;
  lastname: string;
  gender?: string;
  party: string;
  locality: string;
}

const electionYears = ref([2025, 2023, 2021]);
const selectedYear = ref(2025);

// Gebruik het juiste type voor de lijst
const displayedCandidates = ref<ComponentCandidate[]>([]);
const parties = ref<string[]>([]);
const selectedParty = ref('');
const selectedGender = ref('');
const genderOptions = ref([
  { value: '', text: '-- Alle genders --' },
  { value: 'male', text: 'Man' },
  { value: 'female', text: 'Vrouw' },
]);

const loading = ref(false);
const error = ref<string | null>(null);

// Helper functie om API data om te zetten naar Component data
const mapToComponentCandidate = (apiCandidate: any): ComponentCandidate => ({
  dbId: parseInt(apiCandidate.candidateid) || 0,
  firstname: apiCandidate.firstname,
  lastname: apiCandidate.lastname,
  // Maak initialen als ze er niet zijn
  initials: apiCandidate.firstname ? apiCandidate.firstname.charAt(0) + '.' : '',
  party: apiCandidate.party,
  gender: apiCandidate.gender,
  // Locality zit niet in de API, dus geven we een default waarde
  locality: 'Nederland'
});

const loadMetadata = async () => {
  try {
    const allData = await getAllCandidates(selectedYear.value);

    // Map de data hier!
    const mappedData = allData.map(mapToComponentCandidate);

    const uniqueParties = Array.from(new Set(allData.map(c => c.party))).sort();
    parties.value = uniqueParties;

    displayedCandidates.value = mappedData;
  } catch (err: any) {
    console.error("Fout bij laden metadata", err);
  }
};

const fetchFilteredCandidates = async () => {
  loading.value = true;
  error.value = null;

  try {
    const data = await getAllCandidates(
      selectedYear.value,
      selectedParty.value,
      selectedGender.value
    );

    // Map de data ook hier!
    displayedCandidates.value = data.map(mapToComponentCandidate);

  } catch (err: any) {
    error.value = `Kon kandidaten niet laden: ${err.message}`;
  } finally {
    loading.value = false;
  }
};

watch(selectedYear, async () => {
  selectedParty.value = '';
  selectedGender.value = '';
  await loadMetadata();
});

watch([selectedParty, selectedGender], () => {
  fetchFilteredCandidates();
});

onMounted(async () => {
  await loadMetadata();
});
</script>

<template>
  <div class="candidates-view-single">
    <h1>Kandidaten Overzicht {{ selectedYear }}</h1>

    <div class="filters-container">
      <div class="filter-group">
        <label for="year-select">Kies een jaar: </label>
        <select id="year-select" v-model="selectedYear">
          <option v-for="year in electionYears" :key="year" :value="year">
            {{ year }}
          </option>
        </select>
      </div>

      <div class="filter-group">
        <label for="party-select">Filter op partij: </label>
        <select id="party-select" v-model="selectedParty" :disabled="parties.length === 0">
          <option value="">-- Alle partijen --</option>
          <option v-for="party in parties" :key="party" :value="party">{{ party }}</option>
        </select>
      </div>

      <div class="filter-group">
        <label for="gender-select">Filter op gender: </label>
        <select id="gender-select" v-model="selectedGender">
          <option v-for="option in genderOptions" :key="option.value" :value="option.value">
            {{ option.text }}
          </option>
        </select>
      </div>
    </div>

    <div v-if="loading" class="status-message">Laden...</div>
    <div v-if="error" class="error-message">{{ error }}</div>

    <div v-if="!loading && !error">

      <CandidateList :candidates="displayedCandidates" />
    </div>
  </div>
</template>
