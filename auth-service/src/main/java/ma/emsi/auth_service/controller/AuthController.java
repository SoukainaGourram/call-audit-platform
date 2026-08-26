package ma.emsi.auth_service.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ma.emsi.auth_service.dto.LoginRequest;
import ma.emsi.auth_service.dto.LoginResponse;
import ma.emsi.auth_service.security.JwtService;
import ma.emsi.auth_service.service.AuthenticationService;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification JWT", description = "Endpoints d'authentification et de validation de jeton pour la plateforme Inwi Enterprise")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    public AuthController(AuthenticationService authenticationService, JwtService jwtService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Authentifie un utilisateur et retourne un token JWT", description = "Vérifie les identifiants en base PostgreSQL via BCrypt et retourne le jeton JWT signé et les informations de profil.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Authentification réussie avec jeton JWT généré"),
        @ApiResponse(responseCode = "401", description = "Identifiants invalides ou compte désactivé")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @Operation(summary = "Valide un jeton JWT", description = "Vérifie la signature et l'expiration d'un jeton JWT et extrait le nom d'utilisateur et le rôle.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Jeton JWT valide"),
        @ApiResponse(responseCode = "401", description = "Jeton JWT invalide ou expiré")
    })
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestParam("token") String token) {
        try {
            Claims claims = jwtService.validateAndGetClaims(token);
            String username = jwtService.extractUsername(claims);
            String role = jwtService.extractRole(claims);

            return ResponseEntity.ok(Map.of(
                "valid", true,
                "username", username,
                "role", role != null ? role : "USER"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "message", "Token JWT invalide ou expiré"));
        }
    }
}