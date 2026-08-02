package com.musicos.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum InstrumentId {
    GUITAR("guitar"),
    ACOUSTIC("acoustic"),
    KEYS("keys"),
    DRUMS("drums");

    private final String value;

    InstrumentId(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static InstrumentId from(String value) {
        for (InstrumentId id : values()) {
            if (id.value.equalsIgnoreCase(value)) {
                return id;
            }
        }
        throw new IllegalArgumentException("Instrumento inválido: " + value);
    }
}
