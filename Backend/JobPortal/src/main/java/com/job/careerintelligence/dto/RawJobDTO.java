package com.job.careerintelligence.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawJobDTO {
    private String sourcePlatform;
    private String companyName;
    private String externalJobId;
    
    private String sourceJobUrl;
    private String listingUrl;
    
    private String rawTitle;
    private String rawLocation;
    private String rawDepartment;
    private String rawEmploymentType;
    private String rawExperience;
    private String rawQualifications;
    private String rawResponsibilities;
    private String rawSkills;
    
    private String rawJson;
    private String rawHtml;
}
