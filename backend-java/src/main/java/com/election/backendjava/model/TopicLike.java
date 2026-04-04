package com.election.backendjava.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entity representing a like on a forum topic.
 * <p>
 * Each user can like a specific topic only once.
 */
@Entity
@Table(
        name = "topic_likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"topic_id", "user_id"})
)
public class TopicLike {

    /** Unique identifier of the like. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The topic that is liked.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    @JsonIgnore
    private Topic topic;

    /**
     * The user who liked the topic.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    /**
     * Timestamp indicating when the like was created.
     */
    private LocalDateTime createdAt;

    /**
     * Default constructor required by JPA.
     */
    public TopicLike() {
    }

    /**
     * Creates a new TopicLike.
     *
     * @param topic The liked topic.
     * @param user  The user who liked the topic.
     */
    public TopicLike(Topic topic, User user) {
        this.topic = topic;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }


    public Long getId() {
        return id;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
