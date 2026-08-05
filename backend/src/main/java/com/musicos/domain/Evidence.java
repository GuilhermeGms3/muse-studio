package com.musicos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "learning_evidence")
public class Evidence {
    public enum Type { EXECUTION, PERCEPTION_RESPONSE, APPLICATION, TRANSFER, RETENTION, PROCESS, DECLARATIVE, CONTEXTUAL }
    public enum State { PROVISIONAL, VALID, LIMITED, CONTESTED, CONTRADICTORY, INVALID, AGED, SUPERSEDED }
    public enum FunctionalWeight { REQUIRED, PRIMARY, CORROBORATING, CONTEXTUAL, NOT_ADMISSIBLE }
    public enum Reliability { LOW, MODERATE, HIGH }
    public enum Result { SUPPORTS, CHALLENGES, INCONCLUSIVE }
    public enum SourceType { MISSION, LESSON, EXERCISE, ASSESSMENT, REPERTOIRE, SESSION, MANUAL, IMPORT }

    @Id
    private String id;
    private String instrumentProfileId;
    private String competencyId;
    private String criterionKey;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private State state;

    @Enumerated(EnumType.STRING)
    private FunctionalWeight functionalWeight;

    @Enumerated(EnumType.STRING)
    private Reliability reliability;

    @Enumerated(EnumType.STRING)
    private Result result;

    @Enumerated(EnumType.STRING)
    private SourceType sourceType;

    private String sourceId;
    private String independenceKey;
    private int challengeLevel;

    @Column(length = 4000)
    private String observation;

    @Column(length = 3000)
    private String conditions;

    private String protocolVersion;
    private String analyzerVersion;
    private String artifactReference;
    private String supersedesEvidenceId;
    private Instant occurredAt;
    private Instant capturedAt;
    private Instant validUntil;

    protected Evidence() {
    }

    public Evidence(String instrumentProfileId, String competencyId, String criterionKey, Type type, State state,
                    FunctionalWeight functionalWeight, Reliability reliability, Result result,
                    SourceType sourceType, String sourceId, String independenceKey, int challengeLevel,
                    String observation, String conditions, String protocolVersion, String analyzerVersion,
                    String artifactReference, String supersedesEvidenceId, Instant occurredAt, Instant validUntil) {
        this.id = UUID.randomUUID().toString();
        this.instrumentProfileId = DomainRules.requiredText(instrumentProfileId, "instrumentProfileId");
        this.competencyId = DomainRules.requiredText(competencyId, "competencyId");
        this.criterionKey = DomainRules.requiredText(criterionKey, "criterionKey");
        this.type = DomainRules.required(type, "type");
        this.state = DomainRules.required(state, "state");
        this.functionalWeight = DomainRules.required(functionalWeight, "functionalWeight");
        this.reliability = DomainRules.required(reliability, "reliability");
        this.result = DomainRules.required(result, "result");
        this.sourceType = DomainRules.required(sourceType, "sourceType");
        this.sourceId = DomainRules.requiredText(sourceId, "sourceId");
        this.independenceKey = DomainRules.requiredText(independenceKey, "independenceKey");
        this.challengeLevel = DomainRules.between(challengeLevel, 1, 5, "challengeLevel");
        this.observation = DomainRules.requiredText(observation, "observation");
        this.conditions = DomainRules.requiredText(conditions, "conditions");
        this.protocolVersion = protocolVersion;
        this.analyzerVersion = analyzerVersion;
        this.artifactReference = artifactReference;
        this.supersedesEvidenceId = supersedesEvidenceId;
        this.occurredAt = DomainRules.required(occurredAt, "occurredAt");
        this.capturedAt = Instant.now();
        this.validUntil = validUntil;
    }

    public static Evidence restoreSnapshot(
            String id, String instrumentProfileId, String competencyId, String criterionKey,
            Type type, State state, FunctionalWeight functionalWeight, Reliability reliability,
            Result result, SourceType sourceType, String sourceId, String independenceKey,
            int challengeLevel, String observation, String conditions, String protocolVersion,
            String analyzerVersion, String artifactReference, String supersedesEvidenceId,
            Instant occurredAt, Instant capturedAt, Instant validUntil) {
        var restored = new Evidence(instrumentProfileId, competencyId, criterionKey, type, state,
                functionalWeight, reliability, result, sourceType, sourceId, independenceKey,
                challengeLevel, observation, conditions, protocolVersion, analyzerVersion,
                artifactReference, supersedesEvidenceId, occurredAt, validUntil);
        restored.id = DomainRules.requiredText(id, "id");
        restored.capturedAt = capturedAt == null ? Instant.now() : capturedAt;
        return restored;
    }

    public boolean isAgedAt(Instant instant) {
        return validUntil != null && instant.isAfter(validUntil);
    }

    public String getId() { return id; }
    public String getInstrumentProfileId() { return instrumentProfileId; }
    public String getCompetencyId() { return competencyId; }
    public String getCriterionKey() { return criterionKey; }
    public Type getType() { return type; }
    public State getState() { return state; }
    public FunctionalWeight getFunctionalWeight() { return functionalWeight; }
    public Reliability getReliability() { return reliability; }
    public Result getResult() { return result; }
    public SourceType getSourceType() { return sourceType; }
    public String getSourceId() { return sourceId; }
    public String getIndependenceKey() { return independenceKey; }
    public int getChallengeLevel() { return challengeLevel; }
    public String getObservation() { return observation; }
    public String getConditions() { return conditions; }
    public String getProtocolVersion() { return protocolVersion; }
    public String getAnalyzerVersion() { return analyzerVersion; }
    public String getArtifactReference() { return artifactReference; }
    public String getSupersedesEvidenceId() { return supersedesEvidenceId; }
    public Instant getOccurredAt() { return occurredAt; }
    public Instant getCapturedAt() { return capturedAt; }
    public Instant getValidUntil() { return validUntil; }
}
