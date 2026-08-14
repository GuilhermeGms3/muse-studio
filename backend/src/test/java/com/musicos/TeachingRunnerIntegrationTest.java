package com.musicos;

import static org.assertj.core.api.Assertions.assertThat;

import com.musicos.config.TeachingContentCatalog;
import com.musicos.repository.ExerciseRepository;
import com.musicos.repository.MissionRepository;
import com.musicos.service.CatalogService;
import com.musicos.service.LearningWorkspaceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TeachingRunnerIntegrationTest {
    @Autowired
    private LearningWorkspaceService workspace;

    @Autowired
    private CatalogService catalog;

    @Autowired
    private MissionRepository missions;

    @Autowired
    private ExerciseRepository exercises;

    @Test
    void seedsCompleteEvidenceGuidedJourneysForEveryInstrument() {
        assertThat(TeachingContentCatalog.units()).isNotEmpty()
                .extracting(TeachingContentCatalog.Unit::id).doesNotHaveDuplicates();

        for (var unit : TeachingContentCatalog.units()) {
            var mission = missions.findById(unit.id()).orElseThrow();
            var result = workspace.mission(unit.id(), unit.instrument());

            assertThat(mission.getStatus()).isEqualTo(com.musicos.domain.Mission.Status.ACTIVE);
            assertThat(result.lessons()).extracting(item -> item.id()).containsExactly(unit.lessonId());
            assertThat(result.exercises()).extracting(item -> item.id()).containsExactlyElementsOf(unit.exerciseIds());
            assertThat(result.assessments()).hasSize(1);
            assertThat(result.competencies()).extracting(item -> item.competencyId())
                    .containsExactly(unit.competencyId());
            assertThat(result.coach()).isNotNull();
            assertThat(result.coach().whyMission()).isNotBlank();
            assertThat(result.coach().nextMessage()).isNotBlank();
        }
    }

    @Test
    void offersOnlyDeepEditorialExercisesAndKeepsTheirObservableContract() {
        for (var unit : TeachingContentCatalog.units()) {
            var offered = catalog.exercises(unit.instrument(), null);

            assertThat(offered).noneMatch(item -> item.id().startsWith("activity-")
                    || item.id().startsWith("practice-"));
            assertThat(offered).extracting(item -> item.id()).containsAll(unit.exerciseIds());
        }

        assertThat(TeachingContentCatalog.exercises())
                .hasSizeGreaterThanOrEqualTo(TeachingContentCatalog.units().size());
        for (var definition : TeachingContentCatalog.exercises()) {
            var persisted = exercises.findById(definition.getId()).orElseThrow();
            assertThat(persisted.getObservableObjective()).isNotBlank();
            assertThat(persisted.getPracticeConditions()).isNotBlank();
            assertThat(persisted.getSuccessCriteria()).isNotBlank();
            assertThat(persisted.getInstructions()).hasSizeGreaterThanOrEqualTo(3);
            assertThat(persisted.getCompetencyIds()).hasSize(1);
        }
    }
}
