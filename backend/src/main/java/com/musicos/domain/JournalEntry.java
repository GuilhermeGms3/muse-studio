package com.musicos.domain;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "journal_entries")
public class JournalEntry {
    @Id
    @GeneratedValue
    private UUID id;
    private Instant practicedAt;
    private long durationSeconds;

    @Enumerated(EnumType.STRING)
    private InstrumentId instrument;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> worked = new ArrayList<>();

    private String difficulties;
    private String improvements;
    private String notes;

    protected JournalEntry() {
    }

    public JournalEntry(Instant practicedAt, long durationSeconds, InstrumentId instrument, List<String> worked,
                        String difficulties, String improvements, String notes) {
        this.practicedAt = practicedAt;
        this.durationSeconds = durationSeconds;
        this.instrument = instrument;
        this.worked = new ArrayList<>(worked);
        this.difficulties = difficulties;
        this.improvements = improvements;
        this.notes = notes;
    }

    public UUID getId() { return id; }
    public Instant getPracticedAt() { return practicedAt; }
    public long getDurationSeconds() { return durationSeconds; }
    public InstrumentId getInstrument() { return instrument; }
    public List<String> getWorked() { return List.copyOf(worked); }
    public String getDifficulties() { return difficulties; }
    public String getImprovements() { return improvements; }
    public String getNotes() { return notes; }
}
