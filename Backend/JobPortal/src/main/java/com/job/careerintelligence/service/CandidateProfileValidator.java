package com.job.careerintelligence.service;

import com.job.careerintelligence.llm.SemanticCandidateAnalysis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CandidateProfileValidator {

    private static final List<String> VALID_CAREER_LEVELS = Arrays.asList(
            "INTERNSHIP", "APPRENTICESHIP", "GRADUATE_TRAINEE", "ENTRY_LEVEL",
            "EXPERIENCED", "SENIOR", "LEAD", "MANAGER", "DIRECTOR", "SENIOR_LEADERSHIP"
    );

    private static final List<String> VALID_REMOTE_STATUSES = Arrays.asList(
            "REMOTE", "HYBRID", "ONSITE", "UNKNOWN"
    );

    public SemanticCandidateAnalysis validateAndSanitize(SemanticCandidateAnalysis analysis) {
        if (analysis == null) return null;

        // 1. Validate Career Level
        if (analysis.getCareerLevel() != null) {
            String original = analysis.getCareerLevel().toUpperCase();
            if (!VALID_CAREER_LEVELS.contains(original)) {
                log.warn("Sanitized unsupported candidate career level: {}", original);
                analysis.setCareerLevel(null);
            } else {
                analysis.setCareerLevel(original);
            }
        }

        // 2. Validate Remote Preference
        if (analysis.getRemotePreference() != null) {
            String original = analysis.getRemotePreference().toUpperCase();
            if (!VALID_REMOTE_STATUSES.contains(original)) {
                log.warn("Sanitized unsupported remote preference: {}", original);
                analysis.setRemotePreference("UNKNOWN");
            } else {
                analysis.setRemotePreference(original);
            }
        } else {
            analysis.setRemotePreference("UNKNOWN");
        }

        // 3. Validate Numeric ranges
        if (analysis.getTotalExperienceYears() != null && analysis.getTotalExperienceYears() < 0) {
            log.warn("Sanitized negative experience years: {}", analysis.getTotalExperienceYears());
            analysis.setTotalExperienceYears(0);
        }

        // 4. Array cleanup (remove empty/null strings)
        analysis.setTechnicalSkills(cleanList(analysis.getTechnicalSkills()));
        analysis.setTools(cleanList(analysis.getTools()));
        analysis.setFrameworks(cleanList(analysis.getFrameworks()));
        analysis.setProgrammingLanguages(cleanList(analysis.getProgrammingLanguages()));
        analysis.setSoftSkills(cleanList(analysis.getSoftSkills()));
        analysis.setLocationsMentioned(cleanList(analysis.getLocationsMentioned()));
        analysis.setCertifications(cleanList(analysis.getCertifications()));

        if (analysis.getEmployers() != null) {
            analysis.getEmployers().forEach(emp -> {
                emp.setResponsibilities(cleanList(emp.getResponsibilities()));
            });
        }
        
        if (analysis.getProjects() != null) {
            analysis.getProjects().forEach(proj -> {
                proj.setTechnologies(cleanList(proj.getTechnologies()));
            });
        }

        return analysis;
    }

    private List<String> cleanList(List<String> list) {
        if (list == null) return null;
        return list.stream()
                .filter(s -> s != null && !s.trim().isEmpty())
                .collect(Collectors.toList());
    }
}
