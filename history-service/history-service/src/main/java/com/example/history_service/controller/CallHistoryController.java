package com.example.history_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.history_service.entity.CallHistory;
import com.example.history_service.service.CallHistoryService;

@RestController
@RequestMapping("/api/history")
public class CallHistoryController {

    private final CallHistoryService callHistoryService;

    public CallHistoryController(CallHistoryService callHistoryService) {
        this.callHistoryService = callHistoryService;
    }

    @GetMapping
    public List<CallHistory> findAll() {
        return callHistoryService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CallHistory> findById(@PathVariable Long id) {
        return ResponseEntity.ok(callHistoryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CallHistory> save(@RequestBody CallHistory callHistory) {
        return new ResponseEntity<>(callHistoryService.save(callHistory), HttpStatus.CREATED);
    }
}
