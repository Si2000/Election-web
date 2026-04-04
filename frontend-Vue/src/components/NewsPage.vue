<script setup lang="ts">
import { ref, onMounted } from "vue";
import { RouterLink } from "vue-router";

// 1. Interface
interface NewsArticle {
  title: string;
  description: string;
  url: string;
  date: string;
}

const newsArticles = ref<NewsArticle[]>([]);

async function loadNews() {
  try {
    const response = await fetch(
      "https://api.rss2json.com/v1/api.json?rss_url=https://feeds.nos.nl/nosnieuwsalgemeen"
    );

    const data = await response.json();

    // 2. Type casting (item: any)
    newsArticles.value = data.items.map((item: any) => ({
      title: item.title,
      description: item.description.replace(/<[^>]+>/g, ""),
      url: item.link,
      date: new Date(item.pubDate).toLocaleDateString("nl-NL", {
        day: "numeric",
        month: "long",
        year: "numeric"
      })
    }));
  } catch (error) {
    console.error("Fout bij ophalen NOS nieuws:", error);
  }
}
onMounted(loadNews);
</script>

<template>
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
  <div class="rico-page">

    <!-- HERO -->
    <div class="hero">
      <h1>Rico's Politieke Overzicht</h1>
      <p>Alles wat Rico nodig heeft om goed geïnformeerd te stemmen.</p>
    </div>

    <!-- STEMWIJZER -->
    <section class="section">
      <h2>🧭 Doe de StemWijzer</h2>
      <p>
        Weet je nog niet wat je moet stemmen? De StemWijzer helpt je politieke standpunten te vergelijken.
      </p>

      <a href="https://www.stemwijzer.nl" target="_blank" class="cta-button external">
        ➜ Ga naar StemWijzer
      </a>
    </section>

    <!-- VERKIEZINGSUITSLAGEN -->
    <section class="section">
      <h2>📊 Verkiezingsuitslagen</h2>
      <p>Bekijk de officiële verkiezingsuitslagen per gemeente en regio.</p>

      <RouterLink to="/ElectionResult" class="cta-button">
        ➜ Bekijk Verkiezingsuitslagen
      </RouterLink>
    </section>

    <!-- NIEUWS (helemaal onderaan) -->
    <section class="news-section">
      <h2>📰 Laatste NOS Nieuws</h2>

      <div v-if="newsArticles.length === 0" class="loading">Nieuws laden…</div>

      <div class="news-list">
        <div class="news-item" v-for="(article, i) in newsArticles" :key="i">

          <h3>{{ article.title }}</h3>

          <p class="summary">{{ article.description }}</p>

          <div class="meta">
            <span class="date">{{ article.date }}</span>
            <a :href="article.url" target="_blank" class="read-more">Lees meer →</a>
          </div>

        </div>
      </div>
    </section>

  </div>
</template>

<style scoped>
/* PAGE LAYOUT */
.rico-page {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;
}

/* HERO */
.hero {
  text-align: center;
  background: #f5f6fa;
  padding: 50px 25px;
  border-radius: 15px;
  margin-bottom: 40px;
}
.hero h1 {
  font-size: 2.3rem;
  color: #001b5e;
  font-weight: 800;
}
.hero p {
  color: #666;
  font-size: 1.1rem;
}

/* SECTION BLOCKS */
.section {
  background: white;
  padding: 25px 20px;
  border-radius: 12px;
  box-shadow: 0 3px 10px rgba(0,0,0,0.08);
  margin-top: 40px;
}
.section h2 {
  font-size: 1.4rem;
  color: #001b5e;
  margin-bottom: 10px;
}
.section p {
  color: #555;
  margin-bottom: 18px;
}

/* CTA BUTTON */
.cta-button {
  display: inline-block;
  padding: 12px 20px;
  background: #001b5e;
  color: white;
  border-radius: 8px;
  font-weight: 600;
  text-decoration: none;
  transition: 0.25s;
}
.cta-button:hover {
  background: #00308f;
}
.external {
  background: #0066cc;
}
.external:hover {
  background: #004c99;
}

/* NEWS */
.news-section {
  margin-top: 50px;
}
.news-section h2 {
  font-size: 1.6rem;
  color: #001b5e;
  margin-bottom: 15px;
  font-weight: 700;
}
.news-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
}
.news-item {
  background: white;
  padding: 18px 20px;
  border-radius: 12px;
  box-shadow: 0 3px 10px rgba(0,0,0,0.08);
}
.news-item h3 {
  font-size: 1.2rem;
  color: #001b5e;
  margin-bottom: 6px;
}
.summary {
  color: #444;
  font-size: 0.95rem;
  margin-bottom: 12px;
  line-height: 1.35rem;
  display: -webkit-box;
  -webkit-line-clamp: 3;
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
  text-decoration: none;
}
.read-more:hover { text-decoration: underline; }

/* LOADING */
.loading {
  text-align: center;
  color: #666;
  margin-top: 20px;
}

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
  color: #555;
  max-width: 700px;
  margin: 0 auto 30px auto;
}

/* Buttons container */
.cta-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
}

/* Primary CTA button */
.cta-primary {
  display: inline-block;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 600;
  background-color: #001b5e;
  color: white;
  border: 2px solid #001b5e;
  text-decoration: none;
  transition: background-color 0.3s ease;
}

.cta-primary:hover {
  background-color: #002c9b;
}

/* Secondary CTA button */
.cta-secondary {
  display: inline-block;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 600;
  background-color: transparent;
  color: #001b5e;
  border: 2px solid #001b5e;
  text-decoration: none;
  transition: background-color 0.3s ease;
}

.cta-secondary:hover {
  background-color: #e0e6f7;
}

</style>
