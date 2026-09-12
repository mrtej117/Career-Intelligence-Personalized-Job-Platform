package com.job.controller;

import com.job.careerintelligence.dto.CandidateActionRequestDTO;
import com.job.careerintelligence.dto.CandidateActionResponseDTO;
import com.job.careerintelligence.dto.CandidatePreferenceProfileDTO;
import com.job.careerintelligence.service.AdaptiveRecommendationService;
import com.job.entity.JobSeeker;
import com.job.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AdaptiveCareerIntelligenceController {

    private final AdaptiveRecommendationService adaptiveService;

    @PreAuthorize("hasRole('JOB_SEEKER')")
    @PostMapping({"/api/career-intelligence/actions", "/recommendations/actions"})
    public ResponseEntity<CandidateActionResponseDTO> recordAction(
            @Valid @RequestBody CandidateActionRequestDTO dto,
            @RequestAttribute("user") User user) {
        JobSeeker seeker = (JobSeeker) user;
        log.info("Candidate {} (id={}) recording action {}", seeker.getUsername(), seeker.getId(), dto.getActionType());
        CandidateActionResponseDTO response = adaptiveService.recordAction(
                seeker.getId(),
                dto.getJobId(),
                dto.getJobEnrichmentId(),
                dto.getActionType(),
                dto.getMetadata()
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('JOB_SEEKER')")
    @GetMapping({"/api/career-intelligence/preferences", "/recommendations/preferences"})
    public ResponseEntity<CandidatePreferenceProfileDTO> getPreferences(
            @RequestAttribute("user") User user) {
        JobSeeker seeker = (JobSeeker) user;
        return ResponseEntity.ok(adaptiveService.getPreferenceProfileDTO(seeker.getId()));
    }
}
