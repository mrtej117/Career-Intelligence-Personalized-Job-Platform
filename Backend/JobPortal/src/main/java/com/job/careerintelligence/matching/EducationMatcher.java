package com.job.careerintelligence.matching;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Education matcher: only penalizes when the job explicitly requires education
 * and the candidate does not have it. No penalty if job has no education requirement.
 */
@Component
public class EducationMatcher {

    public DimensionResult match(String candidateEducationJson, String jobEducation) {
        List<String> strengths = new ArrayList<>();
        List<String> gaps = new ArrayList<>();

        // If job does not require specific education, full score
        if (jobEducation == null || jobEducation.trim().isEmpty() || "null".equalsIgnoreCase(jobEducation.trim())) {
            strengths.add("No specific education requirement for this role");
            return new DimensionResult("education", 100.0, strengths, gaps);
        }

        // If candidate education is unknown or empty
        if (candidateEducationJson == null || candidateEducationJson.trim().isEmpty()
                || "null".equalsIgnoreCase(candidateEducationJson.trim())
                || "[]".equals(candidateEducationJson.trim())) {
            gaps.add("Job requires education (" + jobEducation + ") but candidate education is unknown");
            return new DimensionResult("education", 30.0, strengths, gaps);
        }

        // Simple substring check against the JSON text
        String candLower = candidateEducationJson.toLowerCase();
        String jobLower = jobEducation.toLowerCase();

        // Check for degree-level keywords
        boolean hasDegreeMatch = false;
        String[] degreeKeywords = {"bachelor", "b.tech", "b.sc", "b.e", "master", "m.tech", "m.sc", "mba", "phd", "degree"};
        for (String kw : degreeKeywords) {
            if (jobLower.contains(kw) && candLower.contains(kw)) {
                hasDegreeMatch = true;
                break;
            }
        }

        // Check for field-of-study keywords
        boolean hasFieldMatch = false;
        String[] fieldKeywords = {"computer science", "engineering", "information technology", "mathematics", "physics",
                "finance", "business", "economics", "statistics", "data science"};
        for (String kw : fieldKeywords) {
            if (jobLower.contains(kw) && candLower.contains(kw)) {
                hasFieldMatch = true;
                break;
            }
        }

        double score;
        if (hasDegreeMatch && hasFieldMatch) {
            score = 100.0;
            strengths.add("Education aligns with job requirement: " + jobEducation);
        } else if (hasDegreeMatch) {
            score = 75.0;
            strengths.add("Degree level matches job requirement");
            gaps.add("Field of study may not precisely match: " + jobEducation);
        } else if (hasFieldMatch) {
            score = 60.0;
            strengths.add("Field of study aligns with requirement");
            gaps.add("Degree level may not match: " + jobEducation);
        } else {
            score = 35.0;
            gaps.add("Education does not clearly match requirement: " + jobEducation);
        }

        return new DimensionResult("education", score, strengths, gaps);
    }
}
