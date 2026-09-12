package com.job.seed;

import com.job.enums.JobType;
import com.job.enums.WorkMode;

import java.util.List;

/**
 * Immutable definition of a single demo job for seeding.
 * Used by {@link SeedJobDataProvider} to define job data
 * and by {@link DemoDataSeeder} to create Job entities.
 */
public record SeedJobDefinition(
        String title,
        String description,
        String location,
        JobType type,
        WorkMode workMode,
        List<String> responsibilities,
        List<String> requiredSkills,
        List<String> screeningQuestions,
        int daysAgo
) {}
