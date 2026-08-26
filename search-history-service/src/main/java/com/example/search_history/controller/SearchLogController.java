package com.example.search_history.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.search_history.entity.SearchLog;
import com.example.search_history.service.SearchLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/searches")
@Tag(name = "Audit de Traçabilité CNDP", description = "Endpoints de consignation et de consultation de l'historique d'audit des recherches")
public class SearchLogController {

    private final SearchLogService service;

    public SearchLogController(SearchLogService service) {
        this.service = service;
    }

    @Operation(summary = "Consigne un nouvel événement de recherche d'appel dans le registre d'audit PostgreSQL")
    @PostMapping
    public ResponseEntity<SearchLog> recordSearch(@RequestBody Map<String, Object> payload) {
        String username = (String) payload.get("username");
        String searchedNumber = (String) payload.get("searchedNumber");
        int resultCount = payload.containsKey("resultCount") ? Integer.parseInt(payload.get("resultCount").toString()) : 0;

        SearchLog log = service.recordSearch(username, searchedNumber, resultCount);
        return new ResponseEntity<>(log, HttpStatus.CREATED);
    }

    @Operation(summary = "Récupère l'intégralité du registre d'audit de traçabilité CNDP")
    @GetMapping
    public ResponseEntity<List<SearchLog>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Récupère l'historique des recherches effectuées par un agent spécifique")
    @GetMapping("/user/{username}")
    public ResponseEntity<List<SearchLog>> findByUsername(@PathVariable String username) {
        return ResponseEntity.ok(service.findByUsername(username));
    }
}
