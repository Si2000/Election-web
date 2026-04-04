package com.election.backendjava.controller;

import com.election.backendjava.api.ReportController;
import com.election.backendjava.config.JwtAuthenticationFilter;
import com.election.backendjava.repository.UserRepository;
import com.election.backendjava.service.JwtService;
import com.election.backendjava.service.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportController.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReportService reportService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private UserRepository userRepository;


    @Test
    void reportTopic_UnhappyPath_UserNotLoggedIn_ShouldReturn403() throws Exception {
        // ARRANGE
        Long topicId = 123L;
        Map<String, String> payload = Map.of("reason", "Spam");

        // ACT & ASSERT
        mockMvc.perform(post("/api/reports/topics/{id}", topicId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isForbidden());

        verify(reportService, never()).reportTopic(any(), any(), any());
    }

    @Test
    void reportTopic_UnhappyPath_ServiceThrowsException_ShouldReturnError() throws Exception {
        // ARRANGE
        Long topicId = 999L;
        String username = "melder@example.com";
        Map<String, String> payload = Map.of("reason", "Spam");

        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        doThrow(new RuntimeException("Topic niet gevonden"))
                .when(reportService).reportTopic(eq(topicId), eq(username), any());

        // ACT & ASSERT
        try {
            mockMvc.perform(post("/api/reports/topics/{id}", topicId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload))
                    .principal(mockPrincipal));
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof RuntimeException);
            assertEquals("Topic niet gevonden", e.getCause().getMessage());
        }
    }
}