package com.job.dto.response;

import lombok.Data;

@Data
public class EmployerProfileDTO {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String companyName;
    private String industry;
    private String profilePicture;
    private String role;
}