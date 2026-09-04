package com.example.history_service.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.history_service.config.DataInitializer;
import com.example.history_service.dto.CallHistoryDto;
import com.example.history_service.entity.CallHistory;
import com.example.history_service.repository.CallHistoryRepository;
import com.example.history_service.util.PhoneNumberUtil;

@Service
public class CallHistoryService {

    private final CallHistoryRepository repository;
    private final DataInitializer dataInitializer;
    private final RestTemplate restTemplate;

    @Value("${search-history-service.url:http://search-history-service:8084}")
    private String searchHistoryServiceUrl;

    public CallHistoryService(CallHistoryRepository repository, DataInitializer dataInitializer) {
        this.repository = repository;
        this.dataInitializer = dataInitializer;
        this.restTemplate = new RestTemplate();
    }

    public List<CallHistoryDto> findAll() {
        checkAndRefreshDatesIfNeeded();
        return repository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public CallHistoryDto findById(Long id) {
        CallHistory entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appel introuvable"));
        return convertToDto(entity);
    }

    public CallHistory save(CallHistory callHistory) {
        return repository.save(callHistory);
    }

    /**
     * S'assure que la base de données contient toujours des données calées sur la date système actuelle (LocalDate.now())
     */
    private synchronized void checkAndRefreshDatesIfNeeded() {
        try {
            LocalDate today = LocalDate.now();
            LocalDateTime startOfToday = today.atStartOfDay();
            LocalDateTime endOfToday = today.atTime(LocalTime.MAX);

            // Vérifier s'il existe des appels enregistrés pour aujourd'hui
            long todayCallsCount = repository.searchCombined("", "", true, startOfToday, endOfToday, "ALL").size();
            if (todayCallsCount == 0) {
                System.out.println(">>> CallHistoryService: Aucune donnée trouvée pour la date actuelle (" + today + "). Re-synchronisation dynamique des dates...");
                dataInitializer.initData();
            }
        } catch (Exception e) {
            System.err.println(">>> CallHistoryService checkAndRefreshDatesIfNeeded error: " + e.getMessage());
        }
    }

    /**
     * Recherche combinée multi-critères : Numéro exact/motif + Période dynamique (LocalDate.now()) + Type d'appel
     */
    public List<CallHistoryDto> searchCombined(String number, String dateRange, String type, String username) {
        checkAndRefreshDatesIfNeeded();

        String cleanNumber = number != null ? number.trim().replaceAll("[\\s\\-\\.]", "") : "";
        String pattern = "";
        if (cleanNumber.contains("*")) {
            pattern = "%" + cleanNumber.replace("*", "%").replaceAll("%+", "%") + "%";
        }

        // Calcul dynamique des bornes temporelles basées sur la date système actuelle (LocalDate.now())
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        boolean hasDateFilter = false;

        LocalDate today = LocalDate.now();

        if ("TODAY".equalsIgnoreCase(dateRange)) {
            startDate = today.atStartOfDay();
            endDate = today.atTime(LocalTime.MAX);
            hasDateFilter = true;
        } else if ("YESTERDAY".equalsIgnoreCase(dateRange)) {
            LocalDate yesterday = today.minusDays(1);
            startDate = yesterday.atStartOfDay();
            endDate = yesterday.atTime(LocalTime.MAX);
            hasDateFilter = true;
        } else if ("MONTH".equalsIgnoreCase(dateRange)) {
            startDate = today.withDayOfMonth(1).atStartOfDay();
            endDate = today.atTime(LocalTime.MAX);
            hasDateFilter = true;
        }

        String filterType = (type != null && !type.trim().isEmpty()) ? type.trim().toUpperCase() : "ALL";

        List<CallHistory> calls = repository.searchCombined(
                cleanNumber,
                pattern,
                hasDateFilter,
                startDate,
                endDate,
                filterType
        );

        // Enregistrer la recherche dans le service d'audit de traçabilité
        recordSearchLog(username != null ? username : "anonyme", cleanNumber.isEmpty() ? "TOUS" : cleanNumber, calls.size());

        return calls.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Compatibilité pour l'ancien endpoint
    public List<CallHistoryDto> search(String number, String username) {
        return searchCombined(number, "ALL", "ALL", username);
    }

    private void recordSearchLog(String username, String searchedNumber, int resultCount) {
        try {
            String url = searchHistoryServiceUrl + "/api/searches";
            Map<String, Object> body = new HashMap<>();
            body.put("username", username);
            body.put("searchedNumber", searchedNumber);
            body.put("searchTimestamp", LocalDateTime.now().toString());
            body.put("resultCount", resultCount);

            restTemplate.postForLocation(url, body);
            System.out.println(">>> Search history recorded via REST for user: " + username + ", number: " + searchedNumber);
        } catch (Exception e) {
            System.err.println(">>> CallHistoryService: Failed to log search event to SearchHistoryService: " + e.getMessage());
        }
    }

    private CallHistoryDto convertToDto(CallHistory entity) {
        return new CallHistoryDto(
                entity.getId(),
                PhoneNumberUtil.mask(entity.getCaller()),
                PhoneNumberUtil.mask(entity.getCallee()),
                entity.getStartedAt(),
                entity.getEndedAt(),
                entity.getDuration(),
                entity.getStatus(),
                entity.getType(),
                entity.getLocation()
        );
    }
}
