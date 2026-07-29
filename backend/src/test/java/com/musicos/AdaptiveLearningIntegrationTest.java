package com.musicos;

import static com.musicos.api.ApiModels.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.musicos.domain.InstrumentId;
import com.musicos.service.DataManagementService;
import com.musicos.service.DiagnosticService;
import com.musicos.service.PracticeSessionService;
import com.musicos.service.RepertoirePlanningService;
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
    private RepertoirePlanningService repertoire;
    @Autowired
    private DataManagementService data;

    @Test
    void diagnosticPlacesSkillsAndBuildsShortPlan() {
        var result = diagnostic.complete(new DiagnosticRequest(InstrumentId.GUITAR, "beginner", 30,
                List.of("Rock"), List.of("Jimi Hendrix"), List.of("Little Wing"),
                72, 40, 58));

        assertThat(result.profile().onboardingCompleted()).isTrue();
        assertThat(result.startingSkills()).isNotEmpty();

        var session = sessions.start(new StartSessionRequest(InstrumentId.GUITAR, 30));
        assertThat(session.activities()).hasSizeBetween(2, 3);
        assertThat(session.activities().stream().mapToInt(PlanActivityView::minutes).sum()).isLessThanOrEqualTo(30);
    }

    @Test
    void activityFeedbackAdaptsBpmAndCreatesSummary() {
        var session = sessions.start(new StartSessionRequest(InstrumentId.GUITAR, 45));
        var activity = session.activities().getFirst();
        var result = sessions.recordActivity(session.id(), activity.id(),
                new ActivityResultRequest("hard", 100, 55, 300, 28));

        assertThat(result.suggestedBpm()).isLessThan(100);
        sessions.finish(session.id(), new FinishSessionRequest(300, "", "", ""));
        var summary = sessions.summary(session.id());
        assertThat(summary.difficulties()).isNotEmpty();
        assertThat(summary.recommendation()).contains(activity.title());
    }

    @Test
    void repertoireCreatesPlanAndBackupCarriesProgress() {
        var plan = repertoire.create("sweet-child", 40);
        assertThat(plan).isNotEmpty();
        assertThat(plan.stream().anyMatch(value -> value.skillId() != null)).isTrue();

        var backup = data.backup();
        assertThat(backup.version()).isEqualTo("music-os-backup-v1");
        assertThat(backup.skillProgress()).isNotEmpty();
        assertThat(backup.journal()).isNotEmpty();
        assertThat(data.restore(backup).message()).contains("restaurado");
    }
}
