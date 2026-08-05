package com.example.history_service.config;

import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.example.history_service.entity.CallHistory;
import com.example.history_service.repository.CallHistoryRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CallHistoryRepository repository;

    public DataInitializer(CallHistoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        try {
            if (repository.count() == 0) {
                repository.save(new CallHistory(
                        null,
                        "0661234567",
                        "0667890123",
                        LocalDateTime.now().minusHours(2),
                        LocalDateTime.now().minusHours(2).plusMinutes(12).plusSeconds(34),
                        "12m 34s",
                        "TERMINE",
                        "OUTGOING",
                        "Casablanca"
                ));

                repository.save(new CallHistory(
                        null,
                        "0655443322",
                        "0661234567",
                        LocalDateTime.now().minusHours(5),
                        LocalDateTime.now().minusHours(5).plusMinutes(4).plusSeconds(15),
                        "04m 15s",
                        "TERMINE",
                        "INCOMING",
                        "Rabat"
                ));

                repository.save(new CallHistory(
                        null,
                        "0661234567",
                        "0611223344",
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now().minusDays(1).plusSeconds(0),
                        "00m 00s",
                        "MANQUE",
                        "MISSED",
                        "Marrakech"
                ));

                repository.save(new CallHistory(
                        null,
                        "0522998877",
                        "0661234567",
                        LocalDateTime.now().minusDays(2),
                        LocalDateTime.now().minusDays(2).plusMinutes(18).plusSeconds(50),
                        "18m 50s",
                        "TERMINE",
                        "INCOMING",
                        "Tanger"
                ));

                repository.save(new CallHistory(
                        null,
                        "0667890123",
                        "0655443322",
                        LocalDateTime.now().minusDays(3),
                        LocalDateTime.now().minusDays(3).plusMinutes(6).plusSeconds(10),
                        "06m 10s",
                        "TERMINE",
                        "OUTGOING",
                        "Agadir"
                ));

                System.out.println(">>> Call-History-Service: Données d'appels téléphoniques d'exemple initialisées !");
            }
        } catch (Exception e) {
            System.err.println(">>> Call-History-Service DataInitializer: " + e.getMessage());
        }
    }
}
