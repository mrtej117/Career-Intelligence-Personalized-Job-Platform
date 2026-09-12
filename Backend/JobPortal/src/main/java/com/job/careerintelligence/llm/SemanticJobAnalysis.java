package com.job.careerintelligence.llm;

import lombok.Data;
import java.util.List;

@Data
public class SemanticJobAnalysis {
    private String careerLevel;
    private String jobFamily;
    private List<String> skills;
    private List<String> preferredSkills;
    private Integer experienceYearsMin;
    private Integer experienceYearsMax;
    private String education;
    private String employmentType;
    private String remoteStatus;
    private List<String> locations;
    private Long salaryMin;
    private Long salaryMax;
    private String salaryCurrency;
    private String semanticSummary;
    private Double confidence;
    private List<String> ambiguousFields;
}
