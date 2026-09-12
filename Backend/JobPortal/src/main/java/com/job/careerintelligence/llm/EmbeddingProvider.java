package com.job.careerintelligence.llm;

import java.util.List;

public interface EmbeddingProvider {
    /**
     * Gets the name of the model being used to generate embeddings.
     */
    String getModelName();

    /**
     * Generates a single embedding vector for the provided text.
     */
    double[] embed(String text);

    /**
     * Optional method for batch generation if the provider supports it natively.
     */
    List<double[]> embedBatch(List<String> texts);
}
