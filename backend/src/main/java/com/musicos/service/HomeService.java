package com.musicos.service;

import com.musicos.domain.LocalProfile;

import static com.musicos.api.ApiModels.*;
import com.musicos.domain.InstrumentId;
import com.musicos.repository.JournalEntryRepository;
import com.musicos.repository.InstrumentProfileRepository;
import com.musicos.repository.UserPreferencesRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class HomeService {
    private final JournalEntryRepository journal;
    private final InstrumentProfileRepository profiles;
    private final UserPreferencesRepository preferences;
    private final Coach coach;
    private final com.musicos.repository.MissionExperienceRepository experiences;

    public HomeService(JournalEntryRepository journal,
                       InstrumentProfileRepository profiles, UserPreferencesRepository preferences, Coach coach,
                       com.musicos.repository.MissionExperienceRepository experiences) {
        this.journal = journal;
        this.profiles = profiles;
        this.preferences = preferences;
        this.coach = coach;
        this.experiences = experiences;
    }

    public HomeView home(InstrumentId instrument) {
        var availableMinutes = preferences.findById(LocalProfile.DEFAULT_ID)
                .map(value -> value.getSessionMinutes()).orElse(45);
        var profile = profiles.findByOwnerIdAndInstrument(LocalProfile.DEFAULT_ID, instrument)
                .orElseThrow(() -> new NotFoundException(
                        "Perfil instrumental não encontrado para a Home: " + instrument.value()));
        var coachAnswer = coach.whatShouldIDoToday(
                profile.getId(), availableMinutes,
                java.time.Instant.now(), 3);
        var learningExperience = experiences.findByInstrumentProfileIdOrderByUpdatedAtDesc(profile.getId())
                .stream().findFirst().map(MissionExperienceService::view).orElse(null);

        return new HomeView(greeting(), "O Coach acompanha sua continuidade musical.",
                availableMinutes, java.util.List.of(), null,
                null, calculateStreak(), coachView(coachAnswer), learningExperience);
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
