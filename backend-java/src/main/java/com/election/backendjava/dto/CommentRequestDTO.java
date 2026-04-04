package com.election.backendjava.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

/**
 * DTO for receiving a new comment payload.
 */
public record CommentRequestDTO(
        @NotNull(message = "Topic ID is required")
        Long topicId,

        @NotBlank(message = "Content cannot be empty")
        @Size(min = 1, message = "Comment must contain at least 1 character")
        String content,
        String replyToUsername,
        Long parentId
) {}