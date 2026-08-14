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
@Table(name = "learning_assessments")
public class Assessment {
    public enum Type { DIAGNOSTIC, FORMATIVE, PERFORMANCE, APPLICATION, TRANSFER, RETENTION, REVIEW }

    @Id
    private String id;
    private String title;
    private String purpose;

    @Enumerated(EnumType.STRING)
    private Type type;

    private String protocolVersion;

    @Column(length = 5000)
    private String instructions;

    @Column(length = 2000)
    private String conditions;

    @Column(length = 2000)
    private String allowedSupport;

    @Column(length = 2000)
    private String inconclusiveRule;

    private int estimatedMinutes;
    private int maximumAttempts;
    private boolean active;

    @Embedded
    private DifficultyDemand difficultyDemand;

    private Instant createdAt;
    private Instant updatedAt;

    @ElementCollection
    @CollectionTable(name = "assessment_competencies", joinColumns = @JoinColumn(name = "assessment_id"))
    @OrderColumn(name = "position")
    @Column(name = "competency_id")
    private List<String> competencyIds = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "assessment_criteria", joinColumns = @JoinColumn(name = "assessment_id"))
    @OrderColumn(name = "position")
    @Column(name = "criterion_key")
    private List<String> criterionKeys = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "assessment_rubric_levels", joinColumns = @JoinColumn(name = "assessment_id"))
    @OrderColumn(name = "position")
    private List<AssessmentRubricLevel> rubricLevels = new ArrayList<>();

    @Version
    private long version;

    protected Assessment() {
    }

    public Assessment(String id, String title, String purpose, Type type, String protocolVersion,
                      String instructions, String conditions, String allowedSupport, String inconclusiveRule,
                      int estimatedMinutes, int maximumAttempts, DifficultyDemand difficultyDemand,
                      List<String> competencyIds, List<String> criterionKeys) {
        this(id, title, purpose, type, protocolVersion, instructions, conditions, allowedSupport,
                inconclusiveRule, estimatedMinutes, maximumAttempts, difficultyDemand, competencyIds,
                criterionKeys, List.of());
    }

    public Assessment(String id, String title, String purpose, Type type, String protocolVersion,
                      String instructions, String conditions, String allowedSupport, String inconclusiveRule,
                      int estimatedMinutes, int maximumAttempts, DifficultyDemand difficultyDemand,
                      List<String> competencyIds, List<String> criterionKeys,
                      List<AssessmentRubricLevel> rubricLevels) {
        this.id = DomainRules.requiredText(id, "id");
        this.title = DomainRules.requiredText(title, "title");
        this.purpose = DomainRules.requiredText(purpose, "purpose");
        this.type = DomainRules.required(type, "type");
        this.protocolVersion = DomainRules.requiredText(protocolVersion, "protocolVersion");
        this.instructions = DomainRules.requiredText(instructions, "instructions");
        this.conditions = DomainRules.requiredText(conditions, "conditions");
        this.allowedSupport = DomainRules.requiredText(allowedSupport, "allowedSupport");
        this.inconclusiveRule = DomainRules.requiredText(inconclusiveRule, "inconclusiveRule");
        this.estimatedMinutes = DomainRules.between(estimatedMinutes, 1, 480, "estimatedMinutes");
        this.maximumAttempts = DomainRules.between(maximumAttempts, 1, 100, "maximumAttempts");
        this.difficultyDemand = difficultyDemand == null ? DifficultyDemand.unspecified() : difficultyDemand;
        this.competencyIds = new ArrayList<>(DomainRules.distinctIds(competencyIds));
        this.criterionKeys = new ArrayList<>(DomainRules.distinctIds(criterionKeys));
        this.rubricLevels = new ArrayList<>(rubricLevels == null ? List.of() : rubricLevels);
        if (this.competencyIds.isEmpty() || this.criterionKeys.isEmpty()) {
            throw new IllegalArgumentException("assessment precisa de competências e critérios");
        }
        this.active = false;
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
    }

    public void activate() {
        this.active = true;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }

    public void synchronizeCatalogDefinition(String title, String purpose, Type type, String protocolVersion,
                                             String instructions, String conditions, String allowedSupport,
                                             String inconclusiveRule, int estimatedMinutes, int maximumAttempts,
                                             DifficultyDemand difficultyDemand, List<String> competencyIds,
                                             List<String> criterionKeys) {
        synchronizeCatalogDefinition(title, purpose, type, protocolVersion, instructions, conditions,
                allowedSupport, inconclusiveRule, estimatedMinutes, maximumAttempts, difficultyDemand,
                competencyIds, criterionKeys, List.of());
    }

    public void synchronizeCatalogDefinition(String title, String purpose, Type type, String protocolVersion,
                                             String instructions, String conditions, String allowedSupport,
                                             String inconclusiveRule, int estimatedMinutes, int maximumAttempts,
                                             DifficultyDemand difficultyDemand, List<String> competencyIds,
                                             List<String> criterionKeys, List<AssessmentRubricLevel> rubricLevels) {
        this.title = DomainRules.requiredText(title, "title");
        this.purpose = DomainRules.requiredText(purpose, "purpose");
        this.type = DomainRules.required(type, "type");
        this.protocolVersion = DomainRules.requiredText(protocolVersion, "protocolVersion");
        this.instructions = DomainRules.requiredText(instructions, "instructions");
        this.conditions = DomainRules.requiredText(conditions, "conditions");
        this.allowedSupport = DomainRules.requiredText(allowedSupport, "allowedSupport");
        this.inconclusiveRule = DomainRules.requiredText(inconclusiveRule, "inconclusiveRule");
        this.estimatedMinutes = DomainRules.between(estimatedMinutes, 1, 480, "estimatedMinutes");
        this.maximumAttempts = DomainRules.between(maximumAttempts, 1, 100, "maximumAttempts");
        this.difficultyDemand = difficultyDemand == null ? DifficultyDemand.unspecified() : difficultyDemand;
        this.competencyIds = new ArrayList<>(DomainRules.distinctIds(competencyIds));
        this.criterionKeys = new ArrayList<>(DomainRules.distinctIds(criterionKeys));
        this.rubricLevels = new ArrayList<>(rubricLevels == null ? List.of() : rubricLevels);
        if (this.competencyIds.isEmpty() || this.criterionKeys.isEmpty()) {
            throw new IllegalArgumentException("assessment precisa de competências e critérios");
        }
        this.updatedAt = Instant.now();
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getPurpose() { return purpose; }
    public Type getType() { return type; }
    public String getProtocolVersion() { return protocolVersion; }
    public String getInstructions() { return instructions; }
    public String getConditions() { return conditions; }
    public String getAllowedSupport() { return allowedSupport; }
    public String getInconclusiveRule() { return inconclusiveRule; }
    public int getEstimatedMinutes() { return estimatedMinutes; }
    public int getMaximumAttempts() { return maximumAttempts; }
    public boolean isActive() { return active; }
    public DifficultyDemand getDifficultyDemand() { return difficultyDemand; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public List<String> getCompetencyIds() { return List.copyOf(competencyIds); }
    public List<String> getCriterionKeys() { return List.copyOf(criterionKeys); }
    public List<AssessmentRubricLevel> getRubricLevels() { return List.copyOf(rubricLevels); }
}
