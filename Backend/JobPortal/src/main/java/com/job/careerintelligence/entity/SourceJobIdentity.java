package com.job.careerintelligence.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "ci_source_job_identity", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"sourcePlatform", "companyName", "externalJobId"})
})
public class SourceJobIdentity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sourcePlatform;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String externalJobId;

    private LocalDateTime firstSeenAt;
    private LocalDateTime lastSeenAt;
    private Boolean isActive;
    
    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer missingCyclesCount = 0;

    @OneToMany(mappedBy = "jobIdentity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RawJobObservation> observations = new ArrayList<>();
}
