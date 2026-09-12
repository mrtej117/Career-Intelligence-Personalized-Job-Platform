package com.job.careerintelligence.entity;

public enum CandidateActionType {
    VIEW(1.0),
    SAVE(3.0),
    APPLY(5.0),
    REJECT(-4.0);

    private final double weight;

    CandidateActionType(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }
}
