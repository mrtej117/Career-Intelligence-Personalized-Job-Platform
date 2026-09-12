package com.job.careerintelligence.dto;

import com.job.careerintelligence.entity.CandidateActionType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateActionResponseDTO {
    private Long actionId;
    private Long candidateId;
    private Long jobId;
    private Long jobEnrichmentId;
    private CandidateActionType actionType;
    private LocalDateTime timestamp;
    private int updatedInteractionCount;
    private String summary;
}
