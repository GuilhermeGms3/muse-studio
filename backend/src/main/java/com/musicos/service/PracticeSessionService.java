package com.musicos.service;

import static com.musicos.api.ApiModels.*;

import com.musicos.domain.JournalEntry;
import com.musicos.domain.PracticeSession;
import com.musicos.domain.SessionStatus;
import com.musicos.repository.JournalEntryRepository;
import com.musicos.repository.PlanActivityRepository;
import com.musicos.repository.PracticeSessionRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PracticeSessionService {
    private final PracticeSessionRepository sessions;
    private final PlanActivityRepository plans;
    private final JournalEntryRepository journal;
    private final com.musicos.repository.SessionActivityResultRepository activityResults;

    public PracticeSessionService(PracticeSessionRepository sessions, PlanActivityRepository plans,
                                  JournalEntryRepository journal,
                                  com.musicos.repository.SessionActivityResultRepository activityResults) {
        this.sessions = sessions;
        this.plans = plans;
        this.journal = journal;
        this.activityResults = activityResults;
    }

    public SessionView start(StartSessionRequest request) {
        var session = sessions.save(new PracticeSession(request.instrument(), java.util.List.of()));
        return view(session);
    }

    @Transactional(readOnly = true)
    public SessionView get(UUID id) {
        return view(find(id));
    }

    public SessionView update(UUID id, UpdateSessionRequest request) {
        var session = find(id);
        session.update(request.elapsedSeconds(), request.currentActivityIndex(), request.notes(), request.status());
        return view(sessions.save(session));
    }

    public SessionView finish(UUID id, FinishSessionRequest request) {
        var session = find(id);
        if (session.getStatus() == SessionStatus.FINISHED) {
            return view(session);
        }
        session.finish(request.elapsedSeconds(), request.notes());
        var worked = session.getActivityIds().stream()
                .map(plans::findById)
                .flatMap(java.util.Optional::stream)
                .map(value -> value.getTitle())
                .toList();
        journal.save(new JournalEntry(session.getStartedAt(), session.getElapsedSeconds(), session.getInstrument(),
                worked, request.difficulties(), request.improvements(), request.notes()));
        return view(sessions.save(session));
    }

    public ActivityResultView recordActivity(UUID sessionId, String activityId, ActivityResultRequest request) {
        find(sessionId);
        throw new IllegalStateException(
                "Sessões legadas registram apenas duração e anotações; resultados pertencem à MissionExperience.");
    }

    @Transactional(readOnly = true)
    public SessionSummaryView summary(UUID sessionId) {
        var session = find(sessionId);
        var results = activityResults.findBySessionIdOrderByCompletedAtAsc(sessionId);
        var improvements = results.stream()
                .filter(value -> value.getAccuracy() >= 82 || "easy".equals(value.getFeedback()))
                .map(value -> value.getTitle() + ": controle em " + value.getBpm() + " BPM")
                .limit(3).toList();
        var difficulties = results.stream()
                .filter(value -> value.getAccuracy() < 72 || "hard".equals(value.getFeedback()))
                .map(value -> value.getTitle() + ": precisa de mais espaco e repeticoes curtas")
                .limit(3).toList();
        var peakBpm = results.stream().mapToInt(com.musicos.domain.SessionActivityResult::getBpm).max().orElse(0);
        var average = (int) Math.round(results.stream()
                .mapToInt(com.musicos.domain.SessionActivityResult::getAccuracy).average().orElse(0));
        var recommendation = "Consulte o Coach para a próxima recomendação; este registro não altera domínio.";
        return new SessionSummaryView(sessionId, session.getElapsedSeconds(), improvements, difficulties,
                peakBpm, average, recommendation, results.stream().map(this::resultView).toList());
    }

    private PracticeSession find(UUID id) {
        return sessions.findById(id).orElseThrow(() -> new NotFoundException("Sessão não encontrada"));
    }

    private SessionView view(PracticeSession session) {
        var activities = session.getActivityIds().stream()
                .map(plans::findById)
                .flatMap(java.util.Optional::stream)
                .map(ViewMapper::activity)
                .toList();
        return new SessionView(session.getId(), session.getInstrument(), session.getStatus(),
                session.getStartedAt(), session.getFinishedAt(), session.getElapsedSeconds(),
                session.getCurrentActivityIndex(), session.getNotes(), activities);
    }

    private ActivityResultView resultView(com.musicos.domain.SessionActivityResult value) {
        var adaptation = value.getSuggestedBpm() < value.getBpm()
                ? "Reduzimos o BPM para recuperar controle."
                : value.getSuggestedBpm() > value.getBpm()
                ? "Voce esta consistente; o proximo passo sobe 4 BPM."
                : "Mantenha este BPM por mais uma rodada.";
        return new ActivityResultView(value.getId(), value.getActivityId(), value.getTitle(),
                value.getFeedback(), value.getBpm(), value.getAccuracy(), value.getDurationSeconds(),
                value.getTimingOffsetMillis(), value.getSuggestedBpm(), adaptation);
    }

}
