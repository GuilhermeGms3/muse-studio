package com.musicos.config;

import com.musicos.domain.InstrumentId;
import com.musicos.domain.LearningStage;
import com.musicos.domain.LearningTrack;
import com.musicos.domain.Skill;
import com.musicos.domain.SkillKind;
import com.musicos.domain.SkillState;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class InstrumentCurriculumCatalog {
    private InstrumentCurriculumCatalog() {}

    static List<Skill> all() {
        var result = new ArrayList<Skill>();

        // Guitarra: base ampla antes de velocidade e linguagem avançada.
        result.add(node("guitar-parts", "Conhecer a guitarra", "Partes e Controles", "Primeiros passos",
                InstrumentId.GUITAR, LearningStage.FIRST_STEPS, SkillKind.KNOWLEDGE, LearningTrack.TECHNIQUE,
                ids(), ids("guitar-tuning"), null));
        result.add(node("guitar-tuning", "Afinar antes de tocar", "Afinação da Guitarra", "Primeiros passos",
                InstrumentId.GUITAR, LearningStage.FIRST_STEPS, SkillKind.ABILITY, LearningTrack.TECHNIQUE,
                ids("guitar-parts"), ids("guitar-clean-fretting"), null));
        result.add(node("guitar-clean-fretting", "Produzir notas limpas", "Pressão e Posição dos Dedos", "Técnica",
                InstrumentId.GUITAR, LearningStage.BEGINNER, SkillKind.ABILITY, LearningTrack.TECHNIQUE,
                ids("guitar-tuning", "guitar-posture"), ids("guitar-pick-control"), 70));
        result.add(node("guitar-pick-control", "Atacar uma corda com controle", "Controle de Palheta", "Técnica",
                InstrumentId.GUITAR, LearningStage.BEGINNER, SkillKind.ABILITY, LearningTrack.TECHNIQUE,
                ids("guitar-clean-fretting", "pulse"), ids("alternate-picking"), 80));
        result.add(node("power-chords", "Tocar riffs com power chords", "Power Chords", "Técnica",
                InstrumentId.GUITAR, LearningStage.BEGINNER_ADVANCED, SkillKind.ABILITY, LearningTrack.TECHNIQUE,
                ids("guitar-clean-fretting", "subdivisions"), ids("rhythm-guitar"), 95));
        result.add(node("rhythm-guitar", "Sustentar uma base inteira", "Guitarra Rítmica", "Ritmo",
                InstrumentId.GUITAR, LearningStage.BEGINNER_ADVANCED, SkillKind.ABILITY, LearningTrack.RHYTHM,
                ids("power-chords", "syncopation"), ids("muting-control"), 100));
        result.add(node("muting-control", "Eliminar ruídos entre as notas", "Abafamento de Cordas", "Técnica",
                InstrumentId.GUITAR, LearningStage.EARLY_INTERMEDIATE, SkillKind.ABILITY, LearningTrack.TECHNIQUE,
                ids("rhythm-guitar", "alternate-picking"), ids("riff-vocabulary"), 105));
        result.add(node("riff-vocabulary", "Construir riffs com intenção", "Vocabulário de Riffs", "Repertório",
                InstrumentId.GUITAR, LearningStage.EARLY_INTERMEDIATE, SkillKind.ABILITY, LearningTrack.REPERTOIRE,
                ids("muting-control", "minor-pentatonic"), ids("position-shifts"), 105));
        result.add(node("position-shifts", "Mudar de região sem perder a frase", "Trocas de Posição", "Técnica",
                InstrumentId.GUITAR, LearningStage.INTERMEDIATE, SkillKind.ABILITY, LearningTrack.TECHNIQUE,
                ids("riff-vocabulary", "sync"), ids("articulation-combinations"), 115));
        result.add(node("articulation-combinations", "Combinar técnicas dentro da frase", "Articulações Combinadas",
                "Improvisação", InstrumentId.GUITAR, LearningStage.INTERMEDIATE, SkillKind.ABILITY,
                LearningTrack.IMPROVISATION, ids("position-shifts", "phrasing"), ids("lead-guitar-language"), 110));
        result.add(node("lead-guitar-language", "Conduzir um solo sobre a forma", "Linguagem de Lead Guitar",
                "Improvisação", InstrumentId.GUITAR, LearningStage.UPPER_INTERMEDIATE, SkillKind.ABILITY,
                LearningTrack.IMPROVISATION, ids("articulation-combinations", "phrasing"),
                ids("guitar-style-adaptation"), 120));
        result.add(node("guitar-style-adaptation", "Adaptar técnica a diferentes estilos", "Fluência Estilística",
                "Performance", InstrumentId.GUITAR, LearningStage.ADVANCED, SkillKind.ABILITY,
                LearningTrack.PERFORMANCE, ids("lead-guitar-language", "performance"), ids(), 125));

        // Violão: acompanhamento musical, independência e arranjo.
        result.add(node("acoustic-posture", "Apoiar o violão sem tensão", "Postura no Violão", "Primeiros passos",
                InstrumentId.ACOUSTIC, LearningStage.FIRST_STEPS, SkillKind.ABILITY, LearningTrack.TECHNIQUE,
                ids(), ids("acoustic-tuning"), null));
        result.add(node("acoustic-tuning", "Afinar o violão", "Afinação do Violão", "Primeiros passos",
                InstrumentId.ACOUSTIC, LearningStage.FIRST_STEPS, SkillKind.ABILITY, LearningTrack.TECHNIQUE,
                ids("acoustic-posture"), ids("acoustic-clean-notes"), null));
        result.add(node("acoustic-clean-notes", "Fazer cada corda soar limpa", "Som Limpo no Violão", "Técnica",
                InstrumentId.ACOUSTIC, LearningStage.BEGINNER, SkillKind.ABILITY, LearningTrack.TECHNIQUE,
                ids("acoustic-tuning"), ids("open-chords"), 70));
        result.add(node("two-chord-song", "Acompanhar uma música com dois acordes", "Primeira Música", "Repertório",
                InstrumentId.ACOUSTIC, LearningStage.BEGINNER, SkillKind.ABILITY, LearningTrack.REPERTOIRE,
                ids("open-chords", "pulse"), ids("common-progressions"), 75));
        result.add(node("common-progressions", "Reconhecer progressões comuns", "Progressões Essenciais", "Harmonia",
                InstrumentId.ACOUSTIC, LearningStage.BEGINNER, SkillKind.KNOWLEDGE, LearningTrack.HARMONY,
                ids("two-chord-song", "triads"), ids("quarter-strumming"), null));
        result.add(node("quarter-strumming", "Manter uma levada em semínimas", "Levada em Semínimas", "Ritmo",
                InstrumentId.ACOUSTIC, LearningStage.BEGINNER, SkillKind.ABILITY, LearningTrack.RHYTHM,
                ids("two-chord-song", "pulse"), ids("eighth-strumming"), 80));
        result.add(node("eighth-strumming", "Tocar levadas em colcheias", "Levada em Colcheias", "Ritmo",
                InstrumentId.ACOUSTIC, LearningStage.BEGINNER_ADVANCED, SkillKind.ABILITY, LearningTrack.RHYTHM,
                ids("quarter-strumming", "subdivisions"), ids("syncopated-strumming"), 90));
        result.add(node("syncopated-strumming", "Criar balanço sem perder o pulso", "Levada Sincopada", "Ritmo",
                InstrumentId.ACOUSTIC, LearningStage.BEGINNER_ADVANCED, SkillKind.ABILITY, LearningTrack.RHYTHM,
                ids("eighth-strumming", "syncopation"), ids("capo-use"), 95));
        result.add(node("capo-use", "Mudar o tom preservando as formas", "Uso do Capotraste", "Harmonia",
                InstrumentId.ACOUSTIC, LearningStage.BEGINNER_ADVANCED, SkillKind.KNOWLEDGE, LearningTrack.HARMONY,
                ids("open-chords", "intervals"), ids("acoustic-arpeggio-patterns"), null));
        result.add(node("acoustic-arpeggio-patterns", "Acompanhar com arpejos", "Padrões de Arpejo", "Técnica",
                InstrumentId.ACOUSTIC, LearningStage.EARLY_INTERMEDIATE, SkillKind.ABILITY, LearningTrack.TECHNIQUE,
                ids("capo-use", "subdivisions"), ids("fingerstyle-independence"), 90));
        result.add(node("fingerstyle-independence", "Separar polegar e dedos", "Independência no Fingerstyle",
                "Técnica", InstrumentId.ACOUSTIC, LearningStage.INTERMEDIATE, SkillKind.ABILITY,
                LearningTrack.TECHNIQUE, ids("acoustic-arpeggio-patterns", "fingerstyle"),
                ids("accompaniment-dynamics"), 100));
        result.add(node("accompaniment-dynamics", "Acompanhar sem cobrir a voz", "Dinâmica de Acompanhamento",
                "Performance", InstrumentId.ACOUSTIC, LearningStage.INTERMEDIATE, SkillKind.ABILITY,
                LearningTrack.PERFORMANCE, ids("fingerstyle-independence", "dynamics"),
                ids("transposed-accompaniment"), 100));
        result.add(node("transposed-accompaniment", "Transportar acompanhamentos com fluidez",
                "Transposição Aplicada", "Harmonia", InstrumentId.ACOUSTIC, LearningStage.UPPER_INTERMEDIATE,
                SkillKind.ABILITY, LearningTrack.HARMONY, ids("accompaniment-dynamics", "harmonic-functions"),
                ids("solo-acoustic-arrangement"), null));
        result.add(node("solo-acoustic-arrangement", "Criar um arranjo completo para violão solo",
                "Arranjo para Violão Solo", "Arranjo", InstrumentId.ACOUSTIC, LearningStage.ADVANCED,
                SkillKind.ABILITY, LearningTrack.CREATION, ids("transposed-accompaniment", "fingerstyle-independence"),
                ids(), null));

        // Teclado: mapa, leitura, coordenação, acompanhamento e voicings.
        result.add(node("piano-posture", "Sentar e mover as mãos sem tensão", "Postura no Teclado", "Primeiros passos",
                InstrumentId.KEYS, LearningStage.FIRST_STEPS, SkillKind.ABILITY, LearningTrack.TECHNIQUE,
                ids(), ids("finger-numbers"), null));
        result.add(node("finger-numbers", "Reconhecer os dedos pelo número", "Numeração dos Dedos", "Primeiros passos",
                InstrumentId.KEYS, LearningStage.FIRST_STEPS, SkillKind.KNOWLEDGE, LearningTrack.TECHNIQUE,
                ids("piano-posture"), ids("five-finger-position"), null));
        result.add(node("five-finger-position", "Tocar cinco notas sem deslocar a mão", "Posição de Cinco Dedos",
                "Técnica", InstrumentId.KEYS, LearningStage.BEGINNER, SkillKind.ABILITY, LearningTrack.TECHNIQUE,
                ids("finger-numbers", "keyboard-map"), ids("keys-note-landmarks"), 70));
        result.add(node("keys-note-landmarks", "Encontrar notas de referência rapidamente", "Notas-Guia no Teclado",
                "Leitura", InstrumentId.KEYS, LearningStage.BEGINNER, SkillKind.KNOWLEDGE, LearningTrack.READING,
                ids("five-finger-position", "notes"), ids("hands-together-basic"), null));
        result.add(node("hands-together-basic", "Coordenar as mãos em movimentos simples", "Mãos Juntas", "Técnica",
                InstrumentId.KEYS, LearningStage.BEGINNER_ADVANCED, SkillKind.ABILITY, LearningTrack.TECHNIQUE,
                ids("keys-note-landmarks", "pulse"), ids("chord-shells"), 75));
        result.add(node("chord-shells", "Acompanhar com formas reduzidas", "Shell Voicings Básicos", "Harmonia",
                InstrumentId.KEYS, LearningStage.BEGINNER_ADVANCED, SkillKind.ABILITY, LearningTrack.HARMONY,
                ids("hands-together-basic", "triads"), ids("left-hand-patterns"), 80));
        result.add(node("left-hand-patterns", "Criar bases com a mão esquerda", "Padrões de Mão Esquerda", "Técnica",
                InstrumentId.KEYS, LearningStage.EARLY_INTERMEDIATE, SkillKind.ABILITY, LearningTrack.TECHNIQUE,
                ids("chord-shells", "subdivisions"), ids("pedal-control"), 85));
        result.add(node("pedal-control", "Usar pedal sem borrar a harmonia", "Controle de Pedal", "Performance",
                InstrumentId.KEYS, LearningStage.EARLY_INTERMEDIATE, SkillKind.ABILITY, LearningTrack.PERFORMANCE,
                ids("left-hand-patterns", "harmonic-functions"), ids("keys-accompaniment-patterns"), null));
        result.add(node("keys-accompaniment-patterns", "Acompanhar em diferentes texturas",
                "Padrões de Acompanhamento", "Repertório", InstrumentId.KEYS, LearningStage.INTERMEDIATE,
                SkillKind.ABILITY, LearningTrack.REPERTOIRE, ids("pedal-control", "keys-voicings"),
                ids("scale-coordination"), 95));
        result.add(node("scale-coordination", "Executar escalas com as duas mãos", "Coordenação de Escalas", "Técnica",
                InstrumentId.KEYS, LearningStage.INTERMEDIATE, SkillKind.ABILITY, LearningTrack.TECHNIQUE,
                ids("keys-accompaniment-patterns", "keys-fingering"), ids("keys-sight-reading-fluency"), 110));
        result.add(node("keys-sight-reading-fluency", "Ler mantendo pulso e direção", "Fluência de Leitura", "Leitura",
                InstrumentId.KEYS, LearningStage.INTERMEDIATE, SkillKind.ABILITY, LearningTrack.READING,
                ids("scale-coordination", "sight-reading"), ids("pop-voicings"), 90));
        result.add(node("pop-voicings", "Construir voicings para música popular", "Voicings Pop", "Harmonia",
                InstrumentId.KEYS, LearningStage.UPPER_INTERMEDIATE, SkillKind.ABILITY, LearningTrack.HARMONY,
                ids("keys-sight-reading-fluency", "voice-leading"), ids("keys-reharmonization"), null));
        result.add(node("keys-reharmonization", "Rearmonizar uma melodia no teclado", "Rearmonização no Teclado",
                "Criação", InstrumentId.KEYS, LearningStage.ADVANCED, SkillKind.ABILITY, LearningTrack.CREATION,
                ids("pop-voicings", "harmonic-field"), ids(), null));

        // Bateria: leitura e grooves básicos até linguagem avançada.
        result.add(node("drum-quarter-note", "Marcar semínimas no kit", "Semínimas na Bateria", "Primeiros passos",
                InstrumentId.DRUMS, LearningStage.FIRST_STEPS, SkillKind.ABILITY, LearningTrack.RHYTHM,
                ids("drum-kit-map", "pulse"), ids("drum-eighth-hihat"), 70));
        result.add(node("drum-eighth-hihat", "Conduzir colcheias no chimbal", "Chimbal em Colcheias", "Grooves",
                InstrumentId.DRUMS, LearningStage.BEGINNER, SkillKind.ABILITY, LearningTrack.RHYTHM,
                ids("drum-quarter-note", "subdivisions"), ids("drum-basic-reading"), 80));
        result.add(node("drum-basic-reading", "Ler bumbo, caixa e chimbal", "Leitura Básica de Bateria", "Leitura",
                InstrumentId.DRUMS, LearningStage.BEGINNER, SkillKind.KNOWLEDGE, LearningTrack.READING,
                ids("drum-eighth-hihat", "rhythm-reading"), ids("drum-two-grooves"), null));
        result.add(node("drum-two-grooves", "Alternar dois grooves sem parar", "Transição entre Grooves", "Repertório",
                InstrumentId.DRUMS, LearningStage.BEGINNER_ADVANCED, SkillKind.ABILITY, LearningTrack.REPERTOIRE,
                ids("drum-basic-reading", "drum-groove-consistency"), ids("drum-triplet-control"), 90));
        result.add(node("drum-triplet-control", "Distribuir tercinas entre os membros", "Controle de Tercinas",
                "Coordenação", InstrumentId.DRUMS, LearningStage.EARLY_INTERMEDIATE, SkillKind.ABILITY,
                LearningTrack.TECHNIQUE, ids("drum-two-grooves", "drum-shuffle"), ids("drum-funk-groove"), 90));
        result.add(node("drum-funk-groove", "Construir grooves de funk", "Vocabulário de Funk", "Grooves",
                InstrumentId.DRUMS, LearningStage.INTERMEDIATE, SkillKind.ABILITY, LearningTrack.RHYTHM,
                ids("drum-triplet-control", "drum-ghost-notes"), ids("drum-jazz-ride"), 100));
        result.add(node("drum-jazz-ride", "Conduzir swing no ride", "Padrão de Ride no Jazz", "Grooves",
                InstrumentId.DRUMS, LearningStage.INTERMEDIATE, SkillKind.ABILITY, LearningTrack.RHYTHM,
                ids("drum-funk-groove", "drum-ride-coordination"), ids("drum-linear-displacement"), 105));
        result.add(node("drum-harmonic-landmarks", "Ouvir mudanças harmônicas na forma",
                "Marcos Harmônicos para Bateristas", "Harmonia", InstrumentId.DRUMS,
                LearningStage.EARLY_INTERMEDIATE, SkillKind.KNOWLEDGE, LearningTrack.HARMONY,
                ids("drum-two-grooves", "ear-rhythm"), ids("drum-fill-improvisation"), null));
        result.add(node("drum-fill-improvisation", "Improvisar viradas que respeitam a música",
                "Improvisação de Viradas", "Improvisação", InstrumentId.DRUMS,
                LearningStage.INTERMEDIATE, SkillKind.ABILITY, LearningTrack.IMPROVISATION,
                ids("drum-harmonic-landmarks", "drum-fill-orchestration"), ids("drum-groove-composition"), 100));
        result.add(node("drum-groove-composition", "Criar um groove para uma forma completa",
                "Composição de Grooves", "Composição", InstrumentId.DRUMS,
                LearningStage.UPPER_INTERMEDIATE, SkillKind.ABILITY, LearningTrack.CREATION,
                ids("drum-fill-improvisation", "drum-harmonic-landmarks"), ids("drum-style-adaptation"), 105));
        result.add(node("drum-linear-displacement", "Deslocar células lineares", "Deslocamento Linear", "Coordenação",
                InstrumentId.DRUMS, LearningStage.UPPER_INTERMEDIATE, SkillKind.ABILITY, LearningTrack.TECHNIQUE,
                ids("drum-jazz-ride", "drum-linear-groove"), ids("drum-odd-meter"), 105));
        result.add(node("drum-odd-meter", "Sustentar grooves em compassos ímpares", "Compassos Ímpares", "Ritmo",
                InstrumentId.DRUMS, LearningStage.UPPER_INTERMEDIATE, SkillKind.ABILITY, LearningTrack.RHYTHM,
                ids("drum-linear-displacement", "meter"), ids("drum-polyrhythm"), 100));
        result.add(node("drum-polyrhythm", "Coordenar camadas métricas diferentes", "Polirritmia", "Coordenação",
                InstrumentId.DRUMS, LearningStage.ADVANCED, SkillKind.ABILITY, LearningTrack.TECHNIQUE,
                ids("drum-odd-meter", "drum-limb-independence"), ids("drum-style-adaptation"), 105));
        result.add(node("drum-style-adaptation", "Adaptar o kit a diferentes estilos", "Fluência Estilística",
                "Performance", InstrumentId.DRUMS, LearningStage.ADVANCED, SkillKind.ABILITY,
                LearningTrack.PERFORMANCE, ids("drum-polyrhythm", "drum-play-along"), ids(), 110));

        return result;
    }

    private static Skill node(String id, String friendly, String technical, String domain,
                              InstrumentId instrument, LearningStage stage, SkillKind kind, LearningTrack track,
                              List<String> prerequisites, List<String> next, Integer targetBpm) {
        var skill = new Skill(id, friendly, technical, domain,
                "Desenvolva " + technical + " em etapas curtas, com referência, aplicação musical e revisão.",
                prerequisites.isEmpty() ? SkillState.AVAILABLE : SkillState.LOCKED,
                0, 0, null, targetBpm, List.of(instrument), prerequisites, List.of(), List.of(), List.of(), next);
        skill.configureCurriculum(stage, kind, track);
        return skill;
    }

    private static List<String> ids(String... values) { return Arrays.asList(values); }
}
