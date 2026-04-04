<template>
  <div v-if="root">

    <header class="municipality-header">
      <h2>Nederland</h2>

      <div class="stats-bar">
        <div class="stat-badge">
          <span class="stat-label">Totaal stemmen</span>
          <span class="stat-value">
            {{ root.totalVotes.toLocaleString() }}
          </span>
        </div>

        <div class="stat-badge">
          <span class="stat-label">Kieskringen</span>
          <span class="stat-value">
            {{ root.children.length }}
          </span>
        </div>
      </div>
    </header>

    <div class="chart-section">
      <h3>Top 5 Partijen</h3>
      <div class="chart-wrapper" >
        <canvas ref="chartCanvas"></canvas>
      </div>
    </div>

    <!-- =========================
         National Party + Candidate Results
         ========================= -->
    <div class="results-grid">
      <PartyResultTable
        :votes-per-party="root.votesPerParty"
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

    <section class="stembureaus-section">
      <h3>Resultaten per Kieskring</h3>

      <div class="stembureau-list">
        <ExpandableCard
          v-for="kr in root.children"
          :key="kr.id"
          :title="kr.name"
          :total-votes="kr.totalVotes"
          :votes-label="'Stemmen'"
          :is-expanded="expandedConstituencyId === kr.id"
          :is-loading="loadingConstituencyId === kr.id && !constituencyCandidatesCache[kr.id]"
          @toggle="toggleConstituency(kr.id)"
        >
          <template #default>
            <CandidateTable
              v-if="constituencyCandidatesCache[kr.id]"
              :candidates="constituencyCandidatesCache[kr.id]"
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
</template>

<script setup lang="ts">
import { ref, watch, onMounted, nextTick } from "vue";
import {
  getCandidatesForConstituency,
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

Chart.register(
  BarController,
  BarElement,
  CategoryScale,
  LinearScale,
  Tooltip,
  Legend
);

/* =========================
   Props
   ========================= */
const props = defineProps<{
  root: ResultNodeDTO;
  candidates: AggregatedCandidateDTO[];
  year: number;
}>();

/* =========================
   Local state
   ========================= */
const expandedConstituencyId = ref<string | null>(null);
const loadingConstituencyId = ref<string | null>(null);
const constituencyCandidatesCache = ref<Record<string, AggregatedCandidateDTO[]>>({});

/* =========================
   Chart state
   ========================= */
const parties: string[] = [];
const votes: number[] = [];
const colors: string[] = [];

const chartCanvas = ref<HTMLCanvasElement | null>(null);
let chart: Chart | null = null;

/* =========================
   Chart preparation
   ========================= */
function makeChart(root: ResultNodeDTO): void {
  const entries = Object.entries(root.votesPerParty)
    .map(([party, count]) => ({ party, count }))
    .sort((a, b) => b.count - a.count)
    .slice(0, 5);

  parties.length = 0;
  votes.length = 0;
  colors.length = 0;

  for (const item of entries) {
    parties.push(item.party);
    votes.push(item.count);
    colors.push(partyColors[item.party] ?? "#9CA3AF"); // fallback gray
  }
}

/* =========================
   Constituency toggle
   ========================= */
async function toggleConstituency(id: string): Promise<void> {
  if (expandedConstituencyId.value === id) {
    expandedConstituencyId.value = null;
    return;
  }

  expandedConstituencyId.value = id;

  if (!constituencyCandidatesCache.value[id]) {
    loadingConstituencyId.value = id;
    try {
      const data = await getCandidatesForConstituency(props.year, id);
      constituencyCandidatesCache.value[id] = data;
    } catch (err) {
      console.error("Error loading constituency candidates:", err);
    } finally {
      loadingConstituencyId.value = null;
    }
  }
}

/* =========================
   Watch root → rebuild chart
   ========================= */
watch(
  () => props.root,
  async (newRoot) => {
    if (!newRoot) return;

    makeChart(newRoot);
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
            label: "Votes",
            data: votes,
            backgroundColor: colors,
            borderColor: colors,
            borderWidth: 1
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
  },
  { immediate: true }
);

onMounted(() => {
  makeChart(props.root);
});
</script>
<style scoped>
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
