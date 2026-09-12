package com.job.careerintelligence.service;

import com.job.careerintelligence.adapter.CareerSourceAdapter;
import com.job.careerintelligence.dto.CollectionMetrics;
import com.job.careerintelligence.dto.RawJobDTO;
import com.job.careerintelligence.dto.SaveObservationResult;
import com.job.careerintelligence.entity.CareerSystemRegistry;
import com.job.careerintelligence.repository.CareerSystemRegistryRepository;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataCollectionService {

    private final CareerSystemRegistryRepository registryRepository;
    private final IndiaLocationService indiaLocationService;
    private final List<CareerSourceAdapter> adapters;
    private final ObservationService observationService;
    private final NormalizationService normalizationService;

    @Value("${career.intelligence.collection.max-concurrent-companies:5}")
    private int maxConcurrentCompanies;

    @Value("${career.intelligence.collection.retry.count:3}")
    private int maxRetries;

    @Value("${career.intelligence.collection.retry.backoff-ms:2000}")
    private long retryBackoffMs;

    @Value("${career.intelligence.collection.timeout-seconds:300}")
    private long companyTimeoutSeconds;

    private ExecutorService executorService;

    // Lazy initialization of executor to respect property changes
    private synchronized ExecutorService getExecutorService() {
        if (executorService == null || executorService.isShutdown()) {
            executorService = Executors.newFixedThreadPool(maxConcurrentCompanies);
        }
        return executorService;
    }

    @PreDestroy
    public void shutdown() {
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }

    public CollectionMetrics runCollectionCycle() {
        long startTime = System.currentTimeMillis();
        CollectionMetrics totalMetrics = new CollectionMetrics();

        List<CareerSystemRegistry> systems = registryRepository.findAll().stream()
                .filter(s -> Boolean.TRUE.equals(s.getIsEnabled()))
                .toList();

        totalMetrics.setSourcesChecked(systems.size());

        List<CompletableFuture<CollectionMetrics>> futures = new ArrayList<>();
        ExecutorService executor = getExecutorService();

        for (CareerSystemRegistry system : systems) {
            CompletableFuture<CollectionMetrics> future = CompletableFuture.supplyAsync(() -> {
                return processCompanyWithRetry(system);
            }, executor).orTimeout(companyTimeoutSeconds, TimeUnit.SECONDS).exceptionally(ex -> {
                log.error("Company collection timed out or failed for {}: {}", system.getCompanyName(), ex.getMessage());
                CollectionMetrics failedMetrics = new CollectionMetrics();
                failedMetrics.setCompaniesAttempted(1);
                failedMetrics.setCompaniesFailed(1);
                return failedMetrics;
            });
            futures.add(future);
        }

        // Wait for all to complete
        List<CollectionMetrics> results = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        for (CollectionMetrics cm : results) {
            totalMetrics.add(cm);
        }

        totalMetrics.setDurationMs(System.currentTimeMillis() - startTime);
        log.info("Collection cycle completed in {} ms. Attempted: {}, Succeeded: {}, Failed: {}, Discovered: {}, Accepted: {}, Skipped: {}",
                totalMetrics.getDurationMs(), totalMetrics.getCompaniesAttempted(), totalMetrics.getCompaniesSucceeded(),
                totalMetrics.getCompaniesFailed(), totalMetrics.getJobsDiscovered(), totalMetrics.getJobsAccepted(),
                totalMetrics.getJobsSkippedByLocation());
        return totalMetrics;
    }

    private CollectionMetrics processCompanyWithRetry(CareerSystemRegistry system) {
        int attempt = 0;
        while (attempt <= maxRetries) {
            attempt++;
            try {
                return processCompany(system);
            } catch (Exception e) {
                boolean isRateLimitOr5xx = e.getMessage() != null && 
                    (e.getMessage().contains("429") || e.getMessage().contains("502") || 
                     e.getMessage().contains("503") || e.getMessage().contains("504"));

                if (attempt <= maxRetries && (isRateLimitOr5xx || e instanceof org.springframework.web.client.RestClientException)) {
                    long backoff = retryBackoffMs * (long) Math.pow(2, attempt - 1);
                    log.warn("Transient error for company {}, attempt {}. Retrying in {} ms. Error: {}", 
                            system.getCompanyName(), attempt, backoff, e.getMessage());
                    try {
                        Thread.sleep(backoff);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    log.error("Failed to process company {} after {} attempts: {}", system.getCompanyName(), attempt, e.getMessage());
                    CollectionMetrics failedMetrics = new CollectionMetrics();
                    failedMetrics.setCompaniesAttempted(1);
                    failedMetrics.setCompaniesFailed(1);
                    return failedMetrics;
                }
            }
        }
        CollectionMetrics failedMetrics = new CollectionMetrics();
        failedMetrics.setCompaniesAttempted(1);
        failedMetrics.setCompaniesFailed(1);
        return failedMetrics;
    }

    private CollectionMetrics processCompany(CareerSystemRegistry system) {
        CollectionMetrics metrics = new CollectionMetrics();
        metrics.setCompaniesAttempted(1);

        CareerSourceAdapter adapter = adapters.stream()
                .filter(a -> a.getPlatformName().equalsIgnoreCase(system.getPlatformProvider())
                        && a.canHandle(system.getBoardToken()))
                .findFirst()
                .orElse(null);

        if (adapter == null) {
            log.warn("No suitable adapter found for company {} and platform {}", system.getCompanyName(), system.getPlatformProvider());
            metrics.setCompaniesFailed(1);
            return metrics;
        }

        List<RawJobDTO> discoveredJobs = adapter.discoverJobs(system.getBoardToken());
        metrics.setJobsDiscovered(discoveredJobs.size());

        List<String> activeExternalIdsFound = new ArrayList<>();

        for (RawJobDTO jobPreview : discoveredJobs) {
            if (indiaLocationService.isValidLocation(jobPreview.getRawLocation())) {
                metrics.setJobsAccepted(metrics.getJobsAccepted() + 1);
                activeExternalIdsFound.add(jobPreview.getExternalJobId());

                try {
                    RawJobDTO detailedJob = adapter.fetchJobDetails(system.getBoardToken(), jobPreview.getExternalJobId());
                    
                    // DB writes within observationService and normalizationService
                    SaveObservationResult result = observationService.saveObservation(detailedJob);

                    if (result.getStatus() == SaveObservationResult.ObservationStatus.NEW) {
                        normalizationService.normalize(result.getObservation());
                        metrics.setNewJobs(metrics.getNewJobs() + 1);
                    } else if (result.getStatus() == SaveObservationResult.ObservationStatus.CHANGED) {
                        normalizationService.normalize(result.getObservation());
                        metrics.setChangedJobs(metrics.getChangedJobs() + 1);
                    } else {
                        metrics.setUnchangedJobs(metrics.getUnchangedJobs() + 1);
                    }
                } catch (Exception e) {
                    log.error("Failed to process job ID {} for company {}", jobPreview.getExternalJobId(), system.getCompanyName(), e);
                }
            } else {
                metrics.setJobsSkippedByLocation(metrics.getJobsSkippedByLocation() + 1);
            }
        }

        int inactive = observationService.processMissingJobs(system.getPlatformProvider(), system.getBoardToken(), activeExternalIdsFound, 3);
        metrics.setInactiveJobs(inactive);
        
        metrics.setCompaniesSucceeded(1);
        return metrics;
    }
}
