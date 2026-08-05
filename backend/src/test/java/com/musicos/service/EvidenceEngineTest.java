package com.musicos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.musicos.domain.Competency;
import com.musicos.domain.Evidence;
import com.musicos.domain.InstrumentId;
import com.musicos.domain.InstrumentProfile;
import com.musicos.domain.LearningStage;
import com.musicos.domain.LearningTrack;
import com.musicos.domain.Mastery;
import com.musicos.domain.SkillKind;
import com.musicos.repository.CompetencyRepository;
import com.musicos.repository.EvidenceRepository;
import com.musicos.repository.InstrumentProfileRepository;
import com.musicos.repository.MasteryRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class EvidenceEngineTest {
    private static final Instant NOW = Instant.parse("2026-08-05T12:00:00Z");

    @Test
    void infersApplicationMasteryOnlyFromIndependentPrimaryObservations() {
        var report = fixture(competency(true), List.of(
                evidence(Evidence.Type.EXECUTION, Evidence.Reliability.HIGH, Evidence.Result.SUPPORTS,
                        "session-1", NOW.minusSeconds(120), NOW.plusSeconds(3600), null),
                evidence(Evidence.Type.APPLICATION, Evidence.Reliability.MODERATE, Evidence.Result.SUPPORTS,
                        "repertoire-1", NOW.minusSeconds(60), NOW.plusSeconds(3600), null)))
                .inspect("profile", "pulse", NOW);

        assertThat(report.masteryState()).isEqualTo(Mastery.State.PROBABLE_MASTERY_APPLICATION);
        assertThat(report.confidence()).isEqualTo(EvidenceEngine.Confidence.SUPPORTED_IN_APPLICATION);
        assertThat(report.independentPrimaryEvidenceCount()).isEqualTo(2);
        assertThat(report.coveredMandatoryCriteria()).containsExactly("steady-pulse");
        assertThat(report.rationale()).doesNotContain("%");
    }

    @Test
    void doesNotCountRepeatedObservationFromSameIndependenceContextTwice() {
        var report = fixture(competency(true), List.of(
                evidence(Evidence.Type.EXECUTION, Evidence.Reliability.HIGH, Evidence.Result.SUPPORTS,
                        "same-session", NOW.minusSeconds(120), NOW.plusSeconds(3600), null),
                evidence(Evidence.Type.APPLICATION, Evidence.Reliability.HIGH, Evidence.Result.SUPPORTS,
                        "same-session", NOW.minusSeconds(60), NOW.plusSeconds(3600), null)))
                .inspect("profile", "pulse", NOW);

        assertThat(report.independentPrimaryEvidenceCount()).isEqualTo(1);
        assertThat(report.masteryState()).isEqualTo(Mastery.State.DEVELOPING);
    }

    @Test
    void preservesLowReliabilityChallengeWithoutTreatingItAsMusicalFailure() {
        var unreliable = evidence(Evidence.Type.EXECUTION, Evidence.Reliability.LOW,
                Evidence.Result.CHALLENGES, "noisy-capture", NOW.minusSeconds(30), NOW.plusSeconds(3600), null);
        var report = fixture(competency(true), List.of(
                evidence(Evidence.Type.EXECUTION, Evidence.Reliability.HIGH, Evidence.Result.SUPPORTS,
                        "session-1", NOW.minusSeconds(120), NOW.plusSeconds(3600), null),
                evidence(Evidence.Type.APPLICATION, Evidence.Reliability.MODERATE, Evidence.Result.SUPPORTS,
                        "repertoire-1", NOW.minusSeconds(60), NOW.plusSeconds(3600), null),
                unreliable)).inspect("profile", "pulse", NOW);

        assertThat(report.masteryState()).isEqualTo(Mastery.State.PROBABLE_MASTERY_APPLICATION);
        assertThat(report.unresolvedConflict()).isFalse();
        assertThat(report.excludedEvidenceIds()).contains(unreliable.getId());
        assertThat(report.contributions()).filteredOn(item -> item.evidenceId().equals(unreliable.getId()))
                .singleElement().satisfies(item -> assertThat(item.reason()).contains("Confiabilidade baixa"));
    }

    @Test
    void expiredPrimaryObservationRequiresRevalidationAndRemainsAuditable() {
        var expired = evidence(Evidence.Type.APPLICATION, Evidence.Reliability.HIGH,
                Evidence.Result.SUPPORTS, "old-performance", NOW.minusSeconds(7200), NOW.minusSeconds(1), null);
        var report = fixture(competency(true), List.of(expired)).inspect("profile", "pulse", NOW);

        assertThat(report.masteryState()).isEqualTo(Mastery.State.REVALIDATION_NEEDED);
        assertThat(report.confidence()).isEqualTo(EvidenceEngine.Confidence.REVALIDATION_REQUIRED);
        assertThat(report.excludedEvidenceIds()).containsExactly(expired.getId());
        assertThat(report.contributions()).singleElement()
                .satisfies(item -> assertThat(item.effectiveState()).isEqualTo(Evidence.State.AGED));
    }

    @Test
    void comparableReliableResultsInOppositeDirectionsCreateConflict() {
        var report = fixture(competency(true), List.of(
                evidence(Evidence.Type.EXECUTION, Evidence.Reliability.HIGH, Evidence.Result.SUPPORTS,
                        "observer-1", NOW.minusSeconds(120), NOW.plusSeconds(3600), null),
                evidence(Evidence.Type.EXECUTION, Evidence.Reliability.HIGH, Evidence.Result.CHALLENGES,
                        "observer-2", NOW.minusSeconds(60), NOW.plusSeconds(3600), null)))
                .inspect("profile", "pulse", NOW);

        assertThat(report.masteryState()).isEqualTo(Mastery.State.REVALIDATION_NEEDED);
        assertThat(report.confidence()).isEqualTo(EvidenceEngine.Confidence.CONFLICTED);
        assertThat(report.unresolvedConflict()).isTrue();
        assertThat(report.nextObservation()).contains("investigar o conflito");
    }

    @Test
    void incompletePolicyCapsInferenceEvenWithStrongApplicationEvidence() {
        var report = fixture(competency(false), List.of(
                evidence(Evidence.Type.EXECUTION, Evidence.Reliability.HIGH, Evidence.Result.SUPPORTS,
                        "session-1", NOW.minusSeconds(120), NOW.plusSeconds(3600), null),
                evidence(Evidence.Type.APPLICATION, Evidence.Reliability.HIGH, Evidence.Result.SUPPORTS,
                        "repertoire-1", NOW.minusSeconds(60), NOW.plusSeconds(3600), null)))
                .inspect("profile", "pulse", NOW);

        assertThat(report.policyComplete()).isFalse();
        assertThat(report.masteryState()).isEqualTo(Mastery.State.DEVELOPING);
        assertThat(report.confidence()).isEqualTo(EvidenceEngine.Confidence.POLICY_INCOMPLETE);
    }

    @Test
    void retentionObservationProducesCategoricalRetainedConfidence() {
        var report = fixture(competency(true), List.of(
                evidence(Evidence.Type.APPLICATION, Evidence.Reliability.HIGH, Evidence.Result.SUPPORTS,
                        "performance-1", NOW.minusSeconds(7200), NOW.plusSeconds(3600), null),
                evidence(Evidence.Type.RETENTION, Evidence.Reliability.HIGH, Evidence.Result.SUPPORTS,
                        "review-1", NOW.minusSeconds(60), NOW.plusSeconds(3600), null)))
                .inspect("profile", "pulse", NOW);

        assertThat(report.masteryState()).isEqualTo(Mastery.State.RETAINED);
        assertThat(report.confidence()).isEqualTo(EvidenceEngine.Confidence.SUPPORTED_AND_RETAINED);
        assertThat(report.retentionObserved()).isTrue();
    }

    @Test
    void supersedingObservationExcludesOriginalWithoutErasingIt() {
        var original = evidence(Evidence.Type.EXECUTION, Evidence.Reliability.MODERATE,
                Evidence.Result.SUPPORTS, "capture-1", NOW.minusSeconds(120), NOW.plusSeconds(3600), null);
        var replacement = evidence(Evidence.Type.EXECUTION, Evidence.Reliability.HIGH,
                Evidence.Result.SUPPORTS, "capture-2", NOW.minusSeconds(60), NOW.plusSeconds(3600), original.getId());
        var report = fixture(competency(true), List.of(replacement, original)).inspect("profile", "pulse", NOW);

        assertThat(report.independentPrimaryEvidenceCount()).isEqualTo(1);
        assertThat(report.supportingEvidenceIds()).containsExactly(replacement.getId());
        assertThat(report.excludedEvidenceIds()).contains(original.getId());
        assertThat(report.contributions()).filteredOn(item -> item.evidenceId().equals(original.getId()))
                .singleElement().satisfies(item ->
                        assertThat(item.effectiveState()).isEqualTo(Evidence.State.SUPERSEDED));
    }

    @Test
    void rejectsDeclarativeObservationAsPrimaryEvidence() {
        var fixture = fixture(competency(true), List.of());
        var observation = new EvidenceEngine.Observation(
                "profile", "pulse", "steady-pulse", Evidence.Type.DECLARATIVE, Evidence.State.VALID,
                Evidence.FunctionalWeight.PRIMARY, Evidence.Reliability.HIGH, Evidence.Result.SUPPORTS,
                Evidence.SourceType.MANUAL, "answer-1", "answer-1", 2, "Aluno descreveu o conceito.",
                "Pergunta oral.", "protocol-v1", null, null, null, Instant.now().minusSeconds(10), null);

        assertThatThrownBy(() -> fixture.collect(observation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não sustenta domínio sozinha");
    }

    private EvidenceEngine fixture(Competency competency, List<Evidence> observations) {
        var profiles = mock(InstrumentProfileRepository.class);
        var competencies = mock(CompetencyRepository.class);
        var evidence = mock(EvidenceRepository.class);
        var mastery = mock(MasteryRepository.class);
        var profile = new InstrumentProfile(
                "profile", "owner", InstrumentId.GUITAR, "Guitarra", LearningStage.BEGINNER,
                "curriculum", null);
        when(profiles.findById("profile")).thenReturn(Optional.of(profile));
        when(competencies.findById("pulse")).thenReturn(Optional.of(competency));
        when(evidence.findByInstrumentProfileIdAndCompetencyIdOrderByOccurredAtDesc("profile", "pulse"))
                .thenReturn(observations);
        when(mastery.findByInstrumentProfileIdAndCompetencyId("profile", "pulse"))
                .thenReturn(Optional.empty());
        return new EvidenceEngine(profiles, competencies, evidence, mastery);
    }

    private Competency competency(boolean completePolicy) {
        return new Competency(
                "pulse", "Pulso", "Pulso estável", "Ritmo", "Manter pulso estável",
                "Executar pulso observável.", "Com metrônomo em condição declarada.",
                SkillKind.ABILITY, LearningTrack.RHYTHM, LearningStage.BEGINNER,
                List.of(InstrumentId.GUITAR), List.of(),
                completePolicy ? List.of("steady-pulse") : List.of(),
                completePolicy ? "pulse-policy-v1" : null, 30, null);
    }

    private Evidence evidence(Evidence.Type type, Evidence.Reliability reliability, Evidence.Result result,
                              String independenceKey, Instant occurredAt, Instant validUntil,
                              String supersedesEvidenceId) {
        return new Evidence(
                "profile", "pulse", "steady-pulse", type, Evidence.State.VALID,
                Evidence.FunctionalWeight.PRIMARY, reliability, result, Evidence.SourceType.SESSION,
                independenceKey, independenceKey, 3, "Pulso observado durante execução.",
                "BPM 80, compasso 4/4, sem apoio.", "protocol-v1", null, null,
                supersedesEvidenceId, occurredAt, validUntil);
    }
}
