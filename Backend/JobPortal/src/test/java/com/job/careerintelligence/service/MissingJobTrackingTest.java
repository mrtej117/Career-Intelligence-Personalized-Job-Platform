package com.job.careerintelligence.service;

import com.job.careerintelligence.adapter.CareerSourceAdapter;
import com.job.careerintelligence.dto.CollectionMetrics;
import com.job.careerintelligence.dto.RawJobDTO;
import com.job.careerintelligence.entity.CareerSystemRegistry;
import com.job.careerintelligence.entity.SourceJobIdentity;
import com.job.careerintelligence.repository.CareerSystemRegistryRepository;
import com.job.careerintelligence.repository.SourceJobIdentityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
        "career.intelligence.collection.max-concurrent-companies=1",
        "career.intelligence.collection.retry.count=0",
        "career.intelligence.scheduler.enabled=false"
})
@ActiveProfiles("dev")
public class MissingJobTrackingTest {

    @Autowired
    private DataCollectionService dataCollectionService;

    @Autowired
    private SourceJobIdentityRepository identityRepository;

    @MockBean
    private CareerSystemRegistryRepository registryRepository;

    @MockBean(name = "greenhouseAdapter")
    private CareerSourceAdapter greenhouseAdapter;
    
    @MockBean
    private IndiaLocationService indiaLocationService;

    @BeforeEach
    public void setup() {
        identityRepository.deleteAll();
        
        when(greenhouseAdapter.getPlatformName()).thenReturn("Greenhouse");
        when(greenhouseAdapter.canHandle(anyString())).thenReturn(true);
        when(indiaLocationService.isValidLocation(anyString())).thenReturn(true);

        org.springframework.test.util.ReflectionTestUtils.setField(
                dataCollectionService, "adapters", Collections.singletonList(greenhouseAdapter));
    }

    @Test
    public void testMissingJobLifecycleWithBoardToken() {
        // Given a registry with mismatched companyName and boardToken
        CareerSystemRegistry registry = new CareerSystemRegistry();
        registry.setCompanyName("GitLab Inc."); // readable name
        registry.setPlatformProvider("Greenhouse");
        registry.setBoardToken("gitlab");       // board token
        registry.setIsEnabled(true);

        when(registryRepository.findAll()).thenReturn(Collections.singletonList(registry));

        // Cycle 1: Job 101 is discovered
        RawJobDTO job101 = RawJobDTO.builder()
                .sourcePlatform("Greenhouse")
                .companyName("gitlab") // adapter outputs boardToken
                .externalJobId("101")
                .rawLocation("India")
                .rawJson("{}")
                .build();
        
        when(greenhouseAdapter.discoverJobs("gitlab")).thenReturn(Collections.singletonList(job101));
        when(greenhouseAdapter.fetchJobDetails("gitlab", "101")).thenReturn(job101);

        CollectionMetrics metrics1 = dataCollectionService.runCollectionCycle();
        assertEquals(1, metrics1.getNewJobs());

        Optional<SourceJobIdentity> id101Opt = identityRepository.findBySourcePlatformAndCompanyNameAndExternalJobId("Greenhouse", "gitlab", "101");
        assertTrue(id101Opt.isPresent());
        assertTrue(id101Opt.get().getIsActive());
        assertEquals(0, id101Opt.get().getMissingCyclesCount());

        // Cycle 2: Job 101 is MISSING from discovery
        when(greenhouseAdapter.discoverJobs("gitlab")).thenReturn(Collections.emptyList());

        dataCollectionService.runCollectionCycle();
        
        SourceJobIdentity id101Cycle2 = identityRepository.findById(id101Opt.get().getId()).get();
        assertEquals(1, id101Cycle2.getMissingCyclesCount());
        assertTrue(id101Cycle2.getIsActive());

        // Cycle 3: Job 101 still MISSING
        dataCollectionService.runCollectionCycle();

        SourceJobIdentity id101Cycle3 = identityRepository.findById(id101Opt.get().getId()).get();
        assertEquals(2, id101Cycle3.getMissingCyclesCount());
        assertTrue(id101Cycle3.getIsActive());

        // Cycle 4: Job 101 still MISSING (threshold is 3, so it should be marked INACTIVE)
        CollectionMetrics metrics4 = dataCollectionService.runCollectionCycle();
        assertEquals(1, metrics4.getInactiveJobs());

        SourceJobIdentity id101Cycle4 = identityRepository.findById(id101Opt.get().getId()).get();
        assertEquals(3, id101Cycle4.getMissingCyclesCount());
        assertFalse(id101Cycle4.getIsActive());
    }
}
