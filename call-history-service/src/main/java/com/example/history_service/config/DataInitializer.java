package com.example.history_service.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
        initData();
    }

    public void initData() {
        try {
            repository.deleteAll();
            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);

            System.out.println(">>> Call-History-Service: Initialisation dynamique des appels pour Aujourd'hui (" + today + "), Hier (" + yesterday + ") et Ce Mois...");

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

            for (String testNum : coreNumbers) {

                // 1. AUJOURD'HUI (Strictement LocalDate.now() avec heures de la matinée/journée)
                calls.add(createCall(testNum, generateMoroccanNumber(random, mobilePrefixes, landlinePrefixes), today.atTime(LocalTime.of(8, 15)), "INCOMING", cities[0], random));
                calls.add(createCall(testNum, generateMoroccanNumber(random, mobilePrefixes, landlinePrefixes), today.atTime(LocalTime.of(9, 0)), "OUTGOING", cities[1], random));
                calls.add(createCall(testNum, generateMoroccanNumber(random, mobilePrefixes, landlinePrefixes), today.atTime(LocalTime.of(9, 30)), "MISSED", cities[2], random));
                calls.add(createCall(testNum, generateMoroccanNumber(random, mobilePrefixes, landlinePrefixes), today.atTime(LocalTime.of(9, 55)), "INCOMING", cities[3], random));

                // 2. HIER (Strictement LocalDate.now().minusDays(1))
                calls.add(createCall(testNum, generateMoroccanNumber(random, mobilePrefixes, landlinePrefixes), yesterday.atTime(LocalTime.of(8, 45)), "OUTGOING", cities[4], random));
                calls.add(createCall(testNum, generateMoroccanNumber(random, mobilePrefixes, landlinePrefixes), yesterday.atTime(LocalTime.of(10, 20)), "INCOMING", cities[5], random));
                calls.add(createCall(testNum, generateMoroccanNumber(random, mobilePrefixes, landlinePrefixes), yesterday.atTime(LocalTime.of(15, 10)), "MISSED", cities[6], random));
                calls.add(createCall(testNum, generateMoroccanNumber(random, mobilePrefixes, landlinePrefixes), yesterday.atTime(LocalTime.of(19, 0)), "OUTGOING", cities[7], random));

                // 3. CE MOIS-CI (Jours de la semaine écoulés dans le mois)
                int dayInMonth = today.getDayOfMonth();
                for (int d = 1; d <= dayInMonth; d++) {
                    if (d != dayInMonth && (yesterday.getMonth() != today.getMonth() || d != yesterday.getDayOfMonth())) {
                        LocalDate monthDate = today.withDayOfMonth(d);
                        calls.add(createCall(testNum, generateMoroccanNumber(random, mobilePrefixes, landlinePrefixes), monthDate.atTime(LocalTime.of(9 + (d % 8), 15)), types[d % 3], cities[random.nextInt(cities.length)], random));
                    }
                }

                // 4. MOIS ANTÉRIEURS (Historique passé)
                for (int prev = 1; prev <= 3; prev++) {
                    LocalDate prevMonthDate = today.minusMonths(prev).withDayOfMonth(15);
                    calls.add(createCall(testNum, generateMoroccanNumber(random, mobilePrefixes, landlinePrefixes), prevMonthDate.atTime(LocalTime.of(14, 20)), types[prev % 3], cities[random.nextInt(cities.length)], random));
                }
            }

            // Génération de 150 autres appels aléatoires répartis
            for (int i = 0; i < 150; i++) {
                String caller = generateMoroccanNumber(random, mobilePrefixes, landlinePrefixes);
                String callee = generateMoroccanNumber(random, mobilePrefixes, landlinePrefixes);
                
                int dayOffset = random.nextInt(45);
                LocalDate callDate = today.minusDays(dayOffset);
                LocalTime callTime = LocalTime.of(8 + random.nextInt(12), random.nextInt(60));
                
                calls.add(createCall(caller, callee, callDate.atTime(callTime), types[random.nextInt(types.length)], cities[random.nextInt(cities.length)], random));
            }

            repository.saveAll(calls);
            System.out.println(">>> Call-History-Service: " + calls.size() + " enregistrements d'appels fictifs créés avec succès pour la date système " + today + " !");
        } catch (Exception e) {
            System.err.println(">>> Call-History-Service DataInitializer Error: " + e.getMessage());
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
