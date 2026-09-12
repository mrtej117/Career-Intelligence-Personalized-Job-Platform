package com.job.dto.response;

import com.job.enums.ApplicationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApplicationResponseDTO {
    private Long applicationId;
    private Long jobId;
    private String username;
    private String resumeUrl;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private String jobTitle;
    private String jobDescription;
    private String jobType;
    private String workMode;
    private String location;
    private String companyName;
    private String companyLogoUrl;
}
