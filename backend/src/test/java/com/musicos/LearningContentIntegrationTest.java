package com.musicos;

import static com.musicos.api.ApiModels.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.musicos.domain.InstrumentId;
import com.musicos.repository.ExerciseRepository;
import com.musicos.repository.LibraryContentRepository;
import com.musicos.service.LearningContentService;
import com.musicos.service.SongRecommendationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "music-os.youtube.api-key=")
class LearningContentIntegrationTest {
    @Autowired
    private LibraryContentRepository library;

    @Autowired
    private ExerciseRepository exercises;

    @Autowired
    private LearningContentService learning;

    @Autowired
    private SongRecommendationService recommendations;

    @Test
    void everyCurriculumBranchReceivesStructuredLearningContent() {
        var lessons = library.findAll();

        assertThat(lessons).hasSizeGreaterThan(70);
        assertThat(lessons).allSatisfy(lesson -> {
            assertThat(lesson.getObjectives()).isNotEmpty();
            assertThat(lesson.getSteps()).hasSizeGreaterThanOrEqualTo(3);
            assertThat(lesson.getCommonMistakes()).isNotEmpty();
        });
        assertThat(exercises.count()).isGreaterThan(75);
    }

    @Test
    void exerciseAndEarAttemptsArePersistedAndEvaluated() {
        var exercise = exercises.findByInstrumentOrderByTechniqueAscNameAsc(InstrumentId.GUITAR).getFirst();
        var attempt = learning.recordExerciseAttempt(exercise.getId(),
                new ExerciseAttemptRequest(exercise.getCurrentBpm(), 90, 180, 3, 2));

        assertThat(attempt.passed()).isTrue();
        assertThat(learning.exerciseHistory(exercise.getId())).isNotEmpty();

        var stats = learning.recordEarAttempt(new EarAttemptRequest(
                "intervals", "3M", "3M", true, 1200, 1));
        assertThat(stats.totalAttempts()).isPositive();
        assertThat(stats.accuracy()).isEqualTo(100);
    }

    @Test
    void preferencesDriveSafeRecommendationFallbackWithoutASecret() {
        var preferences = learning.updatePreferences(new PreferencesRequest(
                "intermediate", 45, java.util.List.of("Rock"), java.util.List.of("Jimi Hendrix")));
        var result = recommendations.recommend("Bends", "guitar");

        assertThat(preferences.sessionMinutes()).isEqualTo(45);
        assertThat(result).isNotEmpty();
        assertThat(result.getFirst().youtubeUrl()).startsWith("https://www.youtube.com/");
    }

    @Test
    void practiceSongSearchReturnsOneSongWithInstrumentWorkspaces() {
        var result = recommendations.searchPracticeSong("Sweet Child O' Mine", InstrumentId.GUITAR);

        assertThat(result.title()).isEqualTo("Sweet Child O' Mine");
        assertThat(result.instruments()).extracting(PracticeInstrumentView::instrument)
                .containsExactly(InstrumentId.DRUMS, InstrumentId.GUITAR,
                        InstrumentId.ACOUSTIC, InstrumentId.KEYS);
        assertThat(result.instruments()).filteredOn(item -> item.instrument() == InstrumentId.GUITAR)
                .allSatisfy(item -> {
                    assertThat(item.available()).isTrue();
                    assertThat(item.tablature()).isNotEmpty();
                    assertThat(item.videos()).isNotEmpty();
                    assertThat(item.vocalTracks()).isNotEmpty();
                    assertThat(item.backingTracks()).isNotEmpty();
                });
    }
}
