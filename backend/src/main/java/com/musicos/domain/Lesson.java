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

@Entity
@Table(name = "learning_lessons")
public class Lesson {
    public enum Format { TEXT, VIDEO, AUDIO, INTERACTIVE, MIXED }

    @Id
    private String id;
    private String title;
    private String technicalName;
    private String category;

    @Column(length = 2000)
    private String summary;

    @Column(length = 10000)
    private String content;

    private int estimatedMinutes;

    @Enumerated(EnumType.STRING)
    private LearningStage stage;

    @Enumerated(EnumType.STRING)
    private Format format;

    private String legacyLibraryContentId;
    private Instant createdAt;
    private Instant updatedAt;

    @ElementCollection
    @CollectionTable(name = "lesson_competencies", joinColumns = @JoinColumn(name = "lesson_id"))
    @OrderColumn(name = "position")
    @Column(name = "competency_id")
    private List<String> competencyIds = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "lesson_objectives", joinColumns = @JoinColumn(name = "lesson_id"))
    @OrderColumn(name = "position")
    @Column(name = "objective", length = 1000)
    private List<String> objectives = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "lesson_examples", joinColumns = @JoinColumn(name = "lesson_id"))
    @OrderColumn(name = "position")
    @Column(name = "example", length = 2000)
    private List<String> examples = new ArrayList<>();

    @Version
    private long version;

    protected Lesson() {
    }

    public void synchronizeLegacyContent(String title, String technicalName, String category, String summary,
                                         String content, int estimatedMinutes, LearningStage stage,
                                         List<String> competencyIds, List<String> objectives,
                                         List<String> examples) {
        if (legacyLibraryContentId == null) throw new IllegalStateException("aula não possui origem legada");
        this.title = DomainRules.requiredText(title, "title");
        this.technicalName = DomainRules.requiredText(technicalName, "technicalName");
        this.category = DomainRules.requiredText(category, "category");
        this.summary = DomainRules.requiredText(summary, "summary");
        this.content = DomainRules.requiredText(content, "content");
        this.estimatedMinutes = DomainRules.between(estimatedMinutes, 1, 480, "estimatedMinutes");
        this.stage = DomainRules.required(stage, "stage");
        this.competencyIds = new ArrayList<>(DomainRules.distinctIds(competencyIds));
        this.objectives = new ArrayList<>(DomainRules.distinctIds(objectives));
        this.examples = examples == null ? new ArrayList<>() : new ArrayList<>(examples);
        this.updatedAt = Instant.now();
    }

    public Lesson(String id, String title, String technicalName, String category, String summary, String content,
                  int estimatedMinutes, LearningStage stage, Format format, List<String> competencyIds,
                  List<String> objectives, List<String> examples, String legacyLibraryContentId) {
        this.id = DomainRules.requiredText(id, "id");
        this.title = DomainRules.requiredText(title, "title");
        this.technicalName = DomainRules.requiredText(technicalName, "technicalName");
        this.category = DomainRules.requiredText(category, "category");
        this.summary = DomainRules.requiredText(summary, "summary");
        this.content = DomainRules.requiredText(content, "content");
        this.estimatedMinutes = DomainRules.between(estimatedMinutes, 1, 480, "estimatedMinutes");
        this.stage = DomainRules.required(stage, "stage");
        this.format = DomainRules.required(format, "format");
        this.competencyIds = new ArrayList<>(DomainRules.distinctIds(competencyIds));
        this.objectives = new ArrayList<>(DomainRules.distinctIds(objectives));
        this.examples = examples == null ? new ArrayList<>() : new ArrayList<>(examples);
        this.legacyLibraryContentId = legacyLibraryContentId;
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getTechnicalName() { return technicalName; }
    public String getCategory() { return category; }
    public String getSummary() { return summary; }
    public String getContent() { return content; }
    public int getEstimatedMinutes() { return estimatedMinutes; }
    public LearningStage getStage() { return stage; }
    public Format getFormat() { return format; }
    public String getLegacyLibraryContentId() { return legacyLibraryContentId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public List<String> getCompetencyIds() { return List.copyOf(competencyIds); }
    public List<String> getObjectives() { return List.copyOf(objectives); }
    public List<String> getExamples() { return List.copyOf(examples); }
}
