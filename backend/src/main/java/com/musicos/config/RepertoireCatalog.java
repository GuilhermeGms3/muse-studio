package com.musicos.config;

import com.musicos.domain.InstrumentId;
import com.musicos.domain.Song;
import com.musicos.domain.SongSection;
import java.util.ArrayList;
import java.util.List;

final class RepertoireCatalog {
    private RepertoireCatalog() {
    }

    static List<Song> all() {
        var songs = new ArrayList<Song>();

        // Guitarra: dificuldade representa o ponto de entrada, não uma promessa de tocar tudo no primeiro dia.
        songs.add(guitar("seven-nation-army-guitar", "Seven Nation Army", "The White Stripes",
                "Standard", "Em", 124, 1, "Riff em uma corda",
                List.of("pulse", "alternate-picking"),
                "Clean encorpado, ganho baixo, reverb curto"));
        songs.add(guitar("smoke-on-the-water-guitar", "Smoke on the Water", "Deep Purple",
                "Standard", "Gm", 112, 1, "Riff com duas notas",
                List.of("pulse", "sync"),
                "Crunch médio, médios presentes, pouco reverb"));
        songs.add(guitar("iron-man-guitar", "Iron Man", "Black Sabbath",
                "Standard", "Bm", 72, 1, "Riff lento e pausas",
                List.of("rhythm", "palm-mute"),
                "Drive forte, graves controlados, noise gate leve"));
        songs.add(guitar("brain-stew-guitar", "Brain Stew", "Green Day",
                "Eb Standard", "—", 76, 1, "Ataques e pausas",
                List.of("rhythm", "palm-mute"),
                "Drive médio, gate suave, delay desligado"));
        songs.add(guitar("zombie-guitar", "Zombie", "The Cranberries",
                "Standard", "Em", 84, 1, "Quatro acordes",
                List.of("pulse", "rhythm"),
                "Clean no verso; drive moderado no refrão"));
        songs.add(guitar("come-as-you-are-guitar", "Come As You Are", "Nirvana",
                "D Standard", "F#m", 120, 2, "Riff alternando cordas",
                List.of("alternate-picking", "string-skipping"),
                "Clean com chorus leve, graves sem excesso"));
        songs.add(guitar("i-love-rock-n-roll-guitar", "I Love Rock 'n Roll", "Joan Jett",
                "Standard", "E", 95, 2, "Base com pausas",
                List.of("rhythm", "palm-mute"),
                "Crunch aberto, médios fortes, reverb curto"));
        songs.add(guitar("highway-to-hell-guitar", "Highway to Hell", "AC/DC",
                "Standard", "A", 116, 2, "Acordes e silêncio",
                List.of("rhythm", "dynamics"),
                "Crunch baixo, poucos graves, sem modulação"));
        songs.add(guitar("tnt-guitar", "T.N.T.", "AC/DC",
                "Standard", "E", 126, 2, "Base em semínimas",
                List.of("pulse", "rhythm"),
                "Crunch leve, médios presentes, reverb mínimo"));
        songs.add(guitar("knockin-heavens-door-guitar", "Knockin' on Heaven's Door", "Guns N' Roses",
                "Eb Standard", "G", 68, 2, "Base simplificada",
                List.of("rhythm", "dynamics"),
                "Clean levemente comprimido; drive suave no refrão"));
        songs.add(guitar("for-whom-bell-tolls-guitar", "For Whom the Bell Tolls", "Metallica",
                "Standard", "Em", 118, 2, "Riff principal lento",
                List.of("palm-mute", "rhythm"),
                "Drive alto, gate moderado, médios controlados"));
        songs.add(guitar("back-in-black-guitar", "Back In Black", "AC/DC",
                "Standard", "E", 94, 3, "Riff e espaços",
                List.of("rhythm", "dynamics"),
                "Crunch baixo, bastante médio, ambiência quase seca"));
        songs.add(guitar("enter-sandman-guitar", "Enter Sandman", "Metallica",
                "Standard", "Em", 123, 3, "Riff com palm mute",
                List.of("palm-mute", "alternate-picking"),
                "Drive alto, gate moderado, reverb curto"));
        songs.add(guitar("hail-to-the-king-guitar", "Hail to the King", "Avenged Sevenfold",
                "Drop D", "Dm", 118, 3, "Guitarra base simplificada",
                List.of("palm-mute", "bends"),
                "Drive moderno, gate moderado, delay apenas no lead"));
        songs.add(guitar("so-far-away-guitar", "So Far Away", "Avenged Sevenfold",
                "Standard", "—", 68, 3, "Base e melodia",
                List.of("dynamics", "bends"),
                "Clean com reverb no verso; lead com delay curto"));
        songs.add(guitar("black-or-white-guitar", "Black or White", "Michael Jackson",
                "Standard", "E", 115, 3, "Riff rítmico",
                List.of("rhythm", "syncopation"),
                "Clean brilhante, compressão leve, drive quase zero"));
        songs.add(guitar("final-countdown-guitar", "The Final Countdown", "Europe",
                "Standard", "F#m", 118, 3, "Base de arena rock",
                List.of("rhythm", "bends"),
                "Drive médio, chorus discreto, delay curto no solo"));
        songs.add(guitar("you-give-love-guitar", "You Give Love a Bad Name", "Bon Jovi",
                "Standard", "Cm", 123, 3, "Base e refrão",
                List.of("palm-mute", "dynamics"),
                "Drive médio-alto, chorus leve, reverb curto"));
        songs.add(guitar("crazy-train-guitar", "Crazy Train", "Ozzy Osbourne",
                "Standard", "F#m", 138, 4, "Riff com troca de cordas",
                List.of("alternate-picking", "string-skipping", "speed"),
                "Drive alto, médios fortes, delay curto no solo"));
        songs.add(guitar("master-puppets-guitar", "Master of Puppets", "Metallica",
                "Standard", "Em", 212, 5, "Riff apenas em velocidade reduzida",
                List.of("palm-mute", "speed", "endurance"),
                "Drive alto, gate firme, graves enxutos"));
        songs.add(guitar("nightmare-guitar", "Nightmare", "Avenged Sevenfold",
                "Drop D", "Dm", 130, 5, "Base por pequenas células",
                List.of("alternate-picking", "palm-mute", "speed"),
                "Drive moderno, gate firme, delay apenas nas melodias"));

        // Violão: primeiro acompanhamentos que sobrevivem com poucas formas, depois dedilhados e arranjos.
        songs.add(acoustic("horse-no-name-acoustic", "A Horse with No Name", "America",
                "Standard", "Em", 78, 1, "Duas formas e pulso constante",
                List.of("open-chords", "strumming")));
        songs.add(acoustic("knockin-heavens-door-acoustic", "Knockin' on Heaven's Door", "Bob Dylan",
                "Standard", "G", 68, 1, "Troca lenta entre acordes abertos",
                List.of("open-chords", "chord-transitions")));
        songs.add(acoustic("stand-by-me-acoustic", "Stand by Me", "Ben E. King",
                "Standard", "A", 120, 1, "Quatro acordes em ciclo",
                List.of("open-chords", "pulse")));
        songs.add(acoustic("zombie-acoustic", "Zombie", "The Cranberries",
                "Standard", "Em", 84, 1, "Um ciclo de quatro acordes",
                List.of("open-chords", "strumming")));
        songs.add(acoustic("seen-rain-acoustic", "Have You Ever Seen the Rain", "Creedence Clearwater Revival",
                "Standard", "C", 116, 1, "Levada simples em acordes abertos",
                List.of("chord-transitions", "strumming")));
        songs.add(acoustic("good-riddance-acoustic", "Good Riddance (Time of Your Life)", "Green Day",
                "Standard", "G", 95, 2, "Dedilhado reduzido para levada",
                List.of("chord-transitions", "fingerstyle")));
        songs.add(acoustic("wonderwall-acoustic", "Wonderwall", "Oasis",
                "Standard · capo 2", "F#m", 87, 2, "Formas fixas e levada",
                List.of("strumming", "chord-transitions")));
        songs.add(acoustic("every-rose-acoustic", "Every Rose Has Its Thorn", "Poison",
                "Standard", "G", 70, 2, "Balada com dinâmica",
                List.of("strumming", "dynamics")));
        songs.add(acoustic("patience-acoustic", "Patience", "Guns N' Roses",
                "Eb Standard", "—", 72, 2, "Base simplificada sem introdução",
                List.of("open-chords", "strumming")));
        songs.add(acoustic("wish-you-were-here-acoustic", "Wish You Were Here", "Pink Floyd",
                "Standard", "G", 60, 2, "Acordes antes do riff",
                List.of("open-chords", "chord-transitions")));
        songs.add(acoustic("house-rising-sun-acoustic", "House of the Rising Sun", "The Animals",
                "Standard", "Am", 79, 2, "Arpejo em acordes abertos",
                List.of("open-chords", "fingerstyle")));
        songs.add(acoustic("nothing-else-matters-acoustic", "Nothing Else Matters", "Metallica",
                "Standard", "Em", 71, 3, "Introdução em pequenas células",
                List.of("fingerstyle", "chord-melody")));
        songs.add(acoustic("so-far-away-acoustic", "So Far Away", "Avenged Sevenfold",
                "Standard", "—", 68, 3, "Base acústica e dinâmica",
                List.of("strumming", "dynamics")));
        songs.add(acoustic("wanted-dead-alive-acoustic", "Wanted Dead or Alive", "Bon Jovi",
                "Standard", "D", 84, 3, "Introdução e acompanhamento",
                List.of("fingerstyle", "strumming")));
        songs.add(acoustic("more-than-words-acoustic", "More Than Words", "Extreme",
                "Standard", "G", 92, 3, "Polegar e acordes percussivos",
                List.of("fingerstyle", "chord-transitions")));
        songs.add(acoustic("mama-coming-home-acoustic", "Mama, I'm Coming Home", "Ozzy Osbourne",
                "Standard", "E", 72, 3, "Arpejos e refrão",
                List.of("fingerstyle", "dynamics")));
        songs.add(acoustic("unforgiven-acoustic", "The Unforgiven", "Metallica",
                "Standard", "Am", 120, 3, "Melodia e base simplificada",
                List.of("fingerstyle", "chord-melody")));
        songs.add(acoustic("hotel-california-acoustic", "Hotel California", "Eagles",
                "Standard · capo 7", "Bm", 75, 4, "Progressão e dedilhado",
                List.of("barre-chords", "fingerstyle", "chord-melody")));
        songs.add(acoustic("dust-in-wind-acoustic", "Dust in the Wind", "Kansas",
                "Standard", "C", 94, 4, "Padrão contínuo de fingerstyle",
                List.of("fingerstyle", "chord-melody", "endurance")));

        // Teclado: melodias curtas de uma mão antes de independência, voicings e timbres de sintetizador.
        songs.add(keys("ode-to-joy-keys", "Ode to Joy", "Ludwig van Beethoven",
                "C", 90, 1, "Melodia dentro de cinco notas",
                List.of("keyboard-map", "keys-fingering")));
        songs.add(keys("seven-nation-army-keys", "Seven Nation Army", "The White Stripes",
                "Em", 124, 1, "Riff com uma mão",
                List.of("keyboard-map", "pulse")));
        songs.add(keys("we-will-rock-you-keys", "We Will Rock You", "Queen",
                "—", 81, 1, "Ritmo e acordes sustentados",
                List.of("pulse", "triads")));
        songs.add(keys("smoke-water-keys", "Smoke on the Water", "Deep Purple",
                "Gm", 112, 1, "Riff em posições próximas",
                List.of("keyboard-map", "keys-fingering")));
        songs.add(keys("let-it-be-keys", "Let It Be", "The Beatles",
                "C", 72, 1, "Acordes em posição fundamental",
                List.of("triads", "pulse")));
        songs.add(keys("imagine-keys", "Imagine", "John Lennon",
                "C", 76, 2, "Acordes e baixo simples",
                List.of("triads", "keys-independence")));
        songs.add(keys("billie-jean-keys", "Billie Jean", "Michael Jackson",
                "F#m", 117, 2, "Padrão repetitivo de synth bass",
                List.of("keys-fingering", "pulse")));
        songs.add(keys("highway-hell-keys", "Highway to Hell", "AC/DC",
                "A", 116, 2, "Acordes acompanhando a banda",
                List.of("triads", "rhythm-reading")));
        songs.add(keys("final-countdown-keys", "The Final Countdown", "Europe",
                "F#m", 118, 2, "Tema principal com uma mão",
                List.of("keys-fingering", "rhythm-reading")));
        songs.add(keys("jump-keys", "Jump", "Van Halen",
                "C", 129, 3, "Riff de sintetizador por blocos",
                List.of("keys-voicings", "rhythm-reading")));
        songs.add(keys("take-on-me-keys", "Take on Me", "a-ha",
                "A", 169, 3, "Tema reduzido em velocidade baixa",
                List.of("keys-fingering", "keys-independence")));
        songs.add(keys("livin-prayer-keys", "Livin' on a Prayer", "Bon Jovi",
                "Em", 123, 3, "Synth bass e acordes",
                List.of("keys-independence", "keys-voicings")));
        songs.add(keys("dont-stop-believin-keys", "Don't Stop Believin'", "Journey",
                "E", 119, 3, "Padrão de acompanhamento",
                List.of("keys-independence", "keys-arpeggios")));
        songs.add(keys("separate-ways-keys", "Separate Ways", "Journey",
                "Em", 131, 3, "Riff de sintetizador e acordes",
                List.of("keys-voicings", "rhythm-reading")));
        songs.add(keys("november-rain-keys", "November Rain", "Guns N' Roses",
                "C", 80, 3, "Introdução simplificada",
                List.of("keys-arpeggios", "dynamics")));
        songs.add(keys("thriller-keys", "Thriller", "Michael Jackson",
                "C#m", 118, 3, "Camadas rítmicas de sintetizador",
                List.of("keys-voicings", "syncopation")));
        songs.add(keys("clocks-keys", "Clocks", "Coldplay",
                "E♭", 131, 3, "Arpejo repetitivo em três notas",
                List.of("keys-arpeggios", "keys-independence")));
        songs.add(keys("nothing-else-matters-keys", "Nothing Else Matters", "Metallica",
                "Em", 71, 3, "Arranjo reduzido para piano",
                List.of("keys-arpeggios", "dynamics")));
        songs.add(keys("human-nature-keys", "Human Nature", "Michael Jackson",
                "D", 93, 4, "Voicings e síncopes",
                List.of("keys-voicings", "syncopation", "dynamics")));
        songs.add(keys("africa-keys", "Africa", "Toto",
                "B", 93, 4, "Camadas e independência",
                List.of("keys-voicings", "keys-independence", "rhythm-reading")));
        songs.add(keys("bohemian-rhapsody-keys", "Bohemian Rhapsody", "Queen",
                "B♭", 72, 5, "Arranjo por seções",
                List.of("keys-voicings", "keys-arpeggios", "performance")));

        // Bateria: começa por pulsação e grooves repetitivos antes de viradas fiéis à gravação.
        songs.add(drums("we-will-rock-you-drums", "We Will Rock You", "Queen",
                81, 1, "Pulso com bumbo e caixa",
                List.of("pulse", "drum-kit-map")));
        songs.add(drums("tnt-drums", "T.N.T.", "AC/DC",
                126, 1, "Rock beat simplificado",
                List.of("drum-rock-groove", "drum-groove-consistency")));
        songs.add(drums("another-one-bites-dust-drums", "Another One Bites the Dust", "Queen",
                110, 1, "Groove repetitivo",
                List.of("drum-rock-groove", "drum-groove-consistency")));
        songs.add(drums("come-as-you-are-drums", "Come As You Are", "Nirvana",
                120, 2, "Condução e bumbo",
                List.of("drum-kick-variations", "drum-ride-coordination")));
        songs.add(drums("zombie-drums", "Zombie", "The Cranberries",
                84, 2, "Dinâmica entre seções",
                List.of("drum-groove-consistency", "drum-dynamics")));
        songs.add(drums("highway-to-hell-drums", "Highway to Hell", "AC/DC",
                116, 2, "Colcheias no chimbal",
                List.of("drum-rock-groove", "drum-fill-timing")));
        songs.add(drums("i-love-rock-n-roll-drums", "I Love Rock 'n Roll", "Joan Jett",
                95, 2, "Groove com espaços",
                List.of("drum-groove-consistency", "drum-one-beat-fill")));
        songs.add(drums("for-whom-bell-tolls-drums", "For Whom the Bell Tolls", "Metallica",
                118, 2, "Peso sem acelerar",
                List.of("drum-rock-groove", "drum-dynamics")));
        songs.add(drums("enter-sandman-drums", "Enter Sandman", "Metallica",
                123, 3, "Groove e viradas curtas",
                List.of("drum-kick-variations", "drum-fill-timing")));
        songs.add(drums("hail-to-the-king-drums", "Hail to the King", "Avenged Sevenfold",
                118, 3, "Peso e consistência",
                List.of("drum-groove-consistency", "drum-fill-orchestration")));
        songs.add(drums("so-far-away-drums", "So Far Away", "Avenged Sevenfold",
                68, 3, "Balada com dinâmica",
                List.of("drum-dynamics", "drum-song-form")));
        songs.add(drums("black-or-white-drums", "Black or White", "Michael Jackson",
                115, 3, "Groove dançante",
                List.of("drum-syncopated-kick", "drum-ghost-notes")));
        songs.add(drums("beat-it-drums", "Beat It", "Michael Jackson",
                139, 3, "Pulso firme e transições",
                List.of("drum-groove-consistency", "drum-fill-timing")));
        songs.add(drums("you-shook-me-drums", "You Shook Me All Night Long", "AC/DC",
                127, 3, "Groove completo de rock",
                List.of("drum-kick-variations", "drum-one-beat-fill")));
        songs.add(drums("sweet-child-drums", "Sweet Child O' Mine", "Guns N' Roses",
                126, 4, "Forma e viradas",
                List.of("drum-song-form", "drum-fill-orchestration")));
        songs.add(drums("master-puppets-drums", "Master of Puppets", "Metallica",
                212, 5, "Resistência em velocidade reduzida",
                List.of("drum-sixteenth-groove", "drum-fill-orchestration", "drum-play-along")));
        songs.add(drums("nightmare-drums", "Nightmare", "Avenged Sevenfold",
                130, 5, "Coordenação e bumbo avançado",
                List.of("drum-limb-independence", "drum-syncopated-kick", "drum-play-along")));

        return songs;
    }

    private static Song guitar(String id, String title, String artist, String tuning, String key, int bpm,
                               int difficulty, String focus, List<String> skills, String tone) {
        var studyBpm = studyBpm(bpm, difficulty);
        return new Song(id, title, artist, tuning, key, bpm, InstrumentId.GUITAR, difficulty, "backlog",
                "Comece por uma seção reconhecível. A versão completa só entra depois que o primeiro trecho estiver estável.",
                0, List.of(focus), List.of(), List.of(
                new SongSection("first-part", "Primeiro trecho", 0, studyBpm,
                        focus + ". Toque somente esta parte por enquanto.", skills, null,
                        null, null, tone),
                new SongSection("simple-version", "Versão simplificada", 0, studyBpm,
                        "Atravesse a música sem solos e sem tentar copiar todos os detalhes.",
                        merge(skills, "song-sections"), null, null, null, tone),
                new SongSection("full-version", "Versão completa", 0, bpm,
                        "Inclua transições, dinâmica e detalhes somente nesta etapa.",
                        merge(skills, "performance"), null, null, null, tone)
        ));
    }

    private static Song drums(String id, String title, String artist, int bpm, int difficulty,
                              String focus, List<String> skills) {
        var studyBpm = studyBpm(bpm, difficulty);
        return new Song(id, title, artist, "Kit padrão", "—", bpm, InstrumentId.DRUMS, difficulty, "backlog",
                "Primeiro mantenha um groove simplificado. As viradas da gravação são uma etapa posterior.",
                0, List.of(focus), List.of(), List.of(
                new SongSection("main-groove", "Groove principal", 0, studyBpm,
                        focus + ". Sem viradas nesta etapa.", skills,
                        "HH|x-x-x-x-|\nSD|--o---o-|\nBD|o---o---|", null, null),
                new SongSection("simple-version", "Música simplificada", 0, studyBpm,
                        "Continue tocando depois dos erros e use uma virada de um tempo.",
                        merge(skills, "drum-one-beat-fill"),
                        "HH|x-x-x-x-|\nSD|--o---oo|\nBD|o---o---|", null, null),
                new SongSection("full-version", "Versão completa", 0, bpm,
                        "Estude forma, dinâmica e viradas próximas da gravação.",
                        merge(skills, "drum-play-along"), null, null, null)
        ));
    }

    private static Song acoustic(String id, String title, String artist, String tuning, String key, int bpm,
                                 int difficulty, String focus, List<String> skills) {
        var studyBpm = studyBpm(bpm, difficulty);
        return new Song(id, title, artist, tuning, key, bpm, InstrumentId.ACOUSTIC, difficulty, "backlog",
                "Comece por duas trocas ou uma levada curta. O arranjo da gravação é a última etapa.",
                0, List.of(focus), List.of(), List.of(
                new SongSection("first-change", "Primeira troca", 0, studyBpm,
                        focus + ". Use somente duas formas até o pulso ficar estável.",
                        skills, null, null, null),
                new SongSection("simple-accompaniment", "Acompanhamento simples", 0, studyBpm,
                        "Atravesse a música com uma única levada e sem detalhes de introdução.",
                        merge(skills, "song-sections"), null, null, null),
                new SongSection("full-arrangement", "Arranjo completo", 0, bpm,
                        "Inclua dedilhado, dinâmica, pestanas e transições da gravação apenas agora.",
                        merge(skills, "performance"), null, null, null)
        ));
    }

    private static Song keys(String id, String title, String artist, String key, int bpm,
                             int difficulty, String focus, List<String> skills) {
        var studyBpm = studyBpm(bpm, difficulty);
        return new Song(id, title, artist, "Teclado padrão", key, bpm, InstrumentId.KEYS, difficulty, "backlog",
                "Aprenda primeiro uma melodia ou blocos de acordes. A independência das mãos vem depois.",
                0, List.of(focus), List.of(), List.of(
                new SongSection("one-hand", "Uma mão", 0, studyBpm,
                        focus + ". Use a mão direita e conte antes de acrescentar acordes.",
                        skills, null, null, null),
                new SongSection("simple-hands", "Duas mãos simples", 0, studyBpm,
                        "Mão esquerda em notas longas; mão direita mantém o tema ou os acordes.",
                        merge(skills, "keys-independence"), null, null, null),
                new SongSection("full-arrangement", "Arranjo completo", 0, bpm,
                        "Inclua voicings, dinâmica, timbre e forma completa nesta etapa.",
                        merge(skills, "performance"), null, null, null)
        ));
    }

    private static int studyBpm(int originalBpm, int difficulty) {
        var ceiling = switch (difficulty) {
            case 1 -> 70;
            case 2 -> 80;
            case 3 -> 90;
            case 4 -> 100;
            default -> 110;
        };
        return Math.max(50, Math.min(originalBpm, ceiling));
    }

    private static List<String> merge(List<String> values, String extra) {
        var result = new ArrayList<>(values);
        result.add(extra);
        return result;
    }
}
