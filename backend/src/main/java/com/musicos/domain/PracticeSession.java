package com.musicos.domain;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "practice_sessions")
public class PracticeSession {
    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    private InstrumentId instrument;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    private Instant startedAt;
    private Instant finishedAt;
    private long elapsedSeconds;
    private int currentActivityIndex;
    private String notes;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> activityIds = new ArrayList<>();

    @Version
    private long version;

    protected PracticeSession() {
    }

    public PracticeSession(InstrumentId instrument, List<String> activityIds) {
        this.instrument = instrument;
        this.activityIds = new ArrayList<>(activityIds);
        this.status = SessionStatus.ACTIVE;
        this.startedAt = Instant.now();
        this.notes = "";
    }

    public void update(long elapsedSeconds, int currentActivityIndex, String notes, SessionStatus status) {
        this.elapsedSeconds = Math.max(this.elapsedSeconds, elapsedSeconds);
        this.currentActivityIndex = Math.max(0, Math.min(currentActivityIndex, Math.max(0, activityIds.size() - 1)));
        if (notes != null) this.notes = notes;
        if (status != null && status != SessionStatus.FINISHED) this.status = status;
    }

    public void finish(long elapsedSeconds, String notes) {
        this.elapsedSeconds = Math.max(this.elapsedSeconds, elapsedSeconds);
        if (notes != null) this.notes = notes;
        this.status = SessionStatus.FINISHED;
        this.finishedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public InstrumentId getInstrument() { return instrument; }
    public SessionStatus getStatus() { return status; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getFinishedAt() { return finishedAt; }
    public long getElapsedSeconds() { return elapsedSeconds; }
    public int getCurrentActivityIndex() { return currentActivityIndex; }
    public String getNotes() { return notes; }
    public List<String> getActivityIds() { return List.copyOf(activityIds); }
}
