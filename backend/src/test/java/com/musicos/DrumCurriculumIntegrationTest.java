package com.musicos;

import static com.musicos.api.ApiModels.DiagnosticRequest;
import static com.musicos.api.ApiModels.StartSessionRequest;
import static org.assertj.core.api.Assertions.assertThat;

import com.musicos.domain.InstrumentId;
import com.musicos.service.CatalogService;
import com.musicos.service.DiagnosticService;
import com.musicos.service.PracticeSessionService;
import com.musicos.repository.CompetencyRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class DrumCurriculumIntegrationTest {
    @Autowired
    private CatalogService catalog;
    @Autowired
    private DiagnosticService diagnostic;
    @Autowired
    private PracticeSessionService sessions;
    @Autowired
    private CompetencyRepository competencies;

    @Test
    void exposesAProgressiveDrumTreeWithLessonsExercisesAndSongs() {
        assertThat(catalog.instruments()).anyMatch(value -> value.id() == InstrumentId.DRUMS);

        var skills = competencies.findAll().stream()
                .filter(item -> item.getInstruments().contains(InstrumentId.DRUMS))
                .toList();
        assertThat(skills).hasSizeGreaterThanOrEqualTo(30);
        assertThat(skills).extracting(item -> item.getId())
                .contains("drum-kit-map", "drum-rock-groove", "drum-one-beat-fill", "drum-play-along");

        var fill = competencies.findById("drum-one-beat-fill").orElseThrow();
        assertThat(fill.getPrerequisites()).extracting(item -> item.getCompetencyId())
                .contains("drum-groove-consistency", "drum-single-stroke");
        assertThat(catalog.libraryContent("lesson-drum-one-beat-fill").steps()).hasSize(3);
        assertThat(catalog.exercises(InstrumentId.DRUMS, null)).hasSizeGreaterThanOrEqualTo(8);
        assertThat(catalog.songs(InstrumentId.DRUMS)).hasSizeGreaterThanOrEqualTo(20);
    }

    @Test
    void repertoireStartsWithSmallSectionsAndKeepsInstrumentDifficultyIndependent() {
        var guitar = catalog.songs(InstrumentId.GUITAR);
        var drums = catalog.songs(InstrumentId.DRUMS);

        assertThat(guitar).hasSizeGreaterThanOrEqualTo(20);
        assertThat(drums).hasSizeGreaterThanOrEqualTo(20);
        assertThat(guitar).isSortedAccordingTo(java.util.Comparator
                .comparingInt(com.musicos.api.ApiModels.SongView::difficulty)
                .thenComparing(com.musicos.api.ApiModels.SongView::title));

        var sevenGuitar = guitar.stream()
                .filter(song -> song.id().equals("seven-nation-army-guitar"))
                .findFirst().orElseThrow();
        assertThat(sevenGuitar.difficulty()).isEqualTo(1);
        assertThat(sevenGuitar.sections()).extracting("name")
                .containsExactly("Primeiro trecho", "Versão simplificada", "Versão completa");
        assertThat(sevenGuitar.sections().getFirst().tonePreset()).contains("ganho baixo");

        assertThat(drums).anyMatch(song -> song.id().equals("seven-nation-army-drums"));
    }

    @Test
    void acousticAndKeyboardRepertoireProgressFromSmallParts() {
        var acoustic = catalog.songs(InstrumentId.ACOUSTIC);
        var keys = catalog.songs(InstrumentId.KEYS);
        var byDifficultyAndTitle = java.util.Comparator
                .comparingInt(com.musicos.api.ApiModels.SongView::difficulty)
                .thenComparing(com.musicos.api.ApiModels.SongView::title);

        assertThat(acoustic).hasSizeGreaterThanOrEqualTo(20);
        assertThat(keys).hasSizeGreaterThanOrEqualTo(22);
        assertThat(acoustic).isSortedAccordingTo(byDifficultyAndTitle);
        assertThat(keys).isSortedAccordingTo(byDifficultyAndTitle);

        var horseWithNoName = acoustic.stream()
                .filter(song -> song.id().equals("horse-no-name-acoustic"))
                .findFirst().orElseThrow();
        assertThat(horseWithNoName.difficulty()).isEqualTo(1);
        assertThat(horseWithNoName.sections()).extracting("name")
                .containsExactly("Primeira troca", "Acompanhamento simples", "Arranjo completo");

        var odeToJoy = keys.stream()
                .filter(song -> song.id().equals("ode-to-joy-keys"))
                .findFirst().orElseThrow();
        assertThat(odeToJoy.difficulty()).isEqualTo(1);
        assertThat(odeToJoy.sections()).extracting("name")
                .containsExactly("Uma mão", "Duas mãos simples", "Arranjo completo");
    }

    @Test
    void diagnosticPlacesBeginnerDrummerAndBuildsDrumOnlySession() {
        var result = diagnostic.complete(new DiagnosticRequest(InstrumentId.DRUMS, "beginner", 30,
                List.of("Rock"), List.of("AC/DC"), List.of("Back In Black"),
                58, 35, 60));

        assertThat(result.startingSkills()).isEmpty();

        var session = sessions.start(new StartSessionRequest(InstrumentId.DRUMS, 30));
        assertThat(session.activities()).isEmpty();
        assertThat(session.instrument()).isEqualTo(InstrumentId.DRUMS);
    }
}
