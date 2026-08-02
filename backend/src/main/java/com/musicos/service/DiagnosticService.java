package com.musicos.service;

import static com.musicos.api.ApiModels.*;

import com.musicos.domain.InstrumentId;
import com.musicos.repository.SkillRepository;
import com.musicos.repository.UserPreferencesRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DiagnosticService {
    private final UserPreferencesRepository preferences;
    private final SkillRepository skills;
    private final StudyPlanService studyPlans;
    private final ProgressEngine progress;

    public DiagnosticService(UserPreferencesRepository preferences, SkillRepository skills,
                             StudyPlanService studyPlans, ProgressEngine progress) {
        this.preferences = preferences;
        this.skills = skills;
        this.studyPlans = studyPlans;
        this.progress = progress;
    }

    @Transactional
    public DiagnosticView complete(DiagnosticRequest request) {
        var profile = preferences.findById("default").orElseGet(() ->
                new com.musicos.domain.UserPreferences(request.level(), request.sessionMinutes(),
                        list(request.favoriteGenres()), list(request.favoriteArtists()),
                        list(request.favoriteSongs()), request.instrument()));
        profile.update(request.level(), request.sessionMinutes(), list(request.favoriteGenres()),
                list(request.favoriteArtists()), list(request.favoriteSongs()), request.instrument());
        profile.completeOnboarding(request.rhythmScore(), request.earScore(), request.techniqueScore());
        preferences.save(profile);

        var placed = new ArrayList<com.musicos.domain.Skill>();
        place(List.of("pulse", "subdivisions", "meter"), request.rhythmScore(), placed);
        place(List.of("ear-pitch", "ear-intervals"), request.earScore(), placed);
        place(techniquePath(request.instrument()), request.techniqueScore(), placed);
        studyPlans.regenerate(request.instrument());

        var starting = placed.stream().distinct().map(skill -> {
            var evaluation = progress.evaluate(skill);
            return ViewMapper.skill(skill, evaluation.progress(), evaluation.nextRequirements());
        }).toList();
        var lowest = Math.min(request.rhythmScore(), Math.min(request.earScore(), request.techniqueScore()));
        var recommendation = lowest == request.rhythmScore()
                ? "Comece pelo pulso: ele vai sustentar todo o restante."
                : lowest == request.earScore()
                ? "Treine ouvir antes de nomear; sua primeira trilha prioriza referencias sonoras."
                : "A primeira semana vai consolidar movimento e controle antes de subir o BPM.";
        return new DiagnosticView(new PreferencesView(profile.getLevel(), profile.getSessionMinutes(),
                profile.getFavoriteGenres(), profile.getFavoriteArtists(), profile.getFavoriteSongs(),
                profile.getPrimaryInstrument(), true, profile.getRhythmBaseline(), profile.getEarBaseline(),
                profile.getTechniqueBaseline()), starting, recommendation);
    }

    private void place(List<String> path, int score, List<com.musicos.domain.Skill> result) {
        var visibleNodes = score >= 82 ? path.size() : score >= 45 ? Math.min(2, path.size()) : 1;
        for (var index = 0; index < visibleNodes; index++) {
            var adjusted = Math.max(25, score - index * 12);
            skills.findById(path.get(index)).ifPresent(skill -> {
                var target = skill.getTargetBpm();
                var bpm = target == null ? null : Math.max(40, (int) Math.round(target * adjusted / 100.0));
                skill.applyDiagnosticPlacement(adjusted, bpm);
                result.add(skills.save(skill));
            });
        }
    }

    private List<String> techniquePath(InstrumentId instrument) {
        return switch (instrument) {
            case ACOUSTIC -> List.of("open-chords", "chord-transitions", "strumming");
            case KEYS -> List.of("keyboard-map", "major-scale", "keys-independence");
            case DRUMS -> List.of("drum-kit-map", "drum-rock-groove", "drum-one-beat-fill");
            default -> List.of("guitar-posture", "sync", "alternate-picking");
        };
    }

    private <T> List<T> list(List<T> value) {
        return value == null ? List.of() : value;
    }
}
