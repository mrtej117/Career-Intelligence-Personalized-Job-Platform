package com.job.careerintelligence.llm;

import lombok.Data;
import java.util.List;

@Data
public class SemanticCandidateAnalysis {
    private String professionalTitle;
    private String careerLevel; // Must match enum string
    private Integer totalExperienceYears;

    private List<String> technicalSkills;
    private List<String> tools;
    private List<String> frameworks;
    private List<String> programmingLanguages;
    private List<String> softSkills;

    private List<EmployerInfo> employers;
    private List<EducationInfo> education;
    private List<ProjectInfo> projects;
    private List<String> certifications;

    private String remotePreference; // REMOTE, HYBRID, ONSITE, UNKNOWN
    private List<String> locationsMentioned;
    private List<String> ambiguousFields;

    @Data
    public static class EmployerInfo {
        private String name;
        private String role;
        private List<String> responsibilities;
        private String industry;
    }

    @Data
    public static class EducationInfo {
        private String degree;
        private String fieldOfStudy;
        private String institution;
        private String graduationYear;
    }

    @Data
    public static class ProjectInfo {
        private String projectName;
        private String projectDescription;
        private List<String> technologies;
    }
}
