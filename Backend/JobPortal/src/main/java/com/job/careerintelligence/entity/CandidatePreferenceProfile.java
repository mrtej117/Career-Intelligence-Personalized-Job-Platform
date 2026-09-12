package com.job.careerintelligence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "ci_candidate_preference_profile", indexes = {
    @Index(name = "idx_cpp_candidate_id", columnList = "candidateId", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidatePreferenceProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long candidateId;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    @Column(nullable = false)
    private int interactionCount = 0;

    @Column(columnDefinition = "TEXT")
    private String preferredJobFamiliesJson;

    @Column(columnDefinition = "TEXT")
    private String preferredSkillsJson;

    @Column(columnDefinition = "TEXT")
    private String preferredWorkModesJson;

    @Column(columnDefinition = "TEXT")
    private String preferredLocationsJson;

    @Column(columnDefinition = "TEXT")
    private String preferredCompaniesJson;

    @Column(columnDefinition = "TEXT")
    private String rejectedJobIdsJson;

    @Column(columnDefinition = "TEXT")
    private String summary;
}
