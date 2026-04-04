<script setup lang="ts">
import { ref, onMounted } from "vue";
// 1. Interface
interface Member {
  name: string;
  description: string;
  date: string;
  url: string;
}

// 2. Type
const members = ref<Member[]>([]);


async function loadMembers() {
  try {
    const response = await fetch(
      "https://opendata.tweedekamer.nl/gebruikersinformatie/people.jsonl"
    );

    const text = await response.text();

    // Split JSONL-file in separate JSON objects
    const lines = text.trim().split("\n");

    const parsed = lines.map(line => JSON.parse(line));

    // Map to display format
    members.value = parsed.map(person => ({
      name: person.fullName,
      description: person.description || "Lid van de Tweede Kamer",
      date: person.dateOfBirth
        ? new Date(person.dateOfBirth).toLocaleDateString("nl-NL", {
          day: "numeric",
          month: "long",
          year: "numeric"
        })
        : "Onbekend",
      url: person.links?.self || "#"
    }));
  } catch (error) {
    console.error("Kon Tweede Kamer data niet laden:", error);
  }
}

onMounted(() => loadMembers());
</script>

<template>
  <div class="members-container">
    <div class="hero">
      <h1>Tweede Kamer Leden</h1>
      <p>Op basis van de officiële JSONL-API van de Tweede Kamer.</p>
    </div>

    <div class="members-list">
      <div class="member-card" v-for="(m, i) in members" :key="i">

        <h2>{{ m.name }}</h2>

        <p class="summary">{{ m.description }}</p>

        <div class="meta">
          <span class="date">{{ m.date }}</span>
          <a :href="m.url" class="read-more" target="_blank">Bekijk profiel →</a>
        </div>

      </div>
    </div>
  </div>
</template>

<style scoped>
.hero {
  text-align: center;
  padding: 40px 20px;
  background: #f5f6fa;
  border-radius: 12px;
  margin-bottom: 30px;
}

.hero h1 {
  font-size: 2rem;
  color: #001b5e;
  font-weight: 800;
}

.hero p {
  color: #666;
  font-size: 1.05rem;
}

.members-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
  padding: 0 15px 40px;
}

.member-card {
  background: #fff;
  padding: 18px 20px;
  border-radius: 12px;
  box-shadow: 0 3px 10px rgba(0,0,0,0.08);
  transition: 0.25s ease;
}

.member-card:hover {
  transform: translateY(-3px);
}

.member-card h2 {
  font-size: 1.15rem;
  font-weight: 700;
  color: #001b5e;
  margin-bottom: 6px;
}

.summary {
  color: #444;
  font-size: 0.95rem;
  line-height: 1.35rem;
  margin-bottom: 12px;

  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.date {
  color: #777;
  font-size: 0.85rem;
}

.read-more {
  color: #001b5e;
  font-weight: 600;
  font-size: 0.9rem;
  text-decoration: none;
}

.read-more:hover {
  text-decoration: underline;
}
</style>
