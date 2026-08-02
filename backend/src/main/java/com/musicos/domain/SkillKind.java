package com.musicos.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SkillKind {
    KNOWLEDGE("knowledge"),
    ABILITY("ability");

    private final String value;

    SkillKind(String value) { this.value = value; }

    @JsonValue
    public String value() { return value; }

    @JsonCreator
    public static SkillKind from(String value) {
        for (var kind : values()) {
            if (kind.value.equalsIgnoreCase(value)) return kind;
        }
        throw new IllegalArgumentException("Tipo de skill inválido: " + value);
    }
}
