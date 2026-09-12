package com.job.careerintelligence;

import com.job.careerintelligence.entity.CandidateSemanticProfile;
import com.job.careerintelligence.entity.JobSemanticEnrichment;
import com.job.careerintelligence.entity.SemanticEmbedding;
import com.job.careerintelligence.repository.CandidateSemanticProfileRepository;
import com.job.careerintelligence.repository.JobSemanticEnrichmentRepository;
import com.job.careerintelligence.service.EmbeddingService;
import com.job.careerintelligence.util.CosineSimilarityCalculator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("postgres")
public class RealDataSemanticTest {

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private CandidateSemanticProfileRepository candidateRepo;

    @Autowired
    private JobSemanticEnrichmentRepository jobRepo;

    @Test
    public void runRealDataTest() {
        System.out.println("=== REAL DATA SEMANTIC TEST ===");
        
        // Find first real candidate
        List<CandidateSemanticProfile> candidates = candidateRepo.findAll();
        if (candidates.isEmpty()) {
            System.out.println("No candidates found in DB. Test skipped.");
            return;
        }
        
        CandidateSemanticProfile candidate = candidates.get(0);
        
        // Embed candidate if missing
        StringBuilder semTextC = new StringBuilder();
        semTextC.append(candidate.getProfessionalTitle()).append(" ");
        semTextC.append(candidate.getCareerLevel()).append(" ");
        semTextC.append(candidate.getSkillsJson()).append(" ");
        SemanticEmbedding candEmb = embeddingService.generateAndSaveEmbedding("CANDIDATE", candidate.getId(), semTextC.toString().trim());

        // Get 3 jobs
        List<JobSemanticEnrichment> jobs = jobRepo.findAll();
        for (int i = 0; i < Math.min(3, jobs.size()); i++) {
            JobSemanticEnrichment job = jobs.get(i);
            
            StringBuilder semTextJ = new StringBuilder();
            semTextJ.append(job.getJobFamily()).append(" ");
            semTextJ.append(job.getCareerLevel()).append(" ");
            semTextJ.append(job.getSkills()).append(" ");
            
            SemanticEmbedding jobEmb = embeddingService.generateAndSaveEmbedding("JOB", job.getId(), semTextJ.toString().trim());
            
            double score = CosineSimilarityCalculator.calculate(candEmb.getVector(), jobEmb.getVector());
            
            System.out.println("Candidate: " + candidate.getProfessionalTitle());
            System.out.println("Job " + i + ": " + job.getJobFamily() + " " + job.getCareerLevel());
            System.out.println("Semantic Similarity Score: " + score);
            System.out.println("-------------------------");
        }
    }
}
