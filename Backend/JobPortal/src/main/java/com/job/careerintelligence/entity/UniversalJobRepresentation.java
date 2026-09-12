package com.job.careerintelligence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "ci_universal_job_representation")
public class UniversalJobRepresentation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_observation_id", nullable = false)
    @ToString.Exclude
    private RawJobObservation rawObservation;

    private String universalTitle;
    private String normalizedLocation;
    private String department;
    private String employmentType;
    private String experience;
    private String salary;
    private String applicationInformation;
    
    private String careerLevel; 
    private String jobFamily;

    @ElementCollection
    @CollectionTable(name = "ci_universal_responsibilities", joinColumns = @JoinColumn(name = "job_id"))
    @Column(columnDefinition = "TEXT")
    private List<String> responsibilities = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "ci_universal_qualifications", joinColumns = @JoinColumn(name = "job_id"))
    @Column(columnDefinition = "TEXT")
    private List<String> qualifications = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "ci_universal_req_skills", joinColumns = @JoinColumn(name = "job_id"))
    @Column(columnDefinition = "TEXT")
    private List<String> requiredSkills = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "ci_universal_pref_skills", joinColumns = @JoinColumn(name = "job_id"))
    @Column(columnDefinition = "TEXT")
    private List<String> preferredSkills = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "ci_universal_education", joinColumns = @JoinColumn(name = "job_id"))
    @Column(columnDefinition = "TEXT")
    private List<String> education = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "ci_universal_benefits", joinColumns = @JoinColumn(name = "job_id"))
    @Column(columnDefinition = "TEXT")
    private List<String> benefits = new ArrayList<>();
}
