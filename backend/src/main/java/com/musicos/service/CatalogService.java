package com.musicos.service;

import static com.musicos.api.ApiModels.*;
import static com.musicos.service.ViewMapper.*;

import com.musicos.domain.InstrumentId;
import com.musicos.domain.SkillState;
import com.musicos.repository.*;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CatalogService {
    private final InstrumentRepository instruments;
    private final PlanActivityRepository plans;
    private final SkillRepository skills;
    private final LibraryContentRepository library;
    private final SongRepository songs;
    private final ExerciseRepository exercises;
    private final MusicProjectRepository projects;
    private final JournalEntryRepository journal;
    private final ProgressEngine progress;
    private final StudyPlanService studyPlans;

    public CatalogService(InstrumentRepository instruments, PlanActivityRepository plans, SkillRepository skills,
                          LibraryContentRepository library, SongRepository songs, ExerciseRepository exercises,
                          MusicProjectRepository projects, JournalEntryRepository journal,
                          ProgressEngine progress, StudyPlanService studyPlans) {
        this.instruments = instruments;
        this.plans = plans;
        this.skills = skills;
        this.library = library;
        this.songs = songs;
        this.exercises = exercises;
        this.projects = projects;
        this.journal = journal;
        this.progress = progress;
        this.studyPlans = studyPlans;
    }

    public List<InstrumentView> instruments() {
        return instruments.findAll().stream().map(ViewMapper::instrument).toList();
    }

    public List<PlanActivityView> todayPlan(InstrumentId instrument) {
        return studyPlans.today(instrument).stream().map(ViewMapper::activity).toList();
    }

    @Transactional
    public List<PlanActivityView> regeneratePlan(InstrumentId instrument) {
        return studyPlans.regenerate(instrument).stream().map(ViewMapper::activity).toList();
    }

    @Transactional
    public PlanActivityView completeActivity(String id, boolean done) {
        var activity = plans.findById(id).orElseThrow(() -> new NotFoundException("Atividade não encontrada"));
        activity.setDone(done);
        return activity(plans.save(activity));
    }

    public List<SkillView> skills(InstrumentId instrument, SkillState state) {
        var result = state == null
                ? skills.findDistinctByInstrumentsContainingOrderByDomainAscTechnicalNameAsc(instrument)
                : skills.findDistinctByInstrumentsContainingAndStateOrderByDomainAscTechnicalNameAsc(instrument, state);
        return result.stream().map(this::skillView).toList();
    }

    public SkillView skill(String id) {
        return skillView(skills.findById(id)
                .orElseThrow(() -> new NotFoundException("Habilidade não encontrada")));
    }

    @Transactional
    public SkillView changeSkillState(String id, SkillState state) {
        var value = skills.findById(id).orElseThrow(() -> new NotFoundException("Habilidade não encontrada"));
        value.changeState(state);
        return skillView(skills.save(value));
    }

    @Transactional
    public SkillView recordEvidence(String id, SkillEvidenceRequest request) {
        var value = skills.findById(id).orElseThrow(() -> new NotFoundException("Habilidade não encontrada"));
        value.recordEvidence(request.hours(), request.accuracy(), request.bpm(), request.review(),
                request.exerciseCompleted(), request.songCompleted(), request.selfRating(),
                request.perceivedDifficulty());
        var evaluation = progress.evaluate(value);
        value.applyCalculatedState(evaluation.state());
        skills.save(value);
        refreshUnlocks();
        return skillView(value);
    }

    public List<LibraryContentView> library(String category) {
        var result = category == null || category.isBlank()
                ? library.findAllByOrderByCategoryAscFriendlyTitleAsc()
                : library.findByCategoryIgnoreCaseOrderByFriendlyTitleAsc(category);
        return result.stream().map(ViewMapper::library).toList();
    }

    public LibraryContentView libraryContent(String id) {
        return ViewMapper.library(
                library.findById(id).orElseThrow(() -> new NotFoundException("Conteúdo não encontrado")));
    }

    public List<SongView> songs(InstrumentId instrument) {
        var result = instrument == null ? songs.findAll()
                : songs.findByInstrumentOrderByDifficultyAscTitleAsc(instrument);
        return result.stream().map(ViewMapper::song).toList();
    }

    public SongView song(String id) {
        return ViewMapper.song(
                songs.findById(id).orElseThrow(() -> new NotFoundException("Música não encontrada")));
    }

    public List<ExerciseView> exercises(InstrumentId instrument, String technique) {
        var result = technique == null || technique.isBlank()
                ? exercises.findByInstrumentOrderByTechniqueAscNameAsc(instrument)
                : exercises.findByInstrumentAndTechniqueIgnoreCaseOrderByNameAsc(instrument, technique);
        var skillsWithEditorialActivities = result.stream()
                .filter(exercise -> exercise.getId().startsWith("activity-"))
                .map(com.musicos.domain.Exercise::getSkillId)
                .collect(java.util.stream.Collectors.toSet());
        return result.stream()
                .filter(exercise -> !exercise.getId().startsWith("practice-")
                        || !skillsWithEditorialActivities.contains(exercise.getSkillId()))
                .map(ViewMapper::exercise).toList();
    }

    public List<ProjectView> projects() {
        return projects.findAll().stream().map(ViewMapper::project).toList();
    }

    public ProjectView project(String id) {
        return ViewMapper.project(
                projects.findById(id).orElseThrow(() -> new NotFoundException("Projeto não encontrado")));
    }

    public List<JournalView> journal(InstrumentId instrument) {
        var result = instrument == null
                ? journal.findAllByOrderByPracticedAtDesc()
                : journal.findByInstrumentOrderByPracticedAtDesc(instrument);
        return result.stream().map(ViewMapper::journal).toList();
    }

    private SkillView skillView(com.musicos.domain.Skill value) {
        var evaluation = progress.evaluate(value);
        return ViewMapper.skill(value, evaluation.progress(), evaluation.nextRequirements());
    }

    private void refreshUnlocks() {
        for (var value : skills.findAll()) {
            var evaluation = progress.evaluate(value);
            if (value.getState() == SkillState.LOCKED && evaluation.state() == SkillState.AVAILABLE) {
                value.applyCalculatedState(SkillState.AVAILABLE);
                skills.save(value);
            }
        }
    }
}
