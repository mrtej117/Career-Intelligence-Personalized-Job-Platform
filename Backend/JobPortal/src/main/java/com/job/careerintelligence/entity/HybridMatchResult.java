package com.job.careerintelligence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "hybrid_match_result", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"candidateProfileId", "jobEnrichmentId"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HybridMatchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long candidateProfileId;

    @Column(nullable = false)
    private Long jobEnrichmentId;

    @Column(nullable = false)
    private Double deterministicScore;

    @Column(nullable = false)
    private Double semanticScore;

    @Column(nullable = false)
    private Double hybridScore;

    @Column(columnDefinition = "TEXT")
    private String matchedSkills;

    @Column(columnDefinition = "TEXT")
    private String missingSkills;
    
    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Column(columnDefinition = "TEXT")
    private String gaps;

    @Column(nullable = false)
    private String confidence;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime calculatedAt;
}
