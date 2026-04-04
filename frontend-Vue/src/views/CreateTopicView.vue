<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { createTopic } from '@/services/TopicService';

const title = ref('');
const content = ref('');
const error = ref('');
const loading = ref(false);
const router = useRouter();

const handleSubmit = async () => {
  error.value = '';

  if (!title.value.trim() || !content.value.trim()) {
    error.value = "Titel en inhoud zijn verplicht.";
    return;
  }

  loading.value = true;

  try {
    await createTopic(title.value, content.value);
    // Succes: stuur terug naar het forum
    router.push('/forum');
  } catch (e: any) {
    error.value = "Er ging iets mis bij het opslaan: " + e.message;
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="create-topic-container">
    <div class="card">
      <h1>Nieuwe Discussie Starten</h1>
      <p class="subtitle">Start een gesprek over een politiek onderwerp.</p>

      <form @submit.prevent="handleSubmit" class="topic-form">
        <div class="form-group">
          <label for="title">Onderwerp (Titel)</label>
          <input
            id="title"
            v-model="title"
            type="text"
            placeholder="Bijv: Wat vinden jullie van de nieuwe onderwijsplannen?"
            required
          />
        </div>

        <div class="form-group">
          <label for="content">Jouw Bericht</label>
          <textarea
            id="content"
            v-model="content"
            rows="6"
            placeholder="Deel hier je mening en argumenten..."
            required
          ></textarea>
        </div>

        <div v-if="error" class="error-message">{{ error }}</div>

        <div class="actions">
          <button type="submit" class="btn-primary" :disabled="loading">
            {{ loading ? 'Bezig met plaatsen...' : 'Plaats Discussie' }}
          </button>
          <router-link to="/forum" class="btn-cancel">Annuleren</router-link>
        </div>
      </form>
    </div>
  </div>
</template>
