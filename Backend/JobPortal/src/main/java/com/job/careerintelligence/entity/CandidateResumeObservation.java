package com.job.careerintelligence.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "ci_candidate_resume_observation")
@Data
public class CandidateResumeObservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long jobSeekerId;

    @Column(nullable = false)
    private String sourceResumeReference;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String extractedText;

    private LocalDateTime uploadedAt;
    private LocalDateTime processedAt;
    
    @Column(nullable = false)
    private String contentHash;

    private String processingStatus;
}
