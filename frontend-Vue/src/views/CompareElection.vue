<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import {
  getNationalResults,
  type ResultNodeDTO
} from '@/services/MunicipalityService';

/**
 * CompareView
 * * Allows users to compare election results between two distinct years side-by-side.
 * * Uses 'getNationalResults' (Tree Node) to ensure totals match the main dashboard exactly.
 */

const availableYears = [2021, 2023, 2025];

const yearLeft = ref<number>(2021);
const yearRight = ref<number>(2023);

const loading = ref<boolean>(false);
const error = ref<string | null>(null);

// We store the full node (ResultNodeDTO) which contains 'votesPerParty' and 'totalVotes'
const nodeLeft = ref<ResultNodeDTO | null>(null);
const nodeRight = ref<ResultNodeDTO | null>(null);

/**
 * Merges results from both selected years.
 * * Uses 'votesPerParty' map from the backend to guarantee correct party totals.
 * * Calculates percentages dynamically based on the year's turnout (totalVotes).
 */
const comparisonData = computed(() => {
  if (!nodeLeft.value || !nodeRight.value) return [];

  // Get the maps
  const mapLeft = nodeLeft.value.votesPerParty || {};
  const mapRight = nodeRight.value.votesPerParty || {};

  // Collect unique party names from both years
  const allParties = new Set([
    ...Object.keys(mapLeft),
    ...Object.keys(mapRight)
  ]);

  const merged = [];

  for (const party of allParties) {
    // Get absolute votes (default to 0 if party didn't exist)
    const votesLeft = mapLeft[party] || 0;
    const votesRight = mapRight[party] || 0;

    // Calculate percentages manually: (PartyVotes / TotalVotesInCountry) * 100
    // Prevent division by zero with ( || 1 )
    const percLeft = (votesLeft / (nodeLeft.value.totalVotes || 1)) * 100;
    const percRight = (votesRight / (nodeRight.value.totalVotes || 1)) * 100;

    const diffVotes = votesRight - votesLeft;
    const diffPerc = percRight - percLeft;

    merged.push({
      partyName: party,
      votesLeft,
      percLeft,
      votesRight,
      percRight,
      diffVotes,
      diffPerc
    });
  }

  // Sort by the vote count of the 'right' (target) year
  return merged.sort((a, b) => b.votesRight - a.votesRight);
});

/**
 * Fetches national root nodes for both years using Promise.all.
 */
const fetchData = async () => {
  if (yearLeft.value === yearRight.value) {
    error.value = "Selecteer twee verschillende jaren om te vergelijken.";
    return;
  }

  loading.value = true;
  error.value = null;

  try {
    const [resLeft, resRight] = await Promise.all([
      getNationalResults(yearLeft.value),
      getNationalResults(yearRight.value)
    ]);

    nodeLeft.value = resLeft;
    nodeRight.value = resRight;

  } catch (e) {
    error.value = "Kon de vergelijkingsdata niet laden. Probeer het later opnieuw.";
  } finally {
    loading.value = false;
  }
};

const formatNumber = (num: number) => {
  return num.toLocaleString('nl-NL');
};

const formatDiff = (num: number) => {
  const sign = num > 0 ? '+' : '';
  return `${sign}${num.toFixed(1)}%`;
};

onMounted(() => {
  fetchData();
});

watch([yearLeft, yearRight], () => {
  fetchData();
});
</script>

<template>
  <div class="page-container">

    <header class="compare-header">
      <h1>Verkiezingsresultaten Vergelijken</h1>
      <p>Analyseer de politieke verschuivingen door de jaren heen.</p>
    </header>

    <div class="controls-bar">
      <div class="select-group">
        <label>Basisjaar</label>
        <div class="select-wrapper">
          <select v-model="yearLeft" class="year-select">
            <option v-for="year in availableYears" :key="year" :value="year">{{ year }}</option>
          </select>
        </div>
      </div>

      <div class="vs-badge">VS</div>

      <div class="select-group">
        <label>Vergelijk met</label>
        <div class="select-wrapper">
          <select v-model="yearRight" class="year-select">
            <option v-for="year in availableYears" :key="year" :value="year">{{ year }}</option>
          </select>
        </div>
      </div>
    </div>

    <main class="content-area">

      <div v-if="loading" class="loading-box">
        <div class="spinner"></div>
        <span>Data ophalen...</span>
      </div>

      <div v-else-if="error" class="error-box">
        {{ error }}
      </div>

      <div v-else class="table-wrapper">
        <table class="compare-table">
          <thead>
          <tr>
            <th class="col-party">Partij</th>
            <th class="col-data">{{ yearLeft }} Resultaat</th>
            <th class="col-data">{{ yearRight }} Resultaat</th>
            <th class="col-diff">Verschil</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="row in comparisonData" :key="row.partyName">

            <td class="col-party">
              <div class="party-name">{{ row.partyName }}</div>
            </td>

            <td class="col-data">
              <div class="vote-count">{{ formatNumber(row.votesLeft) }} stemmen</div>
              <div class="perc-container">
                <div class="perc-bar-bg">
                  <div class="perc-bar-fill" :style="{ width: Math.min(row.percLeft, 100) + '%' }"></div>
                </div>
                <span class="perc-text">{{ row.percLeft.toFixed(1) }}%</span>
              </div>
            </td>

            <td class="col-data">
              <div class="vote-count">{{ formatNumber(row.votesRight) }} stemmen</div>
              <div class="perc-container">
                <div class="perc-bar-bg">
                  <div class="perc-bar-fill" :style="{ width: Math.min(row.percRight, 100) + '%' }"></div>
                </div>
                <span class="perc-text">{{ row.percRight.toFixed(1) }}%</span>
              </div>
            </td>

            <td class="col-diff">
              <div
                class="diff-badge"
                :class="{
                    'positive': row.diffPerc > 0,
                    'negative': row.diffPerc < 0,
                    'neutral': row.diffPerc === 0
                  }"
              >
                <span v-if="row.diffPerc > 0">▲</span>
                <span v-if="row.diffPerc < 0">▼</span>
                {{ formatDiff(row.diffPerc) }}
              </div>
              <div class="diff-votes">
                {{ row.diffVotes > 0 ? '+' : '' }}{{ formatNumber(row.diffVotes) }} stemmen
              </div>
            </td>

          </tr>
          </tbody>
        </table>
      </div>

    </main>
  </div>
</template>

<style scoped>
/* --- GLOBAL LAYOUT --- */
.page-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 40px 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  color: #333;
}

/* --- HEADER --- */
.compare-header {
  text-align: center;
  margin-bottom: 30px;
}
.compare-header h1 {
  color: #001b5e;
  font-size: 2rem;
  margin-bottom: 0.5rem;
}
.compare-header p {
  color: #6b7280;
  font-size: 1.1rem;
}

/* --- CONTROLS BAR --- */
.controls-bar {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 30px;
  background: white;
  padding: 25px;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  margin-bottom: 30px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.02);
}

.select-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.select-group label {
  font-size: 0.9rem;
  font-weight: 600;
  color: #001b5e;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.select-wrapper {
  position: relative;
}

.year-select {
  padding: 12px 16px;
  font-size: 1.1rem;
  border-radius: 8px;
  border: 1px solid #d1d5db;
  min-width: 140px;
  cursor: pointer;
  background-color: #fff;
  color: #333;
  transition: border-color 0.2s, box-shadow 0.2s;
  appearance: none;
  background-image: url("data:image/svg+xml;charset=US-ASCII,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22292.4%22%20height%3D%22292.4%22%3E%3Cpath%20fill%3D%22%23001b5e%22%20d%3D%22M287%2069.4a17.6%2017.6%200%200%200-13-5.4H18.4c-5%200-9.3%201.8-12.9%205.4A17.6%2017.6%200%200%200%200%2082.2c0%205%201.8%209.3%205.4%2012.9l128%20127.9c3.6%203.6%207.8%205.4%2012.8%205.4s9.2-1.8%2012.8-5.4L287%2095c3.5-3.5%205.4-7.8%205.4-12.8%200-5-1.9-9.2-5.5-12.8z%22%2F%3E%3C%2Fsvg%3E");
  background-repeat: no-repeat;
  background-position: right 12px top 50%;
  background-size: 10px auto;
  padding-right: 30px;
}

.year-select:focus {
  outline: none;
  border-color: #001b5e;
  box-shadow: 0 0 0 3px rgba(0, 27, 94, 0.1);
}

.vs-badge {
  background-color: #001b5e;
  color: white;
  font-weight: 700;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-size: 0.9rem;
  margin-top: 24px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

/* --- STATES --- */
.loading-box {
  text-align: center;
  padding: 50px;
  color: #6b7280;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}
.spinner {
  border: 3px solid #f3f3f3;
  border-top: 3px solid #001b5e;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  animation: spin 1s linear infinite;
}
@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }

.error-box {
  text-align: center;
  padding: 20px;
  background: #fef2f2;
  color: #991b1b;
  border: 1px solid #fecaca;
  border-radius: 8px;
  margin-top: 20px;
}

/* --- TABLE --- */
.table-wrapper {
  overflow-x: auto;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 4px 6px rgba(0,0,0,0.02);
  background: white;
}

.compare-table {
  width: 100%;
  border-collapse: collapse;
}

.compare-table th {
  background-color: #f9fafb;
  color: #374151;
  font-weight: 700;
  text-align: left;
  padding: 16px;
  border-bottom: 2px solid #e5e7eb;
  font-size: 0.95rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.compare-table td {
  padding: 16px;
  border-bottom: 1px solid #f3f4f6;
  vertical-align: middle;
}

.compare-table tr:last-child td { border-bottom: none; }
.compare-table tr:hover { background-color: #f8fafc; }

/* Columns */
.col-party { width: 20%; }
.col-data { width: 30%; }
.col-diff { width: 20%; text-align: right; }

/* Party Name */
.party-name {
  font-weight: 700;
  font-size: 1.1rem;
  color: #111827;
}

/* Data Bars */
.vote-count {
  font-size: 0.85rem;
  color: #6b7280;
  margin-bottom: 6px;
}

.perc-container {
  display: flex;
  align-items: center;
  gap: 10px;
}

.perc-bar-bg {
  flex-grow: 1;
  height: 8px;
  background-color: #e5e7eb;
  border-radius: 4px;
  overflow: hidden;
}

.perc-bar-fill {
  height: 100%;
  background-color: #001b5e;
  border-radius: 4px;
  transition: width 0.5s ease-out;
}

.perc-text {
  font-weight: 700;
  font-size: 0.95rem;
  color: #374151;
  min-width: 45px;
  text-align: right;
}

/* Difference Badge */
.diff-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 6px;
  font-weight: 700;
  font-size: 0.9rem;
  margin-bottom: 4px;
}

.diff-badge.positive { background-color: #dcfce7; color: #166534; }
.diff-badge.negative { background-color: #fee2e2; color: #991b1b; }
.diff-badge.neutral  { background-color: #f3f4f6; color: #4b5563; }

.diff-votes {
  font-size: 0.8rem;
  color: #6b7280;
  font-weight: 500;
}
</style>
