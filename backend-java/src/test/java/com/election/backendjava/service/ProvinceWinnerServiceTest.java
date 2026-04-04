package com.election.backendjava.service;

import com.election.backendjava.model.ElectionRegion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProvinceWinnerServiceTest {

    private ElectionFilterService filterService;
    private ProvinceWinnerService service;

    @BeforeEach
    void setUp() {
        filterService = mock(ElectionFilterService.class);
        service = new ProvinceWinnerService(filterService);
    }

    @Test
    void testCalculateWinnerPerProvince_HappyPath() {
        // Arrange
        ElectionRegion groningen = new ElectionRegion("GR", "Groningen", "Province");
        groningen.setVotesPerParty(Map.of("PartyA", 100, "PartyB", 50));

        ElectionRegion arnhem = new ElectionRegion("AR", "Arnhem", "Constituency");
        arnhem.setVotesPerParty(Map.of("PartyA", 40, "PartyB", 60));

        when(filterService.getAllConstituencies(2024)).thenReturn(List.of(groningen, arnhem));

        // Act
        Map<String, ProvinceWinnerService.ProvinceResult> result =
                service.calculateWinnerPerProvinceWithVotes(2024);

        // Assert Groningen
        ProvinceWinnerService.ProvinceResult groningenResult = result.get("Groningen");
        assertNotNull(groningenResult);
        assertEquals("PartyA", groningenResult.winner);
        assertEquals(100, groningenResult.totalVotes);
        assertEquals(Map.of("PartyB", 50), groningenResult.losers);

        // Assert Gelderland (Arnhem)
        ProvinceWinnerService.ProvinceResult gelderlandResult = result.get("Gelderland");
        assertNotNull(gelderlandResult);
        assertEquals("PartyB", gelderlandResult.winner);
        assertEquals(60, gelderlandResult.totalVotes);
        assertEquals(Map.of("PartyA", 40), gelderlandResult.losers);
    }


    @Test
    void testCalculateWinnerPerProvince_EmptyList() {
        // Arrange
        when(filterService.getAllConstituencies(2024)).thenReturn(List.of());

        // Act
        Map<String, ProvinceWinnerService.ProvinceResult> result =
                service.calculateWinnerPerProvinceWithVotes(2024);

        // Assert
        assertTrue(result.isEmpty());
    }
}
