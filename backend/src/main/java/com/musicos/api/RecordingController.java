package com.musicos.api;

import static com.musicos.api.ApiModels.*;

import com.musicos.service.RecordingService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/recordings")
public class RecordingController {
    private final RecordingService service;

    public RecordingController(RecordingService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RecordingView upload(@RequestPart MultipartFile file,
                                @RequestParam String contextType,
                                @RequestParam String contextId,
                                @RequestParam(defaultValue = "0") long durationMillis,
                                @RequestParam(required = false) Integer targetBpm,
                                @RequestParam(required = false) Integer measuredBpm,
                                @RequestParam(required = false) Integer timingOffsetMillis,
                                @RequestParam(required = false) Integer rhythmStability,
                                @RequestParam(required = false) String targetNote,
                                @RequestParam(required = false) Integer pitchOffsetCents,
                                @RequestParam(required = false) Integer bendStability) {
        return service.save(file, contextType, contextId, durationMillis, targetBpm, measuredBpm,
                timingOffsetMillis, rhythmStability, targetNote, pitchOffsetCents, bendStability);
    }

    @GetMapping
    public List<RecordingView> list(@RequestParam String contextType, @RequestParam String contextId) {
        return service.list(contextType, contextId);
    }

    @GetMapping("/{id}/audio")
    public ResponseEntity<org.springframework.core.io.Resource> audio(@PathVariable UUID id) {
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(service.mimeType(id)))
                .body(service.audio(id));
    }
}
