package com.musicos.api;

import static com.musicos.api.ApiModels.*;

import com.musicos.domain.InstrumentId;
import com.musicos.service.LearningContentService;
import com.musicos.service.LearningWorkspaceService;
import com.musicos.service.SongRecommendationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class LearningController {
    private final LearningContentService learning;
    private final SongRecommendationService recommendations;
    private final com.musicos.service.DiagnosticService diagnostic;
    private final LearningWorkspaceService workspace;
    private final com.musicos.service.AssessmentService assessments;
    private final com.musicos.service.MissionExperienceService experiences;

    public LearningController(LearningContentService learning, SongRecommendationService recommendations,
                              com.musicos.service.DiagnosticService diagnostic,
                              LearningWorkspaceService workspace,
                              com.musicos.service.AssessmentService assessments,
                              com.musicos.service.MissionExperienceService experiences) {
        this.learning = learning;
        this.recommendations = recommendations;
        this.diagnostic = diagnostic;
        this.workspace = workspace;
        this.assessments = assessments;
        this.experiences = experiences;
    }

    @PostMapping("/missions/{id}/experience")
    public MissionExperienceView startMission(@PathVariable String id,
                                               @Valid @RequestBody StartMissionExperienceRequest request) {
        return experiences.start(id, request);
    }

    @PatchMapping("/missions/{id}/experience")
    public MissionExperienceView updateMission(@PathVariable String id,
                                                @Valid @RequestBody UpdateMissionExperienceRequest request) {
        return experiences.update(id, request);
    }

    @PostMapping("/missions/{id}/experience/complete")
    public MissionExperienceView completeMission(@PathVariable String id,
                                                  @Valid @RequestBody CompleteMissionExperienceRequest request) {
        return experiences.complete(id, request);
    }

    @GetMapping("/missions/{id}")
    public MissionWorkspaceView mission(@PathVariable String id,
                                        @RequestParam(defaultValue = "guitar") InstrumentId instrument) {
        return workspace.mission(id, instrument);
    }

    @GetMapping("/journey")
    public JourneyView journey(@RequestParam(defaultValue = "guitar") InstrumentId instrument) {
        return workspace.journey(instrument);
    }

    @PutMapping("/library/{id}")
    public LibraryContentView saveLesson(@PathVariable String id,
                                         @Valid @RequestBody LibraryContentRequest request) {
        if (!id.equals(request.id())) throw new IllegalArgumentException("ID divergente");
        return learning.saveLesson(request);
    }

    @DeleteMapping("/library/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLesson(@PathVariable String id) {
        learning.deleteLesson(id);
    }

    @PutMapping("/exercises/{id}")
    public ExerciseView saveExercise(@PathVariable String id, @Valid @RequestBody ExerciseRequest request) {
        if (!id.equals(request.id())) throw new IllegalArgumentException("ID divergente");
        return learning.saveExercise(request);
    }

    @DeleteMapping("/exercises/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExercise(@PathVariable String id) {
        learning.deleteExercise(id);
    }

    @PostMapping("/exercises/{id}/attempts")
    public ExerciseAttemptView recordExerciseAttempt(@PathVariable String id,
                                                     @Valid @RequestBody ExerciseAttemptRequest request) {
        return learning.recordExerciseAttempt(id, request);
    }

    @GetMapping("/exercises/{id}/attempts")
    public List<ExerciseAttemptView> exerciseHistory(@PathVariable String id) {
        return learning.exerciseHistory(id);
    }

    @PostMapping("/assessments/{id}/attempts")
    public AssessmentAttemptView recordAssessment(@PathVariable String id,
                                                   @Valid @RequestBody AssessmentAttemptRequest request) {
        return assessments.record(id, request);
    }

    @PostMapping("/ear-training/attempts")
    public EarTrainingStatsView recordEarAttempt(@Valid @RequestBody EarAttemptRequest request) {
        return learning.recordEarAttempt(request);
    }

    @GetMapping("/ear-training/stats")
    public EarTrainingStatsView earStats() {
        return learning.earStats();
    }

    @GetMapping("/preferences")
    public PreferencesView preferences() {
        return learning.preferences();
    }

    @PutMapping("/preferences")
    public PreferencesView preferences(@Valid @RequestBody PreferencesRequest request) {
        return learning.updatePreferences(request);
    }

    @PostMapping("/diagnostic")
    public DiagnosticView completeDiagnostic(@Valid @RequestBody DiagnosticRequest request) {
        return diagnostic.complete(request);
    }

    @GetMapping("/recommendations/songs")
    public List<SongRecommendationView> songRecommendations(
            @RequestParam(required = false) String skill,
            @RequestParam(defaultValue = "guitar") String instrument) {
        return recommendations.recommend(skill, instrument);
    }

    @GetMapping("/recommendations/exercises")
    public List<SongRecommendationView> exerciseRecommendations(
            @RequestParam String topic,
            @RequestParam String instrument) {
        return recommendations.recommendExercise(topic, InstrumentId.from(instrument));
    }

    @GetMapping("/practice-songs/search")
    public PracticeSongView searchPracticeSong(
            @RequestParam String query,
            @RequestParam(required = false) String instrument) {
        return recommendations.searchPracticeSong(query,
                instrument == null || instrument.isBlank() ? null : InstrumentId.from(instrument));
    }

    @PutMapping("/songs/{id}")
    public SongView saveSong(@PathVariable String id, @Valid @RequestBody SongRequest request) {
        if (!id.equals(request.id())) throw new IllegalArgumentException("ID divergente");
        return learning.saveSong(request);
    }

    @DeleteMapping("/songs/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSong(@PathVariable String id) {
        learning.deleteSong(id);
    }

    @PutMapping("/projects/{id}")
    public ProjectView saveProject(@PathVariable String id, @Valid @RequestBody ProjectRequest request) {
        if (!id.equals(request.id())) throw new IllegalArgumentException("ID divergente");
        return learning.saveProject(request);
    }

    @DeleteMapping("/projects/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable String id) {
        learning.deleteProject(id);
    }
}
