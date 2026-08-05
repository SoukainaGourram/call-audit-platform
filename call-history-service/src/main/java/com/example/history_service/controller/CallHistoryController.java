package com.example.history_service.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.history_service.dto.CallHistoryDto;
import com.example.history_service.entity.CallHistory;
import com.example.history_service.service.CallHistoryService;

@RestController
@RequestMapping("/api/calls")
public class CallHistoryController {

    private final CallHistoryService callHistoryService;

    public CallHistoryController(CallHistoryService callHistoryService) {
        this.callHistoryService = callHistoryService;
    }

    @GetMapping
    public ResponseEntity<List<CallHistoryDto>> findAll() {
        return ResponseEntity.ok(callHistoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CallHistoryDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(callHistoryService.findById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<CallHistoryDto>> search(
            @RequestParam("number") String number,
            @RequestParam(value = "username", required = false, defaultValue = "user") String username) {
        return ResponseEntity.ok(callHistoryService.search(number, username));
    }

    @PostMapping
    public ResponseEntity<CallHistory> save(@RequestBody CallHistory callHistory) {
        return new ResponseEntity<>(callHistoryService.save(callHistory), HttpStatus.CREATED);
    }
}
