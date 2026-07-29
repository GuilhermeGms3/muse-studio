package com.musicos.service;

import static com.musicos.api.ApiModels.*;

import com.musicos.domain.JournalEntry;
import com.musicos.domain.PracticeSession;
import com.musicos.domain.SessionStatus;
import com.musicos.repository.JournalEntryRepository;
import com.musicos.repository.PlanActivityRepository;
import com.musicos.repository.PracticeSessionRepository;
import com.musicos.repository.SkillRepository;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PracticeSessionService {
    private final PracticeSessionRepository sessions;
    private final PlanActivityRepository plans;
    private final StudyPlanService studyPlans;
    private final SkillRepository skills;
    private final ProgressEngine progress;
    private final JournalEntryRepository journal;
    private final com.musicos.repository.SessionActivityResultRepository activityResults;

    public PracticeSessionService(PracticeSessionRepository sessions, PlanActivityRepository plans,
                                  JournalEntryRepository journal, StudyPlanService studyPlans,
                                  SkillRepository skills, ProgressEngine progress,
                                  com.musicos.repository.SessionActivityResultRepository activityResults) {
        this.sessions = sessions;
        this.plans = plans;
        this.journal = journal;
        this.studyPlans = studyPlans;
        this.skills = skills;
        this.progress = progress;
        this.activityResults = activityResults;
    }

    public SessionView start(StartSessionRequest request) {
        var activities = studyPlans.forSession(request.instrument(), request.availableMinutes())
                .stream().limit(4).toList();
        var session = sessions.save(new PracticeSession(request.instrument(),
                activities.stream().map(value -> value.getId()).toList()));
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
        if (skills != null && progress != null && activityResults.findBySessionIdOrderByCompletedAtAsc(id).isEmpty()) {
            recordPracticeEvidence(session);
        }
        return view(sessions.save(session));
    }

    public ActivityResultView recordActivity(UUID sessionId, String activityId, ActivityResultRequest request) {
        var session = find(sessionId);
        var activity = plans.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Atividade nao encontrada"));
        if (!session.getActivityIds().contains(activityId)) {
            throw new IllegalArgumentException("A atividade nao pertence a esta sessao");
        }
        var existing = activityResults.findBySessionIdAndActivityId(sessionId, activityId);
        if (existing.isPresent()) {
            return resultView(existing.get());
        }
        var currentBpm = request.bpm() > 0 ? request.bpm() : bpmFromTarget(activity.getTarget());
        var hard = "hard".equalsIgnoreCase(request.feedback()) || request.accuracy() < 70;
        var easy = "easy".equalsIgnoreCase(request.feedback()) && request.accuracy() >= 88;
        var suggested = hard ? Math.max(35, currentBpm - Math.max(4, (int) Math.round(currentBpm * .08)))
                : easy ? currentBpm + 4 : currentBpm;
        var saved = activityResults.save(new com.musicos.domain.SessionActivityResult(
                sessionId, activityId, activity.getSkillId(), activity.getTitle(), request.feedback(),
                currentBpm, request.accuracy(), request.durationSeconds(), request.timingOffsetMillis(), suggested));
        if (activity.getSkillId() != null) {
            skills.findById(activity.getSkillId()).ifPresent(skill -> {
                skill.recordEvidence(request.durationSeconds() / 3600.0, request.accuracy(), currentBpm,
                        "review".equals(activity.getKind()), request.accuracy() >= 80,
                        "repertoire".equals(activity.getKind()), null,
                        hard ? 5 : easy ? 2 : 3);
                skill.applyCalculatedState(progress.evaluate(skill).state());
                skills.save(skill);
            });
        }
        return resultView(saved);
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
        var weakest = results.stream().min(java.util.Comparator
                .comparingInt(com.musicos.domain.SessionActivityResult::getAccuracy));
        var recommendation = weakest
                .map(value -> "Na proxima sessao, comece por " + value.getTitle() + " em "
                        + value.getSuggestedBpm() + " BPM.")
                .orElse("Na proxima sessao, retome o primeiro bloco antes de aumentar a dificuldade.");
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

    private void recordPracticeEvidence(PracticeSession session) {
        var activities = session.getActivityIds().stream()
                .map(plans::findById)
                .flatMap(java.util.Optional::stream)
                .filter(activity -> activity.getSkillId() != null)
                .toList();
        var plannedSeconds = activities.stream().mapToLong(activity -> activity.getMinutes() * 60L).sum();
        for (var activity : activities) {
            skills.findById(activity.getSkillId()).ifPresent(skill -> {
                var share = plannedSeconds == 0 ? 0
                        : (double) activity.getMinutes() * 60 / plannedSeconds;
                skill.recordEvidence(session.getElapsedSeconds() * share / 3600.0,
                        null, null, false, true, false, null);
                skill.applyCalculatedState(progress.evaluate(skill).state());
                skills.save(skill);
            });
        }
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

    private int bpmFromTarget(String target) {
        if (target == null) return 60;
        var matcher = java.util.regex.Pattern.compile("(\\d{2,3})\\s*BPM",
                java.util.regex.Pattern.CASE_INSENSITIVE).matcher(target);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 60;
    }
}
