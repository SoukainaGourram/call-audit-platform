package com.example.history_service.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.history_service.dto.CallHistoryDto;
import com.example.history_service.entity.CallHistory;
import com.example.history_service.repository.CallHistoryRepository;
import com.example.history_service.util.PhoneNumberUtil;

@Service
public class CallHistoryService {

    private final CallHistoryRepository repository;
    private final RestTemplate restTemplate;

    @Value("${search-history-service.url:http://localhost:8084}")
    private String searchHistoryServiceUrl;

    public CallHistoryService(CallHistoryRepository repository) {
        this.repository = repository;
        this.restTemplate = new RestTemplate();
    }

    public List<CallHistoryDto> findAll() {
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

    public List<CallHistoryDto> search(String number, String username) {
        List<CallHistory> calls = repository.searchByPhoneNumber(number);

        // Record search in Search History Service automatically
        recordSearchLog(username != null ? username : "anonyme", number, calls.size());

        return calls.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
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
