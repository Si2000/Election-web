package com.election.backendjava.dto;

import java.time.LocalDateTime;

public record ReportAdminDTO(
        Long id,
        String reason,
        LocalDateTime reportedAt,
        String reporterUsername,
        String type,
        Long targetId,
        String targetAuthor
) {}
