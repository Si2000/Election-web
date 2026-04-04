package com.election.backendjava.api;

import com.election.backendjava.model.Candidate;
import com.election.backendjava.model.Election;
import com.election.backendjava.model.Party;
import com.election.backendjava.model.Region;
import com.election.backendjava.repository.CandidateRepository;
import com.election.backendjava.service.DutchElectionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;

import java.util.List;
/**
 * Demo controller for showing how you could load the election data in the backend.
 */
@RestController
@RequestMapping("elections")
public class ElectionController {
    private final DutchElectionService electionService;

    private final CandidateRepository candidateRepository;

    public ElectionController(DutchElectionService electionService, CandidateRepository candidateRepository) {
        this.electionService = electionService;
        this.candidateRepository = candidateRepository;

    }
    @GetMapping("/{year}")
    public List<Candidate> getCandidatesByYear(@PathVariable int year) {
        System.out.println("Opvragen van kandidaten voor het jaar: " + year);
        return candidateRepository.findByElectionYear(year);
    }

    /**
     * Processes the result for a specific election.
     * @param electionId the id of the election, e.g. the value of the Id attribute from the ElectionIdentifier tag.
     * @param folderName the name of the folder that contains the XML result files. If none is provided the value from
     *                   the electionId is used.
     * @return Election if the results have been processed successfully. Please be sure yoy don't output all the data!
     * Just the general data about the election should be sent back to the front-end!<br/>
     * <i>If you want to return something else please feel free to do so!</i>
     */
    @PostMapping("{electionId}")
    public Election readResults(@PathVariable String electionId, @RequestParam(required = false) String folderName) {
        if (folderName == null) {
            return electionService.readResults(electionId, electionId);
        } else {
            return electionService.readResults(electionId, folderName);
        }
    }

    @GetMapping("{electionId}/parties")
    public List<Party> getParties(
            @PathVariable String electionId,
            @RequestParam(required = false) String folderName) {

        if (folderName == null) folderName = electionId;

        Election election = electionService.readResults(electionId, folderName);
        return election.getParties();
    }

    @GetMapping("{electionId}/regions/structure")
    public List<Region> getRegionsStructure(@PathVariable String electionId) {
        String folderName = electionId + "-Partial/Structure";
        Election election = electionService.readResults(electionId, folderName);
        return election.getRegions();
    }


    @GetMapping("{electionId}/municipality")
    public Election getMunicipalities(@PathVariable String electionId, @RequestParam(required = false) String folderName) {
        if (folderName == null) {
            folderName = electionId;
        }
        return electionService.readResults(electionId, folderName);
    }

    @GetMapping("{electionId}/constituency")
    public Election getConstituency(@PathVariable String electionId, @RequestParam(required = false) String folderName) {
        if (folderName == null) {
            folderName = electionId;
        }
        return electionService.readResults(electionId, folderName);
    }


    @PostMapping("{electionId}/candidates")
    public List<Candidate> getAllCandidatesFromDb(@PathVariable String electionId,
                                                  @RequestParam(required = false) String folderName,
                                                  // --- VOEG DEZE PARAMETER TOE ---
                                                  @RequestParam(required = false) String partyName) {

        if (folderName == null) folderName = electionId;

        // Deze 'readResults' call lijkt data te parsen/laden.
        // We laten dit intact, zoals het in je originele code stond.
        Election election = electionService.readResults(electionId, folderName);
        if (election == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to load election data");
        }

        int year = Integer.parseInt(electionId.replaceAll("[^0-9]", ""));

        if (partyName != null && !partyName.isEmpty()) {
            System.out.println("Filteren op kandidaten voor jaar: " + year + " en partij: " + partyName);
            return candidateRepository.findByElectionYearAndPartyName(year, partyName);
        } else {
            System.out.println("Ophalen alle kandidaten voor jaar: " + year);
            return candidateRepository.findByElectionYear(year);
        }
    }
}

