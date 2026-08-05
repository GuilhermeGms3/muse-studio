package com.musicos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.Objects;

@Embeddable
public class LearningPathStep {
    public enum Kind {
        CORE,
        SPECIALIZATION,
        REINFORCEMENT,
        DIAGNOSTIC_SHORTCUT,
        APPLICATION,
        OPTIONAL,
        REVIEW,
        REVALIDATION
    }

    public enum Readiness {
        UNASSESSED,
        BLOCKED,
        CANDIDATE,
        DEFERRED
    }

    private String competencyId;

    @Enumerated(EnumType.STRING)
    private Kind kind;

    @Enumerated(EnumType.STRING)
    private Readiness readiness;

    @Column(length = 1000)
    private String rationale;

    protected LearningPathStep() {
    }

    public LearningPathStep(String competencyId, Kind kind, Readiness readiness, String rationale) {
        this.competencyId = DomainRules.requiredText(competencyId, "competencyId");
        this.kind = DomainRules.required(kind, "kind");
        this.readiness = DomainRules.required(readiness, "readiness");
        this.rationale = DomainRules.requiredText(rationale, "rationale");
    }

    public String getCompetencyId() { return competencyId; }
    public Kind getKind() { return kind; }
    public Readiness getReadiness() { return readiness; }
    public String getRationale() { return rationale; }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof LearningPathStep that)) return false;
        return Objects.equals(competencyId, that.competencyId) && kind == that.kind
                && readiness == that.readiness && Objects.equals(rationale, that.rationale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(competencyId, kind, readiness, rationale);
    }
}
