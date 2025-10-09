package fr.monclub.auth_service.auth.service;

import fr.monclub.auth_service.auth.catalog.UserCatalog;
import fr.monclub.auth_service.auth.model.User;
import fr.monclub.auth_service.config.JWT.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserCatalog userCatalog;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void register_shouldSaveUser_whenEmailNotExists() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");

        when(userCatalog.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        String result = authService.register(user);

        assertEquals("User registered successfully", result);
        assertEquals("encodedPassword", user.getPassword());
        assertEquals("USER", user.getRole());
        verify(userCatalog).save(user);
    }

    @Test
    void register_shouldThrowException_whenEmailAlreadyExists() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");

        when(userCatalog.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(user));
        assertEquals("Username already exists", ex.getMessage());

        verify(userCatalog, never()).save(any());
    }


    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {
        String username = "test@example.com";
        String password = "password123";

        User user = new User();
        user.setEmail(username);
        user.setPassword("encodedPassword");

        when(userCatalog.findByEmail(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);
        when(jwtUtils.generateToken(user)).thenReturn("jwt-token");

        String token = authService.login(username, password);

        assertEquals("jwt-token", token);
        verify(jwtUtils).generateToken(user);
    }

    @Test
    void login_shouldThrowException_whenUserNotFound() {
        when(userCatalog.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login("unknown@example.com", "pass"));
        assertEquals("Invalid username or password", ex.getMessage());
    }

    @Test
    void login_shouldThrowException_whenPasswordInvalid() {
        String username = "test@example.com";
        User user = new User();
        user.setEmail(username);
        user.setPassword("encodedPassword");

        when(userCatalog.findByEmail(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(username, "wrongPassword"));
        assertEquals("Invalid username or password", ex.getMessage());

        verify(jwtUtils, never()).generateToken(any());
    }
}
