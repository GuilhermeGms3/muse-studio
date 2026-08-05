package com.musicos;

import static org.assertj.core.api.Assertions.assertThat;

import com.musicos.domain.InstrumentId;
import com.musicos.domain.LearningContentRelation;
import com.musicos.domain.LearningPathStep;
import com.musicos.repository.CompetencyRepository;
import com.musicos.repository.CurriculumRepository;
import com.musicos.repository.EvidenceRepository;
import com.musicos.repository.ExerciseRepository;
import com.musicos.repository.InstrumentProfileRepository;
import com.musicos.repository.LessonRepository;
import com.musicos.repository.LearningContentRelationRepository;
import com.musicos.repository.LearningPathRepository;
import com.musicos.repository.LibraryContentRepository;
import com.musicos.repository.MasteryRepository;
import com.musicos.repository.SkillRepository;
import com.musicos.service.PedagogicalDomainMigration;
import com.musicos.service.CurriculumEngine;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PedagogicalDomainIntegrationTest {
    @Autowired CompetencyRepository competencies;
    @Autowired SkillRepository skills;
    @Autowired LessonRepository lessons;
    @Autowired LibraryContentRepository libraryContents;
    @Autowired ExerciseRepository exercises;
    @Autowired InstrumentProfileRepository profiles;
    @Autowired CurriculumRepository curricula;
    @Autowired EvidenceRepository evidence;
    @Autowired MasteryRepository mastery;
    @Autowired LearningPathRepository learningPaths;
    @Autowired LearningContentRelationRepository contentRelations;
    @Autowired PedagogicalDomainMigration migration;
    @Autowired CurriculumEngine curriculumEngine;
    @Autowired JdbcTemplate jdbc;

    @Test
    void migratesLegacyDefinitionsWithoutInventingMastery() {
        assertThat(competencies.count()).isEqualTo(skills.count());
        assertThat(lessons.count()).isEqualTo(libraryContents.count());
        assertThat(exercises.findAll())
                .filteredOn(exercise -> exercise.getSkillId() != null)
                .allSatisfy(exercise -> assertThat(exercise.getCompetencyIds()).contains(exercise.getSkillId()));

        assertThat(profiles.findByOwnerIdAndActiveTrue("default"))
                .hasSize(InstrumentId.values().length)
                .extracting(profile -> profile.getInstrument())
                .containsExactlyInAnyOrder(InstrumentId.values());
        assertThat(profiles.findByOwnerIdAndPrimaryProfileTrue("default"))
                .get()
                .extracting(profile -> profile.getInstrument())
                .isEqualTo(InstrumentId.GUITAR);

        assertThat(curricula.count()).isEqualTo(InstrumentId.values().length);
        assertThat(curricula.findById("legacy-guitar")).isPresent();
        assertThat(learningPaths.findAll())
                .hasSize(InstrumentId.values().length)
                .allSatisfy(path -> {
                    assertThat(path.getSteps()).isNotEmpty();
                    assertThat(path.getSteps())
                            .allSatisfy(step -> assertThat(step.getReadiness())
                                    .isEqualTo(LearningPathStep.Readiness.UNASSESSED));
                    assertThat(profiles.findById(path.getInstrumentProfileId())).isPresent();
                    assertThat(curricula.findById(path.getCurriculumId()))
                            .get()
                            .extracting(curriculum -> curriculum.getCompetencyIds())
                            .isEqualTo(path.getSteps().stream()
                                    .map(LearningPathStep::getCompetencyId)
                                    .toList());
                });

        assertThat(contentRelations.findBySourceTypeAndSourceId(
                LearningContentRelation.ContentType.COMPETENCY, "rhythm"))
                .anySatisfy(relation -> {
                    assertThat(relation.getRelationType())
                            .isEqualTo(LearningContentRelation.RelationType.PREPARES);
                    assertThat(relation.getTargetType())
                            .isEqualTo(LearningContentRelation.ContentType.COMPETENCY);
                    assertThat(relation.getTargetId()).isEqualTo("alternate-picking");
                });
        assertThat(contentRelations.findBySourceTypeAndSourceId(
                LearningContentRelation.ContentType.EXERCISE, "ex1"))
                .anySatisfy(relation -> assertThat(relation.getTargetId()).isEqualTo("alternate-picking"));

        assertThat(evidence.count()).isZero();
        assertThat(mastery.count()).isZero();
    }

    @Test
    void appliesVersionedSchemaMigration() {
        var version = jdbc.queryForObject(
                "select \"version\" from \"flyway_schema_history\" where \"success\" = true "
                        + "order by \"installed_rank\" desc limit 1",
                String.class);
        assertThat(version).isEqualTo("2");
    }

    @Test
    void migrationIsIdempotent() {
        var competenciesBefore = competencies.count();
        var curriculaBefore = curricula.count();
        var profilesBefore = profiles.count();
        var pathsBefore = learningPaths.count();
        var relationsBefore = contentRelations.count();

        migration.migrateExistingDomain();

        assertThat(competencies.count()).isEqualTo(competenciesBefore);
        assertThat(curricula.count()).isEqualTo(curriculaBefore);
        assertThat(profiles.count()).isEqualTo(profilesBefore);
        assertThat(learningPaths.count()).isEqualTo(pathsBefore);
        assertThat(contentRelations.count()).isEqualTo(relationsBefore);
    }

    @Test
    void curriculumEngineNavigatesMigratedPathWithoutInventingProgress() {
        var profile = profiles.findByOwnerIdAndInstrument("default", InstrumentId.GUITAR).orElseThrow();

        var navigation = curriculumEngine.navigate(profile.getId());

        assertThat(navigation.position().totalCompetencies()).isPositive();
        assertThat(navigation.position().establishedCompetencies()).isZero();
        assertThat(navigation.position().inProgressCompetencies()).isZero();
        assertThat(navigation.nextSteps()).isNotEmpty()
                .allSatisfy(step -> assertThat(step.kind())
                        .isEqualTo(CurriculumEngine.SuggestionKind.INTRODUCTION));
        assertThat(curriculumEngine.findPrerequisites(profile.getId(), "alternate-picking"))
                .anySatisfy(prerequisite -> {
                    assertThat(prerequisite.competencyId()).isEqualTo("rhythm");
                    assertThat(prerequisite.blocking()).isFalse();
                });
    }
}
