package com.musicos.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ear_training_attempts")
public class EarTrainingAttempt {
    @Id
    @GeneratedValue
    private UUID id;
    private Instant practicedAt;
    private String module;
    private String prompt;
    private String answer;
    private Boolean correct;
    private Integer responseMillis;
    private Integer difficulty;

    protected EarTrainingAttempt() {
    }

    public EarTrainingAttempt(String module, String prompt, String answer, boolean correct,
                              int responseMillis, int difficulty) {
        this.practicedAt = Instant.now();
        this.module = module;
        this.prompt = prompt;
        this.answer = answer;
        this.correct = correct;
        this.responseMillis = responseMillis;
        this.difficulty = difficulty;
    }

    public UUID getId() { return id; }
    public Instant getPracticedAt() { return practicedAt; }
    public String getModule() { return module; }
    public String getPrompt() { return prompt; }
    public String getAnswer() { return answer; }
    public boolean isCorrect() { return Boolean.TRUE.equals(correct); }
    public int getResponseMillis() { return responseMillis == null ? 0 : responseMillis; }
    public int getDifficulty() { return difficulty == null ? 1 : difficulty; }
}
