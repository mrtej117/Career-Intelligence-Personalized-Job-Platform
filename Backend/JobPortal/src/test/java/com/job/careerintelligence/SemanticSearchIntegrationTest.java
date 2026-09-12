package com.job.careerintelligence;

import com.job.careerintelligence.entity.CandidateSemanticProfile;
import com.job.careerintelligence.entity.JobSemanticEnrichment;
import com.job.careerintelligence.repository.CandidateSemanticProfileRepository;
import com.job.careerintelligence.repository.JobSemanticEnrichmentRepository;
import com.job.careerintelligence.service.EmbeddingService;
import com.job.careerintelligence.service.SemanticSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("postgres")
public class SemanticSearchIntegrationTest {

    @Autowired
    private SemanticSearchService semanticSearchService;

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private CandidateSemanticProfileRepository candidateRepo;

    @Autowired
    private JobSemanticEnrichmentRepository jobRepo;

    @Test
    @Transactional
    public void testTopKSemanticSearch() {
        // Setup dummy jobs
        JobSemanticEnrichment job1 = new JobSemanticEnrichment();
        job1.setRawObservationId(System.currentTimeMillis() + 1);
        job1.setProcessingStatus("COMPLETED");
        job1.setJobFamily("ENGINEERING");
        job1 = jobRepo.save(job1);
        embeddingService.generateAndSaveEmbedding("JOB", job1.getId(), "Backend API Services Java Spring");

        JobSemanticEnrichment job2 = new JobSemanticEnrichment();
        job2.setRawObservationId(System.currentTimeMillis() + 2);
        job2.setProcessingStatus("COMPLETED");
        job2.setJobFamily("MARKETING");
        job2 = jobRepo.save(job2);
        embeddingService.generateAndSaveEmbedding("JOB", job2.getId(), "Digital Marketing SEO Content");

        JobSemanticEnrichment job3 = new JobSemanticEnrichment();
        job3.setRawObservationId(System.currentTimeMillis() + 3);
        job3.setProcessingStatus("COMPLETED");
        job3.setJobFamily("ENGINEERING");
        job3 = jobRepo.save(job3);
        embeddingService.generateAndSaveEmbedding("JOB", job3.getId(), "Frontend React Vue Javascript UI");

        // Test search by text
        List<SemanticSearchService.SemanticSearchResult> results = semanticSearchService.searchSimilarJobsByText("Backend developer API REST Java", 2);
        assertEquals(2, results.size());
        
        // Job 1 should be top
        assertEquals(job1.getId(), results.get(0).getJob().getId());
        assertTrue(results.get(0).getSimilarityScore() > results.get(1).getSimilarityScore());
        
        // Setup dummy candidate
        CandidateSemanticProfile candidate = new CandidateSemanticProfile();
        candidate.setJobSeekerId(1L);
        candidate.setResumeObservationId(1L);
        candidate.setProcessingStatus("COMPLETED");
        candidate.setContentHash("dummy-" + System.currentTimeMillis());
        candidate.setIsLatestVersion(true);
        candidate = candidateRepo.save(candidate);
        
        // Candidate wants frontend
        embeddingService.generateAndSaveEmbedding("CANDIDATE", candidate.getId(), "React JS Web Developer UI Frontend");

        // Test search by candidate
        List<SemanticSearchService.SemanticSearchResult> candResults = semanticSearchService.searchSimilarJobs(candidate.getId(), 1);
        assertEquals(1, candResults.size());
        assertEquals(job3.getId(), candResults.get(0).getJob().getId());
    }
}
