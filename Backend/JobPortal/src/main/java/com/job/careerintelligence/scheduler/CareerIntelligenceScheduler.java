package com.job.careerintelligence.scheduler;

import com.job.careerintelligence.agent.CareerIntelligenceAgent;
import com.job.careerintelligence.service.DataCollectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
@ConditionalOnProperty(name = "career.intelligence.scheduler.enabled", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
@Slf4j
public class CareerIntelligenceScheduler {

    private final CareerIntelligenceAgent agent;
    private final DataCollectionService dataCollectionService;
    
    private final AtomicBoolean isCollecting = new AtomicBoolean(false);

    // Collection cycle runs independently
    @Scheduled(fixedDelayString = "${career.intelligence.collection.interval:3600000}", initialDelay = 15000)
    public void runContinuousCollection() {
        if (isCollecting.compareAndSet(false, true)) {
            try {
                log.info("=== SCHEDULER TRIGGERING JOB COLLECTION ===");
                dataCollectionService.runCollectionCycle();
            } catch (Exception e) {
                log.error("Job collection failed", e);
            } finally {
                isCollecting.set(false);
            }
        } else {
            log.info("Collection is already running, skipping this scheduled cycle.");
        }
    }

    // Agent cycle runs independently
    @Scheduled(fixedDelayString = "${career.intelligence.agent.interval:60000}", initialDelay = 30000)
    public void runAgentCycle() {
        log.info("=== SCHEDULER TRIGGERING AGENT ===");
        agent.runExecution();
    }
}
