package com.musicos;

import static com.musicos.api.ApiModels.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.musicos.domain.AssessmentAttempt;
import com.musicos.domain.Evidence;
import com.musicos.domain.InstrumentId;
import com.musicos.domain.MissionExperience;
import com.musicos.domain.PracticeRecording;
import com.musicos.repository.AssessmentRepository;
import com.musicos.repository.EvidenceRepository;
import com.musicos.repository.MasteryRepository;
import com.musicos.repository.InstrumentProfileRepository;
import com.musicos.repository.MissionExperienceRepository;
import com.musicos.repository.PracticeRecordingRepository;
import com.musicos.service.DiagnosticService;
import com.musicos.service.Coach;
import com.musicos.service.LearningContentService;
import com.musicos.service.MissionExperienceService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MissionExperienceIntegrationTest {
    private static final String MISSION_ID = "mission-guitar-alternate-picking";

    @Autowired MissionExperienceService experiences;
    @Autowired MissionExperienceRepository experienceRepository;
    @Autowired LearningContentService learning;
    @Autowired PracticeRecordingRepository recordings;
    @Autowired AssessmentRepository assessments;
    @Autowired EvidenceRepository evidence;
    @Autowired MasteryRepository mastery;
    @Autowired DiagnosticService diagnostic;
    @Autowired Coach coach;
    @Autowired InstrumentProfileRepository profiles;

    @Test
    void startsPersistsPausesAndResumesTheSemanticActivity() {
        var started = experiences.start(MISSION_ID, new StartMissionExperienceRequest(InstrumentId.GUITAR));
        var same = experiences.start(MISSION_ID, new StartMissionExperienceRequest(InstrumentId.GUITAR));
        assertThat(same.id()).isEqualTo(started.id());

        var paused = experiences.update(MISSION_ID, new UpdateMissionExperienceRequest(
                InstrumentId.GUITAR, MissionExperience.ActivityKind.LESSON,
                "lesson-alternate-picking", null, true));
        assertThat(paused.status()).isEqualTo("PAUSED");
        assertThat(paused.currentActivityKind()).isEqualTo("LESSON");
        assertThat(paused.currentActivityId()).isEqualTo("lesson-alternate-picking");

        var resumed = experiences.start(MISSION_ID, new StartMissionExperienceRequest(InstrumentId.GUITAR));
        assertThat(resumed.status()).isEqualTo("IN_PROGRESS");
        assertThat(resumed.currentActivityId()).isEqualTo("lesson-alternate-picking");
    }

    @Test
    void completionRequiresExplicitPracticeAndPreservesProvisionalEvidence() {
        var started = experiences.start(MISSION_ID, new StartMissionExperienceRequest(InstrumentId.GUITAR));
        var recording = recordings.saveAndFlush(new PracticeRecording(
                "mission-experience", started.id().toString(), "take.webm", "take.webm",
                "audio/webm", 30_000, 68, 68, 8, 88, null, null, null));
        var request = completionRequest(recording);

        assertThatThrownBy(() -> experiences.complete(MISSION_ID, request))
                .hasMessageContaining("todas as práticas");

        learning.recordExerciseAttempt("deep-guitar-picking-cycle",
                new ExerciseAttemptRequest(72, 86, 420, 3, 3, started.id()));
        learning.recordExerciseAttempt("deep-guitar-picking-riff",
                new ExerciseAttemptRequest(68, 84, 480, 3, 3, started.id()));

        var completed = experiences.complete(MISSION_ID, request);
        assertThat(completed.status()).isEqualTo("COMPLETED");
        assertThat(completed.assessmentAttemptId()).isNotNull();
        assertThat(completed.lastRecordingId()).isEqualTo(recording.getId());
        assertThat(experienceRepository.findById(started.id())).get()
                .extracting(MissionExperience::getCompletedAt).isNotNull();

        var profileEvidence = evidence.findByInstrumentProfileIdOrderByOccurredAtDesc(
                completed.instrumentProfileId()).stream()
                .filter(item -> item.getSourceType() == Evidence.SourceType.ASSESSMENT).toList();
        assertThat(profileEvidence).hasSize(3)
                .allSatisfy(item -> {
                    assertThat(item.getState()).isEqualTo(Evidence.State.PROVISIONAL);
                    assertThat(item.getReliability()).isEqualTo(Evidence.Reliability.LOW);
                    assertThat(item.getArtifactReference()).isEqualTo(recording.getId().toString());
                });
        assertThat(mastery.findByInstrumentProfileIdAndCompetencyId(
                completed.instrumentProfileId(), "alternate-picking"))
                .get().extracting(com.musicos.domain.Mastery::getState)
                .isIn(com.musicos.domain.Mastery.State.UNOBSERVED,
                        com.musicos.domain.Mastery.State.INITIAL_HYPOTHESIS);
    }

    @Test
    void diagnosticCreatesContextWithoutInventingMastery() {
        diagnostic.complete(new DiagnosticRequest(InstrumentId.GUITAR, "intermediate", 40,
                List.of("Rock"), List.of(), List.of(), 70, 60, 55));
        var diagnosticEvidence = evidence.findAll().stream()
                .filter(item -> item.getSourceType() == Evidence.SourceType.DIAGNOSTIC).toList();
        assertThat(diagnosticEvidence).isNotEmpty().allSatisfy(item -> {
            assertThat(item.getState()).isEqualTo(Evidence.State.PROVISIONAL);
            assertThat(item.getReliability()).isEqualTo(Evidence.Reliability.LOW);
            assertThat(item.getResult()).isEqualTo(Evidence.Result.INCONCLUSIVE);
        });
        assertThat(mastery.findAll()).allSatisfy(item ->
                assertThat(item.getState()).isIn(com.musicos.domain.Mastery.State.UNOBSERVED,
                        com.musicos.domain.Mastery.State.INITIAL_HYPOTHESIS));
        var profile = profiles.findByOwnerIdAndInstrument("default", InstrumentId.GUITAR).orElseThrow();
        var today = coach.whatShouldIDoToday(profile.getId(), null, java.time.Instant.now(), 3);
        assertThat(today.recommendations()).anySatisfy(item -> {
            assertThat(item.missionId()).isEqualTo(MISSION_ID);
            assertThat(item.evidence()).anySatisfy(citation ->
                    assertThat(citation.sourceType()).isEqualTo(Evidence.SourceType.DIAGNOSTIC));
        });
    }

    private CompleteMissionExperienceRequest completionRequest(PracticeRecording recording) {
        var assessment = assessments.findById(MISSION_ID + "-assessment").orElseThrow();
        var observations = assessment.getCriterionKeys().stream()
                .map(key -> new AssessmentCriterionObservationRequest(
                        key, Evidence.Result.SUPPORTS, "Observei este aspecto na tomada completa."))
                .toList();
        return new CompleteMissionExperienceRequest(InstrumentId.GUITAR,
                AssessmentAttempt.ObserverType.SELF, 3, recording.getId(),
                "Reflexão depois de ouvir a gravação.", observations);
    }
}
