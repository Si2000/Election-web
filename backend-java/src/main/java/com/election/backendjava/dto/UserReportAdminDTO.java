package com.election.backendjava.dto;

import java.util.List;

public record UserReportAdminDTO(
        Long userId,
        String username,
        long reportCount,
        List<String> reasons
) {}

