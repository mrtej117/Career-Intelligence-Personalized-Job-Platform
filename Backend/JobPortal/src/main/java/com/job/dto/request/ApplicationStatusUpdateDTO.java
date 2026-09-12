package com.job.dto.request;

import com.job.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationStatusUpdateDTO {

    @NotNull(message = "Application status is required")
    private ApplicationStatus status;
}