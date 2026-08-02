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
        linkSongsToSkills();
        seedExercises();
        enrichLearningContent();
        seedProjects();
        seedJournal();
        seedPreferences();
    }

    private void seedInstruments() {
        var seeded = List.of(
                new Instrument(InstrumentId.GUITAR, "Guitarra", "GTR",
                        List.of("Técnica", "Repertório", "Improvisação")),
                new Instrument(InstrumentId.ACOUSTIC, "Violão", "VLA",
                        List.of("Acordes", "Levadas", "Fingerstyle")),
                new Instrument(InstrumentId.KEYS, "Teclado", "KEY",
                        List.of("Leitura", "Independência", "Harmonia")),
                new Instrument(InstrumentId.DRUMS, "Bateria", "BAT",
                        List.of("Grooves", "Coordenação", "Viradas"))
        );
        seeded.forEach(instrument -> {
            if (!instruments.existsById(instrument.getId())) instruments.save(instrument);
        });
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
        core.forEach(CurriculumTaxonomy::apply);
        var curriculum = new java.util.ArrayList<Skill>();
        curriculum.addAll(core);
        curriculum.addAll(CurriculumCatalog.all());
        curriculum.addAll(DrumCurriculumCatalog.all());
        curriculum.addAll(InstrumentCurriculumCatalog.all());
        curriculum.forEach(skill -> {
            skills.findById(skill.getId()).ifPresentOrElse(existing -> {
                existing.refreshDefinition(skill.getFriendlyTitle(), skill.getTechnicalName(), skill.getDomain(),
                        skill.getDescription(), skill.getTargetBpm(), skill.getPrerequisites(),
                        skill.getNextSkills(), skill.getInstruments(), skill.getStage(), skill.getKind(),
                        skill.getTrack());
                skills.save(existing);
            }, () -> skills.save(skill));
        });
        List.of("pulse", "subdivisions", "meter", "syncopation", "groove", "dynamics",
                "ear-rhythm", "rhythm-reading", "song-sections", "section-practice", "memorization",
                "performance", "recording-review", "stage-readiness").forEach(skillId ->
                skills.findById(skillId).ifPresent(skill -> {
                    skill.attachInstrument(InstrumentId.DRUMS);
                    skills.save(skill);
                }));
        skills.findById("rhythm").ifPresent(skill -> {
            skill.detachInstrument(InstrumentId.DRUMS);
            skills.save(skill);
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
                        )),
                new Song("seven-nation-army-drums", "Seven Nation Army", "The White Stripes", "Kit padrão",
                        "Em", 124, InstrumentId.DRUMS, 1, "learning",
                        "Manter caixa em 2 e 4 e voltar ao groove depois das viradas.", 15,
                        List.of("Rock Beat", "Consistência", "Viradas curtas"), List.of("Colcheias"),
                        List.of(
                                new SongSection("verse", "Verso", 20, 100, "Comece sem viradas",
                                        List.of("drum-rock-groove", "drum-groove-consistency"),
                                        "HH|x-x-x-x-|\nSD|----o---|\nBD|o-------|", 4, 50),
                                new SongSection("chorus", "Refrão", 5, 90, "Caixa firme em 2 e 4",
                                        List.of("drum-kick-variations", "drum-dynamics"),
                                        "HH|x-x-x-x-|\nSD|--o---o-|\nBD|o---o---|", 51, 78),
                                new SongSection("fills", "Viradas", 0, 70, "Uma virada de um tempo",
                                        List.of("drum-one-beat-fill", "drum-fill-timing"),
                                        "T1|------oo|\nSD|----oo--|\nBD|o-------|", 79, 96)
                        )),
                new Song("back-in-black-drums", "Back In Black", "AC/DC", "Kit padrão",
                        "E", 94, InstrumentId.DRUMS, 2, "backlog",
                        "Ouvir os espaços e não preencher demais.", 0,
                        List.of("Rock Beat", "Chimbal aberto", "Dinâmica"), List.of("Colcheias", "Pausas"),
                        List.of(
                                new SongSection("intro", "Intro", 0, 75, "Contar as pausas em voz alta",
                                        List.of("drum-groove-consistency", "drum-open-hihat"),
                                        "HH|x-x-x-O-|\nSD|--o---o-|\nBD|o---o---|", 0, 24),
                                new SongSection("verse", "Verso", 0, 75, "Tocar atrás da guitarra",
                                        List.of("drum-kick-variations", "drum-dynamics"), null, 25, 82)
                        )),
                new Song("billie-jean-drums", "Billie Jean", "Michael Jackson", "Kit padrão",
                        "F#m", 117, InstrumentId.DRUMS, 2, "backlog",
                        "O desafio é repetir o mesmo groove sem perder a precisão.", 0,
                        List.of("Consistência", "Chimbal", "Bumbo"), List.of("Colcheias"),
                        List.of(new SongSection("main-groove", "Groove principal", 0, 90,
                                "Grave dois minutos sem parar",
                                List.of("drum-groove-consistency", "drum-kick-variations"),
                                "HH|x-x-x-x-|\nSD|--o---o-|\nBD|o--o----|", 0, 120)))
        );
        seeded.forEach(seed -> songs.findById(seed.getId()).ifPresentOrElse(existing -> {
            if (existing.getSections().isEmpty()
                    || existing.getSections().stream().allMatch(section -> section.getSkillIds().isEmpty())) {
                existing.setSections(seed.getSections());
                songs.save(existing);
            }
        }, () -> songs.save(seed)));
        RepertoireCatalog.all().forEach(song -> {
            if (!songs.existsById(song.getId())) songs.save(song);
        });
    }

    private void seedExercises() {
        var seeded = List.of(
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
                        70, 52, 10, "Um trecho novo por dia sem parar.", "note-reading"),
                new Exercise("drum-ex-kit", "Nomear e tocar cada peça", "Mapa da Bateria",
                        InstrumentId.DRUMS, 70, 60, 5,
                        "Ouça o nome, toque a peça certa e volte as baquetas à posição de descanso.",
                        "drum-kit-map"),
                new Exercise("drum-ex-rock", "Groove básico de rock", "Rock Beat",
                        InstrumentId.DRUMS, 100, 70, 8,
                        "Chimbal em colcheias, caixa em 2 e 4 e bumbo em 1 e 3.",
                        "drum-rock-groove"),
                new Exercise("drum-ex-groove", "Dois minutos sem perder o groove", "Consistência",
                        InstrumentId.DRUMS, 105, 75, 8,
                        "Toque sem viradas. Grave e observe se o último compasso está no mesmo BPM do primeiro.",
                        "drum-groove-consistency"),
                new Exercise("drum-ex-single", "Oito por mão e alternado", "Single Stroke",
                        InstrumentId.DRUMS, 120, 70, 7,
                        "Oito golpes com a direita, oito com a esquerda e dezesseis alternados.",
                        "drum-single-stroke"),
                new Exercise("drum-ex-kick", "Quatro posições de bumbo", "Variações de Bumbo",
                        InstrumentId.DRUMS, 100, 70, 8,
                        "Mantenha caixa e chimbal fixos e mova apenas um ataque do bumbo.",
                        "drum-kick-variations"),
                new Exercise("drum-ex-fill-one", "Virada de um tempo", "Viradas",
                        InstrumentId.DRUMS, 90, 60, 8,
                        "Três compassos de groove, uma virada no tempo 4 e retorno ao bumbo no tempo 1.",
                        "drum-one-beat-fill"),
                new Exercise("drum-ex-fill-return", "Voltar no primeiro tempo", "Entrada e Saída",
                        InstrumentId.DRUMS, 100, 65, 8,
                        "Toque a virada mais simples possível e avalie apenas se o retorno caiu no tempo 1.",
                        "drum-fill-timing"),
                new Exercise("drum-ex-playalong", "Uma música sem parar", "Play-along",
                        InstrumentId.DRUMS, 110, 80, 12,
                        "Simplifique as viradas, marque as seções e continue tocando mesmo depois de um erro.",
                        "drum-play-along")
        );
        seeded.forEach(exercise -> {
            if (!exercises.existsById(exercise.getId())) exercises.save(exercise);
        });
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

            LearningCatalog.activities(skill).forEach(generatedExercise -> {
                exercises.findById(generatedExercise.getId()).ifPresentOrElse(existing -> {
                    existing.update(generatedExercise.getName(), generatedExercise.getTechnique(),
                            generatedExercise.getInstrument(), generatedExercise.getTargetBpm(),
                            existing.getCurrentBpm(), generatedExercise.getMinutes(),
                            generatedExercise.getDescription(), skill.getId(), generatedExercise.getDifficulty(),
                            generatedExercise.getMinBpm(), generatedExercise.getBpmStep(),
                            generatedExercise.getPassAccuracy(), generatedExercise.getPassRepetitions(),
                            generatedExercise.getInstructions(), generatedExercise.getVariations());
                    existing.updateLearningResources(generatedExercise.getActivityType(), generatedExercise.getStage(),
                            generatedExercise.getVideoQuery(), generatedExercise.getReadingTitle(),
                            generatedExercise.getReadingUrl(), generatedExercise.getReadingNote(),
                            generatedExercise.getPracticeSongQuery());
                    exercises.save(existing);
                }, () -> exercises.save(generatedExercise));
                skill.attachExercise(generatedExercise.getId());
            });
            skills.save(skill);
        });
    }

    private void linkSongsToSkills() {
        songs.findAll().forEach(song -> song.getSections().forEach(section ->
                section.getSkillIds().forEach(skillId -> skills.findById(skillId).ifPresent(skill -> {
                    skill.attachSong(song.getId());
                    skills.save(skill);
                }))));
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
