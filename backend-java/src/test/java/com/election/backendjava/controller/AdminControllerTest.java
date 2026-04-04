//package com.election.backendjava.controller;
//
//import com.election.backendjava.api.AdminController;
//import com.election.backendjava.model.User;
//import com.election.backendjava.model.Report;
//import com.election.backendjava.service.ReportService;
//import com.election.backendjava.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.ResponseEntity;
//
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class AdminControllerTest {
//
//    private UserService userService;
//    private ReportService reportService;
//    private AdminController adminController;
//
//    @BeforeEach
//    void setup() {
//        userService = mock(UserService.class);
//        reportService = mock(ReportService.class);
//        adminController = new AdminController(userService, reportService);
//    }
//
//    // All tests with a happy flow
//
//    @Test
//    void testGetAllUsers_HappyFlow() {
//        User u = new User();
//        u.setEmail("test@mail.com");
//
//        when(userService.getAllUsers()).thenReturn(List.of(u));
//
//        ResponseEntity<?> response = adminController.getAllUsers();
//
//        assertEquals(200, response.getStatusCode().value());
//        assertEquals(1, ((List<?>) response.getBody()).size());
//    }
//
//    @Test
//    void testGetBannedUsers_HappyFlow() {
//        User u = new User();
//        u.setEmail("ban@mail.com");
//
//        when(userService.getAllBanedUsers()).thenReturn(List.of(u));
//
//        ResponseEntity<?> response = adminController.getBannedUsers();
//
//        assertEquals(200, response.getStatusCode().value());
//        Map<?, ?> body = (Map<?, ?>) response.getBody();
//
//        assertTrue(body.containsKey("users"));
//        assertEquals(1, ((List<?>) body.get("users")).size());
//    }
//
//    @Test
//    void testGetAllReports_HappyFlow() {
//        Report r = new Report();
//
//        when(reportService.getAllReports()).thenReturn(List.of(r));
//
//        ResponseEntity<?> response = adminController.getAllReports();
//
//        assertEquals(200, response.getStatusCode().value());
//        assertEquals(1, ((List<?>) response.getBody()).size());
//    }
//
//    @Test
//    void testUpdateUserRole_HappyFlow() {
//        ResponseEntity<?> response = adminController.updateUserRole("test@mail.com", "ROLE_ADMIN");
//
//        verify(userService).updateUserRole("test@mail.com", "ROLE_ADMIN");
//        assertEquals(200, response.getStatusCode().value());
//        assertEquals("Role updated", response.getBody());
//    }
//
//    @Test
//    void testUpdateBanState_HappyFlow() {
//        ResponseEntity<?> response = adminController.updateBanState("test@mail.com", true);
//
//        verify(userService).setUserstate("test@mail.com", true);
//        assertEquals(200, response.getStatusCode().value());
//        assertEquals("User banned.", response.getBody());
//    }
//
//    @Test
//    void testDeleteReport_HappyFlow() {
//        when(reportService.deleteReport(1L)).thenReturn(true);
//
//        ResponseEntity<?> response = adminController.deleteReport(1L);
//
//        assertEquals(200, response.getStatusCode().value());
//        assertEquals("Report deleted.", response.getBody());
//    }
//
//    //   All tests with an unhappy flow
//
//    @Test
//    void testDeleteReport_NotFound() {
//        when(reportService.deleteReport(1L)).thenReturn(false);
//
//        ResponseEntity<?> response = adminController.deleteReport(1L);
//
//        assertEquals(404, response.getStatusCode().value());
//        assertEquals("Report not found.", response.getBody());
//    }
//
//    @Test
//    void testUpdateUserRole_ServiceThrows() {
//        doThrow(new IllegalArgumentException("Invalid role"))
//                .when(userService)
//                .updateUserRole(anyString(), anyString());
//
//        Exception ex = assertThrows(Exception.class, () ->
//                adminController.updateUserRole("test@mail.com", "BAD_ROLE")
//        );
//
//        assertEquals("Invalid role", ex.getMessage());
//    }
//
//    @Test
//    void testUpdateBanState_ServiceThrows() {
//        doThrow(new RuntimeException("DB error"))
//                .when(userService).setUserstate(anyString(), anyBoolean());
//
//        assertThrows(RuntimeException.class, () ->
//                adminController.updateBanState("email@mail.com", true)
//        );
//    }
//
//    @Test
//    void testGetAllUsers_Failure() {
//        when(userService.getAllUsers()).thenThrow(new RuntimeException("DB error"));
//
//        assertThrows(RuntimeException.class, () -> adminController.getAllUsers());
//    }
//
//    @Test
//    void testGetAllReports_Failure() {
//        when(reportService.getAllReports()).thenThrow(new RuntimeException("DB error"));
//
//        assertThrows(RuntimeException.class, () -> adminController.getAllReports());
//    }
//}
