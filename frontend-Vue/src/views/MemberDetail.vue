<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();
const id = route.params.id as string; // Guid als string

const loading = ref(true);
const error = ref<string | null>(null);

const member = ref<null | {
  name: string;
  birth: string;
  functie: string | null;
  woonplaats: string | null;
  photo: string;
}>(null);

onMounted(loadMember);

async function loadMember() {
  try {
    const resp = await fetch(
      `https://gegevensmagazijn.tweedekamer.nl/OData/v4/2.0/Persoon(${id})`
    );

    if (!resp.ok) throw new Error("Persoon niet gevonden");

    const p = await resp.json();

    // FOTO NIET AANGEPAST — PRECIES zoals jij het had
    const photo = `https://gegevensmagazijn.tweedekamer.nl/OData/v4/2.0/Persoon/${id}/resource`;

    member.value = {
      name: `${p.Roepnaam ?? ""} ${p.Tussenvoegsel ?? ""} ${p.Achternaam ?? ""}`.trim(),
      birth: p.Geboortedatum
        ? new Date(p.Geboortedatum).toLocaleDateString("nl-NL")
        : "Onbekend",
      functie: p.Functie,
      woonplaats: p.Woonplaats,
      photo,
    };
  } catch (e: any) {
    console.error(e);
    error.value = e?.message ?? "Er ging iets mis.";
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <div class="detail-page">
    <div v-if="loading" class="loading">Laden…</div>

    <div v-else-if="error" class="error">
      {{ error }}
      <router-link to="/members" class="back">← Terug</router-link>
    </div>

    <div v-else class="content">
      <div class="header">
        <!-- FOTO PRECIES ALS EERDER -->
        <img
          :src="member?.photo"
          class="photo"
          @error="($event.target as HTMLImageElement).src = '/placeholder.jpg'"
        />

        <div class="header-text">
          <h1>{{ member?.name }}</h1>
          <p><strong>Functie:</strong> {{ member?.functie }}</p>
          <p><strong>Geboortedatum:</strong> {{ member?.birth }}</p>
          <p><strong>Woonplaats:</strong> {{ member?.woonplaats }}</p>
        </div>
      </div>

      <router-link to="/members" class="back">← Terug naar overzicht</router-link>
    </div>
  </div>
</template>

<style scoped>
.detail-page {
  max-width: 900px;
  margin: 30px auto;
  padding: 0 16px 40px;
}

.loading,
.error {
  text-align: center;
  padding: 40px 20px;
}

.content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.header {
  display: flex;
  gap: 20px;
  align-items: center;
  padding: 18px 20px;
  border-radius: 12px;
  background: #f5f6fa;
}

.photo {
  width: 130px;
  height: 130px;
  border-radius: 16px;
  object-fit: cover;
}

.back {
  display: inline-block;
  margin-top: 12px;
  font-weight: 600;
  color: #001b5e;
  text-decoration: none;
}

.back:hover {
  text-decoration: underline;
}
</style>
