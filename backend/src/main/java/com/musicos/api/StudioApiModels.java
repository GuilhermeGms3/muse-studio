package com.musicos.api;

import com.musicos.domain.InstrumentId;
import com.musicos.domain.StudioProject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class StudioApiModels {
    private StudioApiModels() {}

    public record CreateStudioProjectRequest(
            String title, @NotNull InstrumentId instrument, @NotNull StudioProject.SourceKind sourceKind,
            String sourceId, @Min(30) @Max(300) Integer bpm, String missionId,
            UUID missionExperienceId, String exerciseId, UUID practiceSessionId,
            String songId, String musicProjectId) {}

    public record UpdateStudioProjectRequest(
            @NotBlank String title, @Min(30) @Max(300) int bpm,
            @Min(1) @Max(16) int timeSignatureNumerator,
            @Min(1) @Max(16) int timeSignatureDenominator,
            @Min(0) @Max(8) int countInBars, boolean loopEnabled, UUID selectedRegionId,
            @NotNull StudioProject.EngineMode engineMode,
            @NotNull List<@Valid StudioTrackInput> tracks,
            @NotNull List<@Valid StudioClipInput> clips,
            @NotNull List<@Valid StudioRegionInput> regions,
            @NotNull List<@Valid StudioMarkerInput> markers) {}

    public record StudioTrackInput(UUID id, @NotBlank String name, @NotNull StudioProject.TrackRole role,
                                   boolean muted, boolean solo, @Min(0) @Max(1) double volume,
                                   @Min(-1) @Max(1) double pan, String externalTrackId) {}
    public record StudioClipInput(UUID id, @NotNull UUID trackId, @NotBlank String title,
                                  @NotNull StudioProject.ClipSourceKind sourceKind, UUID recordingId,
                                  String sourceReference, @Min(0) double startSeconds,
                                  @Min(0) double offsetSeconds, @Min(0) double durationSeconds) {}
    public record StudioRegionInput(UUID id, @NotBlank String name, @Min(0) double startSeconds,
                                    @Min(0) double endSeconds, @NotNull StudioProject.RegionOrigin origin) {}
    public record StudioMarkerInput(UUID id, @NotBlank String name, @Min(0) double positionSeconds,
                                    @NotNull StudioProject.RegionOrigin origin) {}
    public record AddStudioTakeRequest(@NotNull UUID trackId, @NotNull UUID recordingId, String title) {}
    public record AddStudioClipRequest(@NotNull UUID trackId, @NotNull UUID recordingId,
                                       String title, @Min(0) double startSeconds,
                                       @Min(0) double durationSeconds) {}
    public record UpdateStudioTakeRequest(String title, boolean preferred) {}

    public record StudioProjectView(
            UUID id, String title, InstrumentId instrument, String sourceKind, String sourceId,
            String missionId, UUID missionExperienceId, String exerciseId, UUID practiceSessionId,
            String songId, String musicProjectId, int bpm, int timeSignatureNumerator,
            int timeSignatureDenominator, int countInBars, boolean loopEnabled, UUID selectedRegionId,
            String engineMode, String externalProjectId, String externalProjectPath,
            Instant createdAt, Instant updatedAt, List<StudioTrackView> tracks,
            List<StudioClipView> clips, List<StudioRegionView> regions,
            List<StudioMarkerView> markers, List<StudioTakeView> takes) {}
    public record StudioTrackView(UUID id, String name, String role, boolean muted, boolean solo,
                                  double volume, double pan, String externalTrackId) {}
    public record StudioClipView(UUID id, UUID trackId, String title, String sourceKind,
                                 UUID recordingId, String sourceReference, String audioUrl,
                                 double startSeconds, double offsetSeconds, double durationSeconds) {}
    public record StudioRegionView(UUID id, String name, double startSeconds, double endSeconds,
                                   String origin) {}
    public record StudioMarkerView(UUID id, String name, double positionSeconds, String origin) {}
    public record StudioTakeView(UUID id, UUID trackId, UUID recordingId, String title,
                                 boolean preferred, Instant createdAt, String externalTakeId,
                                 String audioUrl) {}

    public record ReaperConfigurationRequest(@NotBlank String executablePath,
                                              @NotBlank String workspacePath) {}
    public record ReaperStatusView(String status, boolean configured, String executablePath,
                                   String workspacePath, String message, Instant checkedAt) {}
    public record OpenInReaperView(UUID studioProjectId, String externalProjectId,
                                   String projectPath, String status, String message) {}
}
