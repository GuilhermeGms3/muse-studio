package com.musicos.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "exercises")
public class Exercise {
    @Id
    private String id;
    private String name;
    private String technique;

    @Enumerated(EnumType.STRING)
    private InstrumentId instrument;

    private int targetBpm;
    private int currentBpm;
    private int minutes;
    private String description;
    private String skillId;
    private Integer difficulty;
    private Integer minBpm;
    private Integer bpmStep;
    private Integer passAccuracy;
    private Integer passRepetitions;
    private String activityType;

    @Enumerated(EnumType.STRING)
    private LearningStage stage;

    private String videoQuery;
    private String readingTitle;
    private String readingUrl;
    private String readingNote;
    private String practiceSongQuery;

    private String observableObjective;

    @jakarta.persistence.Column(length = 2000)
    private String practiceConditions;

    @jakarta.persistence.Column(length = 2000)
    private String successCriteria;

    @Embedded
    private DifficultyDemand difficultyDemand;

    @ElementCollection(fetch = FetchType.EAGER)
    @jakarta.persistence.CollectionTable(name = "exercise_competencies",
            joinColumns = @jakarta.persistence.JoinColumn(name = "exercise_id"))
    @jakarta.persistence.OrderColumn(name = "position")
    @jakarta.persistence.Column(name = "competency_id")
    private List<String> competencyIds = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> instructions = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<ExerciseVariation> variations = new ArrayList<>();

    protected Exercise() {
    }

    public Exercise(String id, String name, String technique, InstrumentId instrument, int targetBpm,
                    int currentBpm, int minutes, String description, String skillId) {
        this.id = id;
        this.name = name;
        this.technique = technique;
        this.instrument = instrument;
        this.targetBpm = targetBpm;
        this.currentBpm = currentBpm;
        this.minutes = minutes;
        this.description = description;
        this.skillId = skillId;
        this.difficulty = 2;
        this.minBpm = Math.max(30, Math.min(currentBpm, targetBpm) - 20);
        this.bpmStep = 4;
        this.passAccuracy = 85;
        this.passRepetitions = 3;
        this.difficultyDemand = DifficultyDemand.unspecified();
        if (skillId != null && !skillId.isBlank()) this.competencyIds.add(skillId);
    }

    public Exercise(String id, String name, String technique, InstrumentId instrument, int targetBpm,
                    int currentBpm, int minutes, String description, String skillId, int difficulty,
                    int minBpm, int bpmStep, int passAccuracy, int passRepetitions,
                    List<String> instructions, List<ExerciseVariation> variations) {
        this(id, name, technique, instrument, targetBpm, currentBpm, minutes, description, skillId);
        this.difficulty = difficulty;
        this.minBpm = minBpm;
        this.bpmStep = bpmStep;
        this.passAccuracy = passAccuracy;
        this.passRepetitions = passRepetitions;
        this.instructions = new ArrayList<>(instructions);
        this.variations = new ArrayList<>(variations);
    }

    public void update(String name, String technique, InstrumentId instrument, int targetBpm, int currentBpm,
                       int minutes, String description, String skillId, int difficulty, int minBpm, int bpmStep,
                       int passAccuracy, int passRepetitions, List<String> instructions,
                       List<ExerciseVariation> variations) {
        var previousSkillId = this.skillId;
        this.name = name;
        this.technique = technique;
        this.instrument = instrument;
        this.targetBpm = targetBpm;
        this.currentBpm = currentBpm;
        this.minutes = minutes;
        this.description = description;
        this.skillId = skillId;
        this.difficulty = difficulty;
        this.minBpm = minBpm;
        this.bpmStep = bpmStep;
        this.passAccuracy = passAccuracy;
        this.passRepetitions = passRepetitions;
        this.instructions = new ArrayList<>(instructions);
        this.variations = new ArrayList<>(variations);
        if (competencyIds.isEmpty()
                || (competencyIds.size() == 1 && Objects.equals(competencyIds.getFirst(), previousSkillId))) {
            this.competencyIds = skillId == null || skillId.isBlank()
                    ? new ArrayList<>() : new ArrayList<>(List.of(skillId));
        }
    }

    public Exercise withLearningResources(String activityType, LearningStage stage, String videoQuery,
                                          String readingTitle, String readingUrl, String readingNote,
                                          String practiceSongQuery) {
        this.activityType = activityType;
        this.stage = stage;
        this.videoQuery = videoQuery;
        this.readingTitle = readingTitle;
        this.readingUrl = readingUrl;
        this.readingNote = readingNote;
        this.practiceSongQuery = practiceSongQuery;
        return this;
    }

    public void updateLearningResources(String activityType, LearningStage stage, String videoQuery,
                                        String readingTitle, String readingUrl, String readingNote,
                                        String practiceSongQuery) {
        withLearningResources(activityType, stage, videoQuery, readingTitle, readingUrl, readingNote,
                practiceSongQuery);
    }

    public void recordResult(int bpm, boolean passed) {
        if (passed && bpm >= currentBpm) currentBpm = Math.min(targetBpm, bpm + getBpmStep());
    }

    public void configurePedagogicalDefinition(String observableObjective, String practiceConditions,
                                                String successCriteria, DifficultyDemand difficultyDemand,
                                                List<String> competencyIds) {
        this.observableObjective = DomainRules.requiredText(observableObjective, "observableObjective");
        this.practiceConditions = DomainRules.requiredText(practiceConditions, "practiceConditions");
        this.successCriteria = DomainRules.requiredText(successCriteria, "successCriteria");
        this.difficultyDemand = difficultyDemand == null ? DifficultyDemand.unspecified() : difficultyDemand;
        this.competencyIds = new ArrayList<>(DomainRules.distinctIds(competencyIds));
        if (this.competencyIds.isEmpty()) {
            throw new IllegalArgumentException("exercício precisa desenvolver uma competência");
        }
    }

    public void migrateLegacyCompetency() {
        if (competencyIds.isEmpty() && skillId != null && !skillId.isBlank()) competencyIds.add(skillId);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getTechnique() { return technique; }
    public InstrumentId getInstrument() { return instrument; }
    public int getTargetBpm() { return targetBpm; }
    public int getCurrentBpm() { return currentBpm; }
    public int getMinutes() { return minutes; }
    public String getDescription() { return description; }
    public String getSkillId() { return skillId; }
    public int getDifficulty() { return difficulty == null ? 2 : difficulty; }
    public int getMinBpm() { return minBpm == null ? Math.max(30, currentBpm - 20) : minBpm; }
    public int getBpmStep() { return bpmStep == null ? 4 : bpmStep; }
    public int getPassAccuracy() { return passAccuracy == null ? 85 : passAccuracy; }
    public int getPassRepetitions() { return passRepetitions == null ? 3 : passRepetitions; }
    public List<String> getInstructions() { return List.copyOf(instructions); }
    public List<ExerciseVariation> getVariations() { return List.copyOf(variations); }
    public String getActivityType() { return activityType == null ? "execute" : activityType; }
    public LearningStage getStage() { return stage == null ? LearningStage.INTERMEDIATE : stage; }
    public String getVideoQuery() { return videoQuery; }
    public String getReadingTitle() { return readingTitle; }
    public String getReadingUrl() { return readingUrl; }
    public String getReadingNote() { return readingNote; }
    public String getPracticeSongQuery() { return practiceSongQuery; }
    public String getObservableObjective() { return observableObjective; }
    public String getPracticeConditions() { return practiceConditions; }
    public String getSuccessCriteria() { return successCriteria; }
    public DifficultyDemand getDifficultyDemand() {
        return difficultyDemand == null ? DifficultyDemand.unspecified() : difficultyDemand;
    }
    public List<String> getCompetencyIds() { return List.copyOf(competencyIds); }
}
