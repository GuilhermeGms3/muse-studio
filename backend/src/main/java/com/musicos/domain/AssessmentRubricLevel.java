package com.musicos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class AssessmentRubricLevel {
    private String criterionKey;
    private String band;

    @Column(length = 2000)
    private String description;

    protected AssessmentRubricLevel() {
    }

    public AssessmentRubricLevel(String criterionKey, String band, String description) {
        this.criterionKey = DomainRules.requiredText(criterionKey, "criterionKey");
        this.band = DomainRules.requiredText(band, "band");
        this.description = DomainRules.requiredText(description, "description");
    }

    public String getCriterionKey() { return criterionKey; }
    public String getBand() { return band; }
    public String getDescription() { return description; }
}
