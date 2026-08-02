package com.musicos;

import static org.assertj.core.api.Assertions.assertThat;

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
    void everySkillHasSixVariedActivitiesAndOpenLearningResources() {
        skills.findAll().forEach(skill -> {
            var activities = skill.getExercises().stream()
                    .filter(id -> id.startsWith("activity-"))
                    .map(exercises::findById)
                    .flatMap(java.util.Optional::stream)
                    .toList();
            assertThat(activities).hasSize(6 * skill.getInstruments().size());
            for (var instrument : skill.getInstruments()) {
                var instrumentActivities = activities.stream()
                        .filter(activity -> activity.getInstrument() == instrument).toList();
                assertThat(instrumentActivities).hasSize(6);
                assertThat(instrumentActivities).extracting(activity -> activity.getActivityType())
                        .doesNotHaveDuplicates();
            }
            assertThat(activities).allSatisfy(activity -> {
                assertThat(activity.getStage()).isEqualTo(skill.getStage());
                assertThat(activity.getVideoQuery()).isNotBlank();
                assertThat(activity.getReadingUrl()).startsWith("https://");
            });
        });
    }
}
