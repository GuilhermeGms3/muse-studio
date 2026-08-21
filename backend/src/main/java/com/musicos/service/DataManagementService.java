package com.musicos.service;

import com.musicos.domain.LocalProfile;

import static com.musicos.api.ApiModels.*;

import com.musicos.domain.AssessmentAttempt;
import com.musicos.domain.Evidence;
import com.musicos.domain.InstrumentProfile;
import com.musicos.domain.LearningGoal;
import com.musicos.domain.LearningPath;
import com.musicos.domain.LearningPathStep;
import com.musicos.repository.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.LinkedHashMap;
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
    private final InstrumentProfileRepository instrumentProfiles;
    private final LearningGoalRepository learningGoals;
    private final LearningPathRepository learningPaths;
    private final EvidenceRepository evidence;
    private final MasteryRepository mastery;
    private final AssessmentAttemptRepository assessmentAttempts;
    private final EvidenceEngine evidenceEngine;
    private final Path root;

    public DataManagementService(LearningContentService learning, LibraryContentRepository library,
                                 ExerciseRepository exercises, SongRepository songs,
                                 MusicProjectRepository projects, JournalEntryRepository journal,
                                 PracticeRecordingRepository recordings, UserPreferencesRepository preferences,
                                 SkillRepository skills, InstrumentProfileRepository instrumentProfiles,
                                 LearningGoalRepository learningGoals, LearningPathRepository learningPaths,
                                 EvidenceRepository evidence, MasteryRepository mastery,
                                 AssessmentAttemptRepository assessmentAttempts, EvidenceEngine evidenceEngine,
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
        this.instrumentProfiles = instrumentProfiles;
        this.learningGoals = learningGoals;
        this.learningPaths = learningPaths;
        this.evidence = evidence;
        this.mastery = mastery;
        this.assessmentAttempts = assessmentAttempts;
        this.evidenceEngine = evidenceEngine;
        this.root = Path.of(dataDirectory).toAbsolutePath().normalize();
    }

    @Transactional(readOnly = true)
    public BackupSnapshot backup() {
        var profile = preferences.findById(LocalProfile.DEFAULT_ID).map(value -> new PreferencesView(
                value.getLevel(), value.getSessionMinutes(), value.getFavoriteGenres(),
                value.getFavoriteArtists(), value.getFavoriteSongs(), value.getPrimaryInstrument(),
                value.isOnboardingCompleted(), value.getRhythmBaseline(), value.getEarBaseline(),
                value.getTechniqueBaseline()))
                .orElse(null);
        return new BackupSnapshot("muse-studio-backup-v2", Instant.now(), profile,
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
                        value.getDifficulties(), value.getImprovements(), value.getNotes())).toList(),
                pedagogicalBackup());
    }

    private PedagogicalBackup pedagogicalBackup() {
        return new PedagogicalBackup(
                instrumentProfiles.findAll().stream().map(value -> new InstrumentProfileBackup(
                        value.getId(), value.getOwnerId(), value.getInstrument(), value.getDisplayName(),
                        value.getCurrentStage(), value.getCurriculumId(), value.getLegacyPreferencesId(),
                        value.isPrimaryProfile(), value.isActive(), value.getCreatedAt(), value.getUpdatedAt()))
                        .toList(),
                learningGoals.findAll().stream().map(value -> new LearningGoalBackup(
                        value.getId(), value.getInstrumentProfileId(), value.getCurriculumId(), value.getTitle(),
                        value.getDesiredOutcome(), value.getMusicalContext(), value.getType(), value.getStatus(),
                        value.getPriority(), value.getTargetDate(), value.getPriorityCompetencyIds(),
                        value.getRepertoireIds(), value.getCreatedAt(), value.getUpdatedAt())).toList(),
                learningPaths.findAll().stream().map(value -> new LearningPathBackup(
                        value.getId(), value.getInstrumentProfileId(), value.getCurriculumId(), value.getTitle(),
                        value.getDerivationVersion(), value.getDerivationReason(), value.getStatus(),
                        value.getLearningGoalIds(), value.getSteps().stream().map(step -> new LearningPathStepBackup(
                                step.getCompetencyId(), step.getKind(), step.getReadiness(), step.getRationale()))
                                .toList(), value.getCreatedAt(), value.getUpdatedAt())).toList(),
                evidence.findAll().stream().map(value -> new EvidenceBackup(
                        value.getId(), value.getInstrumentProfileId(), value.getCompetencyId(),
                        value.getCriterionKey(), value.getType(), value.getState(), value.getFunctionalWeight(),
                        value.getReliability(), value.getResult(), value.getSourceType(), value.getSourceId(),
                        value.getIndependenceKey(), value.getChallengeLevel(), value.getObservation(),
                        value.getConditions(), value.getProtocolVersion(), value.getAnalyzerVersion(),
                        value.getArtifactReference(), value.getSupersedesEvidenceId(), value.getOccurredAt(),
                        value.getCapturedAt(), value.getValidUntil())).toList(),
                mastery.findAll().stream().map(value -> new MasteryBackup(
                        value.getId(), value.getInstrumentProfileId(), value.getCompetencyId(), value.getState(),
                        value.getEvidencePolicyVersion(), value.isMandatoryCriteriaCovered(),
                        value.getIndependentEvidenceCount(), value.isApplicationObserved(),
                        value.isTransferObserved(), value.isRetentionObserved(), value.isUnresolvedConflict(),
                        value.getRationale(), value.getAssessedAt(), value.getLastEvidenceAt(),
                        value.getNextReviewAt(), value.getSupportingEvidenceIds(), value.getLimitingEvidenceIds()))
                        .toList(),
                assessmentAttempts.findAll().stream().map(value -> new AssessmentAttemptBackup(
                        value.getId(), value.getAssessmentId(), value.getInstrumentProfileId(),
                        value.getObserverType(), value.getChallengeLevel(), value.getArtifactReference(),
                        value.getNote(), value.getCompletedAt(), value.getEvidenceIds())).toList());
    }

    @Transactional
    public RestoreResult restore(BackupSnapshot snapshot) {
        if (snapshot == null || !("music-os-backup-v1".equals(snapshot.version())
                || "muse-studio-backup-v2".equals(snapshot.version()))) {
            throw new IllegalArgumentException("Este arquivo nao e um backup compativel do Music OS");
        }
        if (snapshot.preferences() != null) {
            var value = snapshot.preferences();
            learning.updatePreferences(new PreferencesRequest(value.level(), value.sessionMinutes(),
                    value.favoriteGenres(), value.favoriteArtists(), value.favoriteSongs(),
                    value.primaryInstrument()));
            if (value.onboardingCompleted()) {
                preferences.findById(LocalProfile.DEFAULT_ID).ifPresent(profile -> {
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
        var restoredPedagogy = restorePedagogy(snapshot.pedagogy());
        return new RestoreResult(list(snapshot.lessons()).size(), list(snapshot.exercises()).size(),
                list(snapshot.songs()).size(), list(snapshot.projects()).size(),
                restoredPedagogy.instrumentProfiles(), restoredPedagogy.learningGoals(),
                restoredPedagogy.learningPaths(), restoredPedagogy.evidence(),
                restoredPedagogy.assessmentAttempts(),
                "Backup restaurado. Registros com o mesmo identificador foram atualizados; "
                        + "dados adicionais permaneceram preservados.");
    }

    private record PedagogyRestoreCounts(int instrumentProfiles, int learningGoals, int learningPaths,
                                         int evidence, int assessmentAttempts) {}

    private PedagogyRestoreCounts restorePedagogy(PedagogicalBackup backup) {
        if (backup == null) return new PedagogyRestoreCounts(0, 0, 0, 0, 0);
        for (var value : list(backup.instrumentProfiles())) {
            var profile = instrumentProfiles.findById(value.id()).orElseGet(() ->
                    InstrumentProfile.restoreSnapshot(value.id(), value.ownerId(), value.instrument(),
                            value.displayName(), value.currentStage(), value.curriculumId(),
                            value.legacyPreferencesId(), value.primaryProfile(), value.active(),
                            value.createdAt(), value.updatedAt()));
            if (instrumentProfiles.existsById(value.id())) {
                profile.restoreState(value.ownerId(), value.instrument(), value.displayName(),
                        value.currentStage(), value.curriculumId(), value.legacyPreferencesId(),
                        value.primaryProfile(), value.active(), value.createdAt(), value.updatedAt());
            }
            instrumentProfiles.save(profile);
        }
        for (var value : list(backup.learningGoals())) {
            var goal = learningGoals.findById(value.id()).orElseGet(() -> LearningGoal.restoreSnapshot(
                    value.id(), value.instrumentProfileId(), value.curriculumId(), value.title(),
                    value.desiredOutcome(), value.musicalContext(), value.type(), value.status(), value.priority(),
                    value.targetDate(), value.priorityCompetencyIds(), value.repertoireIds(),
                    value.createdAt(), value.updatedAt()));
            if (learningGoals.existsById(value.id())) {
                goal.restoreState(value.instrumentProfileId(), value.curriculumId(), value.title(),
                        value.desiredOutcome(), value.musicalContext(), value.type(), value.status(),
                        value.priority(), value.targetDate(), value.priorityCompetencyIds(), value.repertoireIds(),
                        value.createdAt(), value.updatedAt());
            }
            learningGoals.save(goal);
        }
        for (var value : list(backup.learningPaths())) {
            var steps = list(value.steps()).stream().map(step -> new LearningPathStep(
                    step.competencyId(), step.kind(), step.readiness(), step.rationale())).toList();
            var path = learningPaths.findById(value.id()).orElseGet(() -> LearningPath.restoreSnapshot(
                    value.id(), value.instrumentProfileId(), value.curriculumId(), value.title(),
                    value.derivationVersion(), value.derivationReason(), value.status(),
                    value.learningGoalIds(), steps, value.createdAt(), value.updatedAt()));
            if (learningPaths.existsById(value.id())) {
                path.restoreState(value.instrumentProfileId(), value.curriculumId(), value.title(),
                        value.derivationVersion(), value.derivationReason(), value.status(),
                        value.learningGoalIds(), steps, value.createdAt(), value.updatedAt());
            }
            learningPaths.save(path);
        }

        restoreEvidence(list(backup.evidence()));
        for (var value : list(backup.assessmentAttempts())) {
            if (!assessmentAttempts.existsById(value.id())) {
                assessmentAttempts.save(AssessmentAttempt.restoreSnapshot(
                        value.id(), value.assessmentId(), value.instrumentProfileId(), value.observerType(),
                        value.challengeLevel(), value.artifactReference(), value.note(), value.completedAt(),
                        value.evidenceIds()));
            }
        }
        var recalculations = new LinkedHashMap<String, String[]>();
        list(backup.evidence()).forEach(value -> recalculations.put(
                value.instrumentProfileId() + "\u0000" + value.competencyId(),
                new String[]{value.instrumentProfileId(), value.competencyId()}));
        list(backup.mastery()).forEach(value -> recalculations.put(
                value.instrumentProfileId() + "\u0000" + value.competencyId(),
                new String[]{value.instrumentProfileId(), value.competencyId()}));
        recalculations.values().forEach(value -> evidenceEngine.recalculate(value[0], value[1], Instant.now()));
        return new PedagogyRestoreCounts(
                list(backup.instrumentProfiles()).size(), list(backup.learningGoals()).size(),
                list(backup.learningPaths()).size(), list(backup.evidence()).size(),
                list(backup.assessmentAttempts()).size());
    }

    private void restoreEvidence(List<EvidenceBackup> snapshots) {
        var pending = new LinkedHashMap<String, EvidenceBackup>();
        snapshots.forEach(value -> {
            if (!evidence.existsById(value.id())) pending.put(value.id(), value);
        });
        while (!pending.isEmpty()) {
            var restored = pending.values().stream()
                    .filter(value -> value.supersedesEvidenceId() == null
                            || evidence.existsById(value.supersedesEvidenceId()))
                    .toList();
            if (restored.isEmpty()) {
                throw new IllegalArgumentException("Backup possui cadeia de evidências inválida");
            }
            restored.forEach(value -> {
                evidence.save(Evidence.restoreSnapshot(
                        value.id(), value.instrumentProfileId(), value.competencyId(), value.criterionKey(),
                        value.type(), value.state(), value.functionalWeight(), value.reliability(), value.result(),
                        value.sourceType(), value.sourceId(), value.independenceKey(), value.challengeLevel(),
                        value.observation(), value.conditions(), value.protocolVersion(), value.analyzerVersion(),
                        value.artifactReference(), value.supersedesEvidenceId(), value.occurredAt(),
                        value.capturedAt(), value.validUntil()));
                pending.remove(value.id());
            });
            evidence.flush();
        }
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
