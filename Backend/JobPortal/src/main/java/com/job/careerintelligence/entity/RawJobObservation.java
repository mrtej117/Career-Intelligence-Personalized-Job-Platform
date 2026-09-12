package com.job.careerintelligence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "ci_raw_job_observation")
public class RawJobObservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identity_id", nullable = false)
    @ToString.Exclude
    private SourceJobIdentity jobIdentity;

    private LocalDateTime collectionTimestamp;
    private String sourceJobUrl;
    private String listingUrl;

    @Column(columnDefinition = "TEXT")
    private String rawTitle;

    @Column(columnDefinition = "TEXT")
    private String rawLocation;

    @Column(columnDefinition = "TEXT")
    private String rawDepartment;

    @Column(columnDefinition = "TEXT")
    private String rawEmploymentType;

    @Column(columnDefinition = "TEXT")
    private String rawExperience;
    
    @Column(columnDefinition = "TEXT")
    private String rawQualifications;
    
    @Column(columnDefinition = "TEXT")
    private String rawResponsibilities;
    
    @Column(columnDefinition = "TEXT")
    private String rawSkills;

    @Lob
    private String rawJson;

    @Lob
    private String rawHtml;

    private String contentHash;
}
