package com.election.backendjava.controller;

import com.election.backendjava.model.Candidate;
import com.election.backendjava.model.ElectionRegion;
import com.election.backendjava.service.ElectionCacheService;
import com.election.backendjava.service.ElectionFilterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ElectionDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ElectionCacheService cacheService;

    @MockBean
    private ElectionFilterService filterService;

    private final int YEAR = 2023;

    // GET /electionresults/{year}

    @Test
    void getNationalResults_shouldReturn200_whenRegionExists() throws Exception {
        // ARRANGE
        ElectionRegion national = new ElectionRegion("0", "Nederland", "nationaal");
        when(filterService.getNationalRegion(YEAR)).thenReturn(national);

        // ACT
        var result = mockMvc.perform(get("/electionresults/" + YEAR));

        // ASSERT
        result.andExpect(status().isOk());
    }

    @Test
    void getNationalResults_shouldReturn404_whenMissing() throws Exception {
        // ARRANGE
        when(filterService.getNationalRegion(YEAR)).thenReturn(null);

        // ACT
        var result = mockMvc.perform(get("/electionresults/" + YEAR));

        // ASSERT
        result.andExpect(status().isNotFound());
    }

    @Test
    void getConstituencyResults_shouldReturn200_whenKieskring() throws Exception {
        // ARRANGE
        ElectionRegion kieskring = new ElectionRegion("11", "Regio Noord", "KIESKRING");
        when(filterService.findRegion(YEAR, "11")).thenReturn(kieskring);

        // ACT
        var result = mockMvc.perform(
                get("/electionresults/" + YEAR + "/constituencies/11")
        );

        // ASSERT
        result.andExpect(status().isOk());
    }

    @Test
    void getConstituencyResults_shouldReturn404_whenWrongCategory() throws Exception {
        // ARRANGE
        ElectionRegion wrong = new ElectionRegion("11", "Regio Noord", "GEMEENTE");
        when(filterService.findRegion(YEAR, "11")).thenReturn(wrong);

        // ACT
        var result = mockMvc.perform(
                get("/electionresults/" + YEAR + "/constituencies/11")
        );

        // ASSERT
        result.andExpect(status().isNotFound());
    }

    @Test
    void getConstituencyResults_shouldReturn404_whenNotFound() throws Exception {
        // ARRANGE
        when(filterService.findRegion(YEAR, "11")).thenReturn(null);

        // ACT
        var result = mockMvc.perform(
                get("/electionresults/" + YEAR + "/constituencies/11")
        );

        // ASSERT
        result.andExpect(status().isNotFound());
    }

    // GET /electionresults/{year}/constituencies/{id}/candidates

    @Test
    void getConstituencyCandidates_shouldReturn200() throws Exception {
        // ARRANGE
        ElectionRegion kieskring = new ElectionRegion("11", "Regio Noord", "KIESKRING");

        when(filterService.findRegion(YEAR, "11")).thenReturn(kieskring);
        when(filterService.getCandidateVotesForRegion(kieskring, YEAR))
                .thenReturn(Collections.emptyList());

        // ACT
        var result = mockMvc.perform(
                get("/electionresults/" + YEAR + "/constituencies/11/candidates")
        );

        // ASSERT
        result.andExpect(status().isOk());
    }

    @Test
    void getConstituencyCandidates_shouldReturn404_whenWrongCategory() throws Exception {
        // ARRANGE
        ElectionRegion wrong = new ElectionRegion("11", "Regio Noord", "GEMEENTE");
        when(filterService.findRegion(YEAR, "11")).thenReturn(wrong);

        // ACT
        var result = mockMvc.perform(
                get("/electionresults/" + YEAR + "/constituencies/11/candidates")
        );

        // ASSERT
        result.andExpect(status().isNotFound());
    }

    // GET /electionresults/{year}/municipalities/{id}

    @Test
    void getMunicipality_shouldReturn200_whenGemeente() throws Exception {
        // ARRANGE
        ElectionRegion gemeente = new ElectionRegion("G_765", "Pekelen", "GEMEENTE");
        when(filterService.findRegionOrRange(YEAR, "G_765")).thenReturn(gemeente);

        // ACT
        var result = mockMvc.perform(
                get("/electionresults/" + YEAR + "/municipalities/G_765")
        );

        // ASSERT
        result.andExpect(status().isOk());
    }

    @Test
    void getMunicipality_shouldReturn404_whenNotGemeente() throws Exception {
        // ARRANGE
        ElectionRegion wrong = new ElectionRegion("392", "Haarlem", "KIESKRING");
        when(filterService.findRegion(YEAR, "392")).thenReturn(wrong);

        // ACT
        var result = mockMvc.perform(
                get("/electionresults/" + YEAR + "/municipalities/392")
        );

        // ASSERT
        result.andExpect(status().isNotFound());
    }

    // GET /electionresults/{year}/municipalities/{id}/candidates

    @Test
    void getMunicipalityCandidates_shouldReturn200() throws Exception {
        // ARRANGE
        ElectionRegion gemeente = new ElectionRegion("G_765", "Pekela", "GEMEENTE");

        when(filterService.findRegionOrRange(YEAR, "G_765")).thenReturn(gemeente);
        when(filterService.getCandidateVotesForRegion(gemeente, YEAR))
                .thenReturn(Collections.emptyList());

        // ACT
        var result = mockMvc.perform(
                get("/electionresults/" + YEAR + "/municipalities/G_765/candidates")
        );

        // ASSERT
        result.andExpect(status().isOk());
    }

    @Test
    void getMunicipalityCandidates_shouldReturn404_wrongCategory() throws Exception {
        // ARRANGE
        ElectionRegion wrong = new ElectionRegion("392", "Haarlem", "KIESKRING");

        when(filterService.findRegion(YEAR, "392")).thenReturn(wrong);

        // ACT
        var result = mockMvc.perform(
                get("/electionresults/" + YEAR + "/municipalities/392/candidates")
        );

        // ASSERT
        result.andExpect(status().isNotFound());
    }

    // GET /electionresults/{year}/pollingstations/{id}

    @Test
    void getPollingStation_shouldReturn200_whenStembureau() throws Exception {
        // ARRANGE
        ElectionRegion station = new ElectionRegion("510001", "Stembureau A", "Stembureau");
        when(filterService.findRegion(YEAR, "510001")).thenReturn(station);

        // ACT
        var result = mockMvc.perform(
                get("/electionresults/" + YEAR + "/pollingstations/510001")
        );

        // ASSERT
        result.andExpect(status().isOk());
    }

    @Test
    void getPollingStation_shouldReturn404_whenWrongCategory() throws Exception {
        // ARRANGE
        ElectionRegion wrong = new ElectionRegion("510001", "Stembureau A", "GEMEENTE");
        when(filterService.findRegion(YEAR, "510001")).thenReturn(wrong);

        // ACT
        var result = mockMvc.perform(
                get("/electionresults/" + YEAR + "/pollingstations/510001")
        );

        // ASSERT
        result.andExpect(status().isNotFound());
    }

    // GET /electionresults/{year}/pollingstations/{id}/candidates

    @Test
    void getPollingStationCandidates_shouldReturn200() throws Exception {
        // ARRANGE
        ElectionRegion station = new ElectionRegion("510001", "Stembureau A", "Stembureau");

        when(filterService.findRegion(YEAR, "510001")).thenReturn(station);
        when(filterService.getCandidateVotesForRegion(station, YEAR))
                .thenReturn(Collections.emptyList());

        // ACT
        var result = mockMvc.perform(
                get("/electionresults/" + YEAR + "/pollingstations/510001/candidates")
        );

        // ASSERT
        result.andExpect(status().isOk());
    }

    @Test
    void getPollingStationCandidates_shouldReturn404_whenWrongCategory() throws Exception {
        // ARRANGE
        ElectionRegion wrong = new ElectionRegion("510001", "Stembureau A", "GEMEENTE");

        when(filterService.findRegion(YEAR, "510001")).thenReturn(wrong);

        // ACT
        var result = mockMvc.perform(
                get("/electionresults/" + YEAR + "/pollingstations/510001/candidates")
        );

        // ASSERT
        result.andExpect(status().isNotFound());
    }

    // GET /electionresults/{year}/candidates

    @Test
    void getFilteredCandidates_shouldReturn200() throws Exception {
        // ARRANGE
        Candidate c = new Candidate();
        when(filterService.getFilteredCandidates(YEAR, "VVD", "male"))
                .thenReturn(List.of(c));

        // ACT
        var result = mockMvc.perform(
                get("/electionresults/" + YEAR + "/candidates")
                        .param("party", "VVD")
                        .param("gender", "male")
        );

        // ASSERT
        result.andExpect(status().isOk());
        verify(filterService).getFilteredCandidates(YEAR, "VVD", "male");
    }
}
