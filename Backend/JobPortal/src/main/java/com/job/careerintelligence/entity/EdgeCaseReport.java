package com.job.careerintelligence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@Table(name = "ci_edge_case_report")
public class EdgeCaseReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_observation_id", nullable = false)
    @ToString.Exclude
    private RawJobObservation rawObservation;

    private String issueType; // AMBIGUOUS, COMBINED_CONCEPT, SPLIT_CONCEPT, SCHEMA_GAP

    @Column(columnDefinition = "TEXT")
    private String description;
}
