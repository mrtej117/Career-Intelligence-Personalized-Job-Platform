package com.job.dto.response;

import com.job.enums.ApplicationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ApplicationViewForEmployerDTO {
    private long id;
    private String applicantUsername;
    private String applicantEmail;
    private LocalDate applicantDOB;
    private String applicantName;
    private String applicantProfilePicture;
    private String resumeUrl;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private String jobTitle;
}