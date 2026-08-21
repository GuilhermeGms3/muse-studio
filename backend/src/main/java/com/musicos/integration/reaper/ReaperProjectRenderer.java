package com.musicos.integration.reaper;

import com.musicos.domain.StudioProject;
import com.musicos.service.RecordingService;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class ReaperProjectRenderer {
    private final RecordingService recordings;

    public ReaperProjectRenderer(RecordingService recordings) { this.recordings = recordings; }

    public String render(StudioProject project) {
        return render(project, path -> path.toString().replace('\\', '/'));
    }

    public String render(StudioProject project, Function<Path, String> pathMapper) {
        var result = new StringBuilder();
        result.append("<REAPER_PROJECT 0.1 \"7.0/win64\" 0\n")
                .append("  RIPPLE 0\n  GROUPOVERRIDE 0 0 0\n  AUTOXFADE 1\n")
                .append("  TEMPO ").append(project.getBpm()).append(' ')
                .append(project.getTimeSignatureNumerator()).append(' ')
                .append(project.getTimeSignatureDenominator()).append("\n")
                .append("  CURPOS 0\n");
        var selected = project.getRegions().stream()
                .filter(item -> item.id().equals(project.getSelectedRegionId())).findFirst().orElse(null);
        if (selected != null) {
            result.append("  TIMESEL ").append(decimal(selected.startSeconds())).append(' ')
                    .append(decimal(selected.endSeconds())).append("\n  LOOP 1\n");
        }
        int markerIndex = 1;
        for (var marker : project.getMarkers()) {
            result.append("  MARKER ").append(markerIndex++).append(' ')
                    .append(decimal(marker.positionSeconds())).append(" \"")
                    .append(escape(marker.name())).append("\" 0 0 1 B {1192-")
                    .append(marker.externalMarkerId()).append("}\n");
        }
        for (var region : project.getRegions()) {
            if (region.boundaryConfidence() == StudioProject.BoundaryConfidence.UNKNOWN) continue;
            var label = region.boundaryConfidence() == StudioProject.BoundaryConfidence.ESTIMATED
                    ? region.name() + " [limites estimados]" : region.name();
            result.append("  MARKER ").append(markerIndex++).append(' ')
                    .append(decimal(region.startSeconds())).append(" \"")
                    .append(escape(label)).append("\" 0 0 1 R {")
                    .append(region.externalRegionId()).append("}\n")
                    .append("  MARKER ").append(markerIndex++).append(' ')
                    .append(decimal(region.endSeconds())).append(" \"")
                    .append(escape(label)).append("\" 0 0 1 R\n");
        }
        for (var track : project.getTracks()) {
            var guid = track.externalTrackId() == null ? track.id().toString() : track.externalTrackId();
            result.append("  <TRACK {").append(guid).append("}\n")
                    .append("    NAME \"").append(escape(track.name())).append("\"\n")
                    .append("    VOLPAN ").append(decimal(track.volume())).append(' ')
                    .append(decimal(track.pan())).append(" -1 -1 1\n")
                    .append("    MUTESOLO ").append(track.muted() ? 1 : 0).append(' ')
                    .append(track.solo() ? 1 : 0).append(" 0\n");
            if (track.role() == StudioProject.TrackRole.RECORDING) result.append("    REC 1 0 1 0 0 0 0 0\n");
            project.getClips().stream().filter(clip -> clip.trackId().equals(track.id()))
                    .filter(clip -> clip.recordingId() != null).forEach(clip -> appendItem(result, clip, pathMapper));
            project.getTakes().stream().filter(take -> take.trackId().equals(track.id()))
                    .forEach(take -> appendTake(result, take, pathMapper));
            result.append("  >\n");
        }
        return result.append(">\n").toString();
    }

    private void appendItem(StringBuilder result, StudioProject.Clip clip, Function<Path, String> pathMapper) {
        Path path = recordings.managedPath(clip.recordingId());
        result.append("    <ITEM {1C11-").append(clip.externalItemId()).append("}\n      POSITION ")
                .append(decimal(clip.startSeconds()))
                .append("\n      LENGTH ").append(decimal(clip.durationSeconds()))
                .append("\n      SOFFS ").append(decimal(clip.offsetSeconds()))
                .append("\n      NAME \"").append(escape(clip.title())).append("\"\n")
                .append("      <SOURCE WAVE\n        FILE \"")
                .append(escape(pathMapper.apply(path))).append("\"\n      >\n    >\n");
    }

    private void appendTake(StringBuilder result, StudioProject.Take take, Function<Path, String> pathMapper) {
        Path path = recordings.managedPath(take.recordingId());
        result.append("    <ITEM {7A4E-").append(take.externalTakeId())
                .append("}\n      POSITION 0.000000\n      NAME \"")
                .append(escape(take.title())).append("\"\n      <TAKE {9A6B-")
                .append(take.externalTakeId()).append("}\n        NAME \"")
                .append(escape(take.title())).append("\"\n        <SOURCE WAVE\n          FILE \"")
                .append(escape(pathMapper.apply(path)))
                .append("\"\n        >\n      >\n    >\n");
    }

    private String decimal(double value) { return String.format(Locale.ROOT, "%.6f", value); }
    private String escape(String value) { return value == null ? "" : value.replace("\\", "/").replace("\"", "'"); }
}
