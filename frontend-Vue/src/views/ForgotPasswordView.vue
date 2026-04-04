<script setup lang="ts">
import { ref } from 'vue';

const email = ref('');
const message = ref('');
const loading = ref(false);

const handleSubmit = async () => {
  loading.value = true;
  message.value = '';

  try {
    const response = await fetch('http://oege.ie.hva.nl:8888/auth/forgot-password', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: email.value })
    });
    const data = await response.json();
    message.value = data.message; // "Als dit e-mailadres bekend is..."
  } catch (e) {
    message.value = "Er ging iets mis. Probeer het later opnieuw.";
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="auth-container">
    <h2>Wachtwoord Vergeten?</h2>
    <p class="subtitle">Vul je e-mailadres in om een herstellink te ontvangen.</p>

    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label>E-mail:</label>
        <input v-model="email" type="email" required placeholder="jouw@email.nl" />
      </div>

      <div v-if="message" class="status-message success">{{ message }}</div>

      <button type="submit" class="btn-primary" :disabled="loading">
        {{ loading ? 'Versturen...' : 'Stuur Herstellink' }}
      </button>

      <router-link to="/login" class="back-link">Terug naar inloggen</router-link>
    </form>
  </div>
</template>

<style scoped>
@import '@/styles/auth.css'; /* Hergebruik je bestaande auth styles */
.back-link { display: block; text-align: center; margin-top: 15px; color: #666; }
</style>
