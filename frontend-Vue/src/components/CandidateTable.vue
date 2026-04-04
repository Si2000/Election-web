<template>
  <section class="tb">
    <h3>{{ title }}</h3>

    <div class="table-container scrollable">
      <table class="data-table">
        <thead>
        <tr>
          <th>{{ nameHeader }}</th>
          <th class="text-right">{{ votesHeader }}</th>
        </tr>
        </thead>

        <tbody>
        <tr
          v-for="c in candidates"
          :key="c.id"
        >
          <td>
            <div class="candidate-name">{{ c.name }}</div>
            <div class="party-label">{{ c.partyName }}</div>
          </td>

          <td class=" font-bold">
            {{ c.votes.toLocaleString() }}
          </td>
        </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { AggregatedCandidateDTO } from '@/services/MunicipalityService.ts';

/**
 * Props passed from the parent view.
 * `candidates` is required; other props are optional labels/titles.
 */
const props = defineProps<{
  /** Array of candidate results to display */
  candidates: AggregatedCandidateDTO[];

  /** Optional table title */
  title?: string;

  /** Column header: candidate name */
  nameHeader?: string;

  /** Column header: votes */
  votesHeader?: string;
}>();

/** Title shown above the table */
const title = computed(function () {
  return props.title ?? 'Candidate Votes';
});

/** Column name for the candidate column */
const nameHeader = computed(function () {
  return props.nameHeader ?? 'Name';
});

/** Column name for the vote count column */
const votesHeader = computed(function () {
  return props.votesHeader ?? 'Votes';
});
</script>
<style scoped>
.tb {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  background: #fff;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.tb > h3 {
  margin: 0;
  padding: 0.95rem 1rem;
  font-size: 1.05rem;
  font-weight: 800;
  color: #111827;
  background: #f7f9fc;
  border-bottom: 1px solid #e5e7eb;
}

.table-container {
  width: 100%;
}

.scrollable {
  max-height: 460px;
  overflow: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table thead th {
  padding: 10px 12px;
  background: #f3f4f6;
  color: #6b7280;
  font-size: 0.9rem;
  font-weight: 800;
  border-bottom: 2px solid #e5e7eb;
  text-align: left;
  position: sticky;
  top: 0;
  z-index: 2;
}

.data-table tbody td {
  padding: 10px 12px;
  border-bottom: 1px solid #f1f1f1;
  color: #374151;
  font-size: 0.94rem;
  vertical-align: top;
}

.data-table tbody tr:hover td {
  background: #f9fafb;
}

.text-right {
  text-align: right;
}

.font-bold {
  font-weight: 800;
}

.candidate-name {
  font-weight: 800;
  color: #111827;
  line-height: 1.2;
}

.party-label {
  margin-top: 2px;
  font-size: 0.8rem;
  color: #6b7280;
}

@media (max-width: 520px) {
  .tb > h3 {
    padding: 0.85rem 0.9rem;
  }

  .data-table thead th,
  .data-table tbody td {
    padding: 9px 10px;
  }

  .scrollable {
    max-height: 420px;
  }
}
</style>
