package com.musicos.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SessionStatus {
    ACTIVE,
    PAUSED,
    FINISHED;

    @JsonValue
    public String value() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static SessionStatus from(String value) {
        return valueOf(value.toUpperCase());
    }
}
