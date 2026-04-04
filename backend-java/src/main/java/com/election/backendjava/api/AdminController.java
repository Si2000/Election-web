package com.election.backendjava.api;

import com.election.backendjava.dto.ReportAdminDTO;
import com.election.backendjava.model.User;
import com.election.backendjava.service.ReportService;
import com.election.backendjava.service.UserReportService;
import com.election.backendjava.service.UserService;
import com.election.backendjava.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final ReportService reportService;
    private final UserReportService userReportService;
    private final JwtService jwtService;

    public AdminController(UserService userService,
                           ReportService reportService,
                           UserReportService userReportService,
                           JwtService jwtService) {
        this.userService = userService;
        this.reportService = reportService;
        this.userReportService = userReportService;
        this.jwtService = jwtService;
    }

    @PatchMapping("/users/{email}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable String email,
            @RequestParam String role) {

        userService.updateUserRole(email, role);
        return ResponseEntity.ok("Role updated");
    }

    @GetMapping("/banned-users")
    public ResponseEntity<?> getBannedUsers() {
        List<User> users = userService.getAllBanedUsers();
        return ResponseEntity.ok(Map.of("users", users));
    }

    @PatchMapping("/users/{email}/ban")
    public ResponseEntity<?> updateBanState(
            @PathVariable String email,
            @RequestParam boolean state) {

        userService.setUserstate(email, state);
        return ResponseEntity.ok(state ? "User banned." : "User unbanned.");
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/reports")
    public List<ReportAdminDTO> getReports() {
        return reportService.getAllReportDTOs();
    }

    @DeleteMapping("/reports/{id}")
    public ResponseEntity<?> deleteReport(
            @PathVariable Long id,
            @RequestParam boolean deleteTopic
    ) {
        boolean deleted = reportService.deleteReport(id, deleteTopic);

        if (!deleted) {
            return ResponseEntity.status(404).body("Report not found.");
        }

        return ResponseEntity.ok("Report deleted.");
    }

    @GetMapping("/reported-users")
    public ResponseEntity<?> getReportedUsers() {
        return ResponseEntity.ok(userReportService.getReportedUsers());
    }

    @PostMapping("/reported-users")
    public ResponseEntity<?> reportUser(
            @RequestBody Map<String, String> body,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String reporterEmail = getEmailFromToken(authHeader);

            Long reportedUserId = Long.valueOf(body.get("reportedUserId"));
            String reason = body.get("reason");

            userReportService.reportUser(reporterEmail, reportedUserId, reason);

            return ResponseEntity.ok(Map.of("message", "Gebruiker gerapporteerd"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    private String getEmailFromToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtService.getEmailFromToken(token);
    }
}
