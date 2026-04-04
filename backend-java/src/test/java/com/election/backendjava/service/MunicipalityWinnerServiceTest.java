package com.election.backendjava.service;

import com.election.backendjava.dto.MunicipalityPartyResultDTO;
import com.election.backendjava.model.ElectionRegion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MunicipalityWinnerServiceTest {

    private ElectionCacheService cacheService;
    private ElectionFilterService filterService;
    private MunicipalityWinnerService service;

    @BeforeEach
    void setUp() {
        cacheService = mock(ElectionCacheService.class);
        filterService = mock(ElectionFilterService.class);
        service = new MunicipalityWinnerService(cacheService, filterService);
    }

    @Test
    void testGetAllPartyResultsByYear_HappyPath() {
        // Arrange
        ElectionRegion municipality1 = new ElectionRegion("M1", "Groningen", "Municipality");
        municipality1.setVotesPerParty(Map.of("PartyA", 100, "PartyB", 50));

        ElectionRegion municipality2 = new ElectionRegion("M2", "Arnhem", "Municipality");
        municipality2.setVotesPerParty(Map.of("PartyC", 70, "PartyA", 40));

        when(filterService.getAllMunicipalities(2025))
                .thenReturn(List.of(municipality1, municipality2));

        // Act
        List<MunicipalityPartyResultDTO> results = service.getAllPartyResultsByYear(2025);

        // Assert
        assertEquals(4, results.size()); // 2 municipalities * 2 parties each

        // Check first municipality (Groningen) sorted correctly
        MunicipalityPartyResultDTO first = results.get(0);
        assertEquals("M1", first.getId());
        assertEquals("Groningen", first.getName());
        assertEquals("PartyA", first.getParty());
        assertEquals(100, first.getVotes());

        MunicipalityPartyResultDTO second = results.get(1);
        assertEquals("M1", second.getId());
        assertEquals("Groningen", second.getName());
        assertEquals("PartyB", second.getParty());
        assertEquals(50, second.getVotes());

        // Check second municipality (Arnhem) sorted correctly
        MunicipalityPartyResultDTO third = results.get(2);
        assertEquals("M2", third.getId());
        assertEquals("Arnhem", third.getName());
        assertEquals("PartyC", third.getParty());
        assertEquals(70, third.getVotes());

        MunicipalityPartyResultDTO fourth = results.get(3);
        assertEquals("M2", fourth.getId());
        assertEquals("Arnhem", fourth.getName());
        assertEquals("PartyA", fourth.getParty());
        assertEquals(40, fourth.getVotes());
    }

    @Test
    void testGetAllPartyResultsByYear_EmptyList() {
        // Arrange
        when(filterService.getAllMunicipalities(2025)).thenReturn(List.of());

        // Act
        List<MunicipalityPartyResultDTO> results = service.getAllPartyResultsByYear(2025);

        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    void testGetAllPartyResultsByYear_NullOrEmptyVotes() {
        // Arrange
        ElectionRegion municipality1 = new ElectionRegion("M1", "EmptyTown", "Municipality");
        municipality1.setVotesPerParty(Map.of());

        ElectionRegion municipality2 = new ElectionRegion("M2", "NullVotesTown", "Municipality");
        municipality2.setVotesPerParty(null);

        when(filterService.getAllMunicipalities(2025))
                .thenReturn(List.of(municipality1, municipality2));

        // Act
        List<MunicipalityPartyResultDTO> results = service.getAllPartyResultsByYear(2025);

        // Assert
        assertTrue(results.isEmpty());
    }
}
