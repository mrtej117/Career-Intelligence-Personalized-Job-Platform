package com.job.careerintelligence.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "ci_career_system_registry")
public class CareerSystemRegistry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    private String platformProvider;
    private String careersEntryUrl;
    private String jobListingUrlPattern;
    private String jobDetailUrlPattern;
    
    private String searchBehavior;
    private String filteringBehavior;
    private String paginationBehavior;
    private String urlBehavior;
    
    private Boolean hasStableIdentifier;
    private Boolean isServerRendered;
    private Boolean observablePublicRequests;
    
    @Column(columnDefinition = "TEXT")
    private String unusualBehavior;
    private String boardToken;
    private Boolean isEnabled = true;
    
    private LocalDateTime dateObserved;
}
