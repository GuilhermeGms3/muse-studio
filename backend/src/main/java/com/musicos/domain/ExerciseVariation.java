package com.musicos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ExerciseVariation {
    private String name;

    @Column(length = 2000)
    private String instructions;

    private Integer bpmOffset;
    private Integer durationMinutes;

    protected ExerciseVariation() {
    }

    public ExerciseVariation(String name, String instructions, Integer bpmOffset, Integer durationMinutes) {
        this.name = name;
        this.instructions = instructions;
        this.bpmOffset = bpmOffset;
        this.durationMinutes = durationMinutes;
    }

    public String getName() { return name; }
    public String getInstructions() { return instructions; }
    public int getBpmOffset() { return bpmOffset == null ? 0 : bpmOffset; }
    public int getDurationMinutes() { return durationMinutes == null ? 5 : durationMinutes; }
}
