package com.job.careerintelligence.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "ci_candidate_semantic_profile")
@Data
public class CandidateSemanticProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long jobSeekerId;

    @Column(nullable = false)
    private Long resumeObservationId;

    private String modelName;
    private LocalDateTime processingTimestamp;
    private String processingStatus;
    
    @Column(nullable = false)
    private String contentHash;
    
    private Boolean isLatestVersion;

    private String professionalTitle;
    private String careerLevel;
    
    private Integer totalExperienceYears;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String skillsJson; // includes technicalSkills, tools, etc.

    @Lob
    @Column(columnDefinition = "TEXT")
    private String experienceJson; // array of employers, roles, etc.

    @Lob
    @Column(columnDefinition = "TEXT")
    private String educationJson; // degrees, fields, institutions

    @Lob
    @Column(columnDefinition = "TEXT")
    private String projectsJson;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String certificationsJson;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String locationPreferencesJson; // remote, hybrid, onsite, locations
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String ambiguousFieldsJson;
}
