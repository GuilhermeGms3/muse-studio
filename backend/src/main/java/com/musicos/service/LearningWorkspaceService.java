package com.musicos.service;

import static com.musicos.api.ApiModels.*;

import com.musicos.domain.InstrumentId;
import com.musicos.repository.AssessmentRepository;
import com.musicos.repository.EvidenceRepository;
import com.musicos.repository.ExerciseAttemptRepository;
import com.musicos.repository.ExerciseRepository;
import com.musicos.repository.InstrumentProfileRepository;
import com.musicos.repository.LessonRepository;
import com.musicos.repository.LibraryContentRepository;
import com.musicos.repository.MissionRepository;
import java.util.Comparator;
import java.util.HashSet;
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

    public LearningWorkspaceService(MissionRepository missions, LessonRepository lessons,
                                    LibraryContentRepository library, ExerciseRepository exercises,
                                    AssessmentRepository assessments, ExerciseAttemptRepository attempts,
                                    EvidenceRepository evidence, InstrumentProfileRepository profiles) {
        this.missions = missions;
        this.lessons = lessons;
        this.library = library;
        this.exercises = exercises;
        this.assessments = assessments;
        this.attempts = attempts;
        this.evidence = evidence;
        this.profiles = profiles;
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
        return new MissionWorkspaceView(summary, lessonViews,
                exerciseEntities.stream().map(ViewMapper::exercise).toList(), assessmentViews,
                feedback, evidenceViews);
    }
}
