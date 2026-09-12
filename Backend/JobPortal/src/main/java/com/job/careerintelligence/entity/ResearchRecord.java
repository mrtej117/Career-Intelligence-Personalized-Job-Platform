package com.job.careerintelligence.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "ci_research_record")
public class ResearchRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    private String industry;
    private String indianCity;
    private String employeeSizeCategory;
    private String officialWebsite;
    private String officialCareersUrl;
    private String jobSearchUrl;
    private String careerPlatform;
    private LocalDateTime dateChecked;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private String researchStatus; // COMPLETED, PENDING, BLOCKED
}
