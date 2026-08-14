package com.musicos.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "mission_experiences", uniqueConstraints = @UniqueConstraint(
        name = "uk_mission_experience_profile", columnNames = {"mission_id", "instrument_profile_id"}))
public class MissionExperience {
    public enum Status { IN_PROGRESS, PAUSED, COMPLETED }
    public enum ActivityKind { ORIENTATION, LESSON, EXERCISE, APPLICATION, REFLECTION }

    @Id @GeneratedValue private UUID id;
    private String missionId;
    private String instrumentProfileId;
    @Enumerated(EnumType.STRING) private Status status;
    @Enumerated(EnumType.STRING) private ActivityKind currentActivityKind;
    private String currentActivityId;
    private UUID lastRecordingId;
    private UUID assessmentAttemptId;
    private Instant startedAt;
    private Instant updatedAt;
    private Instant pausedAt;
    private Instant completedAt;
    @Version private long version;

    protected MissionExperience() {}

    public MissionExperience(String missionId, String instrumentProfileId) {
        this.missionId = DomainRules.requiredText(missionId, "missionId");
        this.instrumentProfileId = DomainRules.requiredText(instrumentProfileId, "instrumentProfileId");
        this.status = Status.IN_PROGRESS;
        this.currentActivityKind = ActivityKind.ORIENTATION;
        this.currentActivityId = missionId;
        this.startedAt = Instant.now();
        this.updatedAt = startedAt;
    }

    public void resume() {
        if (status == Status.COMPLETED) return;
        status = Status.IN_PROGRESS;
        pausedAt = null;
        updatedAt = Instant.now();
    }

    public void moveTo(ActivityKind kind, String activityId, UUID recordingId) {
        if (status == Status.COMPLETED) throw new IllegalStateException("Experiência já concluída");
        currentActivityKind = DomainRules.required(kind, "activityKind");
        currentActivityId = DomainRules.requiredText(activityId, "activityId");
        if (recordingId != null) lastRecordingId = recordingId;
        status = Status.IN_PROGRESS;
        pausedAt = null;
        updatedAt = Instant.now();
    }

    public void pause() {
        if (status == Status.COMPLETED) return;
        status = Status.PAUSED;
        pausedAt = Instant.now();
        updatedAt = pausedAt;
    }

    public void complete(UUID attemptId, UUID recordingId) {
        assessmentAttemptId = DomainRules.required(attemptId, "assessmentAttemptId");
        if (recordingId != null) lastRecordingId = recordingId;
        status = Status.COMPLETED;
        currentActivityKind = ActivityKind.REFLECTION;
        completedAt = Instant.now();
        pausedAt = null;
        updatedAt = completedAt;
    }

    public UUID getId() { return id; }
    public String getMissionId() { return missionId; }
    public String getInstrumentProfileId() { return instrumentProfileId; }
    public Status getStatus() { return status; }
    public ActivityKind getCurrentActivityKind() { return currentActivityKind; }
    public String getCurrentActivityId() { return currentActivityId; }
    public UUID getLastRecordingId() { return lastRecordingId; }
    public UUID getAssessmentAttemptId() { return assessmentAttemptId; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getPausedAt() { return pausedAt; }
    public Instant getCompletedAt() { return completedAt; }
}
