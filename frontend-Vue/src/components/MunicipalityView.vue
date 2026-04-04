<template>
  <div v-if="node">

    <header class="municipality-header">
      <h2>
        <span
          class="category-tag"
          :class="isStembureau ? 'tag-stembureau' : 'tag-gemeente'"
        >
          {{ isStembureau ? 'Stembureau' : 'Gemeente' }}
        </span>
        {{ node.name }}
      </h2>

      <div class="stats-bar">
        <div class="stat-badge">
          <span class="stat-label">Totaal Stemmen</span>
          <span class="stat-value">{{ node.totalVotes.toLocaleString() }}</span>
        </div>

        <div class="stat-badge" v-if="!isStembureau">
          <span class="stat-label">Stembureaus</span>
          <span class="stat-value">{{ node.children ? node.children.length : 0 }}</span>
        </div>
      </div>
    </header>

    <div class="chart-section">
      <h3>Top 5 Partijen</h3>
      <div class="chart-wrapper" >
        <canvas ref="chartCanvas"></canvas>
      </div>
    </div>

    <div class="results-grid">
      <PartyResultTable
        :votes-per-party="node.votesPerParty"
        title="Uitslag per Partij"
        party-header="Partij"
        votes-header="Stemmen"
      />

      <CandidateTable
        :candidates="candidates"
        title="Uitslag per Kandidaat"
        name-header="Naam"
        votes-header="Stemmen"
      />
    </div>

    <section
      v-if="!isStembureau && node.children && node.children.length > 0"
      class="stembureaus-section"
    >
      <h3>Resultaten per Stembureau</h3>

      <div class="stembureau-list">
        <ExpandableCard
          v-for="station in node.children"
          :key="station.id"
          :title="station.name"
          :total-votes="station.totalVotes"
          votes-label="Stemmen"
          :is-expanded="expandedPollingStationId === station.id"
          :is-loading="loadingPollingStationId === station.id"
          @toggle="togglePollingStation(station.id)"
        >
          <template #default>
            <CandidateTable
              v-if="pollingStationCandidatesCache[station.id]"
              :candidates="pollingStationCandidatesCache[station.id]"
              title="Uitslag per Kandidaat"
              name-header="Naam"
              votes-header="Stemmen"
            />

            <div v-else class="loading-small">Laden...</div>
          </template>
        </ExpandableCard>
      </div>
    </section>
  </div>

  <div v-else-if="loading" class="placeholder">
    Gegevens ophalen...
  </div>

  <div v-else class="placeholder error">
    Geen data gevonden voor dit ID.
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, computed, nextTick } from "vue";
import {
  getMunicipalityNode,
  getCandidatesForMunicipality,
  getCandidatesForPollingStation,
  type ResultNodeDTO,
  type AggregatedCandidateDTO
} from "@/services/MunicipalityService.ts";

import PartyResultTable from "./PartyResultTable.vue";
import CandidateTable from "./CandidateTable.vue";
import ExpandableCard from "./ExpandableCard.vue";

import {
  Chart,
  BarController,
  BarElement,
  CategoryScale,
  LinearScale,
  Tooltip,
  Legend
} from "chart.js";

Chart.register(
  BarController,
  BarElement,
  CategoryScale,
  LinearScale,
  Tooltip,
  Legend
);

/* =========================
   Party color mapping
   ========================= */
const partyColors: Record<string, string> = {
  "50PLUS": "#FF69B4",
  "BBB": "#FFB300",
  "BIJ1": "#8B0000",
  "BVNL / Groep Van Haga": "#1E90FF",
  "CDA": "#008000",
  "ChristenUnie": "#FFA500",
  "D66": "#33CC99",
  "DENK": "#800080",
  "Forum voor Democratie": "#7B1FA2",
  "GROENLINKS / Partij van de Arbeid (PvdA)": "#B71C1C",
  "JA21": "#008AC8",
  "LEF - Voor de Nieuwe Generatie": "#FF4500",
  "LP (Libertaire Partij)": "#20B2AA",
  "Nederland met een PLAN": "#FFD700",
  "Nieuw Sociaal Contract": "#DC143C",
  "PVV (Partij voor de Vrijheid)": "#0033FF",
  "Partij voor de Dieren": "#228B22",
  "PartijvdSport": "#FF6347",
  "Piratenpartij - De Groenen": "#32CD32",
  "Politieke Partij voor Basisinkomen": "#00CED1",
  "SP (Socialistische Partij)": "#E3000F",
  "Samen voor Nederland": "#6A5ACD",
  "Splinter": "#FF1493",
  "Staatkundig Gereformeerde Partij (SGP)": "#556B2F",
  "VVD": "#1D70B8",
  "Volt": "#FF00FF"
};

const props = defineProps<{
  municipalityId: string;
  year: number;
}>();

const node = ref<ResultNodeDTO | null>(null);
const candidates = ref<AggregatedCandidateDTO[]>([]);
const loading = ref(false);

/* Polling station state */
const pollingStationCandidatesCache = ref<Record<string, AggregatedCandidateDTO[]>>({});
const expandedPollingStationId = ref<string | null>(null);
const loadingPollingStationId = ref<string | null>(null);

/* Chart */
const chartCanvas = ref<HTMLCanvasElement | null>(null);
let chart: Chart | null = null;

/* Check if current node is a polling station */
const isStembureau = computed(() =>
  node.value?.category === "Stembureau" ||
  props.municipalityId.startsWith("S_")
);

async function loadData() {
  loading.value = true;
  node.value = null;
  candidates.value = [];

  try {
    node.value = await getMunicipalityNode(props.year, props.municipalityId);
    candidates.value = await getCandidatesForMunicipality(props.year, props.municipalityId);

    await nextTick();
    renderChart();
  } catch (e) {
    console.error("Fout bij laden data:", e);
  } finally {
    loading.value = false;
  }
}

function renderChart() {
  if (!chartCanvas.value || !node.value) return;

  if (chart) {
    chart.destroy();
    chart = null;
  }

  const sortedParties = Object.entries(node.value.votesPerParty)
    .sort(([, a], [, b]) => b - a)
    .slice(0, 5);

  const labels = sortedParties.map(([party]) => party);
  const data = sortedParties.map(([, votes]) => votes);
  const colors = labels.map(
    party => partyColors[party] ?? "#9CA3AF"
  );

  chart = new Chart(chartCanvas.value, {
    type: "bar",
    data: {
      labels,
      datasets: [
        {
          label: "Stemmen",
          data,
          backgroundColor: colors,
          borderColor: colors,
          borderRadius: 4
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: false }
      },
      scales: {
        y: { beginAtZero: true },
        x: { grid: { display: false } }
      }
    }
  });
}

/* Polling station toggle */
async function togglePollingStation(id: string) {
  if (expandedPollingStationId.value === id) {
    expandedPollingStationId.value = null;
    return;
  }

  expandedPollingStationId.value = id;

  if (!pollingStationCandidatesCache.value[id]) {
    loadingPollingStationId.value = id;
    const data = await getCandidatesForPollingStation(props.year, id);
    pollingStationCandidatesCache.value[id] = data;
    loadingPollingStationId.value = null;
  }
}

onMounted(loadData);
watch(() => props.municipalityId, loadData);
watch(() => props.year, loadData);
</script>


<style scoped>
.municipality-header {
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 2px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 1rem;
  flex-wrap: wrap;
}

.municipality-header h2 {
  margin: 0;
  font-size: 1.85rem;
  font-weight: 800;
  color: #111827;
  display: flex;
  align-items: center;
  gap: 0.65rem;
  flex-wrap: wrap;
}



.stats-bar {
  display: flex;
  gap: 1.25rem;
  flex-wrap: wrap;
  align-items: flex-end;
}

.stat-badge {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  min-width: 140px;
}

.stat-label {
  font-size: 0.8rem;
  color: #6b7280;
  letter-spacing: 0.2px;
}

.stat-value {
  font-size: 1.35rem;
  font-weight: 800;
  color: #111827;
}

.chart-section {
  margin-bottom: 1.5rem;
}

.chart-section h3 {
  margin: 0 0 0.75rem;
  font-size: 1.15rem;
  font-weight: 800;
  color: #111827;
}

.chart-wrapper {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 0.9rem 1rem;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  height: 280px;
  max-width: 980px;
}

.chart-wrapper canvas {
  width: 100% !important;
  height: 100% !important;
}

.results-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;
  align-items: start;
  margin-bottom: 2rem;
}

.stembureaus-section {
  margin-top: 0.25rem;
}

.stembureaus-section h3 {
  margin: 0 0 0.85rem;
  font-size: 1.25rem;
  font-weight: 800;
  color: #111827;
}

.stembureau-list {
  display: flex;
  flex-direction: column;
  gap: 0.9rem;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table.small th,
.data-table.small td {
  padding: 10px 10px;
  font-size: 0.92rem;
}

.data-table thead th {
  background: #f3f4f6;
  color: #6b7280;
  font-weight: 800;
  text-align: left;
  border-bottom: 2px solid #e5e7eb;
  position: sticky;
  top: 0;
  z-index: 2;
}

.data-table tbody td {
  border-bottom: 1px solid #f1f1f1;
  color: #374151;
}

.data-table tbody tr:hover td {
  background: #f9fafb;
}

.party-tag {
  display: inline-flex;
  align-items: center;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 0.78rem;
  font-weight: 700;
  background: #eef2ff;
  color: #3f4cd0;
  border: 1px solid #c7d2fe;
}

.text-right {
  text-align: right;
}

.loading-small {
  color: #6b7280;
  font-style: italic;
  padding: 0.75rem 0.25rem;
}

.placeholder {
  padding: 1rem;
  color: #6b7280;
}

.placeholder.error {
  color: #991b1b;
  font-weight: 700;
}

@media (max-width: 1100px) {
  .results-grid {
    grid-template-columns: 1fr;
  }

  .stat-badge {
    align-items: flex-start;
    min-width: 0;
  }

  .chart-wrapper {
    max-width: 100%;
  }
}

@media (max-width: 520px) {
  .municipality-header h2 {
    font-size: 1.55rem;
  }

  .chart-wrapper {
    padding: 0.75rem;
    height: 240px;
  }

  .data-table.small th,
  .data-table.small td {
    padding: 9px 8px;
    font-size: 0.9rem;
  }
}
</style>
