package com.job.careerintelligence.service;

import com.job.careerintelligence.entity.SemanticEmbedding;
import com.job.careerintelligence.llm.EmbeddingProvider;
import com.job.careerintelligence.repository.SemanticEmbeddingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingService {

    private final EmbeddingProvider embeddingProvider;
    private final SemanticEmbeddingRepository embeddingRepository;

    @Transactional
    public SemanticEmbedding generateAndSaveEmbedding(String entityType, Long entityId, String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        String sourceHash = computeHash(text);
        String modelName = embeddingProvider.getModelName();

        // Check if an embedding for this exact text hash already exists for this model
        Optional<SemanticEmbedding> existing = embeddingRepository.findByEntityTypeAndSourceHashAndModelName(entityType, sourceHash, modelName);
        if (existing.isPresent()) {
            // Re-map it to the potentially new entityId if it's the same hash
            SemanticEmbedding emb = existing.get();
            // Since multiple jobs could technically have the identical text, we could either share it or duplicate it.
            // But we requested uniqueness per entityType + sourceHash. 
            // Wait, if it's the exact same hash, it's the same embedding vector. 
            // We should just return it. 
            // Wait, the entityId links it to a specific Job or Candidate.
            // It's better to store a unique embedding per entityId so they can be looked up by entityId.
            // Let's check by entityId first instead.
            
            Optional<SemanticEmbedding> entityExisting = embeddingRepository.findByEntityTypeAndEntityIdAndModelName(entityType, entityId, modelName);
            if (entityExisting.isPresent()) {
                SemanticEmbedding e = entityExisting.get();
                if (e.getSourceHash().equals(sourceHash)) {
                    // It's the exact same data. Reuse!
                    log.debug("Reusing unchanged embedding for {} id {}", entityType, entityId);
                    return e;
                } else {
                    // Update existing embedding with new hash and new vector!
                    double[] vector = embeddingProvider.embed(text);
                    e.setSourceHash(sourceHash);
                    e.setVector(vector);
                    e.setDimensions(vector.length);
                    return embeddingRepository.save(e);
                }
            } else {
                // We have the hash globally but not for this entity. We can copy the vector to save LLM calls!
                double[] vector = emb.getVector();
                SemanticEmbedding newEmb = SemanticEmbedding.builder()
                        .entityType(entityType)
                        .entityId(entityId)
                        .modelName(modelName)
                        .sourceHash(sourceHash)
                        .vector(vector)
                        .dimensions(vector.length)
                        .build();
                return embeddingRepository.save(newEmb);
            }
        }

        // Check if entity has an old embedding we should update
        Optional<SemanticEmbedding> entityExisting = embeddingRepository.findByEntityTypeAndEntityIdAndModelName(entityType, entityId, modelName);
        if (entityExisting.isPresent()) {
             SemanticEmbedding e = entityExisting.get();
             double[] vector = embeddingProvider.embed(text);
             e.setSourceHash(sourceHash);
             e.setVector(vector);
             e.setDimensions(vector.length);
             return embeddingRepository.save(e);
        }

        // Completely new embedding
        double[] vector = embeddingProvider.embed(text);
        if (vector.length == 0) {
            log.warn("Failed to generate embedding for {} id {}", entityType, entityId);
            return null;
        }

        SemanticEmbedding newEmb = SemanticEmbedding.builder()
                .entityType(entityType)
                .entityId(entityId)
                .modelName(modelName)
                .sourceHash(sourceHash)
                .vector(vector)
                .dimensions(vector.length)
                .build();
        return embeddingRepository.save(newEmb);
    }

    private String computeHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
