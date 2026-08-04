package com.example.history_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.history_service.entity.CallHistory;
import com.example.history_service.repository.CallHistoryRepository;

@Service
public class CallHistoryService {

    private final CallHistoryRepository repository;

    public CallHistoryService(CallHistoryRepository repository) {
        this.repository = repository;
    }

    public CallHistory save(CallHistory callHistory) {
        return repository.save(callHistory);
    }

    public List<CallHistory> findAll() {
        return repository.findAll();
    }

    public CallHistory findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Historique introuvable"));
    }
}
