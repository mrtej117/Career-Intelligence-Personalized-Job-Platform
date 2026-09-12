package com.job.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class JobSeekerProfileDTO {
    private Long id;
    private String username;
    private String name;
    private String email;
    private LocalDate dob;
    private String profilePicture;
    private String resume;
    private String resumeOriginalName;
    private String role;
}