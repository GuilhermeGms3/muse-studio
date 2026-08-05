package com.musicos;

import static org.assertj.core.api.Assertions.assertThat;

import com.musicos.config.TeachingContentCatalog;
import com.musicos.domain.InstrumentId;
import com.musicos.domain.LearningStage;
import com.musicos.domain.LearningTrack;
import com.musicos.domain.SkillKind;
import com.musicos.repository.ExerciseRepository;
import com.musicos.repository.SkillRepository;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CurriculumStructureIntegrationTest {
    @Autowired SkillRepository skills;
    @Autowired ExerciseRepository exercises;

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
}
