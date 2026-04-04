<script setup lang="ts">
import {RouterLink} from "vue-router";

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
  <div class="yasser-page">

    <!-- HERO -->
    <section class="hero">
      <h1>Yasser's Politieke Kenniscentrum</h1>
      <p>
        Voor Yasser staat objectieve informatie centraal — feiten, verdieping en transparantie.
        Deze pagina verzamelt alle betrouwbare data om écht inhoudelijk in de politiek te duiken.
      </p>
    </section>

    <!-- TWEEDE KAMER LEDEN -->
    <section class="section">
      <h2>🏛️ Analyseer de Tweede Kamer</h2>
      <p>
        Bekijk alle actieve Kamerleden, inclusief functies, standpunten en recente activiteiten.
      </p>

      <RouterLink to="/candidates" class="cta-button">
        ➜ Bekijk alle Kamerleden
      </RouterLink>
    </section>

    <!-- POLITIEKE DATA / FEITEN -->
    <section class="section">
      <h2> Partijprogramma's & Feiten</h2>
      <p>
        Wil je je verdiepen in politieke standpunten? Vergelijk eenvoudig alle partijprogramma’s
        en beleidsplannen voor een objectieve blik.
      </p>

      <a
        href="https://www.kieskompas.nl"
        target="_blank"
        class="cta-button external"
      >
        ➜ Vergelijk standpunten (Kieskompas)
      </a>
    </section>

    <!-- DEBATTEN / ACTUALITEIT -->
    <section class="section">
      <h2> Politieke Debatten & Commissies</h2>
      <p>
        Volg live wat er speelt in Den Haag. Bekijk debatten, commissievergaderingen en agenda’s.
      </p>

      <a
        href="https://www.tweedekamer.nl/debat_en_vergadering"
        target="_blank"
        class="cta-button external"
      >
        ➜ Bekijk Debatten & Vergaderingen
      </a>
    </section>

    <!-- FORUM / DISCUSSIE -->
    <section class="section">
      <h2> Discussieer met anderen</h2>
      <p>
        Yasser houdt ervan om ideeën uit te wisselen. Sluit je aan bij het discussieplatform en
        praat inhoudelijk mee over politieke thema’s.
      </p>

      <RouterLink to="/forum" class="cta-button">
        ➜ Ga naar het debatforum
      </RouterLink>
    </section>

    <!-- INFOBLOK OVER HOE YASSER STEMT -->
    <section class="info-block">
      <h2> Navigeren door de politiek</h2>
      <p>
        Yasser maakt pas een keuze als hij alle feiten kent — daarom combineert deze pagina data,
        debatten en objectieve tools.
      </p>
      <p>
        Door eerst te begrijpen hoe partijen denken en handelen, maakt hij uiteindelijk een goed
        onderbouwde keuze die écht bij hem past.
      </p>
    </section>

  </div>
</template>

<style scoped>
.yasser-page {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;
}

/* HERO */
.hero {
  background: #eef2f7;
  padding: 50px 25px;
  border-radius: 15px;
  text-align: center;
  margin-bottom: 40px;
}
.hero h1 {
  font-size: 2.3rem;
  color: #00234d;
  font-weight: 800;
  margin-bottom: 15px;
}
.hero p {
  color: #444;
  font-size: 1.1rem;
  max-width: 700px;
  margin: 0 auto;
}

/* SECTIONS */
.section {
  background: #ffffff;
  padding: 30px 25px;
  border-radius: 12px;
  margin-bottom: 35px;
  box-shadow: 0 3px 10px rgba(0,0,0,0.07);
}
.section h2 {
  font-size: 1.5rem;
  color: #00234d;
  margin-bottom: 10px;
}
.section p {
  color: #444;
  margin-bottom: 18px;
  line-height: 1.5rem;
}

/* CTA BUTTONS */
.cta-button {
  display: inline-block;
  padding: 12px 20px;
  border-radius: 8px;
  background: #00234d;
  color: white;
  text-decoration: none;
  font-weight: 600;
  transition: 0.25s;
}
.cta-button:hover {
  background: #003a7a;
}
.external {
  background: #0056b3;
}
.external:hover {
  background: #003d80;
}

/* INFO BLOCK */
.info-block {
  background: #f7f9fc;
  padding: 30px;
  border-radius: 12px;
  text-align: center;
  margin-top: 40px;
}
.info-block h2 {
  font-size: 1.6rem;
  color: #00234d;
  margin-bottom: 10px;
}
.info-block p {
  color: #555;
  font-size: 1rem;
  line-height: 1.5rem;
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
