package com.job.careerintelligence.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "ci_sanitization_audit")
@Data
public class SanitizationAuditRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long rawObservationId;
    
    private String modelName;
    private LocalDateTime timestamp;
    
    private String field;
    private String originalValue;
    private String sanitizedValue;
    private String reason;
}
