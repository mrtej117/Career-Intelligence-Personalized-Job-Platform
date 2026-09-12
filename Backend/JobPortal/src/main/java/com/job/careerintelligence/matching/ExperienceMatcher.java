package com.job.careerintelligence.matching;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Compares candidate total experience years against job required experience range.
 */
@Component
public class ExperienceMatcher {

    public DimensionResult match(Integer candidateYears, Integer jobMinYears, Integer jobMaxYears) {
        List<String> strengths = new ArrayList<>();
        List<String> gaps = new ArrayList<>();

        // If job has no experience requirement, no penalty
        if (jobMinYears == null && jobMaxYears == null) {
            strengths.add("No specific experience requirement for this role");
            return new DimensionResult("experience", 100.0, strengths, gaps);
        }

        // If candidate experience is unknown
        if (candidateYears == null) {
            gaps.add("Candidate experience is unknown — cannot evaluate");
            return new DimensionResult("experience", 50.0, strengths, gaps);
        }

        int min = jobMinYears != null ? jobMinYears : 0;
        int max = jobMaxYears != null ? jobMaxYears : Integer.MAX_VALUE;

        double score;
        if (candidateYears >= min && candidateYears <= max) {
            score = 100.0;
            strengths.add("Candidate has " + candidateYears + " years; job requires " + min + "+ years — strong match");
        } else if (candidateYears > max) {
            // Over-qualified: slight discount but not a rejection
            score = 85.0;
            strengths.add("Candidate has " + candidateYears + " years — exceeds the " + max + " year upper range (overqualified, still strong)");
        } else {
            // Under-qualified
            double ratio = (double) candidateYears / min;
            score = Math.max(0, ratio * 100.0);
            if (candidateYears == 0) {
                gaps.add("Candidate has no experience; job requires " + min + "+ years");
            } else {
                gaps.add("Candidate has " + candidateYears + " years; job requires " + min + "+ years — " + (min - candidateYears) + " year gap");
            }
        }

        return new DimensionResult("experience", score, strengths, gaps);
    }
}
