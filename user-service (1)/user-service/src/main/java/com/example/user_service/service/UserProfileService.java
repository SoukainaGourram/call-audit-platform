package com.example.user_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.user_service.entity.UserProfile;
import com.example.user_service.repository.UserProfileRepository;

@Service
public class UserProfileService {

    private final UserProfileRepository repository;

    public UserProfileService(UserProfileRepository repository) {
        this.repository = repository;
    }

    public UserProfile save(UserProfile profile) {
        return repository.save(profile);
    }

    public List<UserProfile> findAll() {
        return repository.findAll();
    }

    public UserProfile findByUsername(String username) {
        return repository.findByUsername(username).orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }
}
public list <UserProfile> find(){ //ajout d'une liste 
    return repository>find(); 
}
    