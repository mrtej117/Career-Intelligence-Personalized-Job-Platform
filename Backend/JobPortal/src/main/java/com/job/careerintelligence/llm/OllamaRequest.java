package com.job.careerintelligence.llm;

import lombok.Data;

@Data
public class OllamaRequest {
    private String model;
    private String prompt;
    private String format;
    private boolean stream;
}
