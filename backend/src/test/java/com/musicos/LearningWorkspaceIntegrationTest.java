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

    private com.musicos.domain.Curriculum guitarCurriculum() {
        return curriculums.findAll().stream()
                .filter(item -> item.getInstrument() == InstrumentId.GUITAR)
                .findFirst()
                .orElseThrow();
    }
}
