<template>
  <div v-if="node">
    <header class="municipality-header">
      <h2>Municipality {{ node.name }}</h2>
      <div class="stats-bar">
        <div class="stat-badge">
          <span class="stat-label">Totaal stemmen</span>
          <span class="stat-value">{{ node.totalVotes.toLocaleString() }}</span>
        </div>
        <div class="stat-badge">
          <span class="stat-label">Polling Stations</span>
          <span class="stat-value">{{ node.children?.length ?? 0 }}</span>
        </div>
      </div>
    </header>

    <h2>TOP 5 Partijen</h2>
    <canvas ref="chartCanvas" width="400" height="200"></canvas>

    <div class="results-grid">
      <PartyResultTable
        :votes-per-party="node.votesPerParty"
        title="Party Result"
        party-header="Party"
        votes-header="Votes"
      />
      <CandidateTable
        :candidates="candidates"
        title="Candidate Votes"
        name-header="Name"
        votes-header="Votes"
      />
    </div>

    <section class="stembureaus-section">
      <h3>Polling Station Results</h3>
      <div class="stembureau-list">
        <ExpandableCard
          v-for="station in node.children"
          :key="station.id"
          :title="station.name"
          :total-votes="station.totalVotes"
          votes-label="Votes"
          :is-expanded="expandedPollingStationId === station.id"
          :is-loading="loadingPollingStationId === station.id && !pollingStationCandidatesCache[station.id]"
          @toggle="togglePollingStation(station.id)"
        >
          <template #default>
            <table v-if="pollingStationCandidatesCache[station.id]" class="data-table small">
              <thead>
              <tr>
                <th>Candidate</th>
                <th>Party</th>
                <th class="text-right">Votes</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="c in pollingStationCandidatesCache[station.id]" :key="c.id">
                <td>{{ c.name }}</td>
                <td><span class="party-tag">{{ c.partyName }}</span></td>
                <td class="text-right">{{ c.votes }}</td>
              </tr>
              </tbody>
            </table>
            <div v-else class="loading-small">Waiting for result...</div>
          </template>
        </ExpandableCard>
      </div>
    </section>
  </div>

  <div v-else-if="loading" class="placeholder">
    Loading...
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, nextTick } from 'vue';
import { getMunicipalityNode, getCandidatesForMunicipality, getCandidatesForPollingStation, type ResultNodeDTO, type AggregatedCandidateDTO } from '@/services/MunicipalityService.ts';
import PartyResultTable from './PartyResultTable.vue';
import CandidateTable from './CandidateTable.vue';
import ExpandableCard from './ExpandableCard.vue';
import { Chart, BarController, BarElement, CategoryScale, LinearScale, Tooltip, Legend } from "chart.js";

Chart.register(BarController, BarElement, CategoryScale, LinearScale, Tooltip, Legend);

const props = defineProps<{ municipalityId: string; year: number; }>();
const node = ref<ResultNodeDTO | null>(null);
const candidates = ref<AggregatedCandidateDTO[]>([]);
const loading = ref(false);
const pollingStationCandidatesCache = ref<Record<string, AggregatedCandidateDTO[]>>({});
const expandedPollingStationId = ref<string | null>(null);
const loadingPollingStationId = ref<string | null>(null);

const parties: string[] = [];
const votes: number[] = [];
const chartCanvas = ref<HTMLCanvasElement | null>(null);
let chart: Chart | null = null;

async function loadMunicipality() {
  loading.value = true;
  node.value = null;
  candidates.value = [];
  pollingStationCandidatesCache.value = {};
  expandedPollingStationId.value = null;
  loadingPollingStationId.value = null;

  try {
    const [nodeData, candidateData] = await Promise.all([
      getMunicipalityNode(props.year, props.municipalityId),
      getCandidatesForMunicipality(props.year, props.municipalityId)
    ]);
    node.value = nodeData;
    candidates.value = candidateData;

    const entries = Object.entries(node.value!.votesPerParty).map(([party, count]) => ({ party, count }));
    entries.sort((a, b) => b.count - a.count);
    const top5 = entries.slice(0, 5);

    parties.length = 0;
    votes.length = 0;
    for (const item of top5) {
      parties.push(item.party);
      votes.push(item.count);
    }
  } catch (err) {
    console.error(err);
  } finally {
    loading.value = false;
  }
}

async function togglePollingStation(id: string) {
  if (expandedPollingStationId.value === id) { expandedPollingStationId.value = null; return; }
  expandedPollingStationId.value = id;
  if (!pollingStationCandidatesCache.value[id]) {
    loadingPollingStationId.value = id;
    try { pollingStationCandidatesCache.value[id] = await getCandidatesForPollingStation(props.year, id); }
    catch (err) { console.error(err); }
    finally { loadingPollingStationId.value = null; }
  }
}

watch(node, async () => {
  if (!node.value) return;
  await nextTick();
  if (!chartCanvas.value) return;
  if (chart) chart.destroy();
  chart = new Chart(chartCanvas.value, {
    type: 'bar',
    data: { labels: parties, datasets: [{ label: 'Votes', data: votes, backgroundColor: 'rgba(54,162,235,0.6)', borderColor: 'rgba(54,162,235,1)', borderWidth: 1 }] },
    options: { responsive: true, scales: { y: { beginAtZero: true } } }
  });
});

onMounted(loadMunicipality);
watch(() => props.municipalityId, loadMunicipality);
</script>
