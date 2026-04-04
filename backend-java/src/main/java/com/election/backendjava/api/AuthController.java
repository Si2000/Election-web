package com.election.backendjava.api;

import com.election.backendjava.model.User;
import com.election.backendjava.repository.UserRepository;
import com.election.backendjava.service.EmailService;
import com.election.backendjava.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService,
                          EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String username = payload.get("username");
        String password = payload.get("password");

        if (email == null || username == null || password == null) {
            return ResponseEntity.badRequest().body("Vul alle velden in.");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body("Dit e-mailadres is al in gebruik.");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body("Deze gebruikersnaam is al bezet.");
        }

        String hashedPassword = passwordEncoder.encode(password);
        User newUser = new User(email, username, hashedPassword);

        // NIEUW: Account inactief maken en token genereren
        newUser.setEnabled(false);
        String token = UUID.randomUUID().toString();
        newUser.setVerificationToken(token);

        userRepository.save(newUser);

        // Mail versturen met token
        emailService.sendWelcomeEmail(email, username, token);

        return ResponseEntity.ok(Map.of("message", "Registratie gelukt! Check je e-mail om te activeren."));
    }

    // NIEUW ENDPOINT
    @PostMapping("/verify")
    public ResponseEntity<?> verifyAccount(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");

        Optional<User> userOptional = userRepository.findByVerificationToken(token);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Ongeldige of verlopen link.");
        }

        User user = userOptional.get();
        user.setEnabled(true); // Activeer account
        user.setVerificationToken(null); // Verwijder token
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Account geactiveerd! Je kunt nu inloggen."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Ongeldige inloggegevens"));
        }

        User user = userOptional.get();

        // NIEUW: Check of account actief is
        if (!user.isEnabled()) {
            return ResponseEntity.status(403).body(Map.of("error", "Je account is nog niet geactiveerd. Check je e-mail."));
        }
        if (user.isBaned()) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "ACCOUNT_BANNED"));
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Ongeldige inloggegevens"));
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "username", user.getUsername(),
                "email", user.getEmail(),
                "role", user.getRole()
        ));
    }
    // 1. Wachtwoord Reset Aanvragen
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");

        Optional<User> userOptional = userRepository.findByEmail(email);

        // We geven GEEN foutmelding als de email niet bestaat (security practice: "User Enumeration" voorkomen)
        // Maar we sturen alleen een mail als hij wel bestaat.
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String token = UUID.randomUUID().toString();
            user.setResetPasswordToken(token);
            userRepository.save(user);

            emailService.sendPasswordResetEmail(email, token);
        }

        return ResponseEntity.ok(Map.of("message", "Als dit e-mailadres bekend is, hebben we een herstellink gestuurd."));
    }

    // 2. Nieuw Wachtwoord Instellen
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String newPassword = payload.get("newPassword");

        Optional<User> userOptional = userRepository.findByResetPasswordToken(token);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Ongeldige of verlopen link.");
        }

        User user = userOptional.get();

        // Update wachtwoord
        user.setPassword(passwordEncoder.encode(newPassword));

        // Verwijder token zodat de link niet nog eens gebruikt kan worden
        user.setResetPasswordToken(null);

        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Wachtwoord succesvol gewijzigd! Je kunt nu inloggen."));
    }
}