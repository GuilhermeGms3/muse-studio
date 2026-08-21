package com.musicos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.musicos.domain.Assessment;
import com.musicos.domain.DifficultyDemand;
import com.musicos.domain.InstrumentId;
import com.musicos.domain.LearningStage;
import com.musicos.domain.Mission;
import com.musicos.repository.AssessmentRepository;
import com.musicos.repository.CurriculumRepository;
import com.musicos.repository.ExerciseRepository;
import com.musicos.repository.LessonRepository;
import com.musicos.repository.MissionRepository;
import com.musicos.service.LearningWorkspaceService;
import com.musicos.service.LearningHistoryService;
import com.musicos.service.NotFoundException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class LearningWorkspaceIntegrationTest {
    @Autowired
    private LearningWorkspaceService workspace;

    @Autowired
    private LearningHistoryService history;

    @Autowired
    private MissionRepository missions;

    @Autowired
    private AssessmentRepository assessments;

    @Autowired
    private CurriculumRepository curriculums;

    @Autowired
    private LessonRepository lessons;

    @Autowired
    private ExerciseRepository exercises;

    @Test
    void assemblesOnlyTheContentLinkedToTheMission() {
        var exercise = exercises.findByInstrumentOrderByTechniqueAscNameAsc(InstrumentId.GUITAR).getFirst();
        var lesson = lessons.findAll().getFirst();
        var competencyId = exercise.getCompetencyIds().getFirst();
        var assessment = new Assessment(
                "workspace-assessment", "Aplicação observada", "Observar a execução em contexto",
                Assessment.Type.PERFORMANCE, "1", "Execute o trecho proposto.", "Sem metrônomo.",
                "Uma repetição de preparação.", "Repetir em outra sessão se houver falha técnica.",
                5, 2, DifficultyDemand.unspecified(), List.of(competencyId), List.of("execucao-estavel"));
        assessment.activate();
        assessments.save(assessment);

        var curriculum = guitarCurriculum();
        var mission = new Mission(
                "workspace-mission", curriculum.getId(), "Aplicar a competência em música",
                "Executar o trecho nas condições declaradas.", "Aplicação musical guiada.",
                "Transformar estudo isolado em música.", 25, InstrumentId.GUITAR, LearningStage.BEGINNER,
                "Execução estável em duas tentativas.", "Tentativas observáveis e avaliação.",
                "Aplicação em repertório.", null, DifficultyDemand.unspecified(), List.of(competencyId),
                List.of(lesson.getId()), List.of(exercise.getId()), List.of(assessment.getId()));
        mission.activate();
        missions.save(mission);

        var result = workspace.mission(mission.getId(), InstrumentId.GUITAR);

        assertThat(result.mission().title()).isEqualTo(mission.getTitle());
        assertThat(result.lessons()).extracting(item -> item.id()).containsExactly(lesson.getId());
        assertThat(result.exercises()).extracting(item -> item.id()).containsExactly(exercise.getId());
        assertThat(result.assessments()).extracting(item -> item.id()).containsExactly(assessment.getId());
        assertThat(result.evidence()).allSatisfy(item ->
                assertThat(item.competencyId()).isEqualTo(competencyId));
    }

    @Test
    void doesNotExposeAMissionThroughAnotherInstrumentWorkspace() {
        var exercise = exercises.findByInstrumentOrderByTechniqueAscNameAsc(InstrumentId.GUITAR).getFirst();
        var competencyId = exercise.getCompetencyIds().getFirst();
        var curriculum = guitarCurriculum();
        var mission = new Mission(
                "instrument-scoped-mission", curriculum.getId(), "Missão de violão",
                "Executar uma habilidade observável.", "Contexto de violão.", "Prática direcionada.",
                10, InstrumentId.GUITAR, LearningStage.BEGINNER, "Critério observado.",
                "Registro da tentativa.", null, null, DifficultyDemand.unspecified(), List.of(competencyId),
                List.of(), List.of(exercise.getId()), List.of());
        missions.save(mission);

        assertThatThrownBy(() -> workspace.mission(mission.getId(), InstrumentId.KEYS))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("instrumento selecionado");
    }

    @Test
    void exposesTheRealCurriculumNavigationWithLinkedMissionsForEachInstrument() {
        var guitar = workspace.journey(InstrumentId.GUITAR);
        var keys = workspace.journey(InstrumentId.KEYS);

        assertThat(guitar.instrument()).isEqualTo(InstrumentId.GUITAR);
        assertThat(guitar.competencies()).isNotEmpty();
        assertThat(guitar.competencies()).extracting(item -> item.status())
                .allMatch(status -> java.util.Set.of(
                        "BLOCKED", "AVAILABLE", "IN_PROGRESS", "ESTABLISHED", "REVIEW_DUE")
                        .contains(status));
        assertThat(guitar.competencies()).flatExtracting(item -> item.missions())
                .allSatisfy(mission -> assertThat(mission.competencyIds()).isNotEmpty());
        assertThat(keys.instrument()).isEqualTo(InstrumentId.KEYS);
        assertThat(keys.curriculumId()).isNotEqualTo(guitar.curriculumId());
    }

    @Test
    void returnsOnlyRealHistoryWithoutSynthesizingSessions() {
        var items = history.history(InstrumentId.GUITAR);

        assertThat(items).extracting(item -> item.kind()).doesNotContain("SESSION");
        assertThat(items).extracting(item -> item.occurredAt()).isSortedAccordingTo(
                java.util.Comparator.reverseOrder());
    }

    private com.musicos.domain.Curriculum guitarCurriculum() {
        return curriculums.findAll().stream()
                .filter(item -> item.getInstrument() == InstrumentId.GUITAR)
                .findFirst()
                .orElseThrow();
    }
}
