<script setup lang="ts">
import { ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();

const password = ref('');
const confirmPassword = ref('');
const message = ref('');
const error = ref('');
const loading = ref(false);

const handleReset = async () => {
  error.value = '';
  message.value = '';

  if (password.value !== confirmPassword.value) {
    error.value = "Wachtwoorden komen niet overeen.";
    return;
  }

  loading.value = true;
  const token = route.query.token; // Haal token uit URL

  try {
    const response = await fetch('http://oege.ie.hva.nl:8888/auth/reset-password', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ token: token, newPassword: password.value })
    });

    if (response.ok) {
      message.value = "Wachtwoord gewijzigd! Je wordt doorgestuurd...";
      setTimeout(() => router.push('/login'), 3000);
    } else {
      error.value = "Link is ongeldig of verlopen.";
    }
  } catch (e) {
    error.value = "Serverfout bij resetten.";
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="auth-container">
    <h2>Nieuw Wachtwoord</h2>
    <p class="subtitle">Kies een nieuw, veilig wachtwoord.</p>

    <form @submit.prevent="handleReset">
      <div class="form-group">
        <label>Nieuw Wachtwoord:</label>
        <input v-model="password" type="password" required minlength="6" />
      </div>

      <div class="form-group">
        <label>Bevestig Wachtwoord:</label>
        <input v-model="confirmPassword" type="password" required />
      </div>

      <div v-if="error" class="error-message">{{ error }}</div>
      <div v-if="message" class="status-message success">{{ message }}</div>

      <button type="submit" class="btn-primary" :disabled="loading">Opslaan</button>
    </form>
  </div>
</template>

<style scoped>
@import '@/styles/auth.css';
</style>
