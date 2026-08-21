package com.musicos;

import static com.musicos.api.ApiModels.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.musicos.domain.InstrumentId;
import com.musicos.service.DataManagementService;
import com.musicos.service.DiagnosticService;
import com.musicos.service.PracticeSessionService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AdaptiveLearningIntegrationTest {
    @Autowired
    private DiagnosticService diagnostic;
    @Autowired
    private PracticeSessionService sessions;
    @Autowired
    private DataManagementService data;

    @Test
    void diagnosticRecordsContextWithoutInventingSkillsOrPlan() {
        var result = diagnostic.complete(new DiagnosticRequest(InstrumentId.GUITAR, "beginner", 30,
                List.of("Rock"), List.of("Jimi Hendrix"), List.of("Little Wing"),
                72, 40, 58));

        assertThat(result.profile().onboardingCompleted()).isTrue();
        assertThat(result.startingSkills()).isEmpty();

        var session = sessions.start(new StartSessionRequest(InstrumentId.GUITAR, 30));
        assertThat(session.activities()).isEmpty();
    }

    @Test
    void legacySessionRefusesPedagogicalResultsAndKeepsAnHonestSummary() {
        var session = sessions.start(new StartSessionRequest(InstrumentId.GUITAR, 45));
        assertThatThrownBy(() -> sessions.recordActivity(session.id(), "legacy-activity",
                new ActivityResultRequest("hard", 100, 55, 300, 28)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("MissionExperience");
        sessions.finish(session.id(), new FinishSessionRequest(300, "", "", ""));
        var summary = sessions.summary(session.id());
        assertThat(summary.difficulties()).isEmpty();
        assertThat(summary.recommendation()).contains("Coach");
    }

    @Test
    void backupPreservesThePedagogicalDomainWithoutInventingPersonalHistory() {
        var backup = data.backup();
        assertThat(backup.version()).isEqualTo("muse-studio-backup-v2");
        assertThat(backup.skillProgress()).filteredOn(progress -> progress.hours() > 0
                || progress.accuracy() > 0 || progress.currentBpm() != null).isEmpty();
        assertThat(backup.journal()).isEmpty();
        assertThat(backup.pedagogy()).isNotNull();
        assertThat(backup.pedagogy().instrumentProfiles()).isNotEmpty();
        assertThat(backup.pedagogy().learningGoals()).isNotNull();
        assertThat(backup.pedagogy().learningPaths()).isNotEmpty();
        assertThat(data.restore(backup).message()).contains("restaurado");

        var legacyBackup = new BackupSnapshot("music-os-backup-v1", backup.exportedAt(),
                backup.preferences(), backup.lessons(), backup.exercises(), backup.songs(),
                backup.projects(), backup.skillProgress(), backup.journal(), null);
        var legacyResult = data.restore(legacyBackup);
        assertThat(legacyResult.instrumentProfiles()).isZero();
        assertThat(legacyResult.evidence()).isZero();
    }
}
