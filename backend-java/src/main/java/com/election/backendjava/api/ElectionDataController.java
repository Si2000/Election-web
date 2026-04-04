package com.election.backendjava.api;

import com.election.backendjava.dto.CandidateDTO;
import com.election.backendjava.dto.ElectionRegionDTO;
import com.election.backendjava.model.Candidate;
import com.election.backendjava.model.ElectionRegion;
import com.election.backendjava.service.ElectionCacheService;
import com.election.backendjava.service.ElectionFilterService;
import com.election.backendjava.service.MunicipalityWinnerService;
import com.election.backendjava.service.ProvinceWinnerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/electionresults")
public class ElectionDataController {

    private final ElectionCacheService cacheService;
    private final ElectionFilterService filterService;

    public ElectionDataController(ElectionCacheService cacheService, ElectionFilterService filterService, MunicipalityWinnerService municipalityWinnerService, ProvinceWinnerService provinceWinnerService) {
        this.cacheService = cacheService;
        this.filterService = filterService;
    }



    // This gives all data
    @GetMapping("/{year}")
    public ResponseEntity<ElectionRegionDTO> getNationalResults(@PathVariable int year) {
        ElectionRegion root = filterService.getNationalRegion(year);
        if (root == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ElectionRegionDTO(root));
    }

    @GetMapping("/{year}/national/candidates")
    public ResponseEntity<List<CandidateDTO>> getNationalCandidateResults(
            @PathVariable int year) {

        ElectionRegion national = filterService.getNationalRegion(year);
        if (national == null) {
            return ResponseEntity.notFound().build();
        }

        List<CandidateDTO> candidateVotes =
                filterService.getCandidateVotesForRegion(national, year);

        return ResponseEntity.ok(candidateVotes);
    }

    // This gives all data on constituency niveau

    @GetMapping("/{year}/constituencies/{id}")
    public ResponseEntity<ElectionRegionDTO> getConstituencyResults(
            @PathVariable int year,
            @PathVariable String id) {

        ElectionRegion node = filterService.findRegion(year, id);
        if (node == null || !node.getCategory().equals("KIESKRING")) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ElectionRegionDTO(node));
    }

    // This gives all data on constituency niveau with candidates

    @GetMapping("/{year}/constituencies/{id}/candidates")
    public ResponseEntity<List<CandidateDTO>> getCandidateResultsForConstituency(
            @PathVariable int year,
            @PathVariable String id) {

        ElectionRegion node = filterService.findRegion(year, id);
        if (node == null || !node.getCategory().equals("KIESKRING")) {
            return ResponseEntity.notFound().build();
        }
        List<CandidateDTO> candidateVotes = filterService.getCandidateVotesForRegion(node, year);
        return ResponseEntity.ok(candidateVotes);
    }

    // This gives all data on municipality niveau

    @GetMapping("/{year}/municipalities/{id}")
    public ResponseEntity<ElectionRegionDTO> getMunicipalityResults(
            @PathVariable int year,
            @PathVariable String id) {

        ElectionRegion node = filterService.findRegionOrRange(year, id);

        if (node == null) {
            System.err.println("Controller: ID niet gevonden: " + id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ElectionRegionDTO(node));
    }



    // This gives all data on municipality niveau with candidates

    @GetMapping("/{year}/municipalities/{id}/candidates")
    public ResponseEntity<List<CandidateDTO>> getCandidateResultsForMunicipality(
            @PathVariable int year,
            @PathVariable String id) {

        ElectionRegion node = filterService.findRegionOrRange(year, id);

        if (node == null) {
            return ResponseEntity.notFound().build();
        }

        List<CandidateDTO> candidateVotes = filterService.getCandidateVotesForRegion(node, year);

        return ResponseEntity.ok(candidateVotes);
    }

    // This gives all data on polling station niveau

    @GetMapping("/{year}/pollingstations/{id}")
    public ResponseEntity<ElectionRegionDTO> getPollingStationResults(
            @PathVariable int year,
            @PathVariable String id) {

        ElectionRegion node = filterService.findRegion(year, id);
        if (node == null || !node.getCategory().equals("Stembureau")) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ElectionRegionDTO(node));
    }

    // This gives all data on polling station niveau with candidates

    @GetMapping("/{year}/pollingstations/{id}/candidates")
    public ResponseEntity<List<CandidateDTO>> getCandidateResultsForPollingStation(
            @PathVariable int year,
            @PathVariable String id) {

        ElectionRegion node = filterService.findRegion(year, id);
        if (node == null || !node.getCategory().equals("Stembureau")) {
            return ResponseEntity.notFound().build();
        }
        List<CandidateDTO> candidateVotes = filterService.getCandidateVotesForRegion(node, year);
        return ResponseEntity.ok(candidateVotes);
    }

    // This gives all data of every municipality

    @GetMapping("/{year}/municipalities")
    public ResponseEntity<List<ElectionRegionDTO>> getAllMunicipalities(@PathVariable int year) {
        List<ElectionRegion> municipalities = filterService.getAllMunicipalities(year);

        List<ElectionRegionDTO> dtos = municipalities.stream()
                .map(ElectionRegionDTO::new)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{year}/constituencies")
    public ResponseEntity<List<ElectionRegionDTO>> getAllConstituencies(@PathVariable int year) {
        List<ElectionRegion> constituencies = filterService.getAllConstituencies(year);

        List<ElectionRegionDTO> dtos = constituencies.stream()
                .map(ElectionRegionDTO::new)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{year}/candidates")
    public ResponseEntity<List<Candidate>> getCandidates(
            @PathVariable int year,
            @RequestParam(required = false) String party,
            @RequestParam(required = false) String gender) {

        return ResponseEntity.ok(filterService.getFilteredCandidates(year, party, gender));
    }

    @GetMapping("/{year}/postcode/{postcode}")
    public ResponseEntity<List<ElectionRegionDTO>> getPostcodeResults(
            @PathVariable int year,
            @PathVariable String postcode) {

        List<ElectionRegion> results = filterService.findPostcode(year, postcode);

        if (results.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<ElectionRegionDTO> dtos = results.stream()
                .map(ElectionRegionDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * Endpoint for aggregating results based on a postcode range.
     */
    @GetMapping("/{year}/postcoderange")
    public ResponseEntity<List<ElectionRegionDTO>> getPostcodeRange(
            @PathVariable int year,
            @RequestParam String start,
            @RequestParam String end) {

        List<ElectionRegion> results = filterService.getAggregatedRange(year, start, end);

        if (results.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<ElectionRegionDTO> dtos = results.stream()
                .map(ElectionRegionDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

}