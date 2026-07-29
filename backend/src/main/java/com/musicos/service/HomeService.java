package com.musicos.service;

import static com.musicos.api.ApiModels.*;
import static com.musicos.service.ViewMapper.activity;

import com.musicos.domain.InstrumentId;
import com.musicos.repository.JournalEntryRepository;
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

    public HomeService(StudyPlanService plans, SongRepository songs, SkillRepository skills,
                       JournalEntryRepository journal, ProgressEngine progress) {
        this.plans = plans;
        this.songs = songs;
        this.skills = skills;
        this.journal = journal;
        this.progress = progress;
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
                .orElseGet(() -> skills.findDistinctByInstrumentsContainingOrderByDomainAscTechnicalNameAsc(instrument)
                        .stream().findFirst().orElseThrow(() -> new NotFoundException("Nenhum objetivo disponível")));
        var objectiveProgress = progress.evaluate(objectiveSkill);
        var objective = new ObjectiveView(objectiveSkill.getId(), objectiveSkill.getFriendlyTitle(),
                objectiveSkill.getTechnicalName(), objectiveProgress.progress(), objectiveProgress.state());

        return new HomeView(greeting(), "Hoje vamos praticar.", expectedMinutes, today, continuation,
                objective, calculateStreak());
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
