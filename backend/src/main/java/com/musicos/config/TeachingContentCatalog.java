package com.musicos.config;

import com.musicos.domain.DifficultyDemand;
import com.musicos.domain.Assessment;
import com.musicos.domain.Exercise;
import com.musicos.domain.ExerciseVariation;
import com.musicos.domain.InstrumentId;
import com.musicos.domain.LearningStage;
import java.util.List;
import java.util.stream.Stream;

/** Conteúdo editorial usado por Missions; não contém progresso nem evidência simulada. */
public final class TeachingContentCatalog {
    public record Unit(
            String id,
            InstrumentId instrument,
            LearningStage stage,
            String title,
            String competencyId,
            String lessonId,
            List<String> exerciseIds,
            int estimatedMinutes,
            String objective,
            String context,
            String motivation,
            String completionCriteria,
            String expectedEvidence,
            String musicalApplication,
            String assessmentTitle,
            String assessmentPurpose,
            String assessmentInstructions,
            String assessmentConditions,
            String allowedSupport,
            String inconclusiveRule,
            List<String> criterionKeys,
            Assessment.Type assessmentType,
            List<String> repertoireIds) {
    }

    private TeachingContentCatalog() {
    }

    public static List<Exercise> exercises() {
        var core = List.of(
                exercise("deep-guitar-picking-cycle", "Ciclo de palheta em duas cordas",
                        "Alternate Picking", InstrumentId.GUITAR, "alternate-picking", 72, 112, 8,
                        "Fixe a sequência ↓↑ ao cruzar da segunda para a primeira corda sem aumentar o movimento.",
                        "Executar oito notas alternadas em duas cordas, preservando direção, pulso e relaxamento.",
                        "Metrônomo em 72 BPM, colcheias, cordas B e E abafadas; três ciclos com pausa entre eles.",
                        "Três ciclos consecutivos sem repetir direção, perder o pulso ou tensionar ombro e punho.",
                        List.of("Abafe as cordas e confirme lentamente ↓↑↓↑.",
                                "Toque quatro ataques na corda B e quatro na E sem reiniciar a direção.",
                                "Grave três ciclos e escute apenas regularidade e ruído de troca."),
                        List.of(new ExerciseVariation("Recuperação", "Uma corda por vez, a 60 BPM.", -12, 4),
                                new ExerciseVariation("Aplicação", "Use as notas 5–7 em B e 5–7 em E.", 0, 5),
                                new ExerciseVariation("Transferência", "Comece com palhetada para cima.", 4, 4))),
                exercise("deep-guitar-picking-riff", "Riff de dois compassos sem reiniciar a mão",
                        "Alternate Picking", InstrumentId.GUITAR, "alternate-picking", 68, 104, 10,
                        "Aplique palhetada alternada a um riff curto com pausas e mudanças de corda.",
                        "Tocar um riff completo mantendo o ciclo da palheta inclusive depois das pausas.",
                        "Riff de dois compassos, sem distorção, metrônomo em 68 BPM; a gravação começa antes da contagem.",
                        "Duas tomadas completas com ataques regulares e retorno correto após cada pausa.",
                        List.of("Fale 'baixo-cima' durante uma execução sem notas.",
                                "Toque o riff em células de quatro ataques e conecte sem mudar a digitação.",
                                "Faça uma tomada completa; marque o primeiro ponto em que o ciclo se perdeu."),
                        List.of(new ExerciseVariation("Mapa", "Anote a direção do primeiro ataque de cada célula.", -8, 4),
                                new ExerciseVariation("Pulso", "Toque somente a primeira nota de cada tempo.", -4, 4),
                                new ExerciseVariation("Riff", "Execute a forma completa sem recomeçar após erros.", 0, 6))),
                exercise("deep-acoustic-chord-clarity", "Diagnóstico corda por corda: Em e G",
                        "Acordes Abertos", InstrumentId.ACOUSTIC, "open-chords", 60, 76, 8,
                        "Montar Em e G identificando exatamente qual corda precisa de ajuste.",
                        "Produzir os acordes Em e G com todas as cordas previstas audíveis e sem pressão excessiva.",
                        "Sem metrônomo; toque cada corda da sexta para a primeira, ajuste uma variável por tentativa.",
                        "Duas montagens seguidas de cada acorde com todas as cordas previstas soando claramente.",
                        List.of("Monte Em e solte o polegar da parte superior do braço.",
                                "Toque uma corda por vez e nomeie a causa do primeiro ruído encontrado.",
                                "Repita com G e compare a distância dos dedos até o próximo acorde."),
                        List.of(new ExerciseVariation("Pressão mínima", "Reduza a força até a nota falhar e volte só o necessário.", 0, 3),
                                new ExerciseVariation("Olhos fechados", "Monte Em pelo tato e confira corda por corda.", 0, 4),
                                new ExerciseVariation("Preparação", "Alterne as formas sem tocar, observando dedos-guia.", 0, 4))),
                exercise("deep-acoustic-chord-transition", "Troca Em–G dentro de quatro tempos",
                        "Acordes Abertos", InstrumentId.ACOUSTIC, "open-chords", 56, 80, 10,
                        "Trocar entre Em e G sem interromper o pulso e sem sacrificar clareza.",
                        "Executar oito compassos alternando Em e G, chegando à nova forma no primeiro tempo.",
                        "Metrônomo em 56 BPM; um ataque no tempo 1 e três tempos de preparação; depois quatro ataques.",
                        "Oito trocas contínuas, com chegada no tempo 1 e ao menos cinco cordas claras por acorde.",
                        List.of("Faça quatro trocas silenciosas e observe o dedo que chega por último.",
                                "Toque apenas no tempo 1 e use 2–3–4 para preparar a próxima forma.",
                                "Toque semínimas por oito compassos e continue mesmo após uma corda abafada."),
                        List.of(new ExerciseVariation("Sem som", "Trocas silenciosas com contagem em voz alta.", -8, 3),
                                new ExerciseVariation("Um ataque", "Acorde apenas no tempo 1.", -4, 4),
                                new ExerciseVariation("Acompanhamento", "Quatro ataques por compasso.", 0, 5))),
                exercise("deep-keys-five-finger-balance", "Cinco dedos com peso uniforme",
                        "Posição de Cinco Dedos", InstrumentId.KEYS, "five-finger-position", 60, 88, 8,
                        "Tocar C–D–E–F–G e voltar sem colapsar dedos nem mudar o volume entre notas.",
                        "Executar a posição de cinco dedos com articulação e intensidade uniformes.",
                        "Mão direita em C central, sem pedal, semínimas a 60 BPM; punho neutro e ombro solto.",
                        "Três subidas e descidas contínuas sem nota perdida, acento involuntário ou tensão visível.",
                        List.of("Apoie os cinco dedos e confirme os números 1–2–3–4–5.",
                                "Toque uma nota por pulso observando se o punho acompanha lateralmente.",
                                "Grave três voltas e compare o volume do polegar com o do dedo mínimo."),
                        List.of(new ExerciseVariation("Preparação", "Toque 1–2–3 e volte, sem os dedos 4 e 5.", -8, 3),
                                new ExerciseVariation("Controle", "Faça a descida começando no dedo 5.", 0, 4),
                                new ExerciseVariation("Transferência", "Repita começando em G.", 4, 4))),
                exercise("deep-keys-five-finger-phrase", "Pergunta e resposta em cinco notas",
                        "Posição de Cinco Dedos", InstrumentId.KEYS, "five-finger-position", 64, 92, 10,
                        "Transformar a posição fixa em duas frases com direção e respiração.",
                        "Criar e repetir uma pergunta de dois compassos e uma resposta que termine em C.",
                        "Mão direita, notas C–D–E–F–G, 4/4 a 64 BPM, uma pausa obrigatória entre as frases.",
                        "Repetir a forma duas vezes preservando ritmo, pausa e nota final.",
                        List.of("Escolha um motivo de quatro notas e toque como pergunta.",
                                "Deixe um tempo de silêncio antes de responder.",
                                "Repita a forma inteira sem alterar o ritmo da segunda vez."),
                        List.of(new ExerciseVariation("Eco", "Repita exatamente a mesma frase duas vezes.", -4, 4),
                                new ExerciseVariation("Resposta", "Mude somente a última nota para C.", 0, 5),
                                new ExerciseVariation("Outra mão", "Transfira a forma para a mão esquerda.", 0, 5))),
                exercise("deep-drums-rock-layers", "Monte o rock beat em três camadas",
                        "Rock Beat em Colcheias", InstrumentId.DRUMS, "drum-rock-groove", 64, 92, 9,
                        "Construir o groove adicionando chimbal, caixa e bumbo sem deslocar o pulso.",
                        "Tocar quatro compassos de rock beat com chimbal em colcheias, caixa em 2 e 4 e bumbo em 1 e 3.",
                        "Contagem em voz alta, 64 BPM; cada camada entra somente depois de um compasso estável.",
                        "Quatro compassos completos com caixa em 2 e 4 e distância regular entre as colcheias.",
                        List.of("Toque apenas o chimbal e conte 1-e-2-e-3-e-4-e.",
                                "Acrescente caixa em 2 e 4 sem aumentar o volume do chimbal.",
                                "Acrescente bumbo em 1 e 3 e grave quatro compassos sem virada."),
                        List.of(new ExerciseVariation("Condução", "Somente chimbal e contagem.", -8, 3),
                                new ExerciseVariation("Backbeat", "Chimbal e caixa em 2 e 4.", -4, 4),
                                new ExerciseVariation("Groove", "Kit completo por quatro compassos.", 0, 5))),
                exercise("deep-drums-rock-continuity", "Oito compassos com recuperação no tempo",
                        "Rock Beat em Colcheias", InstrumentId.DRUMS, "drum-rock-groove", 68, 96, 10,
                        "Sustentar o rock beat e recuperar um ataque perdido sem parar o compasso.",
                        "Tocar oito compassos mantendo forma, pulso e continuidade mesmo após um erro isolado.",
                        "68 BPM, groove fixo, sem viradas; gravação contínua com contagem apenas no primeiro compasso.",
                        "Oito compassos sem reinício e último tempo alinhado à grade; erros isolados não interrompem a forma.",
                        List.of("Defina o groove e toque dois compassos com contagem.",
                                "Provoque a retirada de um bumbo e mantenha chimbal e caixa.",
                                "Grave oito compassos e compare o primeiro e o último com o clique."),
                        List.of(new ExerciseVariation("Âncora", "Mantenha apenas chimbal se perder outra camada.", -8, 4),
                                new ExerciseVariation("Continuidade", "Retire um bumbo no compasso 4 sem parar.", 0, 5),
                                new ExerciseVariation("Forma", "Faça duas séries de oito compassos.", 4, 6))));
        return Stream.concat(
                Stream.concat(core.stream(), FirstJourneyCatalog.exercises().stream()),
                expandedDefinitions().map(EditorialMissionDefinition::exercise)).toList();
    }

    public static List<Unit> units() {
        var core = List.of(
                unit("mission-guitar-alternate-picking", InstrumentId.GUITAR, LearningStage.BEGINNER,
                        "Cruzar cordas sem perder a palhetada", "alternate-picking", "lesson-alternate-picking",
                        List.of("deep-guitar-picking-cycle", "deep-guitar-picking-riff"), 34,
                        "Tocar um riff de duas cordas mantendo o ciclo alternado e o pulso.",
                        "A palhetada será construída primeiro como movimento previsível e depois aplicada a um riff.",
                        "Uma direção estável reduz correções tardias e libera atenção para fraseado e som.",
                        "Duas tomadas completas do riff sem reiniciar a mão após pausas ou trocas de corda.",
                        "Gravação do riff e assessment de direção, pulso e tensão.",
                        "Aplicar o mesmo ciclo a um trecho curto de repertório.",
                        "Riff alternado sob observação", "Confirmar direção, pulso e autonomia no riff completo."),
                unit("mission-acoustic-open-chords", InstrumentId.ACOUSTIC, LearningStage.BEGINNER,
                        "Fazer Em e G soarem e trocarem no tempo", "open-chords", "lesson-open-chords",
                        List.of("deep-acoustic-chord-clarity", "deep-acoustic-chord-transition"), 32,
                        "Montar Em e G com clareza e alterná-los sem interromper o pulso.",
                        "Primeiro cada corda será diagnosticada; depois a troca entrará em uma forma musical curta.",
                        "Acompanhamento confiável começa com formas claras e continuidade, não com muitas cifras.",
                        "Oito compassos alternando Em e G, com chegada no tempo 1 e sem reiniciar após falhas.",
                        "Gravação contínua e assessment de clareza, chegada e pulso.",
                        "Acompanhar uma canção de dois acordes em semínimas.",
                        "Acompanhamento de dois acordes", "Confirmar clareza e continuidade em oito compassos."),
                unit("mission-keys-five-finger", InstrumentId.KEYS, LearningStage.BEGINNER,
                        "Transformar cinco notas em uma frase", "five-finger-position", "lesson-five-finger-position",
                        List.of("deep-keys-five-finger-balance", "deep-keys-five-finger-phrase"), 31,
                        "Tocar cinco notas com equilíbrio e usá-las em uma pergunta e resposta.",
                        "A posição será estabilizada antes de virar frase, com pausa e direção musical.",
                        "Controle de peso e dedos serve à expressão; a posição fixa não é o resultado final.",
                        "Repetir duas vezes uma pergunta e resposta preservando ritmo, pausa e chegada em C.",
                        "Gravação das duas frases e assessment de equilíbrio, continuidade e forma.",
                        "Criar uma pequena introdução melódica com as mesmas cinco notas.",
                        "Frase em posição de cinco dedos", "Confirmar equilíbrio e repetição consciente da forma."),
                unit("mission-drums-rock-beat", InstrumentId.DRUMS, LearningStage.BEGINNER,
                        "Sustentar seu primeiro rock beat", "drum-rock-groove", "lesson-drum-rock-groove",
                        List.of("deep-drums-rock-layers", "deep-drums-rock-continuity"), 33,
                        "Tocar oito compassos de rock beat mantendo backbeat, colcheias e continuidade.",
                        "O groove será montado em camadas e depois testado sem interrupções.",
                        "A função do baterista é sustentar a forma; recuperar-se no tempo vale mais que recomeçar.",
                        "Oito compassos contínuos com caixa em 2 e 4 e último compasso alinhado ao clique.",
                        "Gravação contínua e assessment de pulso, backbeat e recuperação.",
                        "Acompanhar uma seção de música sem inserir viradas ainda.",
                        "Rock beat contínuo", "Confirmar pulso, backbeat e recuperação durante oito compassos."));
        return Stream.concat(
                Stream.concat(core.stream(), FirstJourneyCatalog.units().stream()),
                expandedDefinitions().map(EditorialMissionDefinition::unit)).toList();
    }

    private static Stream<EditorialMissionDefinition> expandedDefinitions() {
        return Stream.of(
                        GuitarEditorialCatalog.definitions(),
                        AcousticEditorialCatalog.definitions(),
                        KeysEditorialCatalog.definitions(),
                        DrumsEditorialCatalog.definitions())
                .flatMap(List::stream);
    }

    private static Unit unit(String id, InstrumentId instrument, LearningStage stage, String title,
                             String competencyId, String lessonId, List<String> exerciseIds, int minutes,
                             String objective, String context, String motivation, String completion,
                             String evidence, String application, String assessmentTitle, String assessmentPurpose) {
        return new Unit(id, instrument, stage, title, competencyId, lessonId, exerciseIds, minutes,
                objective, context, motivation, completion, evidence, application, assessmentTitle,
                assessmentPurpose, "Faça uma tomada completa sem reiniciar e depois descreva um ajuste.",
                "Use o BPM e as condições indicadas na missão; grave áudio quando disponível.",
                "Contagem inicial, metrônomo e uma tentativa de preparação.",
                "Se a gravação ou a observação não permitir concluir, registrar como inconclusivo e coletar nova tentativa.",
                coreCriteria(competencyId), coreAssessmentType(competencyId),
                repertoireFor(instrument, competencyId));
    }

    private static List<String> coreCriteria(String competencyId) {
        return switch (competencyId) {
            case "alternate-picking" -> List.of("direcao", "pulso", "tensao");
            case "open-chords" -> List.of("clareza", "chegada-no-tempo", "pulso");
            case "five-finger-position" -> List.of("equilibrio", "forma", "continuidade");
            case "drum-rock-groove" -> List.of("pulso", "backbeat", "recuperacao");
            default -> List.of("controle", "continuidade", "aplicacao-musical");
        };
    }

    private static Assessment.Type coreAssessmentType(String competencyId) {
        return switch (competencyId) {
            case "alternate-picking", "open-chords", "five-finger-position" -> Assessment.Type.APPLICATION;
            default -> Assessment.Type.PERFORMANCE;
        };
    }

    private static List<String> repertoireFor(InstrumentId instrument, String competencyId) {
        return switch (instrument) {
            case GUITAR -> List.of("seven-nation-army-guitar", "come-as-you-are-guitar");
            case ACOUSTIC -> List.of("horse-no-name-acoustic", "knockin-heavens-door-acoustic");
            case KEYS -> List.of("ode-to-joy-keys", "seven-nation-army-keys");
            case DRUMS -> List.of("seven-nation-army-drums", "billie-jean-drums");
        };
    }

    private static Exercise exercise(String id, String name, String technique, InstrumentId instrument,
                                     String competencyId, int currentBpm, int targetBpm, int minutes,
                                     String description, String objective, String conditions, String criteria,
                                     List<String> instructions, List<ExerciseVariation> variations) {
        var exercise = new Exercise(id, name, technique, instrument, targetBpm, currentBpm, minutes,
                description, competencyId, 2, Math.max(40, currentBpm - 12), 4, 85, 3,
                instructions, variations).withLearningResources("guided", LearningStage.BEGINNER,
                technique + " " + instrument.value() + " demonstração aula",
                "Material pedagógico da Mission", null,
                "Use primeiro a explicação e a demonstração da Mission.",
                technique + " " + instrument.value() + " backing track lento");
        exercise.configurePedagogicalDefinition(objective, conditions, criteria,
                new DifficultyDemand(2, 2, 2, 1, 1, 2, 1, 0, 2, 1, 1, 2, 1),
                List.of(competencyId));
        return exercise;
    }
}
