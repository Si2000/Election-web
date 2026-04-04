package com.election.backendjava.api;

import com.election.backendjava.dto.MunicipalityPartyResultDTO;
import com.election.backendjava.dto.PartyWinnerDTO;
import com.election.backendjava.exception.ResourceNotFoundException;
import com.election.backendjava.service.MunicipalityWinnerService;
import com.election.backendjava.service.ProvinceWinnerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/electionresults")
public class PartyWinnerController {

    private final MunicipalityWinnerService municipalityWinnerService;
    private final ProvinceWinnerService provinceWinnerService;

    public PartyWinnerController(MunicipalityWinnerService municipalityWinnerService, ProvinceWinnerService provinceWinnerService) {
        this.municipalityWinnerService = municipalityWinnerService;
        this.provinceWinnerService = provinceWinnerService;
    }

    @GetMapping("/{year}/municipalities/winners")
    public ResponseEntity<List<MunicipalityPartyResultDTO>> getMunicipalityWinners(@PathVariable int year) {
        List<MunicipalityPartyResultDTO> results = municipalityWinnerService.getAllPartyResultsByYear(year);
        if (results == null || results.isEmpty()) {
            throw new ResourceNotFoundException("Geen gemeentelijke winnaars gevonden voor jaar " + year);
        }
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{year}/provinces/winners")
    public ResponseEntity<List<PartyWinnerDTO>> getProvinceWinners(@PathVariable int year) {
        Map<String, ProvinceWinnerService.ProvinceResult> winners =
                provinceWinnerService.calculateWinnerPerProvinceWithVotes(year);
        if (winners == null || winners.isEmpty()) {
            throw new ResourceNotFoundException("Geen provinciale winnaars gevonden voor jaar " + year);
        }
        List<PartyWinnerDTO> dtoList = winners.entrySet().stream()
                .map(e -> new PartyWinnerDTO(e.getKey(), e.getValue().winner, e.getValue().totalVotes, e.getValue().losers))
                .toList();
        return ResponseEntity.ok(dtoList);
    }
}
