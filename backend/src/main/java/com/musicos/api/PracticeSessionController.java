package com.musicos.api;

import static com.musicos.api.ApiModels.*;

import com.musicos.service.PracticeSessionService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sessions")
public class PracticeSessionController {
    private final PracticeSessionService service;

    public PracticeSessionController(PracticeSessionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SessionView> start(@Valid @RequestBody StartSessionRequest request) {
        var session = service.start(request);
        return ResponseEntity.created(URI.create("/api/v1/sessions/" + session.id())).body(session);
    }

    @GetMapping("/{id}")
    public SessionView get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PatchMapping("/{id}")
    public SessionView update(@PathVariable UUID id, @Valid @RequestBody UpdateSessionRequest request) {
        return service.update(id, request);
    }

    @PostMapping("/{id}/finish")
    public SessionView finish(@PathVariable UUID id, @Valid @RequestBody FinishSessionRequest request) {
        return service.finish(id, request);
    }

    @PostMapping("/{id}/activities/{activityId}/result")
    public ActivityResultView recordActivity(@PathVariable UUID id, @PathVariable String activityId,
                                             @Valid @RequestBody ActivityResultRequest request) {
        return service.recordActivity(id, activityId, request);
    }

    @GetMapping("/{id}/summary")
    public SessionSummaryView summary(@PathVariable UUID id) {
        return service.summary(id);
    }
}
