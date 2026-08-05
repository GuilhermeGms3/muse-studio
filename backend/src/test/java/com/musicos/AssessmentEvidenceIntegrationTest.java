package com.musicos;

import static com.musicos.api.ApiModels.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.musicos.domain.AssessmentAttempt;
import com.musicos.domain.Evidence;
import com.musicos.domain.InstrumentId;
import com.musicos.repository.EvidenceRepository;
import com.musicos.repository.InstrumentProfileRepository;
import com.musicos.service.AssessmentService;
import com.musicos.service.LearningContentService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AssessmentEvidenceIntegrationTest {
    @Autowired
    private LearningContentService learning;
    @Autowired
    private AssessmentService assessments;
    @Autowired
    private InstrumentProfileRepository profiles;
    @Autowired
    private EvidenceRepository evidence;

    @Test
    void exerciseAttemptCreatesOnlyProvisionalPracticeEvidence() {
        learning.recordExerciseAttempt("deep-guitar-picking-cycle",
                new ExerciseAttemptRequest(72, 85, 480, 3, 3));

        var profile = profiles.findByOwnerIdAndInstrument("default", InstrumentId.GUITAR).orElseThrow();
        var items = evidence.findByInstrumentProfileIdAndCompetencyIdOrderByOccurredAtDesc(
                profile.getId(), "alternate-picking");

        assertThat(items).anySatisfy(item -> {
            assertThat(item.getSourceType()).isEqualTo(Evidence.SourceType.EXERCISE);
            assertThat(item.getState()).isEqualTo(Evidence.State.PROVISIONAL);
            assertThat(item.getFunctionalWeight()).isEqualTo(Evidence.FunctionalWeight.CORROBORATING);
            assertThat(item.getReliability()).isEqualTo(Evidence.Reliability.LOW);
        });
    }

    @Test
    void externalProtocolObservationCreatesTraceablePrimaryEvidence() {
        var result = assessments.record("mission-guitar-alternate-picking-assessment",
                new AssessmentAttemptRequest(InstrumentId.GUITAR, AssessmentAttempt.ObserverType.EXTERNAL, 3,
                        "Observação feita durante a tomada completa.",
                        List.of(
                                observation("controle"),
                                observation("continuidade"),
                                observation("aplicacao-musical"))));

        assertThat(result.results()).hasSize(3).allSatisfy(item -> {
            assertThat(item.state()).isEqualTo(Evidence.State.VALID.name());
            assertThat(item.reliability()).isEqualTo(Evidence.Reliability.MODERATE.name());
        });
        assertThat(result.results()).extracting(AssessmentCriterionResultView::evidenceId)
                .allMatch(id -> evidence.existsById(id));
    }

    private AssessmentCriterionObservationRequest observation(String criterion) {
        return new AssessmentCriterionObservationRequest(
                criterion, Evidence.Result.SUPPORTS, "Critério observado sob o protocolo declarado.");
    }
}
