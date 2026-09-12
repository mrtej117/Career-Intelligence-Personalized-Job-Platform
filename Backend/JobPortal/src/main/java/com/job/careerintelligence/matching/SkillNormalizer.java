package com.job.careerintelligence.matching;

import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Controlled skill normalization layer.
 * Maps common aliases to a canonical form for comparison purposes
 * while preserving original source terminology in match results.
 */
@Component
public class SkillNormalizer {

    private static final Map<String, String> CANONICAL_MAP = new HashMap<>();

    static {
        // JavaScript aliases
        addAlias("javascript", "javascript");
        addAlias("js", "javascript");
        addAlias("ecmascript", "javascript");

        // TypeScript
        addAlias("typescript", "typescript");
        addAlias("ts", "typescript");

        // React
        addAlias("react", "react");
        addAlias("react.js", "react");
        addAlias("reactjs", "react");

        // Angular
        addAlias("angular", "angular");
        addAlias("angularjs", "angular");
        addAlias("angular.js", "angular");

        // Node.js
        addAlias("node.js", "nodejs");
        addAlias("nodejs", "nodejs");
        addAlias("node", "nodejs");

        // PostgreSQL
        addAlias("postgresql", "postgresql");
        addAlias("postgres", "postgresql");

        // MySQL
        addAlias("mysql", "mysql");
        addAlias("my sql", "mysql");

        // MongoDB
        addAlias("mongodb", "mongodb");
        addAlias("mongo", "mongodb");

        // Amazon Web Services
        addAlias("aws", "aws");
        addAlias("amazon web services", "aws");

        // Google Cloud
        addAlias("gcp", "gcp");
        addAlias("google cloud", "gcp");
        addAlias("google cloud platform", "gcp");

        // Microsoft Azure
        addAlias("azure", "azure");
        addAlias("microsoft azure", "azure");

        // Docker
        addAlias("docker", "docker");
        addAlias("docker containers", "docker");

        // Kubernetes
        addAlias("kubernetes", "kubernetes");
        addAlias("k8s", "kubernetes");

        // Python
        addAlias("python", "python");
        addAlias("python3", "python");

        // Java
        addAlias("java", "java");

        // Spring Boot
        addAlias("spring boot", "spring boot");
        addAlias("springboot", "spring boot");
        addAlias("spring-boot", "spring boot");

        // C#
        addAlias("c#", "csharp");
        addAlias("csharp", "csharp");
        addAlias("c sharp", "csharp");

        // .NET
        addAlias(".net", "dotnet");
        addAlias("dotnet", "dotnet");
        addAlias(".net core", "dotnet");

        // Machine Learning
        addAlias("machine learning", "machine learning");
        addAlias("ml", "machine learning");

        // SQL
        addAlias("sql", "sql");
        addAlias("structured query language", "sql");

        // CI/CD
        addAlias("ci/cd", "cicd");
        addAlias("cicd", "cicd");
        addAlias("ci cd", "cicd");
    }

    private static void addAlias(String alias, String canonical) {
        CANONICAL_MAP.put(alias.toLowerCase().trim(), canonical);
    }

    /**
     * Returns the canonical form for a skill, or the lowercase-trimmed original if no alias exists.
     */
    public String normalize(String skill) {
        if (skill == null) return null;
        String key = skill.toLowerCase().trim();
        return CANONICAL_MAP.getOrDefault(key, key);
    }

    /**
     * Normalizes a list of skills into a set of canonical forms.
     */
    public Set<String> normalizeAll(List<String> skills) {
        if (skills == null) return Collections.emptySet();
        Set<String> result = new LinkedHashSet<>();
        for (String s : skills) {
            if (s != null && !s.trim().isEmpty()) {
                result.add(normalize(s));
            }
        }
        return result;
    }
}
