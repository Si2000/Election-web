package com.election.backendjava.service;

import com.election.backendjava.dto.CandidateDTO;
import com.election.backendjava.model.Candidate;
import com.election.backendjava.model.ElectionRegion;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ElectionFilterService {

    private final ElectionCacheService cacheService;

    public ElectionFilterService(ElectionCacheService cacheService) {
        this.cacheService = cacheService;
    }

    /**
     * Retrieves the national root region for a specific year.
     *
     * @param year the election year
     * @return the national ElectionRegion object
     */
    public ElectionRegion getNationalRegion(int year) {
        return cacheService.getNationalRegionRaw(year);
    }

    /**
     * Searches for a specific region by its logical ID.
     * Handles ID normalization (stripping leading zeros if needed).
     *
     * @param year            the election year
     * @param logicalRegionId the ID to search for (e.g., "0014" or "G_0014")
     * @return the found ElectionRegion, or null if not found
     */
    public ElectionRegion findRegion(int year, String logicalRegionId) {
        String normalizedId = logicalRegionId;

        if (!logicalRegionId.contains("::")) {
            normalizedId = logicalRegionId.replaceFirst("^0+", "");
        }

        Map<String, ElectionRegion> yearRegions = cacheService.getRegions(year);
        if (yearRegions == null) return null;

        return yearRegions.get(normalizedId);
    }

    /**
     * Retrieves all regions categorized as "GEMEENTE" (Municipality).
     *
     * @param year the election year
     * @return list of municipalities
     */
    public List<ElectionRegion> getAllMunicipalities(int year) {
        Map<String, ElectionRegion> yearRegions = cacheService.getRegions(year);
        if (yearRegions == null) return new ArrayList<>();

        return yearRegions.values().stream()
                .filter(region -> "GEMEENTE".equals(region.getCategory()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all regions categorized as "KIESKRING" (Constituency).
     *
     * @param year the election year
     * @return list of constituencies
     */
    public List<ElectionRegion> getAllConstituencies(int year) {
        Map<String, ElectionRegion> yearRegions = cacheService.getRegions(year);
        if (yearRegions == null) return new ArrayList<>();

        return yearRegions.values().stream()
                .filter(region -> "KIESKRING".equals(region.getCategory()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of all candidates for a specific year.
     *
     * @param year the election year
     * @return list of Candidate objects
     */
    public List<Candidate> getAllCandidates(int year) {
        Map<String, Candidate> candidatesMap = cacheService.getCandidates(year);
        if (candidatesMap == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(candidatesMap.values());
    }

    /**
     * Retrieves candidates and their vote counts for a specific region.
     * Returns a sorted list of DTOs suitable for the frontend.
     *
     * @param region the region to calculate votes for
     * @param year   the election year
     * @return list of CandidateDTOs sorted by votes (descending)
     */
    public List<CandidateDTO> getCandidateVotesForRegion(ElectionRegion region, int year) {
        List<CandidateDTO> results = new ArrayList<>();

        Map<String, Candidate> candidates = cacheService.getCandidates(year);
        if (region == null || candidates == null) {
            return results;
        }

        for (Map.Entry<String, Candidate> entry : candidates.entrySet()) {
            String compositeKey = entry.getKey();
            Candidate candidate = entry.getValue();

            int votes = region.getVotesPerCompositeKey().getOrDefault(compositeKey, 0);

            results.add(new CandidateDTO(
                    candidate.getCandidateid(),
                    (candidate.getFirstname() != null ? candidate.getFirstname() : "") + " "
                            + (candidate.getLastname() != null ? candidate.getLastname() : ""),
                    candidate.getParty(),
                    votes
            ));
        }

        results.sort(Comparator.comparingInt(CandidateDTO::getVotes).reversed());

        return results;
    }

    /**
     * Filters candidates based on party and/or gender.
     *
     * @param year         the election year
     * @param partyFilter  (optional) party name to filter by
     * @param genderFilter (optional) gender to filter by
     * @return sorted list of matching candidates
     */
    public List<Candidate> getFilteredCandidates(int year, String partyFilter, String genderFilter) {
        Map<String, Candidate> candidatesMap = cacheService.getCandidates(year);

        if (candidatesMap == null) {
            return new ArrayList<>();
        }

        return candidatesMap.values().stream()
                .filter(candidate -> {
                    if (partyFilter == null || partyFilter.isEmpty()) return true;
                    return candidate.getParty() != null &&
                            candidate.getParty().equalsIgnoreCase(partyFilter);
                })
                .filter(candidate -> {
                    if (genderFilter == null || genderFilter.isEmpty()) return true;
                    return candidate.getGender() != null &&
                            candidate.getGender().equalsIgnoreCase(genderFilter);
                })
                .sorted(Comparator.comparing(Candidate::getParty, Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(Candidate::getFirstname, Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(Candidate::getLastname, Comparator.nullsLast(String::compareToIgnoreCase)))
                .collect(Collectors.toList());
    }

    /**
     * Finds polling stations within a postcode range and aggregates their votes.
     * Uses existing party votes and candidate votes from the regions.
     *
     * @param year      Election year
     * @param startCode Start of the range (e.g., "1000AA")
     * @param endCode   End of the range (e.g., "2000ZZ")
     * @return A list containing one aggregated ElectionRegion
     */
    public List<ElectionRegion> getAggregatedRange(int year, String startCode, String endCode) {
        Map<String, ElectionRegion> allRegions = cacheService.getRegions(year);
        if (allRegions == null) return new ArrayList<>();

        String start = startCode.replaceAll("\\s+", "").toUpperCase();
        String end = endCode.replaceAll("\\s+", "").toUpperCase();

        List<ElectionRegion> matches = allRegions.values().stream()
                .filter(r -> "Stembureau".equalsIgnoreCase(r.getCategory()))
                .filter(r -> {
                    String name = r.getName();
                    if (name == null) return false;
                    String marker = "postcode:";
                    int idx = name.toLowerCase().indexOf(marker);
                    if (idx == -1) return false;
                    String raw = name.substring(idx + marker.length());
                    String code = raw.replace(")", "").replace(" ", "").toUpperCase();
                    return code.compareTo(start) >= 0 && code.compareTo(end) <= 0;
                })
                .collect(Collectors.toList());

        if (matches.isEmpty()) return new ArrayList<>();

        ElectionRegion total = new ElectionRegion(
                "RANGE_" + start + "_" + end,
                "Postcode Range: " + start + " - " + end,
                "Stembureau"
        );

        total.setVotesPerParty(new java.util.HashMap<>());
        total.setVotesPerCompositeKey(new java.util.HashMap<>());

        for (ElectionRegion station : matches) {

            if (station.getVotesPerParty() != null) {
                station.getVotesPerParty().forEach((party, votes) -> {
                    if (votes != null) {
                        total.getVotesPerParty().merge(party, votes, Integer::sum);
                    }
                });
            }

            if (station.getVotesPerCompositeKey() != null) {
                station.getVotesPerCompositeKey().forEach((key, votes) -> {
                    if (votes != null) {
                        total.getVotesPerCompositeKey().merge(key, votes, Integer::sum);
                    }
                });
            }
        }

        int calculatedTotal = 0;
        for (Integer votes : total.getVotesPerParty().values()) {
            if (votes != null) {
                calculatedTotal += votes;
            }
        }

        total.setTotalVotes(calculatedTotal);

        List<ElectionRegion> result = new ArrayList<>();
        result.add(total);
        return result;
    }

    /**
     * Finds a region by ID.
     * If the ID starts with "RANGE_", it recalculates the aggregated data on the fly.
     * Otherwise, it looks up the region in the static cache.
     *
     * @param year The election year
     * @param id   The region ID (e.g., "G_0014" or "RANGE_1000_2000")
     * @return The found or calculated ElectionRegion
     */
    public ElectionRegion findRegionOrRange(int year, String id) {
        if (id != null && id.startsWith("RANGE_")) {
            String[] parts = id.split("_");
            if (parts.length >= 3) {
                String start = parts[1];
                String end = parts[2];

                List<ElectionRegion> results = getAggregatedRange(year, start, end);

                if (!results.isEmpty()) {
                    return results.get(0);
                }
            }
            return null;
        }

        return findRegion(year, id);
    }

    /**
     * Finds polling stations where the extracted postcode starts with the search query.
     * <p>
     * Parses the format: "Name (postcode: 1234 AB)".
     * This logic extracts the postcode substring and performs a "starts with" check.
     * </p>
     *
     * @param year  the election year
     * @param query the postcode digits (and optional letters)
     * @return list of matching polling stations
     */
    public List<ElectionRegion> findPostcode(int year, String query) {
        Map<String, ElectionRegion> nodes = cacheService.getRegions(year);
        if (nodes == null) return new ArrayList<>();

        String cleanQuery = query.replaceAll("\\s+", "").toLowerCase();

        return nodes.values().stream()
                .filter(n -> "Stembureau".equalsIgnoreCase(n.getCategory()))
                .filter(n -> {
                    String name = n.getName();
                    if (name == null) return false;

                    String marker = "postcode:";
                    int idx = name.toLowerCase().indexOf(marker);
                    if (idx == -1) return false;

                    String raw = name.substring(idx + marker.length());
                    String code = raw.replace(")", "").replace(" ", "").toLowerCase();

                    return code.startsWith(cleanQuery);
                })
                .collect(Collectors.toList());
    }
}