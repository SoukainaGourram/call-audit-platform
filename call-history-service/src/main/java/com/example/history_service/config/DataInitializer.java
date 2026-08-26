package com.example.history_service.config;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
            repository.deleteAll();
            System.out.println(">>> Call-History-Service: Initialisation des enregistrements d'appels fictifs marocains (Aujourd'hui, Hier, Ce Mois et Antérieurs)...");

            Random random = new Random(42);
            List<CallHistory> calls = new ArrayList<>();

            String[] coreNumbers = {
                "0661234567", "0667890123", "0655443322", "0522998877",
                "0707112233", "0661889900", "0670123456", "0663456789",
                "0537112233", "0770998877", "0661556677", "0662445566",
                "0612345678", "0678901234", "0699887766", "0611223344"
            };

            String[] mobilePrefixes = {"0661", "0662", "0663", "0666", "0667", "0670", "0671", "0700", "0707", "0770"};
            String[] landlinePrefixes = {"0522", "0537", "0524", "0539", "0528", "0535", "0536"};

            String[] cities = {
                "Casablanca", "Rabat", "Marrakech", "Tanger", "Agadir",
                "Fès", "Meknès", "Oujda", "Tétouan", "El Jadida",
                "Kénitra", "Safi", "Nador", "Laâyoune", "Dakhla"
            };

            String[] types = {"INCOMING", "OUTGOING", "MISSED"};
            LocalDateTime now = LocalDateTime.now();

            // Génération explicite d'appels garantis pour les numéros clés sur Aujourd'hui, Hier et Ce Mois
            for (String testNum : coreNumbers) {
                // Appels Aujourd'hui
                for (int t = 0; t < 4; t++) {
                    LocalDateTime startedAt = now.minusHours(t * 2 + 1).minusMinutes(15);
                    calls.add(createCall(testNum, generateMoroccanNumber(random, mobilePrefixes, landlinePrefixes), startedAt, types[t % 3], cities[random.nextInt(cities.length)], random));
                }

                // Appels Hier
                for (int y = 0; y < 4; y++) {
                    LocalDateTime startedAt = now.minusDays(1).minusHours(y * 3 + 2).minusMinutes(10);
                    calls.add(createCall(testNum, generateMoroccanNumber(random, mobilePrefixes, landlinePrefixes), startedAt, types[y % 3], cities[random.nextInt(cities.length)], random));
                }

                // Appels Ce Mois-ci
                for (int m = 0; m < 5; m++) {
                    int dayInMonth = Math.max(1, Math.min(now.getDayOfMonth() - 1, m * 3 + 2));
                    LocalDateTime startedAt = now.withDayOfMonth(dayInMonth).minusHours(m * 2).minusMinutes(20);
                    calls.add(createCall(testNum, generateMoroccanNumber(random, mobilePrefixes, landlinePrefixes), startedAt, types[m % 3], cities[random.nextInt(cities.length)], random));
                }

                // Appels Mois Antérieurs
                for (int prev = 0; prev < 6; prev++) {
                    LocalDateTime startedAt = now.minusDays(45 + prev * 15).minusHours(prev);
                    calls.add(createCall(testNum, generateMoroccanNumber(random, mobilePrefixes, landlinePrefixes), startedAt, types[prev % 3], cities[random.nextInt(cities.length)], random));
                }
            }

            // Génération de 200 autres appels aléatoires
            for (int i = 0; i < 200; i++) {
                String caller = generateMoroccanNumber(random, mobilePrefixes, landlinePrefixes);
                String callee = generateMoroccanNumber(random, mobilePrefixes, landlinePrefixes);
                LocalDateTime startedAt = now.minusDays(random.nextInt(90)).minusHours(random.nextInt(24));
                calls.add(createCall(caller, callee, startedAt, types[random.nextInt(types.length)], cities[random.nextInt(cities.length)], random));
            }

            repository.saveAll(calls);
            System.out.println(">>> Call-History-Service: " + calls.size() + " enregistrements d'appels fictifs marocains initialisés avec succès dans PostgreSQL !");
        } catch (Exception e) {
            System.err.println(">>> Call-History-Service DataInitializer: " + e.getMessage());
        }
    }

    private CallHistory createCall(String caller, String callee, LocalDateTime startedAt, String type, String location, Random random) {
        String status;
        int durationSeconds;

        if ("MISSED".equals(type)) {
            status = "MANQUE";
            durationSeconds = 0;
        } else {
            int statusRand = random.nextInt(10);
            if (statusRand < 7) {
                status = "TERMINE";
                durationSeconds = random.nextInt(1800) + 15;
            } else if (statusRand < 9) {
                status = "OCCUPE";
                durationSeconds = 0;
            } else {
                status = "REJETE";
                durationSeconds = 0;
            }
        }

        LocalDateTime endedAt = startedAt.plusSeconds(durationSeconds);
        String durationStr = formatDuration(durationSeconds);

        return new CallHistory(
            null,
            caller,
            callee,
            startedAt,
            endedAt,
            durationStr,
            status,
            type,
            location
        );
    }

    private String generateMoroccanNumber(Random random, String[] mobilePrefixes, String[] landlinePrefixes) {
        boolean isMobile = random.nextDouble() < 0.85;
        String prefix = isMobile 
            ? mobilePrefixes[random.nextInt(mobilePrefixes.length)]
            : landlinePrefixes[random.nextInt(landlinePrefixes.length)];
        
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String formatDuration(int seconds) {
        if (seconds <= 0) return "00m 00s";
        int mins = seconds / 60;
        int secs = seconds % 60;
        if (mins >= 60) {
            int hrs = mins / 60;
            mins = mins % 60;
            return String.format("%02dh %02dm %02ds", hrs, mins, secs);
        }
        return String.format("%02dm %02ds", mins, secs);
    }
}
