package com.job.careerintelligence;

import com.job.careerintelligence.entity.CandidateSemanticProfile;
import com.job.careerintelligence.entity.HybridMatchResult;
import com.job.careerintelligence.entity.JobSemanticEnrichment;
import com.job.careerintelligence.matching.RealCandidateJobMatchingService;
import com.job.careerintelligence.repository.CandidateSemanticProfileRepository;
import com.job.careerintelligence.repository.HybridMatchResultRepository;
import com.job.careerintelligence.repository.JobSemanticEnrichmentRepository;
import com.job.careerintelligence.service.EmbeddingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("postgres")
public class HybridRecommendationIntegrationTest {

    @Autowired
    private RealCandidateJobMatchingService matchingService;

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private CandidateSemanticProfileRepository candidateRepo;

    @Autowired
    private JobSemanticEnrichmentRepository jobRepo;

    @Autowired
    private HybridMatchResultRepository hybridRepo;

    @Test
    @Transactional
    public void testHybridRecommendationCalculation() {
        // Dummy job
        JobSemanticEnrichment job = new JobSemanticEnrichment();
        job.setRawObservationId(System.currentTimeMillis() + 10);
        job.setProcessingStatus("COMPLETED");
        job.setJobFamily("ENGINEERING");
        job.setSkills("[\"Java\",\"Spring\"]");
        job = jobRepo.save(job);
        embeddingService.generateAndSaveEmbedding("JOB", job.getId(), "Backend API Services Java Spring");

        // Dummy candidate
        CandidateSemanticProfile candidate = new CandidateSemanticProfile();
        candidate.setJobSeekerId(1L);
        candidate.setResumeObservationId(1L);
        candidate.setProcessingStatus("COMPLETED");
        candidate.setContentHash("dummy-" + System.currentTimeMillis());
        candidate.setIsLatestVersion(true);
        candidate.setSkillsJson("[\"Java\",\"Spring\"]");
        candidate = candidateRepo.save(candidate);
        embeddingService.generateAndSaveEmbedding("CANDIDATE", candidate.getId(), "Backend Developer Java Spring API");

        // Trigger match
        matchingService.matchSpecificJobsToAllCandidates(List.of(job));

        // Verify hybrid result
        Optional<HybridMatchResult> hybridOpt = hybridRepo.findByCandidateProfileIdAndJobEnrichmentId(candidate.getId(), job.getId());
        assertTrue(hybridOpt.isPresent(), "HybridMatchResult should be saved");
        HybridMatchResult result = hybridOpt.get();

        assertNotNull(result.getDeterministicScore());
        assertNotNull(result.getSemanticScore());
        assertNotNull(result.getHybridScore());

        // Validate hybrid bounds and calculation
        double expectedHybrid = (result.getDeterministicScore() * 0.8) + (result.getSemanticScore() * 0.2);
        assertEquals(expectedHybrid, result.getHybridScore(), 0.01, "Hybrid score should be correctly weighted");
        assertTrue(result.getHybridScore() >= 0 && result.getHybridScore() <= 100);
        assertTrue(result.getSemanticScore() > 90, "Similar vectors should have high semantic score");
    }
}
