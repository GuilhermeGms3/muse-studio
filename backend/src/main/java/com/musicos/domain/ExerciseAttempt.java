package com.musicos.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "exercise_attempts")
public class ExerciseAttempt {
    @Id
    @GeneratedValue
    private UUID id;
    private String exerciseId;
    private Instant practicedAt;
    private Integer bpm;
    private Integer accuracy;
    private Long durationSeconds;
    private Integer repetitions;
    private Integer perceivedDifficulty;
    private Boolean passed;
    private UUID missionExperienceId;

    protected ExerciseAttempt() {
    }

    public ExerciseAttempt(String exerciseId, int bpm, int accuracy, long durationSeconds, int repetitions,
                           int perceivedDifficulty, boolean passed) {
        this(exerciseId, bpm, accuracy, durationSeconds, repetitions, perceivedDifficulty, passed, null);
    }

    public ExerciseAttempt(String exerciseId, int bpm, int accuracy, long durationSeconds, int repetitions,
                           int perceivedDifficulty, boolean passed, UUID missionExperienceId) {
        this.exerciseId = exerciseId;
        this.practicedAt = Instant.now();
        this.bpm = bpm;
        this.accuracy = accuracy;
        this.durationSeconds = durationSeconds;
        this.repetitions = repetitions;
        this.perceivedDifficulty = perceivedDifficulty;
        this.passed = passed;
        this.missionExperienceId = missionExperienceId;
    }

    public UUID getId() { return id; }
    public String getExerciseId() { return exerciseId; }
    public Instant getPracticedAt() { return practicedAt; }
    public int getBpm() { return bpm == null ? 0 : bpm; }
    public int getAccuracy() { return accuracy == null ? 0 : accuracy; }
    public long getDurationSeconds() { return durationSeconds == null ? 0 : durationSeconds; }
    public int getRepetitions() { return repetitions == null ? 0 : repetitions; }
    public int getPerceivedDifficulty() { return perceivedDifficulty == null ? 3 : perceivedDifficulty; }
    public boolean isPassed() { return Boolean.TRUE.equals(passed); }
    public UUID getMissionExperienceId() { return missionExperienceId; }
}
