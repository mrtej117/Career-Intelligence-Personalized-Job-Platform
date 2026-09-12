package com.job.dto.request;

import com.job.enums.JobType;
import com.job.enums.WorkMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JobRequestDTO {

    @NotBlank(message = "Job title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @NotBlank(message = "Job description is required")
    private String description;

    @NotBlank(message = "Location is required")
    private String location;

    private JobType type;
    private WorkMode workMode;

    private List<String> responsibilities;
    private List<String> requiredSkills;
    private List<String> screeningQuestions;
}