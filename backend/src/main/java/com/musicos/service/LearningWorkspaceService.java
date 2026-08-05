package com.musicos.service;

import static com.musicos.api.ApiModels.*;

import com.musicos.domain.InstrumentId;
import com.musicos.repository.AssessmentRepository;
import com.musicos.repository.CompetencyRepository;
import com.musicos.repository.EvidenceRepository;
import com.musicos.repository.ExerciseAttemptRepository;
import com.musicos.repository.ExerciseRepository;
import com.musicos.repository.InstrumentProfileRepository;
import com.musicos.repository.LessonRepository;
import com.musicos.repository.LibraryContentRepository;
import com.musicos.repository.MissionRepository;
import java.util.Comparator;
import java.util.HashSet;
import java.time.Instant;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LearningWorkspaceService {
    private final MissionRepository missions;
    private final LessonRepository lessons;
    private final LibraryContentRepository library;
    private final ExerciseRepository exercises;
    private final AssessmentRepository assessments;
    private final ExerciseAttemptRepository attempts;
    private final EvidenceRepository evidence;
    private final InstrumentProfileRepository profiles;
    private final CompetencyRepository competencies;
    private final CurriculumEngine curriculumEngine;
    private final EvidenceEngine evidenceEngine;
    private final Coach coach;

    public LearningWorkspaceService(MissionRepository missions, LessonRepository lessons,
                                    LibraryContentRepository library, ExerciseRepository exercises,
                                    AssessmentRepository assessments, ExerciseAttemptRepository attempts,
                                    EvidenceRepository evidence, InstrumentProfileRepository profiles,
                                    CompetencyRepository competencies, CurriculumEngine curriculumEngine,
                                    EvidenceEngine evidenceEngine, Coach coach) {
        this.missions = missions;
        this.lessons = lessons;
        this.library = library;
        this.exercises = exercises;
        this.assessments = assessments;
        this.attempts = attempts;
        this.evidence = evidence;
        this.profiles = profiles;
        this.competencies = competencies;
        this.curriculumEngine = curriculumEngine;
        this.evidenceEngine = evidenceEngine;
        this.coach = coach;
    }

    public MissionWorkspaceView mission(String id, InstrumentId instrument) {
        var mission = missions.findById(id)
                .orElseThrow(() -> new NotFoundException("Missão não encontrada"));
        if (mission.getInstrument() != null && mission.getInstrument() != instrument) {
            throw new NotFoundException("Missão não pertence ao instrumento selecionado");
        }
        var profile = profiles.findByOwnerIdAndInstrument("default", instrument)
                .orElseThrow(() -> new NotFoundException("Perfil instrumental não encontrado"));

        var lessonViews = mission.getLessonIds().stream().map(lessonId -> {
            var lesson = lessons.findById(lessonId)
                    .orElseThrow(() -> new NotFoundException("Aula vinculada não encontrada: " + lessonId));
            var material = lesson.getLegacyLibraryContentId() == null ? null
                    : library.findById(lesson.getLegacyLibraryContentId()).map(ViewMapper::library).orElse(null);
            return new MissionLessonView(lesson.getId(), lesson.getTitle(), lesson.getTechnicalName(),
                    lesson.getCategory(), lesson.getSummary(), lesson.getContent(), lesson.getEstimatedMinutes(),
                    lesson.getStage(), lesson.getFormat().name(), lesson.getCompetencyIds(),
                    lesson.getObjectives(), lesson.getExamples(), material);
        }).toList();
        var exerciseEntities = mission.getExerciseIds().stream().map(exerciseId -> exercises.findById(exerciseId)
                .orElseThrow(() -> new NotFoundException(
                        "Exercício vinculado não encontrado: " + exerciseId))).toList();
        var assessmentViews = mission.getAssessmentIds().stream().map(assessmentId -> {
            var assessment = assessments.findById(assessmentId)
                    .orElseThrow(() -> new NotFoundException(
                            "Avaliação vinculada não encontrada: " + assessmentId));
            return new MissionAssessmentView(assessment.getId(), assessment.getTitle(), assessment.getPurpose(),
                    assessment.getType().name(), assessment.getProtocolVersion(), assessment.getInstructions(),
                    assessment.getConditions(), assessment.getAllowedSupport(), assessment.getInconclusiveRule(),
                    assessment.getEstimatedMinutes(), assessment.getMaximumAttempts(), assessment.isActive(),
                    assessment.getCompetencyIds(), assessment.getCriterionKeys());
        }).toList();
        var feedback = exerciseEntities.stream()
                .flatMap(exercise -> attempts.findTop20ByExerciseIdOrderByPracticedAtDesc(exercise.getId()).stream())
                .sorted(Comparator.comparing(com.musicos.domain.ExerciseAttempt::getPracticedAt).reversed())
                .limit(20).map(ViewMapper::exerciseAttempt).toList();
        var competencyIds = new HashSet<>(mission.getCompetencyIds());
        var evidenceViews = evidence.findByInstrumentProfileIdOrderByOccurredAtDesc(profile.getId()).stream()
                .filter(item -> competencyIds.contains(item.getCompetencyId()))
                .limit(50)
                .map(item -> new LearningEvidenceView(item.getId(), item.getCompetencyId(),
                        item.getCriterionKey(), item.getType().name(), item.getState().name(),
                        item.getReliability().name(), item.getResult().name(), item.getSourceType().name(),
                        item.getSourceId(), item.getChallengeLevel(), item.getObservation(), item.getConditions(),
                        item.getOccurredAt(), item.getValidUntil()))
                .toList();
        var summary = new MissionSummaryView(mission.getId(), mission.getCurriculumId(), mission.getTitle(),
                mission.getObservableObjective(), mission.getContext(), mission.getMotivation(),
                mission.getEstimatedMinutes(), mission.getInstrument(), mission.getStage(),
                mission.getStatus().name(), mission.getCompletionCriteria(), mission.getExpectedEvidence(),
                mission.getMusicalApplication(), mission.getEasierMissionId(), mission.getCompetencyIds());
        var evaluatedAt = Instant.now();
        var competencyMap = competencies.findAllById(mission.getCompetencyIds()).stream()
                .collect(Collectors.toMap(com.musicos.domain.Competency::getId, Function.identity()));
        var navigation = curriculumEngine.navigate(profile.getId(), evaluatedAt, 20);
        var navigationByCompetency = navigation.competencies().stream().collect(Collectors.toMap(
                CurriculumEngine.CompetencyView::competencyId, Function.identity()));
        var competencyViews = mission.getCompetencyIds().stream().map(competencyId -> {
            var competency = competencyMap.get(competencyId);
            if (competency == null) throw new NotFoundException("Competência vinculada não encontrada: " + competencyId);
            var position = navigationByCompetency.get(competencyId);
            if (position == null) {
                throw new IllegalStateException("Competência da Mission não pertence ao caminho ativo: " + competencyId);
            }
            var report = evidenceEngine.inspect(profile.getId(), competencyId, evaluatedAt);
            return new MissionCompetencyView(competencyId, competency.getFriendlyTitle(),
                    competency.getObservableAction(), competency.getObservationConditions(),
                    position.status().name(), position.masteryState().name(), position.unlocked(), position.reason(),
                    report.confidence().name(), report.missingMandatoryCriteria(), report.nextObservation());
        }).toList();
        var prerequisiteIds = new java.util.LinkedHashSet<String>();
        var prerequisiteViews = mission.getCompetencyIds().stream()
                .flatMap(competencyId -> curriculumEngine.findPrerequisites(
                        profile.getId(), competencyId, evaluatedAt).stream())
                .filter(item -> prerequisiteIds.add(item.competencyId()))
                .map(item -> new MissionPrerequisiteView(item.competencyId(),
                        competencies.findById(item.competencyId())
                                .map(com.musicos.domain.Competency::getFriendlyTitle)
                                .orElse(item.competencyId()), item.type().name(), item.depth(), item.direct(),
                        item.satisfied(), item.blocking(), item.reason()))
                .toList();
        var coachView = coachView(mission, profile, evaluatedAt);
        return new MissionWorkspaceView(summary, lessonViews,
                exerciseEntities.stream().map(ViewMapper::exercise).toList(), assessmentViews,
                feedback, evidenceViews, prerequisiteViews, competencyViews, coachView);
    }

    private MissionCoachView coachView(com.musicos.domain.Mission mission,
                                       com.musicos.domain.InstrumentProfile profile,
                                       Instant evaluatedAt) {
        if (mission.getStatus() != com.musicos.domain.Mission.Status.ACTIVE
                || !mission.getCurriculumId().equals(profile.getCurriculumId())) {
            return new MissionCoachView("NOT_CURRENT_RECOMMENDATION",
                    "Esta Mission pode ser explorada, mas não é uma recomendação ativa do currículo deste perfil.",
                    List.of(), "NOT_EVALUATED",
                    "Os próximos passos só podem ser calculados para uma Mission ativa do caminho atual.", List.of());
        }
        var why = coach.whyDidIReceiveMission(profile.getId(), mission.getId(), evaluatedAt);
        var today = coach.whatShouldIDoToday(profile.getId(), null, evaluatedAt, 5);
        var recommendation = why.recommendation();
        var citedEvidence = recommendation == null ? List.<CoachEvidenceView>of()
                : recommendation.evidence().stream().map(this::coachEvidence).toList();
        var next = today.recommendations().stream()
                .filter(item -> item.missionId() != null)
                .filter(item -> !mission.getId().equals(item.missionId()))
                .map(this::coachRecommendation)
                .toList();
        var whyText = recommendation == null ? why.message() : recommendation.explanation();
        var nextMessage = next.isEmpty()
                ? "O Coach aguardará as evidências desta Mission antes de afirmar o próximo passo. " + today.message()
                : today.message();
        return new MissionCoachView(why.status().name(), whyText, citedEvidence,
                today.status().name(), nextMessage, next);
    }

    private CoachRecommendationView coachRecommendation(Coach.Recommendation value) {
        return new CoachRecommendationView(value.missionId(), value.title(), value.competencyId(),
                value.kind().name(), value.estimatedMinutes(), value.observableObjective(),
                value.expectedEvidence(), value.goals().stream().map(goal -> new CoachGoalView(
                        goal.goalId(), goal.title(), goal.desiredOutcome(), goal.musicalContext(),
                        goal.type().name(), goal.declaredPriority(), goal.targetDate())).toList(),
                value.evidence().stream().map(this::coachEvidence).toList(), value.explanation());
    }

    private CoachEvidenceView coachEvidence(Coach.EvidenceCitation value) {
        return new CoachEvidenceView(value.evidenceId(), value.competencyId(), value.criterionKey(),
                value.reliability().name(), value.result().name(), value.occurredAt(),
                value.observation(), value.conditions());
    }
}
