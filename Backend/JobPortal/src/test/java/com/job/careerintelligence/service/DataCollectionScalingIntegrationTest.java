package com.job.careerintelligence.service;

import com.job.careerintelligence.adapter.CareerSourceAdapter;
import com.job.careerintelligence.dto.CollectionMetrics;
import com.job.careerintelligence.dto.RawJobDTO;
import com.job.careerintelligence.dto.SaveObservationResult;
import com.job.careerintelligence.entity.CareerSystemRegistry;
import com.job.careerintelligence.entity.RawJobObservation;
import com.job.careerintelligence.repository.CareerSystemRegistryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {
        "career.intelligence.collection.max-concurrent-companies=2",
        "career.intelligence.collection.retry.count=2",
        "career.intelligence.collection.retry.backoff-ms=10",
        "career.intelligence.collection.timeout-seconds=2",
        "career.intelligence.scheduler.enabled=false"
})
@ActiveProfiles("dev")
public class DataCollectionScalingIntegrationTest {

    @Autowired
    private DataCollectionService dataCollectionService;

    @MockBean
    private CareerSystemRegistryRepository registryRepository;

    @MockBean
    private ObservationService observationService;

    @MockBean
    private NormalizationService normalizationService;

    @MockBean
    private IndiaLocationService indiaLocationService;

    @MockBean(name = "greenhouseAdapter")
    private CareerSourceAdapter greenhouseAdapter;

    @BeforeEach
    public void setup() {
        when(greenhouseAdapter.getPlatformName()).thenReturn("Greenhouse");
        when(greenhouseAdapter.canHandle(anyString())).thenReturn(true);
        when(indiaLocationService.isValidLocation(anyString())).thenReturn(true);
        
        // Expose mock adapter via reflection since it's injected as a list in the service
        org.springframework.test.util.ReflectionTestUtils.setField(
                dataCollectionService, "adapters", Collections.singletonList(greenhouseAdapter));
    }

    private CareerSystemRegistry createRegistry(String company) {
        CareerSystemRegistry r = new CareerSystemRegistry();
        r.setCompanyName(company);
        r.setPlatformProvider("Greenhouse");
        r.setBoardToken(company.toLowerCase());
        r.setIsEnabled(true);
        return r;
    }

    @Test
    public void testBoundedConcurrencyAndMetrics() {
        CareerSystemRegistry c1 = createRegistry("Comp1");
        CareerSystemRegistry c2 = createRegistry("Comp2");
        CareerSystemRegistry c3 = createRegistry("Comp3");

        when(registryRepository.findAll()).thenReturn(Arrays.asList(c1, c2, c3));

        RawJobDTO job = RawJobDTO.builder().externalJobId("1").rawLocation("India").build();
        when(greenhouseAdapter.discoverJobs(anyString())).thenReturn(Collections.singletonList(job));
        when(greenhouseAdapter.fetchJobDetails(anyString(), anyString())).thenReturn(job);
        
        SaveObservationResult result = new SaveObservationResult(new RawJobObservation(), SaveObservationResult.ObservationStatus.NEW);
        when(observationService.saveObservation(any())).thenReturn(result);

        CollectionMetrics metrics = dataCollectionService.runCollectionCycle();

        assertEquals(3, metrics.getCompaniesAttempted());
        assertEquals(3, metrics.getCompaniesSucceeded());
        assertEquals(0, metrics.getCompaniesFailed());
        assertEquals(3, metrics.getJobsDiscovered());
        assertEquals(3, metrics.getJobsAccepted());
        assertEquals(3, metrics.getNewJobs());
    }

    @Test
    public void testOneCompanyFailureDoesNotStopOthers() {
        CareerSystemRegistry c1 = createRegistry("FailComp");
        CareerSystemRegistry c2 = createRegistry("SuccessComp");

        when(registryRepository.findAll()).thenReturn(Arrays.asList(c1, c2));

        when(greenhouseAdapter.discoverJobs("failcomp")).thenThrow(new RuntimeException("Permanent error"));
        when(greenhouseAdapter.discoverJobs("successcomp")).thenReturn(Collections.emptyList());

        CollectionMetrics metrics = dataCollectionService.runCollectionCycle();

        assertEquals(2, metrics.getCompaniesAttempted());
        assertEquals(1, metrics.getCompaniesSucceeded());
        assertEquals(1, metrics.getCompaniesFailed());
    }

    @Test
    public void testRetryBehaviorOn5xx() {
        CareerSystemRegistry c1 = createRegistry("RetryComp");
        when(registryRepository.findAll()).thenReturn(Collections.singletonList(c1));

        // Fail first 2 times, succeed on 3rd
        when(greenhouseAdapter.discoverJobs("retrycomp"))
                .thenThrow(new RuntimeException("502 Bad Gateway"))
                .thenThrow(new RuntimeException("504 Gateway Timeout"))
                .thenReturn(Collections.emptyList());

        CollectionMetrics metrics = dataCollectionService.runCollectionCycle();

        assertEquals(1, metrics.getCompaniesAttempted());
        assertEquals(1, metrics.getCompaniesSucceeded());
        verify(greenhouseAdapter, times(3)).discoverJobs("retrycomp");
    }

    @Test
    public void testTimeoutHandling() {
        CareerSystemRegistry c1 = createRegistry("SlowComp");
        when(registryRepository.findAll()).thenReturn(Collections.singletonList(c1));

        when(greenhouseAdapter.discoverJobs("slowcomp")).thenAnswer(invocation -> {
            Thread.sleep(3000); // Exceeds the 2-second timeout configured above
            return Collections.emptyList();
        });

        CollectionMetrics metrics = dataCollectionService.runCollectionCycle();

        assertEquals(1, metrics.getCompaniesAttempted());
        assertEquals(1, metrics.getCompaniesFailed());
    }
}
