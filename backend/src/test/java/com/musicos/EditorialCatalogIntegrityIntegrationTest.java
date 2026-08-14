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
            assertThat(assessments.existsById(unit.id() + "-assessment")).isTrue();
            assertThat(unit.musicalApplication()).isNotBlank();
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
        System.out.printf("EDITORIAL_TOTAL missions=%d lessons=%d exercises=%d assessments=%d applications=%d repertoireMissions=%d repertoireLinks=%d%n",
                editorialUnits.size(), editorialUnits.stream().map(TeachingContentCatalog.Unit::lessonId).distinct().count(),
                TeachingContentCatalog.exercises().size(), editorialUnits.size(),
                editorialUnits.stream().filter(unit -> !unit.musicalApplication().isBlank()).count(),
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
                    units.stream().mapToLong(unit -> unit.exerciseIds().size()).sum(), units.size(),
                    units.stream().filter(unit -> !unit.musicalApplication().isBlank()).count(), tracks, stages,
                    skillsWithoutMission, skillsWithoutRepertoireApplication);
        }
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
