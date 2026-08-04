package ma.emsi.auth_service.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import ma.emsi.auth_service.dto.RegisterRequest;
import ma.emsi.auth_service.repository.UserRepository;

class UserServiceTest {

    @Test
    void shouldRejectBlankRegistrationData() {
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        UserService userService = new UserService(userRepository, passwordEncoder);

        RegisterRequest request = new RegisterRequest();
        request.setUsername("   ");
        request.setPassword("123456");
        request.setFullName("Test User");
        request.setEmail("test@example.com");

        assertThrows(IllegalArgumentException.class, () -> userService.register(request));
        verifyNoInteractions(userRepository);
    }
}
