export type InstrumentId = "guitar" | "acoustic" | "keys" | "drums";
export type LearningStage =
  | "first_steps"
  | "beginner"
  | "beginner_advanced"
  | "early_intermediate"
  | "intermediate"
  | "upper_intermediate"
  | "advanced";
export type SkillKind = "knowledge" | "ability";
export type LearningTrack =
  | "technique"
  | "rhythm"
  | "ear"
  | "reading"
  | "harmony"
  | "repertoire"
  | "improvisation"
  | "creation"
  | "performance";
export type SkillState =
  | "locked"
  | "available"
  | "learning"
  | "practicing"
  | "consistent"
  | "mastered"
  | "natural"
  | "expert";

export interface Instrument {
  id: InstrumentId;
  name: string;
  shortName: string;
  focus: string[];
}

export interface PlanActivity {
  id: string;
  scheduledFor: string;
  position: number;
  minutes: number;
  title: string;
  kind: "warmup" | "technique" | "theory" | "ear" | "repertoire" | "creative";
  instrument: InstrumentId;
  target?: string;
  done: boolean;
  skillId?: string;
}

export interface Skill {
  id: string;
  friendlyTitle: string;
  technicalName: string;
  domain: string;
  stage: LearningStage;
  kind: SkillKind;
  track: LearningTrack;
  description: string;
  state: SkillState;
  hours: number;
  accuracy: number;
  currentBpm?: number;
  targetBpm?: number;
  instruments: InstrumentId[];
  prerequisites: string[];
  contents: string[];
  exercises: string[];
  songs: string[];
  nextSkills: string[];
  practiceDays: number;
  reviewCount: number;
  exerciseCompletions: number;
  songsCompleted: number;
  selfRating: number;
  lastPracticedAt?: string;
  progress: number;
  nextRequirements: string[];
  retention: number;
  reviewIntervalDays: number;
  nextReviewAt?: string;
}

export interface LessonStep {
  title: string;
  explanation: string;
  musicalExample?: string;
  notation?: string;
  tablature?: string;
  audioNotes?: string;
}

export interface LibraryContent {
  id: string;
  friendlyTitle: string;
  technicalName: string;
  category: string;
  summary: string;
  body: string[];
  examples: string[];
  related: string[];
  skillId?: string;
  level: "beginner" | "intermediate" | "advanced";
  estimatedMinutes: number;
  diagramType?: "fretboard" | "keyboard" | "rhythm";
  diagramData?: string;
  tablature?: string;
  objectives: string[];
  commonMistakes: string[];
  steps: LessonStep[];
}

export interface SongSection {
  id: string;
  name: string;
  progress: number;
  bpm?: number;
  note?: string;
  skillIds: string[];
  tablature?: string;
  startSeconds?: number;
  endSeconds?: number;
  tonePreset?: string;
}

export interface Song {
  id: string;
  title: string;
  artist: string;
  tuning: string;
  musicalKey: string;
  bpm: number;
  instrument: InstrumentId;
  difficulty: number;
  status: string;
  notes: string;
  progress: number;
  techniques: string[];
  scales: string[];
  sections: SongSection[];
}

export interface PracticeTabSection {
  id: string;
  name: string;
  bpm?: number;
  tablature: string;
}

export interface PracticeInstrument {
  instrument: InstrumentId;
  label: string;
  available: boolean;
  localSongId?: string;
  bpm?: number;
  tablatureUrl: string;
  tablature: PracticeTabSection[];
  videos: SongRecommendation[];
  vocalTracks: SongRecommendation[];
  backingTracks: SongRecommendation[];
}

export interface PracticeSong {
  id: string;
  title: string;
  artist: string;
  thumbnailUrl: string;
  instruments: PracticeInstrument[];
}

export interface Exercise {
  id: string;
  name: string;
  technique: string;
  instrument: InstrumentId;
  targetBpm: number;
  currentBpm: number;
  minutes: number;
  description: string;
  skillId?: string;
  difficulty: number;
  minBpm: number;
  bpmStep: number;
  passAccuracy: number;
  passRepetitions: number;
  instructions: string[];
  variations: {
    name: string;
    instructions: string;
    bpmOffset: number;
    durationMinutes: number;
  }[];
  activityType:
    | "understand"
    | "imitate"
    | "slow"
    | "context"
    | "song"
    | "recall"
    | "record"
    | "transfer"
    | "execute";
  stage: LearningStage;
  videoQuery?: string | null;
  readingTitle?: string | null;
  readingUrl?: string | null;
  readingNote?: string | null;
  practiceSongQuery?: string | null;
}

export interface ExerciseAttempt {
  id: string;
  exerciseId: string;
  practicedAt: string;
  bpm: number;
  accuracy: number;
  durationSeconds: number;
  repetitions: number;
  perceivedDifficulty: number;
  passed: boolean;
}

export interface EarTrainingStats {
  totalAttempts: number;
  accuracy: number;
  modules: {
    module: string;
    attempts: number;
    correct: number;
    accuracy: number;
    averageResponseMillis: number;
    recommendedDifficulty: number;
    focusPrompt?: string;
  }[];
}

export interface UserPreferences {
  level: string;
  sessionMinutes: number;
  favoriteGenres: string[];
  favoriteArtists: string[];
  favoriteSongs: string[];
  primaryInstrument: InstrumentId;
  onboardingCompleted: boolean;
  rhythmBaseline: number;
  earBaseline: number;
  techniqueBaseline: number;
}

export interface DiagnosticResult {
  profile: UserPreferences;
  startingSkills: Skill[];
  recommendation: string;
}

export interface SongRecommendation {
  videoId: string;
  title: string;
  channel: string;
  thumbnailUrl: string;
  reason: string;
  youtubeUrl: string;
}

export interface MusicProject {
  id: string;
  name: string;
  musicalKey: string;
  bpm: number;
  status: string;
  lyrics: string;
  ideas: string[];
  references: string[];
  riffs: { id: string; name: string; tab: string }[];
  versions: { id: string; label: string; date: string }[];
}

export interface JournalEntry {
  id: string;
  practicedAt: string;
  durationSeconds: number;
  instrument: InstrumentId;
  worked: string[];
  difficulties: string;
  improvements: string;
  notes: string;
}

export interface HomeData {
  greeting: string;
  message: string;
  expectedMinutes: number;
  todayPlan: PlanActivity[];
  continueFrom: { type: "song" | "library"; id: string; title: string; subtitle: string };
  currentObjective: {
    id: string;
    title: string;
    technicalName: string;
    progress: number;
    state: SkillState;
  };
  streakDays: number;
}

export interface PracticeSession {
  id: string;
  instrument: InstrumentId;
  status: "active" | "paused" | "finished";
  startedAt: string;
  finishedAt?: string;
  elapsedSeconds: number;
  currentActivityIndex: number;
  notes: string;
  activities: PlanActivity[];
}

export interface ActivityResult {
  id: string;
  activityId: string;
  title: string;
  feedback: "easy" | "adequate" | "hard";
  bpm: number;
  accuracy: number;
  durationSeconds: number;
  timingOffsetMillis: number;
  suggestedBpm: number;
  adaptation: string;
}

export interface SessionSummary {
  sessionId: string;
  practicedSeconds: number;
  improvements: string[];
  difficulties: string[];
  peakBpm: number;
  averageAccuracy: number;
  recommendation: string;
  activities: ActivityResult[];
}

export interface PracticeRecording {
  id: string;
  createdAt: string;
  contextType: string;
  contextId: string;
  originalName: string;
  mimeType: string;
  durationMillis: number;
  targetBpm?: number;
  measuredBpm?: number;
  timingOffsetMillis?: number;
  rhythmStability?: number;
  targetNote?: string;
  pitchOffsetCents?: number;
  bendStability?: number;
  audioUrl: string;
}

export interface DataStatus {
  dataDirectory: string;
  lessons: number;
  exercises: number;
  songs: number;
  projects: number;
  journalEntries: number;
  recordings: number;
}

export interface ImportedFile {
  id: string;
  name: string;
  type: "audio" | "midi" | "musicxml" | "guitar-pro";
  size: number;
  importedAt: string;
  storedPath: string;
}
