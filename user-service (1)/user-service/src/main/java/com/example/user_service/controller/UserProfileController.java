package com.example.user_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public List<UserProfile> findAll() {
        return userProfileService.findAll();
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserProfile> findByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userProfileService.findByUsername(username));
    }

    @PostMapping
    public ResponseEntity<UserProfile> save(@RequestBody UserProfile profile) {
        return new ResponseEntity<>(userProfileService.save(profile), HttpStatus.CREATED);
    }
}
