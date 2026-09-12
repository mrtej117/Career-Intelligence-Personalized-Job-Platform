package com.job.careerintelligence.dto;

import lombok.Data;
import java.util.List;

@Data
public class RecommendationDTO {
    private Long matchResultId;
    private Long jobEnrichmentId;
    private Long rawObservationId;
    
    // Job details
    private String jobTitle;
    private String companyName;
    private String companyLogoUrl;
    private String location;
    private String employmentType;
    private String workMode;
    private String sourceJobUrl;
    private String description;
    
    // For frontend compatibility
    private Long id; 
    private String title;
    private String profilePicture;
    
    // Match details
    private Double overallScore;
    private String confidence;
    private String shortExplanation;
    
    // Experimental Hybrid Scores
    private Double semanticScore;
    private Double deterministicScore;
    private Double hybridScore;

    // Adaptive Career Intelligence (Stage 6)
    private Double preferenceScore;
    private Double adaptiveScore;
    
    // JSON arrays converted to lists
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private List<String> strengths;
    private List<String> gaps;
}
