package com.musicos.config;

import com.musicos.domain.InstrumentId;
import com.musicos.domain.Skill;
import com.musicos.domain.SkillState;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class DrumCurriculumCatalog {
    private static final List<InstrumentId> DRUMS = List.of(InstrumentId.DRUMS);

    private DrumCurriculumCatalog() {
    }

    static List<Skill> all() {
        var result = new ArrayList<Skill>();

        // Primeiros passos: conhecer o instrumento e produzir movimentos relaxados.
        result.add(node("drum-kit-map", "Saber onde cada som mora", "Mapa da Bateria",
                "Primeiros passos", ids(), ids("drum-setup", "drum-bass-pedal"), null));
        result.add(node("drum-setup", "Montar a bateria para tocar sem esforço", "Ergonomia do Kit",
                "Primeiros passos", ids("drum-kit-map"), ids("drum-grip"), null));
        result.add(node("drum-grip", "Segurar as baquetas sem travar as mãos", "Pegada",
                "Primeiros passos", ids("drum-setup"), ids("drum-rebound", "drum-single-stroke"), 70));
        result.add(node("drum-rebound", "Deixar a baqueta voltar sozinha", "Rebote",
                "Primeiros passos", ids("drum-grip"), ids("drum-accents", "drum-double-stroke"), 80));
        result.add(node("drum-bass-pedal", "Tocar o bumbo com controle", "Técnica de Pedal",
                "Primeiros passos", ids("drum-kit-map", "pulse"), ids("drum-rock-groove"), 80));
        result.add(node("drum-hi-hat-foot", "Controlar o chimbal com o pé", "Controle do Chimbal",
                "Primeiros passos", ids("drum-kit-map", "pulse"), ids("drum-open-hihat"), 80));

        // Grooves: primeiro sustentar, depois variar.
        result.add(node("drum-rock-groove", "Tocar seu primeiro groove completo", "Rock Beat em Colcheias",
                "Grooves", ids("drum-bass-pedal", "subdivisions"), ids("drum-groove-consistency"), 80));
        result.add(node("drum-groove-consistency", "Segurar o groove sem correr nem frear",
                "Consistência de Groove", "Grooves", ids("drum-rock-groove"),
                ids("drum-kick-variations", "drum-one-beat-fill"), 90));
        result.add(node("drum-kick-variations", "Criar grooves mudando apenas o bumbo",
                "Variações de Bumbo", "Grooves", ids("drum-groove-consistency"),
                ids("drum-syncopated-kick", "drum-sixteenth-groove"), 90));
        result.add(node("drum-open-hihat", "Abrir e fechar o chimbal no tempo", "Chimbal Aberto",
                "Grooves", ids("drum-hi-hat-foot", "drum-groove-consistency"),
                ids("drum-ride-coordination"), 90));
        result.add(node("drum-sixteenth-groove", "Construir grooves com semicolcheias",
                "Groove em Semicolcheias", "Grooves", ids("drum-kick-variations", "subdivisions"),
                ids("drum-ghost-notes", "drum-linear-groove"), 85));
        result.add(node("drum-shuffle", "Sentir o balanço ternário", "Shuffle",
                "Grooves", ids("drum-groove-consistency", "subdivisions"),
                ids("drum-half-time"), 80));
        result.add(node("drum-half-time", "Mudar a sensação sem mudar o BPM", "Half-time e Double-time",
                "Grooves", ids("drum-shuffle", "meter"), ids("drum-song-form"), 90));

        // Coordenação: independência construída em camadas, não por força.
        result.add(node("drum-limb-independence", "Dar uma função diferente para cada membro",
                "Independência dos Membros", "Coordenação", ids("drum-rock-groove"),
                ids("drum-hand-foot-unisons", "drum-syncopated-kick"), 75));
        result.add(node("drum-hand-foot-unisons", "Juntar mãos e pés com precisão",
                "Uníssonos entre Mãos e Pés", "Coordenação", ids("drum-limb-independence"),
                ids("drum-linear-groove"), 80));
        result.add(node("drum-syncopated-kick", "Deslocar o bumbo sem perder a caixa",
                "Bumbo Sincopado", "Coordenação", ids("drum-kick-variations", "drum-limb-independence"),
                ids("drum-ghost-notes"), 85));
        result.add(node("drum-linear-groove", "Tocar grooves sem sobrepor as peças",
                "Groove Linear", "Coordenação", ids("drum-hand-foot-unisons", "drum-sixteenth-groove"),
                ids("drum-fill-orchestration"), 85));
        result.add(node("drum-ghost-notes", "Colocar notas leves entre os ataques fortes",
                "Ghost Notes", "Coordenação", ids("drum-syncopated-kick", "drum-accents"),
                ids("drum-dynamics"), 85));
        result.add(node("drum-ride-coordination", "Levar o groove para o prato de condução",
                "Condução no Ride", "Coordenação", ids("drum-open-hihat", "drum-limb-independence"),
                ids("drum-play-along"), 95));

        // Rudimentos úteis antes de listas intermináveis.
        result.add(node("drum-single-stroke", "Alternar as mãos com som igual", "Single Stroke Roll",
                "Rudimentos", ids("drum-grip", "pulse"), ids("drum-double-stroke", "drum-accents"), 100));
        result.add(node("drum-double-stroke", "Fazer dois golpes controlados por mão", "Double Stroke Roll",
                "Rudimentos", ids("drum-rebound", "drum-single-stroke"), ids("drum-paradiddle"), 90));
        result.add(node("drum-paradiddle", "Combinar golpes simples e duplos", "Single Paradiddle",
                "Rudimentos", ids("drum-double-stroke", "drum-accents"),
                ids("drum-fill-orchestration"), 90));
        result.add(node("drum-accents", "Destacar notas sem endurecer o movimento", "Acentos",
                "Rudimentos", ids("drum-rebound", "subdivisions"), ids("drum-flams", "drum-ghost-notes"), 90));
        result.add(node("drum-flams", "Transformar dois golpes em um ataque largo", "Flam",
                "Rudimentos", ids("drum-accents"), ids("drum-fill-orchestration"), 80));

        // Viradas: entrar e voltar ao groove é mais importante que tocar muitas notas.
        result.add(node("drum-one-beat-fill", "Fazer uma virada curta e voltar no tempo",
                "Virada de Um Tempo", "Viradas", ids("drum-groove-consistency", "drum-single-stroke"),
                ids("drum-tom-movement", "drum-fill-timing"), 75));
        result.add(node("drum-tom-movement", "Mover a virada entre caixa e tons", "Movimentação pelo Kit",
                "Viradas", ids("drum-one-beat-fill", "drum-kit-map"),
                ids("drum-sixteenth-fill"), 75));
        result.add(node("drum-sixteenth-fill", "Preencher um compasso com semicolcheias",
                "Virada em Semicolcheias", "Viradas", ids("drum-tom-movement", "subdivisions"),
                ids("drum-fill-orchestration"), 85));
        result.add(node("drum-fill-timing", "Terminar a virada exatamente no primeiro tempo",
                "Entrada e Saída de Viradas", "Viradas", ids("drum-one-beat-fill", "meter"),
                ids("drum-fill-orchestration", "drum-play-along"), 85));
        result.add(node("drum-fill-orchestration", "Criar viradas usando sons diferentes do kit",
                "Orquestração de Viradas", "Viradas",
                ids("drum-sixteenth-fill", "drum-fill-timing", "drum-paradiddle"),
                ids("drum-song-form"), 90));

        // Tocar música: o destino da árvore.
        result.add(node("drum-dynamics", "Fazer o groove crescer sem acelerar", "Dinâmica na Bateria",
                "Musicalidade", ids("drum-ghost-notes", "dynamics"), ids("drum-play-along"), 90));
        result.add(node("drum-song-form", "Saber onde verso, refrão e ponte começam",
                "Forma Musical para Bateristas", "Musicalidade",
                ids("drum-fill-orchestration", "song-sections"), ids("drum-play-along"), null));
        result.add(node("drum-play-along", "Tocar uma música inteira sem se perder",
                "Play-along", "Musicalidade",
                ids("drum-song-form", "drum-dynamics", "drum-ride-coordination"),
                ids("performance"), 100));

        return result.stream().map(CurriculumTaxonomy::apply).toList();
    }

    private static Skill node(String id, String friendly, String technical, String domain,
                              List<String> prerequisites, List<String> next, Integer targetBpm) {
        var state = prerequisites.isEmpty() ? SkillState.AVAILABLE : SkillState.LOCKED;
        var exerciseId = exerciseId(id);
        return new Skill(id, friendly, technical, domain,
                description(technical, domain), state, 0, 0, null, targetBpm, DRUMS,
                prerequisites, List.of("drums-" + id),
                exerciseId == null ? List.of() : List.of(exerciseId), List.of(), next);
    }

    private static String description(String technical, String domain) {
        return "Aprenda " + technical + " em camadas curtas. No ramo de " + domain
                + ", o objetivo é manter o pulso, tocar relaxado e sempre voltar ao groove.";
    }

    private static String exerciseId(String skillId) {
        return switch (skillId) {
            case "drum-kit-map" -> "drum-ex-kit";
            case "drum-rock-groove" -> "drum-ex-rock";
            case "drum-groove-consistency" -> "drum-ex-groove";
            case "drum-single-stroke" -> "drum-ex-single";
            case "drum-kick-variations" -> "drum-ex-kick";
            case "drum-one-beat-fill" -> "drum-ex-fill-one";
            case "drum-fill-timing" -> "drum-ex-fill-return";
            case "drum-play-along" -> "drum-ex-playalong";
            default -> null;
        };
    }

    private static List<String> ids(String... values) {
        return Arrays.asList(values);
    }
}
