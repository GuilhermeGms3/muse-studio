package com.musicos.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.util.ArrayList;
import java.util.List;
import java.time.Instant;

@Entity
@Table(name = "skills")
public class Skill {
    @Id
    private String id;
    private String friendlyTitle;
    private String technicalName;
    private String domain;

    @Column(length = 3000)
    private String description;

    @Enumerated(EnumType.STRING)
    private SkillState state;

    private double hours;
    private int accuracy;
    private Integer currentBpm;
    private Integer targetBpm;
    private Integer practiceDays = 0;
    private Integer reviewCount = 0;
    private Integer exerciseCompletions = 0;
    private Integer songsCompleted = 0;
    private Integer selfRating = 0;
    private Instant lastPracticedAt;
    private Integer retention = 100;
    private Integer reviewIntervalDays = 1;
    private Instant nextReviewAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "skill_instruments", joinColumns = @JoinColumn(name = "skill_id"))
    @Enumerated(EnumType.STRING)
    private List<InstrumentId> instruments = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> prerequisites = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> contents = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> exercises = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> songs = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> nextSkills = new ArrayList<>();

    @Version
    private long version;

    protected Skill() {
    }

    public Skill(String id, String friendlyTitle, String technicalName, String domain, String description,
                 SkillState state, double hours, int accuracy, Integer currentBpm, Integer targetBpm,
                 List<InstrumentId> instruments, List<String> prerequisites, List<String> contents,
                 List<String> exercises, List<String> songs, List<String> nextSkills) {
        this.id = id;
        this.friendlyTitle = friendlyTitle;
        this.technicalName = technicalName;
        this.domain = domain;
        this.description = description;
        this.state = state;
        this.hours = hours;
        this.accuracy = accuracy;
        this.currentBpm = currentBpm;
        this.targetBpm = targetBpm;
        this.instruments = new ArrayList<>(instruments);
        this.prerequisites = new ArrayList<>(prerequisites);
        this.contents = new ArrayList<>(contents);
        this.exercises = new ArrayList<>(exercises);
        this.songs = new ArrayList<>(songs);
        this.nextSkills = new ArrayList<>(nextSkills);
    }

    public void changeState(SkillState state) { this.state = state; }

    public void recordEvidence(double hours, Integer accuracy, Integer bpm, boolean review,
                               boolean exerciseCompleted, boolean songCompleted, Integer selfRating) {
        recordEvidence(hours, accuracy, bpm, review, exerciseCompleted, songCompleted, selfRating, null);
    }

    public void recordEvidence(double hours, Integer accuracy, Integer bpm, boolean review,
                               boolean exerciseCompleted, boolean songCompleted, Integer selfRating,
                               Integer perceivedDifficulty) {
        this.hours = Math.max(0, this.hours + hours);
        if (accuracy != null) this.accuracy = Math.max(0, Math.min(100, accuracy));
        if (bpm != null) this.currentBpm = Math.max(0, bpm);
        if (review) this.reviewCount = getReviewCount() + 1;
        if (exerciseCompleted) this.exerciseCompletions = getExerciseCompletions() + 1;
        if (songCompleted) this.songsCompleted = getSongsCompleted() + 1;
        if (selfRating != null) this.selfRating = Math.max(1, Math.min(5, selfRating));
        var now = Instant.now();
        if (lastPracticedAt == null
                || !lastPracticedAt.atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                .equals(now.atZone(java.time.ZoneId.systemDefault()).toLocalDate())) {
            this.practiceDays = getPracticeDays() + 1;
        }
        this.lastPracticedAt = now;
        scheduleReview(now, accuracy, perceivedDifficulty);
    }

    private void scheduleReview(Instant now, Integer accuracy, Integer perceivedDifficulty) {
        var result = accuracy == null ? this.accuracy : accuracy;
        var difficulty = perceivedDifficulty == null ? 3 : Math.max(1, Math.min(5, perceivedDifficulty));
        if (result < 65) {
            this.reviewIntervalDays = 1;
            this.retention = Math.max(20, getRetention() - 15);
        } else {
            var multiplier = result >= 90 ? 2.2 : result >= 80 ? 1.7 : 1.3;
            var difficultyFactor = 1.25 - difficulty * .1;
            this.reviewIntervalDays = Math.max(1, Math.min(90,
                    (int) Math.round(getReviewIntervalDays() * multiplier * difficultyFactor)));
            this.retention = Math.min(100, getRetention() + (result >= 85 ? 5 : 2));
        }
        this.nextReviewAt = now.plus(java.time.Duration.ofDays(getReviewIntervalDays()));
    }

    public void applyCalculatedState(SkillState state) { this.state = state; }
    public void applyDiagnosticPlacement(int score, Integer suggestedBpm) {
        var safeScore = Math.max(0, Math.min(100, score));
        this.accuracy = safeScore;
        if (suggestedBpm != null) this.currentBpm = Math.max(30, suggestedBpm);
        this.state = safeScore >= 82 ? SkillState.PRACTICING
                : safeScore >= 45 ? SkillState.LEARNING : SkillState.AVAILABLE;
        this.retention = Math.max(45, safeScore);
        this.reviewIntervalDays = safeScore >= 82 ? 5 : safeScore >= 45 ? 2 : 1;
        this.nextReviewAt = Instant.now().plus(java.time.Duration.ofDays(this.reviewIntervalDays));
    }
    public void restoreProgress(SkillState state, double hours, int accuracy, Integer currentBpm,
                                int practiceDays, int reviewCount, int exerciseCompletions,
                                int songsCompleted, int selfRating, Instant lastPracticedAt,
                                int retention, int reviewIntervalDays, Instant nextReviewAt) {
        this.state = state;
        this.hours = Math.max(0, hours);
        this.accuracy = Math.max(0, Math.min(100, accuracy));
        this.currentBpm = currentBpm;
        this.practiceDays = Math.max(0, practiceDays);
        this.reviewCount = Math.max(0, reviewCount);
        this.exerciseCompletions = Math.max(0, exerciseCompletions);
        this.songsCompleted = Math.max(0, songsCompleted);
        this.selfRating = Math.max(0, Math.min(5, selfRating));
        this.lastPracticedAt = lastPracticedAt;
        this.retention = Math.max(0, Math.min(100, retention));
        this.reviewIntervalDays = Math.max(1, reviewIntervalDays);
        this.nextReviewAt = nextReviewAt;
    }
    public void attachContent(String contentId) {
        if (!contents.contains(contentId)) contents.add(contentId);
    }
    public void attachExercise(String exerciseId) {
        if (!exercises.contains(exerciseId)) exercises.add(exerciseId);
    }
    public void attachInstrument(InstrumentId instrumentId) {
        if (!instruments.contains(instrumentId)) instruments.add(instrumentId);
    }
    public void detachInstrument(InstrumentId instrumentId) {
        instruments.remove(instrumentId);
    }
    public void refreshDefinition(String friendlyTitle, String technicalName, String domain,
                                  String description, Integer targetBpm, List<String> prerequisites,
                                  List<String> nextSkills, List<InstrumentId> instruments) {
        this.friendlyTitle = friendlyTitle;
        this.technicalName = technicalName;
        this.domain = domain;
        this.description = description;
        this.targetBpm = targetBpm;
        this.prerequisites = new ArrayList<>(prerequisites);
        this.nextSkills = new ArrayList<>(nextSkills);
        instruments.forEach(this::attachInstrument);
    }
    public String getId() { return id; }
    public String getFriendlyTitle() { return friendlyTitle; }
    public String getTechnicalName() { return technicalName; }
    public String getDomain() { return domain; }
    public String getDescription() { return description; }
    public SkillState getState() { return state; }
    public double getHours() { return hours; }
    public int getAccuracy() { return accuracy; }
    public Integer getCurrentBpm() { return currentBpm; }
    public Integer getTargetBpm() { return targetBpm; }
    public int getPracticeDays() { return practiceDays == null ? 0 : practiceDays; }
    public int getReviewCount() { return reviewCount == null ? 0 : reviewCount; }
    public int getExerciseCompletions() { return exerciseCompletions == null ? 0 : exerciseCompletions; }
    public int getSongsCompleted() { return songsCompleted == null ? 0 : songsCompleted; }
    public int getSelfRating() { return selfRating == null ? 0 : selfRating; }
    public Instant getLastPracticedAt() { return lastPracticedAt; }
    public int getRetention() { return retention == null ? 100 : retention; }
    public int getReviewIntervalDays() { return reviewIntervalDays == null ? 1 : reviewIntervalDays; }
    public Instant getNextReviewAt() { return nextReviewAt; }
    public List<InstrumentId> getInstruments() { return List.copyOf(instruments); }
    public List<String> getPrerequisites() { return List.copyOf(prerequisites); }
    public List<String> getContents() { return List.copyOf(contents); }
    public List<String> getExercises() { return List.copyOf(exercises); }
    public List<String> getSongs() { return List.copyOf(songs); }
    public List<String> getNextSkills() { return List.copyOf(nextSkills); }
}
