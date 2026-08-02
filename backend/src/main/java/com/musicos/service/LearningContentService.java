package com.musicos.service;

import static com.musicos.api.ApiModels.*;
import static com.musicos.service.ViewMapper.*;

import com.musicos.domain.*;
import com.musicos.repository.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LearningContentService {
    private final LibraryContentRepository library;
    private final ExerciseRepository exercises;
    private final ExerciseAttemptRepository exerciseAttempts;
    private final EarTrainingAttemptRepository earAttempts;
    private final UserPreferencesRepository preferences;
    private final SkillRepository skills;
    private final SongRepository songs;
    private final MusicProjectRepository projects;

    public LearningContentService(LibraryContentRepository library, ExerciseRepository exercises,
                                  ExerciseAttemptRepository exerciseAttempts,
                                  EarTrainingAttemptRepository earAttempts,
                                  UserPreferencesRepository preferences, SkillRepository skills,
                                  SongRepository songs, MusicProjectRepository projects) {
        this.library = library;
        this.exercises = exercises;
        this.exerciseAttempts = exerciseAttempts;
        this.earAttempts = earAttempts;
        this.preferences = preferences;
        this.skills = skills;
        this.songs = songs;
        this.projects = projects;
    }

    @Transactional
    public LibraryContentView saveLesson(LibraryContentRequest request) {
        var content = library.findById(request.id()).orElseGet(() -> new LibraryContent(
                request.id(), request.friendlyTitle(), request.technicalName(), request.category(),
                value(request.summary()), list(request.body()), list(request.examples()), list(request.related())));
        content.update(request.friendlyTitle(), request.technicalName(), request.category(), value(request.summary()),
                request.skillId(), value(request.level(), "beginner"), Math.max(1, request.estimatedMinutes()),
                request.diagramType(), request.diagramData(), request.tablature(), list(request.objectives()),
                list(request.body()), list(request.examples()), list(request.commonMistakes()),
                steps(request.steps()), list(request.related()));
        return library(library.save(content));
    }

    @Transactional
    public void deleteLesson(String id) {
        if (!library.existsById(id)) throw new NotFoundException("Conteúdo não encontrado");
        library.deleteById(id);
    }

    @Transactional
    public ExerciseView saveExercise(ExerciseRequest request) {
        var entity = exercises.findById(request.id()).orElseGet(() -> new Exercise(
                request.id(), request.name(), request.technique(), request.instrument(), request.targetBpm(),
                request.currentBpm(), request.minutes(), value(request.description()), request.skillId()));
        entity.update(request.name(), request.technique(), request.instrument(), request.targetBpm(),
                request.currentBpm(), request.minutes(), value(request.description()), request.skillId(),
                request.difficulty(), request.minBpm(), request.bpmStep(), request.passAccuracy(),
                request.passRepetitions(), list(request.instructions()), variations(request.variations()));
        return exercise(exercises.save(entity));
    }

    @Transactional
    public void deleteExercise(String id) {
        if (!exercises.existsById(id)) throw new NotFoundException("Exercício não encontrado");
        exercises.deleteById(id);
    }

    @Transactional
    public ExerciseAttemptView recordExerciseAttempt(String exerciseId, ExerciseAttemptRequest request) {
        var entity = exercises.findById(exerciseId)
                .orElseThrow(() -> new NotFoundException("Exercício não encontrado"));
        var passed = request.accuracy() >= entity.getPassAccuracy()
                && request.repetitions() >= entity.getPassRepetitions()
                && request.bpm() >= entity.getCurrentBpm();
        var attempt = exerciseAttempts.save(new ExerciseAttempt(exerciseId, request.bpm(), request.accuracy(),
                request.durationSeconds(), request.repetitions(), request.perceivedDifficulty(), passed));
        entity.recordResult(request.bpm(), passed);
        exercises.save(entity);
        if (entity.getSkillId() != null) {
            skills.findById(entity.getSkillId()).ifPresent(skill -> {
                skill.recordEvidence(request.durationSeconds() / 3600.0, request.accuracy(), request.bpm(),
                        false, passed, false, null, request.perceivedDifficulty());
                skills.save(skill);
            });
        }
        return exerciseAttempt(attempt);
    }

    public List<ExerciseAttemptView> exerciseHistory(String exerciseId) {
        return exerciseAttempts.findTop20ByExerciseIdOrderByPracticedAtDesc(exerciseId)
                .stream().map(ViewMapper::exerciseAttempt).toList();
    }

    @Transactional
    public EarTrainingStatsView recordEarAttempt(EarAttemptRequest request) {
        earAttempts.save(new EarTrainingAttempt(request.module(), request.prompt(), request.answer(),
                request.correct(), request.responseMillis(), request.difficulty()));
        skills.findById(skillForEarModule(request.module())).ifPresent(skill -> {
            skill.recordEvidence(0.03, request.correct() ? 100 : 40, null, false,
                    request.correct(), false, null, request.correct() ? 2 : 4);
            skills.save(skill);
        });
        return earStats();
    }

    public EarTrainingStatsView earStats() {
        var attempts = earAttempts.findTop100ByOrderByPracticedAtDesc();
        var grouped = attempts.stream().collect(Collectors.groupingBy(EarTrainingAttempt::getModule));
        var modules = grouped.entrySet().stream().map(entry -> moduleStats(entry.getKey(), entry.getValue()))
                .sorted(java.util.Comparator.comparing(EarModuleStatsView::module)).toList();
        var correct = attempts.stream().filter(EarTrainingAttempt::isCorrect).count();
        return new EarTrainingStatsView(attempts.size(), percentage(correct, attempts.size()), modules);
    }

    public PreferencesView preferences() {
        return view(preferences.findById("default").orElseGet(() ->
                new UserPreferences("intermediate", 60, List.of("Rock", "Blues", "Post-rock"),
                        List.of("Guns N' Roses", "Jimi Hendrix", "The Beatles"))));
    }

    @Transactional
    public PreferencesView updatePreferences(PreferencesRequest request) {
        var entity = preferences.findById("default").orElseGet(() ->
                new UserPreferences(request.level(), request.sessionMinutes(), list(request.favoriteGenres()),
                        list(request.favoriteArtists()), list(request.favoriteSongs()),
                        request.primaryInstrument() == null ? InstrumentId.GUITAR : request.primaryInstrument()));
        entity.update(request.level(), request.sessionMinutes(), list(request.favoriteGenres()),
                list(request.favoriteArtists()), list(request.favoriteSongs()),
                request.primaryInstrument() == null ? entity.getPrimaryInstrument() : request.primaryInstrument());
        return view(preferences.save(entity));
    }

    @Transactional
    public SongView saveSong(SongRequest request) {
        var sections = list(request.sections()).stream().map(section ->
                new SongSection(section.id(), section.name(), section.progress(), section.bpm(), section.note(),
                        list(section.skillIds()), section.tablature(), section.startSeconds(), section.endSeconds(),
                        section.tonePreset()))
                .toList();
        var entity = songs.findById(request.id()).orElseGet(() -> new Song(
                request.id(), request.title(), request.artist(), value(request.tuning()), value(request.musicalKey()),
                request.bpm(), request.instrument(), request.difficulty(), value(request.status()),
                value(request.notes()), request.progress(), list(request.techniques()), list(request.scales())));
        entity.update(request.title(), request.artist(), value(request.tuning()), value(request.musicalKey()),
                request.bpm(), request.instrument(), request.difficulty(), value(request.status()),
                value(request.notes()), request.progress(), list(request.techniques()), list(request.scales()),
                sections);
        return song(songs.save(entity));
    }

    @Transactional
    public void deleteSong(String id) {
        if (!songs.existsById(id)) throw new NotFoundException("Música não encontrada");
        songs.deleteById(id);
    }

    @Transactional
    public ProjectView saveProject(ProjectRequest request) {
        var riffs = list(request.riffs()).stream()
                .map(riff -> new ProjectRiff(riff.id(), riff.name(), riff.tab())).toList();
        var versions = list(request.versions()).stream()
                .map(version -> new ProjectVersion(version.id(), version.label(), version.date())).toList();
        var entity = projects.findById(request.id()).orElseGet(() -> new MusicProject(
                request.id(), request.name(), value(request.musicalKey()), request.bpm(), value(request.status()),
                value(request.lyrics()), list(request.ideas()), list(request.references())));
        entity.update(request.name(), value(request.musicalKey()), request.bpm(), value(request.status()),
                value(request.lyrics()), list(request.ideas()), list(request.references()), riffs, versions);
        return project(projects.save(entity));
    }

    @Transactional
    public void deleteProject(String id) {
        if (!projects.existsById(id)) throw new NotFoundException("Projeto não encontrado");
        projects.deleteById(id);
    }

    private EarModuleStatsView moduleStats(String module, List<EarTrainingAttempt> attempts) {
        var correct = attempts.stream().filter(EarTrainingAttempt::isCorrect).count();
        var average = (int) attempts.stream().mapToInt(EarTrainingAttempt::getResponseMillis).average().orElse(0);
        var accuracy = percentage(correct, attempts.size());
        var currentDifficulty = attempts.stream().mapToInt(EarTrainingAttempt::getDifficulty).max().orElse(1);
        var recommended = accuracy >= 80 && attempts.size() >= 5 ? Math.min(5, currentDifficulty + 1)
                : accuracy < 55 ? Math.max(1, currentDifficulty - 1) : currentDifficulty;
        var focusPrompt = attempts.stream()
                .collect(Collectors.groupingBy(EarTrainingAttempt::getPrompt))
                .entrySet().stream()
                .min(java.util.Comparator.comparingDouble(entry -> entry.getValue().stream()
                        .mapToInt(value -> value.isCorrect() ? 1 : 0).average().orElse(0)))
                .map(Map.Entry::getKey).orElse(null);
        return new EarModuleStatsView(module, attempts.size(), correct, accuracy, average, recommended, focusPrompt);
    }

    private int percentage(long part, long total) {
        return total == 0 ? 0 : (int) Math.round(part * 100.0 / total);
    }

    private String skillForEarModule(String module) {
        return switch (module) {
            case "chords" -> "ear-chords";
            case "rhythms" -> "rhythmic-dictation";
            case "progressions" -> "ear-progressions";
            case "melodies" -> "melodic-dictation";
            default -> "ear-intervals";
        };
    }

    private PreferencesView view(UserPreferences value) {
        return new PreferencesView(value.getLevel(), value.getSessionMinutes(),
                value.getFavoriteGenres(), value.getFavoriteArtists(), value.getFavoriteSongs(),
                value.getPrimaryInstrument(), value.isOnboardingCompleted(), value.getRhythmBaseline(),
                value.getEarBaseline(), value.getTechniqueBaseline());
    }

    private List<LessonStep> steps(List<LessonStepView> values) {
        return list(values).stream().map(value -> new LessonStep(value.title(), value.explanation(),
                value.musicalExample(), value.notation(), value.tablature(), value.audioNotes())).toList();
    }

    private List<ExerciseVariation> variations(List<ExerciseVariationView> values) {
        return list(values).stream().map(value -> new ExerciseVariation(value.name(), value.instructions(),
                value.bpmOffset(), value.durationMinutes())).toList();
    }

    private <T> List<T> list(List<T> value) {
        return value == null ? List.of() : value;
    }

    private String value(String value) {
        return value(value, "");
    }

    private String value(String value, String fallback) {
        return value == null ? fallback : value;
    }
}
