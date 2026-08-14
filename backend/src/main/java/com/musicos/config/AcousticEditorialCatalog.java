package com.musicos.config;

import static com.musicos.domain.InstrumentId.ACOUSTIC;

import com.musicos.domain.Assessment;
import com.musicos.domain.ExerciseVariation;
import com.musicos.domain.LearningStage;
import java.util.List;

/** Jornada editorial de violão: acompanhamento, baixo/polegar, voz e arranjo solo. */
final class AcousticEditorialCatalog {
    private AcousticEditorialCatalog() {}

    static List<EditorialMissionDefinition> definitions() {
        return List.of(
                d("mission-acoustic-posture-breath", LearningStage.FIRST_STEPS, "acoustic-posture", "Apoiar o violão sem prender a respiração",
                        "Sustentar o instrumento e alcançar as seis cordas com ombros, punhos e respiração livres.",
                        "A caixa do violão muda a relação do corpo com braço e mão direita; estabilidade evita compensações que depois limitam levada e dedilhado.",
                        "Cadeira sem braços; apoio escolhido; câmera lateral; três ciclos de montar, tocar e repousar.",
                        List.of("Ajuste a altura até o braço chegar ao instrumento sem elevar o ombro.", "Toque cada corda com o polegar e observe se o tronco procura o instrumento.", "Retire as mãos, respire e reconstrua a posição sem olhar a primeira tentativa."),
                        "Três montagens preservam alcance, punho neutro e respiração durante seis ataques.",
                        List.of(v("Só apoio", "Encontre equilíbrio sem usar a mão esquerda para segurar o braço.", 0, 4), v("Primeiro acorde", "Monte Em sem mudar a posição do instrumento.", 0, 5)),
                        "demonstrate", 0, 0, 7, "Preparar produção sonora e trocas sem tensão acumulada.", Assessment.Type.FORMATIVE,
                        List.of("equilibrio", "alcance", "tensao"), List.of()),

                d("mission-acoustic-pitch-direction", LearningStage.FIRST_STEPS, "ear-pitch", "Cantar o baixo antes de encontrá-lo",
                        "Ouvir se um baixo sobe ou desce e reproduzir o contorno nas cordas graves.",
                        "Acompanhamento ganha direção quando o polegar procura uma nota antecipada pelo ouvido, não uma casa por tentativa.",
                        "Cordas E e A; pares E–G, G–C e C–D; resposta vocal antes do instrumento.",
                        List.of("Ouça o par e desenhe sua direção com a mão.", "Cante a segunda nota e procure-a mantendo a primeira como referência.", "Transfira o mesmo contorno para outra nota inicial."),
                        "Oito de dez direções corretas e três contornos reproduzidos sem busca aleatória.",
                        List.of(v("Cordas soltas", "Use E–A e A–D para começar.", 0, 4), v("Três notas", "Reproduza baixo que sobe, repete e desce.", 0, 5)),
                        "listen", 0, 0, 8, "Antecipar baixos de ligação em um ciclo de acordes.", Assessment.Type.FORMATIVE,
                        List.of("direcao", "antecipacao", "localizacao"), List.of("stand-by-me-acoustic")),

                d("mission-acoustic-rhythm-reading", LearningStage.BEGINNER, "rhythm-reading", "Ler a levada antes da cifra",
                        "Executar uma grade com semínimas, colcheias e pausas mantendo o movimento da mão.",
                        "A cifra informa harmonia; a leitura rítmica evita que toda música receba a mesma levada.",
                        "Cordas abafadas; 4/4 a 60 BPM; setas de mão para baixo e para cima; dois compassos.",
                        List.of("Conte a grade e marque somente os ataques com palmas.", "Faça todos os movimentos da mão e deixe as pausas passarem sem contato.", "Aplique a grade em Em e G, mudando acorde no início do segundo compasso."),
                        "Três formas preservam direção da mão, duração das pausas e troca harmônica.",
                        List.of(v("Sem acorde", "Use somente cordas abafadas e observe a mão contínua.", -8, 4), v("Cifra surpresa", "Aplique a mesma grade em C–G sem reaprender o ritmo.", 0, 6)),
                        "notation", 60, 84, 9, "Ler a célula de acompanhamento de Have You Ever Seen the Rain.", Assessment.Type.APPLICATION,
                        List.of("duracao", "direcao-da-mao", "independencia-da-cifra"), List.of("seen-rain-acoustic")),

                d("mission-acoustic-two-chord-song", LearningStage.BEGINNER, "two-chord-song", "Acompanhar uma forma com dois acordes",
                        "Tocar verso e refrão simplificados com duas formas, pulso e entrada reconhecível.",
                        "Uma música inteira com poucas formas ensina continuidade e escuta de seção antes de ampliar vocabulário harmônico.",
                        "A Horse with No Name; formas simplificadas; 68 BPM; um padrão de semínimas.",
                        List.of("Mapeie quantos compassos cada acorde ocupa.", "Toque só o primeiro ataque de cada compasso e conte a forma.", "Preencha as semínimas e atravesse verso e refrão sem reiniciar."),
                        "A forma completa mantém as mudanças no lugar e chega ao final sem interrupção.",
                        List.of(v("Um ataque", "Toque somente no tempo 1 de cada compasso.", -8, 5), v("Dinâmica", "Faça o refrão mais amplo sem acelerar.", 0, 7)),
                        "play_along", 68, 78, 12, "Acompanhar uma canção completa com duas formas funcionais.", Assessment.Type.PERFORMANCE,
                        List.of("forma", "troca", "continuidade"), List.of("horse-no-name-acoustic")),

                d("mission-acoustic-quarter-strum", LearningStage.BEGINNER, "quarter-strumming", "Baixo no primeiro tempo, acorde nos demais",
                        "Alternar fundamental e acorde em quatro tempos sem deslocar a troca.",
                        "Separar baixo e acorde dá direção ao acompanhamento e prepara independência do polegar.",
                        "C–G–Am–F simplificado; 56 BPM; baixo em 1, acorde em 2–3–4.",
                        List.of("Localize a fundamental de cada acorde antes da forma completa.", "Toque somente os baixos no tempo 1 por quatro voltas.", "Acrescente os ataques de acorde e mantenha o baixo mais presente, não mais apressado."),
                        "Quatro ciclos com fundamental correta no 1 e acordes alinhados nos outros tempos.",
                        List.of(v("Dois acordes", "Use apenas C e G até o polegar antecipar a corda.", -8, 5), v("Baixo alternado", "No segundo ciclo, escolha quinta no tempo 3.", 0, 7)),
                        "guided", 56, 80, 10, "Criar uma textura de acompanhamento para Stand by Me.", Assessment.Type.APPLICATION,
                        List.of("baixo-correto", "pulso", "balanco-de-vozes"), List.of("stand-by-me-acoustic")),

                d("mission-acoustic-eighth-strum", LearningStage.BEGINNER_ADVANCED, "eighth-strumming", "Conservar a mão em colcheias durante as trocas",
                        "Sustentar oito compassos com movimento contínuo e ataques selecionados.",
                        "A levada depende do gesto que atravessa som e silêncio; decorar setas sem movimento contínuo produz quebras.",
                        "Em–C–G–D; 68 BPM; baixo-cima contínuo; ataques em 1, 2-e, 3, 4-e.",
                        List.of("Faça a trajetória completa com cordas abafadas.", "Acentue os ataques escolhidos sem parar nos movimentos vazios.", "Troque acordes mantendo a mão direita e continue após uma corda abafada."),
                        "Oito compassos com padrão reconhecível, trocas no 1 e nenhum congelamento da mão.",
                        List.of(v("Um acorde", "Use Em para estabilizar som e silêncio.", -12, 5), v("Antecipação", "Antecipe D no 'e' de 4 sem mover o pulso.", 0, 7)),
                        "play_along", 68, 92, 11, "Acompanhar o ciclo de Zombie com levada consistente.", Assessment.Type.PERFORMANCE,
                        List.of("movimento-continuo", "ataques", "trocas"), List.of("zombie-acoustic")),

                d("mission-acoustic-capo-logic", LearningStage.BEGINNER_ADVANCED, "capo-use", "Transpor preservando as formas",
                        "Escolher posição de capotraste para mudar o tom e prever os acordes que realmente soam.",
                        "O capotraste é uma ferramenta de transposição e registro, não um número copiado da cifra.",
                        "Progressão G–D–Em–C; capotraste disponível; comparação sem e com capo 2.",
                        List.of("Toque a progressão sem capotraste e nomeie o tom de referência.", "Coloque o capo 2 e calcule cada acorde soando dois semitons acima.", "Compare registro, conforto vocal e timbre antes de escolher a posição."),
                        "A transposição é nomeada corretamente e executada sem alterar relações da progressão.",
                        List.of(v("Um acorde", "Compare apenas G sem capo e com capo 2.", 0, 5), v("Escolha vocal", "Teste duas posições e justifique pela tessitura, não pela facilidade.", 0, 7)),
                        "compare", 0, 0, 10, "Adaptar uma canção à voz preservando shapes familiares.", Assessment.Type.TRANSFER,
                        List.of("semitons", "acorde-real", "escolha-de-registro"), List.of("wonderwall-acoustic")),

                d("mission-acoustic-barre-release", LearningStage.BEGINNER_ADVANCED, "barre-chords", "Pestana com pressão seletiva",
                        "Produzir F maior limpo e liberar pressão nos silêncios sem desmontar a forma.",
                        "Pestana eficiente distribui apoio e relaxamento; força contínua impede trocas e dinâmica.",
                        "F maior na primeira casa; sem metrônomo na montagem; depois dois tempos a 52 BPM.",
                        List.of("Teste primeiro as cordas que dependem do indicador.", "Adicione os outros dedos sem aumentar a força do polegar.", "Toque dois tempos, alivie nos dois seguintes e reconstrua pelo tato."),
                        "Três montagens com seis cordas previstas e relaxamento audível entre os ataques.",
                        List.of(v("Mini pestana", "Use somente as duas cordas agudas antes da forma completa.", 0, 5), v("Troca C–F", "Chegue a F no tempo 1 usando C como origem.", -8, 7)),
                        "guided", 52, 76, 11, "Incluir F em acompanhamentos sem interromper a levada.", Assessment.Type.FORMATIVE,
                        List.of("clareza", "pressao", "reconstrucao"), List.of("hotel-california-acoustic")),

                d("mission-acoustic-arpeggio-voice", LearningStage.EARLY_INTERMEDIATE, "acoustic-arpeggio-patterns", "Polegar conduz, dedos respondem",
                        "Executar P–i–m–a sobre quatro acordes mantendo baixo e vozes superiores distintos.",
                        "Arpejo organiza funções da mão direita: o polegar narra a harmonia enquanto dedos mantêm textura.",
                        "Am–C–G–Em; 60 BPM; polegar na fundamental; i–m–a nas cordas 3–2–1.",
                        List.of("Toque só os baixos e confirme a corda de cada fundamental.", "Acrescente i–m–a sem repetir o polegar.", "Mude acordes deixando o último dedo agudo soar até o próximo baixo."),
                        "Quatro voltas com baixos corretos, ordem dos dedos estável e sobreposição controlada.",
                        List.of(v("P–i", "Use apenas baixo e terceira corda.", -12, 5), v("Baixo alternado", "Troque a fundamental pela quinta no segundo ciclo.", 0, 8)),
                        "guided", 60, 84, 12, "Aplicar o padrão à introdução simplificada de House of the Rising Sun.", Assessment.Type.APPLICATION,
                        List.of("funcao-do-polegar", "ordem-dos-dedos", "conexao"), List.of("house-rising-sun-acoustic")),

                d("mission-acoustic-chord-ear", LearningStage.EARLY_INTERMEDIATE, "ear-chords", "Ouvir maior e menor antes da cifra",
                        "Distinguir tríades maiores e menores e escolher a forma aberta correspondente.",
                        "Percepção harmônica permite acompanhar, transpor e corrigir cifra com o instrumento como confirmação.",
                        "Pares C/Am, G/Em e D/Dm; resposta vocal; violão usado depois da classificação.",
                        List.of("Ouça o par e descreva brilho, repouso ou tensão sem usar o nome.", "Classifique a qualidade e cante a terça que define a resposta.", "Monte uma forma aberta da mesma qualidade e compare com o estímulo."),
                        "Oito de dez qualidades e três reproduções coerentes com a terça ouvida.",
                        List.of(v("Par fixo", "Compare somente C e Am.", 0, 5), v("Mesma tônica", "Compare D e Dm para isolar a terça.", 0, 7)),
                        "listen", 0, 0, 11, "Escolher acordes para harmonizar uma melodia cantada curta.", Assessment.Type.TRANSFER,
                        List.of("qualidade", "terca", "forma-correspondente"), List.of()),

                d("mission-acoustic-memory-form", LearningStage.EARLY_INTERMEDIATE, "memorization", "Acompanhar sem olhar a cifra",
                        "Recuperar a forma de verso e refrão usando baixo, direção harmônica e pontos de reencontro.",
                        "Memorizar é reconstruir relações e forma, não fotografar uma página de acordes.",
                        "Knockin' on Heaven's Door; mapa estudado e depois fechado; andamento reduzido.",
                        List.of("Agrupe os acordes em duas células e cante os baixos.", "Toque a forma olhando apenas nomes de seção.", "Feche o mapa e use o primeiro acorde de cada seção como ponto de reencontro."),
                        "Duas passagens sem cifra preservam ordem, duração e transição entre seções.",
                        List.of(v("Uma seção", "Recupere somente o verso e descreva sua célula.", -8, 6), v("Começo aleatório", "Inicie pelo refrão sem tocar desde o início.", 0, 8)),
                        "recall", 64, 68, 13, "Tocar uma versão completa mantendo contato visual com a voz ou banda.", Assessment.Type.RETENTION,
                        List.of("ordem", "duracao", "reencontro"), List.of("knockin-heavens-door-acoustic")),

                d("mission-acoustic-fingerstyle-independence", LearningStage.INTERMEDIATE, "fingerstyle-independence", "Baixo constante sob melodia móvel",
                        "Manter o polegar em semínimas enquanto os dedos executam uma frase sincopada.",
                        "Fingerstyle começa pela independência de funções: baixo previsível sustenta liberdade nas vozes superiores.",
                        "C e Am; 56 BPM; polegar alterna fundamental e quinta; frase nas cordas 1 e 2.",
                        List.of("Estabilize quatro baixos por compasso sem dedos.", "Cante a frase aguda e toque-a isoladamente.", "Combine, retirando a melodia por um compasso se ela deslocar o polegar."),
                        "Três ciclos mantêm o baixo no pulso e a frase entra nos mesmos pontos.",
                        List.of(v("Uma resposta", "Use uma única nota aguda no 'e' de 2.", -12, 6), v("Melodia variável", "Mude duas notas agudas sem alterar o polegar.", 0, 9)),
                        "guided", 56, 80, 14, "Construir textura de baixo e melodia para Dust in the Wind.", Assessment.Type.PERFORMANCE,
                        List.of("baixo", "independencia", "frase-superior"), List.of("dust-in-wind-acoustic")),

                d("mission-acoustic-sight-rhythm", LearningStage.EARLY_INTERMEDIATE, "sight-reading", "Ler uma linha melódica sem parar",
                        "Executar oito compassos novos preservando pulso, direção e recuperação.",
                        "Leitura no violão conecta símbolo, posição e som; continuidade vale mais que corrigir cada nota retroativamente.",
                        "Melodia original em primeira posição; 4/4 a 52 BPM; quinze segundos de inspeção.",
                        List.of("Marque ritmos e maior salto antes de tocar.", "Leia mantendo um compasso de antecedência visual.", "Se errar altura, preserve o pulso e reencontre na próxima nota de referência."),
                        "A leitura chega ao fim, mantém a forma e recupera erros em até um compasso.",
                        List.of(v("Quatro compassos", "Leia metade do trecho com notas naturais.", -8, 6), v("Outra região", "Transfira a última frase uma oitava quando possível.", 0, 8)),
                        "notation", 52, 76, 13, "Ler uma introdução original antes de incorporá-la ao arranjo.", Assessment.Type.PERFORMANCE,
                        List.of("pulso", "direcao", "recuperacao"), List.of()),

                d("mission-acoustic-dynamic-vocal", LearningStage.INTERMEDIATE, "accompaniment-dynamics", "Acompanhar sem encobrir a voz",
                        "Mudar registro, densidade e intensidade entre verso e refrão preservando o cantor.",
                        "Acompanhamento serve à canção; dinâmica útil responde a texto, tessitura e espaço da voz.",
                        "Progressão G–D–Em–C; voz cantada ou gravação; verso dedilhado, refrão em levada.",
                        List.of("Cante a melodia e marque onde ela ocupa região grave.", "No verso, use baixo e duas vozes com ataque leve.", "No refrão, amplie a levada sem aumentar sobre as entradas vocais."),
                        "A voz permanece inteligível e as seções contrastam sem mudança de pulso.",
                        List.of(v("Voz falada", "Use uma leitura falada para calibrar espaço e volume.", -8, 7), v("Final reduzido", "Retorne à textura mínima nos dois últimos compassos.", 0, 9)),
                        "record", 68, 84, 15, "Criar um acompanhamento que sustente canto real.", Assessment.Type.PERFORMANCE,
                        List.of("espaco-vocal", "contraste", "pulso"), List.of("every-rose-acoustic")),

                d("mission-acoustic-phrase-response", LearningStage.EARLY_INTERMEDIATE, "phrasing", "Responder à voz com poucas notas",
                        "Criar respostas de duas a quatro notas nos espaços de uma melodia cantada.",
                        "Fraseado no violão deve conversar com a voz; preencher cada silêncio transforma resposta em competição.",
                        "Ciclo C–Am–F–G; melodia vocal original; notas C, D, E, G e A; 64 BPM.",
                        List.of("Cante a melodia e marque dois espaços que aceitam resposta.", "Crie uma resposta para o primeiro espaço e preserve seu ritmo.", "No segundo espaço, varie apenas a chegada e deixe a voz retomar sem sobreposição."),
                        "Duas voltas mantêm respostas nos espaços previstos, com ritmo repetível e chegada harmônica.",
                        List.of(v("Uma resposta", "Use somente C e E no primeiro espaço.", -8, 6), v("Outro registro", "Transfira a segunda resposta uma oitava sem aumentar a densidade.", 0, 8)),
                        "create", 64, 88, 13, "Adicionar pequenos comentários melódicos a um acompanhamento cantado.", Assessment.Type.APPLICATION,
                        List.of("espaco", "motivo", "chegada"), List.of()),

                d("mission-acoustic-strum-review", LearningStage.BEGINNER_ADVANCED, "eighth-strumming", "Recuperar uma levada em outra progressão",
                        "Reconstruir o gesto de colcheias sem setas e transferi-lo de Em–C para G–D.",
                        "Revisão de levada precisa testar continuidade fora da cifra aprendida, não reapresentar o desenho original.",
                        "Sem aula aberta; 64 BPM; primeira tomada em cordas abafadas e segunda em G–D.",
                        List.of("Cante os ataques e mantenha a mão completa sem olhar setas.", "Faça quatro compassos abafados e identifique qualquer parada do gesto.", "Aplique em G–D, preservando ataques e acentos durante as trocas."),
                        "Oito compassos mantêm movimento, célula e chegada dos acordes sem apoio visual.",
                        List.of(v("Um acorde", "Recupere a célula inteira sobre G.", -8, 6), v("Acento novo", "Mova um acento sem alterar a trajetória da mão.", 0, 8)),
                        "review", 64, 92, 12, "Usar a levada recuperada em uma canção diferente.", Assessment.Type.RETENTION,
                        List.of("recuperacao", "movimento-continuo", "transferencia"), List.of("seen-rain-acoustic")),

                d("mission-acoustic-transcription", LearningStage.UPPER_INTERMEDIATE, "transcription", "Retirar baixo e levada de uma gravação",
                        "Reconstruir uma seção separando baixo, harmonia e padrão de mão direita.",
                        "Transcrever acompanhamento exige ouvir funções simultâneas e decidir uma ordem de investigação.",
                        "Trecho de Stand by Me; áudio reduzido; papel para baixos, cifra e ritmo.",
                        List.of("Anote apenas os momentos de mudança e cante a linha de baixo.", "Descubra as fundamentais antes das qualidades de acorde.", "Bata a levada separadamente e só então combine com a progressão."),
                        "A versão preserva baixos, ordem harmônica e célula rítmica principal.",
                        List.of(v("Só baixos", "Transcreva as fundamentais de quatro compassos.", 0, 7), v("Outro tom", "Transponha a versão completa para um tom confortável.", 0, 10)),
                        "listen", 0, 0, 16, "Criar uma cifra funcional e tocável a partir do ouvido.", Assessment.Type.TRANSFER,
                        List.of("baixo", "harmonia", "levada"), List.of("stand-by-me-acoustic")),

                d("mission-acoustic-transpose", LearningStage.UPPER_INTERMEDIATE, "transposed-accompaniment", "Transportar sem perder a voz superior",
                        "Levar uma progressão a dois tons diferentes preservando função e contorno da nota aguda.",
                        "Transposição madura considera voz, registro e condução, não apenas deslocamento de cifras.",
                        "I–V–vi–IV em G e A; capo opcional; nota superior definida em cada acorde.",
                        List.of("Analise os graus e toque a versão em G.", "Escolha shapes ou capo para A mantendo a região vocal.", "Compare a nota superior e ajuste inversões para conservar o contorno."),
                        "As duas versões mantêm funções, forma e linha superior reconhecível.",
                        List.of(v("Só graus", "Toque fundamentais e diga I–V–vi–IV.", -8, 7), v("Sem capo", "Faça a segunda versão com pestanas ou voicings móveis.", 0, 10)),
                        "transfer", 68, 92, 17, "Adaptar uma canção a outra voz sem empobrecer o arranjo.", Assessment.Type.TRANSFER,
                        List.of("funcoes", "tonalidade", "voz-superior"), List.of()),

                d("mission-acoustic-solo-arrangement", LearningStage.ADVANCED, "solo-acoustic-arrangement", "Unir baixo, acorde e melodia em oito compassos",
                        "Criar um arranjo solo com melodia reconhecível, baixo funcional e contraste entre seções.",
                        "Arranjo solo integra independência, harmonia, condução e decisão de textura em uma performance completa.",
                        "Melodia original de oito compassos em C; baixo nos tempos fortes; até quatro vozes.",
                        List.of("Toque a melodia sozinha e marque notas que precisam permanecer no topo.", "Acrescente baixos que revelem as funções sem preencher todos os tempos.", "Inclua acordes apenas onde sustentam a melodia e planeje uma textura diferente na repetição."),
                        "O arranjo preserva melodia, funções e forma sem interrupção entre camadas.",
                        List.of(v("Quatro compassos", "Harmonize metade da melodia com baixo e uma voz interna.", -8, 9), v("Introdução", "Crie dois compassos que antecipem o motivo principal.", 0, 12)),
                        "create", 60, 84, 20, "Registrar um estudo autoral completo para violão solo.", Assessment.Type.APPLICATION,
                        List.of("melodia", "baixo", "textura", "forma"), List.of()),

                d("mission-acoustic-stage-readiness", LearningStage.ADVANCED, "stage-readiness", "Preparar um set acústico recuperável",
                        "Executar três peças com transições, contagens e estratégias de recuperação definidas.",
                        "Tocar para pessoas exige gerir forma, afinação, voz, silêncio e erro entre músicas, não apenas dominar trechos isolados.",
                        "Set de três canções; uma afinação inicial; falas e contagens incluídas; gravação contínua.",
                        List.of("Defina ordem, tons e ponto de início de cada peça.", "Ensaiar somente transições: fim, silêncio, ajuste e nova contagem.", "Grave o set sem reiniciar e avalie continuidade, afinação e equilíbrio com a voz."),
                        "As três peças começam e terminam com intenção; erros não quebram o fluxo do set.",
                        List.of(v("Duas peças", "Teste a transição entre duas músicas contrastantes.", -8, 10), v("Sem material", "Faça a tomada final sem cifras visíveis.", 0, 14)),
                        "record", 68, 92, 22, "Apresentar um mini-set acústico coerente e autônomo.", Assessment.Type.PERFORMANCE,
                        List.of("preparacao", "transicoes", "recuperacao", "autonomia"), List.of("horse-no-name-acoustic", "knockin-heavens-door-acoustic"))
        );
    }

    private static EditorialMissionDefinition d(String id, LearningStage stage, String competencyId,
                                                  String title, String objective, String purpose, String conditions,
                                                  List<String> instructions, String success,
                                                  List<ExerciseVariation> variations, String activityType,
                                                  int currentBpm, int targetBpm, int minutes, String application,
                                                  Assessment.Type assessmentType, List<String> criteria,
                                                  List<String> repertoire) {
        return new EditorialMissionDefinition(id, ACOUSTIC, stage, competencyId, title, objective, purpose,
                conditions, instructions, success, variations, activityType, currentBpm, targetBpm, minutes,
                application, assessmentType, criteria, repertoire);
    }

    private static ExerciseVariation v(String name, String instruction, int bpmOffset, int minutes) {
        return new ExerciseVariation(name, instruction, bpmOffset, minutes);
    }
}
