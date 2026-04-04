package com.election.backendjava.controller;

import com.election.backendjava.api.PartyWinnerController;
import com.election.backendjava.dto.MunicipalityPartyResultDTO;
import com.election.backendjava.dto.PartyWinnerDTO;
import com.election.backendjava.exception.ResourceNotFoundException;
import com.election.backendjava.service.MunicipalityWinnerService;
import com.election.backendjava.service.ProvinceWinnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PartyWinnerControllerTest {

    private MunicipalityWinnerService municipalityWinnerService;
    private ProvinceWinnerService provinceWinnerService;
    private PartyWinnerController controller;

    @BeforeEach
    void setup() {
        municipalityWinnerService = mock(MunicipalityWinnerService.class);
        provinceWinnerService = mock(ProvinceWinnerService.class);

        controller = new PartyWinnerController(
                municipalityWinnerService,
                provinceWinnerService
        );
    }

    // Happy flow tests

    @Test
    void getMunicipalityWinners_happyFlow() {
        List<MunicipalityPartyResultDTO> winners = List.of(
                new MunicipalityPartyResultDTO("amsterdam", "Amsterdam", "Party A", 5000)
        );

        when(municipalityWinnerService.getAllPartyResultsByYear(2023))
                .thenReturn(winners);

        ResponseEntity<?> response = controller.getMunicipalityWinners(2023);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(winners, response.getBody());
    }

    @Test
    void getProvinceWinners_happyFlow() {
        Map<String, ProvinceWinnerService.ProvinceResult> serviceResult = Map.of(
                "Noord-Holland", new ProvinceWinnerService.ProvinceResult("Party B", 10000, Map.of())
        );

        when(provinceWinnerService.calculateWinnerPerProvinceWithVotes(2023))
                .thenReturn(serviceResult);

        ResponseEntity<?> response = controller.getProvinceWinners(2023);

        assertEquals(200, response.getStatusCodeValue());

        List<PartyWinnerDTO> body = (List<PartyWinnerDTO>) response.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
        PartyWinnerDTO dto = body.get(0);
        assertEquals("Noord-Holland", dto.getPlace());
        assertEquals("Party B", dto.getPartyName());
        assertEquals(10000, dto.getTotalVotes());
        assertTrue(dto.getLosers().isEmpty());
    }

    // Unhappy flow tests

    @Test
    void getMunicipalityWinners_emptyList() {
        when(municipalityWinnerService.getAllPartyResultsByYear(2023))
                .thenThrow(new ResourceNotFoundException("Geen gemeentelijke winnaars gevonden voor jaar 2023"));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> controller.getMunicipalityWinners(2023)
        );

        assertEquals("Geen gemeentelijke winnaars gevonden voor jaar 2023", exception.getMessage());
    }

    @Test
    void getProvinceWinners_emptyList() {
        when(provinceWinnerService.calculateWinnerPerProvinceWithVotes(2023))
                .thenThrow(new ResourceNotFoundException("Geen provinciale winnaars gevonden voor jaar 2023"));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> controller.getProvinceWinners(2023)
        );

        assertEquals("Geen provinciale winnaars gevonden voor jaar 2023", exception.getMessage());
    }

    @Test
    void getMunicipalityWinners_serviceThrows() {
        when(municipalityWinnerService.getAllPartyResultsByYear(2023))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> controller.getMunicipalityWinners(2023)
        );

        assertEquals("DB error", exception.getMessage());
    }

    @Test
    void getProvinceWinners_serviceThrows() {
        when(provinceWinnerService.calculateWinnerPerProvinceWithVotes(2023))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> controller.getProvinceWinners(2023)
        );

        assertEquals("DB error", exception.getMessage());
    }
}
