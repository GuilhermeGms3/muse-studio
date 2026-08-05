package com.musicos.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "learning_paths")
public class LearningPath {
    public enum Status { ACTIVE, PAUSED, SUPERSEDED }

    @Id
    private String id;

    @Column(name = "instrument_profile_id")
    private String instrumentProfileId;

    @Column(name = "curriculum_id")
    private String curriculumId;

    private String title;
    private String derivationVersion;

    @Column(length = 3000)
    private String derivationReason;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Instant createdAt;
    private Instant updatedAt;

    @ElementCollection
    @CollectionTable(name = "learning_path_goals", joinColumns = @JoinColumn(name = "learning_path_id"))
    @OrderColumn(name = "position")
    @Column(name = "learning_goal_id")
    private List<String> learningGoalIds = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "learning_path_steps", joinColumns = @JoinColumn(name = "learning_path_id"))
    @OrderColumn(name = "position")
    private List<LearningPathStep> steps = new ArrayList<>();

    @Version
    private long version;

    protected LearningPath() {
    }

    public LearningPath(String instrumentProfileId, String curriculumId, String title,
                        String derivationVersion, String derivationReason,
                        List<String> learningGoalIds, List<LearningPathStep> steps) {
        this(UUID.randomUUID().toString(), instrumentProfileId, curriculumId, title, derivationVersion,
                derivationReason, learningGoalIds, steps);
    }

    public LearningPath(String id, String instrumentProfileId, String curriculumId, String title,
                        String derivationVersion, String derivationReason,
                        List<String> learningGoalIds, List<LearningPathStep> steps) {
        this.id = DomainRules.requiredText(id, "id");
        this.instrumentProfileId = DomainRules.requiredText(instrumentProfileId, "instrumentProfileId");
        this.curriculumId = DomainRules.requiredText(curriculumId, "curriculumId");
        this.title = DomainRules.requiredText(title, "title");
        this.derivationVersion = DomainRules.requiredText(derivationVersion, "derivationVersion");
        this.derivationReason = DomainRules.requiredText(derivationReason, "derivationReason");
        this.learningGoalIds = new ArrayList<>(DomainRules.distinctIds(learningGoalIds));
        this.steps = steps == null ? new ArrayList<>() : new ArrayList<>(steps.stream().distinct().toList());
        if (this.steps.isEmpty()) throw new IllegalArgumentException("learning path precisa de ao menos um passo");
        this.status = Status.ACTIVE;
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
    }

    public void synchronizeDerivedPath(String title, String derivationVersion, String derivationReason,
                                       List<String> learningGoalIds, List<LearningPathStep> steps) {
        this.title = DomainRules.requiredText(title, "title");
        this.derivationVersion = DomainRules.requiredText(derivationVersion, "derivationVersion");
        this.derivationReason = DomainRules.requiredText(derivationReason, "derivationReason");
        this.learningGoalIds = new ArrayList<>(DomainRules.distinctIds(learningGoalIds));
        this.steps = steps == null ? new ArrayList<>() : new ArrayList<>(steps.stream().distinct().toList());
        if (this.steps.isEmpty()) throw new IllegalArgumentException("learning path precisa de ao menos um passo");
        this.updatedAt = Instant.now();
    }

    public void pause() { this.status = Status.PAUSED; this.updatedAt = Instant.now(); }
    public void resume() { this.status = Status.ACTIVE; this.updatedAt = Instant.now(); }
    public void supersede() { this.status = Status.SUPERSEDED; this.updatedAt = Instant.now(); }

    public String getId() { return id; }
    public String getInstrumentProfileId() { return instrumentProfileId; }
    public String getCurriculumId() { return curriculumId; }
    public String getTitle() { return title; }
    public String getDerivationVersion() { return derivationVersion; }
    public String getDerivationReason() { return derivationReason; }
    public Status getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public List<String> getLearningGoalIds() { return List.copyOf(learningGoalIds); }
    public List<LearningPathStep> getSteps() { return List.copyOf(steps); }
}
