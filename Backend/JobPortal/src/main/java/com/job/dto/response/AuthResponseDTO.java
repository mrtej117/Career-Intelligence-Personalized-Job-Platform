package com.job.dto.response;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private String token;
    private String role;
}