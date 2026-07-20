package ma.emsi.auth_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ma.emsi.auth_service.dto.LoginRequest;
import ma.emsi.auth_service.dto.LoginResponse;
import ma.emsi.auth_service.dto.RegisterRequest;
import ma.emsi.auth_service.entity.User;
import ma.emsi.auth_service.service.AuthenticationService;
import ma.emsi.auth_service.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    public AuthController(UserService userService,AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;

    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {

        User user = userService.register(request);

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
    @PostMapping("/login")
public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

    return ResponseEntity.ok(authenticationService.login(request));
}
}   