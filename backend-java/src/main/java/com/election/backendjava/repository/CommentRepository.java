package com.election.backendjava.repository;

import com.election.backendjava.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByTopicIdOrderByCreatedAtAsc(Long topicId);

    List<Comment> findByAuthor_IdOrderByCreatedAtDesc(Long userId);
}