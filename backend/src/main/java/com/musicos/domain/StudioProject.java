package com.musicos.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "studio_projects")
public class StudioProject {
    public enum SourceKind { FREE, MISSION, REPERTOIRE, PRACTICE, CREATION }
    public enum EngineMode { WEB, REAPER }
    public enum TrackRole { BACKING, REFERENCE, RECORDING, CLICK, CREATIVE }
    public enum ClipSourceKind { RECORDING, MANAGED_AUDIO, EXTERNAL_REFERENCE }
    public enum RegionOrigin { MISSION, EXERCISE, REPERTOIRE, USER }
    public enum BoundaryConfidence { DEFINED, ESTIMATED, UNKNOWN }

    @Id
    private UUID id;
    private String ownerId;
    private String title;
    @Enumerated(EnumType.STRING)
    private InstrumentId instrument;
    @Enumerated(EnumType.STRING)
    private SourceKind sourceKind;
    private String sourceId;
    private String missionId;
    private UUID missionExperienceId;
    private String exerciseId;
    private UUID practiceSessionId;
    private String songId;
    private String musicProjectId;
    private int bpm;
    private int timeSignatureNumerator;
    private int timeSignatureDenominator;
    private int countInBars;
    private boolean loopEnabled;
    private UUID selectedRegionId;
    @Enumerated(EnumType.STRING)
    private EngineMode engineMode;
    private String externalProjectId;
    private String externalProjectPath;
    private Instant createdAt;
    private Instant updatedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "studio_tracks", joinColumns = @JoinColumn(name = "project_id"))
    @OrderColumn(name = "position_index")
    private List<Track> tracks = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "studio_clips", joinColumns = @JoinColumn(name = "project_id"))
    @OrderColumn(name = "position_index")
    private List<Clip> clips = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "studio_regions", joinColumns = @JoinColumn(name = "project_id"))
    @OrderColumn(name = "position_index")
    private List<Region> regions = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "studio_markers", joinColumns = @JoinColumn(name = "project_id"))
    @OrderColumn(name = "position_index")
    private List<Marker> markers = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "studio_takes", joinColumns = @JoinColumn(name = "project_id"))
    @OrderColumn(name = "position_index")
    private List<Take> takes = new ArrayList<>();

    protected StudioProject() {}

    public StudioProject(String title, InstrumentId instrument, SourceKind sourceKind, String sourceId, int bpm) {
        this.id = UUID.randomUUID();
        this.ownerId = LocalProfile.DEFAULT_ID;
        this.title = title;
        this.instrument = instrument;
        this.sourceKind = sourceKind;
        this.sourceId = sourceId;
        this.bpm = bpm;
        this.timeSignatureNumerator = 4;
        this.timeSignatureDenominator = 4;
        this.countInBars = 1;
        this.engineMode = EngineMode.WEB;
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
    }

    public void configure(int bpm, int numerator, int denominator, int countInBars, boolean loopEnabled,
                          UUID selectedRegionId, EngineMode engineMode) {
        if (bpm < 30 || bpm > 300) throw new IllegalArgumentException("BPM deve estar entre 30 e 300");
        if (numerator < 1 || numerator > 16 || denominator < 1 || denominator > 16) {
            throw new IllegalArgumentException("Compasso inválido");
        }
        if (countInBars < 0 || countInBars > 8) throw new IllegalArgumentException("Contagem inválida");
        if (selectedRegionId != null && regions.stream().noneMatch(item -> item.id.equals(selectedRegionId))) {
            throw new IllegalArgumentException("Região selecionada não pertence ao projeto");
        }
        this.bpm = bpm;
        this.timeSignatureNumerator = numerator;
        this.timeSignatureDenominator = denominator;
        this.countInBars = countInBars;
        this.loopEnabled = loopEnabled;
        this.selectedRegionId = selectedRegionId;
        this.engineMode = engineMode == null ? EngineMode.WEB : engineMode;
        touch();
    }

    public void rename(String title) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Título é obrigatório");
        this.title = title.trim();
        touch();
    }

    public void linkContext(String missionId, UUID missionExperienceId, String exerciseId,
                            UUID practiceSessionId, String songId, String musicProjectId) {
        this.missionId = missionId;
        this.missionExperienceId = missionExperienceId;
        this.exerciseId = exerciseId;
        this.practiceSessionId = practiceSessionId;
        this.songId = songId;
        this.musicProjectId = musicProjectId;
        touch();
    }

    public void replaceStructure(List<Track> tracks, List<Clip> clips, List<Region> regions, List<Marker> markers) {
        var trackIds = tracks.stream().map(Track::id).toList();
        if (trackIds.size() != trackIds.stream().distinct().count()) throw new IllegalArgumentException("Track repetida");
        if (clips.stream().anyMatch(clip -> !trackIds.contains(clip.trackId()))) {
            throw new IllegalArgumentException("Clip precisa pertencer a uma track do projeto");
        }
        if (regions.stream().anyMatch(region -> region.startSeconds() < 0
                || region.endSeconds() <= region.startSeconds())) {
            throw new IllegalArgumentException("Região precisa possuir início e fim válidos");
        }
        this.tracks = new ArrayList<>(tracks);
        this.clips = new ArrayList<>(clips);
        this.regions = new ArrayList<>(regions);
        this.markers = new ArrayList<>(markers);
        touch();
    }

    public void addTake(Take take) {
        if (tracks.stream().noneMatch(track -> track.id.equals(take.trackId))) {
            throw new IllegalArgumentException("Take precisa pertencer a uma track do projeto");
        }
        takes.add(take);
        touch();
    }

    public void updateTake(UUID takeId, String title, boolean preferred) {
        var target = takes.stream().filter(item -> item.id.equals(takeId)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Take não encontrado"));
        if (preferred) takes.forEach(item -> item.preferred = false);
        target.title = title == null || title.isBlank() ? target.title : title.trim();
        target.preferred = preferred;
        touch();
    }

    public void attachExternalProject(String externalId, String path) {
        this.externalProjectId = externalId;
        this.externalProjectPath = path;
        this.engineMode = EngineMode.REAPER;
        touch();
    }

    public void ensureExternalIdentities() {
        tracks.forEach(item -> { if (item.externalTrackId == null) item.externalTrackId = UUID.randomUUID().toString(); });
        clips.forEach(item -> { if (item.externalItemId == null) item.externalItemId = UUID.randomUUID().toString(); });
        regions.forEach(item -> { if (item.externalRegionId == null) item.externalRegionId = UUID.randomUUID().toString(); });
        markers.forEach(item -> { if (item.externalMarkerId == null) item.externalMarkerId = UUID.randomUUID().toString(); });
        takes.forEach(item -> { if (item.externalTakeId == null) item.externalTakeId = UUID.randomUUID().toString(); });
        touch();
    }

    private void touch() { this.updatedAt = Instant.now(); }

    public UUID getId() { return id; }
    public String getOwnerId() { return ownerId; }
    public String getTitle() { return title; }
    public InstrumentId getInstrument() { return instrument; }
    public SourceKind getSourceKind() { return sourceKind; }
    public String getSourceId() { return sourceId; }
    public String getMissionId() { return missionId; }
    public UUID getMissionExperienceId() { return missionExperienceId; }
    public String getExerciseId() { return exerciseId; }
    public UUID getPracticeSessionId() { return practiceSessionId; }
    public String getSongId() { return songId; }
    public String getMusicProjectId() { return musicProjectId; }
    public int getBpm() { return bpm; }
    public int getTimeSignatureNumerator() { return timeSignatureNumerator; }
    public int getTimeSignatureDenominator() { return timeSignatureDenominator; }
    public int getCountInBars() { return countInBars; }
    public boolean isLoopEnabled() { return loopEnabled; }
    public UUID getSelectedRegionId() { return selectedRegionId; }
    public EngineMode getEngineMode() { return engineMode; }
    public String getExternalProjectId() { return externalProjectId; }
    public String getExternalProjectPath() { return externalProjectPath; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public List<Track> getTracks() { return List.copyOf(tracks); }
    public List<Clip> getClips() { return List.copyOf(clips); }
    public List<Region> getRegions() { return List.copyOf(regions); }
    public List<Marker> getMarkers() { return List.copyOf(markers); }
    public List<Take> getTakes() { return List.copyOf(takes); }

    @Embeddable
    public static class Track {
        private UUID id;
        private String name;
        @Enumerated(EnumType.STRING)
        private TrackRole role;
        private boolean muted;
        private boolean solo;
        private double volume;
        private double pan;
        private String externalTrackId;
        protected Track() {}
        public Track(UUID id, String name, TrackRole role, boolean muted, boolean solo,
                     double volume, double pan, String externalTrackId) {
            this.id = id == null ? UUID.randomUUID() : id;
            this.name = name;
            this.role = role;
            this.muted = muted;
            this.solo = solo;
            this.volume = Math.max(0, Math.min(1, volume));
            this.pan = Math.max(-1, Math.min(1, pan));
            this.externalTrackId = externalTrackId;
        }
        public UUID id() { return id; }
        public String name() { return name; }
        public TrackRole role() { return role; }
        public boolean muted() { return muted; }
        public boolean solo() { return solo; }
        public double volume() { return volume; }
        public double pan() { return pan; }
        public String externalTrackId() { return externalTrackId; }
    }

    @Embeddable
    public static class Clip {
        private UUID id;
        private UUID trackId;
        private String title;
        @Enumerated(EnumType.STRING)
        private ClipSourceKind sourceKind;
        private UUID recordingId;
        @Column(length = 2000)
        private String sourceReference;
        private double startSeconds;
        private double offsetSeconds;
        private double durationSeconds;
        private String externalItemId;
        protected Clip() {}
        public Clip(UUID id, UUID trackId, String title, ClipSourceKind sourceKind, UUID recordingId,
                    String sourceReference, double startSeconds, double offsetSeconds, double durationSeconds) {
            this.id = id == null ? UUID.randomUUID() : id;
            this.trackId = trackId;
            this.title = title;
            this.sourceKind = sourceKind;
            this.recordingId = recordingId;
            this.sourceReference = sourceReference;
            this.startSeconds = Math.max(0, startSeconds);
            this.offsetSeconds = Math.max(0, offsetSeconds);
            this.durationSeconds = Math.max(0, durationSeconds);
        }
        public Clip(UUID id, UUID trackId, String title, ClipSourceKind sourceKind, UUID recordingId,
                    String sourceReference, double startSeconds, double offsetSeconds, double durationSeconds,
                    String externalItemId) {
            this(id, trackId, title, sourceKind, recordingId, sourceReference, startSeconds, offsetSeconds,
                    durationSeconds);
            this.externalItemId = externalItemId;
        }
        public UUID id() { return id; }
        public UUID trackId() { return trackId; }
        public String title() { return title; }
        public ClipSourceKind sourceKind() { return sourceKind; }
        public UUID recordingId() { return recordingId; }
        public String sourceReference() { return sourceReference; }
        public double startSeconds() { return startSeconds; }
        public double offsetSeconds() { return offsetSeconds; }
        public double durationSeconds() { return durationSeconds; }
        public String externalItemId() { return externalItemId; }
    }

    @Embeddable
    public static class Region {
        private UUID id;
        private String name;
        private double startSeconds;
        private double endSeconds;
        @Enumerated(EnumType.STRING)
        private RegionOrigin origin;
        @Enumerated(EnumType.STRING)
        private BoundaryConfidence boundaryConfidence;
        private String externalRegionId;
        protected Region() {}
        public Region(UUID id, String name, double startSeconds, double endSeconds, RegionOrigin origin) {
            this(id, name, startSeconds, endSeconds, origin, BoundaryConfidence.DEFINED, null);
        }
        public Region(UUID id, String name, double startSeconds, double endSeconds, RegionOrigin origin,
                      BoundaryConfidence boundaryConfidence, String externalRegionId) {
            this.id = id == null ? UUID.randomUUID() : id;
            this.name = name;
            this.startSeconds = startSeconds;
            this.endSeconds = endSeconds;
            this.origin = origin;
            this.boundaryConfidence = boundaryConfidence == null ? BoundaryConfidence.UNKNOWN : boundaryConfidence;
            this.externalRegionId = externalRegionId;
        }
        public UUID id() { return id; }
        public String name() { return name; }
        public double startSeconds() { return startSeconds; }
        public double endSeconds() { return endSeconds; }
        public RegionOrigin origin() { return origin; }
        public BoundaryConfidence boundaryConfidence() { return boundaryConfidence; }
        public String externalRegionId() { return externalRegionId; }
    }

    @Embeddable
    public static class Marker {
        private UUID id;
        private String name;
        private double positionSeconds;
        @Enumerated(EnumType.STRING)
        private RegionOrigin origin;
        private String externalMarkerId;
        protected Marker() {}
        public Marker(UUID id, String name, double positionSeconds, RegionOrigin origin) {
            this(id, name, positionSeconds, origin, null);
        }
        public Marker(UUID id, String name, double positionSeconds, RegionOrigin origin, String externalMarkerId) {
            this.id = id == null ? UUID.randomUUID() : id;
            this.name = name;
            this.positionSeconds = Math.max(0, positionSeconds);
            this.origin = origin;
            this.externalMarkerId = externalMarkerId;
        }
        public UUID id() { return id; }
        public String name() { return name; }
        public double positionSeconds() { return positionSeconds; }
        public RegionOrigin origin() { return origin; }
        public String externalMarkerId() { return externalMarkerId; }
    }

    @Embeddable
    public static class Take {
        private UUID id;
        private UUID trackId;
        private UUID recordingId;
        private String title;
        private boolean preferred;
        private Instant createdAt;
        private String externalTakeId;
        protected Take() {}
        public Take(UUID id, UUID trackId, UUID recordingId, String title, boolean preferred,
                    Instant createdAt, String externalTakeId) {
            this.id = id == null ? UUID.randomUUID() : id;
            this.trackId = trackId;
            this.recordingId = recordingId;
            this.title = title;
            this.preferred = preferred;
            this.createdAt = createdAt == null ? Instant.now() : createdAt;
            this.externalTakeId = externalTakeId;
        }
        public UUID id() { return id; }
        public UUID trackId() { return trackId; }
        public UUID recordingId() { return recordingId; }
        public String title() { return title; }
        public boolean preferred() { return preferred; }
        public Instant createdAt() { return createdAt; }
        public String externalTakeId() { return externalTakeId; }
    }
}
