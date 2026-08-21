package com.musicos.api;

import static com.musicos.api.StudioApiModels.*;

import com.musicos.integration.reaper.ReaperBridge;
import com.musicos.service.StudioService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class StudioController {
    private final StudioService studio;
    private final ReaperBridge reaper;

    public StudioController(StudioService studio, ReaperBridge reaper) {
        this.studio = studio;
        this.reaper = reaper;
    }

    @GetMapping("/studio/projects")
    public List<StudioProjectView> projects() { return studio.list(); }

    @PostMapping("/studio/projects")
    public StudioProjectView create(@Valid @RequestBody CreateStudioProjectRequest request) {
        return studio.create(request);
    }

    @GetMapping("/studio/projects/{id}")
    public StudioProjectView project(@PathVariable UUID id) { return studio.get(id); }

    @PutMapping("/studio/projects/{id}")
    public StudioProjectView update(@PathVariable UUID id,
                                    @Valid @RequestBody UpdateStudioProjectRequest request) {
        return studio.update(id, request);
    }

    @PostMapping("/studio/projects/{id}/takes")
    public StudioProjectView addTake(@PathVariable UUID id,
                                     @Valid @RequestBody AddStudioTakeRequest request) {
        return studio.addTake(id, request);
    }

    @PostMapping("/studio/projects/{id}/clips")
    public StudioProjectView addClip(@PathVariable UUID id,
                                     @Valid @RequestBody AddStudioClipRequest request) {
        return studio.addClip(id, request);
    }

    @PatchMapping("/studio/projects/{id}/takes/{takeId}")
    public StudioProjectView updateTake(@PathVariable UUID id, @PathVariable UUID takeId,
                                        @Valid @RequestBody UpdateStudioTakeRequest request) {
        return studio.updateTake(id, takeId, request);
    }

    @GetMapping("/integrations/reaper")
    public ReaperStatusView reaperStatus() { return reaper.status(); }

    @PutMapping("/integrations/reaper")
    public ReaperStatusView configureReaper(@Valid @RequestBody ReaperConfigurationRequest request) {
        return reaper.configure(request);
    }

    @PostMapping("/integrations/reaper/test")
    public ReaperStatusView testReaper() { return reaper.testConnection(); }

    @DeleteMapping("/integrations/reaper")
    public ReaperStatusView disconnectReaper() { return reaper.disconnect(); }

    @PostMapping("/integrations/reaper/transport")
    public ReaperOperationView reaperTransport(@Valid @RequestBody ReaperTransportRequest request) {
        return reaper.transport(request);
    }

    @PostMapping("/studio/projects/{id}/reaper/commands")
    public ReaperOperationView reaperCommand(@PathVariable UUID id,
                                             @Valid @RequestBody ReaperOperationRequest request) {
        return reaper.command(studio.mutable(id), request);
    }

    @PostMapping("/studio/projects/{id}/open-in-reaper")
    public OpenInReaperView openInReaper(@PathVariable UUID id) {
        var project = studio.mutable(id);
        var result = reaper.open(project);
        studio.persist(project);
        return result;
    }
}
