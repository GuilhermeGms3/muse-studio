package com.musicos.config;

import static com.musicos.domain.InstrumentId.DRUMS;

import com.musicos.domain.Assessment;
import com.musicos.domain.ExerciseVariation;
import com.musicos.domain.LearningStage;
import java.util.List;

/** Jornada editorial de bateria: som, coordenação, pocket, forma e criação de grooves. */
final class DrumsEditorialCatalog {
    private DrumsEditorialCatalog() {}

    static List<EditorialMissionDefinition> definitions() {
        return List.of(
                d("mission-drums-setup-reach", LearningStage.FIRST_STEPS, "drum-setup", "Montar o kit ao redor do movimento",
                        "Alcançar caixa, chimbal, tons e pedais sem deslocar o centro do corpo.",
                        "Ergonomia define consistência de som e segurança antes de qualquer groove.",
                        "Banco, caixa, chimbal e dois pedais ajustáveis; câmera frontal e lateral.",
                        List.of("Ajuste o banco para os quadris ficarem ligeiramente acima dos joelhos.", "Toque oito semínimas em cada peça sem inclinar o tronco.", "Alterne caixa, tom, surdo e chimbal e corrija apenas o componente que exige alcance excessivo."),
                        "Duas sequências completas preservam equilíbrio e permitem retorno imediato à caixa.",
                        List.of(v("Triângulo básico", "Use somente caixa, chimbal e bumbo.", 0, 5), v("Pratos", "Inclua condução e crash sem cruzar trajetórias.", 0, 6)),
                        "demonstrate", 0, 0, 8, "Preparar movimentos previsíveis para grooves e viradas.", Assessment.Type.FORMATIVE,
                        List.of("equilibrio", "alcance", "retorno"), List.of()),

                d("mission-drums-bass-pedal", LearningStage.FIRST_STEPS, "drum-bass-pedal", "Controlar ataque e retorno do bumbo",
                        "Produzir oito bumbos equivalentes sem enterrar o batedor nem levantar a perna inteira.",
                        "O bumbo precisa articular e recuperar; força sem retorno limita variações e independência.",
                        "Pedal regulado; sem mãos; semínimas a 56 BPM; calcanhar em posição confortável.",
                        List.of("Deixe o batedor voltar e compare som aberto com batedor enterrado.", "Toque quatro golpes usando tornozelo e mantenha joelho estável.", "Faça oito golpes, retirando um no tempo 3 sem perder os demais."),
                        "Três séries mantêm volume, tempo e retorno do batedor.",
                        List.of(v("Quatro golpes", "Use um compasso com pausa longa entre tentativas.", -8, 4), v("Colcheias", "Alterne semínimas e duas colcheias no último tempo.", 0, 6)),
                        "guided", 56, 84, 9, "Levar um bumbo controlado ao primeiro rock beat.", Assessment.Type.FORMATIVE,
                        List.of("ataque", "retorno", "regularidade"), List.of()),

                d("mission-drums-hihat-foot", LearningStage.FIRST_STEPS, "drum-hi-hat-foot", "Fechar o chimbal sem prender o corpo",
                        "Alternar chimbal aberto e fechado com som definido e pulso estável.",
                        "O pé esquerdo controla duração e cor; aprender fechamento prepara aberturas e condução independente.",
                        "Sem baquetas; pé esquerdo em semínimas a 52 BPM; abertura de um tempo.",
                        List.of("Compare fechamento curto, contato excessivo e abertura controlada.", "Mantenha quatro fechamentos iguais sem mover o joelho para dentro.", "Abra no tempo 4 e feche exatamente no 1 seguinte."),
                        "Quatro ciclos apresentam abertura audível e fechamento alinhado ao primeiro tempo.",
                        List.of(v("Fechado", "Produza oito fechamentos equivalentes antes de abrir.", -8, 4), v("Mãos", "Acrescente caixa em 2 e 4 sem mudar o pé.", 0, 6)),
                        "guided", 52, 76, 9, "Preparar chimbal aberto dentro de grooves sem perder forma.", Assessment.Type.APPLICATION,
                        List.of("timbre", "fechamento", "pulso"), List.of()),

                d("mission-drums-reading-grid", LearningStage.BEGINNER, "drum-basic-reading", "Ler três linhas como um único groove",
                        "Executar oito compassos lendo chimbal, caixa e bumbo sem transformar cada linha em exercício separado.",
                        "Partitura de bateria representa simultaneidade; leitura precisa revelar função e coordenação do groove.",
                        "Grade original em 4/4; colcheias; 60 BPM; quatro compassos repetidos com pequena variação de bumbo.",
                        List.of("Leia cada voz e circule ataques simultâneos.", "Toque chimbal e conte onde caixa e bumbo entrarão.", "Acrescente as duas vozes e mantenha os olhos um tempo à frente."),
                        "Duas passagens completas preservam backbeat, variação de bumbo e continuidade.",
                        List.of(v("Duas linhas", "Leia apenas chimbal e caixa.", -8, 5), v("Sem repetição", "Leia segunda grade com o bumbo deslocado.", 0, 7)),
                        "notation", 60, 84, 11, "Ler uma variação antes de aplicá-la a um play-along.", Assessment.Type.PERFORMANCE,
                        List.of("simultaneidade", "backbeat", "antecipacao-visual"), List.of()),

                d("mission-drums-single-stroke", LearningStage.BEGINNER, "drum-single-stroke", "Alternar mãos com centro de som comum",
                        "Executar R–L em quatro superfícies sem mudar altura, pulso ou qualidade do ataque.",
                        "Single stroke serve a condução e orquestração quando as mãos soam equivalentes e se movem com economia.",
                        "Pad e kit; colcheias a 64 BPM; grupos de oito; acento somente no primeiro golpe.",
                        List.of("Compare quatro golpes de cada mão no centro do pad.", "Alterne R–L e reduza a mão que produz som mais alto.", "Mova cada grupo para caixa, tom 1, tom 2 e surdo sem cruzar braços."),
                        "Quatro voltas mantêm alternância, centro e diferença de volume discreta entre mãos.",
                        List.of(v("Pad", "Permaneça em uma superfície até equilibrar as mãos.", -12, 5), v("Acento móvel", "Desloque o acento para o segundo golpe sem endurecer os demais.", 0, 7)),
                        "guided", 64, 100, 11, "Orquestrar uma virada mantendo som e movimento consistentes.", Assessment.Type.APPLICATION,
                        List.of("alternancia", "simetria", "trajetoria"), List.of()),

                d("mission-drums-double-stroke", LearningStage.BEGINNER_ADVANCED, "drum-double-stroke", "Fazer o segundo golpe continuar sendo nota",
                        "Executar RR–LL com segundo golpe controlado, não apenas rebote residual.",
                        "Doubles ampliam densidade com economia, mas só funcionam quando os dois ataques têm intenção e pulso.",
                        "Pad; semicolcheias a 52 BPM; quatro tempos tocados e quatro de comparação single/double.",
                        List.of("Toque RR lentamente e observe quanto o segundo golpe perde volume.", "Use dedos para devolver energia sem apertar a baqueta.", "Alterne um compasso de singles e um de doubles mantendo mesma grade."),
                        "Três pares por mão apresentam dois ataques audíveis e transição estável para singles.",
                        List.of(v("Uma mão", "Resolva RR e LL separadamente.", -8, 5), v("Kit", "Distribua primeiro golpe na caixa e segundo no tom.", 0, 7)),
                        "compare", 52, 84, 12, "Usar doubles para preencher sem aumentar esforço.", Assessment.Type.FORMATIVE,
                        List.of("segundo-golpe", "grade", "relaxamento"), List.of()),

                d("mission-drums-kick-variation", LearningStage.BEGINNER_ADVANCED, "drum-kick-variations", "Mudar o bumbo sem mover a caixa",
                        "Comparar dois grooves alterando apenas o segundo bumbo.",
                        "Variação musical fica compreensível quando uma camada muda e as âncoras permanecem.",
                        "Chimbal em colcheias; caixa 2 e 4; bumbo A em 1 e 3, B em 1 e 'e' de 3; 68 BPM.",
                        List.of("Toque o groove A por quatro compassos e cante o bumbo.", "Mantenha mãos e desloque somente o segundo bumbo.", "Alterne A e B a cada quatro compassos e descreva a mudança de sensação."),
                        "Quatro alternâncias preservam backbeat e colocam o segundo bumbo na posição prevista.",
                        List.of(v("Sem chimbal", "Compare o bumbo com palmas em 2 e 4.", -8, 5), v("Escolha", "Use A no verso e B no refrão de uma forma curta.", 0, 7)),
                        "compare", 68, 92, 12, "Escolher variação de bumbo para distinguir seções de rock.", Assessment.Type.APPLICATION,
                        List.of("ancoras", "posicao-do-bumbo", "contraste"), List.of("come-as-you-are-drums")),

                d("mission-drums-rhythm-ear", LearningStage.BEGINNER_ADVANCED, "ear-rhythm", "Ouvir, falar e tocar uma célula",
                        "Reconhecer duas células de bumbo e caixa, vocalizá-las e reproduzi-las no kit.",
                        "Percepção rítmica vira linguagem quando atravessa audição, memória corporal e execução.",
                        "Células de um compasso; chimbal de referência; opções diferem em um único ataque.",
                        List.of("Ouça sem tocar e marque o pulso no pé.", "Vocalize 'bum' e 'tá' nos pontos ouvidos.", "Reproduza no kit e aplique a célula por quatro compassos com chimbal."),
                        "Oito de dez reconhecimentos e três reproduções preservam posição e duração.",
                        List.of(v("Palmas", "Use duas células de caixa antes de combinar o bumbo.", 0, 5), v("Antecipação", "Ouça a primeira metade e complete a segunda antes da resposta.", 0, 7)),
                        "listen", 0, 0, 11, "Retirar o groove principal de uma gravação simples.", Assessment.Type.TRANSFER,
                        List.of("reconhecimento", "vocalizacao", "reproducao"), List.of("another-one-bites-dust-drums")),

                d("mission-drums-two-grooves", LearningStage.BEGINNER_ADVANCED, "drum-two-grooves", "Trocar de groove na fronteira da seção",
                        "Alternar duas células de bumbo a cada quatro compassos sem anunciar a troca com aceleração.",
                        "Repertório exige manter forma e mudar textura no ponto certo, não colecionar grooves isolados.",
                        "Forma A4–B4; backbeat fixo; bumbo simples em A e sincopado em B; 72 BPM.",
                        List.of("Conte oito compassos sem tocar e marque a fronteira.", "Estabilize A e B separadamente com o mesmo chimbal.", "Conecte A4–B4–A4 e use crash somente na entrada de B."),
                        "Duas formas completas mudam no compasso correto e retornam a A sem pausa.",
                        List.of(v("A2–B2", "Use duas medidas por seção.", -8, 6), v("Sem crash", "Mostre a mudança apenas pelo bumbo e dinâmica.", 0, 8)),
                        "play_along", 72, 96, 14, "Acompanhar verso e refrão simplificados de Zombie.", Assessment.Type.PERFORMANCE,
                        List.of("forma", "transicao", "continuidade"), List.of("zombie-drums")),

                d("mission-drums-triplet-control", LearningStage.EARLY_INTERMEDIATE, "drum-triplet-control", "Distribuir tercinas entre mão e pé",
                        "Executar R–L–K em tercinas sem transformar o bumbo em novo pulso.",
                        "Distribuição ternária prepara shuffle, viradas e coordenação sem depender de velocidade.",
                        "Pad e bumbo; 54 BPM; uma tercina por pulso; acento no primeiro elemento.",
                        List.of("Fale 'tri-po-let' e toque só R–L no pad.", "Acrescente o bumbo no terceiro espaço sem atrasar o próximo R.", "Orquestre R na caixa, L no tom e mantenha o pé idêntico."),
                        "Quatro compassos apresentam três espaços iguais e retorno estável ao primeiro golpe.",
                        List.of(v("Um pulso", "Faça uma célula e deixe três pulsos de descanso.", -8, 5), v("Acento deslocado", "Acentue L mantendo o agrupamento ternário.", 0, 7)),
                        "guided", 54, 84, 12, "Criar uma virada ternária que retorna ao groove.", Assessment.Type.APPLICATION,
                        List.of("subdivisao-ternaria", "distribuicao", "retorno"), List.of()),

                d("mission-drums-harmonic-landmarks", LearningStage.EARLY_INTERMEDIATE, "drum-harmonic-landmarks", "Ouvir a harmonia para orientar transições",
                        "Reconhecer chegada, preparação e retorno em uma progressão e escolher onde abrir o groove.",
                        "Bateristas não tocam acordes, mas precisam ouvir sua direção para sustentar forma e dinâmica.",
                        "Loop I–IV–V–I; groove simples; quatro compassos por função; sem virada inicial.",
                        List.of("Ouça a progressão e aponte onde a tensão pede retorno.", "Toque chimbal fechado no repouso e ride na preparação sem mudar BPM.", "Crie uma virada de um tempo apenas antes do retorno ao I."),
                        "Três ciclos colocam textura e virada nos marcos harmônicos corretos.",
                        List.of(v("Só dinâmica", "Marque as funções variando volume sem trocar peças.", -8, 6), v("Forma surpresa", "Identifique o retorno sem contagem visual.", 0, 8)),
                        "context", 68, 92, 14, "Acompanhar o loop original I–IV–V–I sem depender de uma guia de bateria.", Assessment.Type.APPLICATION,
                        List.of("funcao-ouvida", "textura", "retorno"), List.of()),

                d("mission-drums-funk-pocket", LearningStage.INTERMEDIATE, "drum-funk-groove", "Construir pocket com espaços e ghost notes",
                        "Sustentar groove sincopado mantendo backbeat forte e notas fantasmas realmente leves.",
                        "Funk depende da relação de pesos e espaços; adicionar notas sem hierarquia destrói o pocket.",
                        "Semicolcheias no chimbal; caixa em 2 e 4; ghost no 'a' de 2; bumbo sincopado; 72 BPM.",
                        List.of("Toque backbeat e bumbo sem ghost notes.", "Acrescente a ghost isolada a uma altura menor de baqueta.", "Grave oito compassos e compare volume da ghost com 2 e 4."),
                        "Oito compassos mantêm hierarquia de caixa, grade de semicolcheias e bumbo estável.",
                        List.of(v("Sem ghost", "Resolva bumbo e backbeat antes da nota leve.", -12, 6), v("Chimbal variável", "Abra levemente no 'e' de 4 sem cobrir a caixa.", 0, 8)),
                        "record", 72, 100, 15, "Aplicar pocket dançante a Black or White.", Assessment.Type.PERFORMANCE,
                        List.of("hierarquia", "grade", "pocket"), List.of("black-or-white-drums")),

                d("mission-drums-fill-improv", LearningStage.INTERMEDIATE, "drum-fill-improvisation", "Responder à frase sem perder o um",
                        "Criar três viradas diferentes para a mesma transição e retornar ao groove.",
                        "Improvisar virada é variar contorno e orquestração dentro de uma função formal clara.",
                        "Três compassos de groove + um tempo de virada; 76 BPM; células de semicolcheias conhecidas.",
                        List.of("Cante uma resposta rítmica para o final da frase musical.", "Toque a primeira versão apenas na caixa e confirme o retorno.", "Mude a orquestração e depois a última célula, preservando tamanho e chegada."),
                        "Três versões distinguíveis terminam antes do bumbo no primeiro tempo.",
                        List.of(v("Duas colcheias", "Use só duas notas no tempo 4.", -8, 6), v("Silêncio final", "Deixe a última semicolcheia vazia para ampliar a chegada.", 0, 8)),
                        "create", 76, 104, 15, "Escolher viradas diferentes para verso, refrão e final.", Assessment.Type.APPLICATION,
                        List.of("frase", "variedade", "retorno-no-um"), List.of("enter-sandman-drums")),

                d("mission-drums-performance", LearningStage.EARLY_INTERMEDIATE, "performance", "Atravessar a música preservando a função",
                        "Tocar uma forma completa e recuperar erro mantendo ao menos pulso e backbeat.",
                        "Performance de bateria prioriza sustentação da banda; recomeçar é mais disruptivo que simplificar.",
                        "Highway to Hell simplificada; mapa de seções; uma tomada contínua; viradas opcionais.",
                        List.of("Marque entradas e dois pontos de reencontro no mapa.", "Defina groove mínimo de chimbal e caixa para usar em recuperação.", "Grave a forma e, após qualquer erro, simplifique até reencontrar a seção."),
                        "A música chega ao fim, seções permanecem reconhecíveis e recuperação ocorre em até um compasso.",
                        List.of(v("Verso e refrão", "Execute duas seções conectadas.", -8, 8), v("Sem mapa", "Faça a segunda tomada usando apenas contagem interna.", 0, 10)),
                        "play_along", 76, 116, 17, "Sustentar uma banda durante uma versão completa.", Assessment.Type.PERFORMANCE,
                        List.of("forma", "funcao", "recuperacao"), List.of("highway-to-hell-drums")),

                d("mission-drums-fill-review", LearningStage.BEGINNER, "drum-one-beat-fill", "Recuperar a virada em outra orquestração",
                        "Reconstruir uma virada de um tempo sem partitura e voltar ao groove após mudar sua rota pelo kit.",
                        "A revisão testa tamanho e retorno preservados; copiar novamente a grade não comprovaria recuperação.",
                        "Sem notação; três compassos de groove + tempo 4; 64 BPM; caixa e tons disponíveis.",
                        List.of("Vocalize quatro ataques e execute primeiro somente na caixa.", "Insira após três compassos e confirme o bumbo no 1 sem reiniciar.", "Mude a rota para caixa–tom–surdo–tom mantendo a mesma duração."),
                        "Quatro formas completas terminam a virada antes do primeiro tempo e preservam o groove.",
                        List.of(v("Duas notas", "Use duas colcheias no tempo 4.", -8, 6), v("Rota surpresa", "Escolha a sequência de peças somente antes da tomada.", 0, 8)),
                        "review", 64, 88, 12, "Usar a virada recuperada na transição de outra música.", Assessment.Type.RETENTION,
                        List.of("recuperacao", "duracao", "retorno-no-um"), List.of("i-love-rock-n-roll-drums")),

                d("mission-drums-groove-composition", LearningStage.UPPER_INTERMEDIATE, "drum-groove-composition", "Compor um groove a partir da linha de baixo",
                        "Criar groove de oito compassos que dialoga com acentos de uma linha de baixo.",
                        "Composição de bateria organiza suporte e contraste; copiar todos os acentos reduz independência e espaço.",
                        "Baixo original em Em; forma A4–B4; backbeat definido; até duas variações de bumbo.",
                        List.of("Cante a linha e marque os acentos estruturais.", "Escolha quais acentos o bumbo reforça e quais o chimbal responde.", "Crie B mudando uma camada e conecte a forma com virada curta."),
                        "A forma mantém pocket, dialoga com o baixo e diferencia A de B por uma decisão clara.",
                        List.of(v("Quatro compassos", "Crie somente A com uma variação final.", -8, 8), v("Contraste", "Faça B em half-time sem mudar o BPM.", 0, 11)),
                        "create", 72, 104, 18, "Registrar um groove autoral pronto para composição.", Assessment.Type.APPLICATION,
                        List.of("relacao-com-baixo", "forma", "contraste"), List.of()),

                d("mission-drums-linear-displacement", LearningStage.UPPER_INTERMEDIATE, "drum-linear-displacement", "Deslocar uma célula sem perder a barra",
                        "Mover K–R–L por diferentes semicolcheias mantendo caixa de referência em 2 e 4.",
                        "Deslocamento cria tensão métrica; uma âncora audível impede que a célula redefina o compasso.",
                        "Célula K–R–L; grade de semicolcheias; 64 BPM; chimbal no pé em semínimas.",
                        List.of("Toque a célula começando em 1 e identifique o espaço vazio.", "Desloque para 'e' de 1 mantendo o pé como referência.", "Aplique por um compasso e retorne a groove com caixa em 2 e 4."),
                        "Três deslocamentos ocupam posições corretas e retornam ao groove no primeiro tempo.",
                        List.of(v("Duas peças", "Use K–R antes da célula completa.", -12, 7), v("Orquestração", "Mova R e L entre caixa e tons sem mudar a grade.", 0, 10)),
                        "guided", 64, 96, 16, "Criar transição linear que mantém orientação métrica.", Assessment.Type.TRANSFER,
                        List.of("grade", "deslocamento", "ancora"), List.of()),

                d("mission-drums-odd-meter", LearningStage.UPPER_INTERMEDIATE, "drum-odd-meter", "Construir 7/8 como 2+2+3",
                        "Sustentar groove em 7/8 e variar o bumbo sem perder o agrupamento.",
                        "Compasso ímpar fica musical quando agrupado em gestos audíveis, não quando contado como sequência abstrata.",
                        "7/8 agrupado 2+2+3; chimbal em colcheias; caixa no início do segundo grupo; 60 BPM de semínima pontuada ajustada.",
                        List.of("Fale 'dois-dois-três' e acentue o início de cada grupo.", "Adicione caixa e bumbo preservando o ciclo de sete ataques.", "Mude um bumbo dentro do último grupo e volte à forma original."),
                        "Oito ciclos preservam agrupamento e retorno ao primeiro ataque.",
                        List.of(v("Palmas", "Marque apenas os três acentos do agrupamento.", -8, 6), v("Outra soma", "Reorganize como 3+2+2 e compare a sensação.", 0, 9)),
                        "compare", 60, 84, 16, "Criar groove em métrica ímpar com fraseado reconhecível.", Assessment.Type.APPLICATION,
                        List.of("agrupamento", "ciclo", "variacao"), List.of()),

                d("mission-drums-polyrhythm", LearningStage.ADVANCED, "drum-polyrhythm", "Sustentar três contra dois no kit",
                        "Manter duas pulsações simultâneas e retornar ao groove comum sem recalcular o tempo.",
                        "Polirritmia amplia independência e percepção de ciclos; o objetivo é ouvir convergência, não executar fórmula mecânica.",
                        "Pé em dois ataques por ciclo; mãos em três; clique marca a convergência; 52 BPM.",
                        List.of("Cante as duas camadas e marque somente pontos de encontro.", "Fixe o pé em dois e acrescente três palmas antes das baquetas.", "Orquestre as mãos e conecte dois ciclos a um groove 4/4 simples."),
                        "Quatro ciclos mantêm camadas distinguíveis e convergem no ponto previsto.",
                        List.of(v("Corpo", "Use pé e palmas sem kit.", -8, 7), v("Orquestração", "Distribua a camada de três entre caixa e tom.", 0, 10)),
                        "guided", 52, 76, 18, "Usar um ciclo polirrítmico como textura e resolver em 4/4.", Assessment.Type.TRANSFER,
                        List.of("camadas", "convergencia", "resolucao"), List.of()),

                d("mission-drums-style-transfer", LearningStage.ADVANCED, "drum-style-adaptation", "Adaptar a mesma forma a três estilos",
                        "Tocar uma forma A–B em rock, funk e half-time preservando marcos e função.",
                        "Fluência estilística muda condução, articulação e subdivisão sem perder a arquitetura da música.",
                        "Forma de dezesseis compassos; mesmo BPM; três versões; virada apenas na fronteira.",
                        List.of("Faça a versão rock com colcheias e backbeat aberto.", "Na versão funk, use semicolcheias leves e espaço no bumbo.", "Em half-time, mova a caixa para 3 e preserve duração das seções."),
                        "As três versões mantêm forma e pulso, mas apresentam pocket e articulação coerentes.",
                        List.of(v("Dois estilos", "Compare rock e half-time antes do funk.", -8, 9), v("Medley", "Troque de estilo a cada oito compassos sem parar.", 0, 13)),
                        "transfer", 76, 112, 21, "Responder a mudanças de arranjo em contexto de banda.", Assessment.Type.PERFORMANCE,
                        List.of("forma", "linguagem", "adaptacao", "continuidade"), List.of())
        );
    }

    private static EditorialMissionDefinition d(String id, LearningStage stage, String competencyId,
                                                  String title, String objective, String purpose, String conditions,
                                                  List<String> instructions, String success,
                                                  List<ExerciseVariation> variations, String activityType,
                                                  int currentBpm, int targetBpm, int minutes, String application,
                                                  Assessment.Type assessmentType, List<String> criteria,
                                                  List<String> repertoire) {
        return new EditorialMissionDefinition(id, DRUMS, stage, competencyId, title, objective, purpose,
                conditions, instructions, success, variations, activityType, currentBpm, targetBpm, minutes,
                application, assessmentType, criteria, repertoire);
    }

    private static ExerciseVariation v(String name, String instruction, int bpmOffset, int minutes) {
        return new ExerciseVariation(name, instruction, bpmOffset, minutes);
    }
}
