package com.election.backendjava.service;

import com.election.backendjava.model.Topic;
import com.election.backendjava.model.User;
import com.election.backendjava.repository.TopicLikeRepository;
import com.election.backendjava.repository.TopicRepository;
import com.election.backendjava.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TopicServiceTest {

    private TopicRepository topicRepository;
    private UserRepository userRepository;
    private TopicService topicService;
    private TopicLikeRepository topicLikeRepository;

    @BeforeEach
    void setUp() {
        topicRepository = Mockito.mock(TopicRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        topicLikeRepository = Mockito.mock(TopicLikeRepository.class);
        topicService = new TopicService(topicRepository, userRepository, topicLikeRepository);
    }

    @Test
    void getAllTopics_ShouldReturnListOfTopics() {
        // ARRANGE
        Topic topic = new Topic();
        topic.setId(1L);
        topic.setTitle("Verkiezingen 2023");
        topic.setLikeCount(5);

        when(topicRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(topic));
        when(topicLikeRepository.countByTopicId(1L)).thenReturn(5L);

        // ACT
        List<Topic> result = topicService.getAllTopics();

        // ASSERT
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Verkiezingen 2023", result.get(0).getTitle());
        assertEquals(5, result.get(0).getLikeCount());
        verify(topicRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void createTopic_HappyPath_FoundByUsername() {
        // ARRANGE
        String username = "jan_jansen";

        User user = new User();
        user.setId(10L);
        user.setUsername(username);

        Topic inputTopic = new Topic();
        inputTopic.setTitle("Nieuwe Discussie");
        inputTopic.setContent("Inhoud...");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(topicRepository.save(any(Topic.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        Topic result = topicService.createTopic(inputTopic, username);

        // ASSERT
        assertNotNull(result.getAuthor(), "Auteur moet gekoppeld zijn");
        assertEquals(username, result.getAuthor().getUsername());
        assertNotNull(result.getCreatedAt(), "Aanmaakdatum moet automatisch ingesteld zijn");

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, never()).findByEmail(anyString());
        verify(topicRepository, times(1)).save(inputTopic);
    }

    @Test
    void createTopic_HappyPath_FoundByEmail_Fallback() {
        // ARRANGE
        String email = "jan@example.com";

        User user = new User();
        user.setEmail(email);

        Topic inputTopic = new Topic();
        inputTopic.setTitle("Email Test");

        when(userRepository.findByUsername(email)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        when(topicRepository.save(any(Topic.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        Topic result = topicService.createTopic(inputTopic, email);

        // ASSERT
        assertNotNull(result.getAuthor());
        assertEquals(email, result.getAuthor().getEmail());

        verify(userRepository, times(1)).findByUsername(email);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void createTopic_UnhappyPath_UserNotFound() {
        // ARRANGE
        String unknownIdentifier = "spook@gebruiker.nl";
        Topic inputTopic = new Topic();

        when(userRepository.findByUsername(unknownIdentifier)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(unknownIdentifier)).thenReturn(Optional.empty());

        // ACT & ASSERT
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            topicService.createTopic(inputTopic, unknownIdentifier);
        });

        assertEquals("User not found: " + unknownIdentifier, exception.getMessage());

        verify(topicRepository, never()).save(any());
    }

    @Test
    void getTopicById_HappyPath_TopicFound() {
        // ARRANGE
        Long topicId = 5L;
        Topic topic = new Topic();
        topic.setId(topicId);
        topic.setTitle("Gevonden!");

        when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));

        // ACT
        Topic result = topicService.getTopicById(topicId);

        // ASSERT
        assertNotNull(result);
        assertEquals(topicId, result.getId());
        assertEquals("Gevonden!", result.getTitle());
    }

    @Test
    void getTopicById_UnhappyPath_TopicNotFound() {
        // ARRANGE
        Long topicId = 999L;
        when(topicRepository.findById(topicId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            topicService.getTopicById(topicId);
        });

        assertEquals("Topic not found with id: " + topicId, exception.getMessage());
    }
}