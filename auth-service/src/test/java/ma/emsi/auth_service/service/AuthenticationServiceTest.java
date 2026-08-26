package ma.emsi.auth_service.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import ma.emsi.auth_service.dto.LoginRequest;
import ma.emsi.auth_service.dto.LoginResponse;
import ma.emsi.auth_service.entity.User;
import ma.emsi.auth_service.exception.DisabledAccountException;
import ma.emsi.auth_service.exception.InvalidCredentialsException;
import ma.emsi.auth_service.exception.UserServiceUnavailableException;
import ma.emsi.auth_service.repository.UserRepository;
import ma.emsi.auth_service.security.JwtService;

class AuthenticationServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);
        authenticationService = new AuthenticationService(userRepository, passwordEncoder, jwtService, 1000, 1000);
    }

    @Test
    void login_LocalUserSuccess() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("encodedPassword");
        user.setEnabled(true);
        user.setRole("ADMIN");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("admin123", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(anyString(), anyString(), anyString(), anyString())).thenReturn("mockedJwtToken");

        LoginRequest request = new LoginRequest("admin", "admin123");
        LoginResponse response = authenticationService.login(request);

        assertNotNull(response);
        assertEquals("mockedJwtToken", response.getToken());
        assertEquals("admin", response.getUsername());
        assertEquals("ADMIN", response.getRole());
    }

    @Test
    void login_DisabledAccount_ThrowsForbiddenException() {
        User user = new User();
        user.setUsername("disabled_agent");
        user.setPassword("encodedPassword");
        user.setEnabled(false);

        when(userRepository.findByUsername("disabled_agent")).thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest("disabled_agent", "user123");
        assertThrows(DisabledAccountException.class, () -> authenticationService.login(request));
    }

    @Test
    void login_InvalidPassword_ThrowsUnauthorizedException() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("encodedPassword");
        user.setEnabled(true);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        LoginRequest request = new LoginRequest("admin", "wrongPassword");
        assertThrows(InvalidCredentialsException.class, () -> authenticationService.login(request));
    }
}
