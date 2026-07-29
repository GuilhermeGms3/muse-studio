package com.musicos.api;

import static com.musicos.api.ApiModels.*;

import com.musicos.domain.InstrumentId;
import com.musicos.domain.SkillState;
import com.musicos.service.CatalogService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CatalogController {
    private final CatalogService service;

    public CatalogController(CatalogService service) {
        this.service = service;
    }

    @GetMapping("/instruments")
    public List<InstrumentView> instruments() {
        return service.instruments();
    }

    @GetMapping("/plans/today")
    public List<PlanActivityView> todayPlan(
            @RequestParam(defaultValue = "guitar") InstrumentId instrument) {
        return service.todayPlan(instrument);
    }

    @PostMapping("/plans/today/regenerate")
    public List<PlanActivityView> regeneratePlan(
            @RequestParam(defaultValue = "guitar") InstrumentId instrument) {
        return service.regeneratePlan(instrument);
    }

    @PatchMapping("/plans/activities/{id}")
    public PlanActivityView completeActivity(@PathVariable String id, @RequestBody CompletionRequest request) {
        return service.completeActivity(id, request.done());
    }

    @GetMapping("/skills")
    public List<SkillView> skills(
            @RequestParam(defaultValue = "guitar") InstrumentId instrument,
            @RequestParam(required = false) SkillState state) {
        return service.skills(instrument, state);
    }

    @GetMapping("/skills/{id}")
    public SkillView skill(@PathVariable String id) {
        return service.skill(id);
    }

    @PatchMapping("/skills/{id}/state")
    public SkillView changeSkillState(@PathVariable String id, @RequestBody SkillStateRequest request) {
        return service.changeSkillState(id, request.state());
    }

    @PostMapping("/skills/{id}/evidence")
    public SkillView recordEvidence(@PathVariable String id, @Valid @RequestBody SkillEvidenceRequest request) {
        return service.recordEvidence(id, request);
    }

    @GetMapping("/library")
    public List<LibraryContentView> library(@RequestParam(required = false) String category) {
        return service.library(category);
    }

    @GetMapping("/library/{id}")
    public LibraryContentView libraryContent(@PathVariable String id) {
        return service.libraryContent(id);
    }

    @GetMapping("/songs")
    public List<SongView> songs(@RequestParam(required = false) InstrumentId instrument) {
        return service.songs(instrument);
    }

    @GetMapping("/songs/{id}")
    public SongView song(@PathVariable String id) {
        return service.song(id);
    }

    @GetMapping("/exercises")
    public List<ExerciseView> exercises(
            @RequestParam(defaultValue = "guitar") InstrumentId instrument,
            @RequestParam(required = false) String technique) {
        return service.exercises(instrument, technique);
    }

    @GetMapping("/projects")
    public List<ProjectView> projects() {
        return service.projects();
    }

    @GetMapping("/projects/{id}")
    public ProjectView project(@PathVariable String id) {
        return service.project(id);
    }

    @GetMapping("/journal")
    public List<JournalView> journal(@RequestParam(required = false) InstrumentId instrument) {
        return service.journal(instrument);
    }
}
