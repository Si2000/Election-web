package com.election.backendjava.service;

import com.election.backendjava.dto.UserReportAdminDTO;
import com.election.backendjava.model.User;
import com.election.backendjava.model.UserReport;
import com.election.backendjava.repository.UserReportRepository;
import com.election.backendjava.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserReportService {

    private final UserReportRepository userReportRepository;
    private final UserRepository userRepository;

    public UserReportService(UserReportRepository userReportRepository, UserRepository userRepository) {
        this.userReportRepository = userReportRepository;
        this.userRepository = userRepository;
    }

    // CREATE: gebruiker rapporteren
    public void reportUser(String reporterEmail, Long reportedUserId, String reason) {
        User reporter = userRepository.findByEmail(reporterEmail)
                .orElseThrow(() -> new RuntimeException("Reporter niet gevonden"));

        User reportedUser = userRepository.findById(reportedUserId)
                .orElseThrow(() -> new RuntimeException("Gerapporteerde gebruiker niet gevonden"));

        userReportRepository.save(new UserReport(reason, reporter, reportedUser));
    }

    // READ: alle gerapporteerde gebruikers voor admin
    @Transactional
    public List<UserReportAdminDTO> getReportedUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    List<UserReport> reports = userReportRepository.findByReportedUserId(user.getId());
                    if (reports.isEmpty()) return null;

                    return new UserReportAdminDTO(
                            user.getId(),
                            user.getUsername(),
                            reports.size(),
                            reports.stream().map(UserReport::getReason).toList()
                    );
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
