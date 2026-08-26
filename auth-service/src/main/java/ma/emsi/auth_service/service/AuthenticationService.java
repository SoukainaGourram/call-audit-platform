package ma.emsi.auth_service.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import ma.emsi.auth_service.dto.LoginRequest;
import ma.emsi.auth_service.dto.LoginResponse;
import ma.emsi.auth_service.dto.UserDto;
import ma.emsi.auth_service.entity.User;
import ma.emsi.auth_service.exception.DisabledAccountException;
import ma.emsi.auth_service.exception.InvalidCredentialsException;
import ma.emsi.auth_service.repository.UserRepository;
import ma.emsi.auth_service.security.JwtService;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RestTemplate restTemplate;

    @Value("${user-service.url:http://user-service:8082}")
    private String userServiceUrl;

    public AuthenticationService(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 JwtService jwtService,
                                 @Value("${user-service.connect-timeout:1000}") int connectTimeout,
                                 @Value("${user-service.read-timeout:1000}") int readTimeout) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        this.restTemplate = new RestTemplate(factory);
    }

    public LoginResponse login(LoginRequest request) {
        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("Identifiant et mot de passe requis");
        }

        String username = request.getUsername().trim();
        String password = request.getPassword();

        // 1. Authentification directe via l'API REST de User-Service (Source autoritaire des comptes)
        try {
            String url = userServiceUrl + "/api/users/internal/" + username;
            UserDto userDto = restTemplate.getForObject(url, UserDto.class);

            if (userDto != null) {
                if (!userDto.isEnabled()) {
                    throw new DisabledAccountException("Votre compte est désactivé. Veuillez contacter un administrateur.");
                }
                if (passwordEncoder.matches(password, userDto.getPassword())) {
                    String role = userDto.getRole() != null ? userDto.getRole() : "USER";
                    String fullName = userDto.getFullName() != null ? userDto.getFullName() : userDto.getUsername();
                    String email = userDto.getEmail() != null ? userDto.getEmail() : userDto.getUsername() + "@inwi.ma";

                    String token = jwtService.generateToken(userDto.getUsername(), role, fullName, email);
                    return new LoginResponse(token, userDto.getUsername(), role, fullName, email);
                }
            }
        } catch (DisabledAccountException ex) {
            throw ex;
        } catch (HttpClientErrorException.NotFound ex) {
            // Utilisateur non trouvé sur User-Service
        } catch (Exception ex) {
            System.err.println(">>> Auth-Service: Communication REST vers User-Service fallback local pour: " + username);
        }

        // 2. Fallback rapide sur la base locale d'Auth-Service si User-Service indisponible
        Optional<User> localUserOpt = userRepository.findByUsername(username);
        if (localUserOpt.isPresent()) {
            User user = localUserOpt.get();
            if (!user.isEnabled()) {
                throw new DisabledAccountException("Votre compte est désactivé. Veuillez contacter un administrateur.");
            }
            if (passwordEncoder.matches(password, user.getPassword())) {
                String role = user.getRole() != null ? user.getRole() : "USER";
                String fullName = user.getFullName() != null ? user.getFullName() : user.getUsername();
                String email = user.getEmail() != null ? user.getEmail() : user.getUsername() + "@inwi.ma";

                String token = jwtService.generateToken(user.getUsername(), role, fullName, email);
                return new LoginResponse(token, user.getUsername(), role, fullName, email);
            }
        }

        throw new InvalidCredentialsException("Nom d'utilisateur ou mot de passe incorrect");
    }
}