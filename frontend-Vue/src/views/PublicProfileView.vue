<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { userInfo } from "@/storages/userStorage";
import {
  getPublicUserProfile,
  getUserTopics,
  getUserComments,
} from "@/services/UserService";

import { reportUser } from "@/services/AdminService";

const route = useRoute();
const router = useRouter();
const store = userInfo();

const loading = ref(true);
const error = ref("");

const user = ref<{ id: number; username: string; persona: string | null } | null>(null);
const topics = ref<any[]>([]);
const comments = ref<any[]>([]);

onMounted(async () => {
  try {
    const username = route.params.username as string;

    if (!username) {
      error.value = "Gebruiker niet gevonden.";
      return;
    }

    if (username === store.username) {
      router.push("/account");
      return;
    }

    const profile = await getPublicUserProfile(username);
    user.value = profile;

    topics.value = await getUserTopics(profile.id);
    comments.value = await getUserComments(profile.id);

  } catch (e) {
    console.error(e);
    error.value = "Gebruiker niet gevonden.";
  } finally {
    loading.value = false;
  }
});

function goToTopic(topicId: number) {
  router.push(`/forum/${topicId}`);
}

// --- RAPPORTEREN VAN GEBRUIKER ---
const handleReportUser = async () => {
  if (!user.value || !store.token) return;

  const reason = prompt(`Waarom wil je ${user.value.username} rapporteren?`);
  if (!reason || reason.trim() === "") return;

  try {
    await reportUser(user.value.id, reason, store.token);
    alert("Gebruiker gerapporteerd. Bedankt voor je melding.");
  } catch (e) {
    console.error(e);
    alert("Er ging iets mis bij het versturen van de melding.");
  }
};
</script>

<template>
  <div class="profile-page">
    <div v-if="loading">Laden...</div>
    <div v-else-if="error" class="error">{{ error }}</div>

    <div v-else>
      <div class="profile-card">
        <h1>{{ user?.username }}</h1>
        <p v-if="user?.persona">Persona: {{ user.persona }}</p>

        <!-- RAPPORTEREN KNOP -->
        <button
          v-if="store.isLoggedIn"
          @click="handleReportUser"
          class="btn-report-user"
        >
          ⚠ Rapporteer gebruiker
        </button>
      </div>

      <section class="section">
        <h2>Topics</h2>
        <ul v-if="topics.length">
          <li
            v-for="t in topics"
            :key="t.id"
            class="clickable-item"
            @click="goToTopic(t.id)"
          >
            <strong>{{ t.title }}</strong><br />
            {{ t.content }}
          </li>
        </ul>
        <p v-else>Geen topics.</p>
      </section>

      <section class="section">
        <h2>Comments</h2>
        <ul v-if="comments.length">
          <li
            v-for="c in comments"
            :key="c.id"
            class="clickable-item"
            @click="goToTopic(c.topicId)"
          >
            {{ c.content }}
          </li>
        </ul>
        <p v-else>Geen comments.</p>
      </section>
    </div>
  </div>
</template>

<style scoped>
.profile-page { max-width: 700px; margin: auto; padding: 20px; }
.profile-card { background: #f8fafc; padding: 20px; border-radius: 12px; text-align: center; margin-bottom: 20px; }
.btn-report-user {
  background: #f59e0b; color: white; border: none; padding: 8px 14px; border-radius: 6px;
  cursor: pointer; font-weight: 600; margin-top: 1rem; transition: background 0.2s;
}
.btn-report-user:hover { background: #d97706; }
.section { margin-top: 20px; padding: 15px; background: #f0f3fa; border-radius: 12px; }
.error { color: red; font-weight: bold; }
.clickable-item {
  cursor: pointer; transition: background-color 0.2s; padding: 10px; border-radius: 8px;
  margin-bottom: 10px; background-color: #ffffff; box-shadow: 0 2px 6px rgba(0,0,0,0.05);
}
.clickable-item:hover { background-color: #e2e8f0; }
</style>
