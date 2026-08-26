package com.example.history_service.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.history_service.dto.CallHistoryDto;
import com.example.history_service.entity.CallHistory;
import com.example.history_service.service.CallHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/calls")
@Tag(name = "Historique des Appels", description = "Endpoints de recherche d'appels et consultation masquée CNDP/RGPD")
public class CallHistoryController {

    private final CallHistoryService callHistoryService;

    public CallHistoryController(CallHistoryService callHistoryService) {
        this.callHistoryService = callHistoryService;
    }

    @Operation(summary = "Récupère tous les enregistrements d'appels (numéros masqués dans la réponse)")
    @GetMapping
    public ResponseEntity<List<CallHistoryDto>> findAll() {
        return ResponseEntity.ok(callHistoryService.findAll());
    }

    @Operation(summary = "Récupère les détails d'un appel par son ID")
    @GetMapping("/{id}")
    public ResponseEntity<CallHistoryDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(callHistoryService.findById(id));
    }

    @Operation(summary = "Recherche combinée multi-critères d'appels (numéro, période dynamique, type d'appel)", 
               description = "Applique simultanément la recherche par numéro (brut/masqué), le filtre temporel dynamique (TODAY, YESTERDAY, MONTH) et le type d'appel (INCOMING, OUTGOING, MISSED).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des enregistrements d'appels filtrés et masqués"),
        @ApiResponse(responseCode = "400", description = "Paramètre de recherche invalide")
    })
    @GetMapping("/search")
    public ResponseEntity<List<CallHistoryDto>> search(
            @RequestParam(value = "number", required = false, defaultValue = "") String number,
            @RequestParam(value = "dateRange", required = false, defaultValue = "ALL") String dateRange,
            @RequestParam(value = "type", required = false, defaultValue = "ALL") String type,
            @RequestParam(value = "username", required = false, defaultValue = "user") String username) {
        return ResponseEntity.ok(callHistoryService.searchCombined(number, dateRange, type, username));
    }

    @Operation(summary = "Ajoute un nouvel enregistrement d'appel dans la base PostgreSQL")
    @PostMapping
    public ResponseEntity<CallHistory> save(@RequestBody CallHistory callHistory) {
        return new ResponseEntity<>(callHistoryService.save(callHistory), HttpStatus.CREATED);
    }
}
