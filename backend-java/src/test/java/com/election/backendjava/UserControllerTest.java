//package com.election.backendjava;
//
//import com.election.backendjava.api.UserController;
//import com.election.backendjava.model.User;
//import com.election.backendjava.service.JwtService;
//import com.election.backendjava.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.ResponseEntity;
//
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class UserControllerTest {
//
//    private UserService userService;
//    private JwtService jwtService;
//    private UserController userController;
//
//    @BeforeEach
//    void setup() {
//        userService = mock(UserService.class);
//        jwtService = mock(JwtService.class);
//        userController = new UserController();
//        userController.userService = userService;
//        userController.jwtService = jwtService;
//    }
//
//    private User mockUser() {
//        User u = new User();
//        u.setUsername("testUser");
//        u.setEmail("test@mail.com");
//        return u;
//    }
//
//    // ============================================================
//    // HAPPY FLOW TESTS
//    // ============================================================
//
//    @Test
//    void testGetProfile_HappyFlow() {
//        when(jwtService.getUsernameFromToken("valid")).thenReturn("test@mail.com");
//        when(userService.getUserByEmail("test@mail.com")).thenReturn(mockUser());
//
//        ResponseEntity<?> response = userController.getProfile("Bearer valid");
//
//        assertEquals(200, response.getStatusCodeValue());
//        Map<?, ?> body = (Map<?, ?>) response.getBody();
//        assertEquals("testUser", body.get("username"));
//        assertEquals("test@mail.com", body.get("email"));
//    }
//
//    @Test
//    void testUpdateProfile_HappyFlow() {
//        when(jwtService.getUsernameFromToken("valid")).thenReturn("old@mail.com");
//
//        User updated = new User();
//        updated.setUsername("newUser");
//        updated.setEmail("new@mail.com");
//
//        when(userService.updateProfile("old@mail.com", "newUser", "new@mail.com"))
//                .thenReturn(updated);
//
//        Map<String, String> payload = Map.of(
//                "username", "newUser",
//                "email", "new@mail.com"
//        );
//
//        ResponseEntity<?> response = userController.updateProfile("Bearer valid", payload);
//
//        assertEquals(200, response.getStatusCodeValue());
//        Map<?, ?> body = (Map<?, ?>) response.getBody();
//        assertEquals("newUser", body.get("username"));
//    }
//
//    @Test
//    void testChangePassword_HappyFlow() {
//        when(jwtService.getUsernameFromToken("valid")).thenReturn("test@mail.com");
//
//        Map<String, String> payload = Map.of(
//                "oldPassword", "old123",
//                "newPassword", "new123"
//        );
//
//        ResponseEntity<?> response = userController.changePassword("Bearer valid", payload);
//
//        assertEquals(200, response.getStatusCodeValue());
//        Map<?, ?> body = (Map<?, ?>) response.getBody();
//        assertEquals("Wachtwoord gewijzigd.", body.get("message"));
//        verify(userService, times(1))
//                .changePassword("test@mail.com", "old123", "new123");
//    }
//
//    @Test
//    void testDeleteAccount_HappyFlow() {
//        when(jwtService.getUsernameFromToken("valid")).thenReturn("del@mail.com");
//
//        ResponseEntity<?> response = userController.deleteAccount("Bearer valid");
//
//        assertEquals(200, response.getStatusCodeValue());
//        Map<?, ?> body = (Map<?, ?>) response.getBody();
//        assertEquals("Account verwijderd.", body.get("message"));
//        verify(userService).deleteAccount("del@mail.com");
//    }
//
//    // ============================================================
//    // UNHAPPY FLOW TESTS
//    // ============================================================
//
//    @Test
//    void testGetProfile_InvalidToken() {
//        when(jwtService.getUsernameFromToken(anyString()))
//                .thenThrow(new IllegalArgumentException("Invalid token"));
//
//        ResponseEntity<?> response = userController.getProfile("Bearer invalid");
//
//        assertEquals(401, response.getStatusCodeValue());
//        assertEquals("Niet geautoriseerd", response.getBody());
//    }
//
//    @Test
//    void testUpdateProfile_InvalidPayload() {
//        when(jwtService.getUsernameFromToken("valid")).thenReturn("test@mail.com");
//        when(userService.updateProfile(any(), any(), any()))
//                .thenThrow(new IllegalArgumentException("Onjuist e-mailadres"));
//
//        Map<String, String> payload = Map.of("username", "bad", "email", "wrong");
//
//        ResponseEntity<?> response = userController.updateProfile("Bearer valid", payload);
//
//        assertEquals(400, response.getStatusCodeValue());
//        assertEquals("Onjuist e-mailadres", response.getBody());
//    }
//
//    @Test
//    void testChangePassword_IncorrectPassword() {
//        when(jwtService.getUsernameFromToken("valid")).thenReturn("test@mail.com");
//        doThrow(new IllegalArgumentException("Wachtwoord onjuist"))
//                .when(userService).changePassword(anyString(), anyString(), anyString());
//
//        Map<String, String> payload = Map.of(
//                "oldPassword", "wrongOld",
//                "newPassword", "new123"
//        );
//
//        ResponseEntity<?> response = userController.changePassword("Bearer valid", payload);
//
//        assertEquals(400, response.getStatusCodeValue());
//        assertEquals("Wachtwoord onjuist", response.getBody());
//    }
//
//    @Test
//    void testDeleteAccount_Failure() {
//        when(jwtService.getUsernameFromToken("valid")).thenReturn("test@mail.com");
//        doThrow(new RuntimeException("DB Error"))
//                .when(userService).deleteAccount("test@mail.com");
//
//        ResponseEntity<?> response = userController.deleteAccount("Bearer valid");
//
//        assertEquals(500, response.getStatusCodeValue());
//        assertEquals("Kon account niet verwijderen.", response.getBody());
//    }
//}
