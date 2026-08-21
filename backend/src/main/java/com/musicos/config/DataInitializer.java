package com.musicos.config;

import com.musicos.domain.*;
import com.musicos.repository.*;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements ApplicationRunner {
    private final InstrumentRepository instruments;
    private final SkillRepository skills;
    private final LibraryContentRepository library;
    private final SongRepository songs;
    private final ExerciseRepository exercises;

    public DataInitializer(InstrumentRepository instruments, SkillRepository skills,
                           LibraryContentRepository library, SongRepository songs,
                           ExerciseRepository exercises) {
        this.instruments = instruments;
        this.skills = skills;
        this.library = library;
        this.songs = songs;
        this.exercises = exercises;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedInstruments();
        seedSkills();
        seedLibrary();
        seedSongs();
        linkSongsToSkills();
        seedExercises();
        enrichLearningContent();
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

    private void seedSkills() {
        var core = List.of(
                new Skill("rhythm", "Sentir e sustentar o pulso", "Ritmo", "Fundamentos",
                        "Organizar notas no tempo com estabilidade.", SkillState.AVAILABLE, 0, 0, null, null,
                        List.of(InstrumentId.GUITAR, InstrumentId.ACOUSTIC, InstrumentId.KEYS),
                        List.of(), List.of("ritmo"), List.of(), List.of(), List.of("alternate-picking")),
                new Skill("alternate-picking", "Tocar com palhetadas alternadas", "Alternate Picking", "Técnica",
                        "Alternar ataques para ganhar precisão e velocidade sem tensão.", SkillState.LOCKED,
                        0, 0, null, 140, List.of(InstrumentId.GUITAR),
                        List.of("rhythm"), List.of(),
                        List.of("guitar-chromatic-1234", "guitar-string-crossing-triplets"),
                        List.of("sweet-child"), List.of("bends", "string-skipping")),
                new Skill("bends", "Afinar bends com confiança", "Bends", "Expressão",
                        "Controlar altura, chegada e sustentação do bend usando uma nota de referência.",
                        SkillState.LOCKED, 0, 0, null, 90, List.of(InstrumentId.GUITAR),
                        List.of("alternate-picking"), List.of("bends"),
                        List.of("guitar-whole-step-bend"),
                        List.of("sweet-child"), List.of("vibrato")),
                new Skill("vibrato", "Dar vida às notas longas", "Vibrato", "Expressão",
                        "Controlar amplitude e pulsação do vibrato.", SkillState.LOCKED, 0, 0, null, 100,
                        List.of(InstrumentId.GUITAR), List.of("bends"), List.of(),
                        List.of("guitar-controlled-vibrato"),
                        List.of("sweet-child"), List.of()),
                new Skill("harmonic-field", "Entender como os acordes funcionam juntos", "Campo Harmônico",
                        "Harmonia", "Formar acordes em cada grau e reconhecer suas funções.",
                        SkillState.LOCKED, 0, 0, null, null,
                        List.of(InstrumentId.GUITAR, InstrumentId.ACOUSTIC, InstrumentId.KEYS),
                        List.of("rhythm"), List.of("campo-harmonico"), List.of(), List.of(),
                        List.of("modes")),
                new Skill("modes", "Improvisar usando as cores da escala maior", "Modos Gregos",
                        "Improvisação", "Ouvir e aplicar as notas características de cada modo.",
                        SkillState.LOCKED, 0, 0, null, null,
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
                        "Db maior", 126, InstrumentId.GUITAR, 4, "backlog", null, 0,
                        List.of("Alternate Picking", "Bends", "Vibrato"), List.of("Pentatônica menor"),
                        List.of(
                                new SongSection("intro", "Intro", 0, 126, "Manter o riff relaxado",
                                        List.of("alternate-picking", "string-skipping"),
                                        "e|----------------15----14----|\nB|--15----13-------------------|", 0, 52),
                                new SongSection("verso", "Verso", 0, 126, null),
                                new SongSection("solo-a", "Solo A", 0, 104, null,
                                        List.of("minor-pentatonic", "vibrato"), null, 185, 224),
                                new SongSection("solo-b", "Solo B", 0, 92, "Dividir em quatro células",
                                        List.of("bends", "alternate-picking"),
                                        "e|--15b17--15--12----------------|\nB|----------------15b17--15--12--|",
                                        224, 282)
                        )),
                new Song("little-wing", "Little Wing", "Jimi Hendrix", "Eb Standard",
                        "Em", 68, InstrumentId.GUITAR, 5, "backlog", null, 0,
                        List.of("Hybrid Picking", "Vibrato"), List.of("Pentatônica menor", "Dórico"),
                        List.of(new SongSection("intro", "Intro", 0, 68, "Estudar voicings isolados"))),
                new Song("blackbird", "Blackbird", "The Beatles", "Standard",
                        "G maior", 96, InstrumentId.ACOUSTIC, 3, "backlog", null, 0,
                        List.of("Fingerstyle", "Hammer-on"), List.of("Escala maior"),
                        List.of(
                                new SongSection("intro", "Intro", 0, 96, null),
                                new SongSection("verso", "Verso", 0, 86, null),
                                new SongSection("bridge", "Bridge", 0, 72, "Fixar o baixo")
                        )),
                new Song("gymnopedie", "Gymnopédie No.1", "Erik Satie", "—",
                        "D maior", 60, InstrumentId.KEYS, 3, "backlog", null, 0,
                        List.of("Independência", "Pedal", "Leitura"), List.of("Escala maior"),
                        List.of(
                                new SongSection("a", "Seção A", 0, 60, "Refinar pedal"),
                                new SongSection("b", "Seção B", 0, 54, "Equilibrar as mãos")
                        )),
                new Song("seven-nation-army-drums", "Seven Nation Army", "The White Stripes", "Kit padrão",
                        "Em", 124, InstrumentId.DRUMS, 1, "backlog", null, 0,
                        List.of("Rock Beat", "Consistência", "Viradas curtas"), List.of("Colcheias"),
                        List.of(
                                new SongSection("verse", "Verso", 0, 100, "Comece sem viradas",
                                        List.of("drum-rock-groove", "drum-groove-consistency"),
                                        "HH|x-x-x-x-|\nSD|----o---|\nBD|o-------|", 4, 50),
                                new SongSection("chorus", "Refrão", 0, 90, "Caixa firme em 2 e 4",
                                        List.of("drum-kick-variations", "drum-dynamics"),
                                        "HH|x-x-x-x-|\nSD|--o---o-|\nBD|o---o---|", 51, 78),
                                new SongSection("fills", "Viradas", 0, 70, "Uma virada de um tempo",
                                        List.of("drum-one-beat-fill", "drum-fill-timing"),
                                        "T1|------oo|\nSD|----oo--|\nBD|o-------|", 79, 96)
                        )),
                new Song("back-in-black-drums", "Back In Black", "AC/DC", "Kit padrão",
                        "E", 94, InstrumentId.DRUMS, 2, "backlog", null, 0,
                        List.of("Rock Beat", "Chimbal aberto", "Dinâmica"), List.of("Colcheias", "Pausas"),
                        List.of(
                                new SongSection("intro", "Intro", 0, 75, "Contar as pausas em voz alta",
                                        List.of("drum-groove-consistency", "drum-open-hihat"),
                                        "HH|x-x-x-O-|\nSD|--o---o-|\nBD|o---o---|", 0, 24),
                                new SongSection("verse", "Verso", 0, 75, "Tocar atrás da guitarra",
                                        List.of("drum-kick-variations", "drum-dynamics"), null, 25, 82)
                        )),
                new Song("billie-jean-drums", "Billie Jean", "Michael Jackson", "Kit padrão",
                        "F#m", 117, InstrumentId.DRUMS, 2, "backlog", null, 0,
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
                new Exercise("guitar-chromatic-1234", "Cromático 1-2-3-4", "Alternate Picking", InstrumentId.GUITAR,
                        150, 118, 8, "Quatro notas por corda, subindo e descendo.", "alternate-picking"),
                new Exercise("guitar-string-crossing-triplets", "Troca de corda em tercinas", "Alternate Picking", InstrumentId.GUITAR,
                        132, 96, 6, "Grupos de três com troca na última nota.", "alternate-picking"),
                new Exercise("guitar-whole-step-bend", "Bend afinado de um tom", "Bends", InstrumentId.GUITAR,
                        90, 72, 6, "Bend conferido com uma nota de referência.", "bends"),
                new Exercise("guitar-controlled-vibrato", "Vibrato controlado", "Vibrato", InstrumentId.GUITAR,
                        100, 78, 5, "Vibrato em colcheias e tercinas.", "vibrato"),
                new Exercise("acoustic-fingerstyle-pima", "Fingerstyle p-i-m-a", "Fingerstyle", InstrumentId.ACOUSTIC,
                        100, 68, 8, "Padrão fixo com baixo alternado.", "fingerstyle"),
                new Exercise("keys-sight-reading", "Leitura à primeira vista", "Leitura", InstrumentId.KEYS,
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
        TeachingContentCatalog.exercises().forEach(generated ->
                exercises.findById(generated.getId()).ifPresentOrElse(existing -> {
                    existing.update(generated.getName(), generated.getTechnique(), generated.getInstrument(),
                            generated.getTargetBpm(), existing.getCurrentBpm(), generated.getMinutes(),
                            generated.getDescription(), generated.getSkillId(), generated.getDifficulty(),
                            generated.getMinBpm(), generated.getBpmStep(), generated.getPassAccuracy(),
                            generated.getPassRepetitions(), generated.getInstructions(), generated.getVariations());
                    existing.updateLearningResources(generated.getActivityType(), generated.getStage(),
                            generated.getVideoQuery(), generated.getReadingTitle(), generated.getReadingUrl(),
                            generated.getReadingNote(), generated.getPracticeSongQuery());
                    existing.configurePedagogicalDefinition(generated.getObservableObjective(),
                            generated.getPracticeConditions(), generated.getSuccessCriteria(),
                            generated.getDifficultyDemand(), generated.getCompetencyIds());
                    exercises.save(existing);
                }, () -> exercises.save(generated)));
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

            TeachingContentCatalog.exercises().stream()
                    .filter(exercise -> exercise.getCompetencyIds().contains(skill.getId()))
                    .forEach(exercise -> skill.attachExercise(exercise.getId()));
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

}
