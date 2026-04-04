package com.election.backendjava.api;

import com.election.backendjava.model.Topic;
import com.election.backendjava.service.TopicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * REST controller for managing forum topics.
 * <p>
 * Exposes endpoints for listing, creating, and retrieving individual discussion topics.
 * Allows CORS requests from the frontend application.
 */
@RestController
@RequestMapping("/api/topics")
public class TopicController {

    private final TopicService topicService;

    /**
     * Constructor for dependency injection.
     * @param topicService The service handling topic business logic.
     */
    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    /**
     * Retrieves all available forum topics.
     *
     * @return A list of all topics.
     */
    @GetMapping
    public List<Topic> getAllTopics() {
        return topicService.getAllTopics();
    }

    /**
     * Creates a new topic for the authenticated user.
     *
     * @param topic     The topic payload (title, content).
     * @param principal The security principal identifying the logged-in user.
     * @return The created topic or 401 Unauthorized if the user is not logged in.
     */
    @PostMapping
    public ResponseEntity<Topic> createTopic(@RequestBody Topic topic, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build(); // User not logged in
        }

        // principal.getName() gets the username from the token
        Topic created = topicService.createTopic(topic, principal.getName());
        return ResponseEntity.ok(created);
    }

    /**
     * Retrieves a specific topic by its unique ID.
     *
     * @param id The ID of the topic to retrieve.
     * @return The requested topic.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Topic> getTopicById(@PathVariable Long id) {
        Topic topic = topicService.getTopicById(id);
        return ResponseEntity.ok(topic);
    }

    /**
     * Returns the total number of likes for a topic.
     */
    @GetMapping("/{id}/likes/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable Long id) {
        return ResponseEntity.ok(topicService.getLikeCount(id));
    }

    /**
     * Checks whether the authenticated user has liked the topic.
     */
    @GetMapping("/{id}/likes/me")
    public ResponseEntity<Boolean> hasLiked(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(false); // instead of 401
        }
        return ResponseEntity.ok(topicService.hasUserLiked(id, principal.getName()));
    }


    /**
     * Likes a topic for the authenticated user.
     */
    @PostMapping("/{id}/likes")
    public ResponseEntity<Void> likeTopic(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        topicService.likeTopic(id, principal.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * Unlikes a topic for the authenticated user.
     */
    @DeleteMapping("/{id}/likes")
    public ResponseEntity<Void> unlikeTopic(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        topicService.unlikeTopic(id, principal.getName());
        return ResponseEntity.ok().build();
    }


    /**
     * Endpoint om een eigen topic te verwijderen.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTopic(@PathVariable Long id, Principal principal) {
        // Check of gebruiker is ingelogd
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            // We geven de naam van de ingelogde gebruiker mee aan de service
            topicService.deleteTopic(id, principal.getName());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            // Als het niet lukt (bv. niet de eigenaar), sturen we een foutmelding terug
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}

