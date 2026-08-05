package com.musicos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "learning_content_relations", uniqueConstraints = @UniqueConstraint(
        name = "uk_learning_content_relation",
        columnNames = {"source_type", "source_id", "relation_type", "target_type", "target_id"}))
public class LearningContentRelation {
    public enum ContentType { COMPETENCY, MISSION, LESSON, EXERCISE, ASSESSMENT, SONG, SONG_SECTION, LEARNING_GOAL }
    public enum RelationType {
        REQUIRES,
        PREPARES,
        CO_DEVELOPS,
        APPLIES,
        TRANSFERS_TO,
        CONTRASTS_WITH,
        COMPOSES,
        ALTERNATIVE_PATH,
        EVIDENCES,
        SERVES_GOAL
    }
    public enum Strength { REQUIRED, STRONG, MODERATE, WEAK }

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type")
    private ContentType sourceType;

    @Column(name = "source_id")
    private String sourceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "relation_type")
    private RelationType relationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type")
    private ContentType targetType;

    @Column(name = "target_id")
    private String targetId;

    @Enumerated(EnumType.STRING)
    private Strength strength;

    @Column(length = 2000)
    private String justification;

    @Column(length = 2000)
    private String conditions;

    private String migratedFrom;
    private Instant createdAt;

    protected LearningContentRelation() {
    }

    public LearningContentRelation(ContentType sourceType, String sourceId, RelationType relationType,
                                   ContentType targetType, String targetId, Strength strength,
                                   String justification, String conditions, String migratedFrom) {
        this.sourceType = DomainRules.required(sourceType, "sourceType");
        this.sourceId = DomainRules.requiredText(sourceId, "sourceId");
        this.relationType = DomainRules.required(relationType, "relationType");
        this.targetType = DomainRules.required(targetType, "targetType");
        this.targetId = DomainRules.requiredText(targetId, "targetId");
        if (sourceType == targetType && this.sourceId.equals(this.targetId)) {
            throw new IllegalArgumentException("relação de conteúdo não pode apontar para si mesma");
        }
        this.strength = DomainRules.required(strength, "strength");
        this.justification = DomainRules.requiredText(justification, "justification");
        this.conditions = conditions;
        this.migratedFrom = migratedFrom;
        this.id = deterministicId(sourceType, this.sourceId, relationType, targetType, this.targetId);
        this.createdAt = Instant.now();
    }

    private String deterministicId(ContentType sourceType, String sourceId, RelationType relationType,
                                   ContentType targetType, String targetId) {
        var key = sourceType + ":" + sourceId + ":" + relationType + ":" + targetType + ":" + targetId;
        return UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8)).toString();
    }

    public String getId() { return id; }
    public ContentType getSourceType() { return sourceType; }
    public String getSourceId() { return sourceId; }
    public RelationType getRelationType() { return relationType; }
    public ContentType getTargetType() { return targetType; }
    public String getTargetId() { return targetId; }
    public Strength getStrength() { return strength; }
    public String getJustification() { return justification; }
    public String getConditions() { return conditions; }
    public String getMigratedFrom() { return migratedFrom; }
    public Instant getCreatedAt() { return createdAt; }
}
