    package com.election.backendjava.api;

    import com.election.backendjava.model.User;
    import com.election.backendjava.service.UserService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.web.bind.annotation.*;

    import java.nio.charset.StandardCharsets;
    import java.util.Base64;
    import java.util.Map;

    @RestController
    @RequestMapping("/api/user")
    public class UserController {

        @Autowired
        private UserService userService;

        /**
         * Helper: email van ingelogde user ophalen uit SecurityContext
         */
        private String getEmailFromAuth() {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return auth.getName();
        }

        /**
         * Encrypt email naar Base64 (frontend gebruikt dit)
         */
        public static String encryptEmail(String email) {
            return Base64.getEncoder().encodeToString(email.getBytes(StandardCharsets.UTF_8));
        }

        /**
         * Decrypt emailHash naar originele email
         */
        public static String decryptEmail(String emailHash) {
            try {
                return new String(Base64.getDecoder().decode(emailHash), StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new IllegalArgumentException("Ongeldig email hash");
            }
        }

        @GetMapping("/profile")
        public ResponseEntity<?> getProfile() {
            try {
                String email = getEmailFromAuth();
                User user = userService.getUserByEmail(email);

                return ResponseEntity.ok(Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "email", user.getEmail()
                ));
            } catch (Exception e) {
                return ResponseEntity.status(401).body("Niet geautoriseerd");
            }
        }

        @PutMapping("/profile")
        public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> payload) {
            try {
                String currentEmail = getEmailFromAuth();
                User updatedUser = userService.updateProfile(
                        currentEmail,
                        payload.get("username"),
                        payload.get("email")
                );

                return ResponseEntity.ok(Map.of(
                        "message", "Profiel bijgewerkt",
                        "username", updatedUser.getUsername(),
                        "email", updatedUser.getEmail()
                ));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Er ging iets mis: " + e.getMessage());
            }
        }

        @PostMapping("/change-password")
        public ResponseEntity<?> changePassword(@RequestBody Map<String, String> payload) {
            try {
                String email = getEmailFromAuth();
                userService.changePassword(email, payload.get("oldPassword"), payload.get("newPassword"));
                return ResponseEntity.ok(Map.of("message", "Wachtwoord gewijzigd."));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Fout bij wijzigen wachtwoord.");
            }
        }

        @DeleteMapping("/delete")
        public ResponseEntity<?> deleteAccount() {
            try {
                userService.deleteAccount(getEmailFromAuth());
                return ResponseEntity.ok(Map.of("message", "Account verwijderd."));
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Kon account niet verwijderen.");
            }
        }

        @PutMapping("/persona")
        public ResponseEntity<?> updatePersona(@RequestBody Map<String, String> payload) {
            try {
                String email = getEmailFromAuth();
                String persona = payload.get("persona");

                User updated = userService.updatePersona(email, persona);

                return ResponseEntity.ok(Map.of(
                        "message", "Persona bijgewerkt",
                        "persona", updated.getPersona()
                ));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Er ging iets mis.");
            }
        }

        @GetMapping("/persona")
        public ResponseEntity<?> getMyPersona() {
            try {
                String email = getEmailFromAuth();
                User user = userService.getUserByEmail(email);

                return ResponseEntity.ok(Map.of(
                        "persona", user.getPersona()
                ));
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Kon persona niet ophalen.");
            }
        }


        @GetMapping("/profile/by-username/{username}")
        public ResponseEntity<?> getPublicProfileByUsername(@PathVariable String username) {
            try {
                User user = userService.getUserByUsername(username); // nieuwe service methode

                if (user == null) {
                    return ResponseEntity.status(404).body("Gebruiker niet gevonden");
                }

                // Alleen publieke info teruggeven
                return ResponseEntity.ok(Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "persona", user.getPersona()
                ));
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Er ging iets mis");
            }
        }
    }
