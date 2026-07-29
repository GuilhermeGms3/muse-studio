package com.musicos.config;

import com.musicos.domain.*;
import com.musicos.repository.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements ApplicationRunner {
    private final InstrumentRepository instruments;
    private final PlanActivityRepository plans;
    private final SkillRepository skills;
    private final LibraryContentRepository library;
    private final SongRepository songs;
    private final ExerciseRepository exercises;
    private final MusicProjectRepository projects;
    private final JournalEntryRepository journal;
    private final UserPreferencesRepository preferences;

    public DataInitializer(InstrumentRepository instruments, PlanActivityRepository plans, SkillRepository skills,
                           LibraryContentRepository library, SongRepository songs, ExerciseRepository exercises,
                           MusicProjectRepository projects, JournalEntryRepository journal,
                           UserPreferencesRepository preferences) {
        this.instruments = instruments;
        this.plans = plans;
        this.skills = skills;
        this.library = library;
        this.songs = songs;
        this.exercises = exercises;
        this.projects = projects;
        this.journal = journal;
        this.preferences = preferences;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedInstruments();
        seedPlan();
        seedSkills();
        seedLibrary();
        seedSongs();
        seedExercises();
        enrichLearningContent();
        seedProjects();
        seedJournal();
        seedPreferences();
    }

    private void seedInstruments() {
        if (instruments.count() > 0) return;
        instruments.saveAll(List.of(
                new Instrument(InstrumentId.GUITAR, "Guitarra", "GTR",
                        List.of("Técnica", "Repertório", "Improvisação")),
                new Instrument(InstrumentId.ACOUSTIC, "Violão", "VLA",
                        List.of("Acordes", "Levadas", "Fingerstyle")),
                new Instrument(InstrumentId.KEYS, "Teclado", "KEY",
                        List.of("Leitura", "Independência", "Harmonia"))
        ));
    }

    private void seedPlan() {
        var today = LocalDate.now();
        var existing = plans.findByScheduledForAndInstrumentOrderByPosition(today, InstrumentId.GUITAR);
        if (!existing.isEmpty()) {
            existing.forEach(activity -> {
                String skillId = null;
                if ("Alternate Picking".equals(activity.getTitle())) skillId = "alternate-picking";
                else if ("theory".equals(activity.getKind())) skillId = "harmonic-field";
                else if ("warmup".equals(activity.getKind())) skillId = "bends";
                if (skillId != null) activity.attachSkill(skillId);
            });
            plans.saveAll(existing);
            return;
        }
        var prefix = today + "-";
        plans.saveAll(List.of(
                new PlanActivity(prefix + "gtr-1", today, 1, 15, "Alternate Picking", "technique",
                        InstrumentId.GUITAR, "122 BPM limpo", false, "alternate-picking"),
                new PlanActivity(prefix + "gtr-2", today, 2, 20, "Sweet Child O' Mine", "repertoire",
                        InstrumentId.GUITAR, "Solo B a 92 BPM", false),
                new PlanActivity(prefix + "gtr-3", today, 3, 15, "Campo Harmônico", "theory",
                        InstrumentId.GUITAR, "Graus em C, G e D", false, "harmonic-field"),
                new PlanActivity(prefix + "gtr-4", today, 4, 10, "Revisão", "warmup",
                        InstrumentId.GUITAR, "Bends e vibrato", false, "bends"),
                new PlanActivity(prefix + "vla-1", today, 1, 15, "Fingerstyle", "technique",
                        InstrumentId.ACOUSTIC, "Polegar constante", false, "fingerstyle"),
                new PlanActivity(prefix + "vla-2", today, 2, 20, "Blackbird", "repertoire",
                        InstrumentId.ACOUSTIC, "Bridge", false),
                new PlanActivity(prefix + "key-1", today, 1, 15, "Leitura", "technique",
                        InstrumentId.KEYS, "Sem interromper", false, "note-reading"),
                new PlanActivity(prefix + "key-2", today, 2, 20, "Gymnopédie No.1", "repertoire",
                        InstrumentId.KEYS, "Dinâmica e pedal", false)
        ));
    }

    private void seedSkills() {
        var core = List.of(
                new Skill("rhythm", "Sentir e sustentar o pulso", "Ritmo", "Fundamentos",
                        "Organizar notas no tempo com estabilidade.", SkillState.MASTERED, 18, 91, null, null,
                        List.of(InstrumentId.GUITAR, InstrumentId.ACOUSTIC, InstrumentId.KEYS),
                        List.of(), List.of("ritmo"), List.of(), List.of(), List.of("alternate-picking")),
                new Skill("alternate-picking", "Tocar com palhetadas alternadas", "Alternate Picking", "Técnica",
                        "Alternar ataques para ganhar precisão e velocidade sem tensão.", SkillState.PRACTICING,
                        12.5, 76, 118, 140, List.of(InstrumentId.GUITAR),
                        List.of("rhythm"), List.of(), List.of("ex1", "ex2"),
                        List.of("sweet-child"), List.of("bends", "string-skipping")),
                new Skill("bends", "Afinar bends com confiança", "Bends", "Expressão",
                        "Controlar altura, chegada e sustentação do bend usando uma nota de referência.",
                        SkillState.PRACTICING, 7.5, 72, 72, 90, List.of(InstrumentId.GUITAR),
                        List.of("alternate-picking"), List.of("bends"), List.of("ex5"),
                        List.of("sweet-child"), List.of("vibrato")),
                new Skill("vibrato", "Dar vida às notas longas", "Vibrato", "Expressão",
                        "Controlar amplitude e pulsação do vibrato.", SkillState.AVAILABLE, 3, 55, 78, 100,
                        List.of(InstrumentId.GUITAR), List.of("bends"), List.of(), List.of("ex6"),
                        List.of("sweet-child"), List.of()),
                new Skill("harmonic-field", "Entender como os acordes funcionam juntos", "Campo Harmônico",
                        "Harmonia", "Formar acordes em cada grau e reconhecer suas funções.",
                        SkillState.LEARNING, 6, 64, null, null,
                        List.of(InstrumentId.GUITAR, InstrumentId.ACOUSTIC, InstrumentId.KEYS),
                        List.of("rhythm"), List.of("campo-harmonico"), List.of(), List.of(),
                        List.of("modes")),
                new Skill("modes", "Improvisar usando as cores da escala maior", "Modos Gregos",
                        "Improvisação", "Ouvir e aplicar as notas características de cada modo.",
                        SkillState.LOCKED, 1, 20, null, null,
                        List.of(InstrumentId.GUITAR, InstrumentId.KEYS), List.of("harmonic-field"),
                        List.of("modos-gregos"), List.of(), List.of(), List.of())
        );
        core.forEach(skill -> {
            if (!skills.existsById(skill.getId())) skills.save(skill);
        });
        CurriculumCatalog.all().forEach(skill -> {
            if (!skills.existsById(skill.getId())) skills.save(skill);
        });
    }

    private void seedLibrary() {
        if (library.count() > 0) return;
        library.saveAll(List.of(
                new LibraryContent("campo-harmonico", "Como os acordes funcionam juntos", "Campo Harmônico",
                        "Harmonia", "Os acordes que nascem de uma escala e como eles se relacionam.",
                        List.of("Empilhando terças sobre cada grau da escala maior obtemos os acordes do campo.",
                                "Os graus I, IV e V organizam repouso, movimento e tensão."),
                        List.of("Campo de C: C Dm Em F G Am B°", "I-V-vi-IV: C G Am F"),
                        List.of("escala-maior", "modos-gregos")),
                new LibraryContent("escala-maior", "A régua do sistema tonal", "Escala Maior",
                        "Escalas", "A referência para entender graus, acordes e modos.",
                        List.of("A fórmula é tom, tom, semitom, tom, tom, tom, semitom.",
                                "Cante cada grau em relação à tônica antes de decorar shapes."),
                        List.of("C D E F G A B C"), List.of("campo-harmonico", "modos-gregos")),
                new LibraryContent("modos-gregos", "Como improvisar usando a escala maior", "Modos Gregos",
                        "Improvisação", "Sete sonoridades derivadas da escala maior.",
                        List.of("Modo é uma sonoridade em torno de um centro, não apenas uma posição.",
                                "Comece por dórico, lídio e mixolídio e compare com a escala maior."),
                        List.of("D dórico: D E F G A B C"), List.of("escala-maior", "campo-harmonico")),
                new LibraryContent("bends", "Como fazer a guitarra cantar afinada", "Bends",
                        "Técnicas", "Controle de altura e expressão ao esticar a corda.",
                        List.of("Toque primeiro a nota de destino e memorize sua altura.",
                                "Suba o bend devagar, sustente e confira a afinação."),
                        List.of("Bend de um tom no 8º traste da segunda corda"), List.of("vibrato"))
        ));
    }

    private void seedSongs() {
        var seeded = List.of(
                new Song("sweet-child", "Sweet Child O' Mine", "Guns N' Roses", "Eb Standard",
                        "Db maior", 126, InstrumentId.GUITAR, 4, "learning",
                        "Solo B ainda inconsistente; conferir a afinação dos bends.", 55,
                        List.of("Alternate Picking", "Bends", "Vibrato"), List.of("Pentatônica menor"),
                        List.of(
                                new SongSection("intro", "Intro", 92, 126, "Manter o riff relaxado",
                                        List.of("alternate-picking", "string-skipping"),
                                        "e|----------------15----14----|\nB|--15----13-------------------|", 0, 52),
                                new SongSection("verso", "Verso", 80, 126, null),
                                new SongSection("solo-a", "Solo A", 55, 104, null,
                                        List.of("minor-pentatonic", "vibrato"), null, 185, 224),
                                new SongSection("solo-b", "Solo B", 31, 92, "Dividir em quatro células",
                                        List.of("bends", "alternate-picking"),
                                        "e|--15b17--15--12----------------|\nB|----------------15b17--15--12--|",
                                        224, 282)
                        )),
                new Song("little-wing", "Little Wing", "Jimi Hendrix", "Eb Standard",
                        "Em", 68, InstrumentId.GUITAR, 5, "backlog",
                        "Estudar tríades móveis antes do arranjo.", 10,
                        List.of("Hybrid Picking", "Vibrato"), List.of("Pentatônica menor", "Dórico"),
                        List.of(new SongSection("intro", "Intro", 20, 68, "Estudar voicings isolados"))),
                new Song("blackbird", "Blackbird", "The Beatles", "Standard",
                        "G maior", 96, InstrumentId.ACOUSTIC, 3, "learning",
                        "Trabalhar independência do polegar.", 48,
                        List.of("Fingerstyle", "Hammer-on"), List.of("Escala maior"),
                        List.of(
                                new SongSection("intro", "Intro", 65, 96, null),
                                new SongSection("verso", "Verso", 48, 86, null),
                                new SongSection("bridge", "Bridge", 22, 72, "Fixar o baixo")
                        )),
                new Song("gymnopedie", "Gymnopédie No.1", "Erik Satie", "—",
                        "D maior", 60, InstrumentId.KEYS, 3, "learning",
                        "Refinar dinâmica e pedal.", 70,
                        List.of("Independência", "Pedal", "Leitura"), List.of("Escala maior"),
                        List.of(
                                new SongSection("a", "Seção A", 88, 60, "Refinar pedal"),
                                new SongSection("b", "Seção B", 70, 54, "Equilibrar as mãos")
                        ))
        );
        seeded.forEach(seed -> songs.findById(seed.getId()).ifPresentOrElse(existing -> {
            if (existing.getSections().isEmpty()
                    || existing.getSections().stream().allMatch(section -> section.getSkillIds().isEmpty())) {
                existing.setSections(seed.getSections());
                songs.save(existing);
            }
        }, () -> songs.save(seed)));
    }

    private void seedExercises() {
        if (exercises.count() > 0) return;
        exercises.saveAll(List.of(
                new Exercise("ex1", "Cromático 1-2-3-4", "Alternate Picking", InstrumentId.GUITAR,
                        150, 118, 8, "Quatro notas por corda, subindo e descendo.", "alternate-picking"),
                new Exercise("ex2", "Troca de corda em tercinas", "Alternate Picking", InstrumentId.GUITAR,
                        132, 96, 6, "Grupos de três com troca na última nota.", "alternate-picking"),
                new Exercise("ex5", "Bend afinado de um tom", "Bends", InstrumentId.GUITAR,
                        90, 72, 6, "Bend conferido com uma nota de referência.", "bends"),
                new Exercise("ex6", "Vibrato controlado", "Vibrato", InstrumentId.GUITAR,
                        100, 78, 5, "Vibrato em colcheias e tercinas.", "vibrato"),
                new Exercise("ex15", "Fingerstyle p-i-m-a", "Fingerstyle", InstrumentId.ACOUSTIC,
                        100, 68, 8, "Padrão fixo com baixo alternado.", "fingerstyle"),
                new Exercise("ex17", "Leitura à primeira vista", "Leitura", InstrumentId.KEYS,
                        70, 52, 10, "Um trecho novo por dia sem parar.", "note-reading")
        ));
    }

    private void enrichLearningContent() {
        skills.findAll().forEach(skill -> {
            var contentId = skill.getContents().stream()
                    .map(library::findById)
                    .flatMap(java.util.Optional::stream)
                    .filter(content -> skill.getId().equals(content.getSkillId())
                            || content.getTechnicalName().equalsIgnoreCase(skill.getTechnicalName()))
                    .map(LibraryContent::getId)
                    .findFirst()
                    .orElse("lesson-" + skill.getId());
            var generatedLesson = LearningCatalog.lesson(skill, contentId);
            library.findById(contentId).ifPresentOrElse(existing -> {
                var editorial = LearningCatalog.isEditorial(skill.getId());
                existing.update(editorial ? generatedLesson.getFriendlyTitle() : existing.getFriendlyTitle(),
                        existing.getTechnicalName(), existing.getCategory(),
                        editorial ? generatedLesson.getSummary() : existing.getSummary(),
                        skill.getId(), generatedLesson.getLevel(),
                        generatedLesson.getEstimatedMinutes(), generatedLesson.getDiagramType(),
                        generatedLesson.getDiagramData(), generatedLesson.getTablature(),
                        generatedLesson.getObjectives(), editorial || existing.getBody().isEmpty()
                                ? generatedLesson.getBody() : existing.getBody(),
                        editorial || existing.getExamples().isEmpty()
                                ? generatedLesson.getExamples() : existing.getExamples(),
                        generatedLesson.getCommonMistakes(), generatedLesson.getSteps(), existing.getRelated());
                library.save(existing);
            }, () -> library.save(generatedLesson));
            skill.attachContent(contentId);

            var exerciseId = skill.getExercises().stream().filter(exercises::existsById).findFirst()
                    .orElse("practice-" + skill.getId());
            var generatedExercise = LearningCatalog.exercise(skill, exerciseId);
            exercises.findById(exerciseId).ifPresentOrElse(existing -> {
                if (existing.getInstructions().isEmpty()) {
                    existing.update(existing.getName(), existing.getTechnique(), existing.getInstrument(),
                            existing.getTargetBpm(), existing.getCurrentBpm(), existing.getMinutes(),
                            existing.getDescription(), skill.getId(), generatedExercise.getDifficulty(),
                            generatedExercise.getMinBpm(), generatedExercise.getBpmStep(),
                            generatedExercise.getPassAccuracy(), generatedExercise.getPassRepetitions(),
                            generatedExercise.getInstructions(), generatedExercise.getVariations());
                    exercises.save(existing);
                }
            }, () -> exercises.save(generatedExercise));
            skill.attachExercise(exerciseId);
            skills.save(skill);
        });
    }

    private void seedPreferences() {
        if (!preferences.existsById("default")) {
            preferences.save(new UserPreferences("intermediate", 60,
                    List.of("Rock", "Blues", "Post-rock", "Instrumental"),
                    List.of("Guns N' Roses", "Jimi Hendrix", "The Beatles", "Russian Circles")));
        }
    }

    private void seedProjects() {
        var seeded = List.of(
                new MusicProject("umbra", "Umbra", "Em", 132, "arranging",
                        "Sombras longas na sala vazia...",
                        List.of("Trocar o refrão para 6/8", "Guitarra limpa com delay"),
                        List.of("Russian Circles — Harper Lewis"),
                        List.of(new ProjectRiff("r1", "Riff principal",
                                "D|--2-2-5-2--7-5-----|\nA|--2-2-5-2--7-5-----|\nE|--0-0-3-0--5-3-----|")),
                        List.of(new ProjectVersion("v1", "v0.1 — esqueleto", "02/07"),
                                new ProjectVersion("v2", "v0.2 — riff e ponte", "14/07"))),
                new MusicProject("noturno", "Noturno", "Dm", 72, "sketch", "",
                        List.of("Melodia sobre pedal de D", "Testar rearmonização"),
                        List.of("Satie", "Nils Frahm"),
                        List.of(new ProjectRiff("r1", "Tema de teclado", "Dm — Bb — F — C")),
                        List.of(new ProjectVersion("v1", "v0.1 — ideia inicial", "09/07")))
        );
        seeded.forEach(seed -> projects.findById(seed.getId()).ifPresentOrElse(existing -> {
            if (existing.getRiffs().isEmpty()) existing.setRiffs(seed.getRiffs());
            if (existing.getVersions().isEmpty()) existing.setVersions(seed.getVersions());
            projects.save(existing);
        }, () -> projects.save(seed)));
    }

    private void seedJournal() {
        if (journal.count() > 0) return;
        var now = Instant.now();
        journal.saveAll(List.of(
                entry(now.minus(1, ChronoUnit.DAYS), 8040, InstrumentId.GUITAR,
                        List.of("Pentatônica", "Alternate Picking", "Sweet Child O' Mine"),
                        "Bends desafinando.", "Cromático subiu para 110 BPM."),
                entry(now.minus(2, ChronoUnit.DAYS), 6300, InstrumentId.KEYS,
                        List.of("Leitura", "Campo Harmônico", "Gymnopédie"),
                        "Figuras pontuadas lentas.", "Seção A sem parar."),
                entry(now.minus(3, ChronoUnit.DAYS), 6480, InstrumentId.GUITAR,
                        List.of("Legato", "Improvisação"), "Ruído de cordas soltas.", "Fraseado mais musical."),
                entry(now.minus(4, ChronoUnit.DAYS), 3120, InstrumentId.ACOUSTIC,
                        List.of("Blackbird", "Levadas"), "Polegar perde constância.", "Intro estável."),
                entry(now.minus(5, ChronoUnit.DAYS), 9000, InstrumentId.GUITAR,
                        List.of("Arpejos", "Projeto Umbra"), "Sweep embolado.", "Duas versões gravadas.")
        ));
    }

    private JournalEntry entry(Instant at, long seconds, InstrumentId instrument, List<String> worked,
                               String difficulties, String improvements) {
        return new JournalEntry(at, seconds, instrument, worked, difficulties, improvements, "");
    }
}
