package com.job.careerintelligence.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "ci_field_observation_matrix")
public class FieldObservationMatrix {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private String sourceFieldName;
    private String normalizedConceptCandidate;
    private String observedValueType;
    private String mandatoryStatus;
    private String careerLevel;
    private String jobFamily;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
