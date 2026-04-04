package com.election.backendjava.repository;

import com.election.backendjava.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Topic entities.
 * <p>
 * Provides standard CRUD operations via JpaRepository and custom queries
 * for retrieving forum topics.
 */

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    List<Topic> findAllByOrderByCreatedAtDesc();

    List<Topic> findByAuthor_IdOrderByCreatedAtDesc(Long userId);
}

