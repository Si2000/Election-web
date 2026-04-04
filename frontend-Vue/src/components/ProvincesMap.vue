<template>
  <div class="municipality-header map-header">
    <h2>Provincialekaart</h2>

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
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import { getProvinceWinnersByYear } from "@/services/DatabaseService.ts";
const { control, DomUtil } = L as any;

const GEOJSON_URL = "/provinces.json";

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
  "Volt": "#FF00FF",
};

let map: L.Map;
let geoLayer: L.GeoJSON<any>;

// Normalize names
const normalizeName = (name: string) =>
  name ? name.toLowerCase().replace(/[\s\u2011\u2010-]/g, "") : "";

// Function to load map
const loadMapData = async (year: number) => {
  try {
    // Initialize map only once
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

    // Fetch GeoJSON and winners concurrently
    const [geoRes, provinceWinners] = await Promise.all([
      fetch(GEOJSON_URL),
      getProvinceWinnersByYear(year),
    ]);

    if (!geoRes.ok) throw new Error("Failed to fetch GeoJSON");
    const geoData = await geoRes.json();

    // Map winners by normalized province name
    const winnerByProvince: Record<string, any> = {};
    provinceWinners.forEach((w) => {
      if (w.place) {
        winnerByProvince[normalizeName(w.place)] = w;
      }
    });

    // Remove old layer
    if (geoLayer) {
      geoLayer.clearLayers();
      map.removeLayer(geoLayer);
    }

    // Create new GeoJSON layer
    geoLayer = L.geoJSON(geoData, {
      style: (feature: any) => {
        const name = normalizeName(feature.properties.statnaam);
        const winner = winnerByProvince[name];
        const color = winner ? partyColors[winner.partyName] || "#EEEEEE" : "#EEEEEE"; // fallback grey
        return {
          color: "#333",
          weight: 0.6,
          fillColor: color,
          fillOpacity: 0.85,
        };
      },
      onEachFeature: (feature: any, layer: any) => {
        const name = feature.properties.statnaam || "Onbekend";
        const winner = winnerByProvince[normalizeName(name)];

        if (!winner) {
          layer.bindPopup(`<strong>${name}</strong><br>Geen data`);
          return;
        }

        const winnerHtml = `<strong>Winnaar:</strong> ${winner.partyName} (${winner.totalVotes.toLocaleString("nl-NL")})`;

        let losersHtml = "";
        if (winner.losers && Object.keys(winner.losers).length > 0) {
          losersHtml = `<br><details><summary>Verliezende partijen (${Object.keys(winner.losers).length})</summary>`;
          losersHtml += Object.entries(winner.losers)
            .map(([partij, stemmen]) => `${partij}: ${(stemmen as number).toLocaleString("nl-NL")}`)
            .join("<br>");
          losersHtml += `</details>`;
        }

        const popupHtml = `<div style="max-height:100px; overflow-y:auto;">
         <strong>${name}</strong><br>
         ${winnerHtml}
         ${losersHtml}
        </div>`;
        layer.bindPopup(popupHtml, { maxWidth: 300, autoPan: true });
      },
    }).addTo(map);

    // Fit bounds
    const bounds = geoLayer.getBounds();
    if (bounds.isValid()) map.fitBounds(bounds, { padding: [20, 20] });

    // Add legend only once
    if (!document.querySelector(".info.legend")) {
      const legend = control({ position: "bottomright" });
      legend.onAdd = function () {
        const div = DomUtil.create("div", "info legend");
        div.style.background = "white";
        div.style.padding = "6px";
        div.style.borderRadius = "4px";
        div.style.maxHeight = "300px";
        div.style.overflowY = "auto";
        div.innerHTML = "<strong>Partij kleuren</strong><br>";
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

// Initial load
onMounted(() => loadMapData(selectedYear.value));

// Watch for year changes
watch(selectedYear, (newYear) => loadMapData(newYear));

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
  flex-direction: row;
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
