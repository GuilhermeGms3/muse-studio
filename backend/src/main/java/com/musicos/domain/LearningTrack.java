package com.musicos.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LearningTrack {
    TECHNIQUE("technique"),
    RHYTHM("rhythm"),
    EAR("ear"),
    READING("reading"),
    HARMONY("harmony"),
    REPERTOIRE("repertoire"),
    IMPROVISATION("improvisation"),
    CREATION("creation"),
    PERFORMANCE("performance");

    private final String value;

    LearningTrack(String value) { this.value = value; }

    @JsonValue
    public String value() { return value; }

    @JsonCreator
    public static LearningTrack from(String value) {
        for (var track : values()) {
            if (track.value.equalsIgnoreCase(value)) return track;
        }
        throw new IllegalArgumentException("Trilha de aprendizagem inválida: " + value);
    }
}
