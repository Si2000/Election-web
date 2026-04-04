<script setup lang="ts">
import { defineProps } from 'vue';

/**
 * @interface Candidate
 * Defines the structure for a candidate object.
 */
interface Candidate {
  /** The unique database ID of the candidate, used as the key for list iteration. */
  dbId: number;
  /** The first name of the candidate. */
  firstname: string;
  /** The initials of the candidate. */
  initials: string;
  /** The last name of the candidate. */
  lastname: string;
  /** Optional gender of the candidate ('male', 'female', or 'NULL'). */
  gender?: string;
  /** The name or abbreviation of the political party. */
  party: string;
  /** The locality or residence of the candidate. */
  locality: string;
}

/**
 * @props
 * Defines the properties (props) received by the component.
 */
const props = defineProps<{
  /** Array of Candidate objects to be displayed. */
  candidates: Candidate[]
}>();
</script>

<template>
  <div class="candidates-container">
    <div
      v-for="candidate in props.candidates"
      :key="candidate.dbId"
      class="candidate-card"
    >
      <div class="candidate-name">
        {{ candidate.firstname }} {{ candidate.initials }} {{ candidate.lastname }}
      </div>
      <div class="candidate-info">
        <span
          v-if="candidate.gender && candidate.gender.trim() && candidate.gender !== 'NULL'"
          :class="[
            'candidate-badge',
            candidate.gender === 'male' ? 'gender-m' : '',
            candidate.gender === 'female' ? 'gender-v' : ''
          ]"
        >
          {{ candidate.gender }}
        </span>
        <span class="candidate-badge">{{ candidate.party }}</span>
        <span class="candidate-badge">{{ candidate.locality }}</span>
      </div>
    </div>
    <p v-if="!props.candidates || props.candidates.length === 0">Geen kandidaten gevonden.</p>
  </div>
</template>
