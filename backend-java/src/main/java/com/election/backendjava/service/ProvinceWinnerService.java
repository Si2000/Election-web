package com.election.backendjava.service;

import com.election.backendjava.model.ElectionRegion;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Service to calculate the winning party per province based on constituency votes.
 */
@Service
public class ProvinceWinnerService {

    private final ElectionFilterService electionFilterService;

    /**
     * Hardcoded mapping of constituency ("kieskring") to province.
     */
    private static final Map<String, String> KIESKRING_PROVINCIE = Map.ofEntries(
            Map.entry("Groningen", "Groningen"),
            Map.entry("Leeuwarden", "Fryslân"),
            Map.entry("Assen", "Drenthe"),
            Map.entry("Zwolle", "Overijssel"),
            Map.entry("Lelystad", "Flevoland"),
            Map.entry("Nijmegen", "Gelderland"),
            Map.entry("Arnhem", "Gelderland"),
            Map.entry("Utrecht", "Utrecht"),
            Map.entry("Amsterdam", "Noord‑Holland"),
            Map.entry("Haarlem", "Noord‑Holland"),
            Map.entry("Den Helder", "Noord‑Holland"),
            Map.entry("Den Haag", "Zuid‑Holland"),
            Map.entry("Rotterdam", "Zuid‑Holland"),
            Map.entry("Dordrecht", "Zuid‑Holland"),
            Map.entry("Leiden", "Zuid‑Holland"),
            Map.entry("Middelburg", "Zeeland"),
            Map.entry("Tilburg", "Noord‑Brabant"),
            Map.entry("’s‑Hertogenbosch", "Noord‑Brabant"),
            Map.entry("Maastricht", "Limburg")
    );

    public ProvinceWinnerService(ElectionFilterService electionFilterService) {
        this.electionFilterService = electionFilterService;
    }

    /**
     * Calculates the winning party per province along with total votes.
     *
     * @param year The election year
     * @return Map of province name to {@link ProvinceResult} containing winner and total votes
     */
    public Map<String, ProvinceResult> calculateWinnerPerProvinceWithVotes(int year) {
        List<ElectionRegion> kieskringen = electionFilterService.getAllConstituencies(year);

        Map<String, Map<String, Integer>> stemmenPerProvincie = new HashMap<>();

        // Count votes per province
        for (ElectionRegion k : kieskringen) {
            String provincie = KIESKRING_PROVINCIE.get(k.getName());
            if (provincie == null) continue;

            stemmenPerProvincie.putIfAbsent(provincie, new HashMap<>());
            Map<String, Integer> stemmenPerPartij = stemmenPerProvincie.get(provincie);

            for (Map.Entry<String, Integer> entry : k.getVotesPerParty().entrySet()) {
                stemmenPerPartij.put(entry.getKey(),
                        stemmenPerPartij.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }

        // Determine winner and total votes per province
// Determine winner and total votes per province
        Map<String, ProvinceResult> winnaarPerProvincie = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> entry : stemmenPerProvincie.entrySet()) {
            String provincie = entry.getKey();
            Map<String, Integer> votes = entry.getValue();

            // Winner
            Map.Entry<String, Integer> maxEntry = votes.entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue())
                    .orElse(null);

            if (maxEntry == null) {
                winnaarPerProvincie.put(provincie, new ProvinceResult("Geen winnaar", 0, Map.of()));
                continue;
            }

            String winnaar = maxEntry.getKey();
            int totaalStemmen = maxEntry.getValue();

            // Losers
            Map<String, Integer> losers = new HashMap<>(votes);
            losers.remove(winnaar); // verwijder de winnaar → rest zijn verliezers

            winnaarPerProvincie.put(provincie, new ProvinceResult(winnaar, totaalStemmen, losers));
        }

        return winnaarPerProvincie;
    }

    /**
     * Represents the result for a province, including the winning party and total votes.
     */
    public static class ProvinceResult {
        /** Name of the winning party */
        public final String winner;

        /** Total votes the winner received in this province */
        public final int totalVotes;

        /** All losing parties with their votes */
        public final Map<String, Integer> losers;

        public ProvinceResult(String winner, int totalVotes, Map<String, Integer> losers) {
            this.winner = winner;
            this.totalVotes = totalVotes;
            this.losers = losers;
        }
    }
}
