package com.election.backendjava.repository;

import com.election.backendjava.model.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserReportRepository extends JpaRepository<UserReport, Long> {

    List<UserReport> findByReportedUserId(Long userId);

    long countByReportedUserId(Long userId);
}
