package com.job.careerintelligence.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.job.careerintelligence.dto.RawJobDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class GreenhouseAdapter implements CareerSourceAdapter {
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getPlatformName() {
        return "Greenhouse";
    }

    @Override
    public boolean canHandle(String identifier) {
        // Typically a greenhouse board token (e.g. "stripe", "gitlab")
        return identifier != null && !identifier.isEmpty() && !identifier.startsWith("http");
    }

    @Override
    public List<RawJobDTO> discoverJobs(String boardToken) {
        String url = "https://boards-api.greenhouse.io/v1/boards/" + boardToken + "/jobs";
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
                        .sourceJobUrl(jobNode.path("absolute_url").asText())
                        .rawTitle(jobNode.path("title").asText())
                        .rawLocation(jobNode.path("location").path("name").asText())
                        .rawJson(jobNode.toString())
                        .build();
                    rawJobs.add(dto);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to discover jobs from Greenhouse for board: " + boardToken, e);
        }
        
        return rawJobs;
    }

    @Override
    public RawJobDTO fetchJobDetails(String boardToken, String externalJobId) {
        String url = "https://boards-api.greenhouse.io/v1/boards/" + boardToken + "/jobs/" + externalJobId;
        
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(jsonResponse);
            
            // Extract some basic HTML sections which Greenhouse often exposes as "content"
            String content = root.path("content").asText();
            String unescapedContent = org.springframework.web.util.HtmlUtils.htmlUnescape(content);
            
            String dept = "";
            if (root.has("departments") && root.path("departments").isArray() && root.path("departments").size() > 0) {
                dept = root.path("departments").get(0).path("name").asText();
            }
            
            return RawJobDTO.builder()
                .sourcePlatform(getPlatformName())
                .companyName(boardToken)
                .externalJobId(root.path("id").asText())
                .sourceJobUrl(root.path("absolute_url").asText())
                .rawTitle(root.path("title").asText())
                .rawLocation(root.path("location").path("name").asText())
                .rawDepartment(dept)
                .rawHtml(unescapedContent)
                .rawJson(root.toString()) // preserving raw original data
                .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch job details from Greenhouse for id: " + externalJobId, e);
        }
    }
}
