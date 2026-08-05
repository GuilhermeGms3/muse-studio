package com.musicos.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
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
@Table(name = "learning_competencies")
public class Competency {
    @Id
    private String id;
    private String friendlyTitle;
    private String technicalName;
    private String domain;
    private String observableAction;

    @Column(length = 3000)
    private String description;

    @Column(length = 2000)
    private String observationConditions;

    @Enumerated(EnumType.STRING)
    private SkillKind kind;

    @Enumerated(EnumType.STRING)
    private LearningTrack track;

    @Enumerated(EnumType.STRING)
    private LearningStage stage;

    private String evidencePolicyKey;
    private Integer retentionWindowDays;
    private String legacySkillId;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;

    @ElementCollection
    @CollectionTable(name = "competency_instruments", joinColumns = @JoinColumn(name = "competency_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "instrument")
    private List<InstrumentId> instruments = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "competency_prerequisites", joinColumns = @JoinColumn(name = "competency_id"))
    @AttributeOverrides({
            @AttributeOverride(name = "competencyId", column = @Column(name = "prerequisite_competency_id")),
            @AttributeOverride(name = "type", column = @Column(name = "prerequisite_type"))
    })
    private List<CompetencyPrerequisite> prerequisites = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "competency_criteria", joinColumns = @JoinColumn(name = "competency_id"))
    @OrderColumn(name = "position")
    @Column(name = "criterion_key")
    private List<String> mandatoryCriterionKeys = new ArrayList<>();

    @Version
    private long version;

    protected Competency() {
    }

    public Competency(String id, String friendlyTitle, String technicalName, String domain,
                      String observableAction, String description, String observationConditions,
                      SkillKind kind, LearningTrack track, LearningStage stage,
                      List<InstrumentId> instruments, List<CompetencyPrerequisite> prerequisites,
                      List<String> mandatoryCriterionKeys, String evidencePolicyKey,
                      Integer retentionWindowDays, String legacySkillId) {
        this.id = DomainRules.requiredText(id, "id");
        this.friendlyTitle = DomainRules.requiredText(friendlyTitle, "friendlyTitle");
        this.technicalName = DomainRules.requiredText(technicalName, "technicalName");
        this.domain = DomainRules.requiredText(domain, "domain");
        this.observableAction = DomainRules.requiredText(observableAction, "observableAction");
        this.description = DomainRules.requiredText(description, "description");
        this.observationConditions = observationConditions;
        this.kind = DomainRules.required(kind, "kind");
        this.track = DomainRules.required(track, "track");
        this.stage = DomainRules.required(stage, "stage");
        this.instruments = instruments == null ? new ArrayList<>() : new ArrayList<>(instruments.stream().distinct().toList());
        this.prerequisites = prerequisites == null ? new ArrayList<>() : new ArrayList<>(prerequisites.stream().distinct().toList());
        this.mandatoryCriterionKeys = new ArrayList<>(DomainRules.distinctIds(mandatoryCriterionKeys));
        this.evidencePolicyKey = evidencePolicyKey;
        if (retentionWindowDays != null && retentionWindowDays < 1) {
            throw new IllegalArgumentException("retentionWindowDays deve ser positivo");
        }
        this.retentionWindowDays = retentionWindowDays;
        this.legacySkillId = legacySkillId;
        this.active = true;
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
    }

    public boolean hasCompleteEvidencePolicy() {
        return evidencePolicyKey != null && !evidencePolicyKey.isBlank() && !mandatoryCriterionKeys.isEmpty();
    }

    public void configureEvidencePolicy(String evidencePolicyKey, List<String> mandatoryCriterionKeys,
                                        Integer retentionWindowDays) {
        this.evidencePolicyKey = DomainRules.requiredText(evidencePolicyKey, "evidencePolicyKey");
        this.mandatoryCriterionKeys = new ArrayList<>(DomainRules.distinctIds(mandatoryCriterionKeys));
        if (this.mandatoryCriterionKeys.isEmpty()) {
            throw new IllegalArgumentException("política de evidência precisa de critérios obrigatórios");
        }
        if (retentionWindowDays != null && retentionWindowDays < 1) {
            throw new IllegalArgumentException("retentionWindowDays deve ser positivo");
        }
        this.retentionWindowDays = retentionWindowDays;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }

    public void synchronizeLegacyDefinition(String friendlyTitle, String technicalName, String domain,
                                            String observableAction, String description,
                                            SkillKind kind, LearningTrack track, LearningStage stage,
                                            List<InstrumentId> instruments,
                                            List<CompetencyPrerequisite> prerequisites) {
        if (legacySkillId == null) throw new IllegalStateException("competência não possui origem legada");
        this.friendlyTitle = DomainRules.requiredText(friendlyTitle, "friendlyTitle");
        this.technicalName = DomainRules.requiredText(technicalName, "technicalName");
        this.domain = DomainRules.requiredText(domain, "domain");
        this.observableAction = DomainRules.requiredText(observableAction, "observableAction");
        this.description = DomainRules.requiredText(description, "description");
        this.kind = DomainRules.required(kind, "kind");
        this.track = DomainRules.required(track, "track");
        this.stage = DomainRules.required(stage, "stage");
        this.instruments = instruments == null ? new ArrayList<>()
                : new ArrayList<>(instruments.stream().distinct().toList());
        this.prerequisites = prerequisites == null ? new ArrayList<>()
                : new ArrayList<>(prerequisites.stream().distinct().toList());
        this.updatedAt = Instant.now();
    }

    public String getId() { return id; }
    public String getFriendlyTitle() { return friendlyTitle; }
    public String getTechnicalName() { return technicalName; }
    public String getDomain() { return domain; }
    public String getObservableAction() { return observableAction; }
    public String getDescription() { return description; }
    public String getObservationConditions() { return observationConditions; }
    public SkillKind getKind() { return kind; }
    public LearningTrack getTrack() { return track; }
    public LearningStage getStage() { return stage; }
    public String getEvidencePolicyKey() { return evidencePolicyKey; }
    public Integer getRetentionWindowDays() { return retentionWindowDays; }
    public String getLegacySkillId() { return legacySkillId; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public List<InstrumentId> getInstruments() { return List.copyOf(instruments); }
    public List<CompetencyPrerequisite> getPrerequisites() { return List.copyOf(prerequisites); }
    public List<String> getMandatoryCriterionKeys() { return List.copyOf(mandatoryCriterionKeys); }
}
