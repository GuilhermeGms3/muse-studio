package com.musicos.service;

import com.musicos.domain.InstrumentId;
import com.musicos.domain.PlanActivity;
import com.musicos.domain.Skill;
import com.musicos.repository.PlanActivityRepository;
import com.musicos.repository.SkillRepository;
import com.musicos.repository.SongRepository;
import com.musicos.repository.UserPreferencesRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudyPlanService {
    private final PlanActivityRepository plans;
    private final SkillRepository skills;
    private final SongRepository songs;
    private final ProgressEngine progress;
    private final UserPreferencesRepository preferences;

    public StudyPlanService(PlanActivityRepository plans, SkillRepository skills,
                            SongRepository songs, ProgressEngine progress,
                            UserPreferencesRepository preferences) {
        this.plans = plans;
        this.skills = skills;
        this.songs = songs;
        this.progress = progress;
        this.preferences = preferences;
    }

    @Transactional
    public List<PlanActivity> today(InstrumentId instrument) {
        var existing = plans.findByScheduledForAndInstrumentOrderByPosition(LocalDate.now(), instrument);
        return existing.isEmpty() ? generate(instrument) : existing;
    }

    @Transactional
    public List<PlanActivity> regenerate(InstrumentId instrument) {
        plans.deleteByScheduledForAndInstrument(LocalDate.now(), instrument);
        return generate(instrument);
    }

    @Transactional
    public List<PlanActivity> forSession(InstrumentId instrument, Integer availableMinutes) {
        if (availableMinutes == null) return today(instrument);
        var requested = Math.max(15, Math.min(180, availableMinutes));
        var existing = today(instrument);
        var current = existing.stream().mapToInt(PlanActivity::getMinutes).sum();
        if (Math.abs(current - requested) <= 5) return existing;
        plans.deleteByScheduledForAndInstrument(LocalDate.now(), instrument);
        return generate(instrument, requested);
    }

    private List<PlanActivity> generate(InstrumentId instrument) {
        var budget = preferences.findById("default").map(value -> value.getSessionMinutes()).orElse(60);
        return generate(instrument, budget);
    }

    private List<PlanActivity> generate(InstrumentId instrument, int budget) {
        var standardSlot = Math.max(8, Math.min(18, budget / 4));
        var ranked = skills.findDistinctByInstrumentsContainingOrderByDomainAscTechnicalNameAsc(instrument)
                .stream()
                .filter(skill -> progress.priority(skill) >= 0)
                .sorted(Comparator.comparingInt(progress::priority).reversed())
                .toList();
        var selected = new ArrayList<Skill>();
        var selectedIds = new HashSet<String>();

        pick(ranked, selected, selectedIds, List.of("Técnica", "Violão", "Teclado"));
        pick(ranked, selected, selectedIds, List.of("Escalas", "Harmonia", "Teoria"));
        pick(ranked, selected, selectedIds, List.of("Ouvido", "Leitura", "Ritmo"));

        var result = new ArrayList<PlanActivity>();
        var date = LocalDate.now();
        for (var skill : selected) {
            var evaluation = progress.evaluate(skill);
            var minutes = skill.getDomain().equals("Técnica") ? standardSlot + 2 : standardSlot;
            result.add(new PlanActivity(id(date, instrument, result.size() + 1, skill.getId()), date,
                    result.size() + 1, minutes, skill.getTechnicalName(), kind(skill), instrument,
                    evaluation.nextRequirements().stream().findFirst().orElse("Revisão guiada"), false,
                    skill.getId()));
        }

        songs.findFirstByInstrumentAndStatusOrderByTitleAsc(instrument, "learning")
                .ifPresent(song -> result.add(new PlanActivity(
                        id(date, instrument, result.size() + 1, song.getId()), date, result.size() + 1,
                        Math.min(25, standardSlot + 5), song.getTitle(), "repertoire", instrument,
                        "Aplicar o foco técnico de hoje", false)));

        for (var skill : ranked) {
            if (result.size() >= 4) break;
            if (!selectedIds.add(skill.getId())) continue;
            var evaluation = progress.evaluate(skill);
            result.add(new PlanActivity(id(date, instrument, result.size() + 1, skill.getId()), date,
                    result.size() + 1, standardSlot, skill.getTechnicalName(), kind(skill), instrument,
                    evaluation.nextRequirements().stream().findFirst().orElse("Revisão"), false, skill.getId()));
        }
        var limited = result.stream().limit(budget < 30 ? 2 : budget < 45 ? 3 : 4).toList();
        var allocated = limited.stream().mapToInt(PlanActivity::getMinutes).sum();
        if (allocated > budget) {
            var scale = (double) budget / allocated;
            var resized = new ArrayList<PlanActivity>();
            for (var activity : limited) {
                resized.add(new PlanActivity(activity.getId(), activity.getScheduledFor(), activity.getPosition(),
                        Math.max(5, (int) Math.floor(activity.getMinutes() * scale)), activity.getTitle(),
                        activity.getKind(), activity.getInstrument(), activity.getTarget(), false,
                        activity.getSkillId()));
            }
            limited = resized;
        }
        return plans.saveAll(limited);
    }

    private void pick(List<Skill> ranked, List<Skill> selected, HashSet<String> selectedIds,
                      List<String> domains) {
        ranked.stream()
                .filter(skill -> domains.contains(skill.getDomain()))
                .filter(skill -> selectedIds.add(skill.getId()))
                .findFirst()
                .ifPresent(selected::add);
    }

    private String kind(Skill skill) {
        return switch (skill.getDomain()) {
            case "Técnica", "Violão", "Teclado" -> "technique";
            case "Ouvido" -> "ear";
            case "Composição" -> "creative";
            default -> "theory";
        };
    }

    private String id(LocalDate date, InstrumentId instrument, int position, String target) {
        return date + "-" + instrument.value() + "-" + position + "-" + target;
    }
}
