package com.musicos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.musicos.domain.Competency;
import com.musicos.domain.CompetencyPrerequisite;
import com.musicos.domain.Curriculum;
import com.musicos.domain.InstrumentId;
import com.musicos.domain.InstrumentProfile;
import com.musicos.domain.LearningPath;
import com.musicos.domain.LearningPathStep;
import com.musicos.domain.LearningContentRelation;
import com.musicos.domain.LearningStage;
import com.musicos.domain.LearningTrack;
import com.musicos.domain.Mastery;
import com.musicos.domain.SkillKind;
import com.musicos.repository.CompetencyRepository;
import com.musicos.repository.CurriculumRepository;
import com.musicos.repository.EvidenceRepository;
import com.musicos.repository.InstrumentProfileRepository;
import com.musicos.repository.LearningContentRelationRepository;
import com.musicos.repository.LearningPathRepository;
import com.musicos.repository.MasteryRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CurriculumEngineTest {
    private static final Instant NOW = Instant.parse("2026-08-05T12:00:00Z");

    @Test
    void strictPrerequisiteBlocksUntilItHasMinimumCurrentEvidence() {
        var pulse = competency("pulse", List.of());
        var technique = competency("technique", List.of(
                new CompetencyPrerequisite("pulse", CompetencyPrerequisite.Type.STRICT)));
        var fixture = fixture(List.of(pulse, technique), List.of());

        var before = fixture.engine().navigate("profile", NOW, 5);

        assertThat(view(before, "pulse").status()).isEqualTo(CurriculumEngine.CompetencyStatus.AVAILABLE);
        assertThat(view(before, "technique").status()).isEqualTo(CurriculumEngine.CompetencyStatus.BLOCKED);
        assertThat(before.nextSteps()).extracting(CurriculumEngine.NextStep::competencyId)
                .containsExactly("pulse");

        var pulseMastery = mastery("pulse", Mastery.State.CONSISTENT_CONTROLLED, NOW.minusSeconds(60), null);
        when(fixture.mastery().findByInstrumentProfileId("profile")).thenReturn(List.of(pulseMastery));

        var after = fixture.engine().navigate("profile", NOW, 5);
        assertThat(view(after, "technique").status()).isEqualTo(CurriculumEngine.CompetencyStatus.AVAILABLE);
        assertThat(view(after, "technique").unlocked()).isTrue();
    }

    @Test
    void findsDirectAndTransitivePrerequisitesWithoutUsingAdvisoryOnesAsGates() {
        var foundation = competency("foundation", List.of());
        var rhythm = competency("rhythm", List.of(
                new CompetencyPrerequisite("foundation", CompetencyPrerequisite.Type.STRICT)));
        var technique = competency("technique", List.of(
                new CompetencyPrerequisite("rhythm", CompetencyPrerequisite.Type.PEDAGOGICAL)));
        var fixture = fixture(List.of(foundation, rhythm, technique), List.of());

        var prerequisites = fixture.engine().findPrerequisites("profile", "technique", NOW);

        assertThat(prerequisites).anySatisfy(item -> {
            assertThat(item.competencyId()).isEqualTo("rhythm");
            assertThat(item.depth()).isEqualTo(1);
            assertThat(item.direct()).isTrue();
            assertThat(item.blocking()).isFalse();
        }).anySatisfy(item -> {
            assertThat(item.competencyId()).isEqualTo("foundation");
            assertThat(item.depth()).isEqualTo(2);
            assertThat(item.direct()).isFalse();
            assertThat(item.blocking()).isFalse();
        });
        assertThat(view(fixture.engine().navigate("profile", NOW, 5), "technique").unlocked()).isTrue();
    }

    @Test
    void dueReviewTakesPriorityAndTemporarilyStopsStrictDependent() {
        var pulse = competency("pulse", List.of());
        var technique = competency("technique", List.of(
                new CompetencyPrerequisite("pulse", CompetencyPrerequisite.Type.STRICT)));
        var retained = mastery(
                "pulse", Mastery.State.RETAINED, NOW.minusSeconds(86400), NOW.minusSeconds(1));
        var fixture = fixture(List.of(pulse, technique), List.of(retained));

        var navigation = fixture.engine().navigate("profile", NOW, 5);

        assertThat(view(navigation, "pulse").status()).isEqualTo(CurriculumEngine.CompetencyStatus.REVIEW_DUE);
        assertThat(view(navigation, "technique").status()).isEqualTo(CurriculumEngine.CompetencyStatus.BLOCKED);
        assertThat(navigation.reviews()).singleElement().satisfies(review -> {
            assertThat(review.competencyId()).isEqualTo("pulse");
            assertThat(review.directlySupportedCompetencies()).isEqualTo(1);
        });
        assertThat(navigation.nextSteps().getFirst().kind()).isEqualTo(CurriculumEngine.SuggestionKind.REVIEW);
        assertThat(navigation.position().focusCompetencyId()).isEqualTo("pulse");
    }

    @Test
    void reportsCurriculumPositionAndSuggestsApplicationAfterControlledConsistency() {
        var pulse = competency("pulse", List.of());
        var technique = competency("technique", List.of(
                new CompetencyPrerequisite("pulse", CompetencyPrerequisite.Type.STRICT)));
        var fixture = fixture(List.of(pulse, technique), List.of(
                mastery("pulse", Mastery.State.PROBABLE_MASTERY_APPLICATION, NOW.minusSeconds(120), null),
                mastery("technique", Mastery.State.CONSISTENT_CONTROLLED, NOW.minusSeconds(60), null)));

        var navigation = fixture.engine().navigate("profile", NOW, 5);

        assertThat(navigation.position().establishedCompetencies()).isEqualTo(1);
        assertThat(navigation.position().inProgressCompetencies()).isEqualTo(1);
        assertThat(navigation.position().blockedCompetencies()).isZero();
        assertThat(navigation.nextSteps()).singleElement().satisfies(step -> {
            assertThat(step.competencyId()).isEqualTo("technique");
            assertThat(step.kind()).isEqualTo(CurriculumEngine.SuggestionKind.APPLICATION);
        });
    }

    @Test
    void usesRequiresRelationFromKnowledgeGraphAsStrictGate() {
        var pulse = competency("pulse", List.of());
        var technique = competency("technique", List.of());
        var fixture = fixture(List.of(pulse, technique), List.of());
        var relation = new LearningContentRelation(
                LearningContentRelation.ContentType.COMPETENCY, "pulse",
                LearningContentRelation.RelationType.REQUIRES,
                LearningContentRelation.ContentType.COMPETENCY, "technique",
                LearningContentRelation.Strength.REQUIRED, "DependÃªncia de teste.", null, null);
        when(fixture.relations().findBySourceTypeAndTargetType(
                LearningContentRelation.ContentType.COMPETENCY,
                LearningContentRelation.ContentType.COMPETENCY)).thenReturn(List.of(relation));

        var navigation = fixture.engine().navigate("profile", NOW, 5);

        assertThat(view(navigation, "technique").status()).isEqualTo(CurriculumEngine.CompetencyStatus.BLOCKED);
        assertThat(view(navigation, "technique").prerequisites()).singleElement().satisfies(prerequisite -> {
            assertThat(prerequisite.competencyId()).isEqualTo("pulse");
            assertThat(prerequisite.type()).isEqualTo(CompetencyPrerequisite.Type.STRICT);
        });
    }

    @Test
    void reviewOfFoundationDoesNotEraseEstablishedDescendant() {
        var pulse = competency("pulse", List.of());
        var technique = competency("technique", List.of(
                new CompetencyPrerequisite("pulse", CompetencyPrerequisite.Type.STRICT)));
        var fixture = fixture(List.of(pulse, technique), List.of(
                mastery("pulse", Mastery.State.RETAINED, NOW.minusSeconds(86400), NOW.minusSeconds(1)),
                mastery("technique", Mastery.State.PROBABLE_MASTERY_APPLICATION, NOW.minusSeconds(60), null)));

        var navigation = fixture.engine().navigate("profile", NOW, 5);

        assertThat(view(navigation, "pulse").status()).isEqualTo(CurriculumEngine.CompetencyStatus.REVIEW_DUE);
        assertThat(view(navigation, "technique").status())
                .isEqualTo(CurriculumEngine.CompetencyStatus.ESTABLISHED);
        assertThat(view(navigation, "technique").unlocked()).isTrue();
    }

    private CurriculumEngine.CompetencyView view(CurriculumEngine.Navigation navigation, String id) {
        return navigation.competencies().stream().filter(item -> item.competencyId().equals(id))
                .findFirst().orElseThrow();
    }

    private Competency competency(String id, List<CompetencyPrerequisite> prerequisites) {
        return new Competency(
                id, id, id, "Teste", "Executar " + id, "CompetÃªncia de teste.", "CondiÃ§Ã£o controlada.",
                SkillKind.ABILITY, LearningTrack.TECHNIQUE, LearningStage.BEGINNER,
                List.of(InstrumentId.GUITAR), prerequisites, List.of("criterion"), "policy-v1", 7, null);
    }

    private Mastery mastery(String competencyId, Mastery.State state, Instant lastEvidenceAt, Instant nextReviewAt) {
        var result = new Mastery("profile", competencyId, "policy-v1");
        var probable = state == Mastery.State.PROBABLE_MASTERY_APPLICATION || state == Mastery.State.RETAINED;
        result.reviseHypothesis(
                state, probable, probable ? 2 : 1, probable, false, state == Mastery.State.RETAINED,
                false, "HipÃ³tese de teste.", lastEvidenceAt, nextReviewAt, List.of(), List.of());
        return result;
    }

    private Fixture fixture(List<Competency> competencyList, List<Mastery> masteryList) {
        var profiles = mock(InstrumentProfileRepository.class);
        var paths = mock(LearningPathRepository.class);
        var curricula = mock(CurriculumRepository.class);
        var competencies = mock(CompetencyRepository.class);
        var mastery = mock(MasteryRepository.class);
        var evidence = mock(EvidenceRepository.class);
        var relations = mock(LearningContentRelationRepository.class);
        var profile = new InstrumentProfile(
                "profile", "owner", InstrumentId.GUITAR, "Guitarra", LearningStage.BEGINNER,
                "curriculum", null);
        var orderedIds = competencyList.stream().map(Competency::getId).toList();
        var path = new LearningPath(
                "path", "profile", "curriculum", "Caminho", "test-v1", "Caminho de teste.", List.of(),
                orderedIds.stream().map(id -> new LearningPathStep(
                        id, LearningPathStep.Kind.CORE, LearningPathStep.Readiness.UNASSESSED,
                        "Passo de teste.")).toList());
        var curriculum = new Curriculum(
                "curriculum", "CurrÃ­culo", "test-v1", "PropÃ³sito de teste.", "PÃºblico de teste.",
                InstrumentId.GUITAR, LearningStage.FIRST_STEPS, LearningStage.ADVANCED,
                List.of("Resultado."), orderedIds);

        when(profiles.findById("profile")).thenReturn(Optional.of(profile));
        when(paths.findByInstrumentProfileIdAndStatus("profile", LearningPath.Status.ACTIVE))
                .thenReturn(Optional.of(path));
        when(curricula.findById("curriculum")).thenReturn(Optional.of(curriculum));
        when(competencies.findAll()).thenReturn(competencyList);
        when(mastery.findByInstrumentProfileId("profile")).thenReturn(masteryList);
        when(evidence.findByInstrumentProfileIdOrderByOccurredAtDesc("profile")).thenReturn(List.of());
        when(relations.findBySourceTypeAndTargetType(
                LearningContentRelation.ContentType.COMPETENCY,
                LearningContentRelation.ContentType.COMPETENCY)).thenReturn(List.of());
        return new Fixture(new CurriculumEngine(
                profiles, paths, curricula, competencies, mastery, evidence, relations), mastery, relations);
    }

    private record Fixture(CurriculumEngine engine, MasteryRepository mastery,
                           LearningContentRelationRepository relations) {
    }
}
