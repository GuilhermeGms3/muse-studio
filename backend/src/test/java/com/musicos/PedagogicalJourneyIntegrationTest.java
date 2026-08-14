package com.musicos;

import static com.musicos.api.ApiModels.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.musicos.domain.AssessmentAttempt;
import com.musicos.domain.Evidence;
import com.musicos.domain.InstrumentId;
import com.musicos.domain.LearningStage;
import com.musicos.domain.Mastery;
import com.musicos.domain.PracticeRecording;
import com.musicos.repository.AssessmentRepository;
import com.musicos.repository.EvidenceRepository;
import com.musicos.repository.InstrumentProfileRepository;
import com.musicos.repository.MasteryRepository;
import com.musicos.repository.MissionRepository;
import com.musicos.repository.PracticeRecordingRepository;
import com.musicos.service.AssessmentService;
import com.musicos.service.Coach;
import com.musicos.service.DiagnosticService;
import com.musicos.service.LearningContentService;
import com.musicos.service.LearningWorkspaceService;
import com.musicos.service.MissionExperienceService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PedagogicalJourneyIntegrationTest {
    @Autowired DiagnosticService diagnostic;
    @Autowired Coach coach;
    @Autowired MissionExperienceService experiences;
    @Autowired LearningContentService learning;
    @Autowired LearningWorkspaceService workspace;
    @Autowired AssessmentService assessmentService;
    @Autowired AssessmentRepository assessments;
    @Autowired MissionRepository missions;
    @Autowired PracticeRecordingRepository recordings;
    @Autowired EvidenceRepository evidence;
    @Autowired MasteryRepository mastery;
    @Autowired InstrumentProfileRepository profiles;

    @Test
    void guitarJourneyConnectsDiagnosticMissionEvidenceApplicationAndReview() {
        diagnostic.complete(new DiagnosticRequest(InstrumentId.GUITAR, "beginner", 35,
                List.of("Rock"), List.of("The White Stripes"), List.of("Seven Nation Army"),
                42, 58, 55));
        var profile = profiles.findByOwnerIdAndInstrument("default", InstrumentId.GUITAR).orElseThrow();

        var initial = coach.whatShouldIDoToday(profile.getId(), 40, Instant.now(), 10);
        assertThat(initial.status()).isEqualTo(Coach.AnswerStatus.GROUNDED);
        assertThat(initial.recommendations()).extracting(Coach.Recommendation::missionId)
                .contains("mission-guitar-alternate-picking", "mission-guitar-pulse");
        assertThat(initial.recommendations().stream()
                .filter(item -> "mission-guitar-alternate-picking".equals(item.missionId())).findFirst())
                .get().satisfies(recommendation -> assertThat(recommendation.evidence())
                        .anySatisfy(citation -> assertThat(citation.sourceType())
                                .isEqualTo(Evidence.SourceType.DIAGNOSTIC)));

        completeMission("mission-guitar-alternate-picking", true);
        validateExternallyTwice("mission-guitar-alternate-picking-assessment");
        assertThat(evidence.findByInstrumentProfileIdAndCompetencyIdOrderByOccurredAtDesc(
                profile.getId(), "alternate-picking")).anySatisfy(item -> {
            assertThat(item.getType()).isEqualTo(Evidence.Type.APPLICATION);
            assertThat(item.getState()).isEqualTo(Evidence.State.VALID);
            assertThat(item.getSourceType()).isEqualTo(Evidence.SourceType.ASSESSMENT);
        });
        assertThat(mastery.findByInstrumentProfileIdAndCompetencyId(profile.getId(), "alternate-picking"))
                .get().extracting(Mastery::getState).isEqualTo(Mastery.State.PROBABLE_MASTERY_APPLICATION);

        var next = coach.whatShouldIDoToday(profile.getId(), 35, Instant.now(), 10);
        assertThat(next.recommendations()).anySatisfy(item -> {
            assertThat(item.missionId()).isEqualTo("mission-guitar-pulse");
            assertThat(item.kind()).isEqualTo(com.musicos.service.CurriculumEngine.SuggestionKind.INTRODUCTION);
        });
        var nextWorkspace = workspace.mission("mission-guitar-pulse", InstrumentId.GUITAR);
        assertThat(nextWorkspace.repertoire()).extracting(SongView::id)
                .contains("seven-nation-army-guitar");

        completeMission("mission-guitar-pulse", false);
        validateExternallyTwice("mission-guitar-pulse-assessment");
        assertThat(mastery.findByInstrumentProfileIdAndCompetencyId(profile.getId(), "pulse"))
                .get().extracting(Mastery::getState).isEqualTo(Mastery.State.PROBABLE_MASTERY_APPLICATION);

        var review = coach.whatShouldIReview(profile.getId(), Instant.now().plus(31, ChronoUnit.DAYS));
        assertThat(review.status()).isEqualTo(Coach.AnswerStatus.GROUNDED);
        assertThat(review.reviews()).anySatisfy(item -> {
            assertThat(item.missionId()).isEqualTo("mission-guitar-pulse-review");
            assertThat(item.kind()).isIn(com.musicos.service.CurriculumEngine.SuggestionKind.REVIEW,
                    com.musicos.service.CurriculumEngine.SuggestionKind.REVALIDATION);
        });
    }

    @Test
    void everyInstrumentProducesGroundedMissionProgressionFromDiagnosticContext() {
        for (var instrument : InstrumentId.values()) {
            diagnostic.complete(new DiagnosticRequest(instrument, "beginner", 35,
                    List.of("Rock"), List.of(), List.of(), 55, 52, 58));
            var profile = profiles.findByOwnerIdAndInstrument("default", instrument).orElseThrow();
            var answer = coach.whatShouldIDoToday(profile.getId(), 45, Instant.now(), 8);

            assertThat(answer.status()).isEqualTo(Coach.AnswerStatus.GROUNDED);
            assertThat(answer.recommendations()).isNotEmpty().allSatisfy(recommendation ->
                    assertThat(missions.findById(recommendation.missionId())).get()
                            .satisfies(mission -> assertThat(mission.getInstrument()).isEqualTo(instrument)));
        }
    }

    @Test
    void priorKnowledgePositionsProfileWithoutForcingFirstStepsMission() {
        diagnostic.complete(new DiagnosticRequest(InstrumentId.KEYS, "intermediate", 45,
                List.of("Pop"), List.of(), List.of(), 92, 88, 90));
        var profile = profiles.findByOwnerIdAndInstrument("default", InstrumentId.KEYS).orElseThrow();
        var answer = coach.whatShouldIDoToday(profile.getId(), 45, Instant.now(), 10);

        assertThat(profile.getCurrentStage()).isEqualTo(LearningStage.INTERMEDIATE);
        assertThat(answer.status()).isEqualTo(Coach.AnswerStatus.GROUNDED);
        assertThat(answer.recommendations()).extracting(Coach.Recommendation::missionId)
                .noneMatch(id -> id.equals("mission-keys-posture-release")
                        || id.equals("mission-keys-finger-numbers"));
    }

    private void completeMission(String missionId, boolean withRecording) {
        var started = experiences.start(missionId, new StartMissionExperienceRequest(InstrumentId.GUITAR));
        var mission = missions.findById(missionId).orElseThrow();
        for (var exerciseId : mission.getExerciseIds()) {
            var exercise = learning.exerciseHistory(exerciseId);
            learning.recordExerciseAttempt(exerciseId,
                    new ExerciseAttemptRequest(72, 88, 360, 3, 3, started.id()));
            assertThat(learning.exerciseHistory(exerciseId)).hasSizeGreaterThan(exercise.size());
        }
        PracticeRecording recording = null;
        if (withRecording) {
            recording = recordings.saveAndFlush(new PracticeRecording(
                    "mission-experience", started.id().toString(), missionId + ".webm", missionId + ".webm",
                    "audio/webm", 30_000, 68, 68, 8, 88, null, null, null));
        }
        var assessment = assessments.findById(mission.getAssessmentIds().getFirst()).orElseThrow();
        var observations = assessment.getCriterionKeys().stream()
                .map(key -> new AssessmentCriterionObservationRequest(
                        key, Evidence.Result.SUPPORTS, "Aspecto percebido na tomada completa desta sessão."))
                .toList();
        var completed = experiences.complete(missionId, new CompleteMissionExperienceRequest(
                InstrumentId.GUITAR, AssessmentAttempt.ObserverType.SELF, 3,
                recording == null ? null : recording.getId(), "Reflexão após a aplicação.", observations));
        assertThat(completed.status()).isEqualTo("COMPLETED");
    }

    private void validateExternallyTwice(String assessmentId) {
        var assessment = assessments.findById(assessmentId).orElseThrow();
        var observations = assessment.getCriterionKeys().stream()
                .map(key -> new AssessmentCriterionObservationRequest(
                        key, Evidence.Result.SUPPORTS, "Critério observado por músico externo no contexto declarado."))
                .toList();
        for (var attempt = 0; attempt < 2; attempt++) {
            assessmentService.record(assessmentId, new AssessmentAttemptRequest(
                    InstrumentId.GUITAR, AssessmentAttempt.ObserverType.EXTERNAL, 3,
                    "Validação externa independente " + attempt, observations));
        }
    }
}
