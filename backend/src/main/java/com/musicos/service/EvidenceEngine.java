package com.musicos.service;

import com.musicos.domain.Competency;
import com.musicos.domain.Evidence;
import com.musicos.domain.InstrumentProfile;
import com.musicos.domain.Mastery;
import com.musicos.repository.CompetencyRepository;
import com.musicos.repository.EvidenceRepository;
import com.musicos.repository.InstrumentProfileRepository;
import com.musicos.repository.MasteryRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EvidenceEngine {
    public enum Confidence {
        NO_ADMISSIBLE_EVIDENCE,
        PRELIMINARY,
        DEVELOPING,
        SUPPORTED_CONTROLLED,
        SUPPORTED_IN_APPLICATION,
        SUPPORTED_AND_RETAINED,
        POLICY_INCOMPLETE,
        CONFLICTED,
        REVALIDATION_REQUIRED
    }

    public record Observation(
            String instrumentProfileId,
            String competencyId,
            String criterionKey,
            Evidence.Type type,
            Evidence.State state,
            Evidence.FunctionalWeight functionalWeight,
            Evidence.Reliability reliability,
            Evidence.Result result,
            Evidence.SourceType sourceType,
            String sourceId,
            String independenceKey,
            int challengeLevel,
            String observation,
            String conditions,
            String protocolVersion,
            String analyzerVersion,
            String artifactReference,
            String supersedesEvidenceId,
            Instant occurredAt,
            Instant validUntil) {
    }

    public record EvidenceContribution(
            String evidenceId,
            String criterionKey,
            Evidence.State recordedState,
            Evidence.State effectiveState,
            Evidence.FunctionalWeight functionalWeight,
            Evidence.Reliability reliability,
            Evidence.Result result,
            boolean contributes,
            boolean primary,
            String reason) {
    }

    public record WeightSummary(
            int required,
            int primary,
            int corroborating,
            int contextual,
            int excluded) {
    }

    public record ReliabilitySummary(int high, int moderate, int low) {
    }

    public record ConfidenceReport(
            String instrumentProfileId,
            String competencyId,
            String evidencePolicyVersion,
            boolean policyComplete,
            Mastery.State masteryState,
            Confidence confidence,
            List<String> coveredMandatoryCriteria,
            List<String> missingMandatoryCriteria,
            int independentPrimaryEvidenceCount,
            boolean applicationObserved,
            boolean transferObserved,
            boolean retentionObserved,
            boolean unresolvedConflict,
            Instant lastEvidenceAt,
            Instant nextReviewAt,
            WeightSummary weights,
            ReliabilitySummary reliability,
            List<String> supportingEvidenceIds,
            List<String> limitingEvidenceIds,
            List<String> excludedEvidenceIds,
            List<EvidenceContribution> contributions,
            String rationale,
            String nextObservation) {
        public ConfidenceReport {
            coveredMandatoryCriteria = List.copyOf(coveredMandatoryCriteria);
            missingMandatoryCriteria = List.copyOf(missingMandatoryCriteria);
            supportingEvidenceIds = List.copyOf(supportingEvidenceIds);
            limitingEvidenceIds = List.copyOf(limitingEvidenceIds);
            excludedEvidenceIds = List.copyOf(excludedEvidenceIds);
            contributions = List.copyOf(contributions);
        }
    }

    public record CollectionResult(String evidenceId, ConfidenceReport report) {
    }

    private record Context(InstrumentProfile profile, Competency competency, List<Evidence> evidence,
                           Mastery mastery) {
    }

    private record Classified(Evidence evidence, Evidence.State effectiveState, boolean contributes,
                              boolean primary, Instant effectiveValidUntil, String reason) {
    }

    private final InstrumentProfileRepository profiles;
    private final CompetencyRepository competencies;
    private final EvidenceRepository evidence;
    private final MasteryRepository mastery;

    public EvidenceEngine(InstrumentProfileRepository profiles, CompetencyRepository competencies,
                          EvidenceRepository evidence, MasteryRepository mastery) {
        this.profiles = profiles;
        this.competencies = competencies;
        this.evidence = evidence;
        this.mastery = mastery;
    }

    @Transactional
    public CollectionResult collect(Observation observation) {
        Objects.requireNonNull(observation, "observation");
        var profile = requireProfile(observation.instrumentProfileId());
        var competency = requireCompetency(observation.competencyId());
        validateObservation(observation, profile, competency);
        var validUntil = boundedValidity(observation.occurredAt(), observation.validUntil(), competency);
        var collected = evidence.save(new Evidence(
                observation.instrumentProfileId(), observation.competencyId(), observation.criterionKey(),
                observation.type(), observation.state(), observation.functionalWeight(), observation.reliability(),
                observation.result(), observation.sourceType(), observation.sourceId(), observation.independenceKey(),
                observation.challengeLevel(), observation.observation(), observation.conditions(),
                observation.protocolVersion(), observation.analyzerVersion(), observation.artifactReference(),
                observation.supersedesEvidenceId(), observation.occurredAt(), validUntil));
        var report = recalculateInternal(profile, competency, Instant.now());
        return new CollectionResult(collected.getId(), report);
    }

    @Transactional
    public ConfidenceReport recalculate(String instrumentProfileId, String competencyId, Instant evaluatedAt) {
        Objects.requireNonNull(evaluatedAt, "evaluatedAt");
        return recalculateInternal(requireProfile(instrumentProfileId), requireCompetency(competencyId), evaluatedAt);
    }

    @Transactional(readOnly = true)
    public ConfidenceReport inspect(String instrumentProfileId, String competencyId, Instant evaluatedAt) {
        Objects.requireNonNull(evaluatedAt, "evaluatedAt");
        var profile = requireProfile(instrumentProfileId);
        var competency = requireCompetency(competencyId);
        validateProfileCompetency(profile, competency);
        var currentMastery = mastery.findByInstrumentProfileIdAndCompetencyId(profile.getId(), competency.getId())
                .orElse(null);
        var items = evidence.findByInstrumentProfileIdAndCompetencyIdOrderByOccurredAtDesc(
                profile.getId(), competency.getId());
        return calculate(profile, competency, items, currentMastery, evaluatedAt).report();
    }

    private ConfidenceReport recalculateInternal(
            InstrumentProfile profile, Competency competency, Instant evaluatedAt) {
        validateProfileCompetency(profile, competency);
        var items = evidence.findByInstrumentProfileIdAndCompetencyIdOrderByOccurredAtDesc(
                profile.getId(), competency.getId());
        var currentMastery = mastery.findByInstrumentProfileIdAndCompetencyId(profile.getId(), competency.getId())
                .orElseGet(() -> new Mastery(profile.getId(), competency.getId(), policyVersion(competency)));
        var calculated = calculate(profile, competency, items, currentMastery, evaluatedAt);
        var report = calculated.report();
        currentMastery.applyEvidencePolicyVersion(report.evidencePolicyVersion());
        currentMastery.reviseHypothesis(
                report.masteryState(), report.policyComplete() && report.missingMandatoryCriteria().isEmpty(),
                report.independentPrimaryEvidenceCount(), report.applicationObserved(), report.transferObserved(),
                report.retentionObserved(), report.unresolvedConflict(), report.rationale(), report.lastEvidenceAt(),
                report.nextReviewAt(), report.supportingEvidenceIds(), report.limitingEvidenceIds());
        mastery.save(currentMastery);
        return report;
    }

    private Calculated calculate(InstrumentProfile profile, Competency competency, List<Evidence> items,
                                 Mastery currentMastery, Instant evaluatedAt) {
        var supersededIds = items.stream().map(Evidence::getSupersedesEvidenceId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        var classified = items.stream()
                .map(item -> classify(item, competency, supersededIds, evaluatedAt))
                .toList();
        var strong = classified.stream().filter(Classified::contributes).filter(Classified::primary).toList();
        var supportingStrong = strong.stream()
                .filter(item -> item.evidence().getResult() == Evidence.Result.SUPPORTS).toList();
        var limitingStrong = strong.stream()
                .filter(item -> item.evidence().getResult() == Evidence.Result.CHALLENGES).toList();
        var contributingSupport = classified.stream().filter(Classified::contributes)
                .filter(item -> item.evidence().getResult() == Evidence.Result.SUPPORTS).toList();
        var explicitConflict = classified.stream().anyMatch(item ->
                item.effectiveState() == Evidence.State.CONTRADICTORY
                        && item.evidence().getReliability() != Evidence.Reliability.LOW);
        var comparableConflict = hasComparableConflict(supportingStrong, limitingStrong);
        var negativePattern = limitingStrong.stream().map(item -> item.evidence().getIndependenceKey())
                .distinct().count() >= 2;
        var conflict = explicitConflict || comparableConflict;

        var covered = competency.getMandatoryCriterionKeys().stream()
                .filter(criterion -> supportingStrong.stream()
                        .anyMatch(item -> item.evidence().getCriterionKey().equals(criterion)))
                .toList();
        var missing = competency.getMandatoryCriterionKeys().stream()
                .filter(criterion -> !covered.contains(criterion)).toList();
        var independentCount = (int) supportingStrong.stream()
                .map(item -> item.evidence().getIndependenceKey()).distinct().count();
        var applicationObserved = supportsType(supportingStrong, Evidence.Type.APPLICATION);
        var transferObserved = supportsType(supportingStrong, Evidence.Type.TRANSFER);
        var retentionObserved = supportsType(supportingStrong, Evidence.Type.RETENTION);
        var agedStrong = classified.stream().filter(item ->
                item.effectiveState() == Evidence.State.AGED && isPrimaryRole(item.evidence().getFunctionalWeight())
                        && item.evidence().getResult() == Evidence.Result.SUPPORTS).toList();
        var needsTemporalRevalidation = agedStrong.stream().anyMatch(item ->
                competency.getMandatoryCriterionKeys().contains(item.evidence().getCriterionKey())
                        && missing.contains(item.evidence().getCriterionKey()))
                || !agedStrong.isEmpty() && supportingStrong.isEmpty();
        var policyComplete = competency.hasCompleteEvidencePolicy();
        var state = masteryState(policyComplete, independentCount, missing, applicationObserved,
                retentionObserved, contributingSupport, limitingStrong, conflict, negativePattern,
                needsTemporalRevalidation, currentMastery);
        var confidence = confidence(state, policyComplete, conflict);
        var supportingIds = contributingSupport.stream().map(item -> item.evidence().getId()).distinct().toList();
        var limitingIds = classified.stream()
                .filter(item -> item.evidence().getResult() == Evidence.Result.CHALLENGES
                        || item.effectiveState() == Evidence.State.CONTRADICTORY)
                .filter(item -> item.evidence().getReliability() != Evidence.Reliability.LOW)
                .map(item -> item.evidence().getId()).distinct().toList();
        var excludedIds = classified.stream().filter(item -> !item.contributes())
                .map(item -> item.evidence().getId()).distinct().toList();
        var lastEvidenceAt = classified.stream().filter(item -> item.contributes()
                        || item.effectiveState() == Evidence.State.CONTRADICTORY)
                .map(item -> item.evidence().getOccurredAt()).max(Comparator.naturalOrder()).orElse(null);
        var nextReviewAt = supportingStrong.stream().map(Classified::effectiveValidUntil)
                .filter(Objects::nonNull).min(Comparator.naturalOrder()).orElse(null);
        var weightSummary = weightSummary(classified);
        var reliabilitySummary = reliabilitySummary(items);
        var rationale = rationale(policyComplete, covered, missing, independentCount, applicationObserved,
                transferObserved, retentionObserved, conflict, negativePattern, needsTemporalRevalidation,
                supportingIds, limitingIds);
        var contributions = classified.stream().map(this::contribution).toList();
        var report = new ConfidenceReport(profile.getId(), competency.getId(), policyVersion(competency),
                policyComplete, state, confidence, covered, missing, independentCount, applicationObserved,
                transferObserved, retentionObserved, conflict || negativePattern, lastEvidenceAt, nextReviewAt,
                weightSummary, reliabilitySummary, supportingIds, limitingIds, excludedIds, contributions,
                rationale, nextObservation(policyComplete, missing, independentCount, applicationObserved,
                        retentionObserved, conflict || negativePattern, needsTemporalRevalidation));
        return new Calculated(report);
    }

    private Mastery.State masteryState(
            boolean policyComplete, int independentCount, List<String> missing, boolean applicationObserved,
            boolean retentionObserved, List<Classified> contributingSupport, List<Classified> limitingStrong,
            boolean conflict, boolean negativePattern, boolean temporalRevalidation, Mastery currentMastery) {
        if (conflict || negativePattern || temporalRevalidation) return Mastery.State.REVALIDATION_NEEDED;
        if (!policyComplete) {
            if (independentCount > 0 || !limitingStrong.isEmpty()) return Mastery.State.DEVELOPING;
            if (!contributingSupport.isEmpty()) return Mastery.State.INITIAL_HYPOTHESIS;
            return Mastery.State.UNOBSERVED;
        }
        var criteriaCovered = missing.isEmpty();
        if (retentionObserved && criteriaCovered && independentCount >= 2 && applicationObserved) {
            return Mastery.State.RETAINED;
        }
        if (criteriaCovered && independentCount >= 2 && applicationObserved) {
            return Mastery.State.PROBABLE_MASTERY_APPLICATION;
        }
        if (independentCount >= 2) return Mastery.State.CONSISTENT_CONTROLLED;
        if (independentCount > 0 || !limitingStrong.isEmpty()) return Mastery.State.DEVELOPING;
        if (!contributingSupport.isEmpty()) return Mastery.State.INITIAL_HYPOTHESIS;
        if (currentMastery != null && isEstablished(currentMastery.getState())) {
            return currentMastery.getState();
        }
        return Mastery.State.UNOBSERVED;
    }

    private Confidence confidence(Mastery.State state, boolean policyComplete, boolean conflict) {
        if (conflict) return Confidence.CONFLICTED;
        if (state == Mastery.State.REVALIDATION_NEEDED) return Confidence.REVALIDATION_REQUIRED;
        if (!policyComplete) return Confidence.POLICY_INCOMPLETE;
        return switch (state) {
            case UNOBSERVED -> Confidence.NO_ADMISSIBLE_EVIDENCE;
            case INITIAL_HYPOTHESIS -> Confidence.PRELIMINARY;
            case DEVELOPING -> Confidence.DEVELOPING;
            case CONSISTENT_CONTROLLED -> Confidence.SUPPORTED_CONTROLLED;
            case PROBABLE_MASTERY_APPLICATION -> Confidence.SUPPORTED_IN_APPLICATION;
            case RETAINED -> Confidence.SUPPORTED_AND_RETAINED;
            case REVALIDATION_NEEDED -> Confidence.REVALIDATION_REQUIRED;
        };
    }

    private Classified classify(Evidence item, Competency competency, Set<String> supersededIds, Instant evaluatedAt) {
        var effectiveValidUntil = boundedValidity(item.getOccurredAt(), item.getValidUntil(), competency);
        if (supersededIds.contains(item.getId())) {
            return new Classified(item, Evidence.State.SUPERSEDED, false, false, effectiveValidUntil,
                    "Substitu�da por uma observa��o posterior audit�vel.");
        }
        if (item.getState() == Evidence.State.INVALID || item.getState() == Evidence.State.SUPERSEDED) {
            return new Classified(item, item.getState(), false, false, effectiveValidUntil,
                    "Estado da evid�ncia n�o permite uso na hip�tese.");
        }
        if (item.getState() == Evidence.State.PROVISIONAL || item.getState() == Evidence.State.CONTESTED) {
            return new Classified(item, item.getState(), false, false, effectiveValidUntil,
                    "A observa��o ainda precisa de valida��o ou resolu��o.");
        }
        if (item.getState() == Evidence.State.AGED
                || effectiveValidUntil != null && evaluatedAt.isAfter(effectiveValidUntil)) {
            return new Classified(item, Evidence.State.AGED, false, false, effectiveValidUntil,
                    "A observa��o permanece hist�rica, mas perdeu validade para inferir o estado atual.");
        }
        if (item.getState() == Evidence.State.CONTRADICTORY) {
            return new Classified(item, Evidence.State.CONTRADICTORY, false, false, effectiveValidUntil,
                    "A observa��o explicita conflito e exige interpreta��o antes de contribuir.");
        }
        if (item.getFunctionalWeight() == Evidence.FunctionalWeight.NOT_ADMISSIBLE) {
            return new Classified(item, item.getState(), false, false, effectiveValidUntil,
                    "A pol�tica classifica esta fonte como n�o admiss�vel para a conclus�o.");
        }
        if (item.getReliability() == Evidence.Reliability.LOW) {
            return new Classified(item, item.getState(), false, false, effectiveValidUntil,
                    "Confiabilidade baixa: resultado preservado, mas n�o interpretado como acerto ou falha musical.");
        }
        if (item.getResult() == Evidence.Result.INCONCLUSIVE) {
            return new Classified(item, item.getState(), false, false, effectiveValidUntil,
                    "Resultado inconclusivo n�o altera a hip�tese de dom�nio.");
        }
        var primary = isPrimaryRole(item.getFunctionalWeight()) && item.getState() == Evidence.State.VALID;
        var contributes = item.getFunctionalWeight() != Evidence.FunctionalWeight.CONTEXTUAL;
        var reason = primary
                ? "Observa��o prim�ria admiss�vel sob a pol�tica declarada."
                : contributes
                ? "Observa��o limitada ou corroboradora; informa sem sustentar dom�nio sozinha."
                : "Evid�ncia contextual preservada apenas para interpreta��o.";
        return new Classified(item, item.getState(), contributes, primary, effectiveValidUntil, reason);
    }

    private boolean hasComparableConflict(List<Classified> supports, List<Classified> challenges) {
        return supports.stream().anyMatch(support -> challenges.stream().anyMatch(challenge ->
                support.evidence().getCriterionKey().equals(challenge.evidence().getCriterionKey())
                        && !support.evidence().getIndependenceKey().equals(challenge.evidence().getIndependenceKey())
                        && support.evidence().getChallengeLevel() == challenge.evidence().getChallengeLevel()
                        && normalize(support.evidence().getConditions())
                        .equals(normalize(challenge.evidence().getConditions()))));
    }

    private String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

    private boolean supportsType(List<Classified> evidence, Evidence.Type type) {
        return evidence.stream().anyMatch(item -> item.evidence().getType() == type);
    }

    private boolean isPrimaryRole(Evidence.FunctionalWeight weight) {
        return weight == Evidence.FunctionalWeight.REQUIRED || weight == Evidence.FunctionalWeight.PRIMARY;
    }

    private boolean isEstablished(Mastery.State state) {
        return state == Mastery.State.PROBABLE_MASTERY_APPLICATION || state == Mastery.State.RETAINED;
    }

    private WeightSummary weightSummary(List<Classified> classified) {
        return new WeightSummary(
                countWeight(classified, Evidence.FunctionalWeight.REQUIRED),
                countWeight(classified, Evidence.FunctionalWeight.PRIMARY),
                countWeight(classified, Evidence.FunctionalWeight.CORROBORATING),
                countWeight(classified, Evidence.FunctionalWeight.CONTEXTUAL),
                (int) classified.stream().filter(item -> !item.contributes()).count());
    }

    private int countWeight(List<Classified> classified, Evidence.FunctionalWeight weight) {
        return (int) classified.stream().filter(Classified::contributes)
                .filter(item -> item.evidence().getFunctionalWeight() == weight).count();
    }

    private ReliabilitySummary reliabilitySummary(List<Evidence> items) {
        var counts = items.stream().collect(Collectors.groupingBy(
                Evidence::getReliability, Collectors.counting()));
        return new ReliabilitySummary(
                counts.getOrDefault(Evidence.Reliability.HIGH, 0L).intValue(),
                counts.getOrDefault(Evidence.Reliability.MODERATE, 0L).intValue(),
                counts.getOrDefault(Evidence.Reliability.LOW, 0L).intValue());
    }

    private EvidenceContribution contribution(Classified item) {
        return new EvidenceContribution(item.evidence().getId(), item.evidence().getCriterionKey(),
                item.evidence().getState(), item.effectiveState(), item.evidence().getFunctionalWeight(),
                item.evidence().getReliability(), item.evidence().getResult(), item.contributes(), item.primary(),
                item.reason());
    }

    private String rationale(boolean policyComplete, List<String> covered, List<String> missing,
                             int independentCount, boolean application, boolean transfer, boolean retention,
                             boolean conflict, boolean negativePattern, boolean temporalRevalidation,
                             List<String> supportingIds, List<String> limitingIds) {
        var parts = new ArrayList<String>();
        parts.add(policyComplete
                ? "Pol�tica configurada e avaliada por categorias funcionais."
                : "Pol�tica incompleta; as observa��es n�o podem sustentar conclus�o de dom�nio prov�vel.");
        parts.add("Crit�rios obrigat�rios cobertos: " + covered + "; ausentes: " + missing + ".");
        parts.add("Evid�ncias prim�rias independentes atuais: " + independentCount + ".");
        parts.add("Aplica��o: " + observed(application) + "; transfer�ncia: " + observed(transfer)
                + "; reten��o: " + observed(retention) + ".");
        if (conflict) parts.add("Conflito compar�vel ou explicitamente declarado permanece sem resolu��o.");
        if (negativePattern) parts.add("Resultados limitantes independentes pedem reavalia��o focal.");
        if (temporalRevalidation) parts.add("As observa��es prim�rias dispon�veis envelheceram.");
        parts.add("Sustentada por " + supportingIds.size() + " observa��es admiss�veis e limitada por "
                + limitingIds.size() + ".");
        return String.join(" ", parts);
    }

    private String observed(boolean value) {
        return value ? "observada" : "ainda n�o observada";
    }

    private String nextObservation(boolean policyComplete, List<String> missing, int independentCount,
                                   boolean application, boolean retention, boolean conflict,
                                   boolean temporalRevalidation) {
        if (!policyComplete) return "Configurar e calibrar a pol�tica da compet�ncia antes de uma conclus�o forte.";
        if (conflict) return "Realizar avalia��o focal sob condi��es compar�veis para investigar o conflito.";
        if (temporalRevalidation) return "Obter uma observa��o curta e atual de revalida��o.";
        if (!missing.isEmpty()) return "Observar diretamente o crit�rio obrigat�rio: " + missing.getFirst() + ".";
        if (independentCount < 2) return "Obter outra observa��o prim�ria em momento ou contexto independente.";
        if (!application) return "Observar a compet�ncia em aplica��o musical relevante.";
        if (!retention) return "Reobservar ap�s o intervalo definido pela pol�tica para investigar reten��o.";
        return "Manter uso em contextos musicais variados e revalidar quando a pol�tica indicar.";
    }

    private void validateObservation(
            Observation observation, InstrumentProfile profile, Competency competency) {
        validateProfileCompetency(profile, competency);
        requiredText(observation.criterionKey(), "criterionKey");
        Objects.requireNonNull(observation.type(), "type");
        Objects.requireNonNull(observation.state(), "state");
        Objects.requireNonNull(observation.functionalWeight(), "functionalWeight");
        Objects.requireNonNull(observation.reliability(), "reliability");
        Objects.requireNonNull(observation.result(), "result");
        Objects.requireNonNull(observation.sourceType(), "sourceType");
        requiredText(observation.sourceId(), "sourceId");
        requiredText(observation.independenceKey(), "independenceKey");
        requiredText(observation.observation(), "observation");
        requiredText(observation.conditions(), "conditions");
        Objects.requireNonNull(observation.occurredAt(), "occurredAt");
        if (observation.challengeLevel() < 1 || observation.challengeLevel() > 5) {
            throw new IllegalArgumentException("challengeLevel deve estar entre 1 e 5");
        }
        if (observation.occurredAt().isAfter(Instant.now())) {
            throw new IllegalArgumentException("occurredAt n�o pode estar no futuro");
        }
        if (observation.validUntil() != null && !observation.validUntil().isAfter(observation.occurredAt())) {
            throw new IllegalArgumentException("validUntil deve ser posterior a occurredAt");
        }
        if (observation.state() == Evidence.State.AGED || observation.state() == Evidence.State.SUPERSEDED) {
            throw new IllegalArgumentException("AGED e SUPERSEDED s�o estados derivados pelo motor");
        }
        if (isPrimaryRole(observation.functionalWeight()) && observation.state() == Evidence.State.VALID
                && isBlank(observation.protocolVersion())) {
            throw new IllegalArgumentException("evid�ncia prim�ria v�lida exige protocolVersion");
        }
        if (isNonPerformanceType(observation.type()) && isPrimaryRole(observation.functionalWeight())) {
            throw new IllegalArgumentException("evid�ncia declarativa, contextual ou de processo n�o sustenta dom�nio sozinha");
        }
        validateSupersession(observation);
    }

    private void validateSupersession(Observation observation) {
        if (isBlank(observation.supersedesEvidenceId())) return;
        var previous = evidence.findById(observation.supersedesEvidenceId())
                .orElseThrow(() -> new NotFoundException("Evid�ncia substitu�da n�o encontrada"));
        if (!previous.getInstrumentProfileId().equals(observation.instrumentProfileId())
                || !previous.getCompetencyId().equals(observation.competencyId())
                || !previous.getCriterionKey().equals(observation.criterionKey())) {
            throw new IllegalArgumentException("substitui��o deve preservar perfil, compet�ncia e crit�rio");
        }
        if (observation.occurredAt().isBefore(previous.getOccurredAt())) {
            throw new IllegalArgumentException("evid�ncia substituta n�o pode anteceder a observa��o original");
        }
    }

    private boolean isNonPerformanceType(Evidence.Type type) {
        return type == Evidence.Type.DECLARATIVE || type == Evidence.Type.CONTEXTUAL
                || type == Evidence.Type.PROCESS;
    }

    private void validateProfileCompetency(InstrumentProfile profile, Competency competency) {
        if (!profile.isActive()) throw new IllegalStateException("Perfil instrumental est� inativo");
        if (!competency.isActive()) throw new IllegalStateException("Compet�ncia est� inativa");
        if (!competency.getInstruments().isEmpty()
                && !competency.getInstruments().contains(profile.getInstrument())) {
            throw new IllegalArgumentException("Compet�ncia n�o pertence ao instrumento do perfil");
        }
    }

    private Instant boundedValidity(Instant occurredAt, Instant requested, Competency competency) {
        var policyLimit = competency.getRetentionWindowDays() == null ? null
                : occurredAt.plus(competency.getRetentionWindowDays(), ChronoUnit.DAYS);
        if (requested == null) return policyLimit;
        if (policyLimit == null || requested.isBefore(policyLimit)) return requested;
        return policyLimit;
    }

    private InstrumentProfile requireProfile(String id) {
        requiredText(id, "instrumentProfileId");
        return profiles.findById(id)
                .orElseThrow(() -> new NotFoundException("Perfil instrumental n�o encontrado: " + id));
    }

    private Competency requireCompetency(String id) {
        requiredText(id, "competencyId");
        return competencies.findById(id)
                .orElseThrow(() -> new NotFoundException("Compet�ncia n�o encontrada: " + id));
    }

    private String policyVersion(Competency competency) {
        return isBlank(competency.getEvidencePolicyKey()) ? "unconfigured" : competency.getEvidencePolicyKey();
    }

    private void requiredText(String value, String field) {
        if (isBlank(value)) throw new IllegalArgumentException(field + " � obrigat�rio");
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private record Calculated(ConfidenceReport report) {
    }
}
