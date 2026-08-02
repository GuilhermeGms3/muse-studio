package com.musicos.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LearningStage {
    FIRST_STEPS("first_steps", "Primeiros passos", 0),
    BEGINNER("beginner", "Iniciante", 1),
    BEGINNER_ADVANCED("beginner_advanced", "Iniciante avançado", 2),
    EARLY_INTERMEDIATE("early_intermediate", "Intermediário inicial", 3),
    INTERMEDIATE("intermediate", "Intermediário", 4),
    UPPER_INTERMEDIATE("upper_intermediate", "Intermediário avançado", 5),
    ADVANCED("advanced", "Avançado", 6);

    private final String value;
    private final String label;
    private final int order;

    LearningStage(String value, String label, int order) {
        this.value = value;
        this.label = label;
        this.order = order;
    }

    @JsonValue
    public String value() { return value; }
    public String label() { return label; }
    public int order() { return order; }

    @JsonCreator
    public static LearningStage from(String value) {
        for (var stage : values()) {
            if (stage.value.equalsIgnoreCase(value)) return stage;
        }
        throw new IllegalArgumentException("Faixa de aprendizagem inválida: " + value);
    }
}
