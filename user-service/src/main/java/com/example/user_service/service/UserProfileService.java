package com.example.user_service.service;

import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.user_service.entity.UserProfile;
import com.example.user_service.repository.UserProfileRepository;

@Service
public class UserProfileService {

    private final UserProfileRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserProfileService(UserProfileRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserProfile> findAll() {
        return repository.findAll();
    }

    public UserProfile findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'ID : " + id));
    }

    public UserProfile findByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + username));
    }

    /**
     * Sauvegarde ou création fluide sans blocage (met à jour intelligemment en cas de réutilisation de login ou d'email)
     */
    public UserProfile save(UserProfile profile) {
        if (profile.getId() == null) {
            // 1. Chercher si un profil existe déjà par username ou par email pour éviter tout conflit de contrainte d'unicité
            UserProfile existing = repository.findByUsername(profile.getUsername())
                    .orElseGet(() -> repository.findByEmail(profile.getEmail()).orElse(null));

            if (existing != null) {
                // Mettre à jour l'utilisateur existant de manière fluide
                existing.setUsername(profile.getUsername());
                existing.setFullName(profile.getFullName());
                existing.setEmail(profile.getEmail());
                existing.setRole(profile.getRole());
                existing.setEnabled(profile.isEnabled());
                if (profile.getPassword() != null && !profile.getPassword().trim().isEmpty()) {
                    if (!profile.getPassword().startsWith("$2a$")) {
                        existing.setPassword(passwordEncoder.encode(profile.getPassword()));
                    } else {
                        existing.setPassword(profile.getPassword());
                    }
                }
                return repository.save(existing);
            }

            // 2. Sinon, créer un nouveau profil
            if (profile.getPassword() != null && !profile.getPassword().startsWith("$2a$")) {
                profile.setPassword(passwordEncoder.encode(profile.getPassword()));
            }
        }
        return repository.save(profile);
    }

    public UserProfile update(Long id, UserProfile updatedProfile) {
        UserProfile existing = findById(id);
        existing.setFullName(updatedProfile.getFullName());
        existing.setEmail(updatedProfile.getEmail());
        if (updatedProfile.getRole() != null) {
            existing.setRole(updatedProfile.getRole());
        }
        existing.setEnabled(updatedProfile.isEnabled());
        if (updatedProfile.getPassword() != null && !updatedProfile.getPassword().trim().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(updatedProfile.getPassword()));
        }
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Utilisateur introuvable");
        }
        repository.deleteById(id);
    }

    public UserProfile toggleEnabled(Long id) {
        UserProfile profile = findById(id);
        profile.setEnabled(!profile.isEnabled());
        return repository.save(profile);
    }

    public UserProfile updateProfile(String username, String fullName, String email) {
        UserProfile profile = findByUsername(username);
        profile.setFullName(fullName);
        profile.setEmail(email);
        return repository.save(profile);
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        UserProfile profile = findByUsername(username);
        if (!passwordEncoder.matches(oldPassword, profile.getPassword())) {
            throw new RuntimeException("L'ancien mot de passe est incorrect");
        }
        profile.setPassword(passwordEncoder.encode(newPassword));
        repository.save(profile);
    }
}