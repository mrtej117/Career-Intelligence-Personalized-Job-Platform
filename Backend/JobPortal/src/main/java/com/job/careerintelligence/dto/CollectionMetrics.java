package com.job.careerintelligence.dto;

import lombok.Data;

@Data
public class CollectionMetrics {
    private int sourcesChecked;
    private int companiesAttempted;
    private int companiesSucceeded;
    private int companiesFailed;
    private int jobsDiscovered;
    private int jobsAccepted;
    private int jobsSkippedByLocation;
    private int newJobs;
    private int changedJobs;
    private int unchangedJobs;
    private int inactiveJobs;
    private long durationMs;
    
    public void add(CollectionMetrics other) {
        this.sourcesChecked += other.sourcesChecked;
        this.companiesAttempted += other.companiesAttempted;
        this.companiesSucceeded += other.companiesSucceeded;
        this.companiesFailed += other.companiesFailed;
        this.jobsDiscovered += other.jobsDiscovered;
        this.jobsAccepted += other.jobsAccepted;
        this.jobsSkippedByLocation += other.jobsSkippedByLocation;
        this.newJobs += other.newJobs;
        this.changedJobs += other.changedJobs;
        this.unchangedJobs += other.unchangedJobs;
        this.inactiveJobs += other.inactiveJobs;
        this.durationMs += other.durationMs;
    }
}
