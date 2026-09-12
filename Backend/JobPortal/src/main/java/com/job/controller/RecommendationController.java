package com.job.controller;

import com.job.careerintelligence.dto.RecommendationDTO;
import com.job.careerintelligence.service.PersonalizedRecommendationService;
import com.job.entity.User;
import com.job.entity.JobSeeker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final PersonalizedRecommendationService recommendationService;

    @PreAuthorize("hasRole('JOB_SEEKER')")
    @GetMapping
    public ResponseEntity<Page<RecommendationDTO>> getRecommendations(
            @RequestAttribute("user") User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        JobSeeker seeker = (JobSeeker) user;
        Page<RecommendationDTO> recs = recommendationService.getRecommendationsForSeeker(seeker.getId(), PageRequest.of(page, size));
        return ResponseEntity.ok(recs);
    }
}
