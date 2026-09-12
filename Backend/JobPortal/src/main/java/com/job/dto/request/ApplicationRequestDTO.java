package com.job.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApplicationRequestDTO {

    @NotNull(message = "Job ID is required")
    private Long jobId;

    private List<String> screeningAnswers;
}