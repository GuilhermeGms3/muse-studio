package com.musicos.config;

import com.musicos.domain.InstrumentId;
import com.musicos.domain.Skill;
import com.musicos.domain.SkillState;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class CurriculumCatalog {
    private static final List<InstrumentId> ALL = List.of(
            InstrumentId.GUITAR, InstrumentId.ACOUSTIC, InstrumentId.KEYS);
    private static final List<InstrumentId> STRINGS = List.of(
            InstrumentId.GUITAR, InstrumentId.ACOUSTIC);
    private static final List<InstrumentId> GUITAR = List.of(InstrumentId.GUITAR);
    private static final List<InstrumentId> ACOUSTIC = List.of(InstrumentId.ACOUSTIC);
    private static final List<InstrumentId> KEYS = List.of(InstrumentId.KEYS);

    private CurriculumCatalog() {
    }

    static List<Skill> all() {
        var result = new ArrayList<Skill>();

        // Fundamentos e ritmo
        result.add(node("pulse", "Manter um pulso estável", "Pulso", "Fundamentos", ALL,
                ids(), ids("rhythm"), "ritmo"));
        result.add(node("subdivisions", "Dividir o tempo com clareza", "Subdivisões", "Ritmo", ALL,
                ids("pulse"), ids("meter", "syncopation"), "ritmo"));
        result.add(node("meter", "Entender como os tempos se agrupam", "Compasso", "Ritmo", ALL,
                ids("subdivisions"), ids("rhythm-reading"), "ritmo"));
        result.add(node("syncopation", "Tocar acentos fora do lugar esperado", "Síncope", "Ritmo", ALL,
                ids("subdivisions"), ids("groove"), "ritmo"));
        result.add(node("groove", "Fazer o ritmo respirar", "Groove", "Ritmo", ALL,
                ids("syncopation", "meter"), ids("dynamics"), "ritmo"));
        result.add(node("dynamics", "Controlar intensidade e articulação", "Dinâmica", "Musicalidade", ALL,
                ids("groove"), ids("phrasing"), "fraseado"));

        // Teoria, escalas e harmonia
        result.add(node("notes", "Reconhecer as doze notas", "Notas musicais", "Teoria", ALL,
                ids(), ids("intervals"), "intervalos"));
        result.add(node("intervals", "Medir a distância entre duas notas", "Intervalos", "Teoria", ALL,
                ids("notes"), ids("major-scale", "triads", "ear-intervals"), "intervalos"));
        result.add(node("major-scale", "Entender a escala que organiza o sistema tonal", "Escala Maior",
                "Escalas", ALL, ids("intervals"), ids("major-pentatonic", "natural-minor", "harmonic-field"),
                "escala-maior"));
        result.add(node("major-pentatonic", "Improvisar com cinco notas maiores", "Pentatônica Maior",
                "Escalas", ALL, ids("major-scale"), ids("major-improv"), "escala-maior"));
        result.add(node("minor-pentatonic", "Improvisar com cinco notas menores", "Pentatônica Menor",
                "Escalas", STRINGS, ids("intervals"), ids("blues-scale", "phrasing"), "improvisacao"));
        result.add(node("blues-scale", "Adicionar tensão blues ao fraseado", "Escala Blues", "Escalas",
                STRINGS, ids("minor-pentatonic"), ids("blues-improv"), "improvisacao"));
        result.add(node("natural-minor", "Ouvir e construir a escala menor natural", "Escala Menor Natural",
                "Escalas", ALL, ids("major-scale"), ids("minor-harmonic-field", "harmonic-minor"),
                "escala-maior"));
        result.add(node("harmonic-minor", "Criar tensão dominante no modo menor", "Menor Harmônica",
                "Escalas", ALL, ids("natural-minor"), ids("minor-dominant"), "campo-harmonico"));
        result.add(node("melodic-minor", "Explorar a sonoridade da menor melódica", "Menor Melódica",
                "Escalas", ALL, ids("harmonic-minor"), ids("altered-scale"), "modos-gregos"));
        result.add(node("whole-tone", "Reconhecer a sonoridade simétrica aumentada", "Escala de Tons Inteiros",
                "Escalas", ALL, ids("intervals"), ids("outside-playing"), "modos-gregos"));
        result.add(node("diminished-scale", "Usar simetria sobre acordes dominantes", "Escala Diminuta",
                "Escalas", ALL, ids("seventh-chords"), ids("outside-playing"), "modos-gregos"));
        result.add(node("altered-scale", "Resolver tensões alteradas", "Escala Alterada", "Escalas",
                ALL, ids("melodic-minor", "seventh-chords"), ids("outside-playing"), "modos-gregos"));
        result.add(node("triads", "Construir acordes com três notas", "Tríades", "Harmonia", ALL,
                ids("intervals", "major-scale"), ids("triad-inversions", "harmonic-field"), "triades"));
        result.add(node("triad-inversions", "Mover acordes sem saltos desnecessários", "Inversões de Tríades",
                "Harmonia", ALL, ids("triads"), ids("voice-leading"), "triades"));
        result.add(node("seventh-chords", "Adicionar a sétima aos acordes", "Tétrades", "Harmonia", ALL,
                ids("triads"), ids("secondary-dominants", "jazz-harmony"), "triades"));
        result.add(node("minor-harmonic-field", "Entender os acordes do tom menor", "Campo Harmônico Menor",
                "Harmonia", ALL, ids("natural-minor", "triads"), ids("minor-dominant"), "campo-harmonico"));
        result.add(node("minor-dominant", "Resolver com força dentro do tom menor", "Dominante no Modo Menor",
                "Harmonia", ALL, ids("minor-harmonic-field", "harmonic-minor"),
                ids("secondary-dominants"), "campo-harmonico"));
        result.add(node("harmonic-functions", "Ouvir repouso, movimento e tensão", "Funções Harmônicas",
                "Harmonia", ALL, ids("harmonic-field"), ids("cadences", "song-analysis"), "campo-harmonico"));
        result.add(node("cadences", "Reconhecer como frases harmônicas terminam", "Cadências", "Harmonia",
                ALL, ids("harmonic-functions"), ids("secondary-dominants"), "campo-harmonico"));
        result.add(node("secondary-dominants", "Criar dominantes temporárias", "Dominantes Secundários",
                "Harmonia", ALL, ids("cadences", "seventh-chords"), ids("reharmonization"), "rearmonizacao"));
        result.add(node("voice-leading", "Conectar acordes pelo menor movimento", "Condução de Vozes",
                "Harmonia", ALL, ids("triad-inversions"), ids("arrangement"), "voice-leading"));
        result.add(node("reharmonization", "Encontrar novos caminhos entre acordes", "Rearmonização",
                "Harmonia", ALL, ids("secondary-dominants", "voice-leading"), ids("jazz-harmony"),
                "rearmonizacao"));
        result.add(node("jazz-harmony", "Usar extensões e substituições com intenção", "Harmonia Jazz",
                "Harmonia", ALL, ids("reharmonization", "seventh-chords"), ids("outside-playing"),
                "rearmonizacao"));

        // Ouvido
        result.add(node("ear-pitch", "Perceber se um som sobe ou desce", "Direção Melódica", "Ouvido", ALL,
                ids("pulse"), ids("ear-intervals"), "ear-training"));
        result.add(node("ear-intervals", "Reconhecer distâncias entre notas", "Intervalos de Ouvido",
                "Ouvido", ALL, ids("intervals", "ear-pitch"), ids("ear-melody", "ear-chords"), "ear-training"));
        result.add(node("ear-rhythm", "Reconhecer células rítmicas", "Ditado Rítmico", "Ouvido", ALL,
                ids("subdivisions"), ids("transcription"), "ear-training"));
        result.add(node("ear-chords", "Distinguir qualidades de acordes", "Reconhecimento de Acordes",
                "Ouvido", ALL, ids("triads", "ear-intervals"), ids("ear-progressions"), "ear-training"));
        result.add(node("ear-progressions", "Reconhecer movimentos harmônicos", "Progressões de Ouvido",
                "Ouvido", ALL, ids("ear-chords", "harmonic-functions"), ids("transcription"), "ear-training"));
        result.add(node("ear-melody", "Reproduzir pequenas melodias de ouvido", "Ditado Melódico",
                "Ouvido", ALL, ids("ear-intervals"), ids("transcription"), "ear-training"));
        result.add(node("transcription", "Retirar música diretamente da gravação", "Transcrição",
                "Ouvido", ALL, ids("ear-rhythm", "ear-melody"), ids("song-analysis"), "ear-training"));

        // Leitura
        result.add(node("rhythm-reading", "Ler ritmos sem depender do instrumento", "Leitura Rítmica",
                "Leitura", ALL, ids("meter"), ids("note-reading"), "leitura"));
        result.add(node("note-reading", "Encontrar notas escritas no instrumento", "Leitura de Notas",
                "Leitura", ALL, ids("notes", "rhythm-reading"), ids("sight-reading"), "leitura"));
        result.add(node("sight-reading", "Tocar um trecho novo sem interromper", "Leitura à Primeira Vista",
                "Leitura", ALL, ids("note-reading"), ids("ensemble-reading"), "leitura"));
        result.add(node("ensemble-reading", "Seguir partitura e outros músicos ao mesmo tempo",
                "Leitura em Conjunto", "Leitura", ALL, ids("sight-reading"), ids("performance"),
                "leitura"));

        // Técnica de guitarra
        result.add(node("guitar-posture", "Tocar sem criar tensão desnecessária", "Postura na Guitarra",
                "Técnica", GUITAR, ids("pulse"), ids("sync"), "alternate-picking"));
        result.add(node("sync", "Sincronizar as duas mãos", "Sincronização", "Técnica", GUITAR,
                ids("guitar-posture", "rhythm"), ids("alternate-picking", "legato"), "alternate-picking"));
        result.add(node("legato", "Conectar notas sem atacar todas", "Legato", "Técnica", GUITAR,
                ids("sync"), ids("hammer-pull", "tapping"), "legato"));
        result.add(node("hammer-pull", "Controlar hammer-ons e pull-offs", "Hammer-on e Pull-off",
                "Técnica", GUITAR, ids("legato"), ids("tapping"), "legato"));
        result.add(node("string-skipping", "Saltar cordas sem gerar ruído", "String Skipping", "Técnica",
                GUITAR, ids("alternate-picking"), ids("hybrid-picking"), "alternate-picking"));
        result.add(node("palm-mute", "Controlar peso e duração com a mão direita", "Palm Mute", "Técnica",
                GUITAR, ids("rhythm"), ids("endurance"), "alternate-picking"));
        result.add(node("economy-picking", "Economizar movimento nas trocas de corda", "Economy Picking",
                "Técnica", GUITAR, ids("alternate-picking"), ids("sweep-picking"), "alternate-picking"));
        result.add(node("sweep-picking", "Tocar arpejos com um movimento contínuo", "Sweep Picking",
                "Técnica", GUITAR, ids("economy-picking", "arpeggios"), ids("speed"), "alternate-picking"));
        result.add(node("tapping", "Combinar as duas mãos sobre o braço", "Tapping", "Técnica", GUITAR,
                ids("hammer-pull"), ids("speed"), "legato"));
        result.add(node("hybrid-picking", "Combinar palheta e dedos", "Hybrid Picking", "Técnica", GUITAR,
                ids("string-skipping"), ids("chord-melody"), "alternate-picking"));
        result.add(node("speed", "Aumentar velocidade preservando controle", "Velocidade", "Técnica", GUITAR,
                ids("alternate-picking", "sync"), ids("endurance"), "alternate-picking"));
        result.add(node("endurance", "Manter a técnica por períodos maiores", "Resistência", "Técnica",
                GUITAR, ids("speed", "palm-mute"), ids("performance"), "alternate-picking"));

        // Violão
        result.add(node("open-chords", "Formar os acordes abertos essenciais", "Acordes Abertos",
                "Violão", ACOUSTIC, ids("triads"), ids("chord-transitions"), "triades"));
        result.add(node("chord-transitions", "Trocar acordes sem quebrar o pulso", "Troca de Acordes",
                "Violão", ACOUSTIC, ids("open-chords", "pulse"), ids("strumming"), "ritmo"));
        result.add(node("strumming", "Construir levadas consistentes", "Levadas", "Violão", ACOUSTIC,
                ids("chord-transitions", "subdivisions"), ids("barre-chords"), "ritmo"));
        result.add(node("barre-chords", "Mover formas de acordes pelo braço", "Pestanas", "Violão",
                ACOUSTIC, ids("strumming"), ids("fingerstyle"), "triades"));
        result.add(node("fingerstyle", "Separar baixo, harmonia e melodia nos dedos", "Fingerstyle",
                "Violão", ACOUSTIC, ids("barre-chords", "rhythm"), ids("chord-melody"), "legato"));
        result.add(node("chord-melody", "Tocar melodia e acordes juntos", "Chord Melody", "Arranjo",
                STRINGS, ids("fingerstyle", "voice-leading"), ids("arrangement"), "voice-leading"));

        // Teclado
        result.add(node("keyboard-map", "Enxergar notas e intervalos no teclado", "Mapa do Teclado",
                "Teclado", KEYS, ids("notes", "intervals"), ids("keys-fingering"), "escala-maior"));
        result.add(node("keys-fingering", "Usar dedilhados eficientes em escalas", "Dedilhado de Escalas",
                "Teclado", KEYS, ids("keyboard-map", "major-scale"), ids("keys-independence"), "escala-maior"));
        result.add(node("keys-independence", "Dar funções diferentes para cada mão", "Independência das Mãos",
                "Teclado", KEYS, ids("keys-fingering", "rhythm"), ids("keys-voicings"), "leitura"));
        result.add(node("keys-voicings", "Distribuir acordes entre as mãos", "Voicings no Teclado",
                "Teclado", KEYS, ids("keys-independence", "seventh-chords"), ids("keys-arpeggios"),
                "triades"));
        result.add(node("keys-arpeggios", "Percorrer acordes com fluidez", "Arpejos no Teclado",
                "Teclado", KEYS, ids("keys-voicings"), ids("major-improv"), "triades"));

        // Improvisação
        result.add(node("phrasing", "Transformar escalas em frases musicais", "Fraseado", "Improvisação",
                ALL, ids("dynamics"), ids("targeting"), "improvisacao"));
        result.add(node("targeting", "Resolver frases nas notas do acorde", "Target Notes", "Improvisação",
                ALL, ids("phrasing", "triads"), ids("major-improv"), "improvisacao"));
        result.add(node("major-improv", "Improvisar sobre progressões em tom maior", "Improvisação Maior",
                "Improvisação", ALL, ids("major-pentatonic", "targeting"), ids("modes"), "improvisacao"));
        result.add(node("blues-improv", "Construir solos dentro da linguagem blues", "Improvisação Blues",
                "Improvisação", STRINGS, ids("blues-scale", "phrasing"), ids("arpeggio-improv"),
                "improvisacao"));
        result.add(node("arpeggios", "Visualizar as notas de cada acorde", "Arpejos", "Improvisação",
                ALL, ids("triads"), ids("arpeggio-improv", "sweep-picking"), "triades"));
        result.add(node("arpeggio-improv", "Acompanhar os acordes com o solo", "Improvisação com Arpejos",
                "Improvisação", ALL, ids("arpeggios", "targeting"), ids("outside-playing"), "improvisacao"));
        result.add(node("outside-playing", "Criar tensão fora da tonalidade e resolver", "Outside Playing",
                "Improvisação", ALL, ids("arpeggio-improv", "modes"), ids("advanced-improv"),
                "modos-gregos"));
        result.add(node("advanced-improv", "Conduzir um solo com forma e narrativa", "Improvisação Avançada",
                "Improvisação", ALL, ids("outside-playing"), ids("performance"), "improvisacao"));

        // Repertório, criação e performance
        result.add(node("song-sections", "Dividir uma música em partes estudáveis", "Seções Musicais",
                "Repertório", ALL, ids("rhythm"), ids("section-practice"), "forma-musical"));
        result.add(node("section-practice", "Resolver trechos difíceis sem repetir tudo", "Prática por Seções",
                "Repertório", ALL, ids("song-sections"), ids("memorization"), "forma-musical"));
        result.add(node("memorization", "Tocar sem depender de cifras ou tablaturas", "Memorização",
                "Repertório", ALL, ids("section-practice", "ear-melody"), ids("performance"), "ear-training"));
        result.add(node("song-analysis", "Entender o que uma música está fazendo", "Análise Musical",
                "Repertório", ALL, ids("harmonic-functions", "transcription"), ids("arrangement"),
                "campo-harmonico"));
        result.add(node("performance", "Executar uma música inteira com segurança", "Performance",
                "Performance", ALL, ids("memorization", "dynamics"), ids("recording-review"), "forma-musical"));
        result.add(node("recording-review", "Usar gravações para orientar a próxima prática",
                "Revisão por Gravação", "Performance", ALL, ids("performance"), ids("stage-readiness"),
                "ear-training"));
        result.add(node("stage-readiness", "Preparar repertório para tocar para outras pessoas",
                "Preparação de Palco", "Performance", ALL, ids("recording-review"), ids(), "forma-musical"));
        result.add(node("motif", "Criar ideias curtas que podem crescer", "Motivo", "Composição", ALL,
                ids("phrasing"), ids("melody-writing"), "forma-musical"));
        result.add(node("melody-writing", "Construir melodias com direção", "Criação de Melodias",
                "Composição", ALL, ids("motif", "ear-melody"), ids("form"), "forma-musical"));
        result.add(node("form", "Organizar ideias em uma estrutura completa", "Forma Musical",
                "Composição", ALL, ids("song-sections", "melody-writing"), ids("arrangement"), "forma-musical"));
        result.add(node("arrangement", "Distribuir funções e criar contraste", "Arranjo", "Composição",
                ALL, ids("form", "voice-leading"), ids("production-demo"), "voice-leading"));
        result.add(node("production-demo", "Transformar a composição em uma demo clara", "Produção de Demo",
                "Composição", ALL, ids("arrangement", "recording-review"), ids(), "forma-musical"));

        return result;
    }

    private static Skill node(String id, String friendly, String technical, String domain,
                              List<InstrumentId> instruments, List<String> prerequisites,
                              List<String> next, String content) {
        var state = prerequisites.isEmpty() ? SkillState.AVAILABLE : SkillState.LOCKED;
        return new Skill(id, friendly, technical, domain,
                "Aprenda " + technical + " de forma gradual, aplique em exercícios e leve o resultado para músicas.",
                state, 0, 0, null, null, instruments, prerequisites,
                List.of(content), List.of(), List.of(), next);
    }

    private static List<String> ids(String... values) {
        return Arrays.asList(values);
    }
}
