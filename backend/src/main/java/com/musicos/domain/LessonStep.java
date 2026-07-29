package com.musicos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class LessonStep {
    private String title;

    @Column(length = 3000)
    private String explanation;

    @Column(length = 1000)
    private String musicalExample;

    @Column(length = 1000)
    private String notation;

    @Column(length = 2000)
    private String tablature;

    private String audioNotes;

    protected LessonStep() {
    }

    public LessonStep(String title, String explanation, String musicalExample, String notation,
                      String tablature, String audioNotes) {
        this.title = title;
        this.explanation = explanation;
        this.musicalExample = musicalExample;
        this.notation = notation;
        this.tablature = tablature;
        this.audioNotes = audioNotes;
    }

    public String getTitle() { return title; }
    public String getExplanation() { return explanation; }
    public String getMusicalExample() { return musicalExample; }
    public String getNotation() { return notation; }
    public String getTablature() { return tablature; }
    public String getAudioNotes() { return audioNotes; }
}
