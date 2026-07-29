package com.musicos.domain;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "music_projects")
public class MusicProject {
    @Id
    private String id;
    private String name;
    private String musicalKey;
    private int bpm;
    private String status;
    private String lyrics;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> ideas = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> references = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<ProjectRiff> riffs = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<ProjectVersion> versions = new ArrayList<>();

    protected MusicProject() {
    }

    public MusicProject(String id, String name, String musicalKey, int bpm, String status, String lyrics,
                        List<String> ideas, List<String> references) {
        this(id, name, musicalKey, bpm, status, lyrics, ideas, references, List.of(), List.of());
    }

    public MusicProject(String id, String name, String musicalKey, int bpm, String status, String lyrics,
                        List<String> ideas, List<String> references, List<ProjectRiff> riffs,
                        List<ProjectVersion> versions) {
        this.id = id;
        this.name = name;
        this.musicalKey = musicalKey;
        this.bpm = bpm;
        this.status = status;
        this.lyrics = lyrics;
        this.ideas = new ArrayList<>(ideas);
        this.references = new ArrayList<>(references);
        this.riffs = new ArrayList<>(riffs);
        this.versions = new ArrayList<>(versions);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getMusicalKey() { return musicalKey; }
    public int getBpm() { return bpm; }
    public String getStatus() { return status; }
    public String getLyrics() { return lyrics; }
    public List<String> getIdeas() { return List.copyOf(ideas); }
    public List<String> getReferences() { return List.copyOf(references); }
    public List<ProjectRiff> getRiffs() { return List.copyOf(riffs); }
    public List<ProjectVersion> getVersions() { return List.copyOf(versions); }
    public void setRiffs(List<ProjectRiff> riffs) { this.riffs = new ArrayList<>(riffs); }
    public void setVersions(List<ProjectVersion> versions) { this.versions = new ArrayList<>(versions); }
    public void update(String name, String musicalKey, int bpm, String status, String lyrics,
                       List<String> ideas, List<String> references, List<ProjectRiff> riffs,
                       List<ProjectVersion> versions) {
        this.name = name;
        this.musicalKey = musicalKey;
        this.bpm = bpm;
        this.status = status;
        this.lyrics = lyrics;
        this.ideas = new ArrayList<>(ideas);
        this.references = new ArrayList<>(references);
        this.riffs = new ArrayList<>(riffs);
        this.versions = new ArrayList<>(versions);
    }
}
