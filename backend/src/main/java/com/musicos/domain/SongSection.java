package com.musicos.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class SongSection {
    private String sectionId;
    private String name;
    private int progress;
    private Integer bpm;
    private String note;
    private String skillIds;
    private String tablature;
    private Integer startSeconds;
    private Integer endSeconds;
    private String tonePreset;

    protected SongSection() {
    }

    public SongSection(String sectionId, String name, int progress, Integer bpm, String note) {
        this(sectionId, name, progress, bpm, note, java.util.List.of(), null, null, null);
    }

    public SongSection(String sectionId, String name, int progress, Integer bpm, String note,
                       java.util.List<String> skillIds, String tablature,
                       Integer startSeconds, Integer endSeconds) {
        this(sectionId, name, progress, bpm, note, skillIds, tablature, startSeconds, endSeconds, null);
    }

    public SongSection(String sectionId, String name, int progress, Integer bpm, String note,
                       java.util.List<String> skillIds, String tablature,
                       Integer startSeconds, Integer endSeconds, String tonePreset) {
        this.sectionId = sectionId;
        this.name = name;
        this.progress = progress;
        this.bpm = bpm;
        this.note = note;
        this.skillIds = String.join(",", skillIds == null ? java.util.List.of() : skillIds);
        this.tablature = tablature;
        this.startSeconds = startSeconds;
        this.endSeconds = endSeconds;
        this.tonePreset = tonePreset;
    }

    public String getSectionId() { return sectionId; }
    public String getName() { return name; }
    public int getProgress() { return progress; }
    public Integer getBpm() { return bpm; }
    public String getNote() { return note; }
    public java.util.List<String> getSkillIds() {
        return skillIds == null || skillIds.isBlank() ? java.util.List.of()
                : java.util.Arrays.stream(skillIds.split(",")).map(String::trim).filter(v -> !v.isBlank()).toList();
    }
    public String getTablature() { return tablature; }
    public Integer getStartSeconds() { return startSeconds; }
    public Integer getEndSeconds() { return endSeconds; }
    public String getTonePreset() { return tonePreset; }
}
