package com.election.backendjava.service;

import com.election.backendjava.dto.CommentRequestDTO;
import com.election.backendjava.dto.CommentResponseDTO;
import com.election.backendjava.model.Comment;
import com.election.backendjava.model.Topic;
import com.election.backendjava.model.User;
import com.election.backendjava.repository.CommentRepository;
import com.election.backendjava.repository.TopicRepository;
import com.election.backendjava.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handling business logic related to comments.
 */
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, TopicRepository topicRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new comment for a topic.
     * Sanitizes input to prevent XSS.
     *
     * @param request The comment data.
     * @param userEmail The email of the currently logged-in user.
     * @return The created comment as a DTO.
     */
    @Transactional
    public CommentResponseDTO addComment(CommentRequestDTO request, String userEmail, Long parentId) {
        User author = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Topic topic = topicRepository.findById(request.topicId())
                .orElseThrow(() -> new EntityNotFoundException("Topic not found"));

        String sanitizedContent = HtmlUtils.htmlEscape(request.content());

        Comment comment = new Comment(sanitizedContent, author, topic);

        if (request.parentId() != null) {
            Comment parent = commentRepository.findById(request.parentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent comment not found"));
            comment.setParentComment(parent);

            if (request.replyToUsername() != null) {
                comment.setReplyToUsername(request.replyToUsername());
            }
        }

            Comment savedComment = commentRepository.save(comment);
            return mapToDTO(savedComment);

    }

    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getCommentsByUserId(Long userId) {
        return commentRepository.findByAuthor_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }


    /**
     * Retrieves all comments for a specific topic.
     *
     * @param topicId The ID of the topic.
     * @return List of comment DTOs.
     */
    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getCommentsByTopic(Long topicId) {
        return commentRepository.findByTopicIdOrderByCreatedAtAsc(topicId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment niet gevonden"));

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        User author = comment.getAuthor();

        if (author == null || !author.getEmail().equals(currentEmail)) {
            throw new RuntimeException("Je mag alleen je eigen comments verwijderen!");
        }

        commentRepository.delete(comment);
    }

    public CommentResponseDTO updateComment(Long id, CommentRequestDTO dto) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment niet gevonden"));

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = comment.getAuthor();

        if (author == null || !author.getEmail().equals(currentEmail)) {
            throw new RuntimeException("Je mag alleen je eigen comments bewerken!");
        }

        comment.setContent(dto.content());
        Comment savedComment = commentRepository.save(comment);
        return mapToDTO(savedComment);
    }

    private CommentResponseDTO mapToDTO(Comment comment) {
        Long parentId = (comment.getParentComment() != null) ? comment.getParentComment().getId() : null;
        String authorName = comment.getAuthor().getUsername() != null
                ? comment.getAuthor().getUsername()
                : comment.getAuthor().getEmail();

        return new CommentResponseDTO(
                comment.getId(),
                comment.getContent(),
                authorName,
                comment.getCreatedAt(),
                parentId,
                comment.getReplyToUsername(),
                comment.getTopic().getId()
        );
    }

    @Transactional(readOnly = true)
    public CommentResponseDTO getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        return mapToDTO(comment);
    }


}