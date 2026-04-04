package com.election.backendjava.api;

import com.election.backendjava.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class EmailChangeController {

    @Autowired
    private UserService userService;

    // Link uit de mail roept dit aan
    @GetMapping("/confirm-email-change")
    public ResponseEntity<?> confirmEmailChange(@RequestParam("token") String token) {
        userService.confirmEmailChange(token);
        return ResponseEntity.ok().body("E-mailadres succesvol bevestigd.");
    }
}
