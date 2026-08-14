package com.musicos.config;

import static com.musicos.domain.InstrumentId.KEYS;

import com.musicos.domain.Assessment;
import com.musicos.domain.ExerciseVariation;
import com.musicos.domain.LearningStage;
import java.util.List;

/** Jornada editorial de teclado/piano: mapa visual, independência, leitura e voicings. */
final class KeysEditorialCatalog {
    private KeysEditorialCatalog() {}

    static List<EditorialMissionDefinition> definitions() {
        return List.of(
                d("mission-keys-posture-release", LearningStage.FIRST_STEPS, "piano-posture", "Alinhar banco, braço e ataque",
                        "Tocar cinco notas com apoio estável, punho livre e retorno silencioso das teclas.",
                        "No teclado, altura do banco e distância mudam peso, articulação e alcance das duas mãos.",
                        "Banco ajustável; C central; câmera lateral; ataques separados sem pedal.",
                        List.of("Ajuste a distância até os cotovelos ficarem levemente à frente do tronco.", "Deixe o braço transferir peso ao C e solte sem levantar o ombro.", "Toque C–D–E–F–G observando se punho e dedos continuam alinhados."),
                        "Três passagens com som controlado, postura repetível e ausência de tensão visível.",
                        List.of(v("Uma tecla", "Compare ataque com braço apoiado e com ombro elevado.", 0, 4), v("Duas mãos", "Repita cinco notas com cada mão sem mudar o banco.", 0, 5)),
                        "demonstrate", 0, 0, 7, "Preparar controle de ataque e deslocamentos sem compensação corporal.", Assessment.Type.FORMATIVE,
                        List.of("alinhamento", "peso", "liberacao"), List.of()),

                d("mission-keys-finger-numbers", LearningStage.FIRST_STEPS, "finger-numbers", "Trocar notas sem trocar a identidade dos dedos",
                        "Responder a sequências de números com dedos corretos em ambas as mãos.",
                        "Numeração compartilhada entre mãos organiza instruções de dedilhado sem confundir direita e esquerda.",
                        "Mãos apoiadas em cinco teclas; sequências 1–2–3–2–1 e 5–4–3–4–5; sem pulso inicial.",
                        List.of("Nomeie polegares como 1 e mínimos como 5 nas duas mãos.", "Execute a primeira sequência com a direita sem mover a posição.", "Faça a sequência espelhada na esquerda e depois alterne uma resposta por mão."),
                        "Seis sequências aleatórias usam os dedos pedidos sem olhar marcas externas.",
                        List.of(v("Três dedos", "Use somente 1–2–3 em cada mão.", 0, 4), v("Ritmo", "Toque a sequência em colcheias mantendo dedos e pulsos distintos.", 0, 5)),
                        "recall", 0, 0, 8, "Ler indicações de dedilhado em padrões e melodias.", Assessment.Type.FORMATIVE,
                        List.of("numero", "mao", "resposta"), List.of()),

                d("mission-keys-pitch-direction", LearningStage.FIRST_STEPS, "ear-pitch", "Prever para que lado a melodia caminha",
                        "Identificar subida, descida ou repetição e reproduzir o contorno em teclas vizinhas.",
                        "A geometria linear do teclado torna visível uma relação que primeiro precisa ser ouvida.",
                        "Pares e trios dentro de C–G; tela/teclas ocultas durante a escuta; voz antes da mão.",
                        List.of("Ouça sem tocar e desenhe o contorno no ar.", "Cante o segundo som e escolha uma tecla à direita, esquerda ou igual.", "Transfira o mesmo contorno para começar em G."),
                        "Oito de dez contornos e três transferências preservam a direção ouvida.",
                        List.of(v("Saltos largos", "Use terças para destacar direção.", 0, 4), v("Quatro sons", "Reproduza contorno com uma repetição interna.", 0, 6)),
                        "listen", 0, 0, 8, "Retirar de ouvido uma pequena melodia em região diferente.", Assessment.Type.TRANSFER,
                        List.of("direcao", "antecipacao", "transferencia"), List.of("ode-to-joy-keys")),

                d("mission-keys-note-landmarks-reading", LearningStage.BEGINNER, "keys-note-landmarks", "Ligar nota escrita ao relevo das teclas",
                        "Encontrar C, F e G escritos em três regiões sem contar desde uma única referência.",
                        "Leitura fluida une símbolo, registro e geometria; etiquetas permanentes atrasam essa conexão.",
                        "Cartões em clave de sol e fá; três oitavas; uma nota por resposta; sem adesivos.",
                        List.of("Identifique a clave e diga se a nota está acima ou abaixo do C central.", "Use grupos de duas e três teclas pretas para localizar a nota.", "Misture claves e regiões, nomeando antes do ataque."),
                        "Nove de doze notas chegam à região e tecla corretas sem contagem sequencial.",
                        List.of(v("Uma clave", "Use somente clave de sol na oitava central.", 0, 5), v("Duas mãos", "Responda clave de fá com esquerda e sol com direita.", 0, 6)),
                        "notation", 0, 0, 10, "Localizar pontos de apoio de uma partitura antes da leitura contínua.", Assessment.Type.FORMATIVE,
                        List.of("clave", "registro", "tecla"), List.of("ode-to-joy-keys")),

                d("mission-keys-triad-balance", LearningStage.BEGINNER, "triads", "Construir tríades e ouvir cada voz",
                        "Montar C, F e G maiores e equilibrar fundamental, terça e quinta.",
                        "Tríade no teclado deixa a formação visível, mas compreensão exige ouvir a função de cada nota.",
                        "Mão direita; posição fundamental; sem pedal; notas tocadas primeiro separadas e depois juntas.",
                        List.of("Empilhe C–E–G e nomeie os intervalos desde a fundamental.", "Toque cada voz separadamente e cante a terça.", "Construa F e G pela mesma lógica, ajustando o peso para nenhuma nota desaparecer."),
                        "As três tríades contêm notas corretas e permitem ouvir as vozes individualmente.",
                        List.of(v("Duas notas", "Comece por fundamental e terça para definir qualidade.", 0, 5), v("Menores", "Altere somente a terça e compare C com Cm.", 0, 7)),
                        "compare", 0, 0, 10, "Harmonizar uma melodia curta com três funções básicas.", Assessment.Type.APPLICATION,
                        List.of("formacao", "terca", "equilibrio"), List.of("let-it-be-keys")),

                d("mission-keys-hands-together", LearningStage.BEGINNER_ADVANCED, "hands-together-basic", "Uma mão sustenta, a outra responde",
                        "Manter notas longas na esquerda enquanto a direita toca frase de quatro notas.",
                        "Coordenação começa com funções diferentes e simples, não com duplicação rápida das duas mãos.",
                        "Esquerda em C e G por quatro tempos; direita C–D–E–G; 56 BPM; sem pedal.",
                        List.of("Toque apenas os baixos e conte a duração completa.", "Cante e toque a frase direita sem esquerda.", "Combine e, se a direita atrasar, mantenha o baixo até o próximo compasso."),
                        "Quatro ciclos preservam duração da esquerda e entradas da frase direita.",
                        List.of(v("Uma nota direita", "Ataque somente C no tempo 3 sobre cada baixo.", -8, 5), v("Resposta variável", "Mude a última nota da direita sem alterar a esquerda.", 0, 7)),
                        "guided", 56, 76, 11, "Criar acompanhamento simples para uma melodia de uma mão.", Assessment.Type.APPLICATION,
                        List.of("duracao-esquerda", "entrada-direita", "independencia"), List.of("imagine-keys")),

                d("mission-keys-shell-voicings", LearningStage.BEGINNER_ADVANCED, "chord-shells", "Fundamental e terça contam a harmonia",
                        "Acompanhar C–Am–F–G usando baixos e shells sem saltos desnecessários.",
                        "Formas reduzidas deixam espaço para melodia e revelam quais notas definem função e qualidade.",
                        "Baixo na esquerda; terça e sétima ou terça e quinta na direita; um acorde por compasso.",
                        List.of("Toque só os baixos e cante a direção da progressão.", "Adicione a terça de cada acorde e compare maior/menor.", "Complete os shells escolhendo a nota que exige menor movimento na direita."),
                        "Quatro voltas mantêm fundamentais corretas, qualidade e condução compacta.",
                        List.of(v("Baixo + terça", "Use somente duas notas por acorde.", -8, 6), v("Melodia no topo", "Preserve G como nota superior quando compatível.", 0, 8)),
                        "guided", 60, 84, 12, "Acompanhar Let It Be sem ocupar a região da melodia.", Assessment.Type.APPLICATION,
                        List.of("fundamental", "qualidade", "conducao"), List.of("let-it-be-keys")),

                d("mission-keys-syncopation", LearningStage.BEGINNER_ADVANCED, "syncopation", "Acordes no contratempo, baixo no chão",
                        "Manter semínimas na esquerda enquanto a direita ataca somente nos contratempos.",
                        "Síncope no teclado depende de uma âncora que continue audível enquanto outra camada desloca expectativa.",
                        "C e Am; esquerda em 1–2–3–4; direita no 'e' de 2 e 'e' de 4; 60 BPM.",
                        List.of("Conte números e 'e' mantendo somente a esquerda.", "Bata os contratempos da direita fora do teclado.", "Combine e retire a direita por um compasso sempre que ela puxar a esquerda."),
                        "Três séries mantêm baixos nos números e acordes exatamente nos dois contratempos.",
                        List.of(v("Um contratempo", "Use apenas o 'e' de 4.", -8, 5), v("Progressão", "Troque C–Am–F–G sem mudar a célula.", 0, 7)),
                        "context", 60, 88, 11, "Criar uma textura original sincopada sobre C–Am–F–G.", Assessment.Type.PERFORMANCE,
                        List.of("ancora", "contratempo", "independencia"), List.of()),

                d("mission-keys-left-hand-pattern", LearningStage.EARLY_INTERMEDIATE, "left-hand-patterns", "Baixo, quinta e oitava sem olhar a mão",
                        "Executar 1–5–8–5 na esquerda enquanto acompanha mudanças de acorde.",
                        "Padrão de esquerda útil libera a visão para leitura e a direita para melodia ou voicings.",
                        "C–Am–F–G; 60 BPM; colcheias na esquerda; direita em acordes longos.",
                        List.of("Mapeie fundamental, quinta e oitava de C sem pulso.", "Transfira a geometria para Am, F e G antes de conectar.", "Acrescente acordes direitos no tempo 1 e mantenha a esquerda após pequenos erros."),
                        "Quatro voltas com desenho completo, baixos corretos e direita sustentada.",
                        List.of(v("1–5", "Retire a oitava até os saltos ficarem previsíveis.", -12, 6), v("Inversões direitas", "Escolha inversões que se movam por graus conjuntos.", 0, 8)),
                        "guided", 60, 92, 13, "Construir um acompanhamento contínuo inspirado em Imagine.", Assessment.Type.APPLICATION,
                        List.of("mapa-esquerda", "saltos", "coordenacao"), List.of("imagine-keys")),

                d("mission-keys-pedal-harmony", LearningStage.EARLY_INTERMEDIATE, "pedal-control", "Trocar o pedal depois do acorde",
                        "Conectar quatro acordes sem buracos nem mistura harmônica excessiva.",
                        "O pedal sustenta ressonância, mas a troca precisa seguir a harmonia e o ouvido, não um gesto automático.",
                        "C–G/B–Am–F; andamento livre e depois 56 BPM; pedal sustain; registro médio.",
                        List.of("Toque a progressão sem pedal e ouça a duração natural.", "Ataque o novo acorde, troque o pedal logo depois e escute a limpeza.", "Grave com e sem pedal e identifique exatamente onde a harmonia borra."),
                        "Duas voltas conectadas em que cada mudança permanece identificável.",
                        List.of(v("Dois acordes", "Alterne C e G até sincronizar pé e mãos.", 0, 6), v("Meio pedal", "Reduza a profundidade em registro grave e compare clareza.", 0, 8)),
                        "compare", 56, 76, 12, "Colorir uma balada sem esconder a condução harmônica.", Assessment.Type.PERFORMANCE,
                        List.of("sincronia", "limpeza", "ressonancia"), List.of("november-rain-keys")),

                d("mission-keys-chord-ear", LearningStage.EARLY_INTERMEDIATE, "ear-chords", "Ouvir a terça e escolher o voicing",
                        "Distinguir maior e menor e reconstruir a qualidade em outra região do teclado.",
                        "Percepção de acorde deve atravessar reconhecer, cantar, reproduzir e aplicar.",
                        "Tríades bloqueadas e arpejadas; C/Cm, A/Am, E/Em; resposta sem teclas visíveis.",
                        List.of("Ouça e cante a nota central que define a qualidade.", "Classifique maior ou menor antes de olhar.", "Reproduza em outra oitava e escolha uma inversão que mantenha a mesma qualidade."),
                        "Oito de dez classificações e três reconstruções conservam a terça correta.",
                        List.of(v("Mesma tônica", "Compare apenas C e Cm.", 0, 5), v("Inversões", "Reconheça a qualidade sem a fundamental no baixo.", 0, 8)),
                        "listen", 0, 0, 12, "Harmonizar uma frase escolhendo qualidade pelo som.", Assessment.Type.TRANSFER,
                        List.of("qualidade", "terca", "reconstrucao"), List.of()),

                d("mission-keys-accompaniment-textures", LearningStage.INTERMEDIATE, "keys-accompaniment-patterns", "Trocar textura sem trocar a harmonia",
                        "Acompanhar verso e refrão com padrões distintos e transição no compasso correto.",
                        "Textura comunica forma; repetir o mesmo desenho em toda música apaga contraste e ocupa espaço desnecessário.",
                        "C–G–Am–F; verso com baixos e shells, refrão com oitavas e acordes sincopados.",
                        List.of("Faça oito compassos do verso e confirme a condução compacta.", "Isole o padrão de refrão sem transição.", "Conecte as seções, antecipando mentalmente a nova função no último compasso."),
                        "Duas formas completas distinguem seções sem quebrar pulso ou progressão.",
                        List.of(v("Contraste simples", "Use acordes longos no verso e semínimas no refrão.", -8, 7), v("Retorno", "Volte ao verso retirando densidade no último tempo.", 0, 9)),
                        "play_along", 68, 96, 15, "Criar acompanhamento estrutural para Don't Stop Believin'.", Assessment.Type.PERFORMANCE,
                        List.of("textura", "forma", "transicao"), List.of("dont-stop-believin-keys")),

                d("mission-keys-scale-coordination", LearningStage.INTERMEDIATE, "scale-coordination", "Cruzar polegar sem deslocar o pulso",
                        "Executar C maior em duas mãos com dedilhados complementares e direção uniforme.",
                        "Coordenação de escalas organiza passagem de polegar, alinhamento e antecipação; velocidade é consequência.",
                        "Duas oitavas; mãos separadas e juntas; 60 BPM em colcheias; sem pedal.",
                        List.of("Marque separadamente onde cada mão passa o polegar.", "Toque mãos separadas e pare após a passagem para conferir alinhamento.", "Junte em grupos de quatro notas sem acentuar as travessias."),
                        "Três escalas completas com dedilhado correto, notas simultâneas e volume uniforme.",
                        List.of(v("Uma oitava", "Reduza extensão e toque mãos separadas.", -12, 6), v("Contrário", "Parta do centro e mova as mãos em direções opostas.", 0, 8)),
                        "guided", 60, 96, 14, "Usar a escala como passagem entre registros de uma frase.", Assessment.Type.PERFORMANCE,
                        List.of("dedilhado", "sincronia", "travessia"), List.of()),

                d("mission-keys-sight-reading", LearningStage.INTERMEDIATE, "keys-sight-reading-fluency", "Ler duas pautas sem parar para corrigir",
                        "Executar oito compassos novos preservando pulso, direção e alinhamento vertical.",
                        "Leitura pianística exige ver intervalos e ritmos em blocos, mantendo a música em movimento.",
                        "Trecho original em C; claves de sol e fá; 52 BPM; vinte segundos de inspeção.",
                        List.of("Marque mudanças de posição e ritmos diferentes entre mãos.", "Leia por intervalos e mantenha os olhos um pulso à frente.", "Em erro isolado, preserve a mão que continua correta e reencontre no próximo tempo forte."),
                        "A leitura chega ao final, conserva pulsação e recupera desalinhamentos em até um compasso.",
                        List.of(v("Mãos separadas", "Leia cada pauta mantendo sua contagem.", -8, 7), v("Nova tonalidade", "Leia trecho equivalente em G com F# indicado.", 0, 9)),
                        "notation", 52, 76, 15, "Preparar uma partitura curta para tocar com outra pessoa.", Assessment.Type.PERFORMANCE,
                        List.of("pulso", "leitura-vertical", "recuperacao"), List.of()),

                d("mission-keys-major-improv", LearningStage.EARLY_INTERMEDIATE, "major-improv", "Improvisar guiado pela voz superior",
                        "Criar pergunta e resposta sobre I–vi–IV–V chegando a notas de cada acorde.",
                        "Improvisação no teclado conecta ouvido, mapa e harmonia; correr pela escala não revela a progressão.",
                        "Mão esquerda em shells; direita na pentatônica de C; 68 BPM; dois compassos por frase.",
                        List.of("Cante uma pergunta e limite a mão direita a três notas.", "Responda chegando em C ou E sobre C e A ou C sobre Am.", "Repita a forma, mudando o ritmo mas preservando as chegadas harmônicas."),
                        "Duas formas apresentam pausa, contraste e ao menos três chegadas coerentes com os acordes.",
                        List.of(v("Uma mão", "Use playback e improvise somente com C, D e E.", -8, 6), v("Duas mãos", "Sustente shells na esquerda sem duplicar o ritmo da frase.", 0, 9)),
                        "create", 68, 92, 14, "Criar uma introdução improvisada para uma progressão pop.", Assessment.Type.APPLICATION,
                        List.of("frase", "pausa", "nota-alvo"), List.of()),

                d("mission-keys-hands-review", LearningStage.BEGINNER_ADVANCED, "hands-together-basic", "Recuperar duas funções em outra tonalidade",
                        "Reconstruir baixo sustentado e frase independente começando em G, sem consultar dedilhado.",
                        "A revisão muda tônica e retira instruções para distinguir coordenação disponível de memória do exemplo em C.",
                        "Material fechado; esquerda G–D por quatro tempos; direita G–A–B–D; 56 BPM.",
                        List.of("Descreva a função de cada mão antes de tocar.", "Faça uma tomada sem referência e preserve a esquerda após erro da direita.", "Confira apenas notas iniciais, feche o material e repita com uma resposta diferente."),
                        "Três ciclos mantêm duração esquerda, entrada direita e função após a transposição.",
                        List.of(v("Uma nota", "Use somente D na direita no tempo 3.", -8, 6), v("Resposta autoral", "Mude o ritmo direito sem duplicar a esquerda.", 0, 8)),
                        "review", 56, 76, 12, "Levar a coordenação recuperada a um acompanhamento em G.", Assessment.Type.RETENTION,
                        List.of("recuperacao", "independencia", "transferencia"), List.of("imagine-keys")),

                d("mission-keys-pop-voicings", LearningStage.UPPER_INTERMEDIATE, "pop-voicings", "Conduzir vozes dentro de uma progressão pop",
                        "Tocar I–V–vi–IV com baixo definido e voz superior por movimento mínimo.",
                        "Voicing eficaz preserva função enquanto cria uma linha cantável e espaço entre as mãos.",
                        "Progressão em E; esquerda em fundamentais/oitavas; direita em tríades e tétrades invertidas.",
                        List.of("Escolha uma nota superior para o primeiro acorde e cante o caminho possível.", "Encontre inversões que movam cada voz no máximo por terça.", "Toque a progressão e destaque a linha superior sem aumentar todo o acorde."),
                        "Quatro voltas mantêm funções e uma voz superior contínua, sem cruzamento confuso.",
                        List.of(v("Tríades", "Use apenas três vozes na direita.", -8, 7), v("Sétimas", "Inclua sétimas onde reforçam condução e estilo.", 0, 10)),
                        "guided", 68, 96, 16, "Reformular os acordes de Livin' on a Prayer para teclado.", Assessment.Type.APPLICATION,
                        List.of("funcao", "conducao", "balanco"), List.of("livin-prayer-keys")),

                d("mission-keys-transcription", LearningStage.UPPER_INTERMEDIATE, "transcription", "Separar baixo, voicing e ritmo pelo ouvido",
                        "Reconstruir quatro compassos de teclado em três passagens auditivas organizadas.",
                        "Transcrição de textura evita procurar todas as camadas ao mesmo tempo e conecta som a gesto.",
                        "Textura original Muse C–Am–F–G; loop curto; registro de baixo, notas superiores e célula rítmica.",
                        List.of("Cante e encontre somente o baixo de cada compasso.", "Isole a nota superior e anote seu contorno.", "Descubra a célula rítmica, complete as vozes internas e compare a gravação."),
                        "A reconstrução preserva baixos, contorno superior e padrão de ataque principal.",
                        List.of(v("Uma camada", "Transcreva apenas o contorno agudo.", 0, 7), v("Transposição", "Mova a textura para outra tonalidade mantendo funções.", 0, 10)),
                        "listen", 0, 0, 16, "Criar versão estudável sem depender de tutorial nota a nota.", Assessment.Type.TRANSFER,
                        List.of("baixo", "voz-superior", "ritmo"), List.of()),

                d("mission-keys-reharmonization", LearningStage.ADVANCED, "keys-reharmonization", "Mudar a harmonia sem perder a melodia",
                        "Criar duas harmonizações para uma frase, justificando função e condução de cada versão.",
                        "Rearmonizar é propor outra leitura do mesmo material, não substituir acordes por complexidade aleatória.",
                        "Melodia original de oito compassos em C; versão diatônica e versão com dominante secundário.",
                        List.of("Harmonize primeiro com funções diatônicas e cante a melodia superior.", "Escolha um ponto de chegada e prepare-o com uma dominante secundária.", "Compare tensão, resolução e movimento das vozes antes de manter a alteração."),
                        "As duas versões sustentam a melodia, resolvem funções declaradas e têm condução executável.",
                        List.of(v("Quatro compassos", "Rearmonize apenas a cadência final.", -8, 9), v("Baixo alternativo", "Crie segunda versão mudando inversões antes de novos acordes.", 0, 12)),
                        "create", 60, 84, 20, "Produzir um estudo autoral com duas leituras harmônicas.", Assessment.Type.APPLICATION,
                        List.of("melodia", "funcao", "resolucao", "conducao"), List.of()),

                d("mission-keys-stage-readiness", LearningStage.ADVANCED, "stage-readiness", "Preparar timbres, entradas e recuperações",
                        "Executar um mini-set de três peças com trocas de timbre e contagens confiáveis.",
                        "Performance de teclado inclui operação do instrumento, equilíbrio de registros e continuidade entre músicas.",
                        "Três peças; dois timbres; volumes nivelados; gravação contínua; sem editar entre faixas.",
                        List.of("Salve timbres e nivele volumes antes de tocar a primeira nota.", "Ensaiar somente finais, troca de preset e nova contagem.", "Grave o set, usando um voicing reduzido como rota de recuperação se uma camada falhar."),
                        "O set mantém fluxo, volumes coerentes e recuperação em até um compasso.",
                        List.of(v("Duas peças", "Teste uma única troca de timbre entre músicas.", -8, 10), v("Sem visor", "Faça a última tomada olhando apenas nos momentos operacionais previstos.", 0, 14)),
                        "record", 72, 100, 22, "Apresentar um mini-set de teclado com autonomia técnica e musical.", Assessment.Type.PERFORMANCE,
                        List.of("preparacao", "niveis", "transicoes", "recuperacao"), List.of("let-it-be-keys", "imagine-keys"))
        );
    }

    private static EditorialMissionDefinition d(String id, LearningStage stage, String competencyId,
                                                  String title, String objective, String purpose, String conditions,
                                                  List<String> instructions, String success,
                                                  List<ExerciseVariation> variations, String activityType,
                                                  int currentBpm, int targetBpm, int minutes, String application,
                                                  Assessment.Type assessmentType, List<String> criteria,
                                                  List<String> repertoire) {
        return new EditorialMissionDefinition(id, KEYS, stage, competencyId, title, objective, purpose,
                conditions, instructions, success, variations, activityType, currentBpm, targetBpm, minutes,
                application, assessmentType, criteria, repertoire);
    }

    private static ExerciseVariation v(String name, String instruction, int bpmOffset, int minutes) {
        return new ExerciseVariation(name, instruction, bpmOffset, minutes);
    }
}
