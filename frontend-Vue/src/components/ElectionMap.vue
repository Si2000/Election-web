<template>
  <div class="municipality-header map-header">
    <h2>Gemeentekaart</h2>

    <div class="stats-bar">
      <div class="stat-badge">
        <span class="stat-label">Jaar</span>
        <select v-model="selectedYear" class="year-dropdown">
          <option value="2021">2021</option>
          <option value="2023">2023</option>
          <option value="2025">2025</option>
        </select>
      </div>
    </div>
  </div>

  <div id="map"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import * as L from "leaflet";
const { control, DomUtil } = L as any;

import "leaflet/dist/leaflet.css";
import type { FeatureCollection } from "geojson";
import { getWinnersByYear } from "@/services/DatabaseService";

const GEOJSON_URL = "/gemeente.json";
const router = useRouter();

export interface MunicipalityPartyResultDTO {
  id: string;
  name: string;
  party: string;
  votes: number;
}

// Year filter
const selectedYear = ref(2023);

const partyColors: Record<string, string> = {
  "50PLUS": "#FF69B4",
  "BBB": "#FFB300",
  "BIJ1": "#8B0000",
  "BVNL / Groep Van Haga": "#1E90FF",
  "CDA": "#008000",
  "ChristenUnie": "#FFA500",
  "D66": "#33CC99",
  "DENK": "#800080",
  "Forum voor Democratie": "#7B1FA2",
  "GROENLINKS / Partij van de Arbeid (PvdA)": "#B71C1C",
  "JA21": "#008AC8",
  "LEF - Voor de Nieuwe Generatie": "#FF4500",
  "LP (Libertaire Partij)": "#20B2AA",
  "Nederland met een PLAN": "#FFD700",
  "Nieuw Sociaal Contract": "#DC143C",
  "PVV (Partij voor de Vrijheid)": "#0033FF",
  "Partij voor de Dieren": "#228B22",
  "PartijvdSport": "#FF6347",
  "Piratenpartij - De Groenen": "#32CD32",
  "Politieke Partij voor Basisinkomen": "#00CED1",
  "SP (Socialistische Partij)": "#E3000F",
  "Samen voor Nederland": "#6A5ACD",
  "Splinter": "#FF1493",
  "Staatkundig Gereformeerde Partij (SGP)": "#556B2F",
  "VVD": "#1D70B8",
  "Volt": "#FF00FF"
};

let map: L.Map;
let geoLayer: L.GeoJSON<any>;

// Normalize municipality name
const normalizeName = (name: string) => name.toLowerCase().trim().replace(/\s+/g, '');

// Load map and data
const loadMapData = async (year: number) => {
  try {
    // Initialize map if not already
    if (!map) {
      map = L.map("map", {
        dragging: true,
        scrollWheelZoom: false,
        doubleClickZoom: false,
        boxZoom: false,
        keyboard: false,
        maxBounds: [
          [50.5, 3.0],
          [53.7, 7.3],
        ],
        maxBoundsViscosity: 1.0,
        zoomControl: false,
      }).setView([52.1, 5.3], 8);
      document.getElementById("map")!.style.background = "white";
    }

    // Fetch GeoJSON
    const geoResp = await fetch(GEOJSON_URL);
    if (!geoResp.ok) throw new Error(`GeoJSON load error: ${geoResp.status}`);
    const geoData: FeatureCollection = await geoResp.json();

    // Fetch winners for selected year
    let results: MunicipalityPartyResultDTO[] = [];
    try {
      results = await getWinnersByYear(year);
    } catch (err) {
      console.warn(`Geen data gevonden voor jaar ${year}`);
    }

    // Organize results by municipality
    const resultsByMunicipality: Record<string, MunicipalityPartyResultDTO[]> = {};
    for (const r of results) {
      const key = normalizeName(r.name);
      if (!resultsByMunicipality[key]) resultsByMunicipality[key] = [];
      resultsByMunicipality[key].push(r);
    }

    // Remove old layer if exists
    if (geoLayer) {
      geoLayer.clearLayers();
      map.removeLayer(geoLayer);
    }

    // Create new layer
    geoLayer = L.geoJSON(geoData, {
      style: (feature: any) => {
        const winner = resultsByMunicipality[normalizeName(feature.properties?.statnaam || feature.properties?.name || '')]?.[0];
        const color = winner ? partyColors[winner.party] || "#BBBBBB" : "#EEEEEE"; // fallback grey
        return { color: "#333", weight: 0.6, fillColor: color, fillOpacity: 0.85 };
      },
      onEachFeature: (feature: any, layer: any) => {
        const key = normalizeName(feature.properties?.statnaam || feature.properties?.name || '');
        const munResults = resultsByMunicipality[key];

        // If no data, show popup with "Geen data"
        if (!munResults || munResults.length === 0) {
          layer.bindPopup(`<strong>${feature.properties?.statnaam || feature.properties?.name}</strong><br>Geen data`);
          return;
        }

        // Sort winners
        munResults.sort((a, b) => b.votes - a.votes);
        const topWinner = munResults[0];
        const losers = munResults.slice(1);
        const municipalityId = topWinner.id;

        // Build popup HTML
        let popupHtml = `<div style="max-height:100px; overflow-y:auto;">`;
        popupHtml += `<strong>${feature.properties?.statnaam || feature.properties?.name}</strong><br>`;
        popupHtml += `<strong>Winnaar:</strong> ${topWinner.party} (${topWinner.votes.toLocaleString("nl-NL")})`;

        if (losers.length > 0) {
          popupHtml += `<br><details><summary>Verliezende partijen (${losers.length})</summary>`;
          popupHtml += losers.map(r => `${r.party}: ${r.votes.toLocaleString("nl-NL")}`).join("<br>");
          popupHtml += `</details>`;
        }

        popupHtml += `<br><button class="more-btn" data-id="${municipalityId}">Meer</button>`;
        popupHtml += `</div>`;

        layer.bindPopup(popupHtml, { maxWidth: 300, autoPan: true });

        layer.on('popupopen', () => {
          const btn = document.querySelector('.more-btn') as HTMLButtonElement;
          if (btn) {
            btn.addEventListener('click', () => {
              router.push({
                name: 'MunicipalityResultView',
                params: { year, municipalityId }
              });
            });
          }
        });
      }
    }).addTo(map);

    // Fit bounds
    const bounds = geoLayer.getBounds();
    if (bounds.isValid()) map.fitBounds(bounds, { padding: [20, 20] });

    // Add legend only once
    if (!document.querySelector('.info.legend')) {
      const legend = control({ position: 'bottomright' });
      legend.onAdd = function () {
        const div = DomUtil.create('div', 'info legend');
        div.style.background = 'white';
        div.style.padding = '6px';
        div.style.borderRadius = '4px';
        div.style.maxHeight = '300px';
        div.style.overflowY = 'auto';
        div.innerHTML = '<strong>Partij kleuren</strong><br>';
        for (const [party, color] of Object.entries(partyColors)) {
          div.innerHTML += `<i style="background:${color}; width:12px; height:12px; display:inline-block; margin-right:6px;"></i>${party}<br>`;
        }
        return div;
      };
      legend.addTo(map);
    }

  } catch (err) {
    console.error("Fout tijdens kaartinitialisatie:", err);
  }
};

// Load initial year
onMounted(() => loadMapData(selectedYear.value));

// Watch for year changes
watch(selectedYear, (newYear) => {
  loadMapData(newYear);
});

</script>

<style scoped>
.map-header {
  background-color: #2c3e50;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;   /*zelfde padding als top-bar*/
  min-height: 64px;     /*zelfde hoogte als top-bar*/
}

.map-header h2 {
  margin: 0;
  font-size: 1.75rem;  /*zelfde grootte als top-bar h1/h2*/
  font-weight: 700;
  color: white;
}

.stats-bar {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.stat-badge {
  display: flex;
  flex-direction: row;  /* label en dropdown naast elkaar */
  align-items: center;
  gap: 0.5rem;
}

.stat-label {
  font-size: 0.95rem;
  font-weight: 500;
  color: white;
}

.year-dropdown {
  padding: 0.35rem 0.6rem;
  border-radius: 6px;
  border: 1px solid #d1d5db;
  background: #fff;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
}

.year-dropdown:focus {
  outline: none;
  border-color: #4f7cff;
  box-shadow: 0 0 0 2px rgba(85, 138, 255, 0.25);
}

/* Map container fills remaining space */
#map {
  flex: 1;
  height: calc(100vh - 64px); /*aftrekken header hoogte*/
  width: 100%;
}

</style>
