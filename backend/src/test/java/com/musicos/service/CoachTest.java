package com.musicos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.musicos.domain.DifficultyDemand;
import com.musicos.domain.Evidence;
import com.musicos.domain.InstrumentId;
import com.musicos.domain.InstrumentProfile;
import com.musicos.domain.LearningGoal;
import com.musicos.domain.LearningStage;
import com.musicos.domain.Mastery;
import com.musicos.domain.Mission;
import com.musicos.repository.EvidenceRepository;
import com.musicos.repository.InstrumentProfileRepository;
import com.musicos.repository.LearningGoalRepository;
import com.musicos.repository.MissionRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CoachTest {
    private static final Instant NOW = Instant.parse("2026-08-05T12:00:00Z");

    @Test
    void recommendsGoalAlignedMissionAndCitesPersistedObservation() {
        var observation = evidence(Evidence.State.VALID, NOW.minusSeconds(120), NOW.plusSeconds(3600));
        var regular = mission("regular", List.of("pulse"), 12);
        var aligned = mission("aligned", List.of("pulse", "creative-goal"), 15);
        var goal = goal("Criar acompanhamentos", List.of("creative-goal"));
        var fixture = fixture(navigation(CurriculumEngine.SuggestionKind.PRACTICE, false),
                report(observation, Evidence.State.VALID), List.of(regular, aligned), List.of(goal), observation);

        var answer = fixture.coach().whatShouldIDoToday("profile", 20, NOW, 3);

        assertThat(answer.status()).isEqualTo(Coach.AnswerStatus.GROUNDED);
        assertThat(answer.recommendations()).singleElement().satisfies(recommendation -> {
            assertThat(recommendation.missionId()).isEqualTo("aligned");
            assertThat(recommendation.goals()).extracting(Coach.GoalCitation::goalId).containsExactly(goal.getId());
            assertThat(recommendation.evidence()).extracting(Coach.EvidenceCitation::evidenceId)
                    .containsExactly(observation.getId());
            assertThat(recommendation.explanation()).contains(observation.getId(), goal.getId());
        });
    }

    @Test
    void refusesToInventDailyMissionWhenNoObservableEvidenceExists() {
        var fixture = fixture(navigation(CurriculumEngine.SuggestionKind.INTRODUCTION, false),
                emptyReport(), List.of(mission("intro", List.of("pulse"), 10)), List.of(), null);
        when(fixture.curriculum().findPrerequisites("profile", "pulse", NOW)).thenReturn(List.of());

        var answer = fixture.coach().whatShouldIDoToday("profile", 20, NOW, 3);

        assertThat(answer.status()).isEqualTo(Coach.AnswerStatus.INSUFFICIENT_EVIDENCE);
        assertThat(answer.recommendations()).isEmpty();
        assertThat(answer.message()).contains("sem inventar justificativa");
    }

    @Test
    void recommendsReviewFromAgedEvidenceEvenWithoutMissionWrapper() {
        var aged = evidence(Evidence.State.VALID, NOW.minusSeconds(7200), NOW.minusSeconds(1));
        var fixture = fixture(navigation(CurriculumEngine.SuggestionKind.REVIEW, true),
                report(aged, Evidence.State.AGED), List.of(), List.of(), aged);

        var answer = fixture.coach().whatShouldIReview("profile", NOW);

        assertThat(answer.status()).isEqualTo(Coach.AnswerStatus.GROUNDED);
        assertThat(answer.reviews()).singleElement().satisfies(review -> {
            assertThat(review.missionId()).isNull();
            assertThat(review.kind()).isEqualTo(CurriculumEngine.SuggestionKind.REVIEW);
            assertThat(review.evidence()).singleElement().satisfies(citation -> {
                assertThat(citation.evidenceId()).isEqualTo(aged.getId());
                assertThat(citation.effectiveState()).isEqualTo(Evidence.State.AGED);
            });
        });
    }

    @Test
    void answersAdvanceOnlyWhenDecisionHasCitableEvidence() {
        var observation = evidence(Evidence.State.VALID, NOW.minusSeconds(120), NOW.plusSeconds(3600));
        var fixture = fixture(navigation(CurriculumEngine.SuggestionKind.PRACTICE, false),
                report(observation, Evidence.State.VALID), List.of(), List.of(), observation);

        var answer = fixture.coach().canIAdvance("profile", "pulse", NOW);

        assertThat(answer.decision()).isEqualTo(Coach.AdvanceDecision.YES_FOR_GUIDED_PRACTICE);
        assertThat(answer.curriculumStatus()).isEqualTo(CurriculumEngine.CompetencyStatus.IN_PROGRESS);
        assertThat(answer.evidence()).extracting(Coach.EvidenceCitation::evidenceId)
                .containsExactly(observation.getId());
    }

    @Test
    void explainsMissionFromTheSameEnginesAndEvidenceUsedToRecommendIt() {
        var observation = evidence(Evidence.State.VALID, NOW.minusSeconds(120), NOW.plusSeconds(3600));
        var mission = mission("mission-1", List.of("pulse"), 12);
        var fixture = fixture(navigation(CurriculumEngine.SuggestionKind.APPLICATION, false),
                report(observation, Evidence.State.VALID), List.of(mission), List.of(), observation);

        var answer = fixture.coach().whyDidIReceiveMission("profile", "mission-1", NOW);

        assertThat(answer.status()).isEqualTo(Coach.AnswerStatus.GROUNDED);
        assertThat(answer.recommendation().kind()).isEqualTo(CurriculumEngine.SuggestionKind.APPLICATION);
        assertThat(answer.recommendation().explanation())
                .contains("Curriculum Engine", "Evidence Engine", observation.getId());
    }

    @Test
    void recommendationRecordRejectsUngroundedExplanation() {
        assertThatThrownBy(() -> new Coach.Recommendation(
                "mission", "Missão", "pulse", CurriculumEngine.SuggestionKind.PRACTICE,
                10, "Objetivo", "Evidência", List.of(), List.of(), "Explicação"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("evidência observável citada");
    }

    private Fixture fixture(CurriculumEngine.Navigation navigation, EvidenceEngine.ConfidenceReport report,
                            List<Mission> missionList, List<LearningGoal> goalList, Evidence observation) {
        var curriculum = mock(CurriculumEngine.class);
        var evidenceEngine = mock(EvidenceEngine.class);
        var profiles = mock(InstrumentProfileRepository.class);
        var goals = mock(LearningGoalRepository.class);
        var missions = mock(MissionRepository.class);
        var evidence = mock(EvidenceRepository.class);
        var profile = new InstrumentProfile(
                "profile", "owner", InstrumentId.GUITAR, "Guitarra", LearningStage.BEGINNER,
                "curriculum", null);
        when(profiles.findById("profile")).thenReturn(Optional.of(profile));
        when(curriculum.navigate(org.mockito.ArgumentMatchers.eq("profile"),
                org.mockito.ArgumentMatchers.eq(NOW), anyInt())).thenReturn(navigation);
        when(goals.findByInstrumentProfileIdAndStatus("profile", LearningGoal.Status.ACTIVE))
                .thenReturn(goalList);
        when(missions.findByCurriculumIdAndStatus("curriculum", Mission.Status.ACTIVE))
                .thenReturn(missionList);
        when(evidenceEngine.inspect("profile", "pulse", NOW)).thenReturn(report);
        when(evidence.findByInstrumentProfileIdAndCompetencyIdOrderByOccurredAtDesc("profile", "pulse"))
                .thenReturn(observation == null ? List.of() : List.of(observation));
        return new Fixture(new Coach(curriculum, evidenceEngine, profiles, goals, missions, evidence), curriculum);
    }

    private CurriculumEngine.Navigation navigation(CurriculumEngine.SuggestionKind kind, boolean reviewDue) {
        var status = reviewDue ? CurriculumEngine.CompetencyStatus.REVIEW_DUE
                : CurriculumEngine.CompetencyStatus.IN_PROGRESS;
        var view = new CurriculumEngine.CompetencyView(
                "pulse", "Pulso", 0, status,
                reviewDue ? Mastery.State.REVALIDATION_NEEDED : Mastery.State.DEVELOPING,
                true, List.of(), reviewDue ? "Evidência envelhecida." : "Evidência parcial.");
        var goalView = new CurriculumEngine.CompetencyView(
                "creative-goal", "Criação", 1, CurriculumEngine.CompetencyStatus.AVAILABLE,
                Mastery.State.UNOBSERVED, true, List.of(), "Disponível no caminho ativo.");
        var reviews = reviewDue
                ? List.of(new CurriculumEngine.ReviewNeed(
                "pulse", "Pulso", kind, 0, 1, "Evidência envelhecida."))
                : List.<CurriculumEngine.ReviewNeed>of();
        var step = new CurriculumEngine.NextStep(
                "pulse", "Pulso", kind, 0,
                reviewDue ? "Evidência envelhecida." : "Evidência parcial indica consolidação.", List.of());
        var position = new CurriculumEngine.CurriculumPosition(
                2, 0, reviewDue ? 0 : 1, 1, 0, reviews.size(), "pulse", "Foco em pulso.");
        return new CurriculumEngine.Navigation(
                "profile", "curriculum", "path", NOW, position, List.of(view, goalView), reviews, List.of(step));
    }

    private EvidenceEngine.ConfidenceReport report(Evidence observation, Evidence.State effectiveState) {
        var current = effectiveState != Evidence.State.AGED;
        var contribution = new EvidenceEngine.EvidenceContribution(
                observation.getId(), observation.getCriterionKey(), observation.getState(), effectiveState,
                observation.getFunctionalWeight(), observation.getReliability(), observation.getResult(),
                current, current, current ? "Observação admissível." : "Observação envelhecida.");
        return new EvidenceEngine.ConfidenceReport(
                "profile", "pulse", "policy-v1", true,
                effectiveState == Evidence.State.AGED
                        ? Mastery.State.REVALIDATION_NEEDED : Mastery.State.DEVELOPING,
                effectiveState == Evidence.State.AGED
                        ? EvidenceEngine.Confidence.REVALIDATION_REQUIRED : EvidenceEngine.Confidence.DEVELOPING,
                current ? List.of("steady-pulse") : List.of(), current ? List.of() : List.of("steady-pulse"),
                current ? 1 : 0, false, false, false, false,
                observation.getOccurredAt(), observation.getValidUntil(),
                new EvidenceEngine.WeightSummary(0, 1, 0, 0, current ? 0 : 1),
                new EvidenceEngine.ReliabilitySummary(1, 0, 0),
                current ? List.of(observation.getId()) : List.of(), List.of(),
                current ? List.of() : List.of(observation.getId()), List.of(contribution),
                current ? "Evidência parcial." : "Evidência envelhecida.",
                current ? "Obter aplicação musical." : "Obter observação atual de revalidação.");
    }

    private EvidenceEngine.ConfidenceReport emptyReport() {
        return new EvidenceEngine.ConfidenceReport(
                "profile", "pulse", "policy-v1", true, Mastery.State.UNOBSERVED,
                EvidenceEngine.Confidence.NO_ADMISSIBLE_EVIDENCE, List.of(), List.of("steady-pulse"),
                0, false, false, false, false, null, null,
                new EvidenceEngine.WeightSummary(0, 0, 0, 0, 0),
                new EvidenceEngine.ReliabilitySummary(0, 0, 0),
                List.of(), List.of(), List.of(), List.of(), "Nada observado.",
                "Realizar observação diagnóstica.");
    }

    private Evidence evidence(Evidence.State state, Instant occurredAt, Instant validUntil) {
        return new Evidence(
                "profile", "pulse", "steady-pulse", Evidence.Type.EXECUTION, state,
                Evidence.FunctionalWeight.PRIMARY, Evidence.Reliability.HIGH, Evidence.Result.SUPPORTS,
                Evidence.SourceType.SESSION, "session-1", "session-1", 2,
                "Pulso mantido durante a execução.", "BPM 60, compasso 4/4.",
                "protocol-v1", null, null, null, occurredAt, validUntil);
    }

    private Mission mission(String id, List<String> competencyIds, int minutes) {
        var result = new Mission(
                id, "curriculum", "Missão " + id, "Manter o pulso em contexto musical.",
                "Acompanhamento simples.", "Aplicar o pulso observado.", minutes, InstrumentId.GUITAR,
                LearningStage.BEGINNER, "Execução contínua.", "Observação de execução.",
                "Aplicação musical.", null, DifficultyDemand.unspecified(), competencyIds,
                List.of("lesson"), List.of("exercise"), List.of("assessment"));
        result.activate();
        return result;
    }

    private LearningGoal goal(String title, List<String> competencyIds) {
        return new LearningGoal(
                "profile", "curriculum", title, "Tocar com autonomia.", "Contexto musical declarado.",
                LearningGoal.Type.CUSTOM, 1, null, competencyIds, List.of());
    }

    private record Fixture(Coach coach, CurriculumEngine curriculum) {
    }
}
