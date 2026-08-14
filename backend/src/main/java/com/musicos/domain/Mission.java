package com.musicos.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
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

@Entity
@Table(name = "learning_missions")
public class Mission {
    public enum Status { DRAFT, ACTIVE, RETIRED }

    @Id
    private String id;
    private String curriculumId;
    private String title;

    @Column(length = 2000)
    private String observableObjective;

    @Column(length = 2000)
    private String context;

    @Column(length = 2000)
    private String motivation;

    private int estimatedMinutes;

    @Enumerated(EnumType.STRING)
    private InstrumentId instrument;

    @Enumerated(EnumType.STRING)
    private LearningStage stage;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(length = 2000)
    private String completionCriteria;

    @Column(length = 2000)
    private String expectedEvidence;

    @Column(length = 2000)
    private String musicalApplication;

    private String easierMissionId;

    @Embedded
    private DifficultyDemand difficultyDemand;

    private Instant createdAt;
    private Instant updatedAt;

    @ElementCollection
    @CollectionTable(name = "mission_competencies", joinColumns = @JoinColumn(name = "mission_id"))
    @OrderColumn(name = "position")
    @Column(name = "competency_id")
    private List<String> competencyIds = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "mission_lessons", joinColumns = @JoinColumn(name = "mission_id"))
    @OrderColumn(name = "position")
    @Column(name = "lesson_id")
    private List<String> lessonIds = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "mission_exercises", joinColumns = @JoinColumn(name = "mission_id"))
    @OrderColumn(name = "position")
    @Column(name = "exercise_id")
    private List<String> exerciseIds = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "mission_assessments", joinColumns = @JoinColumn(name = "mission_id"))
    @OrderColumn(name = "position")
    @Column(name = "assessment_id")
    private List<String> assessmentIds = new ArrayList<>();

    @Version
    private long version;

    protected Mission() {
    }

    public Mission(String id, String curriculumId, String title, String observableObjective, String context,
                   String motivation, int estimatedMinutes, InstrumentId instrument, LearningStage stage,
                   String completionCriteria, String expectedEvidence, String musicalApplication,
                   String easierMissionId, DifficultyDemand difficultyDemand, List<String> competencyIds,
                   List<String> lessonIds, List<String> exerciseIds, List<String> assessmentIds) {
        this.id = DomainRules.requiredText(id, "id");
        this.curriculumId = DomainRules.requiredText(curriculumId, "curriculumId");
        this.title = DomainRules.requiredText(title, "title");
        this.observableObjective = DomainRules.requiredText(observableObjective, "observableObjective");
        this.context = DomainRules.requiredText(context, "context");
        this.motivation = DomainRules.requiredText(motivation, "motivation");
        this.estimatedMinutes = DomainRules.between(estimatedMinutes, 1, 480, "estimatedMinutes");
        this.instrument = instrument;
        this.stage = DomainRules.required(stage, "stage");
        this.completionCriteria = DomainRules.requiredText(completionCriteria, "completionCriteria");
        this.expectedEvidence = DomainRules.requiredText(expectedEvidence, "expectedEvidence");
        this.musicalApplication = musicalApplication;
        this.easierMissionId = easierMissionId;
        this.difficultyDemand = difficultyDemand == null ? DifficultyDemand.unspecified() : difficultyDemand;
        this.competencyIds = new ArrayList<>(DomainRules.distinctIds(competencyIds));
        if (this.competencyIds.isEmpty()) throw new IllegalArgumentException("missão precisa trabalhar uma competência");
        this.lessonIds = new ArrayList<>(DomainRules.distinctIds(lessonIds));
        this.exerciseIds = new ArrayList<>(DomainRules.distinctIds(exerciseIds));
        this.assessmentIds = new ArrayList<>(DomainRules.distinctIds(assessmentIds));
        this.status = Status.DRAFT;
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
    }

    public void activate() {
        this.status = Status.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void retire() {
        this.status = Status.RETIRED;
        this.updatedAt = Instant.now();
    }

    public void synchronizeCatalogDefinition(String title, String observableObjective, String context,
                                             String motivation, int estimatedMinutes, InstrumentId instrument,
                                             LearningStage stage, String completionCriteria,
                                             String expectedEvidence, String musicalApplication,
                                             String easierMissionId, DifficultyDemand difficultyDemand,
                                             List<String> competencyIds, List<String> lessonIds,
                                             List<String> exerciseIds, List<String> assessmentIds) {
        this.title = DomainRules.requiredText(title, "title");
        this.observableObjective = DomainRules.requiredText(observableObjective, "observableObjective");
        this.context = DomainRules.requiredText(context, "context");
        this.motivation = DomainRules.requiredText(motivation, "motivation");
        this.estimatedMinutes = DomainRules.between(estimatedMinutes, 1, 480, "estimatedMinutes");
        this.instrument = instrument;
        this.stage = DomainRules.required(stage, "stage");
        this.completionCriteria = DomainRules.requiredText(completionCriteria, "completionCriteria");
        this.expectedEvidence = DomainRules.requiredText(expectedEvidence, "expectedEvidence");
        this.musicalApplication = musicalApplication;
        this.easierMissionId = easierMissionId;
        this.difficultyDemand = difficultyDemand == null ? DifficultyDemand.unspecified() : difficultyDemand;
        this.competencyIds = new ArrayList<>(DomainRules.distinctIds(competencyIds));
        this.lessonIds = new ArrayList<>(DomainRules.distinctIds(lessonIds));
        this.exerciseIds = new ArrayList<>(DomainRules.distinctIds(exerciseIds));
        this.assessmentIds = new ArrayList<>(DomainRules.distinctIds(assessmentIds));
        if (this.competencyIds.isEmpty()) throw new IllegalArgumentException("missão precisa trabalhar uma competência");
        this.updatedAt = Instant.now();
    }

    public String getId() { return id; }
    public String getCurriculumId() { return curriculumId; }
    public String getTitle() { return title; }
    public String getObservableObjective() { return observableObjective; }
    public String getContext() { return context; }
    public String getMotivation() { return motivation; }
    public int getEstimatedMinutes() { return estimatedMinutes; }
    public InstrumentId getInstrument() { return instrument; }
    public LearningStage getStage() { return stage; }
    public Status getStatus() { return status; }
    public String getCompletionCriteria() { return completionCriteria; }
    public String getExpectedEvidence() { return expectedEvidence; }
    public String getMusicalApplication() { return musicalApplication; }
    public String getEasierMissionId() { return easierMissionId; }
    public DifficultyDemand getDifficultyDemand() { return difficultyDemand; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public List<String> getCompetencyIds() { return List.copyOf(competencyIds); }
    public List<String> getLessonIds() { return List.copyOf(lessonIds); }
    public List<String> getExerciseIds() { return List.copyOf(exerciseIds); }
    public List<String> getAssessmentIds() { return List.copyOf(assessmentIds); }
}
