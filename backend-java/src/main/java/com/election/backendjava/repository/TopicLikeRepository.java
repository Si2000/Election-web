package com.election.backendjava.repository;

import com.election.backendjava.model.TopicLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Repository for managing TopicLike entities.
 */
@Repository
public interface TopicLikeRepository extends JpaRepository<TopicLike, Long> {

    /**
     * Counts how many likes a topic has.
     *
     * @param topicId The topic ID.
     * @return Total like count.
     */
    long countByTopicId(Long topicId);

    /**
     * Checks if a user has liked a topic.
     *
     * @param topicId The topic ID.
     * @param userId The user ID.
     * @return True if liked, otherwise false.
     */
    boolean existsByTopicIdAndUserId(Long topicId, Long userId);

    /**
     * Finds a like record by topic and user.
     *
     * @param topicId The topic ID.
     * @param userId  The user ID.
     * @return Optional TopicLike.
     */
    Optional<TopicLike> findByTopicIdAndUserId(Long topicId, Long userId);

    /**
     * Deletes a like by topic and user.
     *
     * @param topicId The topic ID.
     * @param userId  The user ID.
     */
    @Transactional
    @Modifying
    void deleteByTopicIdAndUserId(Long topicId, Long userId);

    @Transactional
    @Modifying
    void deleteByTopicId(Long topicId);
}
