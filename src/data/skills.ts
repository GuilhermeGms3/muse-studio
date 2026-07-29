import type { Instrument, Skill } from "./types";

export const instruments: Instrument[] = [
  {
    id: "guitar",
    name: "Guitarra",
    short: "GTR",
    focus: ["Técnica", "Repertório", "Escalas", "Improvisação"],
  },
  {
    id: "acoustic",
    name: "Violão",
    short: "ACO",
    focus: ["Acordes", "Levadas", "Fingerstyle", "Repertório"],
  },
  {
    id: "keys",
    name: "Teclado",
    short: "KEY",
    focus: ["Leitura", "Independência", "Acordes", "Improvisação", "Escalas"],
  },
];

const s = (
  id: string,
  name: string,
  domain: string,
  state: Skill["state"],
  requires: string[],
  extra: Partial<Skill> = {},
): Skill => ({
  id,
  name,
  domain,
  instruments: extra.instruments ?? ["guitar", "acoustic", "keys"],
  requires,
  unlocks: [],
  state,
  hours: extra.hours ?? 0,
  accuracy: extra.accuracy ?? 0,
  ...extra,
});

const raw: Skill[] = [
  // Ritmo
  s("rhythm-basics", "Pulso e Compasso", "Ritmo", "natural", [], { hours: 62, accuracy: 96 }),
  s("subdivisions", "Subdivisões", "Ritmo", "mastered", ["rhythm-basics"], {
    hours: 41,
    accuracy: 90,
  }),
  s("syncopation", "Síncopa", "Ritmo", "practicing", ["subdivisions"], { hours: 12, accuracy: 71 }),
  s("polyrhythm", "Polirritmia", "Ritmo", "locked", ["syncopation", "independence"], {}),
  s("strumming", "Levadas", "Ritmo", "consistent", ["subdivisions"], {
    instruments: ["acoustic", "guitar"],
    hours: 30,
    accuracy: 84,
  }),

  // Leitura
  s("note-reading", "Leitura de Notas", "Leitura", "practicing", [], { hours: 18, accuracy: 68 }),
  s("rhythm-reading", "Leitura Rítmica", "Leitura", "learning", ["note-reading", "subdivisions"], {
    hours: 6,
    accuracy: 52,
  }),
  s("tab-reading", "Leitura de Tablatura", "Leitura", "natural", [], {
    instruments: ["guitar", "acoustic"],
    hours: 80,
    accuracy: 98,
  }),
  s("sight-reading", "Leitura à Primeira Vista", "Leitura", "locked", [
    "note-reading",
    "rhythm-reading",
  ]),

  // Harmonia / Teoria
  s("intervals", "Intervalos", "Harmonia", "mastered", [], { hours: 27, accuracy: 88 }),
  s("major-scale", "Escala Maior", "Escalas", "mastered", ["intervals"], {
    hours: 34,
    accuracy: 91,
  }),
  s("minor-scales", "Escalas Menores", "Escalas", "consistent", ["major-scale"], {
    hours: 19,
    accuracy: 80,
  }),
  s("pentatonic", "Pentatônicas", "Escalas", "consistent", ["intervals"], {
    hours: 44,
    accuracy: 86,
  }),
  s("triads", "Tríades", "Harmonia", "consistent", ["intervals"], { hours: 21, accuracy: 82 }),
  s("seventh-chords", "Tétrades", "Harmonia", "practicing", ["triads"], {
    hours: 9,
    accuracy: 64,
  }),
  s("harmonic-field", "Campo Harmônico", "Harmonia", "practicing", [
    "major-scale",
    "intervals",
    "triads",
  ], { hours: 11, accuracy: 66 }),
  s("modes", "Modos Gregos", "Escalas", "available", ["harmonic-field", "major-scale"]),
  s("reharmonization", "Rearmonização", "Harmonia", "locked", ["harmonic-field", "seventh-chords"]),
  s("voice-leading", "Condução de Vozes", "Harmonia", "locked", ["triads", "harmonic-field"]),
  s("chord-formation", "Formação de Acordes", "Harmonia", "consistent", ["intervals", "triads"], {
    hours: 24,
    accuracy: 83,
  }),

  // Técnica guitarra
  s("posture", "Postura e Alongamento", "Técnica", "natural", [], { hours: 50, accuracy: 95 }),
  s("sync", "Sincronização Mãos", "Técnica", "consistent", ["posture"], {
    hours: 33,
    accuracy: 85,
    bpm: { current: 132, target: 160 },
  }),
  s("alternate-picking", "Alternate Picking", "Técnica", "practicing", ["sync"], {
    instruments: ["guitar"],
    hours: 46,
    accuracy: 74,
    bpm: { current: 118, target: 150 },
  }),
  s("economy-picking", "Economy Picking", "Técnica", "available", ["alternate-picking"], {
    instruments: ["guitar"],
  }),
  s("hybrid-picking", "Hybrid Picking", "Técnica", "available", ["alternate-picking"], {
    instruments: ["guitar"],
  }),
  s("arpeggios", "Arpejos", "Técnica", "practicing", ["triads"], { hours: 14, accuracy: 70 }),
  s("sweep-picking", "Sweep Picking", "Técnica", "locked", [
    "alternate-picking",
    "arpeggios",
    "sync",
  ], { instruments: ["guitar"] }),
  s("string-skipping", "String Skipping", "Técnica", "locked", ["alternate-picking"], {
    instruments: ["guitar"],
  }),
  s("legato", "Legato", "Técnica", "practicing", ["sync"], {
    instruments: ["guitar"],
    hours: 16,
    accuracy: 72,
    bpm: { current: 104, target: 140 },
  }),
  s("hammer-pull", "Hammer-on / Pull-off", "Técnica", "consistent", ["posture"], {
    instruments: ["guitar", "acoustic"],
    hours: 22,
    accuracy: 84,
  }),
  s("tapping", "Tapping", "Técnica", "locked", ["legato", "hammer-pull"], {
    instruments: ["guitar"],
  }),
  s("bends", "Bends", "Técnica", "practicing", ["posture", "intervals"], {
    instruments: ["guitar"],
    hours: 13,
    accuracy: 61,
    notes: "Afinação instável acima do 12º traste.",
  }),
  s("vibrato", "Vibrato", "Técnica", "practicing", ["bends"], {
    instruments: ["guitar"],
    hours: 8,
    accuracy: 65,
  }),
  s("slides", "Slides", "Técnica", "consistent", ["posture"], {
    instruments: ["guitar", "acoustic"],
    hours: 11,
    accuracy: 81,
  }),
  s("palm-mute", "Palm Mute", "Técnica", "consistent", ["posture"], {
    instruments: ["guitar"],
    hours: 17,
    accuracy: 87,
  }),
  s("fingerstyle", "Fingerstyle", "Técnica", "learning", ["hammer-pull"], {
    instruments: ["acoustic"],
    hours: 7,
    accuracy: 55,
  }),
  s("speed", "Velocidade", "Técnica", "locked", ["alternate-picking", "legato"]),
  s("endurance", "Resistência", "Técnica", "available", ["sync", "posture"]),
  s("independence", "Independência de Mãos", "Técnica", "learning", ["posture"], {
    instruments: ["keys"],
    hours: 9,
    accuracy: 58,
  }),

  // Ear training
  s("ear-intervals", "Ouvido: Intervalos", "Ear Training", "practicing", ["intervals"], {
    hours: 15,
    accuracy: 73,
  }),
  s("ear-chords", "Ouvido: Acordes", "Ear Training", "learning", ["ear-intervals", "triads"], {
    hours: 5,
    accuracy: 49,
  }),
  s("ear-progressions", "Ouvido: Progressões", "Ear Training", "locked", [
    "ear-chords",
    "harmonic-field",
  ]),
  s("ear-melody", "Ouvido: Melodias", "Ear Training", "available", ["ear-intervals"]),
  s("ear-rhythm", "Ouvido: Ritmos", "Ear Training", "practicing", ["subdivisions"], {
    hours: 10,
    accuracy: 76,
  }),
  s("transcription", "Transcrição", "Ear Training", "locked", ["ear-melody", "ear-chords"]),

  // Improvisação / composição / performance
  s("improv-basics", "Improvisação Básica", "Improvisação", "practicing", [
    "pentatonic",
    "rhythm-basics",
  ], { hours: 20, accuracy: 68 }),
  s("phrasing", "Fraseado", "Improvisação", "learning", ["improv-basics", "vibrato"], {
    hours: 6,
    accuracy: 54,
  }),
  s("modal-improv", "Improvisação Modal", "Improvisação", "locked", ["modes", "improv-basics"]),
  s("targeting", "Notas Alvo", "Improvisação", "locked", ["harmonic-field", "improv-basics"]),
  s("composition", "Composição", "Composição", "available", ["harmonic-field", "phrasing"]),
  s("song-form", "Forma Musical", "Composição", "available", ["harmonic-field"]),
  s("arrangement", "Arranjo", "Composição", "locked", ["composition", "voice-leading"]),
  s("recording", "Gravação", "Performance", "available", ["song-form"]),
  s("live-performance", "Performance ao Vivo", "Performance", "locked", ["endurance", "phrasing"]),
];

// derive unlocks
const byId = new Map(raw.map((k) => [k.id, k]));
for (const skill of raw) {
  for (const req of skill.requires) {
    byId.get(req)?.unlocks.push(skill.id);
  }
}

export const skills: Skill[] = raw;
export const skillById = byId;

export const skillStateOrder: Skill["state"][] = [
  "locked",
  "available",
  "learning",
  "practicing",
  "consistent",
  "mastered",
  "natural",
  "expert",
];

export const skillStateLabel: Record<Skill["state"], string> = {
  locked: "Bloqueada",
  available: "Disponível",
  learning: "Aprendendo",
  practicing: "Praticando",
  consistent: "Consistente",
  mastered: "Dominada",
  natural: "Natural",
  expert: "Especialista",
};

export const domains = Array.from(new Set(skills.map((k) => k.domain)));
