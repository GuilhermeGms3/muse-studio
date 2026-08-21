package com.musicos.service;

import com.musicos.domain.LocalProfile;

import com.musicos.config.TeachingContentCatalog;
import com.musicos.domain.Assessment;
import com.musicos.domain.Competency;
import com.musicos.domain.CompetencyPrerequisite;
import com.musicos.domain.Curriculum;
import com.musicos.domain.DifficultyDemand;
import com.musicos.domain.InstrumentId;
import com.musicos.domain.InstrumentProfile;
import com.musicos.domain.LearningContentRelation;
import com.musicos.domain.LearningGoal;
import com.musicos.domain.LearningPath;
import com.musicos.domain.LearningPathStep;
import com.musicos.domain.LearningStage;
import com.musicos.domain.Lesson;
import com.musicos.domain.Mission;
import com.musicos.repository.AssessmentRepository;
import com.musicos.repository.CompetencyRepository;
import com.musicos.repository.CurriculumRepository;
import com.musicos.repository.ExerciseRepository;
import com.musicos.repository.InstrumentProfileRepository;
import com.musicos.repository.LearningContentRelationRepository;
import com.musicos.repository.LearningGoalRepository;
import com.musicos.repository.LearningPathRepository;
import com.musicos.repository.LessonRepository;
import com.musicos.repository.LibraryContentRepository;
import com.musicos.repository.MissionRepository;
import com.musicos.repository.SkillRepository;
import com.musicos.repository.SongRepository;
import com.musicos.repository.UserPreferencesRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedagogicalDomainMigration {
    private final SkillRepository skills;
    private final CompetencyRepository competencies;
    private final LibraryContentRepository libraryContents;
    private final LessonRepository lessons;
    private final ExerciseRepository exercises;
    private final UserPreferencesRepository preferences;
    private final InstrumentProfileRepository instrumentProfiles;
    private final CurriculumRepository curricula;
    private final LearningPathRepository learningPaths;
    private final LearningGoalRepository learningGoals;
    private final LearningContentRelationRepository contentRelations;
    private final SongRepository songs;
    private final AssessmentRepository assessments;
    private final MissionRepository missions;

    public PedagogicalDomainMigration(SkillRepository skills, CompetencyRepository competencies,
                                      LibraryContentRepository libraryContents, LessonRepository lessons,
                                      ExerciseRepository exercises, UserPreferencesRepository preferences,
                                      InstrumentProfileRepository instrumentProfiles,
                                      CurriculumRepository curricula, LearningPathRepository learningPaths,
                                       LearningGoalRepository learningGoals,
                                       LearningContentRelationRepository contentRelations,
                                       SongRepository songs, AssessmentRepository assessments,
                                       MissionRepository missions) {
        this.skills = skills;
        this.competencies = competencies;
        this.libraryContents = libraryContents;
        this.lessons = lessons;
        this.exercises = exercises;
        this.preferences = preferences;
        this.instrumentProfiles = instrumentProfiles;
        this.curricula = curricula;
        this.learningPaths = learningPaths;
        this.learningGoals = learningGoals;
        this.contentRelations = contentRelations;
        this.songs = songs;
        this.assessments = assessments;
        this.missions = missions;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void migrateExistingDomain() {
        migrateCompetencies();
        migrateLessons();
        migrateExerciseLinks();
        migrateCurricula();
        migrateInstrumentProfiles();
        migrateLearningPaths();
        migrateContentRelations();
        seedTeachingMissions();
    }

    private void seedTeachingMissions() {
        TeachingContentCatalog.units().forEach(unit -> {
            var curriculumId = legacyCurriculumId(unit.instrument());
            if (!curricula.existsById(curriculumId)) {
                throw new IllegalStateException("Currículo ausente para Mission editorial: " + curriculumId);
            }
            if (!competencies.existsById(unit.competencyId())) {
                throw new IllegalStateException("Competência ausente para Mission editorial: "
                        + unit.competencyId());
            }
            competencies.findById(unit.competencyId()).ifPresent(competency -> {
                competency.configureEvidencePolicy(
                        "teaching-runner-v1:" + unit.competencyId(), unit.criterionKeys(), 30);
                competencies.save(competency);
            });
            if (!lessons.existsById(unit.lessonId())) {
                throw new IllegalStateException("Lesson ausente para Mission editorial: " + unit.lessonId());
            }
            var missingExercises = unit.exerciseIds().stream().filter(id -> !exercises.existsById(id)).toList();
            if (!missingExercises.isEmpty()) {
                throw new IllegalStateException("Exercises ausentes para Mission editorial: " + missingExercises);
            }

            var assessmentIds = new ArrayList<String>();
            if (unit.formalAssessment()) {
                var assessmentId = unit.id() + "-assessment";
                assessmentIds.add(assessmentId);
                assessments.findById(assessmentId).ifPresentOrElse(assessment -> {
                    assessment.synchronizeCatalogDefinition(unit.assessmentTitle(), unit.assessmentPurpose(),
                            unit.assessmentType(), "teaching-runner-v2", unit.assessmentInstructions(),
                            unit.assessmentConditions(), unit.allowedSupport(), unit.inconclusiveRule(), 6, 3,
                            DifficultyDemand.unspecified(), List.of(unit.competencyId()), unit.criterionKeys(),
                            unit.rubricLevels());
                    assessment.activate();
                    assessments.save(assessment);
                }, () -> {
                    var assessment = new Assessment(assessmentId, unit.assessmentTitle(), unit.assessmentPurpose(),
                            unit.assessmentType(), "teaching-runner-v2", unit.assessmentInstructions(),
                            unit.assessmentConditions(), unit.allowedSupport(), unit.inconclusiveRule(), 6, 3,
                            DifficultyDemand.unspecified(), List.of(unit.competencyId()), unit.criterionKeys(),
                            unit.rubricLevels());
                    assessment.activate();
                    assessments.save(assessment);
                });
            } else {
                assessments.findById(unit.id() + "-assessment").ifPresent(previous -> {
                    previous.deactivate();
                    assessments.save(previous);
                });
            }
            missions.findById(unit.id()).ifPresentOrElse(mission -> {
                mission.synchronizeCatalogDefinition(unit.title(), unit.objective(), unit.context(),
                        unit.motivation(), unit.estimatedMinutes(), unit.instrument(), unit.stage(),
                        unit.completionCriteria(), unit.expectedEvidence(), unit.musicalApplication(), null,
                        DifficultyDemand.unspecified(), List.of(unit.competencyId()), List.of(unit.lessonId()),
                        unit.exerciseIds(), assessmentIds);
                mission.activate();
                missions.save(mission);
            }, () -> {
                var mission = new Mission(unit.id(), curriculumId, unit.title(), unit.objective(), unit.context(),
                        unit.motivation(), unit.estimatedMinutes(), unit.instrument(), unit.stage(),
                        unit.completionCriteria(), unit.expectedEvidence(), unit.musicalApplication(), null,
                        DifficultyDemand.unspecified(), List.of(unit.competencyId()), List.of(unit.lessonId()),
                        unit.exerciseIds(), assessmentIds);
                mission.activate();
                missions.save(mission);
            });
            linkMissionContent(unit, assessmentIds);
        });
    }

    private void linkMissionContent(TeachingContentCatalog.Unit unit, List<String> assessmentIds) {
        addTeachingRelation(unit.id(), LearningContentRelation.RelationType.COMPOSES,
                LearningContentRelation.ContentType.LESSON, unit.lessonId(),
                "A aula apresenta a oportunidade de aprendizagem desta Mission.");
        unit.exerciseIds().forEach(exerciseId -> addTeachingRelation(unit.id(),
                LearningContentRelation.RelationType.COMPOSES,
                LearningContentRelation.ContentType.EXERCISE, exerciseId,
                "O exercício pratica uma função explícita dentro desta Mission."));
        assessmentIds.forEach(assessmentId -> addTeachingRelation(unit.id(),
                LearningContentRelation.RelationType.EVIDENCES,
                LearningContentRelation.ContentType.ASSESSMENT, assessmentId,
                "O assessment observa os critérios declarados sem presumir domínio."));
        unit.repertoireIds().forEach(songId -> {
            if (!songs.existsById(songId)) {
                throw new IllegalStateException("Repertório ausente para Mission editorial: " + songId);
            }
            addTeachingRelation(unit.id(), LearningContentRelation.RelationType.APPLIES,
                    LearningContentRelation.ContentType.SONG, songId,
                    "A Mission leva a competência a uma aplicação de repertório identificada.");
        });
    }

    private void addTeachingRelation(String missionId, LearningContentRelation.RelationType type,
                                     LearningContentRelation.ContentType targetType, String targetId,
                                     String justification) {
        var value = new LearningContentRelation(LearningContentRelation.ContentType.MISSION, missionId, type,
                targetType, targetId, LearningContentRelation.Strength.STRONG, justification,
                "Relação editorial da primeira jornada.", "teaching-content-catalog");
        if (!contentRelations.existsById(value.getId())) contentRelations.save(value);
    }

    private void migrateCompetencies() {
        var existing = competencies.findAll().stream()
                .collect(Collectors.toMap(Competency::getId, competency -> competency));
        var migrated = new ArrayList<Competency>();
        skills.findAll().forEach(skill -> {
            var prerequisites = skill.getPrerequisites().stream()
                    .map(id -> new CompetencyPrerequisite(id, CompetencyPrerequisite.Type.PEDAGOGICAL))
                    .toList();
            var competency = existing.get(skill.getId());
            if (competency == null) {
                competency = new Competency(
                        skill.getId(), skill.getFriendlyTitle(), skill.getTechnicalName(), skill.getDomain(),
                        skill.getFriendlyTitle(), skill.getDescription(),
                        "Condições ainda não formalizadas; definição migrada da habilidade legada.",
                        skill.getKind(), skill.getTrack(), skill.getStage(), skill.getInstruments(), prerequisites,
                        List.of(), null, null, skill.getId());
            } else if (competency.getLegacySkillId() != null) {
                competency.synchronizeLegacyDefinition(
                        skill.getFriendlyTitle(), skill.getTechnicalName(), skill.getDomain(),
                        skill.getFriendlyTitle(), skill.getDescription(), skill.getKind(), skill.getTrack(),
                        skill.getStage(), skill.getInstruments(), prerequisites);
            }
            migrated.add(competency);
        });
        competencies.saveAll(migrated);
    }

    private void migrateLessons() {
        var existing = lessons.findAll().stream().collect(Collectors.toMap(Lesson::getId, lesson -> lesson));
        var migrated = new ArrayList<Lesson>();
        libraryContents.findAll().forEach(content -> {
            var competencyIds = content.getSkillId() == null ? List.<String>of() : List.of(content.getSkillId());
            var body = String.join("\n\n", content.getBody());
            var lesson = existing.get(content.getId());
            if (lesson == null) {
                lesson = new Lesson(
                        content.getId(), content.getFriendlyTitle(), content.getTechnicalName(),
                        content.getCategory(), content.getSummary(), body, content.getEstimatedMinutes(),
                        parseStage(content.getLevel()), Lesson.Format.TEXT, competencyIds,
                        content.getObjectives(), content.getExamples(), content.getId());
            } else if (lesson.getLegacyLibraryContentId() != null) {
                lesson.synchronizeLegacyContent(
                        content.getFriendlyTitle(), content.getTechnicalName(), content.getCategory(),
                        content.getSummary(), body, content.getEstimatedMinutes(), parseStage(content.getLevel()),
                        competencyIds, content.getObjectives(), content.getExamples());
            }
            migrated.add(lesson);
        });
        lessons.saveAll(migrated);
    }

    private void migrateExerciseLinks() {
        var changed = new ArrayList<com.musicos.domain.Exercise>();
        exercises.findAll().forEach(exercise -> {
            if (exercise.getCompetencyIds().isEmpty() && exercise.getSkillId() != null) {
                exercise.migrateLegacyCompetency();
                changed.add(exercise);
            }
        });
        exercises.saveAll(changed);
    }

    private void migrateCurricula() {
        for (var instrument : InstrumentId.values()) {
            var ids = orderedCompetencies(instrument).stream().map(Competency::getId).toList();
            var curriculumId = legacyCurriculumId(instrument);
            if (ids.isEmpty()) continue;
            var name = "Currículo migrado de " + instrumentLabel(instrument);
            var purpose = "Preservar e organizar a trilha existente por competências observáveis, "
                    + "sem converter progresso legado em domínio comprovado.";
            var audience = "Estudantes atuais do Muse Studio em diferentes pontos de partida.";
            var outcomes = List.of("Evoluir competências musicais observáveis no instrumento.");
            curricula.findById(curriculumId).ifPresentOrElse(curriculum -> {
                curriculum.synchronizeLegacyCurriculum(
                        name, "legacy-v2", purpose, audience, outcomes, ids);
                curricula.save(curriculum);
            }, () -> curricula.save(new Curriculum(
                        curriculumId,
                        name,
                        "legacy-v2",
                        purpose,
                        audience,
                        instrument,
                        LearningStage.FIRST_STEPS,
                        LearningStage.ADVANCED,
                        outcomes,
                        ids)));
        }
    }

    private void migrateInstrumentProfiles() {
        var existingPreferences = preferences.findById(LocalProfile.DEFAULT_ID).orElse(null);
        var stage = existingPreferences == null
                ? LearningStage.FIRST_STEPS : parseStage(existingPreferences.getLevel());
        var primaryInstrument = existingPreferences == null
                ? InstrumentId.GUITAR : existingPreferences.getPrimaryInstrument();
        for (var instrument : InstrumentId.values()) {
            var profile = instrumentProfiles.findByOwnerIdAndInstrument(LocalProfile.DEFAULT_ID, instrument)
                    .orElseGet(() -> new InstrumentProfile(
                            "legacy-profile-" + LocalProfile.DEFAULT_ID + "-" + instrument.value(),
                            LocalProfile.DEFAULT_ID, instrument, instrumentLabel(instrument),
                            stage, legacyCurriculumId(instrument), LocalProfile.DEFAULT_ID));
            profile.changeCurriculum(legacyCurriculumId(instrument));
            profile.updateStage(stage);
            profile.markPrimary(instrument == primaryInstrument);
            instrumentProfiles.save(profile);
        }
    }

    private void migrateLearningPaths() {
        instrumentProfiles.findByOwnerIdAndActiveTrue(LocalProfile.DEFAULT_ID).forEach(profile -> {
            var ordered = orderedCompetencies(profile.getInstrument());
            if (ordered.isEmpty()) return;
            var steps = ordered.stream()
                    .map(competency -> new LearningPathStep(
                            competency.getId(),
                            LearningPathStep.Kind.CORE,
                            LearningPathStep.Readiness.UNASSESSED,
                            competency.getPrerequisites().isEmpty()
                                    ? "Núcleo inicial preservado do currículo legado."
                                    : "Posicionado após relações pedagógicas legadas; prontidão depende de evidência válida."))
                    .toList();
            var goalIds = learningGoals.findByInstrumentProfileIdAndStatus(
                            profile.getId(), LearningGoal.Status.ACTIVE).stream()
                    .map(LearningGoal::getId)
                    .toList();
            var pathId = "legacy-path-" + profile.getId();
            var title = "Caminho migrado de " + profile.getDisplayName();
            var reason = "Derivado do currículo, instrumento e relações existentes. "
                    + "Todos os passos permanecem não avaliados até existirem evidências admissíveis.";
            learningPaths.findById(pathId).ifPresentOrElse(path -> {
                path.synchronizeDerivedPath(title, "legacy-v2", reason, goalIds, steps);
                learningPaths.save(path);
            }, () -> learningPaths.save(new LearningPath(
                    pathId, profile.getId(), profile.getCurriculumId(), title,
                    "legacy-v2", reason, goalIds, steps)));
        });
    }

    private void migrateContentRelations() {
        var candidates = new LinkedHashMap<String, LearningContentRelation>();
        var competencyIds = competencies.findAll().stream().map(Competency::getId).collect(Collectors.toSet());
        var lessonIds = lessons.findAll().stream().map(Lesson::getId).collect(Collectors.toSet());
        var exerciseIds = exercises.findAll().stream()
                .map(com.musicos.domain.Exercise::getId).collect(Collectors.toSet());
        var songIds = songs.findAll().stream().map(com.musicos.domain.Song::getId).collect(Collectors.toSet());

        skills.findAll().forEach(skill -> {
            skill.getPrerequisites().stream().filter(competencyIds::contains).forEach(prerequisiteId ->
                    addRelation(candidates, relation(
                            LearningContentRelation.ContentType.COMPETENCY, prerequisiteId,
                            LearningContentRelation.RelationType.PREPARES,
                            LearningContentRelation.ContentType.COMPETENCY, skill.getId(),
                            LearningContentRelation.Strength.MODERATE,
                            "Pré-requisito pedagógico preservado da Skill legada.",
                            "A relação orienta sequência, mas não desbloqueia sem evidência.",
                            "skills.prerequisites")));
            skill.getNextSkills().stream().filter(competencyIds::contains).forEach(nextId ->
                    addRelation(candidates, relation(
                            LearningContentRelation.ContentType.COMPETENCY, skill.getId(),
                            LearningContentRelation.RelationType.PREPARES,
                            LearningContentRelation.ContentType.COMPETENCY, nextId,
                            LearningContentRelation.Strength.MODERATE,
                            "Próximo passo indicado pela Skill legada.", null, "skills.next_skills")));
            skill.getContents().stream().filter(lessonIds::contains).forEach(lessonId ->
                    addRelation(candidates, relation(
                            LearningContentRelation.ContentType.LESSON, lessonId,
                            LearningContentRelation.RelationType.PREPARES,
                            LearningContentRelation.ContentType.COMPETENCY, skill.getId(),
                            LearningContentRelation.Strength.STRONG,
                            "Aula associada à competência no catálogo legado.", null, "skills.contents")));
            skill.getExercises().stream().filter(exerciseIds::contains).forEach(exerciseId ->
                    addRelation(candidates, relation(
                            LearningContentRelation.ContentType.EXERCISE, exerciseId,
                            LearningContentRelation.RelationType.PREPARES,
                            LearningContentRelation.ContentType.COMPETENCY, skill.getId(),
                            LearningContentRelation.Strength.STRONG,
                            "Exercício associado à competência no catálogo legado.", null, "skills.exercises")));
            skill.getSongs().stream().filter(songIds::contains).forEach(songId ->
                    addRelation(candidates, relation(
                            LearningContentRelation.ContentType.SONG, songId,
                            LearningContentRelation.RelationType.APPLIES,
                            LearningContentRelation.ContentType.COMPETENCY, skill.getId(),
                            LearningContentRelation.Strength.STRONG,
                            "Repertório associado à aplicação da competência no catálogo legado.",
                            null, "skills.songs")));
        });

        libraryContents.findAll().forEach(content -> {
            if (content.getSkillId() != null && competencyIds.contains(content.getSkillId())) {
                addRelation(candidates, relation(
                        LearningContentRelation.ContentType.LESSON, content.getId(),
                        LearningContentRelation.RelationType.PREPARES,
                        LearningContentRelation.ContentType.COMPETENCY, content.getSkillId(),
                        LearningContentRelation.Strength.STRONG,
                        "Competência principal declarada pelo conteúdo legado.", null,
                        "library_contents.skill_id"));
            }
            content.getRelated().stream().filter(lessonIds::contains).forEach(relatedId ->
                    addRelation(candidates, relation(
                            LearningContentRelation.ContentType.LESSON, content.getId(),
                            LearningContentRelation.RelationType.PREPARES,
                            LearningContentRelation.ContentType.LESSON, relatedId,
                            LearningContentRelation.Strength.WEAK,
                            "Relação de conteúdo preservada da biblioteca legada.",
                            "Semântica específica ainda não classificada.", "library_contents.related")));
        });

        exercises.findAll().forEach(exercise -> exercise.getCompetencyIds().stream()
                .filter(competencyIds::contains).forEach(competencyId ->
                        addRelation(candidates, relation(
                                LearningContentRelation.ContentType.EXERCISE, exercise.getId(),
                                LearningContentRelation.RelationType.PREPARES,
                                LearningContentRelation.ContentType.COMPETENCY, competencyId,
                                LearningContentRelation.Strength.STRONG,
                                "Exercício controlado vinculado à competência migrada.", null,
                                "exercises.skill_id"))));

        songs.findAll().forEach(song -> song.getSections().forEach(section ->
                section.getSkillIds().stream().filter(competencyIds::contains).forEach(competencyId ->
                        addRelation(candidates, relation(
                                LearningContentRelation.ContentType.SONG_SECTION,
                                song.getId() + "#" + section.getSectionId(),
                                LearningContentRelation.RelationType.APPLIES,
                                LearningContentRelation.ContentType.COMPETENCY, competencyId,
                                LearningContentRelation.Strength.STRONG,
                                "Seção de repertório aplica a competência declarada no legado.",
                                "Evidência futura permanece contextualizada ao instrumento e à seção.",
                                "song_sections.skill_ids")))));

        var existingIds = contentRelations.findAll().stream()
                .map(LearningContentRelation::getId).collect(Collectors.toSet());
        contentRelations.saveAll(candidates.values().stream()
                .filter(relation -> !existingIds.contains(relation.getId()))
                .toList());
    }

    private List<Competency> orderedCompetencies(InstrumentId instrument) {
        var candidates = competencies.findAll().stream()
                .filter(competency -> competency.getInstruments().contains(instrument))
                .collect(Collectors.toMap(Competency::getId, competency -> competency));
        var outgoing = new HashMap<String, LinkedHashSet<String>>();
        var indegree = new HashMap<String, Integer>();
        candidates.keySet().forEach(id -> indegree.put(id, 0));
        candidates.values().forEach(competency -> competency.getPrerequisites().forEach(prerequisite -> {
            if (!candidates.containsKey(prerequisite.getCompetencyId())) return;
            if (outgoing.computeIfAbsent(prerequisite.getCompetencyId(), ignored -> new LinkedHashSet<>())
                    .add(competency.getId())) {
                indegree.compute(competency.getId(), (ignored, value) -> value == null ? 1 : value + 1);
            }
        }));

        var comparator = Comparator.comparingInt((String id) -> candidates.get(id).getStage().order())
                .thenComparing(id -> id);
        var ready = new PriorityQueue<>(comparator);
        indegree.forEach((id, degree) -> { if (degree == 0) ready.add(id); });
        var ordered = new ArrayList<Competency>();
        var visited = new LinkedHashSet<String>();
        while (!ready.isEmpty()) {
            var id = ready.remove();
            if (!visited.add(id)) continue;
            ordered.add(candidates.get(id));
            outgoing.getOrDefault(id, new LinkedHashSet<>()).forEach(next -> {
                var degree = indegree.compute(next, (ignored, value) -> value - 1);
                if (degree == 0) ready.add(next);
            });
        }
        candidates.keySet().stream().filter(id -> !visited.contains(id)).sorted(comparator)
                .map(candidates::get).forEach(ordered::add);
        return ordered;
    }

    private LearningContentRelation relation(
            LearningContentRelation.ContentType sourceType, String sourceId,
            LearningContentRelation.RelationType relationType,
            LearningContentRelation.ContentType targetType, String targetId,
            LearningContentRelation.Strength strength, String justification,
            String conditions, String migratedFrom) {
        return new LearningContentRelation(sourceType, sourceId, relationType, targetType, targetId,
                strength, justification, conditions, migratedFrom);
    }

    private void addRelation(Map<String, LearningContentRelation> relations,
                             LearningContentRelation relation) {
        relations.putIfAbsent(relation.getId(), relation);
    }

    private LearningStage parseStage(String value) {
        if (value == null || value.isBlank()) return LearningStage.INTERMEDIATE;
        var normalized = value.trim().toLowerCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
        try {
            return LearningStage.from(normalized);
        } catch (IllegalArgumentException ignored) {
            return switch (normalized) {
                case "iniciante" -> LearningStage.BEGINNER;
                case "avançado", "avancado" -> LearningStage.ADVANCED;
                default -> LearningStage.INTERMEDIATE;
            };
        }
    }

    private String legacyCurriculumId(InstrumentId instrument) {
        return "legacy-" + instrument.value();
    }

    private String instrumentLabel(InstrumentId instrument) {
        return switch (instrument) {
            case GUITAR -> "Guitarra";
            case ACOUSTIC -> "Violão";
            case KEYS -> "Teclado";
            case DRUMS -> "Bateria";
        };
    }
}
