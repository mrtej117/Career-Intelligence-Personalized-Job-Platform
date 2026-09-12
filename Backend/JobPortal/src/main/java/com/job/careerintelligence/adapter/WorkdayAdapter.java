package com.job.careerintelligence.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.job.careerintelligence.dto.RawJobDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class WorkdayAdapter implements CareerSourceAdapter {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getPlatformName() {
        return "Workday";
    }

    @Override
    public boolean canHandle(String identifier) {
        // Assume identifier is tenant/site (e.g., "mycompany/mycompany_careers")
        return identifier != null && identifier.contains("/");
    }

    @Override
    public List<RawJobDTO> discoverJobs(String boardToken) {
        // Format: tenant/site
        String[] parts = boardToken.split("/");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Workday boardToken must be in format 'tenant/site'");
        }
        String tenant = parts[0];
        String site = parts[1];
        
        String url = "https://" + tenant + ".myworkdayjobs.com/wday/cxs/" + tenant + "/" + site + "/jobs";
        List<RawJobDTO> rawJobs = new ArrayList<>();
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String payload = "{\"limit\":100,\"offset\":0}";
            HttpEntity<String> request = new HttpEntity<>(payload, headers);
            
            String jsonResponse = restTemplate.postForObject(url, request, String.class);
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode jobsNode = root.path("jobPostings");
            
            if (jobsNode.isArray()) {
                for (JsonNode jobNode : jobsNode) {
                    String externalPath = jobNode.path("externalPath").asText();
                    String location = jobNode.path("locationsText").asText();
                    if (location == null || location.isEmpty()) {
                        // Workday sometimes puts locations in bulletFields
                        JsonNode bullets = jobNode.path("bulletFields");
                        if (bullets.isArray() && bullets.size() > 0) {
                            location = bullets.get(0).asText();
                        }
                    }

                    RawJobDTO dto = RawJobDTO.builder()
                        .sourcePlatform(getPlatformName())
                        .companyName(boardToken)
                        .externalJobId(externalPath)
                        .sourceJobUrl("https://" + tenant + ".myworkdayjobs.com/en-US/" + site + externalPath)
                        .rawTitle(jobNode.path("title").asText())
                        .rawLocation(location)
                        .rawJson(jobNode.toString())
                        .build();
                    rawJobs.add(dto);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to discover jobs from Workday for board: " + boardToken, e);
        }
        
        return rawJobs;
    }

    @Override
    public RawJobDTO fetchJobDetails(String boardToken, String externalJobId) {
        String[] parts = boardToken.split("/");
        String tenant = parts[0];
        String site = parts[1];
        
        // externalJobId is the path (e.g., "/job/Location/Title_ID")
        String url = "https://" + tenant + ".myworkdayjobs.com/wday/cxs/" + tenant + "/" + site + externalJobId;
        
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode jobNode = root.path("jobPostingInfo");
            
            String content = jobNode.path("jobDescription").asText();
            String location = jobNode.path("location").asText();
            String dept = jobNode.path("jobFamilyGroup").asText();

            return RawJobDTO.builder()
                .sourcePlatform(getPlatformName())
                .companyName(boardToken)
                .externalJobId(externalJobId)
                .sourceJobUrl("https://" + tenant + ".myworkdayjobs.com/en-US/" + site + externalJobId)
                .rawTitle(jobNode.path("title").asText())
                .rawLocation(location)
                .rawDepartment(dept)
                .rawHtml(content)
                .rawJson(root.toString())
                .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch job details from Workday for id: " + externalJobId, e);
        }
    }
}
