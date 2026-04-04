package com.election.backendjava.service;

import com.election.backendjava.model.Topic;
import com.election.backendjava.model.TopicLike;
import com.election.backendjava.model.User;
import com.election.backendjava.repository.TopicLikeRepository;
import com.election.backendjava.repository.TopicRepository;
import com.election.backendjava.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for managing forum topics and likes.
 */
@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final TopicLikeRepository topicLikeRepository;

    /**
     * Constructor for dependency injection.
     *
     * @param topicRepository Repository for topic persistence.
     * @param userRepository  Repository for user lookup.
     */
    public TopicService(
            TopicRepository topicRepository,
            UserRepository userRepository,
            TopicLikeRepository topicLikeRepository
    ) {
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
        this.topicLikeRepository = topicLikeRepository;
    }

    /**
     * Returns all topics sorted by creation date (newest first).
     */
    public List<Topic> getAllTopics() {
        List<Topic> topics = topicRepository.findAllByOrderByCreatedAtDesc();
        for (Topic topic : topics) {
            topic.setLikeCount((int) topicLikeRepository.countByTopicId(topic.getId()));
        }
        return topics;
    }

    /**
     * Creates a new topic.
     */
    public Topic createTopic(Topic topic, String identifier) {
        User author = userRepository.findByUsername(identifier).orElse(null);

        if (author == null) {
            author = userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new RuntimeException("User not found: " + identifier));
        }

        topic.setAuthor(author);
        topic.setCreatedAt(LocalDateTime.now());

        Topic saved = topicRepository.save(topic);
        saved.setLikeCount(0);
        return saved;
    }

    public List<Topic> getTopicsByUserId(Long userId) {
        List<Topic> topics = topicRepository.findByAuthor_IdOrderByCreatedAtDesc(userId);
        for (Topic topic : topics) {
            topic.setLikeCount((int) topicLikeRepository.countByTopicId(topic.getId()));
        }
        return topics;
    }


    /**
     * Returns a topic by ID.
     */
    public Topic getTopicById(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found with id: " + id));

        topic.setLikeCount((int) topicLikeRepository.countByTopicId(id));
        return topic;
    }

    /**
     * Returns the total number of likes for a topic.
     */
    public long getLikeCount(Long topicId) {
        return topicLikeRepository.countByTopicId(topicId);
    }

    /**
     * Checks if a user has liked a topic.
     */
    public boolean hasUserLiked(Long topicId, String identifier) {
        User user = userRepository.findByUsername(identifier).orElse(null);
        if (user == null) {
            user = userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new RuntimeException("User not found: " + identifier));
        }
        return topicLikeRepository.existsByTopicIdAndUserId(topicId, user.getId());
    }

    /**
     * Likes a topic.
     */
    @Transactional
    public void likeTopic(Long topicId, String identifier) {
        User user = userRepository.findByUsername(identifier).orElse(null);
        if (user == null) {
            user = userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new RuntimeException("User not found: " + identifier));
        }

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found: " + topicId));

        if (topicLikeRepository.existsByTopicIdAndUserId(topicId, user.getId())) {
            return;
        }

        topicLikeRepository.save(new TopicLike(topic, user));
    }

    /**
     * Unlikes a topic.
     */
    @Transactional
    public void unlikeTopic(Long topicId, String identifier) {
        User user = userRepository.findByUsername(identifier).orElse(null);
        if (user == null) {
            user = userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new RuntimeException("User not found: " + identifier));
        }

        topicLikeRepository.deleteByTopicIdAndUserId(topicId, user.getId());
    }

    /**
     * Deletes a topic by its ID.
     *
     * @param id The unique database ID of the topic.
     * @throws RuntimeException If the topic does not exist.
     */
    /**
     * Verwijdert een topic, maar alleen als de gebruiker de eigenaar is.
     */
    public void deleteTopic(Long id, String identifier) {
        // 1. Zoek de gebruiker
        User user = userRepository.findByUsername(identifier).orElse(null);
        if (user == null) {
            user = userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new RuntimeException("Gebruiker niet gevonden"));
        }

        // 2. Zoek de topic
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic niet gevonden met id: " + id));

        // 3. CHECK: Is deze gebruiker de auteur?
        // (We checken ID's om zeker te zijn)
        if (!topic.getAuthor().getId().equals(user.getId())) {
            // Eventueel kun je hier ook checken op ROLE_ADMIN als admins alles mogen verwijderen
            throw new RuntimeException("Je mag alleen je eigen berichten verwijderen!");
        }

        // 4. Verwijder de topic (JPA cascadeert vaak de likes, anders moet je die eerst verwijderen)
        topicRepository.delete(topic);
    }
}
