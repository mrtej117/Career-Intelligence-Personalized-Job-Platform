package com.job.careerintelligence.matching;

import lombok.Data;
import java.util.List;

/**
 * Holds the result of a single dimension comparison with a 0-100 score.
 */
@Data
public class DimensionResult {
    private final String dimension;
    private final double score;
    private final List<String> strengths;
    private final List<String> gaps;

    public DimensionResult(String dimension, double score, List<String> strengths, List<String> gaps) {
        this.dimension = dimension;
        this.score = Math.max(0, Math.min(100, score));
        this.strengths = strengths;
        this.gaps = gaps;
    }
}
