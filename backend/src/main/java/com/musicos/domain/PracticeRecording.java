package com.musicos.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "practice_recordings")
public class PracticeRecording {
    @Id
    @GeneratedValue
    private UUID id;
    private Instant createdAt;
    private String contextType;
    private String contextId;
    private String fileName;
    private String originalName;
    private String mimeType;
    private long durationMillis;
    private Integer targetBpm;
    private Integer measuredBpm;
    private Integer timingOffsetMillis;
    private Integer rhythmStability;
    private String targetNote;
    private Integer pitchOffsetCents;
    private Integer bendStability;

    protected PracticeRecording() {
    }

    public PracticeRecording(String contextType, String contextId, String fileName, String originalName,
                             String mimeType, long durationMillis, Integer targetBpm, Integer measuredBpm,
                             Integer timingOffsetMillis, Integer rhythmStability, String targetNote,
                             Integer pitchOffsetCents, Integer bendStability) {
        this.createdAt = Instant.now();
        this.contextType = contextType;
        this.contextId = contextId;
        this.fileName = fileName;
        this.originalName = originalName;
        this.mimeType = mimeType;
        this.durationMillis = durationMillis;
        this.targetBpm = targetBpm;
        this.measuredBpm = measuredBpm;
        this.timingOffsetMillis = timingOffsetMillis;
        this.rhythmStability = rhythmStability;
        this.targetNote = targetNote;
        this.pitchOffsetCents = pitchOffsetCents;
        this.bendStability = bendStability;
    }

    public UUID getId() { return id; }
    public Instant getCreatedAt() { return createdAt; }
    public String getContextType() { return contextType; }
    public String getContextId() { return contextId; }
    public String getFileName() { return fileName; }
    public String getOriginalName() { return originalName; }
    public String getMimeType() { return mimeType; }
    public long getDurationMillis() { return durationMillis; }
    public Integer getTargetBpm() { return targetBpm; }
    public Integer getMeasuredBpm() { return measuredBpm; }
    public Integer getTimingOffsetMillis() { return timingOffsetMillis; }
    public Integer getRhythmStability() { return rhythmStability; }
    public String getTargetNote() { return targetNote; }
    public Integer getPitchOffsetCents() { return pitchOffsetCents; }
    public Integer getBendStability() { return bendStability; }
}
