package com.election.backendjava.api;

import com.election.backendjava.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/topics/{id}")
    public ResponseEntity<?> reportTopic(@PathVariable Long id, @RequestBody Map<String, String> payload, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        String reason = payload.get("reason");
        reportService.reportTopic(id, principal.getName(), reason);

        return ResponseEntity.ok(Map.of("message", "Topic gerapporteerd. Bedankt voor je melding."));
    }

    @PostMapping("/comments/{id}")
    public ResponseEntity<?> reportComment(@PathVariable Long id,
                                           @RequestBody Map<String, String> payload,
                                           Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        String reason = payload.get("reason");
        reportService.reportComment(id, principal.getName(), reason);

        return ResponseEntity.ok(Map.of("message", "Comment gerapporteerd. Bedankt voor je melding."));
    }

}