package com.job.careerintelligence.dto;

import com.job.careerintelligence.entity.CandidateActionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateActionRequestDTO {

    private Long jobId;

    private Long jobEnrichmentId;

    @NotNull(message = "actionType is required")
    private CandidateActionType actionType;

    private String metadata;
}
