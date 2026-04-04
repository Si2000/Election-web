package com.election.backendjava.api;

import com.election.backendjava.dto.CommentRequestDTO;
import com.election.backendjava.dto.CommentResponseDTO;
import com.election.backendjava.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * REST Controller for managing comments on discussion topics.
 */
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Adds a new comment to a specific topic.
     * Requires the user to be authenticated.
     *
     * @param request   The comment data (content and topic ID).
     * @param principal The currently authenticated user (injected by Spring Security).
     * @return The created comment.
     */
    @PostMapping
    public ResponseEntity<CommentResponseDTO> addComment(
            @Valid @RequestBody CommentRequestDTO request,
            Principal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CommentResponseDTO response = commentService.addComment(request, principal.getName(), request.parentId());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all comments for a specific topic.
     * Publicly accessible.
     *
     * @param topicId The ID of the topic.
     * @return List of comments ordered chronologically.
     */
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<List<CommentResponseDTO>> getComments(@PathVariable Long topicId) {
        return ResponseEntity.ok(commentService.getCommentsByTopic(topicId));
    }

    /**
     * Retrieves a single comment by its ID.
     * Publicly accessible.
     *
     * @param commentId The ID of the comment.
     * @return The comment data.
     */
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> getCommentById(@PathVariable Long commentId) {
        CommentResponseDTO comment = commentService.getCommentById(commentId);
        return ResponseEntity.ok(comment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequestDTO commentDTO
    ) {
        CommentResponseDTO updatedComment = commentService.updateComment(id, commentDTO);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}