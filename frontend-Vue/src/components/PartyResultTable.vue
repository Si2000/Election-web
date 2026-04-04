<template>
  <section class="tb">
    <h3>{{ title }}</h3>
    <div class="table-container">
      <table class="data-table">
        <thead>
        <tr>
          <th>{{ partyHeader }}</th>
          <th class="text-right">{{ votesHeader }}</th>
        </tr>
        </thead>
        <tbody>
        <tr
          v-for="row in rows"
          :key="row.name"
        >
          <td>{{ row.name }}</td>
          <td class=" font-bold">
            {{ row.votes.toLocaleString() }}
          </td>
        </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue';

const props = defineProps<{
  /** A map of party name → number of votes */
  votesPerParty: Record<string, number>;

  /** Optional title for the table header */
  title?: string;

  /** Optional label for the "party" column */
  partyHeader?: string;

  /** Optional label for the "votes" column */
  votesHeader?: string;
}>();

interface Row {
  name: string;
  votes: number;
}

/**
 * Convert the votesPerParty object into an array of
 * rows that can be rendered in a table.
 * Also sorts descending by vote count.
 */
function computeRows(): Row[] {
  if (!props.votesPerParty) {
    return [];
  }

  return Object.entries(props.votesPerParty)
    .map(function (entry) {
      return {
        name: entry[0],
        votes: entry[1]
      } as Row;
    })
    .sort(function (a, b) {
      return b.votes - a.votes;
    });
}

/** Reactive list of sorted rows */
const rows = computed(computeRows);

/** Fallback values for customizable props */
const title = computed(function () {
  return props.title ?? 'Party Result';
});
const partyHeader = computed(function () {
  return props.partyHeader ?? 'Party';
});
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
}

.data-table tbody tr:hover td {
  background: #f9fafb;
}


.font-bold {
  font-weight: 800;
}

@media (max-width: 520px) {
  .tb > h3 {
    padding: 0.85rem 0.9rem;
  }

  .data-table thead th,
  .data-table tbody td {
    padding: 9px 10px;
  }

  .table-container {
    max-height: 420px;
  }
}
</style>
