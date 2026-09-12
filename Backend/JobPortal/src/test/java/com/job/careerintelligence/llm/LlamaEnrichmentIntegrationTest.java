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
public class LlamaEnrichmentIntegrationTest {

    @Autowired
    private DataCollectionService dataCollectionService;

    @Autowired
    private SemanticEnrichmentService enrichmentService;

    @Autowired
    private JobSemanticEnrichmentRepository enrichmentRepo;

    @Autowired
    private RawJobObservationRepository observationRepo;

    @Autowired
    private CareerSystemRegistryRepository registryRepo;
    
    @Autowired
    private ResearchRecordRepository researchRepo;

    @Autowired
    private LocalLlmClient llmClient;

    @Test
    @Transactional
    public void testLlamaEnrichment() {
        if (!llmClient.isModelAvailable()) {
            System.out.println("Ollama or Llama 3.2 is not available. Skipping test execution.");
            return;
        }

        // Setup Stripe to collect a few jobs
        setupCompany("Stripe", "Fintech", "Bengaluru", "Greenhouse");
        dataCollectionService.runCollectionCycle();

        List<RawJobObservation> jobs = observationRepo.findAll();
        assertTrue(jobs.size() > 0, "Should have collected jobs");

        System.out.println("Testing Llama Enrichment on 2 real jobs...");
        
        long startTime = System.currentTimeMillis();
        enrichmentService.processBatch(2);
        long endTime = System.currentTimeMillis();

        List<JobSemanticEnrichment> enrichments = enrichmentRepo.findAll();
        assertEquals(2, enrichments.size());
        
        long avgTime = (endTime - startTime) / 2;
        System.out.println("Average processing time per job: " + avgTime + "ms");

        for (JobSemanticEnrichment enrichment : enrichments) {
            System.out.println("--------------------------------------------------");
            System.out.println("Job ID: " + enrichment.getRawObservationId());
            System.out.println("Status: " + enrichment.getProcessingStatus());
            System.out.println("Career Level: " + enrichment.getCareerLevel());
            System.out.println("Job Family: " + enrichment.getJobFamily());
            System.out.println("Experience Min: " + enrichment.getExperienceYearsMin());
            System.out.println("Experience Max: " + enrichment.getExperienceYearsMax());
            System.out.println("Remote Status: " + enrichment.getRemoteStatus());
            System.out.println("Skills: " + enrichment.getSkills());
            System.out.println("Ambiguous: " + enrichment.getAmbiguousFields());
            System.out.println("--------------------------------------------------");
            
            assertEquals("COMPLETED", enrichment.getProcessingStatus(), "LLM should successfully process the job");
            assertNotNull(enrichment.getModelName(), "Model name should be recorded");
        }
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
