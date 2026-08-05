package com.musicos.service;

import static com.musicos.api.ApiModels.*;
import static com.musicos.service.ViewMapper.activity;

import com.musicos.domain.InstrumentId;
import com.musicos.repository.JournalEntryRepository;
import com.musicos.repository.InstrumentProfileRepository;
import com.musicos.repository.SkillRepository;
import com.musicos.repository.SongRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class HomeService {
    private final StudyPlanService plans;
    private final SongRepository songs;
    private final SkillRepository skills;
    private final JournalEntryRepository journal;
    private final ProgressEngine progress;
    private final InstrumentProfileRepository profiles;
    private final Coach coach;

    public HomeService(StudyPlanService plans, SongRepository songs, SkillRepository skills,
                       JournalEntryRepository journal, ProgressEngine progress,
                       InstrumentProfileRepository profiles, Coach coach) {
        this.plans = plans;
        this.songs = songs;
        this.skills = skills;
        this.journal = journal;
        this.progress = progress;
        this.profiles = profiles;
        this.coach = coach;
    }

    public HomeView home(InstrumentId instrument) {
        var today = plans.today(instrument)
                .stream().limit(4).map(ViewMapper::activity).toList();
        var expectedMinutes = today.stream().mapToInt(PlanActivityView::minutes).sum();
        var currentSong = songs.findFirstByInstrumentAndStatusOrderByTitleAsc(instrument, "learning");
        var continuation = currentSong
                .<ContinueView>map(song -> new ContinueView("song", song.getId(), song.getTitle(), song.getArtist()))
                .orElseGet(() -> new ContinueView("library", "campo-harmonico",
                        "Como os acordes funcionam juntos", "Campo Harmônico"));
        var objectiveSkill = skills.findById("bends")
                .filter(skill -> skill.getInstruments().contains(instrument))
                .orElseGet(() -> skills.findDistinctByInstrumentsContainingOrderByDomainAscTechnicalNameAsc(instrument)
                        .stream().findFirst().orElseThrow(() -> new NotFoundException("Nenhum objetivo disponível")));
        var objectiveProgress = progress.evaluate(objectiveSkill);
        var objective = new ObjectiveView(objectiveSkill.getId(), objectiveSkill.getFriendlyTitle(),
                objectiveSkill.getTechnicalName(), objectiveProgress.progress(), objectiveProgress.state());

        var profile = profiles.findByOwnerIdAndInstrument("default", instrument)
                .orElseThrow(() -> new NotFoundException(
                        "Perfil instrumental nÃ£o encontrado para a Home: " + instrument.value()));
        var coachAnswer = coach.whatShouldIDoToday(
                profile.getId(), expectedMinutes > 0 ? expectedMinutes : null,
                java.time.Instant.now(), 3);

        return new HomeView(greeting(), "Hoje vamos praticar.", expectedMinutes, today, continuation,
                objective, calculateStreak(), coachView(coachAnswer));
    }

    private CoachHomeView coachView(Coach.TodayAnswer answer) {
        var profile = new CoachProfileView(
                answer.profile().instrumentProfileId(), answer.profile().instrument(),
                answer.profile().stage(), answer.profile().curriculumId());
        return new CoachHomeView(answer.status().name(), profile, answer.evaluatedAt(),
                answer.availableMinutes(), answer.activeGoals().stream().map(this::goalView).toList(),
                answer.recommendations().stream().map(this::recommendationView).toList(),
                answer.message());
    }

    private CoachGoalView goalView(Coach.GoalCitation goal) {
        return new CoachGoalView(goal.goalId(), goal.title(), goal.desiredOutcome(),
                goal.musicalContext(), goal.type().name(), goal.declaredPriority(), goal.targetDate());
    }

    private CoachEvidenceView evidenceView(Coach.EvidenceCitation evidence) {
        return new CoachEvidenceView(evidence.evidenceId(), evidence.competencyId(),
                evidence.criterionKey(), evidence.reliability().name(), evidence.result().name(),
                evidence.occurredAt(), evidence.observation(), evidence.conditions());
    }

    private CoachRecommendationView recommendationView(Coach.Recommendation recommendation) {
        return new CoachRecommendationView(recommendation.missionId(), recommendation.title(),
                recommendation.competencyId(), recommendation.kind().name(),
                recommendation.estimatedMinutes(), recommendation.observableObjective(),
                recommendation.expectedEvidence(),
                recommendation.goals().stream().map(this::goalView).toList(),
                recommendation.evidence().stream().map(this::evidenceView).toList(),
                recommendation.explanation());
    }

    private String greeting() {
        var hour = java.time.LocalTime.now().getHour();
        if (hour < 12) return "Bom dia.";
        if (hour < 18) return "Boa tarde.";
        return "Boa noite.";
    }

    private int calculateStreak() {
        var zone = ZoneId.systemDefault();
        var date = LocalDate.now();
        var streak = 0;
        while (streak < 365) {
            var start = date.atStartOfDay(zone).toInstant();
            var end = date.plusDays(1).atStartOfDay(zone).toInstant();
            if (!journal.existsByPracticedAtBetween(start, end)) {
                if (date.equals(LocalDate.now())) {
                    date = date.minusDays(1);
                    continue;
                }
                break;
            }
            streak++;
            date = date.minusDays(1);
        }
        return streak;
    }
}
