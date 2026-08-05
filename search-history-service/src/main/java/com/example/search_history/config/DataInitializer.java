package com.example.search_history.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.example.search_history.service.SearchLogService;

@Component
public class DataInitializer implements CommandLineRunner {

    private final SearchLogService service;

    public DataInitializer(SearchLogService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) {
        try {
            if (service.findAll().isEmpty()) {
                service.recordSearch("admin", "0661234567", 3);
                service.recordSearch("user", "0655443322", 2);
                service.recordSearch("admin", "0522998877", 1);
                System.out.println(">>> Search-History-Service: Audit logs de recherche initialisés !");
            }
        } catch (Exception e) {
            System.err.println(">>> Search-History-Service DataInitializer: " + e.getMessage());
        }
    }
}
