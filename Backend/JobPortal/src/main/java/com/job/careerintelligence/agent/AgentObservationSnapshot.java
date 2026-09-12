package com.job.careerintelligence.agent;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class AgentObservationSnapshot {
    private int activeJobsCount;
    private int activeCandidatesCount;

    // Jobs needing work
    private List<Long> jobsToEnrich = new ArrayList<>();
    private List<Long> jobsToEmbed = new ArrayList<>();
    private List<Long> jobsToMatch = new ArrayList<>();

    // Candidates needing work
    private List<Long> candidatesToProfile = new ArrayList<>();
    private List<Long> candidatesToEmbed = new ArrayList<>();
    private List<Long> candidatesToMatch = new ArrayList<>();

    // System Health
    private boolean ollamaAvailable;
    private boolean postgresAvailable;

    public boolean hasAnyWork() {
        return !jobsToEnrich.isEmpty() || !jobsToEmbed.isEmpty() || !jobsToMatch.isEmpty() ||
               !candidatesToProfile.isEmpty() || !candidatesToEmbed.isEmpty() || !candidatesToMatch.isEmpty();
    }
}
