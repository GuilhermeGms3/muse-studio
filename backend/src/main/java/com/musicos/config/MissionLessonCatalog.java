package com.musicos.config;

import com.musicos.domain.Exercise;
import com.musicos.domain.InstrumentId;
import com.musicos.domain.LessonStep;
import com.musicos.domain.LibraryContent;
import com.musicos.domain.Skill;
import java.util.List;
import java.util.Locale;

/** Lessons alinhadas ao trabalho observável das Missions editoriais atuais. */
final class MissionLessonCatalog {
    private MissionLessonCatalog() {}

    static boolean teaches(String skillId) {
        return TeachingContentCatalog.units().stream().anyMatch(unit -> unit.competencyId().equals(skillId));
    }

    static LibraryContent lesson(Skill skill, String contentId) {
        var unit = TeachingContentCatalog.units().stream()
                .filter(candidate -> candidate.lessonId().equals(contentId))
                .findFirst().orElse(null);
        if (unit == null) return null;
        var exercise = TeachingContentCatalog.exercises().stream()
                .filter(candidate -> unit.exerciseIds().contains(candidate.getId()))
                .findFirst().orElseThrow(() -> new IllegalStateException("Exercise editorial ausente: " + unit.id()));
        return switch (unit.competencyId()) {
            case "guitar-tuning" -> tuning(skill, contentId, unit, exercise);
            case "muting-control" -> guitarSilence(skill, contentId, unit, exercise);
            case "lead-guitar-language" -> leadForm(skill, contentId, unit, exercise);
            case "quarter-strumming" -> bassAndChord(skill, contentId, unit, exercise);
            case "acoustic-arpeggio-patterns" -> thumbAndFingers(skill, contentId, unit, exercise);
            case "solo-acoustic-arrangement" -> soloArrangement(skill, contentId, unit, exercise);
            case "triads" -> triads(skill, contentId, unit, exercise);
            case "syncopation" -> syncopation(skill, contentId, unit, exercise);
            case "keys-reharmonization" -> reharmonization(skill, contentId, unit, exercise);
            case "pop-voicings" -> popVoicings(skill, contentId, unit, exercise);
            case "drum-kick-variations" -> kickVariation(skill, contentId, unit, exercise);
            case "drum-harmonic-landmarks" -> harmonicLandmarks(skill, contentId, unit, exercise);
            case "drum-groove-composition" -> grooveComposition(skill, contentId, unit, exercise);
            case "drum-odd-meter" -> oddMeter(skill, contentId, unit, exercise);
            case "drum-polyrhythm" -> polyrhythm(skill, contentId, unit, exercise);
            case "drum-style-adaptation" -> drumStyleTransfer(skill, contentId, unit, exercise);
            default -> exerciseAlignedLesson(skill, contentId, unit, exercise);
        };
    }

    private static LibraryContent tuning(Skill skill, String id, TeachingContentCatalog.Unit unit, Exercise ex) {
        return material(skill, id, unit, ex, "Ouvir batimentos para decidir a direção da afinação",
                "Duas alturas próximas criam uma pulsação: quanto mais lenta ela fica, mais as frequências se aproximam. O ouvido percebe aproximação, não cents medidos.",
                List.of(
                        step("Referência das seis cordas", "Memorize a ordem grave-agudo E–A–D–G–B–E. Ouça uma referência por vez e deixe a corda soar sem acorde.", "E2;A2;D3;G3;B3;E4", "E2 A2 D3 G3 B3 E4", "e|E4|\nB|B3|\nG|G3|\nD|D3|\nA|A2|\nE|E2|"),
                        step("Descobrir o lado antes de girar", "Baixe deliberadamente a corda, ouça a referência e suba em passos pequenos. Se os batimentos acelerarem, o ajuste foi para o lado errado; reverta sem ultrapassar.", "E2;F2;E2", "abaixo → aproxima → acima", null),
                        step("Aproximar e conferir de novo", "Faça microajustes, ataque novamente para renovar a sustentação e pare quando a pulsação estiver lenta. Ao terminar E–A–D–G–B–E, volte ao E grave: a tensão total pode ter mudado.", "E2;A2;D3;G3;B3;E4;E2", "1ª passagem + segunda conferência", null)));
    }

    private static LibraryContent guitarSilence(Skill skill, String id, TeachingContentCatalog.Unit unit, Exercise ex) {
        return material(skill, id, unit, ex, "Controlar o ciclo inteiro da nota, inclusive o silêncio",
                "Uma nota possui ataque, sustentação e encerramento. O ruído entre notas costuma nascer quando uma mão inicia a próxima ação antes de a outra terminar a anterior.",
                List.of(
                        step("Ataque, corpo e corte", "Toque uma nota e nomeie três instantes: a palheta inicia, a corda sustenta e as mãos encerram. Corte sem arrancar o dedo da casa.", "E3;R;E3;R", "nota — sustenta — x", "e|----------------|\nB|----------------|\nG|----------------|\nD|--2---x---2---x-|\nA|----------------|\nE|----------------|"),
                        step("Duas mãos, funções diferentes", "A esquerda alivia pressão sem abandonar a corda; a direita encosta nas cordas que não devem vibrar. Teste cada função isolada antes de combiná-las.", "E3;R;A3;R", "E–A–D com pausas iguais", "e|----------------|\nB|----------------|\nG|----------------|\nD|--------0-x-----|\nA|--0-x-----------|\nE|------------0-x-|"),
                        step("Ganho revela vibração simpática", "Compare som limpo e ganho moderado sem mudar a digitação. O ganho não cria o erro: torna audível a corda solta que continuou vibrando. Preserve a duração do riff abaixo.", "E3;R;A3;R;D3;R;E3", "2 compassos | nota ataque silêncio |", "e|----------------|\nB|----------------|\nG|----------------|\nD|------0-x-------|\nA|--0-x-----0-x---|\nE|------------0-x-|")));
    }

    private static LibraryContent leadForm(Skill skill, String id, TeachingContentCatalog.Unit unit, Exercise ex) {
        return material(skill, id, unit, ex, "Construir um arco de solo sobre A–A–B",
                "Forma transforma frases em discurso: A apresenta e varia um motivo; B muda densidade, registro ou alvo sem apagar a identidade criada.",
                List.of(
                        step("Motivo com chegada", "Use o motivo original A–C–D–E e termine no alvo C sobre Am. A pausa final faz parte do motivo.", "A3;C4;D4;E4;R;C4", "A (4 compassos): motivo + espaço", "e|----------------5---|\nB|--5-8-10-5--(pausa)-|"),
                        step("Segundo A: variar uma dimensão", "Repita o ritmo e altere somente a nota final ou o registro. Isso produz reconhecimento com movimento, em vez de uma frase nova sem relação.", "A3;C4;D4;G4;R;E4", "A': mesmo ritmo, novo alvo", "e|------------8-5-|\nB|--5-8-10--------|"),
                        step("B: contraste e arco", "Use o backing original Am | F | C | G em três ciclos. Nos quatro compassos B, aumente a densidade e alcance E5; depois reduza eventos para a chegada final.", "A2,C3,E3;F2,A2,C3;C3,E3,G3;G2,B2,D3;A2,C3,E3;F2,A2,C3;C3,E3,G3;G2,B2,D3;A2,C3,E3;F2,A2,C3;C3,E3,G3;G2,B2,D3", "12 compassos | A4 | A'4 | B4 |", "e|--5-8-10----12-15-17--15-12--8-5-|")));
    }

    private static LibraryContent bassAndChord(Skill skill, String id, TeachingContentCatalog.Unit unit, Exercise ex) {
        return material(skill, id, unit, ex, "Separar o chão do primeiro tempo e o corpo do acorde",
                "O baixo fundamental anuncia a harmonia no tempo 1; os ataques de acorde em 2–3–4 completam o compasso sem competir com ele.",
                List.of(
                        step("Antecipe a fundamental", "Antes do tempo 1, localize a corda grave de C, G, Am ou F. O polegar prepara a corda enquanto os dedos conservam a forma.", "C3;G2;A2;F2", "| baixo . . . |", "e|------------|\nB|------------|\nG|------------|\nD|--2---------|\nA|--3---------|\nE|------------|"),
                        step("Peso diferente para funções diferentes", "Toque o baixo sozinho no 1 e um acorde leve em 2–3–4. Compare duas versões: baixo encoberto e baixo audível sem ficar agressivo.", "C3;C4,E4,G4;C4,E4,G4;C4,E4,G4", "1: baixo | 2 3 4: acorde", "e|----0--0--0-|\nB|----1--1--1-|\nG|----0--0--0-|\nD|-------------|\nA|--3----------|\nE|-------------|"),
                        step("Ciclo original de estudo", "Aplique em C–G–Am–F a 56 BPM, dois compassos por acorde. Este é um acompanhamento original de estudo, não transcrição de gravação comercial.", "C3;C4,E4,G4;G2;B3,D4,G4;A2;A3,C4,E4;F2;A3,C4,F4", "8 compassos, baixo no 1", null)));
    }

    private static LibraryContent thumbAndFingers(Skill skill, String id, TeachingContentCatalog.Unit unit, Exercise ex) {
        return material(skill, id, unit, ex, "Fazer o polegar conduzir enquanto i–m–a respondem",
                "P toca baixos; i, m e a normalmente atendem G, B e e. Independência significa manter o baixo e deixar a voz superior sustentar entre trocas.",
                List.of(
                        step("Mapa P–i–m–a", "Em Am, toque P na quinta corda, i em G, m em B e a em e. Não retire um dedo cedo apenas para preparar o seguinte.", "A2;A3;C4;E4", "P i m a", "e|------0-|\nB|----1---|\nG|--2-----|\nD|--------|\nA|0-------|\nE|--------|"),
                        step("Baixo conduz, agudo responde", "Acentue discretamente P e faça a nota de a soar até o próximo baixo. Compare com a versão em que todos os dedos atacam com o mesmo peso.", "A2;A3;C4;E4;C4;A3", "P i m a m i", null),
                        step("Continuidade entre acordes", "Use Am–C–G–Em a 60 BPM e preserve P–i–m–a. A referência é uma adaptação editorial do gesto de arpejo; não reproduz a tablatura de House of the Rising Sun.", "A2;A3;C4;E4;C3;G3;C4;E4;G2;G3;B3;D4;E2;G3;B3;E4", "4 acordes, um padrão funcional", null)));
    }

    private static LibraryContent soloArrangement(Skill skill, String id, TeachingContentCatalog.Unit unit, Exercise ex) {
        return material(skill, id, unit, ex, "Organizar três vozes sem preencher todos os espaços",
                "Arranjo solo exige hierarquia: melodia no topo, baixo definindo função e harmonia seletiva apenas onde sustenta a condução.",
                List.of(
                        step("Camada 1: melodia Muse", "Aprenda a melodia original de oito compassos C–E–G–E | D–F–E–C | A–C–E–D | G–E–D–C. Fraseie antes de adicionar acordes.", "C4;E4;G4;E4;D4;F4;E4;C4;A3;C4;E4;D4;G3;E4;D4;C4", "8 compassos, melodia superior", "e|0-3-5-3|1-5-3-0|0-3-5-3|3-0---0|"),
                        step("Camada 2: baixo funcional", "Acrescente C, G, Am e F nos inícios de frase. Se a melodia atrasar ou sumir, retire o preenchimento: baixo e topo são a estrutura mínima.", "C3,C4;E4;G2,G4;E4;A2,D4;F2,F4;E4;C3,C4", "melodia + baixos C G A F", null),
                        step("Camada 3: preenchimento seletivo", "Inclua terça ou quinta em pontos longos, preserve notas comuns e reduza para duas vozes nas transições difíceis. Densidade constante apaga a melodia.", "C3,E3,C4;E4;G2,D3,G4;A2,E3,A4;F2,C3,F4;C3,E3,C4", "topo > baixo > preenchimento", null)));
    }

    private static LibraryContent triads(Skill skill, String id, TeachingContentCatalog.Unit unit, Exercise ex) {
        return material(skill, id, unit, ex, "Construir C, F e G e escutar cada voz",
                "Tríade é fundamental, terça e quinta empilhadas. A terça decide maior ou menor; equilíbrio permite ouvi-la sem apagar fundamental e quinta.",
                List.of(
                        step("Empilhe 1–3–5", "Em C use C–E–G; em F, F–A–C; em G, G–B–D. Toque cada voz separada e depois simultânea.", "C4;E4;G4;C4,E4,G4;F3,A3,C4;G3,B3,D4", "C E G | F A C | G B D", null),
                        step("A terça muda a qualidade", "Compare C–E–G com C–Eb–G mantendo fundamental e quinta. Cante E e depois Eb antes de tocar.", "C4,E4,G4;C4,Eb4,G4", "maior: 1 3 5 | menor: 1 b3 5", null),
                        step("Balanceie as vozes", "Toque C, F e G com a terça audível, mas não acentuada. Em seguida destaque deliberadamente cada voz para reconhecer qual desaparecia no primeiro ataque.", "C4,E4,G4;F3,A3,C4;G3,B3,D4", "C | F | G, sem pedal", null)));
    }

    private static LibraryContent syncopation(Skill skill, String id, TeachingContentCatalog.Unit unit, Exercise ex) {
        return material(skill, id, unit, ex, "Manter o baixo no pulso enquanto o acorde cai entre pulsos",
                "Contratempo é uma posição na subdivisão. A mão esquerda ancora semínimas; a direita entra no 'e' de 2 e no 'e' de 4.",
                List.of(
                        step("Ouça chão e deslocamento", "Conte 1-e-2-e-3-e-4-e. Toque o baixo nos números e palmas apenas no e de 2 e e de 4.", "C3;R;C3;C4,E4,G4;C3;R;C3;C4,E4,G4", "LH: 1 2 3 4 | RH: . . 2e . . 4e", null),
                        step("Independência por retirada", "Mantenha C na esquerda e acrescente um único acorde sincopado. Depois retire a direita sem deixar a esquerda mudar de duração.", "C3;C3;C4,E4,G4;C3;C3", "baixo = âncora; acorde = deslocamento", null),
                        step("Demonstração C–Am", "Alterne C e Am a 60 BPM. O exemplo é original e isola a habilidade; qualquer repertório associado é referência de linguagem, não transcrição.", "C3;C3;C4,E4,G4;C3;A2;A2;A3,C4,E4;A2", "2 compassos por acorde", null)));
    }

    private static LibraryContent reharmonization(Skill skill, String id, TeachingContentCatalog.Unit unit, Exercise ex) {
        return material(skill, id, unit, ex, "Trocar acordes preservando a lógica da melodia",
                "Reharmonizar é escolher outra função sob uma nota melódica que age como restrição. A alternativa precisa preparar e resolver, não apenas conter a nota.",
                List.of(
                        step("Melodia Muse como restrição", "Use a melodia original C–E–G–E | D–F–E–C | A–G–F–E | D–B–C. Marque notas longas antes de escolher acordes.", "C4;E4;G4;E4;D4;F4;E4;C4;A4;G4;F4;E4;D4;B3;C4", "8 compassos, C maior", null),
                        step("Solução A: diatônica", "Harmonize com C | G | Am | F | Dm | G | C/G | C. Observe notas comuns e mova cada voz pelo menor caminho disponível.", "C3,E3,G3;G2,B2,D3;A2,C3,E3;F2,A2,C3;D3,F3,A3;G2,B2,D3;C3,E3,G3", "I V vi IV | ii V I", null),
                        step("Solução B: tonicização", "Substitua o compasso anterior a Dm por A7: C# prepara D e G resolve em F. Compare a tensão A7→Dm com Am→Dm e preserve a melodia compatível.", "A2,C#3,E3,G3;D3,F3,A3;G2,B2,D3;C3,E3,G3", "V/ii → ii → V → I", null)));
    }

    private static LibraryContent popVoicings(Skill skill, String id, TeachingContentCatalog.Unit unit, Exercise ex) {
        return material(skill, id, unit, ex, "Conduzir cada voz dentro de E–B–C#m–A",
                "Voicing pop separa função, registro e linha superior. Inversões preservam notas comuns e evitam que todas as vozes saltem com a fundamental.",
                List.of(
                        step("Resolva as vozes antes do ritmo", "Na direita, use E–G#–B, D#–F#–B, E–G#–C# e E–A–C#. Observe B comum no primeiro par e E/G# no segundo.", "E4,G#4,B4;D#4,F#4,B4;E4,G#4,C#5;E4,A4,C#5", "I | V | vi | IV em E", null),
                        step("Cante a voz superior", "Compare B–B–C#–C# com uma versão em que todo acorde salta. Escolha a linha cantável antes de acrescentar oitavas na esquerda.", "B4;B4;C#5;C#5", "topo: B B C# C#", null),
                        step("Equilíbrio entre as mãos", "Toque fundamentais ou oitavas na esquerda e mantenha a direita no registro médio. Destaque a linha superior sem aumentar todo o acorde; use sétimas somente quando melhorarem a condução.", "E2;E4,G#4,B4;B1;D#4,F#4,B4;C#2;E4,G#4,C#5;A1;E4,A4,C#5", "baixo separado | voicing conduzido", null)));
    }

    private static LibraryContent kickVariation(Skill skill, String id, TeachingContentCatalog.Unit unit, Exercise ex) {
        return material(skill, id, unit, ex, "Mudar o bumbo mantendo caixa e condução invariantes",
                "O contraste vem de uma camada: chimbal em colcheias e caixa em 2 e 4 são âncoras; o bumbo muda a relação com o baixo sem mover o backbeat.",
                List.of(
                        step("Groove A", "Toque H em todas as colcheias, S em 2 e 4 e K em 1 e 3. Ouça o backbeat antes de alterar o pé.", "K,H;H;S,H;H;K,H;H;S,H;H", "H x-x-x-x-x-x-x-x | S --o-----o----- | K o---o---", null),
                        step("Groove B", "Preserve H e S; mova apenas o segundo bumbo para o e de 3. Se a caixa sair de 2 ou 4, volte ao A.", "K,H;H;S,H;H;H;K,H;S,H;H", "H x-x-x-x-x-x-x-x | S --o-----o----- | K o-----o-", null),
                        step("Contraste A–B", "Faça quatro compassos A e quatro B a 68 BPM. Nomeie a única camada alterada e volte ao A sem virada.", "K,H;H;S,H;H;K,H;H;S,H;H;K,H;H;S,H;H;H;K,H;S,H;H", "A4 | B4", null)));
    }

    private static LibraryContent harmonicLandmarks(Skill skill, String id, TeachingContentCatalog.Unit unit, Exercise ex) {
        return material(skill, id, unit, ex, "Usar a harmonia como mapa de transições",
                "Bateristas não precisam tocar alturas para reagir à harmonia: estabilidade, preparação, tensão e retorno indicam quando conservar textura ou conduzir uma transição.",
                List.of(
                        step("Loop Muse I–IV–V–I", "Ouça C–F–G–C e marque: repouso, movimento, tensão, retorno. Cante o baixo C–F–G–C antes de tocar bateria.", "C3,E3,G3;F3,A3,C4;G3,B3,D4;C3,E3,G3", "I | IV | V | I", null),
                        step("Textura prepara sem preencher", "Mantenha groove fechado em I e IV; abra a condução no V. A mudança anuncia retorno sem exigir virada em todo acorde.", "K,H;H;S,H;H;K,H;H;S,H;H;K,H;H;S,H;H;K,R;S,R;K,H", "I estável | IV movimento | V prepara", null),
                        step("Virada e retorno", "Use somente o último tempo do V para uma célula S–T–T–K e chegue ao C com bumbo e crash. O teste termina quando o groove retorna estável.", "G3,B3,D4;S;T;T;K;C3,E3,G3;K,C", "V: 3 compassos + virada | I: retorno", null)));
    }

    private static LibraryContent grooveComposition(Skill skill, String id, TeachingContentCatalog.Unit unit, Exercise ex) {
        return material(skill, id, unit, ex, "Compor bateria a partir dos acentos de um baixo original",
                "Bumbo pode duplicar um acento do baixo ou respondê-lo; espaço evita que todas as notas virem obrigação. Caixa em 2 e 4 conserva uma âncora enquanto A e B mudam uma camada.",
                List.of(
                        step("Linha de baixo Muse em Em", "Ouça E–E–G–B | E–D–B–A. Marque os ataques E, G e B que definem o contorno; não copie automaticamente todos.", "E2;E2;G2;B2;E2;D2;B1;A1", "Em | E E G B | E D B A |", null),
                        step("Groove A: duplicar e deixar espaço", "Mantenha S em 2 e 4. Use K no primeiro E e no G; deixe o segundo E sem bumbo para o baixo respirar.", "K,H;H;S,H;H;K,H;H;S,H;H", "A: backbeat fixo, 2 bumbos", null),
                        step("Groove B: responder", "Preserve chimbal e caixa; mova apenas um bumbo para depois do B. Compare se B cria contraste sem perder o contorno da linha. Organize quatro compassos A + quatro B.", "K,H;H;S,H;H;H;K,H;S,H;H", "A4 | B4, uma camada muda", null)));
    }

    private static LibraryContent oddMeter(Skill skill, String id, TeachingContentCatalog.Unit unit, Exercise ex) {
        return material(skill, id, unit, ex, "Ouvir 7/8 como 2+2+3",
                "Sete colcheias deixam de ser uma contagem abstrata quando três gestos têm comprimentos 2, 2 e 3. O primeiro ataque de cada grupo cria o fraseado.",
                List.of(
                        step("Três grupos, sete ataques", "Fale DOIS–DOIS–TRÊS e acentue 1, 3 e 5. O grupo de três é o único que demora uma colcheia a mais.", "C,H;H;C,H;H;C,H;H;H", "7/8 | > x | > x | > x x |", null),
                        step("Adicione âncoras", "Conserve o chimbal nas sete colcheias, caixa no início do segundo grupo e bumbo no primeiro. Complete oito ciclos antes de variar.", "K,H;H;S,H;H;H;H;H", "H x x x x x x x | S - - o - - - - | K o - - - - - -", null),
                        step("Compare 2+2+3 e 3+2+2", "Mude somente o agrupamento. O número de ataques não muda, mas os pontos de apoio mudam; volte a 2+2+3 sem inserir uma oitava colcheia.", "C,H;H;C,H;H;C,H;H;H;C,H;H;H;C,H;H;C,H;H", "2+2+3 ↔ 3+2+2", null)));
    }

    private static LibraryContent polyrhythm(Skill skill, String id, TeachingContentCatalog.Unit unit, Exercise ex) {
        return material(skill, id, unit, ex, "Construir 3:2 pelo ciclo de seis subdivisões",
                "Três contra dois compartilha começo e fim. Em seis pontos iguais, a camada de três toca 1–3–5 e a de dois toca 1–4; a convergência orienta o ouvido.",
                List.of(
                        step("Mapa de convergência", "Conte seis pontos. Palmas entram em 1–3–5; pé em 1–4. O primeiro ponto recebe as duas camadas e não é um ataque extra.", "K,S;R;S;K;S;R", "grade 1 2 3 4 5 6 | mãos X . X . X . | pé X . . X . .", null),
                        step("Ouça cada camada sobreviver", "Fixe o pé em dois ataques e cante a camada de três antes de usar baquetas. Se uma camada virar acompanhamento da outra, retire a orquestração.", "K,H;R;S;K,H;S;R", "2 no pé + 3 nas mãos", null),
                        step("Textura e resolução", "Orquestre as três mãos entre caixa e tom por dois ciclos; no encontro seguinte, resolva em groove 4/4 sem recalcular o pulso.", "K,S;R;T;K;S;R;K,H;H;S,H;H;K,H;H;S,H;H", "3:2 por 2 ciclos → 4/4", null)));
    }

    private static LibraryContent drumStyleTransfer(Skill skill, String id, TeachingContentCatalog.Unit unit, Exercise ex) {
        return material(skill, id, unit, ex, "Mudar linguagem sem perder forma A–B",
                "Transferência estilística conserva duração e marcos; condução, subdivisão, articulação e espaço mudam para produzir outra linguagem.",
                List.of(
                        step("Rock: colcheias e backbeat", "Mantenha chimbal em colcheias, caixa em 2 e 4 e bumbo sustentando a forma. A virada existe apenas na fronteira A–B.", "K,H;H;S,H;H;K,H;H;S,H;H", "rock | H em colcheias | S 2 e 4", null),
                        step("Funk: subdivisão leve e espaço", "Troque a condução por semicolcheias leves, preserve o backbeat e retire um bumbo. A densidade aumenta no chimbal sem preencher o grave.", "K,H;H;H;H;S,H;H;H;H;H;K,H;H;S,H;H", "funk | condução densa, bumbo seletivo", null),
                        step("Half-time: nova caixa, mesma forma", "Mova a caixa principal para 3 sem mudar BPM ou número de compassos. Execute A–B nas três versões e compare o que permaneceu invariável.", "K,H;H;H;H;S,H;H;K,H;H;H;H;S,H;H", "half-time | S em 3 | forma preservada", null)));
    }

    private static LibraryContent exerciseAlignedLesson(Skill skill, String id, TeachingContentCatalog.Unit unit,
                                                         Exercise ex) {
        var family = family(unit, ex);
        var framing = switch (family) {
            case EAR -> "A resposta nasce de uma comparação audível: antecipe, identifique e reproduza antes de conferir o nome.";
            case READING -> "Leitura coordena posição e duração; primeiro decodifique a grade, depois mantenha continuidade sem corrigir para trás.";
            case CREATION -> "Criação aqui é escolha dentro de limites: preserve o material dado e varie somente a dimensão indicada.";
            case PERFORMANCE -> "Performance organiza preparação, forma e recuperação; um erro isolado não encerra a função musical.";
            case HARMONY -> "A construção precisa ser ouvida como função e aplicada sob uma restrição musical, não apenas nomeada.";
            case RHYTHM -> "Pulso e subdivisão são a grade; identifique quais eventos ficam fixos e quais podem mudar.";
            case DRUM_TECHNIQUE -> "Som consistente vem de movimento e rebote controlados; isole a camada antes de coordená-la com o kit.";
            case INSTRUMENT_TECHNIQUE -> "O gesto é aprendido pela relação entre preparação, ataque e resultado sonoro nas condições declaradas.";
        };
        var firstInstruction = ex.getInstructions().getFirst();
        var secondInstruction = ex.getInstructions().get(Math.min(1, ex.getInstructions().size() - 1));
        var lastInstruction = ex.getInstructions().getLast();
        var notation = canonicalNotation(unit, ex);
        var audio = canonicalAudio(unit, ex);
        return material(skill, id, unit, ex, unit.title(), framing, List.of(
                step("Referência que governa a tarefa", framing + " Condição: " + ex.getPracticeConditions(), audio,
                        notation, tablature(unit, notation)),
                step("Exemplo resolvido em partes", firstInstruction + " Em seguida: " + secondInstruction
                                + " Compare o resultado com esta regra: " + ex.getSuccessCriteria(), audio,
                        notation, tablature(unit, notation)),
                step("Variação com uma decisão", lastInstruction + " A tentativa termina quando "
                                + decapitalize(ex.getSuccessCriteria()), audio, notation, tablature(unit, notation))));
    }

    private enum Family { EAR, READING, CREATION, PERFORMANCE, HARMONY, RHYTHM, DRUM_TECHNIQUE, INSTRUMENT_TECHNIQUE }

    private static Family family(TeachingContentCatalog.Unit unit, Exercise ex) {
        var key = (unit.competencyId() + " " + ex.getActivityType()).toLowerCase(Locale.ROOT);
        if (key.contains("ear") || key.contains("pitch") || key.contains("transcription") || key.contains("listen") || key.contains("compare")) return Family.EAR;
        if (key.contains("read") || key.contains("sight") || key.contains("notation")) return Family.READING;
        if (key.contains("creat") || key.contains("composition") || key.contains("arrangement") || key.contains("improv") || key.contains("adaptation")) return Family.CREATION;
        if (key.contains("performance") || key.contains("stage") || key.contains("memor")) return Family.PERFORMANCE;
        if (key.contains("chord") || key.contains("voicing") || key.contains("harmon") || key.contains("triad")) return Family.HARMONY;
        if (unit.instrument() == InstrumentId.DRUMS && (key.contains("stroke") || key.contains("pedal") || key.contains("rudiment") || key.contains("setup"))) return Family.DRUM_TECHNIQUE;
        if (key.contains("rhythm") || key.contains("groove") || key.contains("meter") || key.contains("poly") || key.contains("triplet") || key.contains("fill")) return Family.RHYTHM;
        return Family.INSTRUMENT_TECHNIQUE;
    }

    private static LibraryContent material(Skill skill, String id, TeachingContentCatalog.Unit unit, Exercise ex,
                                           String title, String summary, List<LessonStep> steps) {
        return new LibraryContent(id, title, skill.getTechnicalName(), skill.getDomain(), summary,
                skill.getId(), unit.stage().name().toLowerCase(Locale.ROOT), Math.max(10, ex.getMinutes()),
                diagramType(unit.instrument()), steps.getFirst().getNotation(), steps.getFirst().getTablature(),
                List.of(unit.objective(), "Reconhecer a referência antes da tentativa.", ex.getSuccessCriteria()),
                List.of(summary, "Condição de estudo: " + ex.getPracticeConditions(),
                        "Resultado observável: " + ex.getSuccessCriteria()),
                List.of(steps.getFirst().getMusicalExample(), steps.getLast().getMusicalExample()),
                List.of("Mudar mais de uma variável por tentativa.", "Confundir conclusão da atividade com domínio."),
                steps, skill.getNextSkills().stream().map(next -> "lesson-" + next).toList());
    }

    private static LessonStep step(String title, String explanation, String audio, String notation, String tab) {
        return new LessonStep(title, explanation, notation, notation, tab, audio);
    }

    private static String canonicalAudio(TeachingContentCatalog.Unit unit, Exercise ex) {
        if (unit.instrument() == InstrumentId.DRUMS) return "K,H;H;S,H;H;K,H;H;S,H;H";
        if (family(unit, ex) == Family.HARMONY) return "C4,E4,G4;F3,A3,C4;G3,B3,D4;C4,E4,G4";
        if (family(unit, ex) == Family.EAR) return "C4;E4;D4;G4;E4;C4";
        return switch (unit.instrument()) {
            case GUITAR -> "E3;G3;A3;B3;R;A3;G3;E3";
            case ACOUSTIC -> "C3;G3;C4;E4;G2;G3;B3;D4";
            case KEYS -> "C4;D4;E4;G4;E4;D4;C4";
            case DRUMS -> "K,H;H;S,H;H;K,H;H;S,H;H";
        };
    }

    private static String canonicalNotation(TeachingContentCatalog.Unit unit, Exercise ex) {
        if (unit.instrument() == InstrumentId.DRUMS) return "H x-x-x-x-x-x-x-x | S --o-----o----- | K o---o---";
        if (ex.getCurrentBpm() > 0) return "4/4 | célula original | " + ex.getCurrentBpm() + " BPM configurado";
        return "material original Muse | " + unit.competencyId();
    }

    private static String tablature(TeachingContentCatalog.Unit unit, String notation) {
        if (unit.instrument() != InstrumentId.GUITAR && unit.instrument() != InstrumentId.ACOUSTIC) return null;
        return "e|----------------|\nB|------0-1-0-----|\nG|--0-2-------2-0-|\nD|----------------|\nA|----------------|\nE|----------------|";
    }

    private static String diagramType(InstrumentId instrument) {
        return switch (instrument) {
            case GUITAR, ACOUSTIC -> "fretboard";
            case KEYS -> "keyboard";
            case DRUMS -> "rhythm";
        };
    }

    private static String decapitalize(String value) {
        if (value == null || value.isBlank()) return "o critério declarado for observável.";
        return Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }
}
