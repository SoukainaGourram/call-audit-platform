package com.example.user_service.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.user_service.dto.InternalUserDto;
import com.example.user_service.entity.UserProfile;
import com.example.user_service.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Gestion des Utilisateurs", description = "Endpoints d'administration des comptes utilisateurs, profils et habilitations agents")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @Operation(summary = "Récupère la liste de tous les utilisateurs enregistrés en base PostgreSQL")
    @GetMapping
    public ResponseEntity<List<UserProfile>> findAll() {
        return ResponseEntity.ok(userProfileService.findAll());
    }

    @Operation(summary = "Récupère les détails d'un utilisateur par son ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserProfile> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userProfileService.findById(id));
    }

    @Operation(summary = "Récupère un profil utilisateur par son nom d'utilisateur (login)")
    @GetMapping("/username/{username}")
    public ResponseEntity<UserProfile> findByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userProfileService.findByUsername(username));
    }

    @Operation(summary = "Endpoint interne de récupération des identifiants et mot de passe BCrypt pour l'Auth Service")
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

    @Operation(summary = "Crée un nouveau compte utilisateur dans la base de données")
    @PostMapping
    public ResponseEntity<UserProfile> save(@RequestBody UserProfile profile) {
        return new ResponseEntity<>(userProfileService.save(profile), HttpStatus.CREATED);
    }

    @Operation(summary = "Met à jour les informations d'un utilisateur par son ID")
    @PutMapping("/{id}")
    public ResponseEntity<UserProfile> update(@PathVariable Long id, @RequestBody UserProfile profile) {
        return ResponseEntity.ok(userProfileService.update(id, profile));
    }

    @Operation(summary = "Supprime définitivement un compte utilisateur par son ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        userProfileService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé avec succès"));
    }

    @Operation(summary = "Bascule le statut d'activation (Actif / Désactivé) d'un compte utilisateur")
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<UserProfile> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(userProfileService.toggleEnabled(id));
    }

    @Operation(summary = "Met à jour le nom complet et l'email du profil personnel d'un agent")
    @PutMapping("/profile/{username}")
    public ResponseEntity<UserProfile> updateProfile(@PathVariable String username, @RequestBody Map<String, String> body) {
        String fullName = body.get("fullName");
        String email = body.get("email");
        return ResponseEntity.ok(userProfileService.updateProfile(username, fullName, email));
    }

    @Operation(summary = "Modifie le mot de passe personnel d'un agent de manière sécurisée")
    @PutMapping("/profile/{username}/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@PathVariable String username, @RequestBody Map<String, String> body) {
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        userProfileService.changePassword(username, oldPassword, newPassword);
        return ResponseEntity.ok(Map.of("message", "Mot de passe modifié avec succès"));
    }
}
