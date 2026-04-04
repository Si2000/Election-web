package com.election.backendjava.repository;

import com.election.backendjava.model.Report;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Report r WHERE r.topic.id = :topicId")
    void deleteAllByTopicId(@Param("topicId") Long topicId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Report r WHERE r.comment.id = :commentId")
    void deleteAllByCommentId(@Param("commentId") Long commentId);

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM Report r
        WHERE r.comment.id IN (
            SELECT c.id FROM Comment c WHERE c.topic.id = :topicId
        )
    """)
    void deleteAllByCommentsOfTopic(@Param("topicId") Long topicId);
}
