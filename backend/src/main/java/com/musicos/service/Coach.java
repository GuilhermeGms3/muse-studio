package com.musicos.service;

import com.musicos.domain.Evidence;
import com.musicos.domain.InstrumentId;
import com.musicos.domain.InstrumentProfile;
import com.musicos.domain.LearningGoal;
import com.musicos.domain.LearningStage;
import com.musicos.domain.Mission;
import com.musicos.repository.EvidenceRepository;
import com.musicos.repository.InstrumentProfileRepository;
import com.musicos.repository.LearningGoalRepository;
import com.musicos.repository.MissionRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Coach {
    public enum AnswerStatus {
        GROUNDED,
        INSUFFICIENT_EVIDENCE,
        NO_ELIGIBLE_MISSION,
        NO_REVIEW_DUE,
        NOT_CURRENT_RECOMMENDATION
    }

    public enum AdvanceDecision {
        YES_FOR_GUIDED_PRACTICE,
        NOT_YET,
        INSUFFICIENT_EVIDENCE
    }

    public record ProfileContext(
            String instrumentProfileId,
            InstrumentId instrument,
            LearningStage stage,
            String curriculumId) {
    }

    public record GoalCitation(
            String goalId,
            String title,
            String desiredOutcome,
            String musicalContext,
            LearningGoal.Type type,
            int declaredPriority,
            LocalDate targetDate) {
    }

    public record EvidenceCitation(
            String evidenceId,
            String competencyId,
            String criterionKey,
            Evidence.Type type,
            Evidence.State recordedState,
            Evidence.State effectiveState,
            Evidence.Reliability reliability,
            Evidence.Result result,
            Evidence.SourceType sourceType,
            String sourceId,
            Instant occurredAt,
            String observation,
            String conditions) {
    }

    public record Recommendation(
            String missionId,
            String title,
            String competencyId,
            CurriculumEngine.SuggestionKind kind,
            Integer estimatedMinutes,
            String observableObjective,
            String expectedEvidence,
            List<GoalCitation> goals,
            List<EvidenceCitation> evidence,
            String explanation) {
        public Recommendation {
            goals = List.copyOf(goals);
            evidence = List.copyOf(evidence);
            if (evidence.isEmpty()) {
                throw new IllegalArgumentException("recomendação do Coach exige evidência observável citada");
            }
        }
    }

    public record TodayAnswer(
            AnswerStatus status,
            ProfileContext profile,
            Instant evaluatedAt,
            Integer availableMinutes,
            List<GoalCitation> activeGoals,
            List<Recommendation> recommendations,
            String message) {
        public TodayAnswer {
            activeGoals = List.copyOf(activeGoals);
            recommendations = List.copyOf(recommendations);
        }
    }

    public record ReviewAnswer(
            AnswerStatus status,
            ProfileContext profile,
            Instant evaluatedAt,
            List<Recommendation> reviews,
            String message) {
        public ReviewAnswer {
            reviews = List.copyOf(reviews);
        }
    }

    public record AdvanceAnswer(
            AdvanceDecision decision,
            ProfileContext profile,
            String competencyId,
            CurriculumEngine.CompetencyStatus curriculumStatus,
            List<GoalCitation> goals,
            List<EvidenceCitation> evidence,
            String explanation) {
        public AdvanceAnswer {
            goals = List.copyOf(goals);
            evidence = List.copyOf(evidence);
            if (decision != AdvanceDecision.INSUFFICIENT_EVIDENCE && evidence.isEmpty()) {
                throw new IllegalArgumentException("decisão de avanço exige evidência observável citada");
            }
        }
    }

    public record MissionAnswer(
            AnswerStatus status,
            ProfileContext profile,
            String missionId,
            Recommendation recommendation,
            String message) {
    }

    private record Context(
            InstrumentProfile profile,
            CurriculumEngine.Navigation navigation,
            List<LearningGoal> goals,
            List<Mission> missions) {
    }

    private final CurriculumEngine curriculumEngine;
    private final EvidenceEngine evidenceEngine;
    private final InstrumentProfileRepository profiles;
    private final LearningGoalRepository goals;
    private final MissionRepository missions;
    private final EvidenceRepository evidence;

    public Coach(CurriculumEngine curriculumEngine, EvidenceEngine evidenceEngine,
                 InstrumentProfileRepository profiles, LearningGoalRepository goals,
                 MissionRepository missions, EvidenceRepository evidence) {
        this.curriculumEngine = curriculumEngine;
        this.evidenceEngine = evidenceEngine;
        this.profiles = profiles;
        this.goals = goals;
        this.missions = missions;
        this.evidence = evidence;
    }

    @Transactional(readOnly = true)
    public TodayAnswer whatShouldIDoToday(String instrumentProfileId) {
        return whatShouldIDoToday(instrumentProfileId, null, Instant.now(), 3);
    }

    @Transactional(readOnly = true)
    public TodayAnswer whatShouldIDoToday(
            String instrumentProfileId, Integer availableMinutes, Instant evaluatedAt, int limit) {
        Objects.requireNonNull(evaluatedAt, "evaluatedAt");
        if (availableMinutes != null && availableMinutes < 1) {
            throw new IllegalArgumentException("availableMinutes deve ser positivo");
        }
        if (limit < 1) throw new IllegalArgumentException("limit deve ser positivo");
        var context = load(instrumentProfileId, evaluatedAt, Math.max(limit * 10, 50));
        var recommendations = new ArrayList<Recommendation>();

        for (var step : context.navigation().nextSteps()) {
            if (recommendations.size() >= limit) break;
            var report = evidenceEngine.inspect(context.profile().getId(), step.competencyId(), evaluatedAt);
            var citations = citationsForStep(context, step, report, evaluatedAt);
            if (citations.isEmpty()) continue;
            var mission = bestMission(context, step, availableMinutes);
            if (mission != null) {
                var matchingGoals = goalCitations(matchingGoals(
                        context.goals(), Set.copyOf(mission.getCompetencyIds())));
                recommendations.add(recommendation(mission, step, report, matchingGoals, citations));
            } else if (step.kind() == CurriculumEngine.SuggestionKind.REVIEW
                    || step.kind() == CurriculumEngine.SuggestionKind.REVALIDATION) {
                var matchingGoals = goalCitations(matchingGoals(context.goals(), Set.of(step.competencyId())));
                recommendations.add(reviewRecommendation(step, report, matchingGoals, citations));
            }
        }

        var status = !recommendations.isEmpty()
                ? AnswerStatus.GROUNDED
                : hasEligibleMissionForAnyStep(context, availableMinutes)
                ? AnswerStatus.INSUFFICIENT_EVIDENCE
                : AnswerStatus.NO_ELIGIBLE_MISSION;
        var message = switch (status) {
            case GROUNDED -> "As sugestões abaixo são sustentadas por observações persistidas e decisões dos motores.";
            case INSUFFICIENT_EVIDENCE -> "Ainda não há evidência observável suficiente para recomendar uma missão sem inventar justificativa; faça uma coleta diagnóstica ou exploração sem registrar avanço.";
            case NO_ELIGIBLE_MISSION -> "Não existe missão ativa compatível com o currículo, instrumento e tempo informados; o Coach não criou uma alternativa fictícia.";
            case NO_REVIEW_DUE, NOT_CURRENT_RECOMMENDATION ->
                    throw new IllegalStateException("status inesperado para plano diário");
        };
        return new TodayAnswer(status, profileContext(context.profile()), evaluatedAt, availableMinutes,
                goalCitations(context.goals()), recommendations, message);
    }

    @Transactional(readOnly = true)
    public ReviewAnswer whatShouldIReview(String instrumentProfileId) {
        return whatShouldIReview(instrumentProfileId, Instant.now());
    }

    @Transactional(readOnly = true)
    public ReviewAnswer whatShouldIReview(String instrumentProfileId, Instant evaluatedAt) {
        Objects.requireNonNull(evaluatedAt, "evaluatedAt");
        var context = load(instrumentProfileId, evaluatedAt, 20);
        var recommendations = new ArrayList<Recommendation>();
        for (var review : context.navigation().reviews()) {
            var report = evidenceEngine.inspect(context.profile().getId(), review.competencyId(), evaluatedAt);
            var citations = citations(report, context.profile().getId(), review.competencyId());
            if (citations.isEmpty()) continue;
            var step = new CurriculumEngine.NextStep(review.competencyId(), review.title(), review.kind(),
                    review.pathPosition(), review.reason(), List.of());
            var mission = bestMission(context, step, null);
            var matchingGoals = goalCitations(matchingGoals(context.goals(), mission == null
                    ? Set.of(review.competencyId()) : Set.copyOf(mission.getCompetencyIds())));
            recommendations.add(mission == null
                    ? reviewRecommendation(step, report, matchingGoals, citations)
                    : recommendation(mission, step, report, matchingGoals, citations));
        }
        var status = recommendations.isEmpty()
                ? context.navigation().reviews().isEmpty()
                ? AnswerStatus.NO_REVIEW_DUE
                : AnswerStatus.INSUFFICIENT_EVIDENCE
                : AnswerStatus.GROUNDED;
        var message = status == AnswerStatus.GROUNDED
                ? "As revisões estão ordenadas pelo Curriculum Engine e citam as observações que as tornaram necessárias."
                : status == AnswerStatus.INSUFFICIENT_EVIDENCE
                ? "O currículo sinalizou revisão, mas os registros citáveis não estão disponíveis; nenhuma justificativa foi inventada."
                : "Nenhuma revisão está devida no caminho ativo.";
        return new ReviewAnswer(status, profileContext(context.profile()), evaluatedAt, recommendations, message);
    }

    @Transactional(readOnly = true)
    public AdvanceAnswer canIAdvance(String instrumentProfileId, String competencyId, Instant evaluatedAt) {
        Objects.requireNonNull(evaluatedAt, "evaluatedAt");
        var context = load(instrumentProfileId, evaluatedAt, 20);
        var view = context.navigation().competencies().stream()
                .filter(item -> item.competencyId().equals(competencyId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Competência não pertence ao caminho ativo: " + competencyId));
        var report = evidenceEngine.inspect(context.profile().getId(), competencyId, evaluatedAt);
        var citations = citations(report, context.profile().getId(), competencyId);
        if (citations.isEmpty() && view.unlocked()) {
            citations = prerequisiteCitations(context.profile().getId(), competencyId, evaluatedAt);
        }
        var matchingGoals = goalCitations(matchingGoals(context.goals(), Set.of(competencyId)));
        if (citations.isEmpty()) {
            return new AdvanceAnswer(AdvanceDecision.INSUFFICIENT_EVIDENCE, profileContext(context.profile()),
                    competencyId, view.status(), matchingGoals, List.of(),
                    "Não há observação real suficiente para afirmar avanço. O Curriculum Engine informa: "
                            + view.reason());
        }
        var decision = view.status() == CurriculumEngine.CompetencyStatus.BLOCKED
                || view.status() == CurriculumEngine.CompetencyStatus.REVIEW_DUE
                ? AdvanceDecision.NOT_YET : AdvanceDecision.YES_FOR_GUIDED_PRACTICE;
        var explanation = "Curriculum Engine: " + view.reason()
                + " Evidence Engine: " + report.confidence() + ". Evidências citadas: "
                + evidenceIds(citations) + ".";
        return new AdvanceAnswer(decision, profileContext(context.profile()), competencyId, view.status(),
                matchingGoals, citations, explanation);
    }

    @Transactional(readOnly = true)
    public AdvanceAnswer canIAdvance(String instrumentProfileId, Instant evaluatedAt) {
        var context = load(instrumentProfileId, evaluatedAt, 5);
        var focus = context.navigation().position().focusCompetencyId();
        if (focus == null) {
            return new AdvanceAnswer(AdvanceDecision.INSUFFICIENT_EVIDENCE, profileContext(context.profile()),
                    null, null, List.of(), List.of(), context.navigation().position().explanation());
        }
        return canIAdvance(instrumentProfileId, focus, evaluatedAt);
    }

    @Transactional(readOnly = true)
    public MissionAnswer whyDidIReceiveMission(
            String instrumentProfileId, String missionId, Instant evaluatedAt) {
        Objects.requireNonNull(evaluatedAt, "evaluatedAt");
        var context = load(instrumentProfileId, evaluatedAt, 20);
        var mission = context.missions().stream().filter(item -> item.getId().equals(missionId))
                .findFirst().orElseThrow(() -> new NotFoundException(
                        "Missão ativa não encontrada no currículo do perfil: " + missionId));
        var step = context.navigation().nextSteps().stream()
                .filter(item -> mission.getCompetencyIds().contains(item.competencyId()))
                .findFirst().orElse(null);
        if (step == null) {
            return new MissionAnswer(AnswerStatus.NOT_CURRENT_RECOMMENDATION, profileContext(context.profile()),
                    missionId, null,
                    "A missão existe, mas não corresponde a uma necessidade atual indicada pelo Curriculum Engine.");
        }
        var report = evidenceEngine.inspect(context.profile().getId(), step.competencyId(), evaluatedAt);
        var citations = citationsForStep(context, step, report, evaluatedAt);
        if (citations.isEmpty()) {
            return new MissionAnswer(AnswerStatus.INSUFFICIENT_EVIDENCE, profileContext(context.profile()),
                    missionId, null,
                    "Não há observação persistida suficiente para justificar esta missão sem inventar causalidade.");
        }
        var matchingGoals = goalCitations(matchingGoals(context.goals(), Set.copyOf(mission.getCompetencyIds())));
        return new MissionAnswer(AnswerStatus.GROUNDED, profileContext(context.profile()), missionId,
                recommendation(mission, step, report, matchingGoals, citations),
                "A justificativa foi reconstruída exclusivamente a partir dos motores, da missão e das evidências citadas.");
    }

    private Context load(String instrumentProfileId, Instant evaluatedAt, int suggestionLimit) {
        if (instrumentProfileId == null || instrumentProfileId.isBlank()) {
            throw new IllegalArgumentException("instrumentProfileId é obrigatório");
        }
        var profile = profiles.findById(instrumentProfileId)
                .orElseThrow(() -> new NotFoundException("Perfil instrumental não encontrado: " + instrumentProfileId));
        if (!profile.isActive()) throw new IllegalStateException("Perfil instrumental está inativo");
        var navigation = curriculumEngine.navigate(instrumentProfileId, evaluatedAt, suggestionLimit);
        if (!Objects.equals(profile.getCurriculumId(), navigation.curriculumId())) {
            throw new IllegalStateException("Currículo do perfil diverge do caminho ativo");
        }
        var activeGoals = goals.findByInstrumentProfileIdAndStatus(instrumentProfileId, LearningGoal.Status.ACTIVE)
                .stream().filter(goal -> goal.getCurriculumId() == null
                        || goal.getCurriculumId().equals(navigation.curriculumId())).toList();
        var activeMissions = missions.findByCurriculumIdAndStatus(
                        navigation.curriculumId(), Mission.Status.ACTIVE).stream()
                .filter(mission -> mission.getInstrument() == null
                        || mission.getInstrument() == profile.getInstrument()).toList();
        return new Context(profile, navigation, activeGoals, activeMissions);
    }

    private Mission bestMission(Context context, CurriculumEngine.NextStep step, Integer availableMinutes) {
        var reviewNeeded = step.kind() == CurriculumEngine.SuggestionKind.REVIEW
                || step.kind() == CurriculumEngine.SuggestionKind.REVALIDATION;
        return context.missions().stream()
                .filter(mission -> mission.getCompetencyIds().contains(step.competencyId()))
                .filter(mission -> missionCompetenciesAreAvailable(context, mission))
                .filter(mission -> availableMinutes == null || mission.getEstimatedMinutes() <= availableMinutes)
                .sorted(Comparator
                        .comparing((Mission mission) -> mission.getId().contains("-review") != reviewNeeded)
                        .thenComparing((Mission mission) -> matchingGoals(
                                context.goals(), Set.copyOf(mission.getCompetencyIds())).isEmpty())
                        .thenComparingInt(Mission::getEstimatedMinutes)
                        .thenComparing(Mission::getId))
                .findFirst().orElse(null);
    }

    private boolean missionCompetenciesAreAvailable(Context context, Mission mission) {
        var views = context.navigation().competencies().stream().collect(Collectors.toMap(
                CurriculumEngine.CompetencyView::competencyId, Function.identity()));
        return mission.getCompetencyIds().stream().allMatch(competencyId -> {
            var view = views.get(competencyId);
            return view != null && view.unlocked()
                    && view.status() != CurriculumEngine.CompetencyStatus.BLOCKED;
        });
    }

    private boolean hasEligibleMissionForAnyStep(Context context, Integer availableMinutes) {
        return context.navigation().nextSteps().stream()
                .anyMatch(step -> bestMission(context, step, availableMinutes) != null);
    }

    private List<EvidenceCitation> citationsForStep(
            Context context, CurriculumEngine.NextStep step, EvidenceEngine.ConfidenceReport report,
            Instant evaluatedAt) {
        var direct = citations(report, context.profile().getId(), step.competencyId());
        if (!direct.isEmpty() || step.kind() != CurriculumEngine.SuggestionKind.INTRODUCTION) return direct;
        var diagnostic = evidence.findByInstrumentProfileIdAndCompetencyIdOrderByOccurredAtDesc(
                        context.profile().getId(), step.competencyId()).stream()
                .filter(item -> item.getSourceType() == Evidence.SourceType.DIAGNOSTIC)
                .limit(1).map(item -> evidenceCitation(item, null)).toList();
        if (!diagnostic.isEmpty()) return diagnostic;
        return prerequisiteCitations(context.profile().getId(), step.competencyId(), evaluatedAt);
    }

    private List<EvidenceCitation> prerequisiteCitations(
            String instrumentProfileId, String competencyId, Instant evaluatedAt) {
        var result = new ArrayList<EvidenceCitation>();
        curriculumEngine.findPrerequisites(instrumentProfileId, competencyId, evaluatedAt).stream()
                .filter(CurriculumEngine.PrerequisiteView::satisfied)
                .forEach(prerequisite -> result.addAll(citations(
                        evidenceEngine.inspect(instrumentProfileId, prerequisite.competencyId(), evaluatedAt),
                        instrumentProfileId, prerequisite.competencyId())));
        return distinctCitations(result);
    }

    private List<EvidenceCitation> citations(
            EvidenceEngine.ConfidenceReport report, String instrumentProfileId, String competencyId) {
        var contributionById = report.contributions().stream().collect(Collectors.toMap(
                EvidenceEngine.EvidenceContribution::evidenceId, Function.identity(), (first, ignored) -> first));
        var ids = new LinkedHashSet<String>();
        ids.addAll(report.supportingEvidenceIds());
        ids.addAll(report.limitingEvidenceIds());
        report.contributions().stream()
                .filter(item -> item.effectiveState() == Evidence.State.AGED
                        || item.effectiveState() == Evidence.State.CONTRADICTORY)
                .map(EvidenceEngine.EvidenceContribution::evidenceId).forEach(ids::add);
        if (ids.isEmpty()) return List.of();
        return evidence.findByInstrumentProfileIdAndCompetencyIdOrderByOccurredAtDesc(
                        instrumentProfileId, competencyId).stream()
                .filter(item -> ids.contains(item.getId()))
                .map(item -> evidenceCitation(item, contributionById.get(item.getId())))
                .toList();
    }

    private EvidenceCitation evidenceCitation(
            Evidence item, EvidenceEngine.EvidenceContribution contribution) {
        var effectiveState = contribution == null ? item.getState() : contribution.effectiveState();
        return new EvidenceCitation(item.getId(), item.getCompetencyId(), item.getCriterionKey(), item.getType(),
                item.getState(), effectiveState, item.getReliability(), item.getResult(), item.getSourceType(),
                item.getSourceId(), item.getOccurredAt(), item.getObservation(), item.getConditions());
    }

    private List<EvidenceCitation> distinctCitations(List<EvidenceCitation> citations) {
        return citations.stream().collect(Collectors.toMap(EvidenceCitation::evidenceId, Function.identity(),
                        (first, ignored) -> first, java.util.LinkedHashMap::new))
                .values().stream().toList();
    }

    private Recommendation recommendation(
            Mission mission, CurriculumEngine.NextStep step, EvidenceEngine.ConfidenceReport report,
            List<GoalCitation> matchingGoals, List<EvidenceCitation> citations) {
        var explanation = "Curriculum Engine: " + step.reason()
                + " Evidence Engine para " + step.competencyId() + ": " + report.confidence() + "; "
                + report.nextObservation() + " Evidências citadas: " + evidenceIds(citations) + "."
                + goalExplanation(matchingGoals);
        return new Recommendation(mission.getId(), mission.getTitle(), step.competencyId(), step.kind(),
                mission.getEstimatedMinutes(), mission.getObservableObjective(), mission.getExpectedEvidence(),
                matchingGoals, citations, explanation);
    }

    private Recommendation reviewRecommendation(
            CurriculumEngine.NextStep step, EvidenceEngine.ConfidenceReport report,
            List<GoalCitation> matchingGoals, List<EvidenceCitation> citations) {
        var explanation = "Curriculum Engine: " + step.reason()
                + " Evidence Engine: " + report.confidence() + "; " + report.nextObservation()
                + " Evidências citadas: " + evidenceIds(citations) + "." + goalExplanation(matchingGoals);
        return new Recommendation(null, "Revisar " + step.title(), step.competencyId(), step.kind(), null,
                report.nextObservation(), "Nova observação válida para reavaliar a hipótese atual.",
                matchingGoals, citations, explanation);
    }

    private String goalExplanation(List<GoalCitation> matchingGoals) {
        return matchingGoals.isEmpty() ? ""
                : " Objetivos declarados relacionados: "
                + matchingGoals.stream().map(goal -> goal.goalId() + " (" + goal.title() + ")").toList() + ".";
    }

    private String evidenceIds(List<EvidenceCitation> citations) {
        return citations.stream().map(EvidenceCitation::evidenceId).toList().toString();
    }

    private List<LearningGoal> matchingGoals(List<LearningGoal> activeGoals, Set<String> competencyIds) {
        return activeGoals.stream().filter(goal -> goal.getPriorityCompetencyIds().stream()
                .anyMatch(competencyIds::contains)).toList();
    }

    private List<GoalCitation> goalCitations(List<LearningGoal> activeGoals) {
        return activeGoals.stream().map(goal -> new GoalCitation(
                goal.getId(), goal.getTitle(), goal.getDesiredOutcome(), goal.getMusicalContext(),
                goal.getType(), goal.getPriority(), goal.getTargetDate())).toList();
    }

    private ProfileContext profileContext(InstrumentProfile profile) {
        return new ProfileContext(profile.getId(), profile.getInstrument(), profile.getCurrentStage(),
                profile.getCurriculumId());
    }
}
