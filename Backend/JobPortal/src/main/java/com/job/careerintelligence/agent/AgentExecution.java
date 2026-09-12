package com.job.careerintelligence.agent;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "agent_execution")
@Data
public class AgentExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String executionId;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgentState currentState;

    @Column(nullable = false)
    private String status; // RUNNING, COMPLETED, FAILED

    private Integer iterationCount = 0;

    private String source;

    @Enumerated(EnumType.STRING)
    private AgentAction lastAction;

    @Column(columnDefinition = "TEXT")
    private String decisionReason;

    @Column(columnDefinition = "TEXT")
    private String resultSummary;

    @Column(columnDefinition = "TEXT")
    private String error;

    private String modelUsed;

    @Column(columnDefinition = "TEXT")
    private String observationSummary;

    @Column(columnDefinition = "TEXT")
    private String decisionSummary;

    @Column(columnDefinition = "TEXT")
    private String verificationSummary;

    private Integer jobsNewlyEnriched = 0;
    private Integer jobsEmbedded = 0;
    private Integer candidatesMatched = 0;

    @Column(columnDefinition = "TEXT")
    private String terminationReason;
}
