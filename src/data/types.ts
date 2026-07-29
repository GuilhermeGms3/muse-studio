export type InstrumentId = "guitar" | "acoustic" | "keys";

export interface Instrument {
  id: InstrumentId;
  name: string;
  short: string;
  focus: string[];
}

export type SkillState =
  | "locked"
  | "available"
  | "learning"
  | "practicing"
  | "consistent"
  | "mastered"
  | "natural"
  | "expert";

export interface Skill {
  id: string;
  name: string;
  domain: string;
  instruments: InstrumentId[];
  requires: string[];
  unlocks: string[];
  state: SkillState;
  hours: number;
  bpm?: { current: number; target: number };
  accuracy: number;
  notes?: string;
  library?: string[];
  exercises?: string[];
  songs?: string[];
}

export interface LibraryNode {
  id: string;
  title: string;
  category: string;
  summary: string;
  body: string[];
  diagram?: { type: "fretboard" | "keyboard"; notes: number[]; label: string };
  examples?: string[];
  related?: string[];
  skills?: string[];
}

export interface PlanBlock {
  id: string;
  minutes: number;
  title: string;
  kind: "warmup" | "technique" | "theory" | "ear" | "repertoire" | "creative";
  instrument: InstrumentId;
  target?: string;
  done?: boolean;
}

export interface SongSection {
  id: string;
  name: string;
  progress: number;
  bpm?: number;
  note?: string;
}

export interface Song {
  id: string;
  title: string;
  artist: string;
  tuning: string;
  key: string;
  bpm: number;
  instrument: InstrumentId;
  difficulty: 1 | 2 | 3 | 4 | 5;
  techniques: string[];
  scales: string[];
  status: "backlog" | "learning" | "polishing" | "performance";
  notes: string;
  sections: SongSection[];
}

export interface Exercise {
  id: string;
  name: string;
  technique: string;
  instrument: InstrumentId;
  bpmTarget: number;
  bpmCurrent: number;
  minutes: number;
  description: string;
  skill?: string;
}

export interface JournalEntry {
  id: string;
  date: string;
  duration: string;
  instrument: InstrumentId;
  worked: string[];
  difficulties: string;
  improvements: string;
}

export interface MusicProject {
  id: string;
  name: string;
  key: string;
  bpm: number;
  status: "sketch" | "arranging" | "recording" | "archived";
  riffs: { id: string; name: string; tab: string }[];
  lyrics: string;
  ideas: string[];
  versions: { id: string; label: string; date: string }[];
  references: string[];
}
