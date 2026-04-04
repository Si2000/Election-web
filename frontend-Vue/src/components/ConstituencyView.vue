<template>
  <!-- ============================
       Main Kieskring View
     ============================ -->
  <div v-if="node">
    <header class="municipality-header">
      <h2>Kieskring {{ node.name }}</h2>

      <div class="stats-bar">
        <div class="stat-badge">
          <span class="stat-label">Total Votes</span>
          <span class="stat-value">
            {{ node.totalVotes.toLocaleString() }}
          </span>
        </div>

        <div class="stat-badge">
          <span class="stat-label">Municipalities</span>
          <span class="stat-value">
            {{ node.children?.length ?? 0 }}
          </span>
        </div>
      </div>
    </header>

    <!-- Chart -->
    <h2>TOP 5 Partijen</h2>
    <canvas ref="chartCanvas"  height="320"></canvas>

    <!-- ============================
         Party Results + Candidate Results
       ============================ -->
    <div class="results-grid">
      <PartyResultTable
        :votes-per-party="node.votesPerParty"
        title="Party Results"
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

    <!-- ============================
         Municipality List
       ============================ -->
    <section class="stembureaus-section">
      <h3>Resultaten per Municipality</h3>

      <div class="stembureau-list">
        <ExpandableCard
          v-for="m in node.children"
          :key="m.id"
          :title="m.name"
          :total-votes="m.totalVotes"
          votes-label="Votes"
          :is-expanded="expandedMunicipalityId === m.id"
          :is-loading="loadingMunicipalityId === m.id && !municipalityCandidatesCache[m.id]"
          @toggle="toggleMunicipality(m.id)"
        >
          <template #default>
            <CandidateTable
              v-if="municipalityCandidatesCache[m.id]"
              :candidates="municipalityCandidatesCache[m.id]"
              title="Uitslag per Kandidaat"
              name-header="Naam"
              votes-header="Stemmen"
            />

            <div v-else class="loading-small">
              Waiting for result...
            </div>
          </template>
        </ExpandableCard>
      </div>
    </section>
  </div>

  <!-- Loading state -->
  <div v-else-if="loading" class="placeholder">
    Loading...
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, nextTick } from "vue";
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

import {
  getConstituencyNode,
  getCandidatesForConstituency,
  getCandidatesForMunicipality,
  type ResultNodeDTO,
  type AggregatedCandidateDTO
} from "@/services/MunicipalityService.ts";

import PartyResultTable from "./PartyResultTable.vue";
import CandidateTable from "./CandidateTable.vue";
import ExpandableCard from "./ExpandableCard.vue";

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

/* Props */
const props = defineProps<{
  constituencyId: string;
  year: number;
}>();

/* Data state */
const node = ref<ResultNodeDTO | null>(null);
const candidates = ref<AggregatedCandidateDTO[]>([]);
const loading = ref(false);

/* Municipality lazy-load state */
const municipalityCandidatesCache = ref<Record<string, AggregatedCandidateDTO[]>>({});
const expandedMunicipalityId = ref<string | null>(null);
const loadingMunicipalityId = ref<string | null>(null);

/* Chart data */
const parties: string[] = [];
const votes: number[] = [];
const colors: string[] = [];

/* Canvas + Chart */
const chartCanvas = ref<HTMLCanvasElement | null>(null);
let chart: Chart | null = null;

/* =========================
   Load constituency
   ========================= */
async function loadConstituency(): Promise<void> {
  loading.value = true;
  node.value = null;
  candidates.value = [];

  try {
    const [nodeData, candidateData] = await Promise.all([
      getConstituencyNode(props.year, props.constituencyId),
      getCandidatesForConstituency(props.year, props.constituencyId)
    ]);

    node.value = nodeData;
    candidates.value = candidateData;

    const entries = Object.entries(nodeData.votesPerParty)
      .map(([party, count]) => ({ party, count }))
      .sort((a, b) => b.count - a.count)
      .slice(0, 5);

    parties.length = 0;
    votes.length = 0;
    colors.length = 0;

    for (const item of entries) {
      parties.push(item.party);
      votes.push(item.count);
      colors.push(partyColors[item.party] ?? "#9CA3AF");
    }
  } catch (err) {
    console.error("Failed to load constituency.", err);
  } finally {
    loading.value = false;
  }
}

/* =========================
   Municipality toggle
   ========================= */
async function toggleMunicipality(id: string): Promise<void> {
  if (expandedMunicipalityId.value === id) {
    expandedMunicipalityId.value = null;
    return;
  }

  expandedMunicipalityId.value = id;

  if (!municipalityCandidatesCache.value[id]) {
    loadingMunicipalityId.value = id;
    try {
      const data = await getCandidatesForMunicipality(props.year, id);
      municipalityCandidatesCache.value[id] = data;
    } catch (err) {
      console.error("Error loading municipality candidates", err);
    } finally {
      loadingMunicipalityId.value = null;
    }
  }
}

/* =========================
   Build chart AFTER DOM render
   ========================= */
watch(node, async (newNode) => {
  if (!newNode) return;

  await nextTick();

  if (!chartCanvas.value) return;

  if (chart) {
    chart.destroy();
    chart = null;
  }

  chart = new Chart(chartCanvas.value, {
    type: "bar",
    data: {
      labels: parties,
      datasets: [
        {
          label: "Stemmen",
          data: votes,
          backgroundColor: colors,
          borderColor: colors,
          borderWidth: 1
        }
      ]
    },
    options: {
      responsive: true,
      plugins: {
        legend: { display: false },
        tooltip: { enabled: false }
      },
      scales: {
        y: { beginAtZero: true }
      }
    }
  });
});

/* Initial load */
onMounted(loadConstituency);

/* Reload when constituencyId changes */
watch(
  () => props.constituencyId,
  async () => {
    await loadConstituency();
  }
);
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
  font-size: 1.9rem;
  font-weight: 800;
  color: #111827;
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

h2 {
  margin: 0.5rem 0 0.75rem;
  font-size: 1.1rem;
  font-weight: 800;
  color: #1f2937;
}

canvas {
  width: 100%;
  max-width: 980px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 0.75rem;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  margin-bottom: 1.5rem;
}

.results-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;
  align-items: start;
  margin-bottom: 2rem;
}

.stembureaus-section h3 {
  margin: 0 0 0.75rem;
  font-size: 1.25rem;
  font-weight: 800;
  color: #111827;
}

.stembureau-list {
  display: flex;
  flex-direction: column;
  gap: 0.9rem;
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

@media (max-width: 1100px) {
  .results-grid {
    grid-template-columns: 1fr;
  }

  .stat-badge {
    align-items: flex-start;
  }
}

@media (max-width: 520px) {
  .municipality-header h2 {
    font-size: 1.55rem;
  }

  .stat-badge {
    min-width: 0;
  }

  canvas {
    padding: 0.6rem;
  }
}
</style>
