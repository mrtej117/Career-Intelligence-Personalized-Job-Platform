package com.job.careerintelligence.llm;

import com.job.careerintelligence.entity.*;
import com.job.careerintelligence.repository.*;
import com.job.careerintelligence.service.DataCollectionService;
import com.job.careerintelligence.service.SemanticEnrichmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "ollama.base-url=http://localhost:9999", // Intentional bad URL
    "ollama.model=llama3.2"
})
public class OllamaFailureTest {

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

    @Test
    @Transactional
    public void testOllamaFailureHandling() {
        // Setup Stripe to collect a few jobs
        setupCompany("Stripe", "Fintech", "Bengaluru", "Greenhouse");
        dataCollectionService.runCollectionCycle();

        List<RawJobObservation> jobs = observationRepo.findAll();
        assertTrue(jobs.size() > 0, "Should have collected jobs");
        
        System.out.println("Testing Ollama Failure Handling on 2 real jobs...");
        
        // This should not crash, but just log errors and return gracefully
        enrichmentService.processBatch(2);

        // Since processBatch checks llmClient.isModelAvailable() at the start and aborts
        // if it can't connect, no enrichments will be created in the batch loop.
        List<JobSemanticEnrichment> enrichments = enrichmentRepo.findAll();
        assertEquals(0, enrichments.size(), "Batch should abort if Ollama is unreachable");

        // Let's test individual processObservation just to be sure it fails gracefully
        JobSemanticEnrichment e = enrichmentService.processObservation(jobs.get(0), null);
        assertNotNull(e);
        assertEquals("FAILED", e.getProcessingStatus());
        
        System.out.println("Ollama failure gracefully handled. Status = " + e.getProcessingStatus());
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
