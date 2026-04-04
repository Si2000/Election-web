<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();
const message = ref("Je account wordt geverifieerd...");
const isSuccess = ref(false);

onMounted(async () => {
  // Haal de token uit de URL (bijv. http://localhost:5173/verify-account?token=XYZ)
  const token = route.query.token;

  if (!token) {
    message.value = "Geen verificatiecode gevonden in de link.";
    return;
  }

  try {
    // Stuur de token naar de backend
    const response = await fetch('http://oege.ie.hva.nl:8888/auth/verify', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ token: token })
    });

    const data = await response.json();

    if (response.ok) {
      isSuccess.value = true;
      message.value = "Succes! Je account is geverifieerd. Je wordt doorgestuurd naar de login pagina...";

      // Na 3 seconden doorsturen naar login
      setTimeout(() => {
        router.push('/login');
      }, 3000);
    } else {
      // Foutmelding van backend tonen (bijv. "Token verlopen")
      message.value = data.message || "Verificatie mislukt.";
    }
  } catch (e) {
    message.value = "Kan geen verbinding maken met de server.";
  }
});
</script>

<template>
  <div class="verify-container">
    <div class="card" :class="{ success: isSuccess, error: !isSuccess && message !== 'Je account wordt geverifieerd...' }">
      <h2>Account Verificatie</h2>
      <p>{{ message }}</p>

      <router-link v-if="!isSuccess && message !== 'Je account wordt geverifieerd...'" to="/login" class="btn-link">
        Terug naar Inloggen
      </router-link>
    </div>
  </div>
</template>

