package com.musicos.config;

import static com.musicos.domain.InstrumentId.GUITAR;

import com.musicos.domain.Assessment;
import com.musicos.domain.ExerciseVariation;
import com.musicos.domain.LearningStage;
import java.util.List;

/** Jornada editorial de guitarra: controle de cordas, linguagem de riffs e lead. */
final class GuitarEditorialCatalog {
    private GuitarEditorialCatalog() {}

    static List<EditorialMissionDefinition> definitions() {
        return List.of(
                d("mission-guitar-tuning-reference", LearningStage.FIRST_STEPS, "guitar-tuning", "Afinar comparando batimentos",
                        "Ajustar as seis cordas e confirmar cada uma contra uma referência estável.",
                        "Afinar é a primeira decisão de escuta: sem referência confiável, digitação e percepção dão respostas falsas.",
                        "Guitarra em som limpo; referência E–A–D–G–B–E; uma corda por vez, sem tocar acordes.",
                        List.of("Ouça a referência e cante se sua corda está abaixo ou acima.", "Gire a tarraxa em pequenos passos até os batimentos desacelerarem.", "Volte à primeira corda e confira se a tensão das demais alterou a afinação."),
                        "As seis cordas ficam centradas e a segunda conferência não exige correção ampla.",
                        List.of(v("Duas cordas", "Compare somente E grave e A, dizendo a direção antes de ajustar.", 0, 4), v("Acorde de controle", "Depois de afinar, toque Em e investigue qualquer oscilação.", 0, 5)),
                        "listen", 0, 0, 8, "Preparar uma tomada limpa do primeiro riff sem mascarar desafinação.", Assessment.Type.FORMATIVE,
                        List.of("direcao-da-correcao", "estabilidade", "segunda-conferencia"), List.of()),

                d("mission-guitar-pitch-direction", LearningStage.FIRST_STEPS, "ear-pitch", "Encontrar a direção antes da casa",
                        "Ouvir se a segunda nota sobe ou desce e reproduzir a direção em uma corda.",
                        "A mão encontra notas com menos tentativa quando o ouvido antecipa o movimento melódico.",
                        "Pares de duas notas; corda B; distância de uma a três casas; resposta cantada antes de tocar.",
                        List.of("Ouça o par sem olhar o braço e indique subida, descida ou repetição.", "Cante a segunda nota e deslize um dedo até encontrá-la.", "Repita o contorno a partir de outra casa sem copiar números."),
                        "Oito de dez contornos são identificados e seis são reproduzidos na direção correta.",
                        List.of(v("Contraste largo", "Use saltos de três casas para tornar a direção evidente.", 0, 4), v("Contorno triplo", "Reproduza subir–subir, subir–descer e descer–subir.", 0, 5)),
                        "listen", 0, 0, 8, "Retirar de ouvido o contorno inicial de um riff curto.", Assessment.Type.FORMATIVE,
                        List.of("direcao-ouvida", "antecipacao-vocal", "reproducao"), List.of("seven-nation-army-guitar")),

                d("mission-guitar-rhythm-reading", LearningStage.BEGINNER, "rhythm-reading", "Ler pausas dentro de uma tablatura rítmica",
                        "Executar dois compassos com semínimas, colcheias e pausas sem encurtar os silêncios.",
                        "Tablatura informa onde tocar; a leitura rítmica determina quando a nota e o silêncio ocupam espaço.",
                        "Uma nota abafada; 4/4 a 64 BPM; grade de dois compassos lida antes de tocar.",
                        List.of("Bata e conte o ritmo sem guitarra, incluindo as pausas.", "Toque apenas os ataques dos tempos numerados e mantenha a palheta em movimento no silêncio.", "Leia um compasso à frente enquanto executa o anterior."),
                        "Três leituras contínuas com ataques e pausas nos mesmos pontos da grade.",
                        List.of(v("Só corpo", "Marque o ritmo com palmas e pé antes de usar a palheta.", -8, 4), v("Duas alturas", "Distribua os ataques entre E e G sem alterar a duração.", 0, 5)),
                        "notation", 64, 84, 9, "Aplicar a célula rítmica a uma base simplificada de Black or White.", Assessment.Type.APPLICATION,
                        List.of("duracao", "pausas", "continuidade-da-leitura"), List.of("black-or-white-guitar")),

                d("mission-guitar-interval-shapes", LearningStage.BEGINNER, "intervals", "Ouvir e localizar oitava e quinta",
                        "Distinguir quinta e oitava e encontrar as duas relações a partir de A no braço.",
                        "Intervalos conectam o mapa do braço ao som de power chords, melodias e transposição.",
                        "Som limpo; referência A; pares isolados; nomes revelados somente após a resposta auditiva.",
                        List.of("Compare A–E e A–A e descreva qual par parece mais aberto.", "Encontre a quinta em outra corda usando o som antes de conferir a forma.", "Mova as duas relações para C e explique o que permaneceu igual."),
                        "Seis comparações corretas e duas transposições sem perder a qualidade do intervalo.",
                        List.of(v("Som antes do nome", "Responda apenas 'quinta' ou 'oitava' em pares contrastantes.", 0, 4), v("Mapa móvel", "Parta de três tônicas diferentes escolhidas ao acaso.", 0, 6)),
                        "compare", 0, 0, 10, "Usar a quinta para construir um power chord e a oitava para dobrar um riff.", Assessment.Type.TRANSFER,
                        List.of("qualidade-ouvida", "localizacao", "transposicao"), List.of()),

                d("mission-guitar-power-chord-gate", LearningStage.BEGINNER_ADVANCED, "power-chords", "Mover power chords sem arrastar ruído",
                        "Alternar E5, G5 e A5 com ataques curtos e silêncios realmente limpos.",
                        "Power chord convincente depende do contraste entre impacto e silêncio, não apenas da forma correta.",
                        "Drive moderado; colcheias a 72 BPM; dois tempos por acorde; cordas não usadas abafadas.",
                        List.of("Monte E5 e teste separadamente nota fundamental e quinta.", "Toque dois ataques e interrompa o som com as duas mãos no 'e' de 2.", "Percorra E5–G5–A5–G5 sem deslizar pressão entre as posições."),
                        "Quatro ciclos com duas notas claras por acorde e nenhum ruído sustentado nas pausas.",
                        List.of(v("Uma mudança", "Alterne E5 e G5 com pausa de dois tempos.", -12, 4), v("Acentos", "Mantenha colcheias e acentue apenas a primeira de cada forma.", 0, 6)),
                        "guided", 72, 96, 10, "Construir uma base curta inspirada em riffs de arena rock.", Assessment.Type.APPLICATION,
                        List.of("forma", "muting", "chegada-na-posicao"), List.of("smoke-on-the-water-guitar")),

                d("mission-guitar-rhythm-grid", LearningStage.BEGINNER_ADVANCED, "rhythm-guitar", "Sustentar a grade enquanto os acordes mudam",
                        "Tocar oito compassos de power chords preservando colcheias e acentos em 2 e 4.",
                        "A guitarra rítmica precisa manter a função da banda mesmo quando a mão esquerda muda de região.",
                        "E5–G5–A5–G5; 76 BPM; colcheias; acentos em 2 e 4; sem reiniciar após erro isolado.",
                        List.of("Abafe as cordas e fixe a grade com os acentos.", "Troque os acordes somente no tempo 1 sem alterar a mão direita.", "Grave oito compassos e compare a distância entre ataques antes e depois das trocas."),
                        "Oito compassos contínuos, acentos audíveis e primeira colcheia de cada acorde alinhada.",
                        List.of(v("Sem acorde", "Faça a mão direita completa em cordas abafadas.", -12, 4), v("Síncope final", "Antecipe apenas o último acorde no 'e' de 4.", 0, 6)),
                        "play_along", 76, 104, 11, "Acompanhar a base simplificada de Highway to Hell sem preencher os espaços.", Assessment.Type.PERFORMANCE,
                        List.of("grade", "acentos", "continuidade"), List.of("highway-to-hell-guitar")),

                d("mission-guitar-song-sections", LearningStage.BEGINNER_ADVANCED, "song-sections", "Mapear uma música antes de tocar tudo",
                        "Reconhecer introdução, verso e refrão e executar uma entrada distinta para cada seção.",
                        "Dividir a forma impede que o estudo vire repetição do começo e permite decisões técnicas por seção.",
                        "Gravação de Zombie; folha com oito blocos vazios; guitarra usada apenas nas entradas marcadas.",
                        List.of("Ouça e marque cada mudança de seção sem tocar.", "Escolha um acorde ou ataque que identifique a entrada de cada bloco.", "Atravesse a forma tocando somente as entradas e contando os compassos entre elas."),
                        "O mapa coincide com a gravação e todas as entradas chegam sem antecipação.",
                        List.of(v("Duas seções", "Mapeie apenas verso e refrão.", 0, 5), v("Forma sem áudio", "Reconstrua o mapa depois de uma pausa sem consultar anotações.", 0, 6)),
                        "analyze", 0, 0, 10, "Usar o mapa para planejar estudos separados de verso e refrão.", Assessment.Type.APPLICATION,
                        List.of("forma", "contagem-de-compassos", "entradas"), List.of("zombie-guitar")),

                d("mission-guitar-string-control", LearningStage.EARLY_INTERMEDIATE, "muting-control", "Separar nota, ataque e silêncio",
                        "Executar um riff em três cordas com cada nota cercada por silêncio controlado.",
                        "Ruído entre cordas encobre articulação e torna velocidade aparente menos musical.",
                        "Som com ganho moderado; riff E–A–D; 68 BPM; pausas de colcheia depois de cada grupo.",
                        List.of("Toque a frase sem ganho e identifique qual corda vibra por simpatia.", "Use a mão esquerda para interromper a nota e a direita para bloquear cordas graves.", "Ative o ganho e preserve exatamente as mesmas pausas."),
                        "Três tomadas em que apenas as notas escritas permanecem audíveis.",
                        List.of(v("Duas cordas", "Retire a corda D e isole o mecanismo de abafamento.", -12, 5), v("Troca ampla", "Salte de E para D sem roçar a corda A.", 0, 6)),
                        "record", 68, 96, 11, "Limpar a introdução de Come As You Are sem alterar seu contorno.", Assessment.Type.PERFORMANCE,
                        List.of("ruido-residual", "duracao", "controle-entre-cordas"), List.of("come-as-you-are-guitar")),

                d("mission-guitar-riff-variation", LearningStage.EARLY_INTERMEDIATE, "riff-vocabulary", "Variar um riff sem perder sua identidade",
                        "Criar duas variações de um motivo mantendo contorno ou ritmo reconhecível.",
                        "Vocabulário nasce ao transformar uma ideia com limites claros, não ao acumular licks desconectados.",
                        "Motivo original de dois compassos em Em; pentatônica menor; uma variável alterada por volta.",
                        List.of("Toque o motivo e cante o fragmento que o torna reconhecível.", "Mantenha as alturas e mude apenas o final rítmico.", "Mantenha o ritmo e troque duas alturas dentro da pentatônica."),
                        "As duas variações são diferentes entre si e ainda permitem reconhecer o motivo-base.",
                        List.of(v("Uma nota", "Altere somente a última nota do motivo.", -8, 5), v("Resposta", "Use uma variação como pergunta e outra como resposta.", 0, 7)),
                        "create", 72, 96, 12, "Montar uma introdução de quatro compassos com motivo e resposta.", Assessment.Type.APPLICATION,
                        List.of("identidade-do-motivo", "variacao-controlada", "forma"), List.of("come-as-you-are-guitar")),

                d("mission-guitar-major-minor-ear", LearningStage.EARLY_INTERMEDIATE, "ear-chords", "Distinguir tríades maiores e menores no braço",
                        "Reconhecer a qualidade de pares de tríades e reproduzir uma delas em três cordas.",
                        "Ouvir a terça orienta escolha de acorde e frase antes de qualquer análise escrita.",
                        "Pares C/Cm, G/Gm e A/Am; tríades em grupos de três cordas; resposta sem cifra visível.",
                        List.of("Ouça cada par e cante a nota que mudou.", "Classifique maior ou menor e só então revele a forma.", "Escolha uma tônica, monte as duas qualidades e compare a terça isolada."),
                        "Oito de dez qualidades corretas e duas construções com terça adequada.",
                        List.of(v("Contraste fixo", "Use apenas C e Cm até localizar a terça pelo som.", 0, 5), v("Inversões", "Repita a comparação com a terça no baixo.", 0, 7)),
                        "compare", 0, 0, 11, "Escolher a qualidade adequada para harmonizar uma frase curta.", Assessment.Type.TRANSFER,
                        List.of("qualidade", "terca", "reproducao"), List.of()),

                d("mission-guitar-phrase-breath", LearningStage.EARLY_INTERMEDIATE, "phrasing", "Fazer a frase respirar sobre dois acordes",
                        "Improvisar quatro compassos com uma pausa planejada e chegada em nota do acorde.",
                        "Fraseado organiza atenção, silêncio e direção; tocar a escala inteira não cria discurso.",
                        "Loop Am–F; pentatônica de Am; 72 BPM; máximo de cinco notas por compasso.",
                        List.of("Cante uma pergunta curta e marque onde ficará o silêncio.", "Toque a pergunta sem preencher a pausa.", "Responda chegando em A sobre Am ou C sobre F e repita a forma."),
                        "Duas tomadas preservam a pausa e chegam conscientemente em nota do acorde.",
                        List.of(v("Duas notas", "Use somente A e C para construir pergunta e resposta.", -8, 5), v("Mudança de registro", "Repita a resposta uma oitava acima sem mudar o ritmo.", 0, 7)),
                        "create", 72, 96, 12, "Criar um solo curto com começo, espaço e resolução.", Assessment.Type.APPLICATION,
                        List.of("pausa", "direcao", "nota-alvo"), List.of()),

                d("mission-guitar-position-shift", LearningStage.INTERMEDIATE, "position-shifts", "Mudar de região durante a frase",
                        "Conectar duas posições da pentatônica com slide afinado e pulso contínuo.",
                        "Troca de posição útil acontece dentro de uma frase e preserva som, tempo e destino.",
                        "Am pentatônica nas posições 5 e 8; 76 BPM; slide no quarto tempo; som limpo.",
                        List.of("Toque a nota de partida e a de chegada separadamente e cante o destino.", "Deslize sem apertar mais a mão e pare exatamente na casa-alvo.", "Conecte dois compassos, um em cada posição, sem acentuar a mudança."),
                        "Quatro conexões chegam afinadas, no pulso e sem ruído entre regiões.",
                        List.of(v("Uma corda", "Faça a troca somente na corda B.", -12, 5), v("Rota alternativa", "Escolha outra corda para chegar à mesma nota-alvo.", 0, 7)),
                        "guided", 76, 108, 12, "Ampliar um solo além de um único desenho de pentatônica.", Assessment.Type.PERFORMANCE,
                        List.of("destino", "tempo-da-troca", "continuidade"), List.of()),

                d("mission-guitar-articulation-dialogue", LearningStage.INTERMEDIATE, "articulation-combinations", "Contrastar ataque, legato e slide",
                        "Tocar o mesmo motivo com três articulações e escolher uma para a resposta.",
                        "Articulação muda intenção sem mudar nota; combinar técnicas só faz sentido quando o contraste é audível.",
                        "Motivo de quatro notas em Am; 68 BPM; versões palhetada, hammer/pull e slide.",
                        List.of("Grave o motivo todo palhetado como referência.", "Substitua apenas o segundo ataque por hammer-on e compare o contorno.", "Crie uma resposta usando slide somente na nota de chegada."),
                        "As três versões mantêm ritmo e alturas, mas apresentam articulações distinguíveis.",
                        List.of(v("Dois gestos", "Compare apenas palhetada e hammer-on.", -8, 5), v("Combinação", "Use legato na pergunta e slide na resposta.", 0, 7)),
                        "compare", 68, 96, 12, "Editar a articulação de um solo curto de acordo com sua função.", Assessment.Type.APPLICATION,
                        List.of("contraste", "clareza", "escolha-musical"), List.of()),

                d("mission-guitar-performance-map", LearningStage.EARLY_INTERMEDIATE, "performance", "Atravessar uma música com plano de recuperação",
                        "Executar uma forma completa e recuperar um erro sem abandonar pulso ou seção.",
                        "Performance mede continuidade, memória da forma e decisão sob pressão, não ausência absoluta de erros.",
                        "Zombie em versão simplificada; mapa de seções disponível; uma tomada sem reinício.",
                        List.of("Marque no mapa dois pontos seguros para reencontro.", "Toque a forma e, se perder uma troca, preserve a mão direita até o próximo ponto.", "Revise a gravação e identifique se a recuperação aconteceu dentro da seção correta."),
                        "A forma chega ao fim, as seções permanecem reconhecíveis e qualquer recuperação ocorre em até um compasso.",
                        List.of(v("Duas seções", "Execute apenas verso e refrão sem parar.", -8, 6), v("Sem mapa", "Faça uma segunda tomada sem olhar a folha de forma.", 0, 8)),
                        "record", 76, 84, 14, "Registrar uma versão simplificada completa de Zombie.", Assessment.Type.PERFORMANCE,
                        List.of("forma", "continuidade", "recuperacao"), List.of("zombie-guitar")),

                d("mission-guitar-lead-form", LearningStage.UPPER_INTERMEDIATE, "lead-guitar-language", "Construir um solo que acompanha a forma",
                        "Improvisar sobre três seções mudando densidade e registro sem perder motivos anteriores.",
                        "Lead guitar precisa narrar a harmonia e a forma; intensidade constante transforma tudo em uma longa frase.",
                        "Forma A–A–B de doze compassos em Am; pentatônica, arpejos e duas regiões do braço.",
                        List.of("Defina um motivo curto para a primeira seção A.", "Na repetição, varie apenas o final e suba o registro.", "Na seção B, use menos notas e chegue a uma nota do acorde final."),
                        "O solo distingue A e B, reapresenta o motivo e encerra junto com a forma.",
                        List.of(v("A–B curto", "Use quatro compassos por seção e um único motivo.", -12, 7), v("Clímax", "Reserve o registro mais agudo para a segunda metade de B.", 0, 9)),
                        "play_along", 72, 108, 16, "Criar um solo autoral sobre uma backing track em Am.", Assessment.Type.PERFORMANCE,
                        List.of("motivo", "forma", "arco-dinamico"), List.of()),

                d("mission-guitar-transcription", LearningStage.UPPER_INTERMEDIATE, "transcription", "Retirar um riff em ciclos de escuta",
                        "Transcrever ritmo e alturas de um riff de quatro compassos sem busca aleatória no braço.",
                        "Transcrição integra ouvido, memória, mapa do instrumento e verificação consciente.",
                        "Trecho curto de Come As You Are; reprodução em velocidade reduzida; papel para ritmo e notas.",
                        List.of("Ouça o trecho inteiro e cante apenas o ritmo.", "Localize a primeira nota e resolva uma célula por vez sem tocar junto.", "Conecte as células, grave e compare contorno, entradas e durações."),
                        "A versão contém o mesmo número de eventos, contorno e pontos de entrada do trecho de referência.",
                        List.of(v("Dois compassos", "Transcreva somente a primeira célula e sua repetição.", 0, 7), v("Outra posição", "Transfira o riff para uma região diferente mantendo alturas relativas.", 0, 9)),
                        "listen", 0, 0, 16, "Adicionar ao repertório uma versão estudável construída pelo ouvido.", Assessment.Type.TRANSFER,
                        List.of("ritmo", "contorno", "verificacao"), List.of("come-as-you-are-guitar")),

                d("mission-guitar-riff-review", LearningStage.EARLY_INTERMEDIATE, "riff-vocabulary", "Recuperar e transformar um riff sem tablatura",
                        "Reconstruir um motivo conhecido e criar uma variação depois de retirar todas as referências visuais.",
                        "A revisão verifica acesso ao vocabulário e transferência; reler a aula apenas repetiria reconhecimento.",
                        "Material fechado; 68 BPM; uma tomada de memória, uma conferência curta e uma nova tomada.",
                        List.of("Cante o contorno e toque o riff sem consultar casas ou digitação.", "Confira apenas o primeiro ponto divergente e feche novamente o material.", "Repita e altere somente o final rítmico, preservando a identidade."),
                        "A segunda tomada recupera o motivo e a variação mantém contorno ou ritmo reconhecível.",
                        List.of(v("Primeira célula", "Recupere apenas o primeiro compasso antes da forma inteira.", -8, 6), v("Outra região", "Leve o motivo a uma posição diferente sem mudar seu ritmo.", 0, 8)),
                        "review", 68, 96, 12, "Reutilizar vocabulário antigo como material de uma nova introdução.", Assessment.Type.RETENTION,
                        List.of("recuperacao", "identidade", "transferencia"), List.of("come-as-you-are-guitar")),

                d("mission-guitar-arrangement", LearningStage.UPPER_INTERMEDIATE, "arrangement", "Distribuir funções entre duas guitarras",
                        "Criar duas partes complementares para uma progressão sem duplicar registro e ritmo.",
                        "Arranjo transforma conhecimento harmônico em escolhas de espaço, textura e função.",
                        "Progressão Am–F–C–G; duas gravações; uma base rítmica e uma resposta melódica.",
                        List.of("Grave a base em registro grave com ritmo estável e espaços definidos.", "Crie uma resposta aguda que apareça apenas nos espaços da base.", "Sobreponha as tomadas e retire qualquer nota que encubra a mudança de acorde."),
                        "As duas partes permanecem reconhecíveis, não competem ritmicamente e deixam as mudanças harmônicas audíveis.",
                        List.of(v("Uma resposta", "Crie uma única frase aguda no final de quatro compassos.", -8, 8), v("Inversões", "Substitua a base por tríades em outra região.", 0, 10)),
                        "create", 72, 96, 18, "Produzir um arranjo original de oito compassos com duas camadas.", Assessment.Type.APPLICATION,
                        List.of("funcao-das-camadas", "espaco", "clareza-harmonica"), List.of()),

                d("mission-guitar-style-transfer", LearningStage.ADVANCED, "guitar-style-adaptation", "Manter a frase e mudar a linguagem",
                        "Adaptar o mesmo motivo a rock, funk e balada alterando articulação, dinâmica e espaço.",
                        "Fluência estilística é reconhecer quais parâmetros carregam a linguagem sem perder identidade musical.",
                        "Motivo de dois compassos; três playbacks no mesmo tom; andamento ajustado a cada estilo.",
                        List.of("No rock, use ataque firme e sustentações controladas.", "No funk, encurte notas e transforme espaços em parte do groove.", "Na balada, reduza densidade, amplie dinâmica e preserve o contorno do motivo."),
                        "As três versões mantêm o motivo identificável e apresentam diferenças estilísticas coerentes.",
                        List.of(v("Dois estilos", "Compare rock e funk antes de incluir a balada.", -8, 8), v("Forma completa", "Adapte também a resposta e a transição entre seções.", 0, 12)),
                        "transfer", 84, 120, 20, "Preparar o mesmo material para três contextos de banda.", Assessment.Type.TRANSFER,
                        List.of("identidade", "linguagem", "adaptacao"), List.of())
        );
    }

    private static EditorialMissionDefinition d(String id, LearningStage stage, String competencyId,
                                                  String title, String objective, String purpose, String conditions,
                                                  List<String> instructions, String success,
                                                  List<ExerciseVariation> variations, String activityType,
                                                  int currentBpm, int targetBpm, int minutes, String application,
                                                  Assessment.Type assessmentType, List<String> criteria,
                                                  List<String> repertoire) {
        return new EditorialMissionDefinition(id, GUITAR, stage, competencyId, title, objective, purpose,
                conditions, instructions, success, variations, activityType, currentBpm, targetBpm, minutes,
                application, assessmentType, criteria, repertoire);
    }

    private static ExerciseVariation v(String name, String instruction, int bpmOffset, int minutes) {
        return new ExerciseVariation(name, instruction, bpmOffset, minutes);
    }
}
