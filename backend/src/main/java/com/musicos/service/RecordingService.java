package com.musicos.service;

import static com.musicos.api.ApiModels.*;

import com.musicos.domain.PracticeRecording;
import com.musicos.repository.PracticeRecordingRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class RecordingService {
    private final PracticeRecordingRepository recordings;
    private final Path root;

    public RecordingService(PracticeRecordingRepository recordings,
                            @Value("${music-os.data-dir:${java.io.tmpdir}/music-os}") String dataDir) {
        this.recordings = recordings;
        this.root = Path.of(dataDir).toAbsolutePath().normalize().resolve("recordings");
    }

    @Transactional
    public RecordingView save(MultipartFile file, String contextType, String contextId, long durationMillis,
                              Integer targetBpm, Integer measuredBpm, Integer timingOffsetMillis,
                              Integer rhythmStability, String targetNote, Integer pitchOffsetCents,
                              Integer bendStability) {
        if (file.isEmpty()) throw new IllegalArgumentException("A gravacao esta vazia");
        try {
            Files.createDirectories(root);
            var extension = extension(file.getOriginalFilename(), file.getContentType());
            var fileName = UUID.randomUUID() + extension;
            var destination = root.resolve(fileName).normalize();
            if (!destination.startsWith(root)) throw new IllegalArgumentException("Nome de arquivo invalido");
            file.transferTo(destination);
            var saved = recordings.save(new PracticeRecording(contextType, contextId, fileName,
                    file.getOriginalFilename() == null ? "gravacao" + extension : file.getOriginalFilename(),
                    file.getContentType(), durationMillis, targetBpm, measuredBpm, timingOffsetMillis,
                    rhythmStability, targetNote, pitchOffsetCents, bendStability));
            return view(saved);
        } catch (IOException exception) {
            throw new IllegalStateException("Nao foi possivel salvar a gravacao", exception);
        }
    }

    @Transactional(readOnly = true)
    public List<RecordingView> list(String contextType, String contextId) {
        return recordings.findTop10ByContextTypeAndContextIdOrderByCreatedAtDesc(contextType, contextId)
                .stream().map(this::view).toList();
    }

    @Transactional(readOnly = true)
    public Resource audio(UUID id) {
        var recording = recordings.findById(id)
                .orElseThrow(() -> new NotFoundException("Gravacao nao encontrada"));
        try {
            var resource = new UrlResource(root.resolve(recording.getFileName()).toUri());
            if (!resource.exists()) throw new NotFoundException("Arquivo de audio nao encontrado");
            return resource;
        } catch (java.net.MalformedURLException exception) {
            throw new IllegalStateException("Arquivo de audio invalido", exception);
        }
    }

    @Transactional(readOnly = true)
    public String mimeType(UUID id) {
        return recordings.findById(id).map(PracticeRecording::getMimeType).orElse("audio/webm");
    }

    private RecordingView view(PracticeRecording value) {
        return new RecordingView(value.getId(), value.getCreatedAt(), value.getContextType(),
                value.getContextId(), value.getOriginalName(), value.getMimeType(), value.getDurationMillis(),
                value.getTargetBpm(), value.getMeasuredBpm(), value.getTimingOffsetMillis(),
                value.getRhythmStability(), value.getTargetNote(), value.getPitchOffsetCents(),
                value.getBendStability(), "/api/v1/recordings/" + value.getId() + "/audio");
    }

    private String extension(String name, String mimeType) {
        if (name != null && name.lastIndexOf('.') >= 0) {
            var value = name.substring(name.lastIndexOf('.')).replaceAll("[^a-zA-Z0-9.]", "");
            if (value.length() <= 8) return value;
        }
        return mimeType != null && mimeType.contains("ogg") ? ".ogg" : ".webm";
    }
}
