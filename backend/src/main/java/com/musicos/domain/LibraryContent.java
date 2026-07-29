package com.musicos.domain;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "library_contents")
public class LibraryContent {
    @Id
    private String id;
    private String friendlyTitle;
    private String technicalName;
    private String category;
    private String summary;
    private String skillId;
    private String level;
    private Integer estimatedMinutes;
    private String diagramType;

    @jakarta.persistence.Column(length = 2000)
    private String diagramData;

    @jakarta.persistence.Column(length = 3000)
    private String tablature;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> body = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> examples = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> related = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> objectives = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> commonMistakes = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<LessonStep> steps = new ArrayList<>();

    protected LibraryContent() {
    }

    public LibraryContent(String id, String friendlyTitle, String technicalName, String category, String summary,
                          List<String> body, List<String> examples, List<String> related) {
        this.id = id;
        this.friendlyTitle = friendlyTitle;
        this.technicalName = technicalName;
        this.category = category;
        this.summary = summary;
        this.body = new ArrayList<>(body);
        this.examples = new ArrayList<>(examples);
        this.related = new ArrayList<>(related);
    }

    public LibraryContent(String id, String friendlyTitle, String technicalName, String category, String summary,
                          String skillId, String level, int estimatedMinutes, String diagramType, String diagramData,
                          String tablature, List<String> objectives, List<String> body, List<String> examples,
                          List<String> commonMistakes, List<LessonStep> steps, List<String> related) {
        this(id, friendlyTitle, technicalName, category, summary, body, examples, related);
        this.skillId = skillId;
        this.level = level;
        this.estimatedMinutes = estimatedMinutes;
        this.diagramType = diagramType;
        this.diagramData = diagramData;
        this.tablature = tablature;
        this.objectives = new ArrayList<>(objectives);
        this.commonMistakes = new ArrayList<>(commonMistakes);
        this.steps = new ArrayList<>(steps);
    }

    public void update(String friendlyTitle, String technicalName, String category, String summary, String skillId,
                       String level, int estimatedMinutes, String diagramType, String diagramData, String tablature,
                       List<String> objectives, List<String> body, List<String> examples,
                       List<String> commonMistakes, List<LessonStep> steps, List<String> related) {
        this.friendlyTitle = friendlyTitle;
        this.technicalName = technicalName;
        this.category = category;
        this.summary = summary;
        this.skillId = skillId;
        this.level = level;
        this.estimatedMinutes = estimatedMinutes;
        this.diagramType = diagramType;
        this.diagramData = diagramData;
        this.tablature = tablature;
        this.objectives = new ArrayList<>(objectives);
        this.body = new ArrayList<>(body);
        this.examples = new ArrayList<>(examples);
        this.commonMistakes = new ArrayList<>(commonMistakes);
        this.steps = new ArrayList<>(steps);
        this.related = new ArrayList<>(related);
    }

    public String getId() { return id; }
    public String getFriendlyTitle() { return friendlyTitle; }
    public String getTechnicalName() { return technicalName; }
    public String getCategory() { return category; }
    public String getSummary() { return summary; }
    public List<String> getBody() { return List.copyOf(body); }
    public List<String> getExamples() { return List.copyOf(examples); }
    public List<String> getRelated() { return List.copyOf(related); }
    public String getSkillId() { return skillId; }
    public String getLevel() { return level == null ? "beginner" : level; }
    public int getEstimatedMinutes() { return estimatedMinutes == null ? 12 : estimatedMinutes; }
    public String getDiagramType() { return diagramType; }
    public String getDiagramData() { return diagramData; }
    public String getTablature() { return tablature; }
    public List<String> getObjectives() { return List.copyOf(objectives); }
    public List<String> getCommonMistakes() { return List.copyOf(commonMistakes); }
    public List<LessonStep> getSteps() { return List.copyOf(steps); }
}
