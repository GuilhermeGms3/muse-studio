package com.musicos.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "session_activity_results")
public class SessionActivityResult {
    @Id
    @GeneratedValue
    private UUID id;
    private UUID sessionId;
    private String activityId;
    private String skillId;
    private String title;
    private String feedback;
    private int bpm;
    private int accuracy;
    private long durationSeconds;
    private int timingOffsetMillis;
    private int suggestedBpm;
    private Instant completedAt;

    protected SessionActivityResult() {
    }

    public SessionActivityResult(UUID sessionId, String activityId, String skillId, String title,
                                 String feedback, int bpm, int accuracy, long durationSeconds,
                                 int timingOffsetMillis, int suggestedBpm) {
        this.sessionId = sessionId;
        this.activityId = activityId;
        this.skillId = skillId;
        this.title = title;
        this.feedback = feedback;
        this.bpm = bpm;
        this.accuracy = accuracy;
        this.durationSeconds = durationSeconds;
        this.timingOffsetMillis = timingOffsetMillis;
        this.suggestedBpm = suggestedBpm;
        this.completedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getSessionId() { return sessionId; }
    public String getActivityId() { return activityId; }
    public String getSkillId() { return skillId; }
    public String getTitle() { return title; }
    public String getFeedback() { return feedback; }
    public int getBpm() { return bpm; }
    public int getAccuracy() { return accuracy; }
    public long getDurationSeconds() { return durationSeconds; }
    public int getTimingOffsetMillis() { return timingOffsetMillis; }
    public int getSuggestedBpm() { return suggestedBpm; }
    public Instant getCompletedAt() { return completedAt; }
}
