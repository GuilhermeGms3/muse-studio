package com.musicos.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import java.util.ArrayList;
import java.util.List;

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
}
