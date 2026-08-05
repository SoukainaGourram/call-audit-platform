package com.example.user_service.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.user_service.dto.InternalUserDto;
import com.example.user_service.entity.UserProfile;
import com.example.user_service.service.UserProfileService;

@RestController
@RequestMapping("/api/users")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public ResponseEntity<List<UserProfile>> findAll() {
        return ResponseEntity.ok(userProfileService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfile> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userProfileService.findById(id));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserProfile> findByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userProfileService.findByUsername(username));
    }

    @GetMapping("/internal/{username}")
    public ResponseEntity<InternalUserDto> getInternalUser(@PathVariable String username) {
        UserProfile profile = userProfileService.findByUsername(username);
        InternalUserDto dto = new InternalUserDto(
                profile.getId(),
                profile.getUsername(),
                profile.getPassword(),
                profile.getFullName(),
                profile.getEmail(),
                profile.getRole(),
                profile.isEnabled()
        );
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<UserProfile> save(@RequestBody UserProfile profile) {
        return new ResponseEntity<>(userProfileService.save(profile), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserProfile> update(@PathVariable Long id, @RequestBody UserProfile profile) {
        return ResponseEntity.ok(userProfileService.update(id, profile));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        userProfileService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé avec succès"));
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<UserProfile> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(userProfileService.toggleEnabled(id));
    }

    @PutMapping("/profile/{username}")
    public ResponseEntity<UserProfile> updateProfile(@PathVariable String username, @RequestBody Map<String, String> body) {
        String fullName = body.get("fullName");
        String email = body.get("email");
        return ResponseEntity.ok(userProfileService.updateProfile(username, fullName, email));
    }

    @PutMapping("/profile/{username}/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@PathVariable String username, @RequestBody Map<String, String> body) {
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        userProfileService.changePassword(username, oldPassword, newPassword);
        return ResponseEntity.ok(Map.of("message", "Mot de passe modifié avec succès"));
    }
}
