package com.example.history_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.history_service.entity.CallHistory;

public interface CallHistoryRepository extends JpaRepository<CallHistory, Long> {
}
