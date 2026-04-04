package com.election.backendjava.service;

import com.election.backendjava.exception.NoBannedUsersFoundException;
import com.election.backendjava.model.User;
import com.election.backendjava.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        // Arrange
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        emailService = Mockito.mock(EmailService.class);
        userService = new UserService();
        org.springframework.test.util.ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        org.springframework.test.util.ReflectionTestUtils.setField(userService, "passwordEncoder", passwordEncoder);
        org.springframework.test.util.ReflectionTestUtils.setField(userService, "emailService", emailService);
    }

    @Test
    void getUserByEmail_HappyPath() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        User result = userService.getUserByEmail(email);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    void getUserByEmail_UnhappyPath_NotFound() {
        // Arrange
        String email = "unknown@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.getUserByEmail(email));
    }

    @Test
    void updateProfile_HappyPath() {
        // Arrange
        String currentEmail = "old@example.com";
        String newUsername = "newname";
        String newEmail = "new@example.com";

        User user = new User();
        user.setEmail(currentEmail);
        user.setUsername("oldname");

        when(userRepository.findByEmail(currentEmail)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(newUsername)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(newEmail)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        User result = userService.updateProfile(currentEmail, newUsername, newEmail);

        // Assert
        assertEquals(newUsername, result.getUsername());
        assertEquals("old@example.com", result.getEmail());
    }

    @Test
    void updateProfile_UnhappyPath_UsernameTaken() {
        // Arrange
        String currentEmail = "me@example.com";
        String newUsername = "takenUser";

        User user = new User();
        user.setEmail(currentEmail);
        user.setUsername("me");

        User existingUser = new User();

        when(userRepository.findByEmail(currentEmail)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(newUsername)).thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                userService.updateProfile(currentEmail, newUsername, "me@example.com")
        );
    }

    @Test
    void changePassword_HappyPath() {
        // Arrange
        String email = "test@example.com";
        String oldPass = "oldPass";
        String newPass = "newPass";

        User user = new User();
        user.setEmail(email);
        user.setPassword("encodedOldPass");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(oldPass, "encodedOldPass")).thenReturn(true);
        when(passwordEncoder.encode(newPass)).thenReturn("encodedNewPass");

        // Act
        userService.changePassword(email, oldPass, newPass);

        // Assert
        verify(userRepository).save(user);
        assertEquals("encodedNewPass", user.getPassword());
    }

    @Test
    void changePassword_UnhappyPath_WrongOldPassword() {
        // Arrange
        String email = "test@example.com";
        String oldPass = "wrongPass";

        User user = new User();
        user.setPassword("encodedRealPass");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(oldPass, "encodedRealPass")).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                userService.changePassword(email, oldPass, "new")
        );
    }

    @Test
    void updatePersona_HappyPath() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User result = userService.updatePersona(email, "nico");

        // Assert
        assertEquals("nico", result.getPersona());
    }

    @Test
    void updatePersona_UnhappyPath_InvalidPersona() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                userService.updatePersona(email, "invalid")
        );
    }

    @Test
    void getAllBanedUsers_HappyPath() {
        // Arrange
        User banned = new User();
        banned.setBaned(true);
        when(userRepository.findByBanedTrue()).thenReturn(List.of(banned));

        // Act
        List<User> result = userService.getAllBanedUsers();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getAllBanedUsers_UnhappyPath_NoneFound() {
        // Arrange
        when(userRepository.findByBanedTrue()).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(NoBannedUsersFoundException.class, () -> userService.getAllBanedUsers());
    }

    @Test
    void updateUserRole_HappyPath() {
        // Arrange
        String email = "user@test.com";
        User user = new User();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        userService.updateUserRole(email, "ROLE_ADMIN");

        // Assert
        assertEquals("ROLE_ADMIN", user.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void updateUserRole_UnhappyPath_InvalidRole() {
        // Arrange
        String email = "user@test.com";
        User user = new User();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                userService.updateUserRole(email, "SUPER_ADMIN")
        );
    }
}