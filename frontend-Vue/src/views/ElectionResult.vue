<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import Sidebar from '@/components/sidebar.vue';
import NationalView from '@/components/NationalView.vue';
import ConstituencyView from '@/components/ConstituencyView.vue';
import MunicipalityView from '@/components/MunicipalityView.vue';

import {
  getAllConstituency,
  getNationalResults,
  getCandidatesForNational,
  type ResultNodeDTO,
  type AggregatedCandidateDTO,
  searchByPostcode, searchByPostcodeRange,
} from '@/services/MunicipalityService.ts';

/** Selected election year */
const electionYear = ref<number>(2023);

/** Global loading state (for sidebar + initial national load) */
const loading = ref<boolean>(false);

/** Global error message */
const error = ref<string | null>(null);

/** List of constituencies for the sidebar */
const constituencyList = ref<ResultNodeDTO[]>([]);

const originalConstituencyList = ref<ResultNodeDTO[]>([]);

/** Search text (currently used to filter inside Sidebar) */
const searchQuery = ref<string>('');
const postcodeQuery = ref<string>('');

/** National root node + aggregated candidate results */
const nationalNode = ref<ResultNodeDTO | null>(null);
const nationalCandidates = ref<AggregatedCandidateDTO[]>([]);

/** Currently selected level: only store IDs, detail data is fetched in child views */
const selectedConstituencyId = ref<string | null>(null);
const selectedMunicipalityId = ref<string | null>(null);


const selectedPollingStationId = ref<string | null>(null);

function filterActiveNodes(nodes: ResultNodeDTO[]): ResultNodeDTO[] {
  return nodes
    .filter(node => node.totalVotes > 0)
    .map(node => {
      if (node.children && node.children.length > 0) {
        return { ...node, children: filterActiveNodes(node.children) };
      }
      return node;
    });
}

/** Load all constituencies */
async function fetchConstituencies(): Promise<void> {
  try {
    const allConstituencies = await getAllConstituency(electionYear.value);

    // Filter and sort
    const activeConstituencies = filterActiveNodes(allConstituencies);
    activeConstituencies.sort(function (a, b) {
      return a.name.localeCompare(b.name);
    });

    constituencyList.value = activeConstituencies;

    originalConstituencyList.value = activeConstituencies;

  } catch (err) {
    console.error(err);
    error.value = 'Kon kieskringen niet laden.';
  }
}

/** Load national root node + national candidate results */
async function loadNational(): Promise<void> {
  try {
    const [root, candidateData] = await Promise.all([
      getNationalResults(electionYear.value),
      getCandidatesForNational(electionYear.value),
    ]);

    if (root.children) {
      root.children = filterActiveNodes(root.children);
    }

    nationalNode.value = root;
    nationalCandidates.value = candidateData;
  } catch (err) {
    console.error('Failed to load national results', err);
    error.value = 'Kon landelijke resultaten niet laden.';
  }
}

/** Load sidebar list + national results for a specific year */
async function loadAllForYear(): Promise<void> {
  loading.value = true;
  error.value = null;
  searchQuery.value = '';

  try {
    await Promise.all([
      fetchConstituencies(),
      loadNational()
    ]);
  } finally {
    loading.value = false;
  }
}

/** v-model handler for search input from Sidebar */
function onSearchQueryChange(value: string): void {
  searchQuery.value = value;
}

function onPostcodeQueryChange(value: string): void {
  postcodeQuery.value = value;
}

const postcodeError = ref<string | null>(null);

/** watcher for postcodes */
watch(postcodeQuery, async (newVal) => {
  postcodeError.value = null;

  if (!newVal) {
    constituencyList.value = originalConstituencyList.value;
    return;
  }

  const rangeRegex = /^([1-9][0-9]{3}[a-zA-Z]{0,2})\s*-\s*([1-9][0-9]{3}[a-zA-Z]{0,2})$/;

  const singleRegex = /^[1-9][0-9]{3}\s?[a-zA-Z]{0,2}$/;

  loading.value = true;

  try {
    let results = [];

    const rangeMatch = newVal.match(rangeRegex);

    if (rangeMatch) {
      results = await searchByPostcodeRange(electionYear.value, rangeMatch[1], rangeMatch[2]);
    }
    else if (singleRegex.test(newVal)) {
      results = await searchByPostcode(electionYear.value, newVal);
    }
    else {
      loading.value = false;
      return;
    }

    if (results.length === 0) {
      postcodeError.value = rangeMatch
        ? `Geen resultaten voor range ${rangeMatch[1]} tot ${rangeMatch[2]}.`
        : `Geen resultaten voor "${newVal}".`;

      constituencyList.value = [];
    } else {
      constituencyList.value = results;
    }

  } catch (e) {
    console.error(e);
    constituencyList.value = [];
    postcodeError.value = "Fout bij ophalen resultaten.";
  } finally {
    loading.value = false;
  }
});

watch(postcodeQuery, (newValue) => {
  if (newValue) {
    localStorage.setItem("election_postcode_filter", newValue );
  }
  else {
    localStorage.removeItem("election_postcode_filter");
  }
})


/** When an item is clicked in Sidebar (can be Constituency OR Stembureau from search) */
function onSelectConstituency(node: ResultNodeDTO): void {
  if (node.category === 'Stembureau') {
    selectedPollingStationId.value = node.id;

    selectedConstituencyId.value = null;
    selectedMunicipalityId.value = null;
    return;
  }

  selectedConstituencyId.value = node.id;
  selectedMunicipalityId.value = null;
  selectedPollingStationId.value = null;
}

/** Payload for municipality selection coming from Sidebar */
interface MunicipalitySelectionPayload {
  municipality: ResultNodeDTO;
  constituencyId: string;
}

/** When a municipality is clicked in Sidebar */
function onSelectMunicipality(payload: MunicipalitySelectionPayload): void {
  selectedMunicipalityId.value = payload.municipality.id;
  // Clear constituency selection so MunicipalityView becomes active
  selectedConstituencyId.value = null;
  selectedPollingStationId.value = null;
}

/** Initial load */
onMounted(async () => {
  await  loadAllForYear()

  const savedPostCode = localStorage.getItem("election_postcode_filter")

  if (savedPostCode) {
    postcodeQuery.value = savedPostCode;
  }

})

/** When the year changes: reset selections and reload everything */
function handleYearChange(): void {
  selectedConstituencyId.value = null;
  selectedMunicipalityId.value = null;
  selectedPollingStationId.value = null;
  loadAllForYear();
}

watch(electionYear, handleYearChange);
</script>

<template>
  <div class="page-container">
    <header class="top-bar">
      <div class="header-content">
        <h1>Verkiezingsresultaten – Tweede Kamer</h1>

        <div class="year-selector">
          <label for="year-select" id="select">Selecteer jaar:</label>
          <select
            id="year-select"
            v-model="electionYear"
            class="year-dropdown"
          >
            <option :value="2025">2025</option>
            <option :value="2023">2023</option>
            <option :value="2021">2021</option>
          </select>
        </div>
      </div>
    </header>

    <div class="content-layout">
      <Sidebar
        class="sidebar"
        :constituencies="constituencyList"
        :search-query="searchQuery"
        :postcode-query="postcodeQuery"
        :loading="loading"
        :error-message="postcodeError"
        :selected-constituency-id="selectedConstituencyId"
        :selected-municipality-id="selectedMunicipalityId"
        @update:searchQuery="onSearchQueryChange"
        @update:postcodeQuery="onPostcodeQueryChange"
        @select-constituency="onSelectConstituency"
        @select-municipality="onSelectMunicipality"
      />

      <main class="main-content">
        <div v-if="error" class="error-box">{{ error }}</div>

        <NationalView
          v-if="nationalNode && !selectedConstituencyId && !selectedMunicipalityId && !selectedPollingStationId"
          :root="nationalNode"
          :candidates="nationalCandidates"
          :year="electionYear"
        />

        <ConstituencyView
          v-else-if="selectedConstituencyId"
          :constituency-id="selectedConstituencyId"
          :year="electionYear"
        />

        <MunicipalityView
          v-else-if="selectedMunicipalityId"
          :municipality-id="selectedMunicipalityId"
          :year="electionYear"
        />

        <MunicipalityView
          v-else-if="selectedPollingStationId"
          :municipality-id="selectedPollingStationId"
          :year="electionYear"
          title-prefix="Stembureau"
        />

        <div
          v-else-if="loading"
          class="placeholder"
        >
          Laden...
        </div>
      </main>
    </div>
  </div>
</template>
<style scoped>
/* ===== Page header layout ===== */
.page-container {
  min-height: 100vh;
}

/* Top bar: sticky + glass */
.top-bar {
  position: sticky;
  top: 0;
  z-index: 50;

  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);

  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
  background: rgba(255, 255, 255, 0.72);
}

/* Inner container */
.header-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 16px 20px;

  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

/* Title */
.header-content h1 {
  margin: 0;
  font-size: clamp(18px, 2.2vw, 26px);
  font-weight: 800;
  letter-spacing: -0.02em;
  line-height: 1.15;

  color: #001b5e;
}

/* Year selector group */
.year-selector {
  display: flex;
  align-items: center;
  gap: 10px;

  padding: 10px 12px;
  border-radius: 14px;

  border: 1px solid rgba(0, 0, 0, 0.10);
  background: rgba(255, 255, 255, 0.85);
  box-shadow: 0 10px 24px rgba(2, 6, 23, 0.06);
}

/* Label */
.year-selector label {
  font-size: 13px;
  font-weight: 600;
  color: rgba(15, 23, 42, 0.75);
  white-space: nowrap;
}

/* Select */
#select {
  margin: 0;
}
.year-dropdown {
  appearance: none;
  -webkit-appearance: none;

  min-width: 110px;
  padding: 10px 40px 10px 12px;
  border-radius: 12px;

  border: 1px solid rgba(0, 0, 0, 0.12);
  background: rgba(255, 255, 255, 0.95);

  font-size: 14px;
  font-weight: 700;
  color: #0f172a;

  cursor: pointer;
  outline: none;

  /* custom arrow */
  background-image:
    linear-gradient(45deg, transparent 50%, rgba(15, 23, 42, 0.7) 50%),
    linear-gradient(135deg, rgba(15, 23, 42, 0.7) 50%, transparent 50%),
    linear-gradient(to right, rgba(0, 0, 0, 0.10), rgba(0, 0, 0, 0.10));
  background-position:
    calc(100% - 18px) 55%,
    calc(100% - 12px) 55%,
    calc(100% - 36px) 50%;
  background-size:
    6px 6px,
    6px 6px,
    1px 60%;
  background-repeat: no-repeat;

  transition: border-color 150ms ease, box-shadow 150ms ease, transform 150ms ease;
}

.year-dropdown:hover {
  border-color: rgba(0, 0, 0, 0.20);
}

.year-dropdown:focus-visible {
  border-color: rgba(37, 99, 235, 0.55);
  box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.16);
}

.year-dropdown:active {
  transform: translateY(1px);
}

/* Responsive: stack on small screens */
@media (max-width: 640px) {
  .header-content {
    flex-direction: column;
    align-items: stretch;
  }

  .year-selector {
    justify-content: space-between;
  }

  .year-dropdown {
    width: 100%;
  }
}


.content-layout {
  flex: 1;
  display: flex;
  min-height: 0;
  overflow: hidden;
}

.main-content {
  flex: 1;
  min-width: 0;
  padding: 1.5rem 2rem;
  overflow-y: auto;
}

.error-box {
  padding: 0.85rem 1rem;
  margin-bottom: 1rem;
  border-radius: 10px;
  border: 1px solid #fecaca;
  background: #fee2e2;
  color: #991b1b;
  font-weight: 600;
}

.placeholder {
  padding: 1rem;
  color: #6b7280;
}

@media (max-width: 980px) {
  .top-bar {
    padding: 1rem 1.25rem;
  }

  .main-content {
    padding: 1.25rem;
  }
}

@media (max-width: 820px) {
  .content-layout {
    flex-direction: column;
  }

  :deep(.sidebar) {
    width: 100% !important;
    border-right: none !important;
    border-bottom: 1px solid #e5e7eb;
    max-height: 45vh;
  }
}
</style>
