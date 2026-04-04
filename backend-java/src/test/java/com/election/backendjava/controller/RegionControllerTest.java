package com.election.backendjava.controller;

import com.election.backendjava.api.RegionController;
import com.election.backendjava.config.JwtAuthenticationFilter;
import com.election.backendjava.model.Region;
import com.election.backendjava.repository.RegionJPARepository;
import com.election.backendjava.repository.UserRepository;
import com.election.backendjava.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegionController.class)
@AutoConfigureMockMvc(addFilters = false)
class RegionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegionJPARepository regionRepo;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private UserRepository userRepository;

    @Test
    void addRegion_ShouldSaveAndReturnRegion() throws Exception {
        // Arrange
        Region inputRegion = new Region();
        inputRegion.setRegionName("New Region");

        Region savedRegion = new Region();
        savedRegion.setRegionName("New Region");

        when(regionRepo.save(any(Region.class))).thenReturn(savedRegion);

        // Act
        var result = mockMvc.perform(post("/regions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputRegion)));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.regionName").value("New Region"));
        verify(regionRepo, times(1)).save(any(Region.class));
    }

    @Test
    void getAllRegions_ShouldReturnList() throws Exception {
        // Arrange
        Region r1 = new Region();
        r1.setRegionName("Region 1");
        Region r2 = new Region();
        r2.setRegionName("Region 2");

        when(regionRepo.findAll()).thenReturn(List.of(r1, r2));

        // Act
        var result = mockMvc.perform(get("/regions"));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getRegionById_ShouldReturnRegion() throws Exception {
        // Arrange
        Long id = 1L;
        Region region = new Region();
        region.setRegionName("Target Region");

        when(regionRepo.findById(id)).thenReturn(region);

        // Act
        var result = mockMvc.perform(get("/regions/{id}", id));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.regionName").value("Target Region"));
    }

    @Test
    void deleteRegion_ShouldCallDelete() throws Exception {
        // Arrange
        Long id = 1L;

        // Act
        var result = mockMvc.perform(delete("/regions/{id}", id));

        // Assert
        result.andExpect(status().isOk());
        verify(regionRepo, times(1)).deleteById(id);
    }
}