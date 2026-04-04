<template>
  <div
    class="stembureau-item"
    :class="{ 'is-expanded': isExpanded }"
  >
    <div
      class="stembureau-header"
      @click="$emit('toggle')"
    >
      <div class="header-left">
        <span class="toggle-icon">{{ isExpanded ? '▼' : '▶' }}</span>
        <span class="station-name">{{ title }}</span>
      </div>

      <div
        v-if="totalVotes !== null && totalVotes !== undefined"
        class="header-right"
      >
        <span class="station-votes">
          {{ totalVotes }} {{ votesLabel }}
        </span>
      </div>
    </div>

    <div
      v-if="isExpanded"
      class="stembureau-details"
    >
      <div
        v-if="isLoading"
        class="loading-small"
      >
        <slot name="loading">Loading...</slot>
      </div>

      <slot v-else />
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * Props used to configure the expandable card.
 * This component is used for:
 *   - Constituency → Municipality list
 *   - National → Constituency list
 *   - Municipality → Polling station list
 */
const props = defineProps<{
  /** Title displayed on the left side of the header */
  title: string;

  /** Optional vote count shown on the right side */
  totalVotes?: number | null;

  /** Label after vote count (default: "Votes") */
  votesLabel?: string;

  /** Whether the card is currently expanded */
  isExpanded: boolean;

  /** Whether the inner content is loading */
  isLoading?: boolean;
}>();

/**
 * Component emits:
 *   - "toggle": parent should flip expanded state
 */
defineEmits<{
  (e: 'toggle'): void;
}>();
</script>
<style scoped>
.stembureau-item {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  overflow: hidden;
  background: #fff;
  transition: box-shadow 0.18s ease, border-color 0.18s ease, transform 0.18s ease;
}

.stembureau-item.is-expanded {
  border-color: #c7d2fe;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.10);
}

.stembureau-header {
  background: #fff;
  padding: 0.95rem 1rem;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  user-select: none;
}

.stembureau-header:hover {
  background: #f5f7ff;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 0.65rem;
  min-width: 0;
}

.toggle-icon {
  font-size: 0.85rem;
  color: #6b7280;
  width: 18px;
  display: inline-flex;
  justify-content: center;
  flex: 0 0 auto;
}

.station-name {
  font-weight: 800;
  font-size: 1rem;
  color: #111827;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.header-right {
  flex: 0 0 auto;
}

.station-votes {
  font-weight: 800;
  background: #e7f7ef;
  color: #1b7f46;
  padding: 6px 10px;
  border-radius: 999px;
  font-size: 0.9rem;
  border: 1px solid #b7e4cc;
  white-space: nowrap;
}

.stembureau-details {
  background: #fafbff;
  padding: 0.95rem 1rem;
  border-top: 1px solid #e5e7eb;
  max-height: 340px;
  overflow-y: auto;
}

.loading-small {
  color: #6b7280;
  font-style: italic;
  padding: 0.25rem 0;
}

@media (max-width: 520px) {
  .stembureau-header {
    padding: 0.85rem 0.9rem;
  }

  .station-votes {
    padding: 5px 9px;
    font-size: 0.85rem;
  }

  .stembureau-details {
    padding: 0.85rem 0.9rem;
    max-height: 300px;
  }
}
</style>
