package com.job.careerintelligence;

import com.job.careerintelligence.entity.CandidateSemanticProfile;
import com.job.careerintelligence.entity.JobSemanticEnrichment;
import com.job.careerintelligence.entity.SemanticEmbedding;
import com.job.careerintelligence.entity.SemanticSimilarityResult;
import com.job.careerintelligence.matching.RealCandidateJobMatchingService;
import com.job.careerintelligence.repository.CandidateSemanticProfileRepository;
import com.job.careerintelligence.repository.JobSemanticEnrichmentRepository;
import com.job.careerintelligence.repository.SemanticEmbeddingRepository;
import com.job.careerintelligence.repository.SemanticSimilarityResultRepository;
import com.job.careerintelligence.service.EmbeddingService;
import com.job.careerintelligence.util.CosineSimilarityCalculator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("postgres")
public class SemanticEmbeddingsIntegrationTest {

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private SemanticEmbeddingRepository embeddingRepo;

    @Autowired
    private SemanticSimilarityResultRepository simRepo;

    @Autowired
    private CandidateSemanticProfileRepository candidateRepo;

    @Autowired
    private JobSemanticEnrichmentRepository jobRepo;

    @Autowired
    private RealCandidateJobMatchingService matchingService;

    @Test
    public void testCosineSimilarityMath() {
        // Identical vectors
        double[] v1 = {1.0, 0.0, 0.5};
        double[] v2 = {1.0, 0.0, 0.5};
        double scoreIdentical = CosineSimilarityCalculator.calculate(v1, v2);
        assertTrue(scoreIdentical > 99.9, "Identical vectors must be ~100");

        // Orthogonal vectors
        double[] v3 = {1.0, 0.0, 0.0};
        double[] v4 = {0.0, 1.0, 0.0};
        double scoreOrthogonal = CosineSimilarityCalculator.calculate(v3, v4);
        assertTrue(scoreOrthogonal < 0.1, "Orthogonal vectors must be ~0");

        // Similar vectors
        double[] v5 = {1.0, 0.1, 0.0};
        double[] v6 = {1.0, 0.2, 0.0};
        double scoreSimilar = CosineSimilarityCalculator.calculate(v5, v6);
        assertTrue(scoreSimilar > 90.0 && scoreSimilar < 100.0, "Similar vectors must be high but not 100");
        
        // Zero vectors
        double[] v7 = {0.0, 0.0};
        double scoreZero = CosineSimilarityCalculator.calculate(v7, v7);
        assertEquals(0.0, scoreZero, "Zero vector must yield 0");
    }

    @Test
    @Transactional
    public void testEmbeddingGenerationAndReuse() {
        String testText = "Software Engineer React.js " + System.currentTimeMillis();

        // 1. Generate new embedding
        SemanticEmbedding emb1 = embeddingService.generateAndSaveEmbedding("JOB", 99999L, testText);
        assertNotNull(emb1);
        assertEquals("nomic-embed-text", emb1.getModelName());
        assertTrue(emb1.getDimensions() > 0);
        assertNotNull(emb1.getVector());
        assertEquals(emb1.getDimensions(), emb1.getVector().length);
        
        String originalHash = emb1.getSourceHash();

        // 2. Generate exactly same text, should reuse hash completely
        SemanticEmbedding emb2 = embeddingService.generateAndSaveEmbedding("JOB", 99999L, testText);
        assertEquals(emb1.getId(), emb2.getId(), "Must reuse existing embedding exactly for same entity and hash");

        // 3. Generate changed text for same entity
        SemanticEmbedding emb3 = embeddingService.generateAndSaveEmbedding("JOB", 99999L, testText + " v2");
        assertEquals(emb1.getId(), emb3.getId(), "Must update same SemanticEmbedding row for same entity");
        assertNotEquals(originalHash, emb3.getSourceHash(), "Must update hash");
        
        // 4. Generate same original text for DIFFERENT entity, should reuse vector but create new row
        SemanticEmbedding emb4 = embeddingService.generateAndSaveEmbedding("JOB", 88888L, testText);
        assertNotEquals(emb1.getId(), emb4.getId(), "Must create new row for different entity");
        assertEquals(originalHash, emb4.getSourceHash(), "Must have same hash");
    }

    @Test
    @Transactional
    public void testSimilarityCalculationIntegration() {
        // Setup dummy candidate
        CandidateSemanticProfile candidate = new CandidateSemanticProfile();
        candidate.setJobSeekerId(1L);
        candidate.setResumeObservationId(1L);
        candidate.setProcessingStatus("COMPLETED");
        candidate.setIsLatestVersion(true);
        candidate.setContentHash("dummy-hash-" + System.currentTimeMillis());
        candidate.setProfessionalTitle("Software Engineer");
        candidate = candidateRepo.save(candidate);
        embeddingService.generateAndSaveEmbedding("CANDIDATE", candidate.getId(), "Software Engineer Java Spring");

        // Setup dummy job
        JobSemanticEnrichment job = new JobSemanticEnrichment();
        job.setRawObservationId(System.currentTimeMillis());
        job.setProcessingStatus("COMPLETED");
        job.setJobFamily("ENGINEERING");
        job = jobRepo.save(job);
        embeddingService.generateAndSaveEmbedding("JOB", job.getId(), "Backend Developer Java Spring");

        // Trigger the matching orchestrator
        matchingService.matchSpecificJobsToAllCandidates(List.of(job));

        // Verify SemanticSimilarityResult was stored
        Optional<SemanticSimilarityResult> resultOpt = simRepo.findByCandidateProfileIdAndJobEnrichmentIdAndModelName(candidate.getId(), job.getId(), "nomic-embed-text");
        assertTrue(resultOpt.isPresent(), "SemanticSimilarityResult must be saved independently");
        
        SemanticSimilarityResult result = resultOpt.get();
        assertNotNull(result.getSimilarityScore());
        assertTrue(result.getSimilarityScore() > 0 && result.getSimilarityScore() <= 100);
    }
}
