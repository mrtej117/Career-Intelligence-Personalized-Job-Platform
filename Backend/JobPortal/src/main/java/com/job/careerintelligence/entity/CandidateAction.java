package com.job.careerintelligence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "ci_candidate_action", indexes = {
    @Index(name = "idx_ca_candidate_time", columnList = "candidateId, timestamp")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long candidateId;

    private Long jobId;

    private Long jobEnrichmentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CandidateActionType actionType;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private String jobFamily;

    private String companyName;

    private String workMode;

    private String location;

    @Column(columnDefinition = "TEXT")
    private String skillsJson;

    @Column(columnDefinition = "TEXT")
    private String metadataJson;
}
