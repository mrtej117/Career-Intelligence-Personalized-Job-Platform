package com.job.controller;

import com.job.careerintelligence.agent.AgentExecution;
import com.job.careerintelligence.agent.AgentExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/career-intelligence/agent")
@RequiredArgsConstructor
public class AgentRunController {

    private final AgentExecutionRepository executionRepo;

    @GetMapping("/runs")
    public ResponseEntity<List<AgentExecution>> getRecentRuns() {
        // Return top 10 most recent runs
        List<AgentExecution> runs = executionRepo.findAll(
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startedAt"))).getContent();
        return ResponseEntity.ok(runs);
    }
}
