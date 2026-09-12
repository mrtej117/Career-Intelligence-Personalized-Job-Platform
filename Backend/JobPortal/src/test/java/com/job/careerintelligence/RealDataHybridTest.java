package com.job.careerintelligence;

import com.job.careerintelligence.entity.CandidateSemanticProfile;
import com.job.careerintelligence.entity.HybridMatchResult;
import com.job.careerintelligence.entity.JobSemanticEnrichment;
import com.job.careerintelligence.repository.CandidateSemanticProfileRepository;
import com.job.careerintelligence.repository.HybridMatchResultRepository;
import com.job.careerintelligence.repository.JobSemanticEnrichmentRepository;
import com.job.careerintelligence.service.HybridRecommendationService;
import com.job.careerintelligence.service.SemanticSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@SpringBootTest
@ActiveProfiles("postgres")
public class RealDataHybridTest {

    @Autowired
    private CandidateSemanticProfileRepository candidateRepo;

    @Autowired
    private JobSemanticEnrichmentRepository jobRepo;

    @Autowired
    private HybridMatchResultRepository hybridRepo;

    @Autowired
    private SemanticSearchService semanticSearchService;

    @Autowired
    private HybridRecommendationService hybridService;

    @Test
    public void runRealDataHybridAndSearchTest() {
        System.out.println("\n=== REAL DATA SEMANTIC SEARCH AND HYBRID TEST ===\n");

        List<CandidateSemanticProfile> candidates = candidateRepo.findAll();
        if (candidates.isEmpty()) return;

        CandidateSemanticProfile candidate = candidates.get(0);
        System.out.println("Candidate Profile: " + candidate.getProfessionalTitle() + " | Skills: " + candidate.getSkillsJson());

        List<JobSemanticEnrichment> jobs = jobRepo.findAll();
        for (JobSemanticEnrichment job : jobs) {
            hybridService.calculateAndStoreHybridScore(candidate.getId(), job.getId());
        }

        List<HybridMatchResult> allResults = hybridRepo.findAll().stream()
                .filter(r -> r.getCandidateProfileId().equals(candidate.getId()))
                .toList();

        System.out.println("\n--- RANKING A: DETERMINISTIC ---");
        allResults.stream()
                .sorted(Comparator.comparingDouble(HybridMatchResult::getDeterministicScore).reversed())
                .limit(5)
                .forEach(r -> printResult(r, "Deterministic"));

        System.out.println("\n--- RANKING B: SEMANTIC SIMILARITY ---");
        allResults.stream()
                .sorted(Comparator.comparingDouble(HybridMatchResult::getSemanticScore).reversed())
                .limit(5)
                .forEach(r -> printResult(r, "Semantic"));

        System.out.println("\n--- RANKING C: HYBRID (80/20) ---");
        allResults.stream()
                .sorted(Comparator.comparingDouble(HybridMatchResult::getHybridScore).reversed())
                .limit(5)
                .forEach(r -> printResult(r, "Hybrid"));

        System.out.println("\n--- SEMANTIC SEARCH PERFORMANCE TEST ---");
        long startSearch = System.currentTimeMillis();
        List<SemanticSearchService.SemanticSearchResult> searchResults = semanticSearchService.searchSimilarJobs(candidate.getId(), 5);
        long endSearch = System.currentTimeMillis();
        System.out.println("Semantic search over " + jobs.size() + " jobs took: " + (endSearch - startSearch) + "ms");
        
        System.out.println("Top search result: " + searchResults.get(0).getJob().getJobFamily() + " (Score: " + searchResults.get(0).getSimilarityScore() + ")");
    }

    private void printResult(HybridMatchResult r, String type) {
        JobSemanticEnrichment job = jobRepo.findById(r.getJobEnrichmentId()).orElse(null);
        if (job != null) {
            System.out.printf("[%s] Job: %s | Det: %.2f | Sem: %.2f | Hybrid: %.2f%n",
                    type, job.getJobFamily(), r.getDeterministicScore(), r.getSemanticScore(), r.getHybridScore());
        }
    }
}
