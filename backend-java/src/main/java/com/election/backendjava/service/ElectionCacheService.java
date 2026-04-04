package com.election.backendjava.service;
import com.election.backendjava.model.Candidate;
import com.election.backendjava.model.ElectionRegion;
import com.election.backendjava.model.RawVote;
import com.election.backendjava.model.Region;
import com.election.backendjava.repository.CandidateRepository;
import com.election.backendjava.repository.RawVoteRepository;
import com.election.backendjava.repository.RegionJPARepository;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ElectionCacheService {

    private final RegionJPARepository regionRepo;
    private final CandidateRepository candidateRepo;
    private final RawVoteRepository rawVoteRepo;

    // National region per year
    private final Map<Integer, ElectionRegion> nationalRegionCache = new HashMap<>();

    // All regions per year
    private final Map<Integer, Map<String, ElectionRegion>> regionLookupByYear = new HashMap<>();

    // Candidates per year
    private final Map<Integer, Map<String, Candidate>> candidateMapByYear = new HashMap<>();

    public ElectionCacheService(RegionJPARepository regionRepo, CandidateRepository candidateRepo, RawVoteRepository rawVoteRepo) {
        this.regionRepo = regionRepo;this.candidateRepo = candidateRepo;this.rawVoteRepo = rawVoteRepo;
    }

    public void buildCache() {
        buildTree(2023);
        buildTree(2021);
        buildTree(2025);
    }


    private void buildTree(int year) {
        Map<String, ElectionRegion> currentYearRegions = new HashMap<>();
        regionLookupByYear.put(year, currentYearRegions);

        Map<String, Candidate> currentYearCandidates = new HashMap<>();
        candidateMapByYear.put(year, currentYearCandidates);

        List<Region> allRegions = regionRepo.findAll();

        ElectionRegion nationalRegion = null;

        // Create all ElectionRegion objects
        for (Region region : allRegions) {
            String logicalId = region.getLogicalNumber();
            if (logicalId != null) {
                ElectionRegion electionRegion =
                        new ElectionRegion(logicalId, region.getRegionName(), region.getCategory());

                currentYearRegions.put(logicalId, electionRegion);

                if ("NL_ROOT".equals(logicalId)) {
                    nationalRegion = electionRegion;
                }
            }
        }

        // Builds the parent child relations
        for (Region region : allRegions) {
            String logicalId = region.getLogicalNumber();
            ElectionRegion childRegion = currentYearRegions.get(logicalId);

            if (region.getSuperNumber() != null) {
                ElectionRegion parentRegion = currentYearRegions.get(region.getSuperNumber());

                if (parentRegion != null && childRegion != null) {
                    parentRegion.addChild(childRegion);
                }
            }

            if (logicalId != null && logicalId.startsWith("K_") && nationalRegion != null && childRegion != null) {

                if (childRegion.getParent() == null) {
                    nationalRegion.addChild(childRegion);
                }
            }
        }

        // Loads the candidates
        List<Candidate> candidates = candidateRepo.findByElectionYear(year);
        for (Candidate candidate : candidates) {
            String compositeKey = candidate.getPartyId() + "_" + candidate.getCandidateid();
            currentYearCandidates.putIfAbsent(compositeKey, candidate);
        }

        // Adds the votes
        List<RawVote> votes = rawVoteRepo.findByElectionYear(year);

        for (RawVote vote : votes) {
            ElectionRegion targetRegion = currentYearRegions.get(vote.getRegionId());

            String compositeKey = vote.getPartyId() + "_" + vote.getCandidateId();
            Candidate candidate = currentYearCandidates.get(compositeKey);

            if (targetRegion != null && candidate != null) {
                targetRegion.addVote(vote.getVoteCount(), candidate.getPartyId(), candidate.getParty(), candidate.getCandidateid());
            }
        }

        // Saves in cache
        if (nationalRegion != null) {
            nationalRegionCache.put(year, nationalRegion);
        }
    }

    /**
     * Retrieves the raw map of regions for a specific year.
     */
    public Map<String, ElectionRegion> getRegions(int year) {
        return regionLookupByYear.get(year);
    }

    /**
     * Retrieves the raw map of candidates for a specific year.
     */
    public Map<String, Candidate> getCandidates(int year) {
        return candidateMapByYear.get(year);
    }

    /**
     * Retrieves the national region (root) for a specific year.
     */
    public ElectionRegion getNationalRegionRaw(int year) {
        return nationalRegionCache.get(year);
    }
}