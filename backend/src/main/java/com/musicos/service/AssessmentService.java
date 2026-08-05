package com.musicos.service;

import static com.musicos.api.ApiModels.*;

import com.musicos.domain.Assessment;
import com.musicos.domain.AssessmentAttempt;
import com.musicos.domain.Evidence;
import com.musicos.repository.AssessmentAttemptRepository;
import com.musicos.repository.AssessmentRepository;
import com.musicos.repository.InstrumentProfileRepository;
import com.musicos.repository.PracticeRecordingRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssessmentService {
    private final AssessmentRepository assessments;
    private final AssessmentAttemptRepository attempts;
    private final InstrumentProfileRepository profiles;
    private final PracticeRecordingRepository recordings;
    private final EvidenceEngine evidenceEngine;

    public AssessmentService(AssessmentRepository assessments, AssessmentAttemptRepository attempts,
                             InstrumentProfileRepository profiles, PracticeRecordingRepository recordings,
                             EvidenceEngine evidenceEngine) {
        this.assessments = assessments;
        this.attempts = attempts;
        this.profiles = profiles;
        this.recordings = recordings;
        this.evidenceEngine = evidenceEngine;
    }

    @Transactional
    public AssessmentAttemptView record(String assessmentId, AssessmentAttemptRequest request) {
        var assessment = assessments.findById(assessmentId)
                .orElseThrow(() -> new NotFoundException("Assessment não encontrado"));
        if (!assessment.isActive()) throw new IllegalStateException("Assessment está inativo");
        if (assessment.getCompetencyIds().size() != 1) {
            throw new IllegalStateException(
                    "Assessment com múltiplas competências exige mapeamento explícito de critérios");
        }
        var profile = profiles.findByOwnerIdAndInstrument("default", request.instrument())
                .orElseThrow(() -> new NotFoundException("Perfil instrumental não encontrado"));
        var observations = observationsByCriterion(assessment, request.observations());
        var artifactReference = latestMissionRecording(assessmentId);
        var attempt = attempts.saveAndFlush(new AssessmentAttempt(
                assessmentId, profile.getId(), request.observerType(), request.challengeLevel(),
                artifactReference, request.note()));
        var attemptKey = attempt.getId().toString();
        var competencyId = assessment.getCompetencyIds().getFirst();
        var results = new ArrayList<AssessmentCriterionResultView>();
        var evidenceIds = new ArrayList<String>();
        for (var criterionKey : assessment.getCriterionKeys()) {
            var observation = observations.get(criterionKey);
            var external = request.observerType() == AssessmentAttempt.ObserverType.EXTERNAL;
            var collected = evidenceEngine.collect(new EvidenceEngine.Observation(
                    profile.getId(), competencyId, criterionKey,
                    external ? evidenceType(assessment.getType()) : Evidence.Type.DECLARATIVE,
                    external ? Evidence.State.VALID : Evidence.State.PROVISIONAL,
                    external ? Evidence.FunctionalWeight.PRIMARY : Evidence.FunctionalWeight.CORROBORATING,
                    external ? Evidence.Reliability.MODERATE : Evidence.Reliability.LOW,
                    observation.result(), Evidence.SourceType.ASSESSMENT, attemptKey, attemptKey,
                    request.challengeLevel(), observation.observation(),
                    assessment.getConditions() + " Apoio permitido: " + assessment.getAllowedSupport(),
                    external ? assessment.getProtocolVersion() : null, null, artifactReference,
                    null, Instant.now(), null));
            evidenceIds.add(collected.evidenceId());
            results.add(new AssessmentCriterionResultView(
                    collected.evidenceId(), competencyId, criterionKey, observation.result().name(),
                    external ? Evidence.State.VALID.name() : Evidence.State.PROVISIONAL.name(),
                    external ? Evidence.Reliability.MODERATE.name() : Evidence.Reliability.LOW.name(),
                    collected.report().confidence().name(), collected.report().nextObservation()));
        }
        attempt.attachEvidence(evidenceIds);
        attempts.save(attempt);
        return new AssessmentAttemptView(attempt.getId(), assessmentId, attempt.getObserverType().name(),
                attempt.getCompletedAt(), artifactReference, results);
    }

    private Map<String, AssessmentCriterionObservationRequest> observationsByCriterion(
            Assessment assessment, List<AssessmentCriterionObservationRequest> values) {
        if (values == null) throw new IllegalArgumentException("Informe o resultado de cada critério");
        var result = new LinkedHashMap<String, AssessmentCriterionObservationRequest>();
        for (var value : values) {
            if (value == null || value.criterionKey() == null || value.criterionKey().isBlank()
                    || value.observation() == null || value.observation().isBlank()) {
                throw new IllegalArgumentException("Cada critério precisa de resultado e observação");
            }
            if (!assessment.getCriterionKeys().contains(value.criterionKey())) {
                throw new IllegalArgumentException("Critério não pertence ao Assessment: " + value.criterionKey());
            }
            if (result.put(value.criterionKey(), value) != null) {
                throw new IllegalArgumentException("Critério repetido: " + value.criterionKey());
            }
        }
        if (!result.keySet().containsAll(assessment.getCriterionKeys())) {
            throw new IllegalArgumentException("Todos os critérios do Assessment precisam ser registrados");
        }
        return result;
    }

    private String latestMissionRecording(String assessmentId) {
        var missionId = assessmentId.endsWith("-assessment")
                ? assessmentId.substring(0, assessmentId.length() - "-assessment".length()) : null;
        if (missionId == null) return null;
        return recordings.findTop10ByContextTypeAndContextIdOrderByCreatedAtDesc("mission", missionId).stream()
                .findFirst().map(item -> item.getId().toString()).orElse(null);
    }

    private Evidence.Type evidenceType(Assessment.Type type) {
        return switch (type) {
            case APPLICATION -> Evidence.Type.APPLICATION;
            case TRANSFER -> Evidence.Type.TRANSFER;
            case RETENTION -> Evidence.Type.RETENTION;
            default -> Evidence.Type.EXECUTION;
        };
    }
}
