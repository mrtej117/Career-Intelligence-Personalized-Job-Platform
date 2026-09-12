package com.job.careerintelligence.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "ci_job_match_result")
@Data
public class JobMatchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long candidateProfileId;

    @Column(nullable = false)
    private Long jobEnrichmentId;

    private Double overallScore;
    private Double skillScore;
    private Double experienceScore;
    private Double careerLevelScore;
    private Double jobFamilyScore;
    private Double educationScore;
    private Double locationScore;
    private Double workModeScore;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String matchedSkillsJson;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String missingSkillsJson;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String preferredSkillsMatchedJson;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String strengthsJson;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String gapsJson;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String explanation;

    private String confidence; // HIGH, MEDIUM, LOW
    private String scoringVersion;

    private LocalDateTime calculatedAt;
}
