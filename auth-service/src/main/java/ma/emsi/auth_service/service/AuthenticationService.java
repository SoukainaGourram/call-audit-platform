package ma.emsi.auth_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ma.emsi.auth_service.dto.LoginRequest;
import ma.emsi.auth_service.dto.LoginResponse;
import ma.emsi.auth_service.dto.UserDto;
import ma.emsi.auth_service.entity.User;
import ma.emsi.auth_service.repository.UserRepository;
import ma.emsi.auth_service.security.JwtService;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RestTemplate restTemplate;

    @Value("${user-service.url:http://localhost:8082}")
    private String userServiceUrl;

    public AuthenticationService(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.restTemplate = new RestTemplate();
    }

    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();

        // First attempt REST call to User-Service
        try {
            String url = userServiceUrl + "/api/users/internal/" + username;
            UserDto userDto = restTemplate.getForObject(url, UserDto.class);

            if (userDto != null) {
                if (!userDto.isEnabled()) {
                    throw new RuntimeException("Votre compte est désactivé. Veuillez contacter un administrateur.");
                }
                if (!passwordEncoder.matches(password, userDto.getPassword())) {
                    throw new RuntimeException("Mot de passe incorrect");
                }
                String token = jwtService.generateToken(userDto.getUsername(), userDto.getRole(), userDto.getFullName(), userDto.getEmail());
                return new LoginResponse(token, userDto.getUsername(), userDto.getRole(), userDto.getFullName(), userDto.getEmail());
            }
        } catch (Exception ex) {
            // Fallback to local Auth-Service UserRepository if user-service REST call failed
            System.out.println("User-Service REST call fallback: " + ex.getMessage());
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur ou mot de passe incorrect"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        String role = user.getRole() != null ? user.getRole() : "USER";
        String token = jwtService.generateToken(user.getUsername(), role, user.getUsername(), user.getUsername() + "@inwi.ma");
        return new LoginResponse(token, user.getUsername(), role, user.getUsername(), user.getUsername() + "@inwi.ma");
    }
}