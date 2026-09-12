package com.job.careerintelligence.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.job.careerintelligence.dto.RawJobDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class AshbyAdapter implements CareerSourceAdapter {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getPlatformName() {
        return "Ashby";
    }

    @Override
    public boolean canHandle(String identifier) {
        return identifier != null && !identifier.isEmpty() && !identifier.startsWith("http");
    }

    @Override
    public List<RawJobDTO> discoverJobs(String boardToken) {
        String url = "https://api.ashbyhq.com/posting-api/job-board/" + boardToken;
        List<RawJobDTO> rawJobs = new ArrayList<>();
        
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode jobsNode = root.path("jobs");
            
            if (jobsNode.isArray()) {
                for (JsonNode jobNode : jobsNode) {
                    RawJobDTO dto = RawJobDTO.builder()
                        .sourcePlatform(getPlatformName())
                        .companyName(boardToken)
                        .externalJobId(jobNode.path("id").asText())
                        .sourceJobUrl(jobNode.path("jobUrl").asText())
                        .rawTitle(jobNode.path("title").asText())
                        .rawLocation(jobNode.path("location").asText())
                        .rawJson(jobNode.toString())
                        .build();
                    rawJobs.add(dto);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to discover jobs from Ashby for board: " + boardToken, e);
        }
        
        return rawJobs;
    }

    @Override
    public RawJobDTO fetchJobDetails(String boardToken, String externalJobId) {
        // Ashby posting API returns details in the list.
        // For efficiency in a real setup we might cache the list, but here we just fetch the list and find the ID.
        String url = "https://api.ashbyhq.com/posting-api/job-board/" + boardToken;
        
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode jobsNode = root.path("jobs");
            
            if (jobsNode.isArray()) {
                for (JsonNode jobNode : jobsNode) {
                    if (jobNode.path("id").asText().equals(externalJobId)) {
                        String dept = jobNode.path("department").asText();
                        String content = jobNode.path("descriptionHtml").asText();
                        
                        return RawJobDTO.builder()
                            .sourcePlatform(getPlatformName())
                            .companyName(boardToken)
                            .externalJobId(jobNode.path("id").asText())
                            .sourceJobUrl(jobNode.path("jobUrl").asText())
                            .rawTitle(jobNode.path("title").asText())
                            .rawLocation(jobNode.path("location").asText())
                            .rawDepartment(dept)
                            .rawHtml(content)
                            .rawJson(jobNode.toString())
                            .build();
                    }
                }
            }
            throw new RuntimeException("Job not found in Ashby board: " + externalJobId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch job details from Ashby for id: " + externalJobId, e);
        }
    }
}
