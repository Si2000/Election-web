<script setup lang="ts" xmlns="http://www.w3.org/1999/html">
/**
 * Homepage with persona selection functionality.
 * This version:
 *  - Keeps the original homepage layout (hero + buttons)
 *  - Shows persona cards only when user has no persona
 *  - Automatically redirects if the user already selected persona
 */

import { RouterLink, useRouter } from 'vue-router';
import { onMounted, ref, computed } from 'vue';
import { userInfo } from '@/storages/userStorage';

const router = useRouter();
const store = userInfo();

// Reactive logged-in status (Pinia getter must be used inside computed)
const loggedIn = computed(() => store.isLoggedIn);

// Persona returned from backend
const persona = ref<string | null>(null);

// Loading state for initial fetch
const loading = ref(true);

/**
 * Fetch persona for the logged-in user based on username.
 * If persona already exists, automatically redirect to the correct persona page.
 */
async function fetchPersona() {
  if (!loggedIn.value) {
    loading.value = false;
    return;
  }


  try {
    const response = await fetch(`http://oege.ie.hva.nl:8888/api/user/persona`, {
      headers: { Authorization: `Bearer ${store.token}` }
    });

    const data = await response.json();

    // Accept only valid persona values
    if (data.persona === 'nico' || data.persona === 'yasser') {
      persona.value = data.persona;

      // Redirect based on persona
      if (persona.value === 'nico') router.push('/news');
      if (persona.value === 'yasser') router.push('/yasser');
    } else {
      // User has no persona yet
      persona.value = null;
    }

  } catch (e) {
    console.error('Failed to load persona:', e);
  }

  loading.value = false;
}

/**
 * Set a persona for the logged-in user.
 * After saving the persona, redirect to the corresponding page.
 */
async function choosePersona(type: 'nico' | 'yasser') {
  if (!loggedIn.value) {
    alert('Please log in to choose a persona.');
    return;
  }

  try {
    const token = store.token;

    await fetch('http://oege.ie.hva.nl:8888/api/user/persona', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify({ persona: type })
    });

    // Redirect to the correct page
    if (type === 'nico') router.push('/news');
    if (type === 'yasser') router.push('/yasser');

  } catch (e) {
    console.error('Failed to save persona:', e);
  }
}

// Load persona when page loads
onMounted(fetchPersona);
</script>

<template>
  <div class="home-view-container">

    <!-- Loading spinner while checking persona -->
    <div v-if="loading" class="loading-text">
      Loading...
    </div>

    <!-- Show homepage only when loading is done -->
    <div v-else>

      <!-- HERO SECTION (unchanged) -->
      <div class="hero-section">
        <h1 class="hero-title">Welkom bij het 2025 Verkiezingen Platform</h1>
        <p class="hero-subtitle">
          Jouw centrale punt voor alle data, analyses en inzichten over Nederlandse verkiezingen.
        </p>
        <div class="cta-buttons">
          <RouterLink to="/candidates" class="cta-primary">Bekijk Kandidaten</RouterLink>
          <RouterLink to="/ElectionResult" class="cta-secondary">Regionale Uitslagen</RouterLink>
        </div>
      </div>

      <!-- PERSONA SELECTION (ONLY shown if user has NO persona) -->
      <div v-if="loggedIn && persona === null" class="profiles-section">

        <!-- RICO / NICO persona -->
        <div class="profile-card">
          <div class="avatar">
            <img src="../images/man.png" alt="Nico avatar">
          </div>
          <h2 class="profile-name">Nico</h2>
          <p class="profile-description">
            Nico houdt van nieuws en politieke updates.
            Kies deze persona om politiek nieuws te volgen.
            <br>
            <br>
          </p>
          <button @click="choosePersona('nico')" class="choose-btn">Kies Nico</button>
        </div>

        <!-- YASSER persona -->
        <div class="profile-card">
          <div class="avatar">
            <img src="../images/user.png" alt="Yasser avatar">
          </div>
          <h2 class="profile-name">Yasser</h2>
          <p class="profile-description">
            Yasser houdt van objectieve informatie en politieke verdieping.
            Kies deze persona voor uitgebreide politieke data.
          </p>
          <button @click="choosePersona('yasser')" class="choose-btn">Kies Yasser</button>
        </div>

      </div>

      <!-- If NOT logged in, show message -->
      <div v-if="!loggedIn" class="not-logged-hint">
        Log in om een persona te kiezen.
      </div>

    </div>
  </div>
</template>

<style scoped>
.loading-text {
  text-align: center;
  font-size: 1.5rem;
  padding: 50px;
  color: #444;
}

/* Hero section styling */
.hero-section {
  background-color: #f5f6fa;
  padding: 80px 30px;
  text-align: center;
  border-radius: 12px;
  margin-bottom: 40px;
}
.hero-title {
  font-size: 2.5rem;
  color: #001b5e;
  margin-bottom: 15px;
  font-weight: 800;
}
.hero-subtitle {
  font-size: 1.25rem;
  color: #000000;
  max-width: 700px;
  margin: 0 auto 30px auto;
}

.cta-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
}

/* Buttons */
.cta-primary, .cta-secondary {
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 600;
  text-decoration: none;
  transition: 0.3s;
}
.cta-primary {
  background-color: #001b5e;
  color: white;
  border: 2px solid #001b5e;
}
.cta-primary:hover {
  background-color: #002c9b;
}
.cta-secondary {
  background-color: transparent;
  border: 2px solid #001b5e;
  color: #001b5e;
}
.cta-secondary:hover {
  background-color: #e0e6f7;
}

/* Persona cards */
.profiles-section {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-top: 60px;
  flex-wrap: wrap;
}
.profile-card {
  width: 360px;
  background: linear-gradient(to bottom, #3366cc, #0a1a33);
  color: white;
  padding: 30px;
  border-radius: 20px;
  text-align: center;
}
.avatar {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  background-color: #cfcfcf;
  margin: 0 auto 20px auto;
  overflow: hidden;
}

.avatar img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}
.choose-btn {
  margin-top: 20px;
  padding: 12px 20px;
  border-radius: 30px;
  background: #001b5e;
  color: white;
  border: none;
  cursor: pointer;
  width: 100%;
  transition: 0.3s;
}
.choose-btn:hover {
  background: #003a8c;
}

/* Not logged in message */
.not-logged-hint {
  margin-top: 40px;
  text-align: center;
  font-size: 1.2rem;
  color: #444;
}

.profile-description {
  color: #e6ecff;
  font-size: 1.1rem;
  line-height: 1.5;
  opacity: 0.9;
}

.profile-name {
  color: #ffffff;
  font-size: 1.5rem;
  font-weight: 800;
  margin-bottom: 12px;
  letter-spacing: 0.3px;
}


</style>
