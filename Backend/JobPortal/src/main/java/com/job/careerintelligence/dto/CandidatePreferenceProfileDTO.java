package com.job.careerintelligence.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidatePreferenceProfileDTO {
    private Long candidateId;
    private LocalDateTime lastUpdated;
    private int interactionCount;
    private Map<String, Double> preferredJobFamilies;
    private Map<String, Double> preferredSkills;
    private Map<String, Double> preferredWorkModes;
    private Map<String, Double> preferredLocations;
    private Map<String, Double> preferredCompanies;
    private Set<Long> rejectedJobIds;
    private String summary;
}
