package com.musicos.service;

import static com.musicos.api.ApiModels.*;
import static com.musicos.service.ViewMapper.*;

import com.musicos.domain.InstrumentId;
import com.musicos.repository.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CatalogService {
    private final InstrumentRepository instruments;
    private final LibraryContentRepository library;
    private final SongRepository songs;
    private final ExerciseRepository exercises;
    private final MusicProjectRepository projects;
    private final JournalEntryRepository journal;

    public CatalogService(InstrumentRepository instruments, LibraryContentRepository library,
                          SongRepository songs, ExerciseRepository exercises,
                          MusicProjectRepository projects, JournalEntryRepository journal) {
        this.instruments = instruments;
        this.library = library;
        this.songs = songs;
        this.exercises = exercises;
        this.projects = projects;
        this.journal = journal;
    }

    public List<InstrumentView> instruments() {
        return instruments.findAll().stream().map(ViewMapper::instrument).toList();
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
        return result.stream()
                .filter(exercise -> !exercise.getId().startsWith("activity-")
                        && !exercise.getId().startsWith("practice-"))
                .map(ViewMapper::exercise).toList();
    }

    public ExerciseView exercise(String id) {
        return ViewMapper.exercise(exercises.findById(id)
                .orElseThrow(() -> new NotFoundException("Exercício não encontrado")));
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

}
