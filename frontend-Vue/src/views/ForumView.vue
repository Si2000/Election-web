<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getAllTopics, type Topic } from '@/services/TopicService'; // Let op 'type' import
import { userInfo } from '@/storages/userStorage'; // Store importeren

const store = userInfo(); // Store initialiseren
const topics = ref<Topic[]>([]);
const loading = ref(true);
const error = ref('');

const formatDate = (dateString: string) => {
  if(!dateString) return '';
  return new Date(dateString).toLocaleDateString('nl-NL', {
    day: 'numeric', month: 'long', year: 'numeric', hour: '2-digit', minute: '2-digit'
  });
};

onMounted(async () => {
  try {
    topics.value = await getAllTopics();
  } catch (err) {
    error.value = 'Kon discussies niet laden. Probeer het later opnieuw.';
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="forum-container">
    <header class="forum-header">
      <h1>Politiek Forum</h1>
      <p>Praat mee over de laatste ontwikkelingen.</p>

      <div class="header-actions">
        <router-link v-if="store.isLoggedIn" to="/create-topic" class="btn-create">
          + Start Nieuwe Discussie
        </router-link>
        <div v-else class="login-hint">
          <router-link to="/login">Log in</router-link> om mee te praten.
        </div>
      </div>
    </header>

    <div v-if="loading" class="loading">Discussies laden...</div>
    <div v-else-if="error" class="error">{{ error }}</div>

    <div v-else class="topics-list">
      <div v-if="topics.length === 0" class="empty-state">
        Nog geen discussies. Wees de eerste!
      </div>

      <article
        v-for="topic in topics"
        :key="topic.id"
        class="topic-card"
        @click="$router.push(`/forum/${topic.id}`)"
      >
        <div class="topic-content">
          <h2 class="topic-title">{{ topic.title }}</h2>
          <p class="topic-meta">
            Gestart door <strong>{{ topic.authorName }}</strong> op {{ formatDate(topic.createdAt) }}
          </p>
        </div>

        <div class="topic-action">
          <!-- NEW: Like counter (uses backend topic.likeCount) -->
          <div class="like-badge" title="Aantal likes">
            <span class="heart">♥</span>
            <span class="count">{{ topic.likeCount ?? 0 }}</span>
          </div>

          <span class="arrow">→</span>
        </div>
      </article>
    </div>
  </div>
</template>

<style scoped>
/* Alleen extra styling voor de like badge, de rest laat je zoals je het al had */

.topic-action {
  display: flex;
  align-items: center;
  gap: 10px;
}

.like-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 10px;
  border-radius: 999px;
  border: 1px solid #e6e6e6;
  background: #f7f8fc;
  color: #001b5e;
  font-weight: 700;
  white-space: nowrap;
}

.like-badge .heart {
  font-size: 0.95rem;
}

.like-badge .count {
  font-size: 0.92rem;
}
</style>
