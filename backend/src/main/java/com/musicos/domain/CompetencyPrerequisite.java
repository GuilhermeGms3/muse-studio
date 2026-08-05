package com.musicos.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.Objects;

@Embeddable
public class CompetencyPrerequisite {
    public enum Type {
        STRICT,
        PEDAGOGICAL,
        RECOMMENDED,
        CONTEXTUAL,
        COREQUISITE
    }

    private String competencyId;

    @Enumerated(EnumType.STRING)
    private Type type;

    protected CompetencyPrerequisite() {
    }

    public CompetencyPrerequisite(String competencyId, Type type) {
        this.competencyId = DomainRules.requiredText(competencyId, "competencyId");
        this.type = DomainRules.required(type, "type");
    }

    public String getCompetencyId() { return competencyId; }
    public Type getType() { return type; }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof CompetencyPrerequisite that)) return false;
        return Objects.equals(competencyId, that.competencyId) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(competencyId, type);
    }
}
