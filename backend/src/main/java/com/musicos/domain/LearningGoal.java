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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "learning_goals")
public class LearningGoal {
    public enum Type { FOUNDATIONS, REPERTOIRE, TECHNIQUE, IMPROVISATION, COMPOSITION, PERFORMANCE, EXAM, CUSTOM }
    public enum Status { ACTIVE, PAUSED, ACHIEVED, ABANDONED }

    @Id
    private String id;
    private String instrumentProfileId;
    private String curriculumId;
    private String title;

    @Column(length = 3000)
    private String desiredOutcome;

    @Column(length = 2000)
    private String musicalContext;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private Status status;

    private int priority;
    private LocalDate targetDate;
    private Instant createdAt;
    private Instant updatedAt;

    @ElementCollection
    @CollectionTable(name = "learning_goal_competencies", joinColumns = @JoinColumn(name = "learning_goal_id"))
    @OrderColumn(name = "position")
    @Column(name = "competency_id")
    private List<String> priorityCompetencyIds = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "learning_goal_repertoire", joinColumns = @JoinColumn(name = "learning_goal_id"))
    @OrderColumn(name = "position")
    @Column(name = "repertoire_id")
    private List<String> repertoireIds = new ArrayList<>();

    @Version
    private long version;

    protected LearningGoal() {
    }

    public LearningGoal(String instrumentProfileId, String curriculumId, String title, String desiredOutcome,
                        String musicalContext, Type type, int priority, LocalDate targetDate,
                        List<String> priorityCompetencyIds, List<String> repertoireIds) {
        this.id = UUID.randomUUID().toString();
        this.instrumentProfileId = DomainRules.requiredText(instrumentProfileId, "instrumentProfileId");
        this.curriculumId = curriculumId;
        this.title = DomainRules.requiredText(title, "title");
        this.desiredOutcome = DomainRules.requiredText(desiredOutcome, "desiredOutcome");
        this.musicalContext = DomainRules.requiredText(musicalContext, "musicalContext");
        this.type = DomainRules.required(type, "type");
        this.priority = DomainRules.between(priority, 1, 5, "priority");
        this.targetDate = targetDate;
        this.priorityCompetencyIds = new ArrayList<>(DomainRules.distinctIds(priorityCompetencyIds));
        this.repertoireIds = new ArrayList<>(DomainRules.distinctIds(repertoireIds));
        this.status = Status.ACTIVE;
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
    }

    public void pause() { this.status = Status.PAUSED; this.updatedAt = Instant.now(); }
    public void resume() { this.status = Status.ACTIVE; this.updatedAt = Instant.now(); }
    public void achieve() { this.status = Status.ACHIEVED; this.updatedAt = Instant.now(); }
    public void abandon() { this.status = Status.ABANDONED; this.updatedAt = Instant.now(); }

    public static LearningGoal restoreSnapshot(
            String id, String instrumentProfileId, String curriculumId, String title,
            String desiredOutcome, String musicalContext, Type type, Status status, int priority,
            LocalDate targetDate, List<String> priorityCompetencyIds, List<String> repertoireIds,
            Instant createdAt, Instant updatedAt) {
        var goal = new LearningGoal(instrumentProfileId, curriculumId, title, desiredOutcome,
                musicalContext, type, priority, targetDate, priorityCompetencyIds, repertoireIds);
        goal.id = DomainRules.requiredText(id, "id");
        goal.status = DomainRules.required(status, "status");
        goal.createdAt = createdAt == null ? Instant.now() : createdAt;
        goal.updatedAt = updatedAt == null ? goal.createdAt : updatedAt;
        return goal;
    }

    public void restoreState(String instrumentProfileId, String curriculumId, String title,
                             String desiredOutcome, String musicalContext, Type type, Status status,
                             int priority, LocalDate targetDate, List<String> priorityCompetencyIds,
                             List<String> repertoireIds, Instant createdAt, Instant updatedAt) {
        if (!this.instrumentProfileId.equals(instrumentProfileId)) {
            throw new IllegalArgumentException("backup de objetivo diverge do perfil persistido");
        }
        this.curriculumId = curriculumId;
        this.title = DomainRules.requiredText(title, "title");
        this.desiredOutcome = DomainRules.requiredText(desiredOutcome, "desiredOutcome");
        this.musicalContext = DomainRules.requiredText(musicalContext, "musicalContext");
        this.type = DomainRules.required(type, "type");
        this.status = DomainRules.required(status, "status");
        this.priority = DomainRules.between(priority, 1, 5, "priority");
        this.targetDate = targetDate;
        this.priorityCompetencyIds = new ArrayList<>(DomainRules.distinctIds(priorityCompetencyIds));
        this.repertoireIds = new ArrayList<>(DomainRules.distinctIds(repertoireIds));
        this.createdAt = createdAt == null ? this.createdAt : createdAt;
        this.updatedAt = updatedAt == null ? Instant.now() : updatedAt;
    }

    public String getId() { return id; }
    public String getInstrumentProfileId() { return instrumentProfileId; }
    public String getCurriculumId() { return curriculumId; }
    public String getTitle() { return title; }
    public String getDesiredOutcome() { return desiredOutcome; }
    public String getMusicalContext() { return musicalContext; }
    public Type getType() { return type; }
    public Status getStatus() { return status; }
    public int getPriority() { return priority; }
    public LocalDate getTargetDate() { return targetDate; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public List<String> getPriorityCompetencyIds() { return List.copyOf(priorityCompetencyIds); }
    public List<String> getRepertoireIds() { return List.copyOf(repertoireIds); }
}
