package com.job.careerintelligence.llm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class OllamaEmbeddingProvider implements EmbeddingProvider {

    @Value("${ollama.base-url:http://localhost:11434}")
    private String baseUrl;

    @Value("${ollama.embedding-model:nomic-embed-text}")
    private String model;

    private final RestTemplate restTemplate;

    public OllamaEmbeddingProvider() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public String getModelName() {
        return model;
    }

    @Override
    public double[] embed(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new double[0];
        }

        try {
            String url = baseUrl + "/api/embeddings";
            Map<String, Object> request = new HashMap<>();
            request.put("model", model);
            request.put("prompt", text);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            OllamaEmbeddingResponse response = restTemplate.postForObject(url, entity, OllamaEmbeddingResponse.class);

            if (response != null && response.getEmbedding() != null) {
                return response.getEmbedding();
            }
        } catch (Exception e) {
            log.error("Failed to generate embedding from Ollama for model {}: {}", model, e.getMessage());
        }
        return new double[0];
    }

    @Override
    public List<double[]> embedBatch(List<String> texts) {
        List<double[]> results = new ArrayList<>();
        for (String text : texts) {
            results.add(embed(text));
        }
        return results;
    }
    
    // Internal DTO for mapping Ollama response
    private static class OllamaEmbeddingResponse {
        private double[] embedding;

        public double[] getEmbedding() {
            return embedding;
        }

        public void setEmbedding(double[] embedding) {
            this.embedding = embedding;
        }
    }
}
