package com.election.backendjava.controller;

import com.election.backendjava.api.CommentController;
import com.election.backendjava.dto.CommentRequestDTO;
import com.election.backendjava.dto.CommentResponseDTO;
import com.election.backendjava.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void addComment_shouldReturnOk_whenUserIsAuthenticated() throws Exception {
        // Arrange
        CommentRequestDTO request = new CommentRequestDTO(1L, "Great post!", null, null);
        CommentResponseDTO response = new CommentResponseDTO(10L, "Great post!", "testuser", LocalDateTime.now(), null, null, null);
        Principal mockPrincipal = mock(Principal.class);

        when(mockPrincipal.getName()).thenReturn("user@test.com");
        when(commentService.addComment(any(CommentRequestDTO.class), eq("user@test.com"), eq(null))).thenReturn(response);

        // Act
        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(mockPrincipal))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Great post!"))
                .andExpect(jsonPath("$.authorName").value("testuser"));
    }

    @Test
    void addComment_shouldReturnUnauthorized_whenPrincipalIsNull() throws Exception {
        // Arrange
        CommentRequestDTO request = new CommentRequestDTO(1L, "Great post!", null, null);

        // Act
        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))) // No principal provided
                // Assert
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getComments_shouldReturnListOfComments() throws Exception {
        // Arrange
        Long topicId = 1L;
        CommentResponseDTO comment1 = new CommentResponseDTO(1L, "First!", "User1", LocalDateTime.now(), null, null, null);
        CommentResponseDTO comment2 = new CommentResponseDTO(2L, "Second!", "User2", LocalDateTime.now(), null, null, null);
        List<CommentResponseDTO> comments = List.of(comment1, comment2);

        when(commentService.getCommentsByTopic(topicId)).thenReturn(comments);

        // Act
        mockMvc.perform(get("/api/comments/topic/{id}", topicId))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].content").value("First!"))
                .andExpect(jsonPath("$[1].content").value("Second!"));
    }
}