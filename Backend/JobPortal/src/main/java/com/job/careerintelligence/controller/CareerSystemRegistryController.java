package com.job.careerintelligence.controller;

import com.job.careerintelligence.dto.CareerSourceDTO;
import com.job.careerintelligence.service.CareerSystemRegistryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/career-intelligence/sources")
@RequiredArgsConstructor
public class CareerSystemRegistryController {

    private final CareerSystemRegistryService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CareerSourceDTO> createSource(@Valid @RequestBody CareerSourceDTO dto) {
        CareerSourceDTO created = service.createSource(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CareerSourceDTO>> getAllSources() {
        return ResponseEntity.ok(service.getAllSources());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CareerSourceDTO> updateSource(@PathVariable Long id, @Valid @RequestBody CareerSourceDTO dto) {
        return ResponseEntity.ok(service.updateSource(id, dto));
    }

    @PatchMapping("/{id}/enabled")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> setEnabled(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        if (!body.containsKey("enabled")) {
            return ResponseEntity.badRequest().build();
        }
        service.setEnabled(id, body.get("enabled"));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSource(@PathVariable Long id) {
        service.deleteSource(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
