package com.musicos.api;

import com.musicos.domain.InstrumentId;
import com.musicos.domain.SessionStatus;
import com.musicos.domain.SkillState;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public final class ApiModels {
    private ApiModels() {
    }

    public record InstrumentView(InstrumentId id, String name, String shortName, List<String> focus) {}

    public record PlanActivityView(String id, LocalDate scheduledFor, int position, int minutes, String title,
                                   String kind, InstrumentId instrument, String target, boolean done,
                                   String skillId) {}

    public record SkillView(String id, String friendlyTitle, String technicalName, String domain,
                            String description, SkillState state, double hours, int accuracy,
                            Integer currentBpm, Integer targetBpm, List<InstrumentId> instruments,
                            List<String> prerequisites, List<String> contents, List<String> exercises,
                            List<String> songs, List<String> nextSkills, int practiceDays,
                            int reviewCount, int exerciseCompletions, int songsCompleted,
                            int selfRating, Instant lastPracticedAt, int progress,
                            List<String> nextRequirements, int retention, int reviewIntervalDays,
                            Instant nextReviewAt) {}

    public record LibraryContentView(String id, String friendlyTitle, String technicalName, String category,
                                     String summary, List<String> body, List<String> examples,
                                     List<String> related, String skillId, String level, int estimatedMinutes,
                                     String diagramType, String diagramData, String tablature,
                                     List<String> objectives, List<String> commonMistakes,
                                     List<LessonStepView> steps) {}

    public record LessonStepView(String title, String explanation, String musicalExample, String notation,
                                 String tablature, String audioNotes) {}

    public record SongView(String id, String title, String artist, String tuning, String musicalKey, int bpm,
                           InstrumentId instrument, int difficulty, String status, String notes, int progress,
                           List<String> techniques, List<String> scales, List<SongSectionView> sections) {}

    public record SongSectionView(String id, String name, int progress, Integer bpm, String note,
                                  List<String> skillIds, String tablature,
                                  Integer startSeconds, Integer endSeconds, String tonePreset) {
        public SongSectionView(String id, String name, int progress, Integer bpm, String note) {
            this(id, name, progress, bpm, note, List.of(), null, null, null, null);
        }
    }

    public record ExerciseView(String id, String name, String technique, InstrumentId instrument,
                               int targetBpm, int currentBpm, int minutes, String description, String skillId,
                               int difficulty, int minBpm, int bpmStep, int passAccuracy, int passRepetitions,
                               List<String> instructions, List<ExerciseVariationView> variations) {}

    public record ExerciseVariationView(String name, String instructions, int bpmOffset, int durationMinutes) {}
    public record ExerciseAttemptView(UUID id, String exerciseId, Instant practicedAt, int bpm, int accuracy,
                                      long durationSeconds, int repetitions, int perceivedDifficulty,
                                      boolean passed) {}

    public record ProjectView(String id, String name, String musicalKey, int bpm, String status, String lyrics,
                              List<String> ideas, List<String> references, List<ProjectRiffView> riffs,
                              List<ProjectVersionView> versions) {}

    public record ProjectRiffView(String id, String name, String tab) {}
    public record ProjectVersionView(String id, String label, String date) {}

    public record JournalView(UUID id, Instant practicedAt, long durationSeconds, InstrumentId instrument,
                              List<String> worked, String difficulties, String improvements, String notes) {}

    public record ContinueView(String type, String id, String title, String subtitle) {}

    public record ObjectiveView(String id, String title, String technicalName, int progress, SkillState state) {}

    public record HomeView(String greeting, String message, int expectedMinutes,
                           List<PlanActivityView> todayPlan, ContinueView continueFrom,
                           ObjectiveView currentObjective, int streakDays) {}

    public record StartSessionRequest(@NotNull InstrumentId instrument, Integer availableMinutes) {
        public StartSessionRequest(InstrumentId instrument) {
            this(instrument, null);
        }
    }

    public record UpdateSessionRequest(
            @Min(0) long elapsedSeconds,
            @Min(0) int currentActivityIndex,
            String notes,
            SessionStatus status) {}

    public record FinishSessionRequest(
            @Min(0) long elapsedSeconds,
            String notes,
            String difficulties,
            String improvements) {}

    public record SessionView(UUID id, InstrumentId instrument, SessionStatus status, Instant startedAt,
                              Instant finishedAt, long elapsedSeconds, int currentActivityIndex, String notes,
                              List<PlanActivityView> activities) {}

    public record ActivityResultRequest(@NotNull String feedback, @Min(0) int bpm,
                                        @Min(0) int accuracy, @Min(0) long durationSeconds,
                                        int timingOffsetMillis) {}
    public record ActivityResultView(UUID id, String activityId, String title, String feedback,
                                     int bpm, int accuracy, long durationSeconds, int timingOffsetMillis,
                                     int suggestedBpm, String adaptation) {}
    public record SessionSummaryView(UUID sessionId, long practicedSeconds, List<String> improvements,
                                     List<String> difficulties, int peakBpm, int averageAccuracy,
                                     String recommendation, List<ActivityResultView> activities) {}

    public record RecordingView(UUID id, Instant createdAt, String contextType, String contextId,
                                String originalName, String mimeType, long durationMillis,
                                Integer targetBpm, Integer measuredBpm, Integer timingOffsetMillis,
                                Integer rhythmStability, String targetNote, Integer pitchOffsetCents,
                                Integer bendStability, String audioUrl) {}

    public record SkillProgressBackup(String id, SkillState state, double hours, int accuracy,
                                      Integer currentBpm, int practiceDays, int reviewCount,
                                      int exerciseCompletions, int songsCompleted, int selfRating,
                                      Instant lastPracticedAt, int retention, int reviewIntervalDays,
                                      Instant nextReviewAt) {}
    public record JournalBackup(Instant practicedAt, long durationSeconds, InstrumentId instrument,
                                List<String> worked, String difficulties, String improvements, String notes) {}
    public record BackupSnapshot(String version, Instant exportedAt, PreferencesView preferences,
                                 List<LibraryContentRequest> lessons, List<ExerciseRequest> exercises,
                                 List<SongRequest> songs, List<ProjectRequest> projects,
                                 List<SkillProgressBackup> skillProgress, List<JournalBackup> journal) {}
    public record RestoreResult(int lessons, int exercises, int songs, int projects, String message) {}
    public record ImportedFileView(String id, String name, String type, long size, Instant importedAt,
                                   String storedPath) {}
    public record DataStatusView(String dataDirectory, long lessons, long exercises, long songs,
                                 long projects, long journalEntries, long recordings) {}

    public record CompletionRequest(boolean done) {}
    public record SkillStateRequest(@NotNull SkillState state) {}

    public record SkillEvidenceRequest(
            @Min(0) double hours,
            Integer accuracy,
            Integer bpm,
            boolean review,
            boolean exerciseCompleted,
            boolean songCompleted,
            Integer selfRating,
            Integer perceivedDifficulty) {}

    public record LibraryContentRequest(
            @NotNull String id, @NotNull String friendlyTitle, @NotNull String technicalName,
            @NotNull String category, String summary, String skillId, String level,
            @Min(1) int estimatedMinutes, String diagramType, String diagramData, String tablature,
            List<String> objectives, List<String> body, List<String> examples,
            List<String> commonMistakes, List<LessonStepView> steps, List<String> related) {}

    public record ExerciseRequest(
            @NotNull String id, @NotNull String name, @NotNull String technique,
            @NotNull InstrumentId instrument, @Min(1) int targetBpm, @Min(1) int currentBpm,
            @Min(1) int minutes, String description, String skillId, @Min(1) int difficulty,
            @Min(1) int minBpm, @Min(1) int bpmStep, @Min(1) int passAccuracy,
            @Min(1) int passRepetitions, List<String> instructions, List<ExerciseVariationView> variations) {}

    public record ExerciseAttemptRequest(@Min(1) int bpm, @Min(0) int accuracy,
                                         @Min(0) long durationSeconds, @Min(1) int repetitions,
                                         @Min(1) int perceivedDifficulty) {}

    public record EarAttemptRequest(@NotNull String module, @NotNull String prompt, @NotNull String answer,
                                    boolean correct, @Min(0) int responseMillis, @Min(1) int difficulty) {}
    public record EarModuleStatsView(String module, long attempts, long correct, int accuracy,
                                     int averageResponseMillis, int recommendedDifficulty,
                                     String focusPrompt) {}
    public record EarTrainingStatsView(long totalAttempts, int accuracy, List<EarModuleStatsView> modules) {}

    public record PreferencesView(String level, int sessionMinutes, List<String> favoriteGenres,
                                  List<String> favoriteArtists, List<String> favoriteSongs,
                                  InstrumentId primaryInstrument, boolean onboardingCompleted,
                                  int rhythmBaseline, int earBaseline, int techniqueBaseline) {}
    public record PreferencesRequest(@NotNull String level, @Min(15) int sessionMinutes,
                                     List<String> favoriteGenres, List<String> favoriteArtists,
                                     List<String> favoriteSongs, InstrumentId primaryInstrument) {
        public PreferencesRequest(String level, int sessionMinutes, List<String> favoriteGenres,
                                  List<String> favoriteArtists) {
            this(level, sessionMinutes, favoriteGenres, favoriteArtists, List.of(), null);
        }
    }

    public record DiagnosticRequest(
            @NotNull InstrumentId instrument,
            @NotNull String level,
            @Min(15) int sessionMinutes,
            List<String> favoriteGenres,
            List<String> favoriteArtists,
            List<String> favoriteSongs,
            @Min(0) int rhythmScore,
            @Min(0) int earScore,
            @Min(0) int techniqueScore) {}

    public record DiagnosticView(PreferencesView profile, List<SkillView> startingSkills,
                                 String recommendation) {}

    public record SongRecommendationView(String videoId, String title, String channel, String thumbnailUrl,
                                         String reason, String youtubeUrl) {}

    public record PracticeTabSectionView(String id, String name, Integer bpm, String tablature) {}

    public record PracticeInstrumentView(
            InstrumentId instrument,
            String label,
            boolean available,
            String localSongId,
            Integer bpm,
            String tablatureUrl,
            List<PracticeTabSectionView> tablature,
            List<SongRecommendationView> videos,
            List<SongRecommendationView> backingTracks) {}

    public record PracticeSongView(
            String id,
            String title,
            String artist,
            String thumbnailUrl,
            List<PracticeInstrumentView> instruments) {}

    public record SongRequest(
            @NotNull String id, @NotNull String title, @NotNull String artist, String tuning,
            String musicalKey, @Min(1) int bpm, @NotNull InstrumentId instrument,
            @Min(1) int difficulty, String status, String notes, @Min(0) int progress,
            List<String> techniques, List<String> scales, List<SongSectionView> sections) {}

    public record ProjectRequest(
            @NotNull String id, @NotNull String name, String musicalKey, @Min(1) int bpm,
            String status, String lyrics, List<String> ideas, List<String> references,
            List<ProjectRiffView> riffs, List<ProjectVersionView> versions) {}
}
