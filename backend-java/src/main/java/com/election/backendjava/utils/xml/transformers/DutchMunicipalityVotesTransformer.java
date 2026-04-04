package com.election.backendjava.utils.xml.transformers;

import com.election.backendjava.model.Election;
import com.election.backendjava.model.RawVote;
import com.election.backendjava.model.Region;
import com.election.backendjava.repository.RegionJPARepository;
import com.election.backendjava.utils.xml.TagAndAttributeNames;
import com.election.backendjava.utils.xml.VotesTransformer;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DutchMunicipalityVotesTransformer implements VotesTransformer, TagAndAttributeNames {

    private final Election election;
    private final int electionYear;
    private final RegionJPARepository regionRepo;
    private final Set<String> knownRegionIds;

    private String currentStembureauId;
    private String currentPartyId;

    public DutchMunicipalityVotesTransformer(Election election, RegionJPARepository regionRepo) {
        this.election = election;
        this.electionYear = election.getYear();
        this.regionRepo = regionRepo;

        this.knownRegionIds = regionRepo.findAll().stream()
                .map(Region::getLogicalNumber)
                .collect(Collectors.toSet());
    }

    private String normalizeId(String id) {
        if (id == null) return null;
        return id.replaceFirst("^0+", "");
    }

    @Override
    public void registerPartyVotes(boolean aggregated, Map<String, String> electionData) {
        String partyId = electionData.get(AFFILIATION_IDENTIFIER + "-" + ID);
        if (partyId != null) {
            this.currentPartyId = partyId;
        }

        if (!aggregated) {
            String rawStembureauId = electionData.get(REPORTING_UNIT_IDENTIFIER + "-" + ID);
            String stembureauNaam = electionData.get(REPORTING_UNIT_IDENTIFIER);
            String rawGemeenteId = electionData.get(AUTHORITY_IDENTIFIER + "-" + ID);

            if (rawStembureauId != null && rawGemeenteId != null) {
                String logicalStembureauId = "S_" + normalizeId(rawStembureauId);
                String logicalGemeenteId = "G_" + normalizeId(rawGemeenteId);

                this.currentStembureauId = logicalStembureauId;

                if (!this.knownRegionIds.contains(logicalStembureauId)) {
                    Region stembureau = new Region(
                            logicalStembureauId,
                            stembureauNaam,
                            "Stembureau",
                            logicalGemeenteId
                    );
                    regionRepo.save(stembureau);
                    this.knownRegionIds.add(logicalStembureauId);
                }
            }
        } else {
            this.currentStembureauId = null;
        }
    }

    @Override
    public void registerCandidateVotes(boolean aggregated, Map<String, String> electionData) {
        if (aggregated) return;

        String candidateId = electionData.get(CANDIDATE_IDENTIFIER + "-" + ID);
        String candidateVoteStr = electionData.get(VALID_VOTES);

        if (candidateId != null && candidateVoteStr != null && this.currentPartyId != null && this.currentStembureauId != null) {
            int voteCount = Integer.parseInt(candidateVoteStr);

            if (voteCount > 0) {
                RawVote rawVote = new RawVote(
                        this.electionYear,
                        this.currentStembureauId,
                        this.currentPartyId,
                        candidateId,
                        voteCount
                );
                this.election.getRawVotes().add(rawVote);
            }
        }
    }

    @Override
    public void registerMetadata(boolean aggregated, Map<String, String> electionData) {}
}