package com.musicos.service;

import com.musicos.domain.LocalProfile;

import static com.musicos.api.ApiModels.*;

import com.musicos.domain.InstrumentId;
import com.musicos.domain.Evidence;
import com.musicos.domain.LearningStage;
import com.musicos.repository.InstrumentProfileRepository;
import com.musicos.repository.UserPreferencesRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DiagnosticService {
    private final UserPreferencesRepository preferences;
    private final InstrumentProfileRepository instrumentProfiles;
    private final EvidenceEngine evidenceEngine;

    public DiagnosticService(UserPreferencesRepository preferences,
                             InstrumentProfileRepository instrumentProfiles,
                             EvidenceEngine evidenceEngine) {
        this.preferences = preferences;
        this.instrumentProfiles = instrumentProfiles;
        this.evidenceEngine = evidenceEngine;
    }

    @Transactional
    public DiagnosticView complete(DiagnosticRequest request) {
        var profile = preferences.findById(LocalProfile.DEFAULT_ID).orElseGet(() ->
                new com.musicos.domain.UserPreferences(request.level(), request.sessionMinutes(),
                        list(request.favoriteGenres()), list(request.favoriteArtists()),
                        list(request.favoriteSongs()), request.instrument()));
        profile.update(request.level(), request.sessionMinutes(), list(request.favoriteGenres()),
                list(request.favoriteArtists()), list(request.favoriteSongs()), request.instrument());
        profile.completeOnboarding(request.rhythmScore(), request.earScore(), request.techniqueScore());
        preferences.save(profile);

        instrumentProfiles.findByOwnerIdAndInstrument(LocalProfile.DEFAULT_ID, request.instrument()).ifPresent(instrumentProfile -> {
            instrumentProfile.updateStage(stage(request.level()));
            instrumentProfile.markPrimary(true);
            instrumentProfiles.save(instrumentProfile);
            collectDiagnosticContext(instrumentProfile, request);
        });

        var lowest = Math.min(request.rhythmScore(), Math.min(request.earScore(), request.techniqueScore()));
        var recommendation = lowest == request.rhythmScore()
                ? "Comece pelo pulso: ele vai sustentar todo o restante."
                : lowest == request.earScore()
                ? "Treine ouvir antes de nomear; sua primeira trilha prioriza referencias sonoras."
                : "A primeira semana vai consolidar movimento e controle antes de subir o BPM.";
        return new DiagnosticView(new PreferencesView(profile.getLevel(), profile.getSessionMinutes(),
                profile.getFavoriteGenres(), profile.getFavoriteArtists(), profile.getFavoriteSongs(),
                profile.getPrimaryInstrument(), true, profile.getRhythmBaseline(), profile.getEarBaseline(),
                profile.getTechniqueBaseline()), List.of(), recommendation);
    }

    private void collectDiagnosticContext(com.musicos.domain.InstrumentProfile profile,
                                          DiagnosticRequest request) {
        var techniqueCompetencyId = switch (request.instrument()) {
            case GUITAR -> "alternate-picking";
            case ACOUSTIC -> "chord-transitions";
            case KEYS -> "keys-independence";
            case DRUMS -> "drum-rock-groove";
        };
        collectDiagnosticObservation(profile, "pulse", "ritmo-declarado", request.rhythmScore(),
                "Autopercepção diagnóstica de pulso e ritmo");
        collectDiagnosticObservation(profile,
                request.instrument() == InstrumentId.DRUMS ? "ear-rhythm" : "ear-pitch",
                "ouvido-declarado", request.earScore(), "Autopercepção diagnóstica de percepção musical");
        collectDiagnosticObservation(profile, techniqueCompetencyId, "tecnica-declarada",
                request.techniqueScore(), "Autopercepção diagnóstica de técnica instrumental");
    }

    private void collectDiagnosticObservation(com.musicos.domain.InstrumentProfile profile,
                                               String competencyId, String criterionKey,
                                               int score, String label) {
        var sourceId = "diagnostic-" + java.util.UUID.randomUUID();
        evidenceEngine.collect(new EvidenceEngine.Observation(
                profile.getId(), competencyId, criterionKey, Evidence.Type.DECLARATIVE,
                Evidence.State.PROVISIONAL, Evidence.FunctionalWeight.CONTEXTUAL, Evidence.Reliability.LOW,
                Evidence.Result.INCONCLUSIVE, Evidence.SourceType.DIAGNOSTIC, sourceId, sourceId,
                Math.max(1, Math.min(5, score / 20)),
                label + ": " + score
                        + "/100. Este registro orienta o ponto de partida e não comprova domínio.",
                "Diagnóstico inicial sem observador externo.", null, null, null, null,
                java.time.Instant.now(), null));
    }

    private LearningStage stage(String level) {
        return switch (level == null ? "beginner" : level) {
            case "advanced" -> LearningStage.ADVANCED;
            case "intermediate" -> LearningStage.INTERMEDIATE;
            default -> LearningStage.BEGINNER;
        };
    }

    private <T> List<T> list(List<T> value) {
        return value == null ? List.of() : value;
    }
}
