package com.job.careerintelligence.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "ci_job_semantic_enrichment")
@Data
public class JobSemanticEnrichment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "raw_observation_id", unique = true, nullable = false)
    private Long rawObservationId;

    private String modelName;
    private String modelVersion;
    private LocalDateTime processingTimestamp;

    private String processingStatus; // PENDING, PROCESSING, COMPLETED, FAILED

    @Column(columnDefinition = "TEXT")
    private String promptIdentifier;

    private String careerLevel;
    private String jobFamily;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(columnDefinition = "TEXT")
    private String preferredSkills;

    private Integer experienceYearsMin;
    private Integer experienceYearsMax;

    private String education;
    private String employmentType;
    private String remoteStatus;

    @Column(columnDefinition = "TEXT")
    private String locations;

    private Long salaryMin;
    private Long salaryMax;
    private String salaryCurrency;

    @Column(columnDefinition = "TEXT")
    private String semanticSummary;

    private Double confidence;

    @Column(columnDefinition = "TEXT")
    private String ambiguousFields;
}
