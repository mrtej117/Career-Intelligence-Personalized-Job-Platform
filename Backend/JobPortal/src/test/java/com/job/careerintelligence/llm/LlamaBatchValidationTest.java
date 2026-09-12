package com.job.careerintelligence.llm;

import com.job.careerintelligence.entity.*;
import com.job.careerintelligence.repository.*;
import com.job.careerintelligence.service.DataCollectionService;
import com.job.careerintelligence.service.SemanticEnrichmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LlamaBatchValidationTest {

    @Autowired
    private DataCollectionService dataCollectionService;

    @Autowired
    private SemanticEnrichmentService enrichmentService;

    @Autowired
    private JobSemanticEnrichmentRepository enrichmentRepo;

    @Autowired
    private RawJobObservationRepository observationRepo;

    @Autowired
    private UniversalJobRepresentationRepository universalRepo;

    @Autowired
    private CareerSystemRegistryRepository registryRepo;
    
    @Autowired
    private ResearchRecordRepository researchRepo;

    @Autowired
    private LocalLlmClient llmClient;

    @Test
    @Transactional
    public void testLlamaBatchValidation() {
        if (!llmClient.isModelAvailable()) {
            System.out.println("Ollama or Llama 3.2 is not available. Skipping test execution.");
            return;
        }

        // Setup companies to get a diverse sample of jobs
        setupCompany("Stripe", "Fintech", "Bengaluru", "Greenhouse");
        setupCompany("Twilio", "Telecom", "Bengaluru", "Greenhouse");
        setupCompany("Inmobi", "AdTech", "Bengaluru", "Greenhouse");
        setupCompany("freshworks", "SaaS", "Chennai", "Lever");
        setupCompany("paytm", "Fintech", "Noida", "Lever");
        
        dataCollectionService.runCollectionCycle();

        List<RawJobObservation> jobs = observationRepo.findAll();
        System.out.println("Total collected jobs: " + jobs.size());
        
        // Take a batch of 30 jobs
        int batchSize = 30;
        
        System.out.println("Starting batch processing of " + batchSize + " jobs...");
        long startTime = System.currentTimeMillis();
        enrichmentService.processBatch(batchSize);
        long endTime = System.currentTimeMillis();

        List<JobSemanticEnrichment> enrichments = enrichmentRepo.findAll();
        
        long totalTime = endTime - startTime;
        long avgTime = enrichments.size() > 0 ? totalTime / enrichments.size() : 0;
        
        System.out.println("==================================================");
        System.out.println("BATCH PROCESSING METRICS");
        System.out.println("==================================================");
        System.out.println("Total Processed: " + enrichments.size());
        System.out.println("Total Time: " + totalTime + " ms");
        System.out.println("Average Time: " + avgTime + " ms");

        int successCount = 0;
        int failureCount = 0;
        int familyAgreements = 0;
        int levelAgreements = 0;
        int familyDisagreements = 0;
        int levelDisagreements = 0;
        
        for (JobSemanticEnrichment e : enrichments) {
            if ("COMPLETED".equals(e.getProcessingStatus())) {
                successCount++;
            } else {
                failureCount++;
            }
            
            UniversalJobRepresentation u = universalRepo.findByRawObservationId(e.getRawObservationId());
            if (u != null && "COMPLETED".equals(e.getProcessingStatus())) {
                String detFamily = u.getJobFamily();
                String llmFamily = e.getJobFamily();
                if (detFamily != null && detFamily.equals(llmFamily)) {
                    familyAgreements++;
                } else if (detFamily != null || llmFamily != null) {
                    familyDisagreements++;
                    System.out.println("Job Family Disagreement (Job ID " + e.getRawObservationId() + "): Deterministic=" + detFamily + ", LLM=" + llmFamily);
                }
                
                String detLevel = u.getCareerLevel();
                String llmLevel = e.getCareerLevel();
                if (detLevel != null && detLevel.equals(llmLevel)) {
                    levelAgreements++;
                } else if (detLevel != null || llmLevel != null) {
                    levelDisagreements++;
                    System.out.println("Career Level Disagreement (Job ID " + e.getRawObservationId() + "): Deterministic=" + detLevel + ", LLM=" + llmLevel);
                }
                
                if (e.getExperienceYearsMin() != null) {
                    System.out.println("Extracted Min Exp: " + e.getExperienceYearsMin() + " (Job ID " + e.getRawObservationId() + ")");
                }
            }
        }
        
        System.out.println("Success: " + successCount + ", Failures: " + failureCount);
        System.out.println("Family Agreements: " + familyAgreements + ", Disagreements: " + familyDisagreements);
        System.out.println("Level Agreements: " + levelAgreements + ", Disagreements: " + levelDisagreements);
        System.out.println("==================================================");
    }
    
    private void setupCompany(String name, String industry, String city, String platform) {
        ResearchRecord research = new ResearchRecord();
        research.setCompanyName(name);
        research.setIndustry(industry);
        research.setIndianCity(city);
        research.setCareerPlatform(platform);
        research.setResearchStatus("COMPLETED");
        researchRepo.save(research);

        CareerSystemRegistry registry = new CareerSystemRegistry();
        registry.setCompanyName(name);
        registry.setPlatformProvider(platform);
        registry.setObservablePublicRequests(true);
        registryRepo.save(registry);
    }
}
