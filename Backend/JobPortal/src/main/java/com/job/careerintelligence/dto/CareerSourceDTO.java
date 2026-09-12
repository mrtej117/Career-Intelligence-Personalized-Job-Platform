package com.job.careerintelligence.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CareerSourceDTO {
    private Long id;

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Source platform is required")
    @Pattern(regexp = "^(Greenhouse|Lever|Ashby|Workday)$", message = "Platform must be Greenhouse, Lever, Ashby, or Workday")
    private String platformProvider;

    @NotBlank(message = "Board token is required")
    private String boardToken;

    private Boolean isEnabled = true;
}
