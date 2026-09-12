package com.job.careerintelligence.matching;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.job.careerintelligence.entity.CandidateSemanticProfile;
import com.job.careerintelligence.entity.JobSemanticEnrichment;
import com.job.careerintelligence.entity.JobMatchResult;
import com.job.careerintelligence.repository.CandidateSemanticProfileRepository;
import com.job.careerintelligence.repository.JobSemanticEnrichmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class RealCandidateJobMatchingIntegrationTest {

    @Autowired
    private RealCandidateJobMatchingService realMatchingService;

    @Autowired
    private CandidateSemanticProfileRepository candidateRepo;

    @Autowired
    private JobSemanticEnrichmentRepository jobRepo;

    private final ObjectMapper om = new ObjectMapper();

    @Test
    public void testRealMatchingPipeline() throws Exception {
        // Ensure there is at least one job
        List<JobSemanticEnrichment> jobs = jobRepo.findAll();
        if (jobs.isEmpty()) {
            // Seed a synthetic job if DB is totally empty (though it shouldn't be with the seeder)
            JobSemanticEnrichment job = new JobSemanticEnrichment();
            job.setRawObservationId(9999L);
            job.setProcessingStatus("COMPLETED");
            job.setJobFamily("ENGINEERING");
            job.setCareerLevel("SENIOR");
            job.setExperienceYearsMin(5);
            job.setSkills("[\"java\", \"spring boot\", \"postgresql\"]");
            job.setProcessingTimestamp(LocalDateTime.now());
            jobRepo.save(job);
        }

        // Ensure there is at least one candidate
        List<CandidateSemanticProfile> candidates = candidateRepo.findAll();
        if (candidates.isEmpty()) {
            CandidateSemanticProfile c = new CandidateSemanticProfile();
            c.setJobSeekerId(9999L);
            c.setResumeObservationId(9999L);
            c.setContentHash("real-matching-synthetic-hash");
            c.setProcessingStatus("COMPLETED");
            c.setIsLatestVersion(true);
            c.setProcessingTimestamp(LocalDateTime.now());
            c.setProfessionalTitle("Senior Java Engineer");
            c.setCareerLevel("SENIOR");
            c.setTotalExperienceYears(6);
            c.setSkillsJson("[\"java\", \"spring boot\", \"mysql\", \"docker\"]");
            c.setEducationJson("[{\"degree\":\"B.Tech\",\"fieldOfStudy\":\"Computer Science\"}]");
            c.setLocationPreferencesJson("{\"remotePreference\":\"REMOTE\",\"locations\":[\"India\"]}");
            candidateRepo.save(c);
        }

        // Execute batch matching
        RealCandidateJobMatchingService.BatchMatchResult result = realMatchingService.matchAllCandidatesToAllJobs();
        
        System.out.println("=== REAL MATCHING PIPELINE SUMMARY ===");
        System.out.println("Total Jobs Processed: " + result.getTotalJobsProcessed());
        System.out.println("Successful Matches: " + result.getSuccessfulMatches());
        System.out.println("Failed Matches: " + result.getFailedMatches());
        System.out.println("Total Time (ms): " + result.getTotalTimeMs());
        System.out.println("Average Time (ms): " + result.getAverageTimeMs());
        System.out.println("Fastest Match (ms): " + result.getFastestMatchMs());
        System.out.println("Slowest Match (ms): " + result.getSlowestMatchMs());

        assertTrue(result.getSuccessfulMatches() > 0, "Should have successfully matched at least one candidate-job pair");
        assertEquals(0, result.getFailedMatches(), "There should be zero failed matches");

        // Inspect one High, Medium, or Low match
        for (JobMatchResult match : result.getResults()) {
            System.out.println("\n--- MATCH RESULT [" + match.getOverallScore() + "] CONFIDENCE [" + match.getConfidence() + "] ---");
            System.out.println(match.getExplanation());
        }
    }
}
