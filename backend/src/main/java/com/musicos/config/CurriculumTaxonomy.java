package com.musicos.config;

import com.musicos.domain.LearningStage;
import com.musicos.domain.LearningTrack;
import com.musicos.domain.Skill;
import com.musicos.domain.SkillKind;
import java.util.Set;

final class CurriculumTaxonomy {
    private static final Set<String> FIRST_STEPS = Set.of(
            "pulse", "notes", "ear-pitch", "rhythm", "guitar-posture", "keyboard-map",
            "drum-kit-map", "drum-setup", "drum-grip", "drum-bass-pedal", "drum-hi-hat-foot");
    private static final Set<String> BEGINNER = Set.of(
            "subdivisions", "intervals", "major-scale", "minor-pentatonic", "triads", "rhythm-reading",
            "note-reading", "open-chords", "chord-transitions", "keys-fingering", "alternate-picking",
            "sync", "bends", "drum-rebound", "drum-rock-groove", "drum-groove-consistency",
            "drum-single-stroke", "drum-one-beat-fill", "drum-tom-movement");
    private static final Set<String> BEGINNER_ADVANCED = Set.of(
            "meter", "syncopation", "groove", "major-pentatonic", "natural-minor", "triad-inversions",
            "ear-intervals", "ear-rhythm", "strumming", "barre-chords", "keys-independence", "legato",
            "hammer-pull", "palm-mute", "vibrato", "song-sections", "section-practice",
            "drum-double-stroke", "drum-accents", "drum-kick-variations", "drum-open-hihat",
            "drum-sixteenth-groove", "drum-fill-timing", "drum-sixteenth-fill");
    private static final Set<String> EARLY_INTERMEDIATE = Set.of(
            "dynamics", "blues-scale", "harmonic-field", "seventh-chords", "minor-harmonic-field",
            "harmonic-functions", "cadences", "ear-chords", "ear-melody", "sight-reading", "fingerstyle",
            "keys-voicings", "keys-arpeggios", "string-skipping", "speed", "phrasing", "targeting",
            "major-improv", "blues-improv", "arpeggios", "memorization", "performance", "motif",
            "drum-paradiddle", "drum-flams", "drum-shuffle", "drum-half-time",
            "drum-limb-independence", "drum-hand-foot-unisons", "drum-syncopated-kick");
    private static final Set<String> UPPER_INTERMEDIATE = Set.of(
            "harmonic-minor", "melodic-minor", "minor-dominant", "secondary-dominants", "voice-leading",
            "reharmonization", "ear-progressions", "transcription", "ensemble-reading", "chord-melody",
            "economy-picking", "sweep-picking", "tapping", "hybrid-picking", "endurance",
            "arpeggio-improv", "song-analysis", "recording-review", "melody-writing", "form", "arrangement",
            "drum-linear-groove", "drum-ghost-notes", "drum-ride-coordination",
            "drum-fill-orchestration", "drum-dynamics", "drum-song-form", "drum-play-along");
    private static final Set<String> ADVANCED = Set.of(
            "whole-tone", "diminished-scale", "altered-scale", "jazz-harmony", "outside-playing",
            "advanced-improv", "stage-readiness", "production-demo", "modes");
    private static final Set<String> KNOWLEDGE = Set.of(
            "notes", "intervals", "major-scale", "major-pentatonic", "minor-pentatonic", "blues-scale",
            "natural-minor", "harmonic-minor", "melodic-minor", "whole-tone", "diminished-scale",
            "altered-scale", "triads", "triad-inversions", "seventh-chords", "harmonic-field",
            "minor-harmonic-field", "minor-dominant", "harmonic-functions", "cadences",
            "secondary-dominants", "voice-leading", "reharmonization", "jazz-harmony", "meter",
            "subdivisions", "song-analysis", "form", "song-sections");

    private CurriculumTaxonomy() {}

    static Skill apply(Skill skill) {
        skill.configureCurriculum(stage(skill.getId()), kind(skill.getId()), track(skill.getDomain()));
        return skill;
    }

    private static LearningStage stage(String id) {
        if (FIRST_STEPS.contains(id)) return LearningStage.FIRST_STEPS;
        if (BEGINNER.contains(id)) return LearningStage.BEGINNER;
        if (BEGINNER_ADVANCED.contains(id)) return LearningStage.BEGINNER_ADVANCED;
        if (EARLY_INTERMEDIATE.contains(id)) return LearningStage.EARLY_INTERMEDIATE;
        if (UPPER_INTERMEDIATE.contains(id)) return LearningStage.UPPER_INTERMEDIATE;
        if (ADVANCED.contains(id)) return LearningStage.ADVANCED;
        return LearningStage.INTERMEDIATE;
    }

    private static SkillKind kind(String id) {
        return KNOWLEDGE.contains(id) ? SkillKind.KNOWLEDGE : SkillKind.ABILITY;
    }

    private static LearningTrack track(String domain) {
        return switch (domain) {
            case "Fundamentos", "Ritmo", "Grooves", "Viradas" -> LearningTrack.RHYTHM;
            case "Ouvido" -> LearningTrack.EAR;
            case "Leitura" -> LearningTrack.READING;
            case "Teoria", "Escalas", "Harmonia" -> LearningTrack.HARMONY;
            case "Repertório" -> LearningTrack.REPERTOIRE;
            case "Improvisação", "Expressão" -> LearningTrack.IMPROVISATION;
            case "Composição", "Arranjo" -> LearningTrack.CREATION;
            case "Performance", "Musicalidade" -> LearningTrack.PERFORMANCE;
            default -> LearningTrack.TECHNIQUE;
        };
    }
}
