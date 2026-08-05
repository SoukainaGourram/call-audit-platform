package com.example.user_service.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.example.user_service.entity.UserProfile;
import com.example.user_service.service.UserProfileService;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserProfileService userProfileService;

    public DataInitializer(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @Override
    public void run(String... args) {
        try {
            if (userProfileService.findAll().isEmpty()) {
                UserProfile admin = new UserProfile(
                        null,
                        "admin",
                        "admin123",
                        "Administrateur Inwi",
                        "admin@inwi.ma",
                        "ADMIN",
                        true
                );
                userProfileService.save(admin);

                UserProfile user = new UserProfile(
                        null,
                        "user",
                        "user123",
                        "Agent Consultation",
                        "user@inwi.ma",
                        "USER",
                        true
                );
                userProfileService.save(user);

                System.out.println(">>> User-Service: Données de démonstration (admin/user) initialisées avec succès !");
            }
        } catch (Exception e) {
            System.err.println(">>> User-Service DataInitializer: " + e.getMessage());
        }
    }
}
