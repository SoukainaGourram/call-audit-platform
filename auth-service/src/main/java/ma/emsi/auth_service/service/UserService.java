package ma.emsi.auth_service.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ma.emsi.auth_service.dto.RegisterRequest;
import ma.emsi.auth_service.entity.User;
import ma.emsi.auth_service.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest request) {

        if (request == null || isBlank(request.getUsername()) || isBlank(request.getPassword())
                || isBlank(request.getFullName()) || isBlank(request.getEmail())) {
            throw new IllegalArgumentException("Tous les champs sont obligatoires");
        }

        if (userRepository.existsByUsername(request.getUsername().trim())) {
            throw new RuntimeException("Nom d'utilisateur déjà utilisé");
        }

        if (userRepository.existsByEmail(request.getEmail().trim())) {
            throw new RuntimeException("Email déjà utilisé");
        }

        User user = new User();

        user.setUsername(request.getUsername().trim());
        user.setFullName(request.getFullName().trim());
        user.setEmail(request.getEmail().trim());

        // Chiffrement du mot de passe
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Valeurs par défaut
        user.setRole("USER");
        user.setEnabled(true);

        return userRepository.save(user);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

}