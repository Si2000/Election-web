<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();

const loading = ref(true);
const message = ref("");
const error = ref("");

onMounted(async () => {
  const token = route.query.token as string | undefined;

  if (!token) {
    error.value = "Geen token gevonden in de link.";
    loading.value = false;
    return;
  }

  try {
    const res = await fetch(
      `http://oege.ie.hva.nl:8888/api/user/confirm-email-change?token=${encodeURIComponent(token)}`
    );

    const text = await res.text();
    if (!res.ok) throw new Error(text || "Bevestigen mislukt.");

    message.value = "E-mailadres succesvol gewijzigd!";
    setTimeout(() => router.push("/account"), 1500);
  } catch (e: any) {
    error.value = e.message || "Bevestigen mislukt.";
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div style="max-width: 650px; margin: 40px auto; padding: 20px;">
    <h1>E-mail bevestigen</h1>

    <p v-if="loading">Bezig met bevestigen...</p>

    <p v-if="message" style="color: green; font-weight: 600;">
      {{ message }}
    </p>

    <p v-if="error" style="color: red; font-weight: 600;">
      {{ error }}
    </p>

    <button
      v-if="!loading"
      @click="router.push('/account')"
      style="margin-top: 16px; padding: 10px 14px; border-radius: 8px; border: none; background: #001b5e; color: white; cursor: pointer;"
    >
      Terug naar account
    </button>
  </div>
</template>
