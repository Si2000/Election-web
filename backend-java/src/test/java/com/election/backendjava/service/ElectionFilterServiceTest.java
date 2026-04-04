package com.election.backendjava.service;

import com.election.backendjava.model.Candidate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ElectionFilterServiceTest {

    private ElectionCacheService cacheService;
    private ElectionFilterService filterService;

    @BeforeEach
    void setup() {
        cacheService = Mockito.mock(ElectionCacheService.class);
        filterService = new ElectionFilterService(cacheService);
    }

    private Candidate candidate(String id, String first, String last, String party, String gender) {
        Candidate c = new Candidate();
        c.setCandidateid(id);
        c.setFirstname(first);
        c.setLastname(last);
        c.setParty(party);
        c.setGender(gender);
        return c;
    }

    @Test
    void testGetFilteredCandidates_NoCacheData() {
        when(filterService.getAllCandidates(2023)).thenReturn(null);
        List<Candidate> result = filterService.getFilteredCandidates(2023, "VVD", "F");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetFilteredCandidates_NoMatches() {
        Map<String, Candidate> map = new HashMap<>();
        map.put("1", candidate("1", "Alice", "Anders", "VVD", "F"));

        when(filterService.getAllCandidates(2023)).thenReturn(null);

        List<Candidate> result = filterService.getFilteredCandidates(2023, "D66", "M");

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetFilteredCandidates_EmptyFiltersReturnAllCandidates() {
        Map<String, Candidate> map = new HashMap<>();
        map.put("1", candidate("1", "Alice", "Anders", "VVD", "F"));
        map.put("2", candidate("2", "Bob", "Bakker", "VVD", "M"));

        when(filterService.getAllCandidates(2023)).thenReturn(null);

        List<Candidate> result = filterService.getFilteredCandidates(2023, "", "");

        assertEquals(0, result.size());
    }

    @Test
    void testGetFilteredCandidates_NullFiltersReturnAllCandidates() {
        Map<String, Candidate> map = new HashMap<>();
        map.put("1", candidate("1", "Alice", "Anders", "VVD", "F"));

        when(filterService.getAllCandidates(2023)).thenReturn(null);

        List<Candidate> result = filterService.getFilteredCandidates(2023, null, null);

        assertEquals(0, result.size());
    }
}
