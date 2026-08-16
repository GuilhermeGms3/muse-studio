package com.musicos.service;

import static com.musicos.api.StudioApiModels.*;

import com.musicos.domain.LearningContentRelation;
import com.musicos.domain.StudioProject;
import com.musicos.repository.ExerciseRepository;
import com.musicos.repository.LearningContentRelationRepository;
import com.musicos.repository.MissionExperienceRepository;
import com.musicos.repository.MissionRepository;
import com.musicos.repository.MusicProjectRepository;
import com.musicos.repository.PracticeRecordingRepository;
import com.musicos.repository.PracticeSessionRepository;
import com.musicos.repository.SongRepository;
import com.musicos.repository.StudioProjectRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudioService {
    private final StudioProjectRepository studios;
    private final MissionRepository missions;
    private final MissionExperienceRepository experiences;
    private final ExerciseRepository exercises;
    private final SongRepository songs;
    private final PracticeSessionRepository sessions;
    private final MusicProjectRepository projects;
    private final PracticeRecordingRepository recordings;
    private final LearningContentRelationRepository relations;

    public StudioService(StudioProjectRepository studios, MissionRepository missions,
                         MissionExperienceRepository experiences, ExerciseRepository exercises,
                         SongRepository songs, PracticeSessionRepository sessions,
                         MusicProjectRepository projects, PracticeRecordingRepository recordings,
                         LearningContentRelationRepository relations) {
        this.studios = studios;
        this.missions = missions;
        this.experiences = experiences;
        this.exercises = exercises;
        this.songs = songs;
        this.sessions = sessions;
        this.projects = projects;
        this.recordings = recordings;
        this.relations = relations;
    }

    @Transactional(readOnly = true)
    public List<StudioProjectView> list() {
        return studios.findByOwnerIdOrderByUpdatedAtDesc("default").stream().map(this::view).toList();
    }

    @Transactional(readOnly = true)
    public StudioProjectView get(UUID id) { return view(require(id)); }

    @Transactional
    public StudioProjectView create(CreateStudioProjectRequest request) {
        var sourceId = sourceId(request);
        if (sourceId != null) {
            var current = studios.findFirstByOwnerIdAndSourceKindAndSourceIdOrderByUpdatedAtDesc(
                    "default", request.sourceKind(), sourceId);
            if (current.isPresent()) return view(current.get());
        }

        var title = request.title();
        var bpm = request.bpm() == null ? 80 : request.bpm();
        var regions = new ArrayList<StudioProject.Region>();
        String missionId = request.missionId();
        UUID experienceId = request.missionExperienceId();
        String exerciseId = request.exerciseId();
        String songId = request.songId();
        String musicProjectId = request.musicProjectId();

        if (request.sourceKind() == StudioProject.SourceKind.MISSION) {
            missionId = required(sourceId, "Mission");
            var mission = missions.findById(missionId)
                    .orElseThrow(() -> new NotFoundException("Mission não encontrada"));
            title = blank(title) ? mission.getTitle() : title;
            if (experienceId != null && !experiences.existsById(experienceId)) {
                throw new NotFoundException("Experiência da Mission não encontrada");
            }
            if (exerciseId == null && !mission.getExerciseIds().isEmpty()) {
                exerciseId = mission.getExerciseIds().getLast();
            }
            if (exerciseId != null) {
                var exercise = exercises.findById(exerciseId)
                        .orElseThrow(() -> new NotFoundException("Exercício não encontrado"));
                bpm = request.bpm() == null ? exercise.getCurrentBpm() : bpm;
            }
            var relatedSong = relations.findBySourceTypeAndSourceId(
                            LearningContentRelation.ContentType.MISSION, missionId).stream()
                    .filter(item -> item.getTargetType() == LearningContentRelation.ContentType.SONG)
                    .map(LearningContentRelation::getTargetId).findFirst().orElse(null);
            if (songId == null) songId = relatedSong;
        } else if (request.sourceKind() == StudioProject.SourceKind.REPERTOIRE) {
            songId = required(sourceId, "Música");
        } else if (request.sourceKind() == StudioProject.SourceKind.PRACTICE
                && request.practiceSessionId() != null) {
            sessions.findById(request.practiceSessionId())
                    .orElseThrow(() -> new NotFoundException("Sessão de prática não encontrada"));
        } else if (request.sourceKind() == StudioProject.SourceKind.CREATION) {
            musicProjectId = required(sourceId, "Projeto de criação");
            var creation = projects.findById(musicProjectId)
                    .orElseThrow(() -> new NotFoundException("Projeto de criação não encontrado"));
            title = blank(title) ? creation.getName() : title;
            bpm = request.bpm() == null ? creation.getBpm() : bpm;
        }

        if (songId != null) {
            var song = songs.findById(songId).orElseThrow(() -> new NotFoundException("Música não encontrada"));
            title = blank(title) ? "Studio · " + song.getTitle() : title;
            bpm = request.bpm() == null ? song.getBpm() : bpm;
            double cursor = 0;
            for (var section : song.getSections()) {
                var start = section.getStartSeconds() == null ? cursor : section.getStartSeconds().doubleValue();
                var end = section.getEndSeconds() == null ? start + barsSeconds(bpm, 8) : section.getEndSeconds();
                regions.add(new StudioProject.Region(null, section.getName(), start, end,
                        StudioProject.RegionOrigin.REPERTOIRE));
                cursor = end;
            }
        }

        if (blank(title)) title = request.sourceKind() == StudioProject.SourceKind.FREE
                ? "Prática livre" : "Studio";
        var studio = new StudioProject(title, request.instrument(), request.sourceKind(), sourceId, bpm);
        studio.linkContext(missionId, experienceId, exerciseId, request.practiceSessionId(), songId, musicProjectId);
        var tracks = new ArrayList<StudioProject.Track>();
        if (songId != null) tracks.add(new StudioProject.Track(null, "Backing / referência",
                StudioProject.TrackRole.REFERENCE, false, false, 0.85, 0, null));
        if (request.sourceKind() == StudioProject.SourceKind.CREATION) {
            tracks.add(new StudioProject.Track(null, "Ideia musical", StudioProject.TrackRole.CREATIVE,
                    false, false, 0.9, 0, null));
        }
        tracks.add(new StudioProject.Track(null, instrumentTrackName(request.instrument()),
                StudioProject.TrackRole.RECORDING, false, false, 1, 0, null));
        studio.replaceStructure(tracks, List.of(), regions,
                regions.stream().map(region -> new StudioProject.Marker(null, region.name(),
                        region.startSeconds(), region.origin())).toList());
        return view(studios.save(studio));
    }

    @Transactional
    public StudioProjectView update(UUID id, UpdateStudioProjectRequest request) {
        var studio = require(id);
        studio.rename(request.title());
        var tracks = request.tracks().stream().map(item -> new StudioProject.Track(item.id(), item.name(),
                item.role(), item.muted(), item.solo(), item.volume(), item.pan(), item.externalTrackId())).toList();
        var clips = request.clips().stream().map(item -> new StudioProject.Clip(item.id(), item.trackId(),
                item.title(), item.sourceKind(), item.recordingId(), item.sourceReference(), item.startSeconds(),
                item.offsetSeconds(), item.durationSeconds())).toList();
        var regions = request.regions().stream().map(item -> new StudioProject.Region(item.id(), item.name(),
                item.startSeconds(), item.endSeconds(), item.origin())).toList();
        var markers = request.markers().stream().map(item -> new StudioProject.Marker(item.id(), item.name(),
                item.positionSeconds(), item.origin())).toList();
        studio.replaceStructure(tracks, clips, regions, markers);
        studio.configure(request.bpm(), request.timeSignatureNumerator(), request.timeSignatureDenominator(),
                request.countInBars(), request.loopEnabled(), request.selectedRegionId(), request.engineMode());
        return view(studios.save(studio));
    }

    @Transactional
    public StudioProjectView addTake(UUID id, AddStudioTakeRequest request) {
        var studio = require(id);
        if (!recordings.existsById(request.recordingId())) throw new NotFoundException("Gravação não encontrada");
        var number = studio.getTakes().size() + 1;
        studio.addTake(new StudioProject.Take(null, request.trackId(), request.recordingId(),
                blank(request.title()) ? "Take " + number : request.title(), false, Instant.now(), null));
        return view(studios.save(studio));
    }

    @Transactional
    public StudioProjectView addClip(UUID id, AddStudioClipRequest request) {
        var studio = require(id);
        if (!recordings.existsById(request.recordingId())) throw new NotFoundException("Áudio não encontrado");
        var clips = new ArrayList<>(studio.getClips());
        clips.add(new StudioProject.Clip(null, request.trackId(),
                blank(request.title()) ? "Áudio importado" : request.title(),
                StudioProject.ClipSourceKind.MANAGED_AUDIO, request.recordingId(), null,
                request.startSeconds(), 0, request.durationSeconds()));
        studio.replaceStructure(studio.getTracks(), clips, studio.getRegions(), studio.getMarkers());
        return view(studios.save(studio));
    }

    @Transactional
    public StudioProjectView updateTake(UUID id, UUID takeId, UpdateStudioTakeRequest request) {
        var studio = require(id);
        studio.updateTake(takeId, request.title(), request.preferred());
        return view(studios.save(studio));
    }

    @Transactional
    public StudioProject mutable(UUID id) { return require(id); }

    @Transactional
    public void persist(StudioProject project) { studios.save(project); }

    private StudioProject require(UUID id) {
        return studios.findById(id).orElseThrow(() -> new NotFoundException("Studio não encontrado"));
    }

    public StudioProjectView view(StudioProject value) {
        return new StudioProjectView(value.getId(), value.getTitle(), value.getInstrument(),
                value.getSourceKind().name(), value.getSourceId(), value.getMissionId(),
                value.getMissionExperienceId(), value.getExerciseId(), value.getPracticeSessionId(),
                value.getSongId(), value.getMusicProjectId(), value.getBpm(),
                value.getTimeSignatureNumerator(), value.getTimeSignatureDenominator(), value.getCountInBars(),
                value.isLoopEnabled(), value.getSelectedRegionId(), value.getEngineMode().name(),
                value.getExternalProjectId(), value.getExternalProjectPath(), value.getCreatedAt(),
                value.getUpdatedAt(), value.getTracks().stream().map(item -> new StudioTrackView(item.id(),
                        item.name(), item.role().name(), item.muted(), item.solo(), item.volume(), item.pan(),
                        item.externalTrackId())).toList(), value.getClips().stream().map(item ->
                        new StudioClipView(item.id(), item.trackId(), item.title(), item.sourceKind().name(),
                                item.recordingId(), item.sourceReference(), audioUrl(item.recordingId()),
                                item.startSeconds(), item.offsetSeconds(), item.durationSeconds())).toList(),
                value.getRegions().stream().map(item -> new StudioRegionView(item.id(), item.name(),
                        item.startSeconds(), item.endSeconds(), item.origin().name())).toList(),
                value.getMarkers().stream().map(item -> new StudioMarkerView(item.id(), item.name(),
                        item.positionSeconds(), item.origin().name())).toList(),
                value.getTakes().stream().map(item -> new StudioTakeView(item.id(), item.trackId(),
                        item.recordingId(), item.title(), item.preferred(), item.createdAt(), item.externalTakeId(),
                        audioUrl(item.recordingId()))).toList());
    }

    private String audioUrl(UUID recordingId) {
        return recordingId == null ? null : "/api/v1/recordings/" + recordingId + "/audio";
    }

    private String sourceId(CreateStudioProjectRequest request) {
        if (!blank(request.sourceId())) return request.sourceId();
        return switch (request.sourceKind()) {
            case MISSION -> request.missionId();
            case REPERTOIRE -> request.songId();
            case PRACTICE -> request.practiceSessionId() == null ? null : request.practiceSessionId().toString();
            case CREATION -> request.musicProjectId();
            case FREE -> null;
        };
    }

    private String required(String value, String label) {
        if (blank(value)) throw new IllegalArgumentException(label + " é obrigatório");
        return value;
    }
    private boolean blank(String value) { return value == null || value.isBlank(); }
    private double barsSeconds(int bpm, int bars) { return bars * 4 * 60.0 / bpm; }
    private String instrumentTrackName(com.musicos.domain.InstrumentId instrument) {
        return switch (instrument) {
            case GUITAR -> "Minha guitarra";
            case ACOUSTIC -> "Meu violão";
            case KEYS -> "Minhas teclas";
            case DRUMS -> "Minha bateria";
        };
    }
}
