package com.musicos.domain;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "songs")
public class Song {
    @Id
    private String id;
    private String title;
    private String artist;
    private String tuning;
    private String musicalKey;
    private int bpm;

    @Enumerated(EnumType.STRING)
    private InstrumentId instrument;

    private int difficulty;
    private String status;
    private String notes;
    private int progress;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> techniques = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> scales = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<SongSection> sections = new ArrayList<>();

    protected Song() {
    }

    public Song(String id, String title, String artist, String tuning, String musicalKey, int bpm,
                InstrumentId instrument, int difficulty, String status, String notes, int progress,
                List<String> techniques, List<String> scales) {
        this(id, title, artist, tuning, musicalKey, bpm, instrument, difficulty, status, notes, progress,
                techniques, scales, List.of());
    }

    public Song(String id, String title, String artist, String tuning, String musicalKey, int bpm,
                InstrumentId instrument, int difficulty, String status, String notes, int progress,
                List<String> techniques, List<String> scales, List<SongSection> sections) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.tuning = tuning;
        this.musicalKey = musicalKey;
        this.bpm = bpm;
        this.instrument = instrument;
        this.difficulty = difficulty;
        this.status = status;
        this.notes = notes;
        this.progress = progress;
        this.techniques = new ArrayList<>(techniques);
        this.scales = new ArrayList<>(scales);
        this.sections = new ArrayList<>(sections);
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getTuning() { return tuning; }
    public String getMusicalKey() { return musicalKey; }
    public int getBpm() { return bpm; }
    public InstrumentId getInstrument() { return instrument; }
    public int getDifficulty() { return difficulty; }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }
    public int getProgress() { return progress; }
    public List<String> getTechniques() { return List.copyOf(techniques); }
    public List<String> getScales() { return List.copyOf(scales); }
    public List<SongSection> getSections() { return List.copyOf(sections); }
    public void setSections(List<SongSection> sections) { this.sections = new ArrayList<>(sections); }
    public void update(String title, String artist, String tuning, String musicalKey, int bpm,
                       InstrumentId instrument, int difficulty, String status, String notes, int progress,
                       List<String> techniques, List<String> scales, List<SongSection> sections) {
        this.title = title;
        this.artist = artist;
        this.tuning = tuning;
        this.musicalKey = musicalKey;
        this.bpm = bpm;
        this.instrument = instrument;
        this.difficulty = difficulty;
        this.status = status;
        this.notes = notes;
        this.progress = Math.max(0, Math.min(100, progress));
        this.techniques = new ArrayList<>(techniques);
        this.scales = new ArrayList<>(scales);
        this.sections = new ArrayList<>(sections);
    }
}
