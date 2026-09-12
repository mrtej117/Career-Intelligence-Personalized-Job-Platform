package com.job.careerintelligence.llm;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class LocalLlmClient {

    @Value("${ollama.base-url:http://localhost:11434}")
    private String baseUrl;

    @Value("${ollama.model:llama3.2}")
    private String model;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public LocalLlmClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public boolean isModelAvailable() {
        try {
            String url = baseUrl + "/api/tags";
            String response = restTemplate.getForObject(url, String.class);
            return response != null && response.contains("\"name\":\"" + model);
        } catch (Exception e) {
            log.error("Failed to connect to Ollama at {}: {}", baseUrl, e.getMessage());
            return false;
        }
    }

    public SemanticJobAnalysis analyzeJob(String jobTextContext) {
        String prompt = buildPrompt(jobTextContext);
        return callOllama(prompt, SemanticJobAnalysis.class);
    }

    public SemanticCandidateAnalysis analyzeResume(String resumeText) {
        String prompt = buildResumePrompt(resumeText);
        return callOllama(prompt, SemanticCandidateAnalysis.class);
    }

    private <T> T callOllama(String prompt, Class<T> responseType) {
        Map<String, Object> request = new HashMap<>();
        request.put("model", model);
        request.put("prompt", prompt);
        request.put("stream", false);
        request.put("format", "json");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        try {
            String url = baseUrl + "/api/generate";
            OllamaResponse response = restTemplate.postForObject(url, entity, OllamaResponse.class);
            
            if (response != null && response.getResponse() != null) {
                String jsonStr = response.getResponse();
                // Sometime LLMs wrap json in markdown
                if (jsonStr.startsWith("```json")) {
                    jsonStr = jsonStr.substring(7);
                    if (jsonStr.endsWith("```")) {
                        jsonStr = jsonStr.substring(0, jsonStr.length() - 3);
                    }
                }
                return objectMapper.readValue(jsonStr, responseType);
            }
        } catch (Exception e) {
            log.error("LLM Analysis failed", e);
        }
        return null;
    }

    public String generateText(String prompt) {
        Map<String, Object> request = new HashMap<>();
        request.put("model", model);
        request.put("prompt", prompt);
        request.put("stream", false);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        
        try {
            String url = baseUrl + "/api/generate";
            OllamaResponse response = restTemplate.postForObject(url, entity, OllamaResponse.class);
            return response != null ? response.getResponse() : "";
        } catch (Exception e) {
            log.error("LLM text generation failed", e);
            return "";
        }
    }

    private String buildPrompt(String jobContent) {
        return "You are a precise technical recruiter AI. Analyze the following job description and extract semantic structured data.\n" +
               "RULES:\n" +
               "1. Use ONLY the supplied job evidence.\n" +
               "2. NEVER invent or guess qualifications, salary, experience, location, or employment type.\n" +
               "3. If information is missing, return null for that field.\n" +
               "4. Separate required skills from preferred skills.\n" +
               "5. For careerLevel, use ONLY one of: INTERNSHIP, APPRENTICESHIP, GRADUATE_TRAINEE, ENTRY_LEVEL, EXPERIENCED, SENIOR, LEAD, MANAGER, DIRECTOR, SENIOR_LEADERSHIP. If uncertain, return null.\n" +
               "6. For jobFamily, use ONLY one of: ENGINEERING, DATA_AI, PRODUCT, DESIGN_UX, MARKETING, HR, RECRUITMENT, FINANCE, SALES, OPERATIONS, CONSULTING, MANUFACTURING, RESEARCH, OTHER. If uncertain, return null.\n" +
               "7. For remoteStatus, use ONLY one of: REMOTE, HYBRID, ONSITE, UNKNOWN.\n" +
               "8. Return ONLY raw JSON matching this EXACT structure, without markdown formatting or extra text:\n" +
               "{\n" +
               "  \"careerLevel\": \"string|null\",\n" +
               "  \"jobFamily\": \"string|null\",\n" +
               "  \"skills\": [\"string\"],\n" +
               "  \"preferredSkills\": [\"string\"],\n" +
               "  \"experienceYearsMin\": number|null,\n" +
               "  \"experienceYearsMax\": number|null,\n" +
               "  \"education\": \"string|null\",\n" +
               "  \"employmentType\": \"string|null\",\n" +
               "  \"remoteStatus\": \"string|null\",\n" +
               "  \"locations\": [\"string\"],\n" +
               "  \"salaryMin\": number|null,\n" +
               "  \"salaryMax\": number|null,\n" +
               "  \"salaryCurrency\": \"string|null\",\n" +
               "  \"semanticSummary\": \"string|null\",\n" +
               "  \"confidence\": number (0.0 to 1.0),\n" +
               "  \"ambiguousFields\": [\"string\"]\n" +
               "}\n\n" +
               "SOURCE JOB EVIDENCE:\n" + jobContent;
    }

    private String buildResumePrompt(String resumeText) {
        return "You are a precise AI recruiter analyzing a candidate's resume.\n" +
               "RULES:\n" +
               "1. Use ONLY the supplied resume evidence.\n" +
               "2. NEVER invent or hallucinate skills, experience, projects, or education.\n" +
               "3. If a field is missing, return null or empty list.\n" +
               "4. For careerLevel, use ONLY one of: INTERNSHIP, APPRENTICESHIP, GRADUATE_TRAINEE, ENTRY_LEVEL, EXPERIENCED, SENIOR, LEAD, MANAGER, DIRECTOR, SENIOR_LEADERSHIP. If uncertain, return null.\n" +
               "5. For remotePreference, use ONLY one of: REMOTE, HYBRID, ONSITE, UNKNOWN. (Return UNKNOWN unless explicitly mentioned).\n" +
               "6. Return ONLY valid JSON matching this structure exactly (no markdown formatting):\n" +
               "{\n" +
               "  \"professionalTitle\": \"string|null\",\n" +
               "  \"careerLevel\": \"string|null\",\n" +
               "  \"totalExperienceYears\": number|null,\n" +
               "  \"technicalSkills\": [\"string\"],\n" +
               "  \"tools\": [\"string\"],\n" +
               "  \"frameworks\": [\"string\"],\n" +
               "  \"programmingLanguages\": [\"string\"],\n" +
               "  \"softSkills\": [\"string\"],\n" +
               "  \"employers\": [{ \"name\": \"string\", \"role\": \"string\", \"responsibilities\": [\"string\"], \"industry\": \"string|null\" }],\n" +
               "  \"education\": [{ \"degree\": \"string\", \"fieldOfStudy\": \"string\", \"institution\": \"string\", \"graduationYear\": \"string|null\" }],\n" +
               "  \"projects\": [{ \"projectName\": \"string\", \"projectDescription\": \"string\", \"technologies\": [\"string\"] }],\n" +
               "  \"certifications\": [\"string\"],\n" +
               "  \"remotePreference\": \"string\",\n" +
               "  \"locationsMentioned\": [\"string\"],\n" +
               "  \"ambiguousFields\": [\"string\"]\n" +
               "}\n\n" +
               "SOURCE RESUME:\n" + resumeText;
    }

    public String getModelName() {
        return this.model;
    }
}
