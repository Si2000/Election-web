//package com.election.backendjava.service;
//
//import com.election.backendjava.model.Report;
//import com.election.backendjava.model.Topic;
//import com.election.backendjava.model.User;
//import com.election.backendjava.repository.ReportRepository;
//import com.election.backendjava.repository.TopicRepository;
//import com.election.backendjava.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class ReportServiceTest {
//
//    private ReportRepository reportRepository;
//    private TopicRepository topicRepository;
//    private UserRepository userRepository;
//
//    private ReportService reportService;
//
//    @BeforeEach
//    void setup() {
//        reportRepository = mock(ReportRepository.class);
//        topicRepository = mock(TopicRepository.class);
//        userRepository = mock(UserRepository.class);
//
//        reportService = new ReportService(reportRepository, topicRepository, userRepository);
//    }
//
//    @Test
//    void testReportTopic_HappyFlow() {
//        Topic topic = new Topic();
//        User user = new User();
//        user.setEmail("test@mail.com");
//
//        when(topicRepository.findById(1L)).thenReturn(Optional.of(topic));
//        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
//
//        reportService.reportTopic(1L, "test@mail.com", "spam");
//
//        verify(reportRepository, times(1)).save(any(Report.class));
//    }
//
//    @Test
//    void testReportTopic_TopicNotFound() {
//        when(topicRepository.findById(1L)).thenReturn(Optional.empty());
//
//        RuntimeException ex = assertThrows(RuntimeException.class, () ->
//                reportService.reportTopic(1L, "test@mail.com", "bad content")
//        );
//
//        assertEquals("Topic niet gevonden", ex.getMessage());
//    }
//
//    @Test
//    void testReportTopic_UserNotFound() {
//        Topic topic = new Topic();
//
//        when(topicRepository.findById(1L)).thenReturn(Optional.of(topic));
//        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.empty());
//
//        RuntimeException ex = assertThrows(RuntimeException.class, () ->
//                reportService.reportTopic(1L, "test@mail.com", "bad content")
//        );
//
//        assertEquals("Gebruiker niet gevonden", ex.getMessage());
//    }
//
//    @Test
//    void testGetAllReports() {
//        Report r = new Report();
//
//        when(reportRepository.findAll()).thenReturn(List.of(r));
//
//        List<Report> result = reportService.getAllReports();
//
//        assertEquals(1, result.size());
//    }
//
//    @Test
//    void testDeleteReport_HappyFlow() {
//        when(reportRepository.existsById(3L)).thenReturn(true);
//
//        boolean deleted = reportService.deleteReport(3L);
//
//        assertTrue(deleted);
//        verify(reportRepository).deleteById(3L);
//    }
//
//    @Test
//    void testDeleteReport_NotFound() {
//        when(reportRepository.existsById(5L)).thenReturn(false);
//
//        boolean deleted = reportService.deleteReport(5L);
//
//        assertFalse(deleted);
//        verify(reportRepository, never()).deleteById(any());
//    }
//}
