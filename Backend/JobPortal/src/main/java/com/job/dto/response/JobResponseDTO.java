package com.job.dto.response;

import com.job.enums.JobType;
import com.job.enums.WorkMode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class JobResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime postedAt;
    private String profilePicture;
    private String companyName;
    private JobType type;
    private WorkMode workMode;
    private List<String> responsibilities;
    private List<String> requiredSkills;
    private List<String> screeningQuestions;
    private Long employerId;
}