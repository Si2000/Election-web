package com.election.backendjava.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a forum discussion topic.
 * <p>
 * Stores the title, content, creation timestamp, and the author of the discussion.
 */
@Entity
@Table(name = "topics")
public class Topic {

    /** Unique ID of the topic. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The subject or title of the discussion. */
    private String title;

    /** The main body text of the discussion. */
    @Column(columnDefinition = "TEXT")
    private String content;

    /** The date and time when the topic was created. */
    private LocalDateTime createdAt;

    // --- COMMENTS: Worden verwijderd als topic verwijderd wordt ---
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Comment> comments = new ArrayList<>();

    // --- NIEUW: LIKES: Worden NU OOK verwijderd als topic verwijderd wordt ---
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TopicLike> likes = new ArrayList<>();

    /**
     * The user who created this topic.
     * <p>
     * JsonIgnoreProperties prevents infinite recursion and hides sensitive user data
     * (like passwords) when serializing the topic to JSON.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({
            "password",
            "roles",
            "authorities",
            "topics",
            "enabled",
            "accountNonExpired",
            "accountNonLocked",
            "credentialsNonExpired"
    })
    private User author;

    /**
     * Number of likes for this topic.
     * This field is calculated in the service and not stored in the database.
     */
    @Transient
    private int likeCount;

    /** Default constructor for JPA. */
    public Topic() {
    }

    /**
     * Creates a new topic with the specified details.
     *
     * @param title   The title of the topic.
     * @param content The content/body of the topic.
     * @param author  The user creating the topic.
     */
    public Topic(String title, String content, User author) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters

    /** @return The unique ID. */
    public Long getId() { return id; }
    /** @param id The unique ID to set. */
    public void setId(Long id) { this.id = id; }

    /** @return The topic title. */
    public String getTitle() { return title; }
    /** @param title The title to set. */
    public void setTitle(String title) { this.title = title; }

    /** @return The topic content. */
    public String getContent() { return content; }
    /** @param content The content to set. */
    public void setContent(String content) { this.content = content; }

    /** @return The creation timestamp. */
    public LocalDateTime getCreatedAt() { return createdAt; }
    /** @param createdAt The timestamp to set. */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /** @return The author of the topic. */
    public User getAuthor() { return author; }
    /** @param author The author to set. */
    public void setAuthor(User author) { this.author = author; }

    /**
     * Helper method for frontend display.
     *
     * @return The author's username, or "Onbekend" if author is null.
     */
    public String getAuthorName() {
        return author != null ? author.getUsername() : "Onbekend";
    }


    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setTopic(this);
    }

    // Nieuwe getter/setter voor likes
    public List<TopicLike> getLikes() {
        return likes;
    }

    public void setLikes(List<TopicLike> likes) {
        this.likes = likes;
    }
}