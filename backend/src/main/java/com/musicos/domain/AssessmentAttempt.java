package com.musicos.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "assessment_attempts")
public class AssessmentAttempt {
    public enum ObserverType { SELF, EXTERNAL }

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "assessment_id")
    private String assessmentId;

    @Column(name = "instrument_profile_id")
    private String instrumentProfileId;

    @Enumerated(EnumType.STRING)
    private ObserverType observerType;

    private int challengeLevel;
    private String artifactReference;

    @Column(length = 2000)
    private String note;

    private Instant completedAt;

    @ElementCollection
    @CollectionTable(name = "assessment_attempt_evidence", joinColumns = @JoinColumn(name = "attempt_id"))
    @OrderColumn(name = "position")
    @Column(name = "evidence_id")
    private List<String> evidenceIds = new ArrayList<>();

    protected AssessmentAttempt() {
    }

    public AssessmentAttempt(String assessmentId, String instrumentProfileId, ObserverType observerType,
                             int challengeLevel, String artifactReference, String note) {
        this.assessmentId = DomainRules.requiredText(assessmentId, "assessmentId");
        this.instrumentProfileId = DomainRules.requiredText(instrumentProfileId, "instrumentProfileId");
        this.observerType = DomainRules.required(observerType, "observerType");
        this.challengeLevel = DomainRules.between(challengeLevel, 1, 5, "challengeLevel");
        this.artifactReference = artifactReference;
        this.note = note;
        this.completedAt = Instant.now();
    }

    public void attachEvidence(List<String> evidenceIds) {
        this.evidenceIds = new ArrayList<>(DomainRules.distinctIds(evidenceIds));
        if (this.evidenceIds.isEmpty()) {
            throw new IllegalArgumentException("tentativa de assessment precisa preservar evidências");
        }
    }

    public static AssessmentAttempt restoreSnapshot(
            UUID id, String assessmentId, String instrumentProfileId, ObserverType observerType,
            int challengeLevel, String artifactReference, String note, Instant completedAt,
            List<String> evidenceIds) {
        var attempt = new AssessmentAttempt(assessmentId, instrumentProfileId, observerType,
                challengeLevel, artifactReference, note);
        attempt.id = DomainRules.required(id, "id");
        attempt.completedAt = completedAt == null ? Instant.now() : completedAt;
        attempt.attachEvidence(evidenceIds);
        return attempt;
    }

    public UUID getId() { return id; }
    public String getAssessmentId() { return assessmentId; }
    public String getInstrumentProfileId() { return instrumentProfileId; }
    public ObserverType getObserverType() { return observerType; }
    public int getChallengeLevel() { return challengeLevel; }
    public String getArtifactReference() { return artifactReference; }
    public String getNote() { return note; }
    public Instant getCompletedAt() { return completedAt; }
    public List<String> getEvidenceIds() { return List.copyOf(evidenceIds); }
}
