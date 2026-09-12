package com.job.careerintelligence.matching;

import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Career level compatibility matrix.
 * Adjacent levels score highly; distant levels score poorly.
 * This is NOT a rejection gate — it only influences scoring.
 */
@Component
public class CareerLevelMatcher {

    private static final List<String> LEVEL_LADDER = Arrays.asList(
            "INTERNSHIP", "APPRENTICESHIP", "GRADUATE_TRAINEE", "ENTRY_LEVEL",
            "EXPERIENCED", "SENIOR", "LEAD", "MANAGER", "DIRECTOR", "SENIOR_LEADERSHIP"
    );

    public DimensionResult match(String candidateLevel, String jobLevel) {
        List<String> strengths = new ArrayList<>();
        List<String> gaps = new ArrayList<>();

        if (candidateLevel == null || jobLevel == null) {
            String reason = (candidateLevel == null ? "Candidate" : "Job") + " career level is unknown";
            gaps.add(reason);
            return new DimensionResult("careerLevel", 50.0, strengths, gaps);
        }

        int candidateIdx = LEVEL_LADDER.indexOf(candidateLevel.toUpperCase());
        int jobIdx = LEVEL_LADDER.indexOf(jobLevel.toUpperCase());

        if (candidateIdx == -1 || jobIdx == -1) {
            gaps.add("Unrecognized career level: candidate=" + candidateLevel + ", job=" + jobLevel);
            return new DimensionResult("careerLevel", 40.0, strengths, gaps);
        }

        int distance = Math.abs(candidateIdx - jobIdx);
        double score;

        if (distance == 0) {
            score = 100.0;
            strengths.add("Career levels align perfectly: " + candidateLevel);
        } else if (distance == 1) {
            score = 85.0;
            strengths.add("Career levels are adjacent (" + candidateLevel + " vs " + jobLevel + ") — reasonable alignment");
        } else if (distance == 2) {
            score = 60.0;
            gaps.add("Career level gap of 2 steps: " + candidateLevel + " vs " + jobLevel);
        } else {
            score = Math.max(0, 100.0 - (distance * 20.0));
            gaps.add("Significant career level mismatch: " + candidateLevel + " vs " + jobLevel + " (" + distance + " steps apart)");
        }

        return new DimensionResult("careerLevel", score, strengths, gaps);
    }
}
