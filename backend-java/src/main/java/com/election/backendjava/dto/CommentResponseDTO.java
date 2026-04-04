package com.election.backendjava.dto;

import java.time.LocalDateTime;

/**
 * DTO for sending comment details back to the client.
 */
public record CommentResponseDTO(
        Long id,
        String content,
        String authorName,
        LocalDateTime createdAt,
        Long parentId,
        String replyToUsername,
        Long topicId
) {}