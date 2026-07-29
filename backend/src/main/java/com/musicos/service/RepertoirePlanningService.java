package com.musicos.service;

import static com.musicos.api.ApiModels.*;

import com.musicos.domain.PlanActivity;
import com.musicos.repository.PlanActivityRepository;
import com.musicos.repository.SongRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RepertoirePlanningService {
    private final SongRepository songs;
    private final PlanActivityRepository plans;

    public RepertoirePlanningService(SongRepository songs, PlanActivityRepository plans) {
        this.songs = songs;
        this.plans = plans;
    }

    @Transactional
    public List<PlanActivityView> create(String songId, int minutes) {
        var song = songs.findById(songId).orElseThrow(() -> new NotFoundException("Musica nao encontrada"));
        var selected = song.getSections().stream()
                .sorted(java.util.Comparator.comparingInt(com.musicos.domain.SongSection::getProgress))
                .limit(4).toList();
        if (selected.isEmpty()) throw new IllegalArgumentException("Cadastre ao menos uma secao na musica");
        var budget = Math.max(15, Math.min(120, minutes));
        plans.deleteByScheduledForAndInstrument(LocalDate.now(), song.getInstrument());
        var result = new ArrayList<PlanActivity>();
        for (var index = 0; index < selected.size(); index++) {
            var section = selected.get(index);
            var duration = Math.max(5, budget / selected.size());
            var skillId = section.getSkillIds().stream().findFirst().orElse(null);
            result.add(new PlanActivity(LocalDate.now() + "-" + songId + "-" + section.getSectionId(),
                    LocalDate.now(), index + 1, duration, song.getTitle() + " · " + section.getName(),
                    "repertoire", song.getInstrument(),
                    (section.getBpm() == null ? song.getBpm() : section.getBpm()) + " BPM · "
                            + (section.getNote() == null ? "trecho completo" : section.getNote()),
                    false, skillId));
        }
        return plans.saveAll(result).stream().map(ViewMapper::activity).toList();
    }
}
