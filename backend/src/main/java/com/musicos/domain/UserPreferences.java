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
@Table(name = "user_preferences")
public class UserPreferences {
    @Id
    private String id = LocalProfile.DEFAULT_ID;
    private String level;
    private Integer sessionMinutes;
    @Enumerated(EnumType.STRING)
    private InstrumentId primaryInstrument = InstrumentId.GUITAR;
    private Boolean onboardingCompleted = false;
    private Integer rhythmBaseline = 0;
    private Integer earBaseline = 0;
    private Integer techniqueBaseline = 0;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> favoriteGenres = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> favoriteArtists = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> favoriteSongs = new ArrayList<>();

    protected UserPreferences() {
    }

    public UserPreferences(String level, int sessionMinutes, List<String> genres, List<String> artists) {
        this(level, sessionMinutes, genres, artists, List.of(), InstrumentId.GUITAR);
    }

    public UserPreferences(String level, int sessionMinutes, List<String> genres, List<String> artists,
                           List<String> songs, InstrumentId primaryInstrument) {
        this.level = level;
        this.sessionMinutes = sessionMinutes;
        this.favoriteGenres = new ArrayList<>(genres);
        this.favoriteArtists = new ArrayList<>(artists);
        this.favoriteSongs = new ArrayList<>(songs);
        this.primaryInstrument = primaryInstrument;
    }

    public void update(String level, int sessionMinutes, List<String> genres, List<String> artists) {
        update(level, sessionMinutes, genres, artists, favoriteSongs, getPrimaryInstrument());
    }

    public void update(String level, int sessionMinutes, List<String> genres, List<String> artists,
                       List<String> songs, InstrumentId instrument) {
        this.level = level;
        this.sessionMinutes = Math.max(15, Math.min(180, sessionMinutes));
        this.favoriteGenres = new ArrayList<>(genres);
        this.favoriteArtists = new ArrayList<>(artists);
        this.favoriteSongs = new ArrayList<>(songs);
        this.primaryInstrument = instrument == null ? InstrumentId.GUITAR : instrument;
    }

    public void completeOnboarding(int rhythm, int ear, int technique) {
        this.rhythmBaseline = clampScore(rhythm);
        this.earBaseline = clampScore(ear);
        this.techniqueBaseline = clampScore(technique);
        this.onboardingCompleted = true;
    }

    public String getId() { return id; }
    public String getLevel() { return level == null ? "intermediate" : level; }
    public int getSessionMinutes() { return sessionMinutes == null ? 60 : sessionMinutes; }
    public List<String> getFavoriteGenres() { return List.copyOf(favoriteGenres); }
    public List<String> getFavoriteArtists() { return List.copyOf(favoriteArtists); }
    public List<String> getFavoriteSongs() { return List.copyOf(favoriteSongs); }
    public InstrumentId getPrimaryInstrument() {
        return primaryInstrument == null ? InstrumentId.GUITAR : primaryInstrument;
    }
    public boolean isOnboardingCompleted() { return Boolean.TRUE.equals(onboardingCompleted); }
    public int getRhythmBaseline() { return rhythmBaseline == null ? 0 : rhythmBaseline; }
    public int getEarBaseline() { return earBaseline == null ? 0 : earBaseline; }
    public int getTechniqueBaseline() { return techniqueBaseline == null ? 0 : techniqueBaseline; }

    private int clampScore(int value) {
        return Math.max(0, Math.min(100, value));
    }
}
