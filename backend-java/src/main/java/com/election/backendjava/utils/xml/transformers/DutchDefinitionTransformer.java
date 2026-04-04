package com.election.backendjava.utils.xml.transformers;

import com.election.backendjava.model.Election;
import com.election.backendjava.model.Party;
import com.election.backendjava.model.Region;
import com.election.backendjava.repository.RegionJPARepository;
import com.election.backendjava.utils.xml.DefinitionTransformer;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DutchDefinitionTransformer implements DefinitionTransformer {
    private final Election election;
    private final RegionJPARepository regionRepo;

    private final Set<String> knownRegionIds;

    public DutchDefinitionTransformer(Election election, RegionJPARepository regionRepo) {
        this.election = election;
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
    public void registerRegion(Map<String, String> electionData) {
        String name = electionData.get("RegionName");
        String category = electionData.get("Region-RegionCategory");
        String rawLogicalNumber = electionData.get("Region-RegionNumber");
        String rawSuperLogicalNumber = electionData.get("Region-SuperiorRegionNumber");

        // Make unique id's by using prefixes
        String logicalNumber = null;
        if (rawLogicalNumber != null) {
            String norm = normalizeId(rawLogicalNumber);
            if ("KIESKRING".equals(category)) logicalNumber = "K_" + norm;
            else if ("GEMEENTE".equals(category)) logicalNumber = "G_" + norm;
            else if ("STEMBUREAU".equals(category)) logicalNumber = "S_" + norm;
            else if ("STAAT".equals(category)) logicalNumber = "NL_ROOT";
            else logicalNumber = norm;
        }

        String superLogicalNumber = null;
        if (rawSuperLogicalNumber != null) {
            String norm = normalizeId(rawSuperLogicalNumber);

            if ("STEMBUREAU".equals(category)) {
                superLogicalNumber = "G_" + norm;
            }
            else if ("GEMEENTE".equals(category)) {
                superLogicalNumber = "K_" + norm;
            }
            else if ("KIESKRING".equals(category)) {
                superLogicalNumber = "NL_ROOT";
            }
            else {
                superLogicalNumber = norm;
            }
        }


        if (logicalNumber == null && "STAAT".equals(category)) logicalNumber = "NL_ROOT";
        if ("STAAT".equals(category)) superLogicalNumber = null;

        if (category != null && logicalNumber != null && name != null) {
            if (!this.knownRegionIds.contains(logicalNumber)) {
                Region region = new Region(logicalNumber, name, category, superLogicalNumber);
                regionRepo.save(region);

                this.knownRegionIds.add(logicalNumber);
                election.getRegions().add(region);
            }
        }
    }

    @Override
    public void registerParty(Map<String, String> electionData) {
        Party party = new Party(electionData.get("RegisteredAppellation"));
        election.getParties().add(party);
    }
}