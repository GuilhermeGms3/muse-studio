package com.musicos.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "learning_mastery", uniqueConstraints =
        @UniqueConstraint(name = "uk_mastery_profile_competency", columnNames = {"instrument_profile_id", "competency_id"}))
public class Mastery {
    public enum State {
        UNOBSERVED,
        INITIAL_HYPOTHESIS,
        DEVELOPING,
        CONSISTENT_CONTROLLED,
        PROBABLE_MASTERY_APPLICATION,
        RETAINED,
        REVALIDATION_NEEDED
    }

    @Id
    private String id;
    @Column(name = "instrument_profile_id")
    private String instrumentProfileId;
    @Column(name = "competency_id")
    private String competencyId;

    @Enumerated(EnumType.STRING)
    private State state;

    private String evidencePolicyVersion;
    private boolean mandatoryCriteriaCovered;
    private int independentEvidenceCount;
    private boolean applicationObserved;
    private boolean transferObserved;
    private boolean retentionObserved;
    private boolean unresolvedConflict;

    @Column(length = 3000)
    private String rationale;

    private Instant assessedAt;
    private Instant lastEvidenceAt;
    private Instant nextReviewAt;

    @ElementCollection
    @CollectionTable(name = "mastery_supporting_evidence", joinColumns = @JoinColumn(name = "mastery_id"))
    @Column(name = "evidence_id")
    private List<String> supportingEvidenceIds = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "mastery_limiting_evidence", joinColumns = @JoinColumn(name = "mastery_id"))
    @Column(name = "evidence_id")
    private List<String> limitingEvidenceIds = new ArrayList<>();

    @Version
    private long version;

    protected Mastery() {
    }

    public Mastery(String instrumentProfileId, String competencyId, String evidencePolicyVersion) {
        this.id = UUID.randomUUID().toString();
        this.instrumentProfileId = DomainRules.requiredText(instrumentProfileId, "instrumentProfileId");
        this.competencyId = DomainRules.requiredText(competencyId, "competencyId");
        this.evidencePolicyVersion = DomainRules.requiredText(evidencePolicyVersion, "evidencePolicyVersion");
        this.state = State.UNOBSERVED;
        this.rationale = "Nenhuma evidência admissível observada.";
        this.assessedAt = Instant.now();
    }

    public void reviseHypothesis(State state, boolean mandatoryCriteriaCovered, int independentEvidenceCount,
                                 boolean applicationObserved, boolean transferObserved, boolean retentionObserved,
                                 boolean unresolvedConflict, String rationale, Instant lastEvidenceAt,
                                 Instant nextReviewAt, List<String> supportingEvidenceIds,
                                 List<String> limitingEvidenceIds) {
        DomainRules.required(state, "state");
        DomainRules.between(independentEvidenceCount, 0, Integer.MAX_VALUE, "independentEvidenceCount");
        if (requiresProbableMastery(state)
                && (!mandatoryCriteriaCovered || independentEvidenceCount < 2 || !applicationObserved
                || unresolvedConflict)) {
            throw new IllegalStateException("domínio provável exige critérios cobertos, evidências independentes, aplicação e ausência de conflito");
        }
        if (state == State.RETAINED && !retentionObserved) {
            throw new IllegalStateException("estado retido exige evidência de retenção");
        }
        this.state = state;
        this.mandatoryCriteriaCovered = mandatoryCriteriaCovered;
        this.independentEvidenceCount = independentEvidenceCount;
        this.applicationObserved = applicationObserved;
        this.transferObserved = transferObserved;
        this.retentionObserved = retentionObserved;
        this.unresolvedConflict = unresolvedConflict;
        this.rationale = DomainRules.requiredText(rationale, "rationale");
        this.lastEvidenceAt = lastEvidenceAt;
        this.nextReviewAt = nextReviewAt;
        this.supportingEvidenceIds = new ArrayList<>(DomainRules.distinctIds(supportingEvidenceIds));
        this.limitingEvidenceIds = new ArrayList<>(DomainRules.distinctIds(limitingEvidenceIds));
        this.assessedAt = Instant.now();
    }

    public void applyEvidencePolicyVersion(String evidencePolicyVersion) {
        this.evidencePolicyVersion = DomainRules.requiredText(evidencePolicyVersion, "evidencePolicyVersion");
    }

    private boolean requiresProbableMastery(State target) {
        return target == State.PROBABLE_MASTERY_APPLICATION || target == State.RETAINED;
    }

    public String getId() { return id; }
    public String getInstrumentProfileId() { return instrumentProfileId; }
    public String getCompetencyId() { return competencyId; }
    public State getState() { return state; }
    public String getEvidencePolicyVersion() { return evidencePolicyVersion; }
    public boolean isMandatoryCriteriaCovered() { return mandatoryCriteriaCovered; }
    public int getIndependentEvidenceCount() { return independentEvidenceCount; }
    public boolean isApplicationObserved() { return applicationObserved; }
    public boolean isTransferObserved() { return transferObserved; }
    public boolean isRetentionObserved() { return retentionObserved; }
    public boolean isUnresolvedConflict() { return unresolvedConflict; }
    public String getRationale() { return rationale; }
    public Instant getAssessedAt() { return assessedAt; }
    public Instant getLastEvidenceAt() { return lastEvidenceAt; }
    public Instant getNextReviewAt() { return nextReviewAt; }
    public List<String> getSupportingEvidenceIds() { return List.copyOf(supportingEvidenceIds); }
    public List<String> getLimitingEvidenceIds() { return List.copyOf(limitingEvidenceIds); }
}
