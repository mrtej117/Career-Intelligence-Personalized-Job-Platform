package com.job.careerintelligence.matching;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Compares candidate skills against job required and preferred skills.
 * Required skills carry higher weight than preferred skills.
 */
@Component
@RequiredArgsConstructor
public class SkillMatcher {

    private final SkillNormalizer normalizer;

    // 80% weight for required skills, 20% for preferred skills
    private static final double REQUIRED_WEIGHT = 0.80;
    private static final double PREFERRED_WEIGHT = 0.20;

    public SkillMatchResult match(List<String> candidateSkills, List<String> jobRequiredSkills, List<String> jobPreferredSkills) {
        Set<String> candidateNorm = normalizer.normalizeAll(candidateSkills);
        Set<String> requiredNorm = normalizer.normalizeAll(jobRequiredSkills);
        Set<String> preferredNorm = normalizer.normalizeAll(jobPreferredSkills);

        // Required skill matching
        Set<String> matchedRequired = new LinkedHashSet<>(candidateNorm);
        matchedRequired.retainAll(requiredNorm);

        Set<String> missingRequired = new LinkedHashSet<>(requiredNorm);
        missingRequired.removeAll(candidateNorm);

        // Preferred skill matching
        Set<String> matchedPreferred = new LinkedHashSet<>(candidateNorm);
        matchedPreferred.retainAll(preferredNorm);

        // Calculate scores
        double requiredScore = requiredNorm.isEmpty() ? 100.0 : (matchedRequired.size() * 100.0 / requiredNorm.size());
        double preferredScore = preferredNorm.isEmpty() ? 100.0 : (matchedPreferred.size() * 100.0 / preferredNorm.size());

        double combinedScore;
        if (requiredNorm.isEmpty() && preferredNorm.isEmpty()) {
            combinedScore = 50.0; // No skill data available
        } else if (requiredNorm.isEmpty()) {
            combinedScore = preferredScore;
        } else if (preferredNorm.isEmpty()) {
            combinedScore = requiredScore;
        } else {
            combinedScore = (requiredScore * REQUIRED_WEIGHT) + (preferredScore * PREFERRED_WEIGHT);
        }

        // Build strengths/gaps
        List<String> strengths = new ArrayList<>();
        if (!matchedRequired.isEmpty()) {
            strengths.add(matchedRequired.size() + "/" + requiredNorm.size() + " required skills matched: " + String.join(", ", matchedRequired));
        }
        if (!matchedPreferred.isEmpty()) {
            strengths.add(matchedPreferred.size() + "/" + preferredNorm.size() + " preferred skills matched: " + String.join(", ", matchedPreferred));
        }

        List<String> gaps = new ArrayList<>();
        if (!missingRequired.isEmpty()) {
            gaps.add("Missing required skills: " + String.join(", ", missingRequired));
        }

        return new SkillMatchResult(combinedScore, new ArrayList<>(matchedRequired), new ArrayList<>(missingRequired),
                new ArrayList<>(matchedPreferred), strengths, gaps);
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    public static class SkillMatchResult {
        private double score;
        private List<String> matchedRequired;
        private List<String> missingRequired;
        private List<String> matchedPreferred;
        private List<String> strengths;
        private List<String> gaps;
    }
}
