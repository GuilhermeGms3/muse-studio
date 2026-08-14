package com.musicos.config;

import com.musicos.domain.*;
import java.util.List;

final class LearningCatalog {
    private LearningCatalog() {
    }

    static LibraryContent lesson(Skill skill, String id) {
        var editorial = editorialLesson(skill, id);
        if (editorial != null) return editorial;
        var category = skill.getDomain();
        var technical = skill.getTechnicalName();
        var objectives = List.of(
                "Explicar " + technical + " com palavras simples.",
                "Reconhecer o conceito ao ouvir ou observar um exemplo.",
                "Aplicar a habilidade em uma situação musical curta.");
        var body = List.of(
                skill.getDescription(),
                "Aprenda primeiro o som e o movimento. O nome técnico serve para organizar o que você já ouviu.",
                "Pratique devagar, compare cada tentativa com a referência e aumente a dificuldade somente com controle.");
        var examples = examples(skill);
        var mistakes = mistakes(skill);
        var steps = List.of(
                new LessonStep("1. Ouça antes de tocar",
                        "Escute o exemplo e tente cantar ou marcar o pulso. Isso cria uma referência interna.",
                        examples.get(0), notation(skill), null, audio(skill)),
                new LessonStep("2. Isole o menor movimento",
                        "Trabalhe uma célula curta. Pare se tensão, ruído ou perda de pulso aparecerem.",
                        examples.get(Math.min(1, examples.size() - 1)), notation(skill), tablature(skill), audio(skill)),
                new LessonStep("3. Leve para a música",
                        "Use a habilidade em dois compassos, grave e compare com a referência.",
                        "Crie uma frase curta em C ou Am e repita em três velocidades.",
                        notation(skill), tablature(skill), audio(skill)));
        return new LibraryContent(id, skill.getFriendlyTitle(), technical, category,
                "Uma aula progressiva para entender, ouvir e aplicar " + technical + ".",
                skill.getId(), level(skill), 14, diagramType(skill), diagramData(skill), tablature(skill),
                objectives, body, examples, mistakes, steps, skill.getNextSkills().stream()
                        .map(next -> "lesson-" + next).toList());
    }

    static boolean isEditorial(String skillId) {
        return List.of("pulse", "subdivisions", "notes", "major-scale", "open-chords",
                "alternate-picking", "harmonic-field", "five-finger-position").contains(skillId)
                || skillId.startsWith("drum-");
    }

    private static LibraryContent editorialLesson(Skill skill, String id) {
        if (skill.getInstruments().contains(InstrumentId.DRUMS)
                && skill.getId().startsWith("drum-")) {
            return drumLesson(skill, id);
        }
        return switch (skill.getId()) {
            case "pulse" -> editorial(skill, id,
                    "O pulso é a caminhada constante por baixo da música. Antes de tocar notas, você precisa conseguir senti-lo sem correr ou frear.",
                    List.of("Marcar quatro tempos sem acelerar.", "Continuar o pulso durante uma pausa.",
                            "Perceber quando a execução sai da grade."),
                    List.of("Bata o pé em 60 BPM e conte 1, 2, 3, 4.",
                            "Mantenha o pé e bata palmas apenas nos tempos 2 e 4."),
                    List.of(
                            new LessonStep("Encontre a caminhada",
                                    "Ouça quatro pulsos. Continue marcando por mais quatro depois que o som parar. O objetivo não é adivinhar; é conservar a mesma distância.",
                                    "Quatro tempos iguais em 60 BPM.", "4/4 | 1   2   3   4 |", null,
                                    "C5,C5,C5,C5"),
                            new LessonStep("Separe pulso e ritmo",
                                    "O pulso não precisa coincidir com cada nota. Mantenha o pé constante e use as mãos para fazer um desenho diferente.",
                                    "Pé em todos os tempos; palmas em 2 e 4.", "4/4 | -   X   -   X |", null,
                                    "C4,G4,C4,G4"),
                            new LessonStep("Atravesse o silêncio",
                                    "Marque dois compassos, fique um compasso em silêncio e volte. Grave para conferir se o retorno caiu na grade.",
                                    "Toque 8 pulsos, silencie 4, volte por 4.", "4/4 | X X X X | silêncio | X X X X |",
                                    null, "C5,C5,C5,C5")),
                    "rhythm", "1   2   3   4", null,
                    List.of("Acompanhar cada nota em vez do pulso.", "Acelerar durante o silêncio.",
                            "Bater mais forte quando fica inseguro."));
            case "subdivisions" -> editorial(skill, id,
                    "Subdividir é colocar pontos de referência entre os pulsos. Isso transforma velocidade em algo contável.",
                    List.of("Alternar semínimas e colcheias.", "Contar em voz alta sem perder o pulso.",
                            "Voltar à semínima no lugar certo."),
                    List.of("1 2 3 4", "1 e 2 e 3 e 4 e"),
                    List.of(
                            new LessonStep("Um som por pulso", "Comece com quatro ataques iguais e conte os números.",
                                    "1 2 3 4", "♩ ♩ ♩ ♩", null, "C5,C5,C5,C5"),
                            new LessonStep("Dois sons por pulso", "Mantenha os números no mesmo lugar e encaixe o 'e' exatamente no meio.",
                                    "1 e 2 e 3 e 4 e", "♫ ♫ ♫ ♫", null, "C5,D5,C5,D5"),
                            new LessonStep("Troque sem quebrar", "Faça um compasso de semínimas, um de colcheias e volte. O pé não muda.",
                                    "♩ → ♫ → ♩", "| ♩ ♩ ♩ ♩ | ♫ ♫ ♫ ♫ |", null, "C4,C4,D4,D4")),
                    "rhythm", "1 & 2 & 3 & 4 &", null,
                    List.of("Acelerar ao tocar mais notas.", "Parar de contar nas trocas.",
                            "Mover o pé duas vezes mais rápido."));
            case "notes" -> editorial(skill, id,
                    "A música ocidental usa doze nomes que se repetem em regiões graves e agudas. Aprenda primeiro a ouvir vizinhança e repetição.",
                    List.of("Nomear as sete notas naturais.", "Encontrar semitons vizinhos.",
                            "Reconhecer a mesma nota em outra oitava."),
                    List.of("C D E F G A B", "E–F e B–C são vizinhos naturais"),
                    List.of(
                            new LessonStep("Sete nomes naturais", "Fale e toque C, D, E, F, G, A, B. Depois volte. Não tente decorar todos os sustenidos ainda.",
                                    "C D E F G A B C", "C D E F G A B C", null, "C4,D4,E4,F4,G4,A4,B4,C5"),
                            new LessonStep("Os cinco espaços", "Entre quase todas as notas naturais existe uma nota com sustenido ou bemol. E–F e B–C já são vizinhas.",
                                    "C–C#–D; E–F", "C C# D D# E F F# G G# A A# B", null, "C4,C#4,D4,E4,F4"),
                            new LessonStep("A mesma identidade", "Toque C em duas regiões. A altura muda, mas a função e o nome continuam reconhecíveis.",
                                    "C3, C4 e C5", "C3 —— C4 —— C5", null, "C3,C4,C5")),
                    "keyboard", "0,2,4,5,7,9,11", null,
                    List.of("Confundir nota com posição.", "Aprender sustenidos sem as naturais.",
                            "Pensar que oitavas diferentes são notas diferentes."));
            case "major-scale" -> editorial(skill, id,
                    "A escala maior organiza distâncias, acordes e sensação de repouso. O mais importante é ouvir a tônica como casa.",
                    List.of("Construir a fórmula T–T–S–T–T–T–S.", "Cantar a chegada à tônica.",
                            "Criar uma frase curta sem apenas subir e descer."),
                    List.of("C D E F G A B C", "G A B C D E F# G"),
                    List.of(
                            new LessonStep("Ouça casa e caminho", "Toque C, percorra a escala e pare em B. Sinta a vontade de resolver em C antes de tocar a última nota.",
                                    "B pede C.", "1 2 3 4 5 6 7 8", null, "C4,D4,E4,F4,G4,A4,B4,C5"),
                            new LessonStep("Construa pelas distâncias", "Use dois tons, um semitom, três tons e um semitom. Em G, isso exige F#.",
                                    "G A B C D E F# G", "T T S T T T S",
                                    "e|----------------2-3-|\nB|----------0-1-3-----|\nG|----0-2-------------|", "G3,A3,B3,C4,D4,E4,F#4,G4"),
                            new LessonStep("Faça uma frase", "Escolha quatro notas, deixe espaço e termine na tônica. Grave uma pergunta e uma resposta.",
                                    "C–D–E ... G–E–D–C", "1 2 3 ... 5 3 2 1",
                                    "e|--5-7-8---8-7-5----|", "C4,D4,E4,G4,E4,D4,C4")),
                    "fretboard", "0,2,4,5,7,9,11",
                    "e|----------------5-7-8-|\nB|----------5-6-8-------|\nG|----4-5-7-------------|\nD|-5-7------------------|",
                    List.of("Decorar o desenho sem ouvir a tônica.", "Tratar velocidade como objetivo.",
                            "Começar e terminar frases sempre no mesmo lugar."));
            case "open-chords" -> editorial(skill, id,
                    "Acordes abertos combinam cordas presas e soltas. O objetivo inicial é produzir poucas formas limpas e trocar sem interromper o pulso.",
                    List.of("Montar Em, G, C e D com som limpo.", "Diagnosticar cordas abafadas.",
                            "Trocar dois acordes dentro de quatro tempos."),
                    List.of("Em → G", "C → D"),
                    List.of(
                            new LessonStep("Monte de fora para dentro", "Coloque os dedos, toque cada corda separadamente e ajuste somente a que não soa.",
                                    "Em: 0 2 2 0 0 0", null,
                                    "e|0|\nB|0|\nG|0|\nD|2|\nA|2|\nE|0|", "E3,B3,E4,G4,B4,E5"),
                            new LessonStep("Prepare a troca", "Observe qual dedo pode ficar perto da próxima forma. Faça a troca sem tocar, quatro vezes.",
                                    "Em → G em câmera lenta.", "Em | G | Em | G", null, "E3,G3"),
                            new LessonStep("Coloque no tempo", "Toque uma vez no primeiro tempo e use os outros três para preparar o acorde seguinte.",
                                    "Em / / / | G / / /", "| Em . . . | G . . . |", null, "E3,E3,E3,E3,G3,G3,G3,G3")),
                    "fretboard", "0,4,7",
                    "e|--0-----3--|\nB|--0-----3--|\nG|--0-----0--|\nD|--2-----0--|\nA|--2-----2--|\nE|--0-----3--|",
                    List.of("Apertar todas as cordas com força excessiva.", "Recomeçar o compasso após cada erro.",
                            "Praticar muitas formas antes de dominar duas trocas."));
            case "alternate-picking" -> editorial(skill, id,
                    "Palhetada alternada é uma sequência previsível de ataques para cima e para baixo. Economia de movimento vem antes da velocidade.",
                    List.of("Alternar sem repetir direção.", "Cruzar cordas sem tensão.",
                            "Subir 4 BPM somente após três repetições limpas."),
                    List.of("↓ ↑ ↓ ↑ em uma corda", "Quatro notas por corda"),
                    List.of(
                            new LessonStep("Fixe o ciclo", "Abafe as cordas e faça movimentos pequenos. A palheta atravessa a corda, não mergulha entre elas.",
                                    "↓ ↑ ↓ ↑", "x x x x", "e|--5-5-5-5--|", "E4,E4,E4,E4"),
                            new LessonStep("Troque uma corda", "Use quatro ataques na primeira corda e quatro na segunda. Observe a direção do primeiro ataque após a troca.",
                                    "↓↑↓↑ | ↓↑↓↑", "4/4 | ♫ ♫ ♫ ♫ |",
                                    "e|--5-7-8-7----------|\nB|----------8-6-5-6--|", "A4,B4,C5,B4,G4,F4,E4,F4"),
                            new LessonStep("Teste consistência", "Faça três voltas em um BPM confortável. Se uma volta falhar, reduza 6 BPM e recupere relaxamento.",
                                    "3 voltas limpas antes de subir.", "60 → 64 BPM",
                                    "e|----------------5-7-8-|\nB|----------5-6-8-------|", "C4,D4,E4,G4")),
                    "fretboard", "0,2,4,5,7,9,11",
                    "e|----------------5-7-8-|\nB|----------5-6-8-------|\nG|----4-5-7-------------|\nD|-5-7------------------|",
                    List.of("Enterrar a palheta entre as cordas.", "Aumentar BPM depois de uma única volta.",
                            "Tensionar ombro e punho para tocar mais forte."));
            case "harmonic-field" -> editorial(skill, id,
                    "O campo harmônico mostra quais acordes nascem de uma escala e por que alguns soam em repouso, movimento ou tensão.",
                    List.of("Construir as tríades de C maior.", "Ouvir funções de tônica, subdominante e dominante.",
                            "Reconhecer uma progressão I–V–vi–IV."),
                    List.of("C Dm Em F G Am B°", "C G Am F"),
                    List.of(
                            new LessonStep("Empilhe terças", "Pegue uma nota da escala, pule uma, pegue outra, pule outra e pegue a terceira. Em C: C–E–G.",
                                    "C E G = C maior", "1 3 5", null, "C4,E4,G4"),
                            new LessonStep("Repita em cada grau", "O mesmo processo produz maior, menor, menor, maior, maior, menor e diminuto.",
                                    "C Dm Em F G Am B°", "I ii iii IV V vi vii°", null,
                                    "C4,E4,G4;D4,F4,A4;E4,G4,B4"),
                            new LessonStep("Ouça a função", "Compare C, F e G. C repousa, F movimenta e G cria tensão que pede retorno.",
                                    "C → F → G → C", "I IV V I", null, "C4,E4,G4;F4,A4,C5;G4,B4,D5;C4,E4,G4")),
                    "keyboard", "0,4,7", null,
                    List.of("Decorar a sequência sem ouvir funções.", "Confundir grau com cifra.",
                            "Estudar todos os tons antes de entender C maior."));
            case "five-finger-position" -> editorial(skill, id,
                    "A posição de cinco dedos é um ponto de partida para equilibrar a mão e formar frases; não é uma forma rígida para apertar cinco teclas.",
                    List.of("Relacionar dedos 1–5 às notas C–G.", "Tocar com peso uniforme e punho livre.",
                            "Criar pergunta e resposta com pausa audível."),
                    List.of("C–D–E–F–G e volta", "C–D–E ... G–E–D–C"),
                    List.of(
                            new LessonStep("Prepare sem pressionar",
                                    "Apoie os cinco dedos sobre C–D–E–F–G. O punho fica neutro e cada dedo conserva sua curva antes do primeiro som.",
                                    "Dedos 1–2–3–4–5 sobre C–D–E–F–G.", "C D E F G", null,
                                    "C4,D4,E4,F4,G4"),
                            new LessonStep("Compare o peso",
                                    "Toque uma nota por pulso e escute se polegar e dedo mínimo saltam de volume. Ajuste o peso, não a velocidade.",
                                    "C–D–E–F–G | G–F–E–D–C", "♩ ♩ ♩ ♩ ♩", null,
                                    "C4,D4,E4,F4,G4,G4,F4,E4,D4,C4"),
                            new LessonStep("Dê forma às cinco notas",
                                    "Toque uma pergunta de dois compassos, deixe um tempo vazio e responda terminando em C. Repita sem alterar o ritmo.",
                                    "C–D–E–G ... G–E–D–C", "pergunta | pausa | resposta", null,
                                    "C4,D4,E4,G4,G4,E4,D4,C4")),
                    "keyboard", "0,2,4,5,7", null,
                    List.of("Levantar dedos que ainda não vão tocar.", "Acentuar o polegar sem intenção.",
                            "Subir e descer sem ouvir uma frase."));
            default -> null;
        };
    }

    private static LibraryContent drumLesson(Skill skill, String id) {
        var pattern = drumPattern(skill.getId());
        var focus = drumFocus(skill.getId());
        var steps = List.of(
                new LessonStep("1. Entenda a função",
                        focus + " Antes de tocar rápido, conte em voz alta e aponte onde cada ataque acontece.",
                        pattern.count(), pattern.count(), pattern.notation(), "C5,C5,C5,C5"),
                new LessonStep("2. Monte em camadas",
                        "Comece somente com a condução. Acrescente a caixa e, por último, o bumbo. "
                                + "Se o pulso mudar, retire a última camada e recupere o controle.",
                        "Condução → caixa → bumbo", pattern.count(), pattern.notation(), "C5,G4,C5,G4"),
                new LessonStep("3. Toque como música",
                        "Faça três compassos estáveis e use o quarto para aplicar a habilidade. "
                                + "Volte ao primeiro tempo sem parar, grave e avalie apenas uma coisa por vez.",
                        "3 compassos de groove + 1 compasso de aplicação",
                        "4/4 | groove | groove | groove | aplicação |",
                        pattern.notation(), "C4,C4,G4,C4"));
        return editorial(skill, id,
                skill.getDescription() + " Use o notebook como guia: leia uma etapa, toque por alguns minutos "
                        + "e só então avance.",
                List.of("Tocar o padrão devagar sem tensão.",
                        "Manter a contagem enquanto mãos e pés trabalham.",
                        "Voltar ao groove no primeiro tempo."),
                List.of(pattern.count(), pattern.notation()),
                steps, "rhythm", pattern.notation(), pattern.notation(),
                List.of("Acelerar quando entra o bumbo ou a virada.",
                        "Parar e recomeçar depois de qualquer erro.",
                        "Tocar forte para compensar falta de controle."));
    }

    private static DrumPattern drumPattern(String skillId) {
        return switch (skillId) {
            case "drum-kit-map" -> new DrumPattern(
                    "Chimbal, caixa, bumbo, tons e pratos",
                    "HH|x-------|\nSD|--o-----|\nBD|----o---|\nT1|------o-|");
            case "drum-single-stroke", "drum-double-stroke", "drum-paradiddle", "drum-accents",
                    "drum-flams", "drum-rebound", "drum-grip" -> new DrumPattern(
                    "1 e 2 e 3 e 4 e",
                    "SD|R-L-R-L-R-L-R-L-|");
            case "drum-one-beat-fill", "drum-tom-movement", "drum-sixteenth-fill",
                    "drum-fill-timing", "drum-fill-orchestration" -> new DrumPattern(
                    "1 e 2 e 3 e 4 e",
                    "HH|x-x-x-x---------|\nSD|----o-----R-L---|\nT1|-------------R--|\nFT|--------------L-|");
            case "drum-open-hihat", "drum-hi-hat-foot" -> new DrumPattern(
                    "1 e 2 e 3 e 4 e",
                    "HH|x-x-x-x-O-x-x-x-|\nSD|----o-------o---|\nBD|o-------o-------|");
            case "drum-shuffle" -> new DrumPattern(
                    "1 a 2 a 3 a 4 a",
                    "HH|x--x--x--x--|\nSD|---o-----o--|\nBD|o-----o-----|");
            default -> new DrumPattern(
                    "1 e 2 e 3 e 4 e",
                    "HH|x-x-x-x-x-x-x-x-|\nSD|----o-------o---|\nBD|o-------o-o-----|");
        };
    }

    private static String drumFocus(String skillId) {
        return switch (skillId) {
            case "drum-one-beat-fill", "drum-tom-movement", "drum-sixteenth-fill",
                    "drum-fill-timing", "drum-fill-orchestration" ->
                    "A virada só funciona quando termina no lugar certo.";
            case "drum-grip", "drum-rebound", "drum-single-stroke", "drum-double-stroke",
                    "drum-paradiddle", "drum-accents", "drum-flams" ->
                    "O movimento deve aproveitar o rebote, sem apertar a baqueta.";
            case "drum-kit-map", "drum-setup" ->
                    "Organize o kit para alcançar cada peça sem inclinar ou torcer o corpo.";
            default -> "O groove nasce da relação entre condução, caixa e bumbo.";
        };
    }

    private record DrumPattern(String count, String notation) {
    }

    private static LibraryContent editorial(Skill skill, String id, String summary,
                                            List<String> objectives, List<String> examples,
                                            List<LessonStep> steps, String diagramType, String diagramData,
                                            String tablature, List<String> mistakes) {
        return new LibraryContent(id, skill.getFriendlyTitle(), skill.getTechnicalName(), skill.getDomain(),
                summary, skill.getId(), skill.getStage().value(),
                16, diagramType, diagramData, tablature, objectives,
                steps.stream().map(LessonStep::getExplanation).toList(), examples, mistakes, steps,
                skill.getNextSkills().stream().map(next -> "lesson-" + next).toList());
    }

    static Exercise exercise(Skill skill, String id) {
        return activity(skill, skill.getInstruments().getFirst(), id, "execute", "Executar", 8);
    }

    static List<Exercise> activities(Skill skill) {
        var definitions = skill.getStage().order() <= 2
                ? List.of(new ActivityDefinition("understand", "Compreender", 5),
                new ActivityDefinition("imitate", "Imitar", 5),
                new ActivityDefinition("slow", "Executar lentamente", 7),
                new ActivityDefinition("context", "Aplicar em contexto", 8),
                new ActivityDefinition("song", "Aplicar em música", 10),
                new ActivityDefinition("record", "Gravar e avaliar", 8))
                : List.of(new ActivityDefinition("imitate", "Imitar uma referência", 6),
                new ActivityDefinition("context", "Aplicar em contexto", 9),
                new ActivityDefinition("song", "Aplicar em música", 12),
                new ActivityDefinition("recall", "Recordar sem ajuda", 8),
                new ActivityDefinition("record", "Gravar e avaliar", 10),
                new ActivityDefinition("transfer", "Transferir e criar", 12));
        var primary = skill.getInstruments().getFirst();
        return skill.getInstruments().stream().flatMap(instrument -> definitions.stream().map(definition -> {
            var instrumentSuffix = instrument == primary ? "" : "-" + instrument.value();
            return activity(skill, instrument,
                    "activity-" + skill.getId() + instrumentSuffix + "-" + definition.type(), definition.type(),
                    definition.label(), definition.minutes());
        })).toList();
    }

    private static Exercise activity(Skill skill, InstrumentId instrument, String id, String type,
                                     String label, int minutes) {
        var target = skill.getTargetBpm() == null ? targetFor(skill) : skill.getTargetBpm();
        var current = skill.getCurrentBpm() == null ? Math.max(40, target - 30) : skill.getCurrentBpm();
        var source = sourceFor(instrument, skill);
        return new Exercise(id, label + ": " + skill.getTechnicalName(), skill.getTechnicalName(),
                instrument, target, current, minutes, activityDescription(type, skill), skill.getId(),
                difficulty(skill), Math.max(30, current - 20), 4,
                Math.min(90, 75 + skill.getStage().order() * 3),
                3, instructions(type, skill),
                List.of(new ExerciseVariation("Controle", "Reduza 20 BPM e elimine tensão e ruídos.", -20, 4),
                        new ExerciseVariation("Aplicação", "Use a ideia em uma frase ou progressão musical.", 0, 5),
                        new ExerciseVariation("Desafio", "Mude tonalidade ou contexto sem perder o controle.", 4, 4)))
                .withLearningResources(type, skill.getStage(),
                        skill.getTechnicalName() + " " + instrumentLabel(instrument) + " aula tutorial",
                        source.title(), source.url(), source.note(),
                        skill.getTechnicalName() + " " + instrumentLabel(instrument) + " play along");
    }

    private static String activityDescription(String type, Skill skill) {
        return switch (type) {
            case "understand" -> "Entenda o som, o movimento e a função de " + skill.getTechnicalName() + ".";
            case "imitate" -> "Observe uma referência curta e reproduza sem acrescentar dificuldade.";
            case "slow" -> "Construa controle em velocidade confortável antes de buscar fluência.";
            case "context" -> "Use a habilidade em uma frase, groove ou progressão curta.";
            case "song" -> "Leve a habilidade para um trecho de repertório real e mantenha o pulso.";
            case "recall" -> "Execute sem consultar diagramas, cifras ou instruções.";
            case "record" -> "Grave uma tomada, escute e escolha um ajuste mensurável.";
            case "transfer" -> "Transfira a habilidade para outra tonalidade, textura ou arranjo.";
            default -> "Pratique em ciclos curtos de observação, execução e aplicação musical.";
        };
    }

    private static List<String> instructions(String type, Skill skill) {
        return switch (type) {
            case "understand" -> List.of("Leia a explicação e identifique o objetivo musical.",
                    "Assista à referência procurando um único detalhe.", "Explique com suas palavras antes de tocar.");
            case "imitate" -> List.of("Escolha dois compassos da referência.",
                    "Cante ou marque o ritmo antes de tocar.", "Imite três vezes sem acelerar.");
            case "slow" -> List.of("Ajuste o metrônomo no BPM mínimo.",
                    "Execute movimentos pequenos e relaxados.", "Suba somente após três repetições limpas.");
            case "context" -> List.of("Escolha uma frase, groove ou progressão de dois compassos.",
                    "Aplique " + skill.getTechnicalName() + " mantendo o pulso.", "Repita começando em pontos diferentes.");
            case "song" -> List.of("Escolha uma música que use esta habilidade.",
                    "Isole uma seção curta e pratique com o playback.", "Toque a seção inteira sem interromper.");
            case "recall" -> List.of("Feche a aula, cifra ou diagrama.",
                    "Reconstrua a ideia de memória.", "Confira a referência e corrija apenas o que divergiu.");
            case "record" -> List.of("Grave uma tomada completa sem reiniciar.",
                    "Avalie pulso, clareza e tensão.", "Registre um ajuste e grave uma segunda tomada.");
            case "transfer" -> List.of("Mude tonalidade, região ou orquestração.",
                    "Crie uma variação mantendo a função musical.", "Grave o resultado dentro de uma forma completa.");
            default -> List.of("Observe o movimento.", "Pratique com metrônomo.", "Aplique musicalmente.");
        };
    }

    private static Source sourceFor(InstrumentId instrument, Skill skill) {
        if (skill.getKind() == SkillKind.KNOWLEDGE || skill.getTrack() == LearningTrack.HARMONY
                || skill.getTrack() == LearningTrack.EAR || skill.getTrack() == LearningTrack.READING) {
            return new Source("Open Music Theory", "https://viva.pressbooks.pub/openmusictheory/",
                    "Livro aberto para consultar o conceito e ouvir exemplos antes da prática.");
        }
        return switch (instrument) {
            case GUITAR -> new Source("Método completo para guitarra, Op. 59",
                    "https://imslp.org/wiki/M%C3%A9thode_compl%C3%A8te_pour_la_guitare%2C_Op.59_%28Carcassi%2C_Matteo%29",
                    "Método em domínio público para técnica e leitura relacionadas.");
            case ACOUSTIC -> new Source("Wikibooks Guitar", "https://en.wikibooks.org/wiki/Guitar",
                    "Leitura aberta de apoio sobre técnica e acompanhamento.");
            case KEYS -> new Source("Piano Basics",
                    "https://openbooks.library.baylor.edu/pianobasics/front-matter/preface/",
                    "Livro didático aberto com fundamentos, leitura e prática progressiva.");
            case DRUMS -> new Source("Learn to Play Drums", "https://en.wikibooks.org/wiki/Learn_to_Play_Drums",
                    "Guia aberto para revisar montagem, técnica, leitura e grooves.");
        };
    }

    private static String instrumentLabel(InstrumentId instrument) {
        return switch (instrument) {
            case GUITAR -> "guitarra";
            case ACOUSTIC -> "violão";
            case KEYS -> "teclado";
            case DRUMS -> "bateria";
        };
    }

    private record ActivityDefinition(String type, String label, int minutes) {}
    private record Source(String title, String url, String note) {}

    private static List<String> examples(Skill skill) {
        return switch (skill.getDomain()) {
            case "Ritmo", "Fundamentos" -> List.of("Conte 1 e 2 e 3 e 4 e", "Acentue 2 e 4 sem acelerar");
            case "Escalas", "Improvisação" -> List.of("C D E F G A B C", "Crie uma frase que termine na tônica");
            case "Harmonia", "Teoria" -> List.of("C - Am - F - G", "Compare repouso em C com tensão em G");
            case "Ouvido" -> List.of("C4 seguido de G4", "Cante a resposta antes de escolher o nome");
            case "Leitura" -> List.of("4/4: semínima, duas colcheias, mínima", "Leia um compasso à frente");
            default -> List.of("Quatro repetições lentas e relaxadas", "Aplique em dois compassos de uma música");
        };
    }

    private static List<String> mistakes(Skill skill) {
        return switch (skill.getDomain()) {
            case "Ritmo", "Fundamentos" -> List.of(
                    "Acelerar nas partes fáceis.", "Contar apenas quando o trecho fica difícil.",
                    "Confundir tocar mais forte com tocar no tempo.");
            case "Escalas", "Improvisação" -> List.of(
                    "Decorar o desenho sem ouvir a tônica.", "Subir e descer a escala sem criar frases.",
                    "Tentar usar todas as notas ao mesmo tempo.");
            case "Harmonia", "Teoria" -> List.of(
                    "Memorizar fórmulas sem escutar a função.", "Pular os pré-requisitos.",
                    "Usar nomes técnicos antes de reconhecer o som.");
            case "Ouvido" -> List.of(
                    "Responder antes de cantar.", "Treinar opções demais de uma vez.",
                    "Repetir apenas os exemplos que já acerta.");
            default -> List.of(
                    "Aumentar o BPM com tensão.", "Repetir o erro sem reduzir o trecho.",
                    "Avaliar velocidade e ignorar limpeza.");
        };
    }

    private static String level(Skill skill) {
        return skill.getStage().value();
    }

    private static int difficulty(Skill skill) {
        return Math.min(5, 1 + skill.getStage().order());
    }

    private static int targetFor(Skill skill) {
        return switch (skill.getDomain()) {
            case "Ritmo", "Técnica", "Leitura" -> 100;
            default -> 80;
        };
    }

    private static String diagramType(Skill skill) {
        return switch (skill.getDomain()) {
            case "Escalas", "Improvisação", "Técnica", "Expressão" -> "fretboard";
            case "Harmonia", "Teoria" -> "keyboard";
            case "Ritmo", "Leitura" -> "rhythm";
            default -> null;
        };
    }

    private static String diagramData(Skill skill) {
        return switch (diagramType(skill) == null ? "" : diagramType(skill)) {
            case "fretboard" -> "0,2,4,5,7,9,11";
            case "keyboard" -> "0,4,7";
            case "rhythm" -> "1 & 2 & 3 & 4 &";
            default -> null;
        };
    }

    private static String tablature(Skill skill) {
        if (!skill.getInstruments().contains(InstrumentId.GUITAR)
                && !skill.getInstruments().contains(InstrumentId.ACOUSTIC)) return null;
        return switch (skill.getId()) {
            case "alternate-picking" -> "e|----------------5-7-8-|\nB|----------5-6-8-------|\nG|----4-5-7-------------|\nD|-5-7------------------|";
            case "bends" -> "e|----------------------|\nB|--8b10--8--5----------|\nG|-------------7--------|";
            case "major-scale" -> "e|----------------5-7-8-|\nB|----------5-6-8-------|\nG|----4-5-7-------------|\nD|-5-7------------------|";
            default -> "e|--5---7---8---7---5----|\nB|-------------------8--|";
        };
    }

    private static String notation(Skill skill) {
        return switch (skill.getDomain()) {
            case "Ritmo", "Leitura" -> "4/4 | ♩ ♪♪ ♩ ♩ |";
            case "Harmonia" -> "I - vi - IV - V";
            case "Escalas", "Improvisação" -> "1 2 3 4 5 6 7 8";
            default -> null;
        };
    }

    private static String audio(Skill skill) {
        return switch (skill.getDomain()) {
            case "Harmonia" -> "C4,E4,G4;A3,C4,E4;F3,A3,C4;G3,B3,D4";
            case "Ouvido" -> "C4,G4";
            case "Ritmo" -> "C5,C5,C5,C5";
            default -> "C4,D4,E4,G4";
        };
    }
}
