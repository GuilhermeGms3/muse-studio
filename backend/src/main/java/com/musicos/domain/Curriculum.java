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
@Table(name = "learning_curricula")
public class Curriculum {
    @Id
    private String id;
    private String name;
    private String versionLabel;

    @Column(length = 3000)
    private String purpose;

    @Column(length = 2000)
    private String audience;

    @Enumerated(EnumType.STRING)
    private InstrumentId instrument;

    @Enumerated(EnumType.STRING)
    private LearningStage startingStage;

    @Enumerated(EnumType.STRING)
    private LearningStage targetStage;

    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;

    @ElementCollection
    @CollectionTable(name = "curriculum_outcomes", joinColumns = @JoinColumn(name = "curriculum_id"))
    @OrderColumn(name = "position")
    @Column(name = "outcome", length = 1000)
    private List<String> outcomes = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "curriculum_competencies", joinColumns = @JoinColumn(name = "curriculum_id"))
    @OrderColumn(name = "position")
    @Column(name = "competency_id")
    private List<String> competencyIds = new ArrayList<>();

    @Version
    private long version;

    protected Curriculum() {
    }

    public Curriculum(String id, String name, String versionLabel, String purpose, String audience,
                      InstrumentId instrument, LearningStage startingStage, LearningStage targetStage,
                      List<String> outcomes, List<String> competencyIds) {
        this.id = DomainRules.requiredText(id, "id");
        this.name = DomainRules.requiredText(name, "name");
        this.versionLabel = DomainRules.requiredText(versionLabel, "versionLabel");
        this.purpose = DomainRules.requiredText(purpose, "purpose");
        this.audience = DomainRules.requiredText(audience, "audience");
        this.instrument = instrument;
        this.startingStage = DomainRules.required(startingStage, "startingStage");
        this.targetStage = DomainRules.required(targetStage, "targetStage");
        if (targetStage.order() < startingStage.order()) {
            throw new IllegalArgumentException("targetStage não pode preceder startingStage");
        }
        this.outcomes = new ArrayList<>(DomainRules.distinctIds(outcomes));
        this.competencyIds = new ArrayList<>(DomainRules.distinctIds(competencyIds));
        this.active = false;
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
    }

    public void activate() {
        if (competencyIds.isEmpty()) throw new IllegalStateException("currículo sem competências não pode ser ativado");
        this.active = true;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }

    public void replaceCompetencies(List<String> ids) {
        this.competencyIds = new ArrayList<>(DomainRules.distinctIds(ids));
        this.updatedAt = Instant.now();
    }

    public void synchronizeLegacyCurriculum(String name, String versionLabel, String purpose, String audience,
                                            List<String> outcomes, List<String> competencyIds) {
        this.name = DomainRules.requiredText(name, "name");
        this.versionLabel = DomainRules.requiredText(versionLabel, "versionLabel");
        this.purpose = DomainRules.requiredText(purpose, "purpose");
        this.audience = DomainRules.requiredText(audience, "audience");
        this.outcomes = new ArrayList<>(DomainRules.distinctIds(outcomes));
        this.competencyIds = new ArrayList<>(DomainRules.distinctIds(competencyIds));
        this.updatedAt = Instant.now();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getVersionLabel() { return versionLabel; }
    public String getPurpose() { return purpose; }
    public String getAudience() { return audience; }
    public InstrumentId getInstrument() { return instrument; }
    public LearningStage getStartingStage() { return startingStage; }
    public LearningStage getTargetStage() { return targetStage; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public List<String> getOutcomes() { return List.copyOf(outcomes); }
    public List<String> getCompetencyIds() { return List.copyOf(competencyIds); }
}
