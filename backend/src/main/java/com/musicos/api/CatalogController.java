package com.musicos.api;

import static com.musicos.api.ApiModels.*;

import com.musicos.domain.InstrumentId;
import com.musicos.service.CatalogService;
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

    @GetMapping("/exercises/{id}")
    public ExerciseView exercise(@PathVariable String id) {
        return service.exercise(id);
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
