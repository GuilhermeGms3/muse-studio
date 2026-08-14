package com.musicos.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class PedagogicalDomainTest {
    @Test
    void masteryRequiresEvidenceCoverageAndApplication() {
        var mastery = new Mastery("profile-1", "pulse", "policy-v1");

        assertThatThrownBy(() -> mastery.reviseHypothesis(
                Mastery.State.PROBABLE_MASTERY_APPLICATION,
                true, 1, true, false, false, false,
                "Uma única evidência.", Instant.now(), Instant.now().plusSeconds(3600),
                List.of("evidence-1"), List.of()))
                .isInstanceOf(IllegalStateException.class);

        mastery.reviseHypothesis(
                Mastery.State.PROBABLE_MASTERY_APPLICATION,
                true, 2, true, false, false, false,
                "Critérios cobertos por duas evidências independentes em aplicação.",
                Instant.now(), Instant.now().plusSeconds(3600),
                List.of("evidence-1", "evidence-2"), List.of());

        assertThat(mastery.getState()).isEqualTo(Mastery.State.PROBABLE_MASTERY_APPLICATION);
        assertThat(mastery.getSupportingEvidenceIds()).hasSize(2);
    }

    @Test
    void retainedMasteryRequiresRetentionEvidence() {
        var mastery = new Mastery("profile-1", "pulse", "policy-v1");

        assertThatThrownBy(() -> mastery.reviseHypothesis(
                Mastery.State.RETAINED,
                true, 3, true, true, false, false,
                "Sem revalidação temporal.", Instant.now(), null, List.of("evidence-1"), List.of()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void evidenceKeepsContextAndAgesWithoutBeingDeleted() {
        var occurredAt = Instant.parse("2026-08-01T12:00:00Z");
        var validUntil = occurredAt.plusSeconds(86400);
        var evidence = new Evidence(
                "profile-1", "pulse", "steady-pulse", Evidence.Type.EXECUTION, Evidence.State.VALID,
                Evidence.FunctionalWeight.PRIMARY, Evidence.Reliability.HIGH, Evidence.Result.SUPPORTS,
                Evidence.SourceType.ASSESSMENT, "assessment-1", "recording-1", 3,
                "Pulso sustentado por dois minutos.", "100 BPM, sem metrônomo após a contagem.",
                "protocol-v1", "analyzer-v1", "recording.wav", null, occurredAt, validUntil);

        assertThat(evidence.isAgedAt(validUntil.plusSeconds(1))).isTrue();
        assertThat(evidence.getOccurredAt()).isEqualTo(occurredAt);
        assertThat(evidence.getObservation()).contains("dois minutos");
    }

    @Test
    void curriculumStillRequiresStructureAndMissionMayBeFormativeWithoutAssessment() {
        var curriculum = new Curriculum(
                "curriculum-1", "Fundamentos", "v1", "Construir base musical.", "Iniciantes.",
                InstrumentId.GUITAR, LearningStage.FIRST_STEPS, LearningStage.BEGINNER,
                List.of("Sustentar o pulso."), List.of());
        assertThatThrownBy(curriculum::activate).isInstanceOf(IllegalStateException.class);

        var mission = new Mission(
                "mission-1", "curriculum-1", "Sustentar o pulso", "Tocar sem perder o pulso.",
                "Groove simples.", "O pulso organiza a música.", 15, InstrumentId.GUITAR,
                LearningStage.BEGINNER, "Dois minutos estáveis.", "Execução gravada.",
                "Aplicar em uma música.", null, DifficultyDemand.unspecified(),
                List.of("pulse"), List.of("lesson-1"), List.of("exercise-1"), List.of());
        mission.activate();
        assertThat(mission.getStatus()).isEqualTo(Mission.Status.ACTIVE);
    }

    @Test
    void learningPathStartsWithoutInferringReadiness() {
        var step = new LearningPathStep(
                "pulse", LearningPathStep.Kind.CORE, LearningPathStep.Readiness.UNASSESSED,
                "ConteÃºdo migrado sem evidÃªncia admissÃ­vel.");
        var path = new LearningPath(
                "path-1", "profile-1", "curriculum-1", "Trilha inicial", "legacy-v2",
                "Ordem derivada do currÃ­culo; prontidÃ£o nÃ£o inferida.", List.of(), List.of(step));

        assertThat(path.getSteps()).singleElement().isEqualTo(step);
        assertThat(path.getSteps().getFirst().getReadiness()).isEqualTo(LearningPathStep.Readiness.UNASSESSED);
        assertThatThrownBy(() -> new LearningPath(
                "path-2", "profile-1", "curriculum-1", "Trilha vazia", "legacy-v2",
                "Sem passos.", List.of(), List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void contentRelationsAreDeterministicAndRejectSelfReferences() {
        var first = new LearningContentRelation(
                LearningContentRelation.ContentType.COMPETENCY, "rhythm",
                LearningContentRelation.RelationType.PREPARES,
                LearningContentRelation.ContentType.COMPETENCY, "alternate-picking",
                LearningContentRelation.Strength.MODERATE, "RelaÃ§Ã£o legada.", null, "skill.nextSkills");
        var second = new LearningContentRelation(
                LearningContentRelation.ContentType.COMPETENCY, "rhythm",
                LearningContentRelation.RelationType.PREPARES,
                LearningContentRelation.ContentType.COMPETENCY, "alternate-picking",
                LearningContentRelation.Strength.MODERATE, "Outra descriÃ§Ã£o.", null, "skill.nextSkills");

        assertThat(first.getId()).isEqualTo(second.getId());
        assertThatThrownBy(() -> new LearningContentRelation(
                LearningContentRelation.ContentType.COMPETENCY, "rhythm",
                LearningContentRelation.RelationType.REQUIRES,
                LearningContentRelation.ContentType.COMPETENCY, "rhythm",
                LearningContentRelation.Strength.REQUIRED, "InvÃ¡lida.", null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
