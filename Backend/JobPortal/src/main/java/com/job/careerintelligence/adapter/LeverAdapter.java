package com.job.careerintelligence.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.job.careerintelligence.dto.RawJobDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class LeverAdapter implements CareerSourceAdapter {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getPlatformName() {
        return "Lever";
    }

    @Override
    public boolean canHandle(String identifier) {
        return identifier != null && !identifier.isEmpty() && !identifier.startsWith("http");
    }

    @Override
    public List<RawJobDTO> discoverJobs(String boardToken) {
        String url = "https://api.lever.co/v0/postings/" + boardToken + "?mode=json";
        List<RawJobDTO> rawJobs = new ArrayList<>();
        
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(jsonResponse);
            
            if (root.isArray()) {
                for (JsonNode jobNode : root) {
                    RawJobDTO dto = RawJobDTO.builder()
                        .sourcePlatform(getPlatformName())
                        .companyName(boardToken)
                        .externalJobId(jobNode.path("id").asText())
                        .sourceJobUrl(jobNode.path("hostedUrl").asText())
                        .rawTitle(jobNode.path("text").asText())
                        .rawLocation(jobNode.path("categories").path("location").asText())
                        .rawDepartment(jobNode.path("categories").path("team").asText())
                        .rawEmploymentType(jobNode.path("categories").path("commitment").asText())
                        .rawJson(jobNode.toString())
                        .build();
                    rawJobs.add(dto);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to discover jobs from Lever for board: " + boardToken, e);
        }
        
        return rawJobs;
    }

    @Override
    public RawJobDTO fetchJobDetails(String boardToken, String externalJobId) {
        String url = "https://api.lever.co/v0/postings/" + boardToken + "/" + externalJobId + "?mode=json";
        
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(jsonResponse);
            
            // Reconstruct HTML for parser
            StringBuilder fullHtml = new StringBuilder();
            
            String description = root.path("description").asText("");
            if (!description.isEmpty()) {
                fullHtml.append(HtmlUtils.htmlUnescape(description));
            }
            
            JsonNode lists = root.path("lists");
            if (lists.isArray()) {
                for (JsonNode listNode : lists) {
                    String listText = listNode.path("text").asText("");
                    String listContent = listNode.path("content").asText("");
                    if (!listText.isEmpty()) {
                        fullHtml.append("<h3>").append(HtmlUtils.htmlUnescape(listText)).append("</h3>");
                    }
                    if (!listContent.isEmpty()) {
                        fullHtml.append(HtmlUtils.htmlUnescape(listContent));
                    }
                }
            }
            
            String additional = root.path("additional").asText("");
            if (!additional.isEmpty()) {
                fullHtml.append(HtmlUtils.htmlUnescape(additional));
            }

            return RawJobDTO.builder()
                .sourcePlatform(getPlatformName())
                .companyName(boardToken)
                .externalJobId(root.path("id").asText())
                .sourceJobUrl(root.path("hostedUrl").asText())
                .rawTitle(root.path("text").asText())
                .rawLocation(root.path("categories").path("location").asText())
                .rawDepartment(root.path("categories").path("team").asText())
                .rawEmploymentType(root.path("categories").path("commitment").asText())
                .rawHtml(fullHtml.toString())
                .rawJson(root.toString()) // preserving raw original data
                .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch job details from Lever for id: " + externalJobId, e);
        }
    }
}
