package com.musicos;

import static org.assertj.core.api.Assertions.assertThat;

import com.musicos.config.TeachingContentCatalog;
import com.musicos.domain.InstrumentId;
import com.musicos.domain.LearningStage;
import com.musicos.domain.LearningTrack;
import com.musicos.domain.Skill;
import com.musicos.repository.AssessmentRepository;
import com.musicos.repository.ExerciseRepository;
import com.musicos.repository.LibraryContentRepository;
import com.musicos.repository.MissionRepository;
import com.musicos.repository.SkillRepository;
import com.musicos.repository.SongRepository;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class EditorialCatalogIntegrityIntegrationTest {
    @Autowired SkillRepository skills;
    @Autowired ExerciseRepository exercises;
    @Autowired MissionRepository missions;
    @Autowired AssessmentRepository assessments;
    @Autowired LibraryContentRepository lessons;
    @Autowired SongRepository songs;

    @Test
    void catalogHasUniqueIdsAndNoOrphanOrCrossInstrumentReferences() {
        var units = TeachingContentCatalog.units();
        var exerciseDefinitions = TeachingContentCatalog.exercises();

        assertThat(units).extracting(TeachingContentCatalog.Unit::id).doesNotHaveDuplicates();
        assertThat(exerciseDefinitions).extracting(exercise -> exercise.getId()).doesNotHaveDuplicates();
        assertThat(units).extracting(unit -> unit.id() + "-assessment").doesNotHaveDuplicates();
        assertThat(units).extracting(TeachingContentCatalog.Unit::objective).doesNotHaveDuplicates();

        var exercisesById = exerciseDefinitions.stream().collect(Collectors.toMap(
                exercise -> exercise.getId(), Function.identity()));
        units.forEach(unit -> {
            var competency = skills.findById(unit.competencyId()).orElseThrow();
            assertThat(competency.getInstruments()).contains(unit.instrument());
            assertThat(missions.existsById(unit.id())).isTrue();
            assertThat(lessons.existsById(unit.lessonId())).isTrue();
            assertThat(assessments.existsById(unit.id() + "-assessment")).isEqualTo(unit.formalAssessment());
            if (unit.musicalApplication() != null) {
                assertThat(unit.musicalApplication()).isNotBlank();
                assertThat(unit.exerciseIds()).anySatisfy(id -> assertThat(exercisesById.get(id).getActivityType())
                        .isIn("context", "song", "play_along", "record", "transfer", "create", "review"));
            }
            assertThat(unit.criterionKeys()).isNotEmpty().doesNotHaveDuplicates();

            unit.exerciseIds().forEach(exerciseId -> {
                assertThat(exercisesById).containsKey(exerciseId);
                var exercise = exercises.findById(exerciseId).orElseThrow();
                assertThat(exercise.getInstrument()).isEqualTo(unit.instrument());
                assertThat(exercise.getCompetencyIds()).contains(unit.competencyId());
                assertThat(exercise.getInstructions()).hasSizeGreaterThanOrEqualTo(3)
                        .allSatisfy(instruction -> {
                            assertThat(instruction.trim().length()).isGreaterThanOrEqualTo(12);
                            assertThat(instruction.trim().toLowerCase()).isNotIn(
                                    "pratique por 10 minutos", "repita 10 vezes", "toque lentamente",
                                    "use o metrônomo", "tente novamente", "pratique até ficar confortável");
                        });
                assertThat(exercise.getVariations()).hasSizeGreaterThanOrEqualTo(2);
            });
            unit.repertoireIds().forEach(songId -> assertThat(songs.findById(songId)).get()
                    .satisfies(song -> assertThat(song.getInstrument()).isEqualTo(unit.instrument())));
        });
    }

    @Test
    void progressionHasNoStrictOrderingCycleAndAdvancedMissionsHavePreparedCompetencies() {
        var allSkills = skills.findAll().stream().collect(Collectors.toMap(
                Skill::getId, Function.identity(), (first, ignored) -> first, LinkedHashMap::new));
        var visited = new HashSet<String>();
        var active = new ArrayDeque<String>();
        allSkills.keySet().forEach(id -> visit(id, allSkills, visited, active));

        TeachingContentCatalog.units().stream()
                .filter(unit -> unit.stage().order() >= LearningStage.UPPER_INTERMEDIATE.order())
                .forEach(unit -> assertThat(allSkills.get(unit.competencyId()).getPrerequisites())
                        .as("pré-requisitos de %s", unit.id()).isNotEmpty());

        TeachingContentCatalog.units().stream().filter(unit -> unit.id().contains("-review"))
                .forEach(review -> assertThat(TeachingContentCatalog.units()).anySatisfy(previous -> {
                    assertThat(previous.id()).doesNotContain("-review");
                    assertThat(previous.competencyId()).isEqualTo(review.competencyId());
                    assertThat(previous.instrument()).isEqualTo(review.instrument());
                }));
    }

    @Test
    void coverageReportIncludesAllRealTracksAndStagesWithoutTurningCountsIntoPedagogy() {
        var editorialUnits = TeachingContentCatalog.units();
        System.out.printf("EDITORIAL_TOTAL missions=%d lessons=%d exercises=%d assessments=%d applications=%d rubricAssessments=%d rubricLevels=%d repertoireMissions=%d repertoireLinks=%d%n",
                editorialUnits.size(), editorialUnits.stream().map(TeachingContentCatalog.Unit::lessonId).distinct().count(),
                TeachingContentCatalog.exercises().size(),
                editorialUnits.stream().filter(TeachingContentCatalog.Unit::formalAssessment).count(),
                editorialUnits.stream().filter(unit -> unit.musicalApplication() != null).count(),
                editorialUnits.stream().filter(unit -> !unit.rubricLevels().isEmpty()).count(),
                editorialUnits.stream().mapToLong(unit -> unit.rubricLevels().size()).sum(),
                editorialUnits.stream().filter(unit -> !unit.repertoireIds().isEmpty()).count(),
                editorialUnits.stream().mapToLong(unit -> unit.repertoireIds().size()).sum());
        var unitsByInstrument = editorialUnits.stream()
                .collect(Collectors.groupingBy(TeachingContentCatalog.Unit::instrument));

        for (var instrument : InstrumentId.values()) {
            var instrumentSkills = skills.findDistinctByInstrumentsContainingOrderByDomainAscTechnicalNameAsc(instrument);
            var units = unitsByInstrument.getOrDefault(instrument, List.of());
            var coveredSkills = units.stream().map(TeachingContentCatalog.Unit::competencyId)
                    .collect(Collectors.toSet());
            var tracks = units.stream().map(unit -> skills.findById(unit.competencyId()).orElseThrow().getTrack())
                    .collect(Collectors.toSet());
            var stages = units.stream().map(TeachingContentCatalog.Unit::stage).collect(Collectors.toSet());
            var skillsWithoutMission = instrumentSkills.stream().map(Skill::getId)
                    .filter(id -> !coveredSkills.contains(id)).toList();
            var skillsWithoutRepertoireApplication = coveredSkills.stream()
                    .filter(id -> units.stream().filter(unit -> unit.competencyId().equals(id))
                            .noneMatch(unit -> !unit.repertoireIds().isEmpty())).sorted().toList();

            assertThat(tracks).containsAll(Arrays.asList(LearningTrack.values()));
            assertThat(stages).containsAll(Arrays.asList(LearningStage.values()));
            assertThat(units).hasSizeGreaterThanOrEqualTo(25);

            System.out.printf("CURRICULUM_COVERAGE instrument=%s missions=%d lessons=%d exercises=%d assessments=%d applications=%d tracks=%s stages=%s skillsWithoutMission=%s skillsWithoutRepertoire=%s%n",
                    instrument.value(), units.size(), units.stream().map(TeachingContentCatalog.Unit::lessonId).distinct().count(),
                    units.stream().mapToLong(unit -> unit.exerciseIds().size()).sum(),
                    units.stream().filter(TeachingContentCatalog.Unit::formalAssessment).count(),
                    units.stream().filter(unit -> unit.musicalApplication() != null).count(), tracks, stages,
                    skillsWithoutMission, skillsWithoutRepertoireApplication);
        }
    }

    @Test
    void currentMissionsUseSpecificMaterialAndDoNotHideTheOldTemplate() {
        var units = TeachingContentCatalog.units();
        assertThat(units).anyMatch(unit -> !unit.formalAssessment());
        assertThat(units).anyMatch(unit -> unit.musicalApplication() == null);

        var distinctProtocols = units.stream().filter(TeachingContentCatalog.Unit::formalAssessment)
                .map(TeachingContentCatalog.Unit::assessmentInstructions).distinct().count();
        assertThat(distinctProtocols).isGreaterThanOrEqualTo(5);

        units.forEach(unit -> assertThat(lessons.findById(unit.lessonId())).get().satisfies(lesson -> {
            assertThat(lesson.getSteps()).hasSizeGreaterThanOrEqualTo(3);
            assertThat(lesson.getSkillId()).isEqualTo(unit.competencyId());
            assertThat(lesson.getSteps()).allSatisfy(step -> {
                assertThat(step.getExplanation()).hasSizeGreaterThan(50);
                assertThat(step.getAudioNotes() != null || step.getNotation() != null
                        || step.getTablature() != null).isTrue();
            });
            assertThat(lesson.getSteps()).allSatisfy(step -> assertThat(step.getExplanation())
                    .doesNotContain("Ouça antes de tocar", "Isole o menor movimento", "Leve para a música"));
        }));
    }

    @Test
    void mandatoryReferenceMissionsHaveOriginalExecutableMaterialAndQualitativeRubrics() {
        var referenceIds = List.of(
                "mission-guitar-tuning-reference", "mission-guitar-string-control", "mission-guitar-lead-form",
                "mission-acoustic-quarter-strum", "mission-acoustic-arpeggio-voice", "mission-acoustic-solo-arrangement",
                "mission-keys-triad-balance", "mission-keys-syncopation", "mission-keys-reharmonization",
                "mission-drums-kick-variation", "mission-drums-harmonic-landmarks", "mission-drums-groove-composition");
        referenceIds.forEach(id -> {
            var unit = TeachingContentCatalog.units().stream().filter(candidate -> candidate.id().equals(id))
                    .findFirst().orElseThrow();
            assertThat(lessons.findById(unit.lessonId())).get().satisfies(lesson -> {
                assertThat(lesson.getSteps()).hasSize(3);
                assertThat(lesson.getSteps()).allSatisfy(step -> assertThat(step.getAudioNotes()).isNotBlank());
                assertThat(lesson.getBody()).anyMatch(text -> text.contains("Condição de estudo"));
            });
            if (unit.formalAssessment() && !unit.rubricLevels().isEmpty()) {
                assertThat(assessments.findById(id + "-assessment")).get()
                        .satisfies(value -> assertThat(value.getRubricLevels()).hasSizeGreaterThanOrEqualTo(3));
            }
        });
    }

    @Test
    void autonomySampleCoversTwentyFourMissionsAcrossInstrumentsAndDepths() {
        for (var instrument : InstrumentId.values()) {
            var instrumentUnits = TeachingContentCatalog.units().stream()
                    .filter(unit -> unit.instrument() == instrument).toList();
            assertAutonomous(instrumentUnits.stream().filter(unit -> unit.stage().order() <= LearningStage.BEGINNER.order()).limit(2).toList());
            assertAutonomous(instrumentUnits.stream().filter(unit -> unit.stage().order() >= LearningStage.BEGINNER_ADVANCED.order()
                    && unit.stage().order() <= LearningStage.INTERMEDIATE.order()).limit(2).toList());
            assertAutonomous(instrumentUnits.stream().filter(unit -> unit.stage().order() >= LearningStage.UPPER_INTERMEDIATE.order()).limit(2).toList());
        }
    }

    @Test
    void fourInstrumentJourneysDoNotDependOnTheKnownUnteachableShortcuts() {
        var journeys = List.of(
                List.of("mission-guitar-alternate-picking", "mission-guitar-string-control", "mission-guitar-lead-form"),
                List.of("mission-acoustic-open-chords", "mission-acoustic-arpeggio-voice", "mission-acoustic-solo-arrangement"),
                List.of("mission-keys-triad-balance", "mission-keys-pop-voicings", "mission-keys-reharmonization"),
                List.of("mission-drums-rock-beat", "mission-drums-triplet-control", "mission-drums-groove-composition"));
        var forbiddenShortcuts = Set.of("arpeggio-improv", "common-progressions", "chord-melody",
                "reharmonization", "drum-song-form", "drum-dynamics");

        journeys.forEach(ids -> {
            var journeyUnits = ids.stream().map(id -> TeachingContentCatalog.units().stream()
                    .filter(unit -> unit.id().equals(id)).findFirst().orElseThrow()).toList();
            assertThat(journeyUnits).isSortedAccordingTo(java.util.Comparator.comparingInt(unit -> unit.stage().order()));
            journeyUnits.forEach(unit -> {
                assertThat(skills.findById(unit.competencyId())).get().satisfies(skill ->
                        assertThat(skill.getPrerequisites()).doesNotContainAnyElementsOf(forbiddenShortcuts));
                assertThat(lessons.findById(unit.lessonId())).get().satisfies(lesson ->
                        assertThat(lesson.getSteps()).hasSizeGreaterThanOrEqualTo(3));
            });
        });
    }

    private void assertAutonomous(List<TeachingContentCatalog.Unit> sample) {
        assertThat(sample).hasSize(2);
        sample.forEach(unit -> assertThat(lessons.findById(unit.lessonId())).get().satisfies(lesson -> {
            assertThat(lesson.getSkillId()).isEqualTo(unit.competencyId());
            assertThat(lesson.getExamples()).isNotEmpty();
            assertThat(lesson.getSteps()).hasSizeGreaterThanOrEqualTo(3)
                    .allSatisfy(step -> assertThat(step.getExplanation()).hasSizeGreaterThan(50));
        }));
    }

    private static void visit(String id, Map<String, Skill> skills, Set<String> visited, ArrayDeque<String> active) {
        if (visited.contains(id)) return;
        assertThat(active).as("ciclo curricular envolvendo %s", id).doesNotContain(id);
        active.push(id);
        var skill = skills.get(id);
        if (skill != null) {
            skill.getPrerequisites().stream().filter(skills::containsKey)
                    .forEach(prerequisite -> visit(prerequisite, skills, visited, active));
        }
        active.pop();
        visited.add(id);
    }
}
