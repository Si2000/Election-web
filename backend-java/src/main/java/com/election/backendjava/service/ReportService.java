package com.election.backendjava.service;

import com.election.backendjava.dto.ReportAdminDTO;
import com.election.backendjava.model.Comment;
import com.election.backendjava.model.Report;
import com.election.backendjava.model.Topic;
import com.election.backendjava.model.User;
import com.election.backendjava.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final TopicRepository topicRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TopicLikeRepository topicLikeRepository;

    public ReportService(
            ReportRepository reportRepository,
            TopicRepository topicRepository,
            CommentRepository commentRepository,
            UserRepository userRepository,
            TopicLikeRepository topicLikeRepository
    ) {
        this.reportRepository = reportRepository;
        this.topicRepository = topicRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.topicLikeRepository = topicLikeRepository;
    }

    /* ======================
       REPORT AANMAKEN
       ====================== */

    public void reportTopic(Long topicId, String email, String reason) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic niet gevonden"));

        User reporter = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Gebruiker niet gevonden"));

        reportRepository.save(new Report(reason, reporter, topic));
    }

    public void reportComment(Long commentId, String email, String reason) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment niet gevonden"));

        User reporter = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Gebruiker niet gevonden"));

        reportRepository.save(new Report(reason, reporter, comment));
    }

    /* ======================
       ADMIN – REPORTS
       ====================== */

    @Transactional
    public List<ReportAdminDTO> getAllReportDTOs() {
        return reportRepository.findAll().stream().map(report -> {
            if (report.getTopic() != null) {
                return new ReportAdminDTO(
                        report.getId(),
                        report.getReason(),
                        report.getReportedAt(),
                        report.getReporter().getUsername(),
                        "TOPIC",
                        report.getTopic().getId(),
                        report.getTopic().getAuthor().getUsername()
                );
            } else {
                return new ReportAdminDTO(
                        report.getId(),
                        report.getReason(),
                        report.getReportedAt(),
                        report.getReporter().getUsername(),
                        "COMMENT",
                        report.getComment().getId(),
                        report.getComment().getAuthor().getUsername()
                );
            }
        }).toList();
    }

    /* ======================
       ADMIN – ACCEPT / REJECT
       ====================== */

    @Transactional
    public boolean deleteReport(Long reportId, boolean deleteTarget) {
        Report report = reportRepository.findById(reportId).orElse(null);
        if (report == null) return false;

        Topic topic = report.getTopic();
        Comment comment = report.getComment();

        if (deleteTarget) {

            if (topic != null) {
                reportRepository.deleteAllByCommentsOfTopic(topic.getId());
                reportRepository.deleteAllByTopicId(topic.getId());
                topicLikeRepository.deleteByTopicId(topic.getId());
                topicRepository.delete(topic);
            }

            if (comment != null) {
                reportRepository.deleteAllByCommentId(comment.getId());

                commentRepository.delete(comment);
            }

        } else {
            reportRepository.delete(report);
        }

        return true;
    }
}
