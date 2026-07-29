package com.musicos.service;

import static com.musicos.api.ApiModels.*;

import com.musicos.repository.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DataManagementService {
    private final LearningContentService learning;
    private final LibraryContentRepository library;
    private final ExerciseRepository exercises;
    private final SongRepository songs;
    private final MusicProjectRepository projects;
    private final JournalEntryRepository journal;
    private final PracticeRecordingRepository recordings;
    private final UserPreferencesRepository preferences;
    private final SkillRepository skills;
    private final Path root;

    public DataManagementService(LearningContentService learning, LibraryContentRepository library,
                                 ExerciseRepository exercises, SongRepository songs,
                                 MusicProjectRepository projects, JournalEntryRepository journal,
                                 PracticeRecordingRepository recordings, UserPreferencesRepository preferences,
                                 SkillRepository skills,
                                 @Value("${music-os.data-dir:${java.io.tmpdir}/music-os}") String dataDirectory) {
        this.learning = learning;
        this.library = library;
        this.exercises = exercises;
        this.songs = songs;
        this.projects = projects;
        this.journal = journal;
        this.recordings = recordings;
        this.preferences = preferences;
        this.skills = skills;
        this.root = Path.of(dataDirectory).toAbsolutePath().normalize();
    }

    @Transactional(readOnly = true)
    public BackupSnapshot backup() {
        var profile = preferences.findById("default").map(value -> new PreferencesView(
                value.getLevel(), value.getSessionMinutes(), value.getFavoriteGenres(),
                value.getFavoriteArtists(), value.getFavoriteSongs(), value.getPrimaryInstrument(),
                value.isOnboardingCompleted(), value.getRhythmBaseline(), value.getEarBaseline(),
                value.getTechniqueBaseline()))
                .orElse(null);
        return new BackupSnapshot("music-os-backup-v1", Instant.now(), profile,
                library.findAll().stream().map(value -> {
                    var view = ViewMapper.library(value);
                    return new LibraryContentRequest(view.id(), view.friendlyTitle(), view.technicalName(),
                            view.category(), view.summary(), view.skillId(), view.level(), view.estimatedMinutes(),
                            view.diagramType(), view.diagramData(), view.tablature(), view.objectives(), view.body(),
                            view.examples(), view.commonMistakes(), view.steps(), view.related());
                }).toList(),
                exercises.findAll().stream().map(value -> {
                    var view = ViewMapper.exercise(value);
                    return new ExerciseRequest(view.id(), view.name(), view.technique(), view.instrument(),
                            view.targetBpm(), view.currentBpm(), view.minutes(), view.description(), view.skillId(),
                            view.difficulty(), view.minBpm(), view.bpmStep(), view.passAccuracy(),
                            view.passRepetitions(), view.instructions(), view.variations());
                }).toList(),
                songs.findAll().stream().map(value -> {
                    var view = ViewMapper.song(value);
                    return new SongRequest(view.id(), view.title(), view.artist(), view.tuning(), view.musicalKey(),
                            view.bpm(), view.instrument(), view.difficulty(), view.status(), view.notes(),
                            view.progress(), view.techniques(), view.scales(), view.sections());
                }).toList(),
                projects.findAll().stream().map(value -> {
                    var view = ViewMapper.project(value);
                    return new ProjectRequest(view.id(), view.name(), view.musicalKey(), view.bpm(), view.status(),
                            view.lyrics(), view.ideas(), view.references(), view.riffs(), view.versions());
                }).toList(),
                skills.findAll().stream().map(value -> new SkillProgressBackup(value.getId(), value.getState(),
                        value.getHours(), value.getAccuracy(), value.getCurrentBpm(), value.getPracticeDays(),
                        value.getReviewCount(), value.getExerciseCompletions(), value.getSongsCompleted(),
                        value.getSelfRating(), value.getLastPracticedAt(), value.getRetention(),
                        value.getReviewIntervalDays(), value.getNextReviewAt())).toList(),
                journal.findAll().stream().map(value -> new JournalBackup(value.getPracticedAt(),
                        value.getDurationSeconds(), value.getInstrument(), value.getWorked(),
                        value.getDifficulties(), value.getImprovements(), value.getNotes())).toList());
    }

    @Transactional
    public RestoreResult restore(BackupSnapshot snapshot) {
        if (snapshot == null || !"music-os-backup-v1".equals(snapshot.version())) {
            throw new IllegalArgumentException("Este arquivo nao e um backup compativel do Music OS");
        }
        if (snapshot.preferences() != null) {
            var value = snapshot.preferences();
            learning.updatePreferences(new PreferencesRequest(value.level(), value.sessionMinutes(),
                    value.favoriteGenres(), value.favoriteArtists(), value.favoriteSongs(),
                    value.primaryInstrument()));
            if (value.onboardingCompleted()) {
                preferences.findById("default").ifPresent(profile -> {
                    profile.completeOnboarding(value.rhythmBaseline(), value.earBaseline(),
                            value.techniqueBaseline());
                    preferences.save(profile);
                });
            }
        }
        list(snapshot.lessons()).forEach(learning::saveLesson);
        list(snapshot.exercises()).forEach(learning::saveExercise);
        list(snapshot.songs()).forEach(learning::saveSong);
        list(snapshot.projects()).forEach(learning::saveProject);
        list(snapshot.skillProgress()).forEach(value -> skills.findById(value.id()).ifPresent(skill -> {
            skill.restoreProgress(value.state(), value.hours(), value.accuracy(), value.currentBpm(),
                    value.practiceDays(), value.reviewCount(), value.exerciseCompletions(),
                    value.songsCompleted(), value.selfRating(), value.lastPracticedAt(), value.retention(),
                    value.reviewIntervalDays(), value.nextReviewAt());
            skills.save(skill);
        }));
        list(snapshot.journal()).stream().filter(value -> !journal.existsByPracticedAt(value.practicedAt()))
                .forEach(value -> journal.save(new com.musicos.domain.JournalEntry(value.practicedAt(),
                        value.durationSeconds(), value.instrument(), list(value.worked()), value.difficulties(),
                        value.improvements(), value.notes())));
        return new RestoreResult(list(snapshot.lessons()).size(), list(snapshot.exercises()).size(),
                list(snapshot.songs()).size(), list(snapshot.projects()).size(),
                "Backup restaurado sem apagar conteudo mais recente.");
    }

    @Transactional(readOnly = true)
    public byte[] journalCsv() {
        var csv = new StringBuilder("\uFEFFdata,instrumento,duracao_segundos,conteudos,dificuldades,melhorias,notas\n");
        journal.findAll().stream()
                .sorted(java.util.Comparator.comparing(com.musicos.domain.JournalEntry::getPracticedAt).reversed())
                .forEach(entry -> csv.append(cell(entry.getPracticedAt().toString())).append(',')
                        .append(cell(entry.getInstrument().value())).append(',')
                        .append(entry.getDurationSeconds()).append(',')
                        .append(cell(String.join(" | ", entry.getWorked()))).append(',')
                        .append(cell(entry.getDifficulties())).append(',')
                        .append(cell(entry.getImprovements())).append(',')
                        .append(cell(entry.getNotes())).append('\n'));
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    public ImportedFileView importFile(MultipartFile file) {
        if (file.isEmpty()) throw new IllegalArgumentException("O arquivo esta vazio");
        var original = file.getOriginalFilename() == null ? "arquivo" : Path.of(file.getOriginalFilename()).getFileName().toString();
        var type = type(original);
        if ("unsupported".equals(type)) {
            throw new IllegalArgumentException("Use audio, MIDI, MusicXML ou arquivos Guitar Pro");
        }
        try {
            var imports = root.resolve("imports");
            Files.createDirectories(imports);
            var id = UUID.randomUUID().toString();
            var safeName = id + "-" + original.replaceAll("[^a-zA-Z0-9._-]", "_");
            var destination = imports.resolve(safeName).normalize();
            if (!destination.startsWith(imports)) throw new IllegalArgumentException("Nome de arquivo invalido");
            file.transferTo(destination);
            return new ImportedFileView(id, original, type, file.getSize(), Instant.now(), destination.toString());
        } catch (IOException exception) {
            throw new IllegalStateException("Nao foi possivel importar o arquivo", exception);
        }
    }

    @Transactional(readOnly = true)
    public DataStatusView status() {
        return new DataStatusView(root.toString(), library.count(), exercises.count(), songs.count(),
                projects.count(), journal.count(), recordings.count());
    }

    private String type(String name) {
        var lower = name.toLowerCase();
        if (lower.matches(".*\\.(wav|mp3|ogg|flac|m4a)$")) return "audio";
        if (lower.matches(".*\\.(mid|midi)$")) return "midi";
        if (lower.matches(".*\\.(musicxml|xml|mxl)$")) return "musicxml";
        if (lower.matches(".*\\.(gp|gp3|gp4|gp5|gpx)$")) return "guitar-pro";
        return "unsupported";
    }

    private String cell(String value) {
        return "\"" + (value == null ? "" : value.replace("\"", "\"\"")) + "\"";
    }

    private <T> List<T> list(List<T> value) {
        return value == null ? List.of() : value;
    }
}
