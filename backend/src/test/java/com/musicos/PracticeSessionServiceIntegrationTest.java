package com.musicos;

import static com.musicos.api.ApiModels.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.musicos.domain.InstrumentId;
import com.musicos.domain.SessionStatus;
import com.musicos.repository.JournalEntryRepository;
import com.musicos.repository.SkillRepository;
import com.musicos.service.PracticeSessionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PracticeSessionServiceIntegrationTest {

    @Autowired
    private PracticeSessionService sessions;

    @Autowired
    private JournalEntryRepository journal;

    @Autowired
    private SkillRepository skills;

    @Test
    void finishingSessionPersistsJournalEntry() {
        var before = journal.count();
        var previousHours = skills.findById("alternate-picking").orElseThrow().getHours();
        var started = sessions.start(new StartSessionRequest(InstrumentId.GUITAR));

        var finished = sessions.finish(started.id(),
                new FinishSessionRequest(900, "Bends mais estáveis", "Afinação", "Melhor controle"));

        assertThat(started.activities()).hasSize(4);
        assertThat(finished.status()).isEqualTo(SessionStatus.FINISHED);
        assertThat(finished.elapsedSeconds()).isEqualTo(900);
        assertThat(journal.count()).isEqualTo(before + 1);
        assertThat(skills.findById("alternate-picking").orElseThrow().getHours()).isGreaterThan(previousHours);

        sessions.finish(started.id(),
                new FinishSessionRequest(900, "Reenvio", "Afinação", "Melhor controle"));
        assertThat(journal.count()).isEqualTo(before + 1);
    }
}
