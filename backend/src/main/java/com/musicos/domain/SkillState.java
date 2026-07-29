package com.musicos.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SkillState {
    LOCKED("locked"),
    AVAILABLE("available"),
    LEARNING("learning"),
    PRACTICING("practicing"),
    CONSISTENT("consistent"),
    MASTERED("mastered"),
    NATURAL("natural"),
    EXPERT("expert");

    private final String value;

    SkillState(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static SkillState from(String value) {
        for (SkillState state : values()) {
            if (state.value.equalsIgnoreCase(value)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Estado de habilidade inválido: " + value);
    }
}
