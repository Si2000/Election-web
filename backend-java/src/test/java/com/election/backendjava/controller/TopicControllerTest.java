package com.election.backendjava.controller;

import com.election.backendjava.api.TopicController;
import com.election.backendjava.config.JwtAuthenticationFilter;
import com.election.backendjava.model.Topic;
import com.election.backendjava.repository.UserRepository;
import com.election.backendjava.service.JwtService;
import com.election.backendjava.service.TopicService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TopicController.class)
@AutoConfigureMockMvc(addFilters = false)
class TopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TopicService topicService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private UserRepository userRepository;

    @Test
    void getAllTopics_ShouldReturnListOfTopics() throws Exception {
        // Arrange
        Topic t1 = new Topic();
        t1.setId(1L);
        t1.setTitle("Topic 1");

        Topic t2 = new Topic();
        t2.setId(2L);
        t2.setTitle("Topic 2");

        when(topicService.getAllTopics()).thenReturn(List.of(t1, t2));

        // Act
        var result = mockMvc.perform(get("/api/topics"));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void createTopic_HappyPath_UserIsLoggedIn() throws Exception {
        // Arrange
        String username = "testuser";
        Topic inputTopic = new Topic();
        inputTopic.setTitle("Nieuw Topic");

        Topic savedTopic = new Topic();
        savedTopic.setId(10L);
        savedTopic.setTitle("Nieuw Topic");

        when(topicService.createTopic(any(Topic.class), eq(username))).thenReturn(savedTopic);

        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        // Act
        var result = mockMvc.perform(post("/api/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputTopic))
                .principal(mockPrincipal));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void createTopic_UnhappyPath_UserNotLoggedIn() throws Exception {
        // Arrange
        Topic inputTopic = new Topic();
        inputTopic.setTitle("Hack poging");

        // Act
        var result = mockMvc.perform(post("/api/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputTopic)));

        // Assert
        result.andExpect(status().isUnauthorized());
    }

    @Test
    void getTopicById_HappyPath_TopicFound() throws Exception {
        // Arrange
        Long id = 5L;
        Topic topic = new Topic();
        topic.setId(id);
        topic.setTitle("Gevonden Topic");

        when(topicService.getTopicById(id)).thenReturn(topic);

        // Act
        var result = mockMvc.perform(get("/api/topics/{id}", id));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }
}