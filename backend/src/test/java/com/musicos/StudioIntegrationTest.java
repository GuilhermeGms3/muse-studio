package com.musicos;

import static com.musicos.api.StudioApiModels.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.musicos.domain.InstrumentId;
import com.musicos.domain.PracticeRecording;
import com.musicos.domain.StudioProject;
import com.musicos.integration.reaper.ReaperBridge;
import com.musicos.integration.reaper.ReaperProjectRenderer;
import com.musicos.repository.MissionRepository;
import com.musicos.repository.PracticeRecordingRepository;
import com.musicos.repository.SongRepository;
import com.musicos.repository.StudioProjectRepository;
import com.musicos.service.StudioService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class StudioIntegrationTest {
    @Autowired StudioService studio;
    @Autowired StudioProjectRepository projects;
    @Autowired MissionRepository missions;
    @Autowired SongRepository songs;
    @Autowired PracticeRecordingRepository recordings;
    @Autowired ReaperProjectRenderer renderer;
    @Autowired ReaperBridge reaper;

    @Test
    void createsUpdatesAndResumesAFreeStudioWithExplicitTakes() {
        var created = studio.create(new CreateStudioProjectRequest("Ensaio", InstrumentId.GUITAR,
                StudioProject.SourceKind.FREE, null, 92, null, null, null, null, null, null));
        assertThat(created.tracks()).extracting(StudioTrackView::role).contains("RECORDING");

        var region = new StudioRegionInput(null, "A", 0, 16, StudioProject.RegionOrigin.USER);
        var updated = studio.update(created.id(), new UpdateStudioProjectRequest(
                "Ensaio A", 96, 4, 4, 1, true, null, StudioProject.EngineMode.WEB,
                created.tracks().stream().map(track -> new StudioTrackInput(track.id(), track.name(),
                        StudioProject.TrackRole.valueOf(track.role()), track.muted(), track.solo(),
                        track.volume(), track.pan(), track.externalTrackId())).toList(),
                List.of(), List.of(region), List.of()));
        assertThat(updated.bpm()).isEqualTo(96);
        assertThat(projects.findById(created.id())).get().extracting(StudioProject::getTitle)
                .isEqualTo("Ensaio A");

        var recording = recordings.saveAndFlush(new PracticeRecording("studio-project", created.id().toString(),
                "take.webm", "take.webm", "audio/webm", 1000, 96, null, null, null, null, null, null));
        var withTake = studio.addTake(created.id(), new AddStudioTakeRequest(
                created.tracks().getFirst().id(), recording.getId(), null));
        assertThat(withTake.takes()).hasSize(1).first().extracting(StudioTakeView::title).isEqualTo("Take 1");
        assertThat(studio.get(created.id()).takes()).hasSize(1);
    }

    @Test
    void buildsMissionAndRepertoireStudiosFromExistingContext() {
        var mission = missions.findAll().stream().filter(item -> item.getInstrument() == InstrumentId.GUITAR)
                .findFirst().orElseThrow();
        var missionStudio = studio.create(new CreateStudioProjectRequest(null, InstrumentId.GUITAR,
                StudioProject.SourceKind.MISSION, mission.getId(), null, mission.getId(), null,
                null, null, null, null));
        assertThat(missionStudio.missionId()).isEqualTo(mission.getId());
        assertThat(missionStudio.exerciseId()).isNotBlank();

        var song = songs.findAll().stream().filter(item -> item.getInstrument() == InstrumentId.GUITAR)
                .findFirst().orElseThrow();
        var repertoireStudio = studio.create(new CreateStudioProjectRequest(null, InstrumentId.GUITAR,
                StudioProject.SourceKind.REPERTOIRE, song.getId(), null, null, null,
                null, null, song.getId(), null));
        assertThat(repertoireStudio.songId()).isEqualTo(song.getId());
        assertThat(repertoireStudio.bpm()).isEqualTo(song.getBpm());
        assertThat(repertoireStudio.tracks()).extracting(StudioTrackView::role)
                .contains("REFERENCE", "RECORDING");
    }

    @Test
    void rendersAnExplicitReaperProjectAndStaysOptionalWhenUnconfigured() {
        var created = studio.create(new CreateStudioProjectRequest("Forma A B", InstrumentId.KEYS,
                StudioProject.SourceKind.FREE, null, 82, null, null, null, null, null, null));
        var entity = projects.findById(created.id()).orElseThrow();
        var tracks = entity.getTracks();
        entity.replaceStructure(tracks, List.of(),
                List.of(new StudioProject.Region(null, "A", 0, 8, StudioProject.RegionOrigin.USER)),
                List.of(new StudioProject.Marker(null, "Entrada", 0, StudioProject.RegionOrigin.USER)));
        var rpp = renderer.render(entity);
        assertThat(rpp).contains("TEMPO 82 4 4", "NAME \"Minhas teclas\"", "MARKER");
        assertThat(reaper.status().configured()).isFalse();
        assertThat(reaper.status().status()).isEqualTo("NOT_CONFIGURED");
    }
}
