package com.election.backendjava.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_reports")
public class UserReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reason;
    private LocalDateTime reportedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user_id")
    private User reportedUser;

    public UserReport() {}

    public UserReport(String reason, User reporter, User reportedUser) {
        this.reason = reason;
        this.reporter = reporter;
        this.reportedUser = reportedUser;
        this.reportedAt = LocalDateTime.now();
    }

    // Getters & Setters
    public Long getId() { return id; }
    public String getReason() { return reason; }
    public LocalDateTime getReportedAt() { return reportedAt; }
    public User getReporter() { return reporter; }
    public User getReportedUser() { return reportedUser; }
}
