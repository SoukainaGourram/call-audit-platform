package com.example.search_history.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.search_history.entity.SearchLog;
import com.example.search_history.service.SearchLogService;

@RestController
@RequestMapping("/api/searches")
public class SearchLogController {

    private final SearchLogService service;

    public SearchLogController(SearchLogService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SearchLog> recordSearch(@RequestBody Map<String, Object> payload) {
        String username = (String) payload.get("username");
        String searchedNumber = (String) payload.get("searchedNumber");
        int resultCount = payload.containsKey("resultCount") ? Integer.parseInt(payload.get("resultCount").toString()) : 0;

        SearchLog log = service.recordSearch(username, searchedNumber, resultCount);
        return new ResponseEntity<>(log, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SearchLog>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<SearchLog>> findByUsername(@PathVariable String username) {
        return ResponseEntity.ok(service.findByUsername(username));
    }
}
