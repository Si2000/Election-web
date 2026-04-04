package com.election.backendjava.service;

import com.election.backendjava.dto.MunicipalityPartyResultDTO;
import com.election.backendjava.model.ElectionRegion;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MunicipalityWinnerService {

    private final ElectionCacheService cacheService;
    private final ElectionFilterService electionFilterService;

    public MunicipalityWinnerService(ElectionCacheService cacheService, ElectionFilterService electionFilterService) {
        this.cacheService = cacheService;
        this.electionFilterService = electionFilterService;
    }

    /**
     * Haalt alle partijen per gemeente op voor een bepaald jaar.
     * Gesorteerd op aantal stemmen (hoog > laag)
     */
    public List<MunicipalityPartyResultDTO> getAllPartyResultsByYear(int year) {
        List<MunicipalityPartyResultDTO> result = new ArrayList<>();
        List<ElectionRegion> municipalities = electionFilterService.getAllMunicipalities(year);

        for (ElectionRegion municipality : municipalities) {
            Map<String, Integer> votesPerParty = municipality.getVotesPerParty();
            if (votesPerParty == null || votesPerParty.isEmpty()) continue;

            votesPerParty.entrySet().stream()
                    .sorted((a, b) -> b.getValue() - a.getValue())
                    .forEach(e -> {
                        result.add(new MunicipalityPartyResultDTO(
                                municipality.getId(),
                                municipality.getName(),
                                e.getKey(),
                                e.getValue()
                        ));
                    });
        }

        return result;
    }
}
