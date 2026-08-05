package com.musicos.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "learning_instrument_profiles", uniqueConstraints =
        @UniqueConstraint(name = "uk_instrument_profile_owner_instrument", columnNames = {"owner_id", "instrument"}))
public class InstrumentProfile {
    @Id
    private String id;
    @jakarta.persistence.Column(name = "owner_id")
    private String ownerId;

    @Enumerated(EnumType.STRING)
    private InstrumentId instrument;

    private String displayName;

    @Enumerated(EnumType.STRING)
    private LearningStage currentStage;

    private String curriculumId;
    private String legacyPreferencesId;
    private boolean primaryProfile;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;

    @Version
    private long version;

    protected InstrumentProfile() {
    }

    public InstrumentProfile(String ownerId, InstrumentId instrument, String displayName,
                             LearningStage currentStage, String curriculumId, String legacyPreferencesId) {
        this(UUID.randomUUID().toString(), ownerId, instrument, displayName, currentStage, curriculumId,
                legacyPreferencesId);
    }

    public InstrumentProfile(String id, String ownerId, InstrumentId instrument, String displayName,
                             LearningStage currentStage, String curriculumId, String legacyPreferencesId) {
        this.id = DomainRules.requiredText(id, "id");
        this.ownerId = DomainRules.requiredText(ownerId, "ownerId");
        this.instrument = DomainRules.required(instrument, "instrument");
        this.displayName = DomainRules.requiredText(displayName, "displayName");
        this.currentStage = DomainRules.required(currentStage, "currentStage");
        this.curriculumId = curriculumId;
        this.legacyPreferencesId = legacyPreferencesId;
        this.primaryProfile = false;
        this.active = true;
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
    }

    public void changeCurriculum(String curriculumId) {
        this.curriculumId = DomainRules.requiredText(curriculumId, "curriculumId");
        this.updatedAt = Instant.now();
    }

    public void updateStage(LearningStage currentStage) {
        this.currentStage = DomainRules.required(currentStage, "currentStage");
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }

    public void markPrimary(boolean primaryProfile) {
        this.primaryProfile = primaryProfile;
        this.updatedAt = Instant.now();
    }

    public static InstrumentProfile restoreSnapshot(
            String id, String ownerId, InstrumentId instrument, String displayName,
            LearningStage currentStage, String curriculumId, String legacyPreferencesId,
            boolean primaryProfile, boolean active, Instant createdAt, Instant updatedAt) {
        var profile = new InstrumentProfile(id, ownerId, instrument, displayName, currentStage,
                curriculumId, legacyPreferencesId);
        profile.primaryProfile = primaryProfile;
        profile.active = active;
        profile.createdAt = createdAt == null ? Instant.now() : createdAt;
        profile.updatedAt = updatedAt == null ? profile.createdAt : updatedAt;
        return profile;
    }

    public void restoreState(String ownerId, InstrumentId instrument, String displayName,
                             LearningStage currentStage, String curriculumId,
                             String legacyPreferencesId, boolean primaryProfile, boolean active,
                             Instant createdAt, Instant updatedAt) {
        if (!this.ownerId.equals(ownerId) || this.instrument != instrument) {
            throw new IllegalArgumentException("backup de perfil diverge da identidade persistida");
        }
        this.displayName = DomainRules.requiredText(displayName, "displayName");
        this.currentStage = DomainRules.required(currentStage, "currentStage");
        this.curriculumId = curriculumId;
        this.legacyPreferencesId = legacyPreferencesId;
        this.primaryProfile = primaryProfile;
        this.active = active;
        this.createdAt = createdAt == null ? this.createdAt : createdAt;
        this.updatedAt = updatedAt == null ? Instant.now() : updatedAt;
    }

    public String getId() { return id; }
    public String getOwnerId() { return ownerId; }
    public InstrumentId getInstrument() { return instrument; }
    public String getDisplayName() { return displayName; }
    public LearningStage getCurrentStage() { return currentStage; }
    public String getCurriculumId() { return curriculumId; }
    public String getLegacyPreferencesId() { return legacyPreferencesId; }
    public boolean isPrimaryProfile() { return primaryProfile; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
