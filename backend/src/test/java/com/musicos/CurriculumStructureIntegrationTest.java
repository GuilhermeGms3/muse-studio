package com.musicos;

import static org.assertj.core.api.Assertions.assertThat;

import com.musicos.config.TeachingContentCatalog;
import com.musicos.domain.InstrumentId;
import com.musicos.domain.Assessment;
import com.musicos.domain.LearningContentRelation;
import com.musicos.domain.LearningStage;
import com.musicos.domain.LearningTrack;
import com.musicos.domain.SkillKind;
import com.musicos.repository.ExerciseRepository;
import com.musicos.repository.AssessmentRepository;
import com.musicos.repository.LearningContentRelationRepository;
import com.musicos.repository.LibraryContentRepository;
import com.musicos.repository.MissionRepository;
import com.musicos.repository.SongRepository;
import com.musicos.repository.SkillRepository;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CurriculumStructureIntegrationTest {
    @Autowired SkillRepository skills;
    @Autowired ExerciseRepository exercises;
    @Autowired MissionRepository missions;
    @Autowired AssessmentRepository assessments;
    @Autowired LibraryContentRepository library;
    @Autowired SongRepository songs;
    @Autowired LearningContentRelationRepository relations;

    @Test
    void everyInstrumentHasTheFullEditorialLadderAndParallelTracks() {
        for (var instrument : InstrumentId.values()) {
            var catalog = skills.findDistinctByInstrumentsContainingOrderByDomainAscTechnicalNameAsc(instrument);
            assertThat(catalog).extracting(skill -> skill.getStage()).containsAll(Arrays.asList(LearningStage.values()));
            assertThat(catalog).extracting(skill -> skill.getTrack()).containsAll(Arrays.asList(LearningTrack.values()));
            assertThat(catalog).extracting(skill -> skill.getKind())
                    .contains(SkillKind.KNOWLEDGE, SkillKind.ABILITY);
        }
    }

    @Test
    void genericActivitiesAreNotGeneratedAndTeachingExercisesHaveObservableDefinitions() {
        assertThat(skills.findAll()).allSatisfy(skill ->
                assertThat(skill.getExercises()).noneMatch(id -> id.startsWith("activity-")));

        assertThat(TeachingContentCatalog.exercises()).allSatisfy(definition -> {
            var exercise = exercises.findById(definition.getId()).orElseThrow();
            assertThat(exercise.getObservableObjective()).isNotBlank();
            assertThat(exercise.getPracticeConditions()).isNotBlank();
            assertThat(exercise.getSuccessCriteria()).isNotBlank();
            assertThat(exercise.getCompetencyIds()).hasSize(1);
        });
    }

    @Test
    void expandedJourneyHasEditorialDepthTraceableContentRepertoireAndRetention() {
        var unitsByInstrument = TeachingContentCatalog.units().stream()
                .collect(Collectors.groupingBy(TeachingContentCatalog.Unit::instrument));

        for (var instrument : InstrumentId.values()) {
            var units = unitsByInstrument.get(instrument);
            assertThat(units).hasSizeGreaterThanOrEqualTo(25);
            assertThat(units).extracting(TeachingContentCatalog.Unit::stage)
                    .containsAll(Arrays.asList(LearningStage.values()));
            assertThat(units.stream()
                    .map(unit -> skills.findById(unit.competencyId()).orElseThrow().getTrack())
                    .collect(Collectors.toSet()))
                    .containsAll(Arrays.asList(LearningTrack.values()));
            assertThat(units).extracting(TeachingContentCatalog.Unit::assessmentType)
                    .contains(Assessment.Type.FORMATIVE, Assessment.Type.RETENTION)
                    .anyMatch(type -> type == Assessment.Type.APPLICATION
                            || type == Assessment.Type.PERFORMANCE);
            assertThat(units.stream().flatMap(unit -> unit.exerciseIds().stream())
                    .map(id -> exercises.findById(id).orElseThrow().getActivityType()).distinct().count())
                    .isGreaterThanOrEqualTo(5);
            assertThat(units.stream().filter(unit -> !unit.repertoireIds().isEmpty()).count())
                    .isGreaterThanOrEqualTo(4);
            assertThat(units).allSatisfy(unit -> {
                assertThat(unit.objective()).isNotBlank();
                assertThat(unit.motivation()).isNotBlank();
                assertThat(unit.musicalApplication()).isNotBlank();
                assertThat(unit.completionCriteria()).isNotBlank();
            });

            units.forEach(unit -> {
                assertThat(missions.findById(unit.id())).get().satisfies(mission -> {
                    assertThat(mission.getStatus()).isEqualTo(com.musicos.domain.Mission.Status.ACTIVE);
                    assertThat(mission.getCompletionCriteria()).isNotBlank();
                    assertThat(mission.getExpectedEvidence()).isNotBlank();
                });
                assertThat(library.findById(unit.lessonId())).get()
                        .satisfies(content -> assertThat(content.getSteps()).hasSizeGreaterThanOrEqualTo(3));
                assertThat(assessments.findById(unit.id() + "-assessment")).get()
                        .satisfies(assessment -> {
                            assertThat(assessment.getType()).isEqualTo(unit.assessmentType());
                            assertThat(assessment.getCriterionKeys()).containsExactlyElementsOf(unit.criterionKeys());
                            assertThat(assessment.getInconclusiveRule()).isNotBlank();
                        });
                unit.exerciseIds().forEach(id -> assertThat(exercises.findById(id)).get()
                        .satisfies(exercise -> {
                            assertThat(exercise.getInstructions()).hasSizeGreaterThanOrEqualTo(3);
                            assertThat(exercise.getVariations()).hasSizeGreaterThanOrEqualTo(2);
                            assertThat(exercise.getObservableObjective()).isNotBlank();
                            assertThat(exercise.getSuccessCriteria()).isNotBlank();
                        }));
                unit.repertoireIds().forEach(id -> assertThat(songs.existsById(id)).isTrue());

                var missionRelations = relations.findBySourceTypeAndSourceId(
                        LearningContentRelation.ContentType.MISSION, unit.id());
                assertThat(missionRelations).anySatisfy(relation -> {
                    assertThat(relation.getRelationType()).isEqualTo(LearningContentRelation.RelationType.COMPOSES);
                    assertThat(relation.getTargetType()).isEqualTo(LearningContentRelation.ContentType.LESSON);
                }).anySatisfy(relation -> {
                    assertThat(relation.getRelationType()).isEqualTo(LearningContentRelation.RelationType.EVIDENCES);
                    assertThat(relation.getTargetType()).isEqualTo(LearningContentRelation.ContentType.ASSESSMENT);
                });
                if (!unit.repertoireIds().isEmpty()) {
                    assertThat(missionRelations).anySatisfy(relation -> {
                        assertThat(relation.getRelationType()).isEqualTo(LearningContentRelation.RelationType.APPLIES);
                        assertThat(relation.getTargetType()).isEqualTo(LearningContentRelation.ContentType.SONG);
                    });
                }
            });
        }
    }
}
