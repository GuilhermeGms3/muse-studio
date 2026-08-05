package com.musicos.service;

import com.musicos.domain.Competency;
import com.musicos.domain.CompetencyPrerequisite;
import com.musicos.domain.Curriculum;
import com.musicos.domain.Evidence;
import com.musicos.domain.InstrumentProfile;
import com.musicos.domain.LearningContentRelation;
import com.musicos.domain.LearningPath;
import com.musicos.domain.Mastery;
import com.musicos.repository.CompetencyRepository;
import com.musicos.repository.CurriculumRepository;
import com.musicos.repository.EvidenceRepository;
import com.musicos.repository.InstrumentProfileRepository;
import com.musicos.repository.LearningContentRelationRepository;
import com.musicos.repository.LearningPathRepository;
import com.musicos.repository.MasteryRepository;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CurriculumEngine {
    public enum CompetencyStatus { BLOCKED, AVAILABLE, IN_PROGRESS, ESTABLISHED, REVIEW_DUE }
    public enum SuggestionKind { REVIEW, REVALIDATION, INTRODUCTION, PRACTICE, APPLICATION }

    public record PrerequisiteView(
            String competencyId,
            CompetencyPrerequisite.Type type,
            int depth,
            boolean direct,
            boolean satisfied,
            boolean blocking,
            String reason) {
    }

    public record CompetencyView(
            String competencyId,
            String title,
            int pathPosition,
            CompetencyStatus status,
            Mastery.State masteryState,
            boolean unlocked,
            List<PrerequisiteView> prerequisites,
            String reason) {
        public CompetencyView {
            prerequisites = List.copyOf(prerequisites);
        }
    }

    public record ReviewNeed(
            String competencyId,
            String title,
            SuggestionKind kind,
            int pathPosition,
            int directlySupportedCompetencies,
            String reason) {
    }

    public record NextStep(
            String competencyId,
            String title,
            SuggestionKind kind,
            int pathPosition,
            String reason,
            List<String> advisoryPrerequisiteIds) {
        public NextStep {
            advisoryPrerequisiteIds = List.copyOf(advisoryPrerequisiteIds);
        }
    }

    public record CurriculumPosition(
            int totalCompetencies,
            int establishedCompetencies,
            int inProgressCompetencies,
            int availableCompetencies,
            int blockedCompetencies,
            int reviewsDue,
            String focusCompetencyId,
            String explanation) {
    }

    public record Navigation(
            String instrumentProfileId,
            String curriculumId,
            String learningPathId,
            Instant evaluatedAt,
            CurriculumPosition position,
            List<CompetencyView> competencies,
            List<ReviewNeed> reviews,
            List<NextStep> nextSteps) {
        public Navigation {
            competencies = List.copyOf(competencies);
            reviews = List.copyOf(reviews);
            nextSteps = List.copyOf(nextSteps);
        }
    }

    private record Prerequisite(String competencyId, CompetencyPrerequisite.Type type) {
    }

    private record Traversal(String competencyId, CompetencyPrerequisite.Type type, int depth,
                             boolean direct, boolean strictChain) {
    }

    private record EngineContext(
            InstrumentProfile profile,
            LearningPath path,
            Curriculum curriculum,
            List<String> orderedIds,
            Map<String, Competency> competencies,
            Map<String, Mastery> mastery,
            Map<String, List<Evidence>> evidence,
            Map<String, List<Prerequisite>> prerequisites,
            Map<String, Integer> positions) {
    }

    private final InstrumentProfileRepository profiles;
    private final LearningPathRepository learningPaths;
    private final CurriculumRepository curricula;
    private final CompetencyRepository competencies;
    private final MasteryRepository mastery;
    private final EvidenceRepository evidence;
    private final LearningContentRelationRepository relations;

    public CurriculumEngine(InstrumentProfileRepository profiles, LearningPathRepository learningPaths,
                            CurriculumRepository curricula, CompetencyRepository competencies,
                            MasteryRepository mastery, EvidenceRepository evidence,
                            LearningContentRelationRepository relations) {
        this.profiles = profiles;
        this.learningPaths = learningPaths;
        this.curricula = curricula;
        this.competencies = competencies;
        this.mastery = mastery;
        this.evidence = evidence;
        this.relations = relations;
    }

    @Transactional(readOnly = true)
    public Navigation navigate(String instrumentProfileId) {
        return navigate(instrumentProfileId, Instant.now(), 5);
    }

    @Transactional(readOnly = true)
    public Navigation navigate(String instrumentProfileId, Instant evaluatedAt, int suggestionLimit) {
        Objects.requireNonNull(evaluatedAt, "evaluatedAt");
        if (suggestionLimit < 1) throw new IllegalArgumentException("suggestionLimit deve ser positivo");
        var context = load(instrumentProfileId);
        var views = context.orderedIds().stream()
                .map(id -> competencyView(context, id, evaluatedAt))
                .toList();
        var reviews = reviewNeeds(context, views, evaluatedAt);
        var nextSteps = suggestions(context, views, reviews, suggestionLimit);
        var position = position(views, reviews, nextSteps);
        return new Navigation(context.profile().getId(), context.curriculum().getId(), context.path().getId(),
                evaluatedAt, position, views, reviews, nextSteps);
    }

    @Transactional(readOnly = true)
    public List<PrerequisiteView> findPrerequisites(String instrumentProfileId, String competencyId) {
        return findPrerequisites(instrumentProfileId, competencyId, Instant.now());
    }

    @Transactional(readOnly = true)
    public List<PrerequisiteView> findPrerequisites(
            String instrumentProfileId, String competencyId, Instant evaluatedAt) {
        Objects.requireNonNull(evaluatedAt, "evaluatedAt");
        var context = load(instrumentProfileId);
        requireCurriculumCompetency(context, competencyId);
        return prerequisiteViews(context, competencyId, evaluatedAt, true);
    }

    private EngineContext load(String instrumentProfileId) {
        if (instrumentProfileId == null || instrumentProfileId.isBlank()) {
            throw new IllegalArgumentException("instrumentProfileId Ã© obrigatÃ³rio");
        }
        var profile = profiles.findById(instrumentProfileId)
                .orElseThrow(() -> new NotFoundException("Perfil instrumental nÃ£o encontrado: " + instrumentProfileId));
        if (!profile.isActive()) throw new IllegalStateException("Perfil instrumental estÃ¡ inativo");
        var path = learningPaths.findByInstrumentProfileIdAndStatus(instrumentProfileId, LearningPath.Status.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Caminho ativo nÃ£o encontrado para o perfil: " + instrumentProfileId));
        var curriculum = curricula.findById(path.getCurriculumId())
                .orElseThrow(() -> new NotFoundException("CurrÃ­culo do caminho nÃ£o encontrado: " + path.getCurriculumId()));

        var orderedIds = path.getSteps().stream().map(step -> step.getCompetencyId()).distinct().toList();
        var curriculumIds = Set.copyOf(curriculum.getCompetencyIds());
        var outsideCurriculum = orderedIds.stream().filter(id -> !curriculumIds.contains(id)).toList();
        if (!outsideCurriculum.isEmpty()) {
            throw new IllegalStateException("Caminho referencia competÃªncias fora do currÃ­culo: " + outsideCurriculum);
        }
        var competencyMap = competencies.findAll().stream()
                .filter(Competency::isActive)
                .collect(Collectors.toMap(Competency::getId, Function.identity()));
        var missing = orderedIds.stream().filter(id -> !competencyMap.containsKey(id)).toList();
        if (!missing.isEmpty()) {
            throw new IllegalStateException("Caminho referencia competÃªncias ausentes ou inativas: " + missing);
        }
        var masteryMap = mastery.findByInstrumentProfileId(instrumentProfileId).stream()
                .collect(Collectors.toMap(Mastery::getCompetencyId, Function.identity(), this::latestMastery));
        var evidenceMap = evidence.findByInstrumentProfileIdOrderByOccurredAtDesc(instrumentProfileId).stream()
                .collect(Collectors.groupingBy(Evidence::getCompetencyId));
        var positions = new HashMap<String, Integer>();
        for (var index = 0; index < orderedIds.size(); index++) positions.put(orderedIds.get(index), index);
        return new EngineContext(profile, path, curriculum, orderedIds, competencyMap, masteryMap, evidenceMap,
                buildPrerequisites(competencyMap), positions);
    }

    private Mastery latestMastery(Mastery first, Mastery second) {
        return first.getAssessedAt().isAfter(second.getAssessedAt()) ? first : second;
    }

    private Map<String, List<Prerequisite>> buildPrerequisites(Map<String, Competency> competencyMap) {
        var result = new HashMap<String, LinkedHashMap<String, CompetencyPrerequisite.Type>>();
        competencyMap.values().forEach(competency -> competency.getPrerequisites().forEach(prerequisite ->
                result.computeIfAbsent(competency.getId(), ignored -> new LinkedHashMap<>())
                        .merge(prerequisite.getCompetencyId(), prerequisite.getType(), this::strongest)));
        relations.findBySourceTypeAndTargetType(
                        LearningContentRelation.ContentType.COMPETENCY,
                        LearningContentRelation.ContentType.COMPETENCY).stream()
                .filter(relation -> relation.getRelationType() == LearningContentRelation.RelationType.REQUIRES
                        || relation.getRelationType() == LearningContentRelation.RelationType.PREPARES)
                .forEach(relation -> {
                    var type = relation.getRelationType() == LearningContentRelation.RelationType.REQUIRES
                            ? CompetencyPrerequisite.Type.STRICT : CompetencyPrerequisite.Type.PEDAGOGICAL;
                    result.computeIfAbsent(relation.getTargetId(), ignored -> new LinkedHashMap<>())
                            .merge(relation.getSourceId(), type, this::strongest);
                });
        return result.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                entry -> entry.getValue().entrySet().stream()
                        .map(item -> new Prerequisite(item.getKey(), item.getValue())).toList()));
    }

    private CompetencyPrerequisite.Type strongest(
            CompetencyPrerequisite.Type first, CompetencyPrerequisite.Type second) {
        return prerequisiteStrength(first) <= prerequisiteStrength(second) ? first : second;
    }

    private int prerequisiteStrength(CompetencyPrerequisite.Type type) {
        return switch (type) {
            case STRICT -> 0;
            case PEDAGOGICAL -> 1;
            case CONTEXTUAL -> 2;
            case COREQUISITE -> 3;
            case RECOMMENDED -> 4;
        };
    }

    private CompetencyView competencyView(EngineContext context, String competencyId, Instant evaluatedAt) {
        var competency = requireCurriculumCompetency(context, competencyId);
        var direct = prerequisiteViews(context, competencyId, evaluatedAt, false);
        var blocking = direct.stream().filter(PrerequisiteView::blocking).toList();
        var currentMastery = context.mastery().get(competencyId);
        var unlocked = blocking.isEmpty() || isEstablished(currentMastery);
        var reviewReason = reviewReason(context, competencyId, evaluatedAt);
        CompetencyStatus status;
        String reason;
        if (reviewReason != null) {
            status = CompetencyStatus.REVIEW_DUE;
            reason = reviewReason;
        } else if (!unlocked) {
            status = CompetencyStatus.BLOCKED;
            reason = "Bloqueada por prÃ©-requisitos estritos sem evidÃªncia mÃ­nima vÃ¡lida: "
                    + blocking.stream().map(PrerequisiteView::competencyId).toList();
        } else if (isEstablished(currentMastery)) {
            status = CompetencyStatus.ESTABLISHED;
            reason = "HipÃ³tese atual sustentada em aplicaÃ§Ã£o ou retenÃ§Ã£o.";
        } else if (currentMastery != null && currentMastery.getState() != Mastery.State.UNOBSERVED) {
            status = CompetencyStatus.IN_PROGRESS;
            reason = "HÃ¡ evidÃªncia parcial; a competÃªncia ainda precisa de consolidaÃ§Ã£o.";
        } else {
            status = CompetencyStatus.AVAILABLE;
            reason = "DisponÃ­vel para prÃ¡tica guiada; nenhum prÃ©-requisito estrito bloqueia o passo.";
        }
        return new CompetencyView(competencyId, competency.getFriendlyTitle(),
                context.positions().get(competencyId), status,
                currentMastery == null ? Mastery.State.UNOBSERVED : currentMastery.getState(),
                unlocked, direct, reason);
    }

    private List<PrerequisiteView> prerequisiteViews(
            EngineContext context, String competencyId, Instant evaluatedAt, boolean transitive) {
        var traversals = transitive
                ? prerequisiteClosure(context, competencyId)
                : context.prerequisites().getOrDefault(competencyId, List.of()).stream()
                        .map(item -> new Traversal(item.competencyId(), item.type(), 1, true,
                                item.type() == CompetencyPrerequisite.Type.STRICT)).toList();
        return traversals.stream().map(item -> {
            var exists = context.competencies().containsKey(item.competencyId());
            var sufficient = exists && hasMinimumEvidence(context, item.competencyId(), evaluatedAt);
            var blocking = item.strictChain() && !sufficient;
            var reason = !exists
                    ? "PrÃ©-requisito referenciado nÃ£o estÃ¡ disponÃ­vel no caminho ativo."
                    : sufficient
                    ? "Possui evidÃªncia mÃ­nima atual para sustentar o prÃ³ximo passo."
                    : item.type() == CompetencyPrerequisite.Type.STRICT
                    ? "Exige evidÃªncia consistente e atual antes da prÃ¡tica guiada."
                    : "Lacuna aconselhÃ¡vel, mas nÃ£o bloqueante para exploraÃ§Ã£o guiada.";
            return new PrerequisiteView(item.competencyId(), item.type(), item.depth(), item.direct(),
                    sufficient, blocking, reason);
        }).toList();
    }

    private List<Traversal> prerequisiteClosure(EngineContext context, String rootId) {
        var result = new ArrayList<Traversal>();
        var queue = new ArrayDeque<Traversal>();
        context.prerequisites().getOrDefault(rootId, List.of()).forEach(item ->
                queue.add(new Traversal(item.competencyId(), item.type(), 1, true,
                        item.type() == CompetencyPrerequisite.Type.STRICT)));
        var visited = new HashSet<String>();
        visited.add(rootId);
        while (!queue.isEmpty()) {
            var current = queue.removeFirst();
            if (!visited.add(current.competencyId())) continue;
            result.add(current);
            context.prerequisites().getOrDefault(current.competencyId(), List.of()).forEach(item ->
                    queue.addLast(new Traversal(item.competencyId(), item.type(), current.depth() + 1, false,
                            current.strictChain() && item.type() == CompetencyPrerequisite.Type.STRICT)));
        }
        return result;
    }

    private boolean hasMinimumEvidence(EngineContext context, String competencyId, Instant evaluatedAt) {
        var current = context.mastery().get(competencyId);
        if (current == null || current.isUnresolvedConflict() || reviewReason(context, competencyId, evaluatedAt) != null) {
            return false;
        }
        return current.getState() == Mastery.State.CONSISTENT_CONTROLLED
                || current.getState() == Mastery.State.PROBABLE_MASTERY_APPLICATION
                || current.getState() == Mastery.State.RETAINED;
    }

    private boolean isEstablished(Mastery current) {
        return current != null && (current.getState() == Mastery.State.PROBABLE_MASTERY_APPLICATION
                || current.getState() == Mastery.State.RETAINED);
    }

    private String reviewReason(EngineContext context, String competencyId, Instant evaluatedAt) {
        var current = context.mastery().get(competencyId);
        if (current == null || current.getState() == Mastery.State.UNOBSERVED) return null;
        if (current.getState() == Mastery.State.REVALIDATION_NEEDED) {
            return "A hipÃ³tese de domÃ­nio requer revalidaÃ§Ã£o.";
        }
        if (current.isUnresolvedConflict() || hasContradictoryEvidence(context.evidence().get(competencyId))) {
            return "Existem evidÃªncias conflitantes que pedem observaÃ§Ã£o focal.";
        }
        if (current.getNextReviewAt() != null && !current.getNextReviewAt().isAfter(evaluatedAt)) {
            return "A revisÃ£o programada estÃ¡ devida.";
        }
        if (hasAgedSupportingEvidence(current, context.evidence().get(competencyId), evaluatedAt)) {
            return "EvidÃªncia que sustenta a hipÃ³tese atual envelheceu.";
        }
        return null;
    }

    private boolean hasContradictoryEvidence(List<Evidence> items) {
        return safe(items).stream().anyMatch(item -> item.getState() == Evidence.State.CONTRADICTORY);
    }

    private boolean hasAgedSupportingEvidence(Mastery current, List<Evidence> items, Instant evaluatedAt) {
        if (current.getSupportingEvidenceIds().isEmpty()) return false;
        var supporting = Set.copyOf(current.getSupportingEvidenceIds());
        return safe(items).stream().filter(item -> supporting.contains(item.getId()))
                .anyMatch(item -> item.getState() == Evidence.State.AGED || item.isAgedAt(evaluatedAt));
    }

    private List<ReviewNeed> reviewNeeds(
            EngineContext context, List<CompetencyView> views, Instant evaluatedAt) {
        var directDependents = directStrictDependents(context.prerequisites());
        return views.stream().filter(view -> view.status() == CompetencyStatus.REVIEW_DUE)
                .map(view -> new ReviewNeed(view.competencyId(), view.title(),
                        context.mastery().get(view.competencyId()).getState() == Mastery.State.REVALIDATION_NEEDED
                                ? SuggestionKind.REVALIDATION : SuggestionKind.REVIEW,
                        view.pathPosition(), directDependents.getOrDefault(view.competencyId(), 0),
                        reviewReason(context, view.competencyId(), evaluatedAt)))
                .sorted(Comparator.comparingInt(ReviewNeed::directlySupportedCompetencies).reversed()
                        .thenComparingInt(ReviewNeed::pathPosition))
                .toList();
    }

    private Map<String, Integer> directStrictDependents(Map<String, List<Prerequisite>> prerequisites) {
        var result = new HashMap<String, Integer>();
        prerequisites.values().stream().flatMap(Collection::stream)
                .filter(item -> item.type() == CompetencyPrerequisite.Type.STRICT)
                .forEach(item -> result.merge(item.competencyId(), 1, Integer::sum));
        return result;
    }

    private List<NextStep> suggestions(EngineContext context, List<CompetencyView> views,
                                       List<ReviewNeed> reviews, int limit) {
        var result = new ArrayList<NextStep>();
        var candidates = views.stream()
                .filter(view -> view.status() == CompetencyStatus.AVAILABLE
                        || view.status() == CompetencyStatus.IN_PROGRESS)
                .map(view -> nextStep(context, view))
                .toList();
        var reviewLimit = candidates.isEmpty() || limit == 1 ? limit : Math.max(1, limit - 1);
        reviews.stream().limit(reviewLimit).forEach(review -> result.add(new NextStep(
                review.competencyId(), review.title(), review.kind(), review.pathPosition(), review.reason(), List.of())));
        candidates.forEach(result::add);
        return result.stream().limit(limit).toList();
    }

    private NextStep nextStep(EngineContext context, CompetencyView view) {
        var current = context.mastery().get(view.competencyId());
        var kind = current == null || current.getState() == Mastery.State.UNOBSERVED
                ? SuggestionKind.INTRODUCTION
                : current.getState() == Mastery.State.CONSISTENT_CONTROLLED
                ? SuggestionKind.APPLICATION : SuggestionKind.PRACTICE;
        var reason = switch (kind) {
            case INTRODUCTION -> "Primeiro passo ainda nÃ£o observado no caminho ativo.";
            case PRACTICE -> "EvidÃªncias parciais indicam que consolidar agora reduz incerteza.";
            case APPLICATION -> "A execuÃ§Ã£o controlada estÃ¡ consistente; o prÃ³ximo valor Ã© aplicaÃ§Ã£o musical.";
            default -> throw new IllegalStateException("Tipo de sugestÃ£o inesperado: " + kind);
        };
        var advisory = view.prerequisites().stream()
                .filter(prerequisite -> !prerequisite.blocking() && !prerequisite.satisfied())
                .map(PrerequisiteView::competencyId).toList();
        return new NextStep(view.competencyId(), view.title(), kind, view.pathPosition(), reason, advisory);
    }

    private CurriculumPosition position(
            List<CompetencyView> views, List<ReviewNeed> reviews, List<NextStep> nextSteps) {
        var established = count(views, CompetencyStatus.ESTABLISHED);
        var inProgress = count(views, CompetencyStatus.IN_PROGRESS);
        var available = count(views, CompetencyStatus.AVAILABLE);
        var blocked = count(views, CompetencyStatus.BLOCKED);
        var focus = nextSteps.isEmpty() ? null : nextSteps.getFirst().competencyId();
        var explanation = focus == null && established == views.size()
                ? "Todas as competÃªncias do caminho estÃ£o estabelecidas e nenhuma revisÃ£o estÃ¡ devida."
                : focus == null
                ? "Nenhum passo estÃ¡ disponÃ­vel; os bloqueios precisam ser atendidos ou inspecionados."
                : reviews.isEmpty()
                ? "O foco atual Ã© o primeiro passo produtivo e desbloqueado do caminho ativo."
                : "O foco atual prioriza uma revisÃ£o devida sem impedir avanÃ§o paralelo quando adequado.";
        return new CurriculumPosition(views.size(), established, inProgress, available, blocked,
                reviews.size(), focus, explanation);
    }

    private int count(List<CompetencyView> views, CompetencyStatus status) {
        return (int) views.stream().filter(view -> view.status() == status).count();
    }

    private Competency requireCurriculumCompetency(EngineContext context, String competencyId) {
        if (competencyId == null || competencyId.isBlank()) {
            throw new IllegalArgumentException("competencyId Ã© obrigatÃ³rio");
        }
        var competency = context.competencies().get(competencyId);
        if (competency == null || !context.curriculum().getCompetencyIds().contains(competencyId)) {
            throw new NotFoundException("CompetÃªncia nÃ£o pertence ao currÃ­culo ativo: " + competencyId);
        }
        return competency;
    }

    private <T> List<T> safe(List<T> value) {
        return value == null ? List.of() : value;
    }
}
