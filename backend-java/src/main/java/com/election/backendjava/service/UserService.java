package com.election.backendjava.service;

import com.election.backendjava.exception.NoBannedUsersFoundException;
import com.election.backendjava.exception.ResourceNotFoundException;
import com.election.backendjava.model.User;
import com.election.backendjava.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    // 1. Gebruiker ophalen
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Gebruiker niet gevonden: " + email));
    }

    // 2. Profiel wijzigen (USERNAME direct, EMAIL via bevestiging)
    public User updateProfile(String currentEmail, String newUsername, String newEmail) {
        User user = getUserByEmail(currentEmail);

        // Gebruikersnaam check
        if (!user.getUsername().equals(newUsername)
                && userRepository.findByUsername(newUsername).isPresent()) {
            throw new IllegalArgumentException("Gebruikersnaam is al in gebruik.");
        }

        user.setUsername(newUsername);

        // E-mail wijziging alleen via bevestiging
        if (!user.getEmail().equals(newEmail)) {

            if (userRepository.findByEmail(newEmail).isPresent()) {
                throw new IllegalArgumentException("E-mailadres is al in gebruik.");
            }

            String token = UUID.randomUUID().toString();
            user.setPendingEmail(newEmail);
            user.setEmailChangeToken(token);

            // mail naar OUDE e-mail (MET LINK)
            emailService.sendEmailChangeConfirmation(user.getEmail(), user.getUsername(), token);

            // mail naar NIEUWE e-mail (ZONDER LINK)
            emailService.sendEmailChangeWarning(newEmail, user.getUsername());
        }

        return userRepository.save(user);

    }

    // Bevestiging e-mail wijziging
    public void confirmEmailChange(String token) {
        User user = userRepository.findByEmailChangeToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Ongeldig of verlopen token"));

        user.setEmail(user.getPendingEmail());
        user.setPendingEmail(null);
        user.setEmailChangeToken(null);

        userRepository.save(user);
    }

    // 3. Wachtwoord wijzigen
    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = getUserByEmail(email);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Het oude wachtwoord is onjuist.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // 4. Account verwijderen
    public void deleteAccount(String email) {
        User user = getUserByEmail(email);
        userRepository.delete(user);
    }

    public User updatePersona(String email, String persona) {
        User user = getUserByEmail(email);

        if (persona != null &&
                !persona.equals("nico") &&
                !persona.equals("yasser")) {
            throw new IllegalArgumentException("Ongeldige persona waarde.");
        }

        user.setPersona(persona);
        return userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Gebruiker niet gevonden: " + username));
    }

    public List<User> getAllBanedUsers() {
        List<User> bannedUsers = userRepository.findByBanedTrue();

        if (bannedUsers.isEmpty()) {
            throw new NoBannedUsersFoundException("Er zijn geen gebande gebruikers gevonden.");
        }

        return bannedUsers;
    }

    public void setUserstate(String email, boolean state) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        user.setBaned(state);
        userRepository.save(user);
    }

    public void updateUserRole(String email, String role) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!role.equals("ROLE_ADMIN") && !role.equals("ROLE_USER")) {
            throw new IllegalArgumentException("Invalid role");
        }

        user.setRole(role);
        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
