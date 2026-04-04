package com.election.backendjava.service;

import com.election.backendjava.dto.CommentRequestDTO;
import com.election.backendjava.dto.CommentResponseDTO;
import com.election.backendjava.model.Comment;
import com.election.backendjava.model.Topic;
import com.election.backendjava.model.User;
import com.election.backendjava.repository.CommentRepository;
import com.election.backendjava.repository.TopicRepository;
import com.election.backendjava.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void addComment_shouldSaveComment_whenDataIsValid() {
        // Arrange
        String email = "user@test.com";
        CommentRequestDTO request = new CommentRequestDTO(1L, "Hello World", null, null);

        User mockUser = new User();
        mockUser.setEmail(email);
        mockUser.setUsername("John");

        Topic mockTopic = new Topic();
        mockTopic.setId(1L);

        Comment savedComment = new Comment("Hello World", mockUser, mockTopic);
        savedComment.setId(10L);
        savedComment.setCreatedAt(LocalDateTime.now());

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(topicRepository.findById(1L)).thenReturn(Optional.of(mockTopic));
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        // Act
        CommentResponseDTO result = commentService.addComment(request, email, null);

        // Assert
        assertNotNull(result);
        assertEquals("Hello World", result.content());
        assertEquals("John", result.authorName());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void addComment_shouldEscapeHtml_whenContentContainsScript() {
        // Arrange
        String email = "hacker@test.com";
        String maliciousInput = "<script>alert('xss')</script>";
        String expectedSafeOutput = "&lt;script&gt;alert(&#39;xss&#39;)&lt;/script&gt;";

        CommentRequestDTO request = new CommentRequestDTO(1L, maliciousInput, null, null);

        User mockUser = new User();
        Topic mockTopic = new Topic();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(topicRepository.findById(1L)).thenReturn(Optional.of(mockTopic));

        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CommentResponseDTO result = commentService.addComment(request, email, null);

        // Assert
        assertEquals(expectedSafeOutput, result.content());
    }
}