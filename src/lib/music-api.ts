import { useQuery } from "@tanstack/react-query";

export type InstrumentId = "guitar" | "acoustic" | "keys";
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

const API_URL = (import.meta.env.VITE_MUSIC_OS_API_URL ?? "http://127.0.0.1:8081/api/v1").replace(
  /\/$/,
  "",
);

export async function apiRequest<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${API_URL}${path}`, {
    ...init,
    headers: {
      "Content-Type": "application/json",
      ...init?.headers,
    },
  });
  if (!response.ok) {
    const payload = (await response.json().catch(() => null)) as { message?: string } | null;
    throw new Error(payload?.message ?? `A API respondeu com ${response.status}.`);
  }
  if (response.status === 204) return undefined as T;
  return response.json() as Promise<T>;
}

const useApiQuery = <T>(key: readonly unknown[], path: string) =>
  useQuery({ queryKey: key, queryFn: () => apiRequest<T>(path) });

export const useHomeData = (instrument: InstrumentId) =>
  useApiQuery<HomeData>(["home", instrument], `/home?instrument=${instrument}`);
export const useInstruments = () => useApiQuery<Instrument[]>(["instruments"], "/instruments");
export const useTodayPlan = (instrument: InstrumentId) =>
  useApiQuery<PlanActivity[]>(["plan", instrument], `/plans/today?instrument=${instrument}`);
export const useSkills = (instrument: InstrumentId) =>
  useApiQuery<Skill[]>(["skills", instrument], `/skills?instrument=${instrument}`);
export const useLibrary = () => useApiQuery<LibraryContent[]>(["library"], "/library");
export const useSongs = () => useApiQuery<Song[]>(["songs"], "/songs");
export const useExercises = (instrument: InstrumentId) =>
  useApiQuery<Exercise[]>(["exercises", instrument], `/exercises?instrument=${instrument}`);
export const useProjects = () => useApiQuery<MusicProject[]>(["projects"], "/projects");
export const useJournal = () => useApiQuery<JournalEntry[]>(["journal"], "/journal");
export const useEarStats = () =>
  useApiQuery<EarTrainingStats>(["ear-stats"], "/ear-training/stats");
export const usePreferences = () => useApiQuery<UserPreferences>(["preferences"], "/preferences");
export const useDataStatus = () => useApiQuery<DataStatus>(["data-status"], "/data/status");

export const updateActivity = (id: string, done: boolean) =>
  apiRequest<PlanActivity>(`/plans/activities/${id}`, {
    method: "PATCH",
    body: JSON.stringify({ done }),
  });

export const updateSkillState = (id: string, state: SkillState) =>
  apiRequest<Skill>(`/skills/${id}/state`, {
    method: "PATCH",
    body: JSON.stringify({ state }),
  });

export const recordSkillEvidence = (
  id: string,
  evidence: {
    hours: number;
    accuracy?: number;
    bpm?: number;
    review?: boolean;
    exerciseCompleted?: boolean;
    songCompleted?: boolean;
    selfRating?: number;
    perceivedDifficulty?: number;
  },
) =>
  apiRequest<Skill>(`/skills/${id}/evidence`, {
    method: "POST",
    body: JSON.stringify(evidence),
  });

export const recordExerciseAttempt = (
  id: string,
  attempt: {
    bpm: number;
    accuracy: number;
    durationSeconds: number;
    repetitions: number;
    perceivedDifficulty: number;
  },
) =>
  apiRequest<ExerciseAttempt>(`/exercises/${id}/attempts`, {
    method: "POST",
    body: JSON.stringify(attempt),
  });

export const recordEarAttempt = (attempt: {
  module: string;
  prompt: string;
  answer: string;
  correct: boolean;
  responseMillis: number;
  difficulty: number;
}) =>
  apiRequest<EarTrainingStats>("/ear-training/attempts", {
    method: "POST",
    body: JSON.stringify(attempt),
  });

export const savePreferences = (preferences: UserPreferences) =>
  apiRequest<UserPreferences>("/preferences", {
    method: "PUT",
    body: JSON.stringify(preferences),
  });

export const completeDiagnostic = (diagnostic: {
  instrument: InstrumentId;
  level: string;
  sessionMinutes: number;
  favoriteGenres: string[];
  favoriteArtists: string[];
  favoriteSongs: string[];
  rhythmScore: number;
  earScore: number;
  techniqueScore: number;
}) =>
  apiRequest<DiagnosticResult>("/diagnostic", {
    method: "POST",
    body: JSON.stringify(diagnostic),
  });

export const getSongRecommendations = (skill: string, instrument: InstrumentId) =>
  apiRequest<SongRecommendation[]>(
    `/recommendations/songs?skill=${encodeURIComponent(skill)}&instrument=${instrument}`,
  );

export const saveLesson = (lesson: LibraryContent) =>
  apiRequest<LibraryContent>(`/library/${lesson.id}`, {
    method: "PUT",
    body: JSON.stringify(lesson),
  });

export const saveExercise = (exercise: Exercise) =>
  apiRequest<Exercise>(`/exercises/${exercise.id}`, {
    method: "PUT",
    body: JSON.stringify(exercise),
  });

export const saveSong = (song: Song) =>
  apiRequest<Song>(`/songs/${song.id}`, {
    method: "PUT",
    body: JSON.stringify(song),
  });

export const createSongPracticePlan = (songId: string, minutes: number) =>
  apiRequest<PlanActivity[]>(`/songs/${songId}/practice-plan?minutes=${minutes}`, {
    method: "POST",
  });

export const saveProject = (project: MusicProject) =>
  apiRequest<MusicProject>(`/projects/${project.id}`, {
    method: "PUT",
    body: JSON.stringify(project),
  });

export const deleteResource = (kind: "library" | "exercises" | "songs" | "projects", id: string) =>
  apiRequest<void>(`/${kind}/${id}`, { method: "DELETE" });

export const startPracticeSession = (instrument: InstrumentId, availableMinutes?: number) =>
  apiRequest<PracticeSession>("/sessions", {
    method: "POST",
    body: JSON.stringify({ instrument, availableMinutes }),
  });

export const recordSessionActivity = (
  sessionId: string,
  activityId: string,
  body: {
    feedback: "easy" | "adequate" | "hard";
    bpm: number;
    accuracy: number;
    durationSeconds: number;
    timingOffsetMillis: number;
  },
) =>
  apiRequest<ActivityResult>(`/sessions/${sessionId}/activities/${activityId}/result`, {
    method: "POST",
    body: JSON.stringify(body),
  });

export const getSessionSummary = (sessionId: string) =>
  apiRequest<SessionSummary>(`/sessions/${sessionId}/summary`);

export const getRecordings = (contextType: string, contextId: string) =>
  apiRequest<PracticeRecording[]>(
    `/recordings?contextType=${encodeURIComponent(contextType)}&contextId=${encodeURIComponent(contextId)}`,
  );

export const recordingAudioUrl = (recording: PracticeRecording) =>
  `${new URL(API_URL).origin}${recording.audioUrl}`;

export async function uploadRecording(
  file: Blob,
  values: {
    contextType: string;
    contextId: string;
    durationMillis: number;
    targetBpm?: number;
    measuredBpm?: number;
    timingOffsetMillis?: number;
    rhythmStability?: number;
    targetNote?: string;
    pitchOffsetCents?: number;
    bendStability?: number;
  },
) {
  const form = new FormData();
  form.append("file", file, `practice-${Date.now()}.webm`);
  Object.entries(values).forEach(([key, value]) => {
    if (value !== undefined) form.append(key, String(value));
  });
  const response = await fetch(`${API_URL}/recordings`, { method: "POST", body: form });
  if (!response.ok) throw new Error("Não foi possível salvar a gravação.");
  return response.json() as Promise<PracticeRecording>;
}

export async function downloadDataFile(path: "/data/backup" | "/data/journal.csv") {
  const response = await fetch(`${API_URL}${path}`);
  if (!response.ok) throw new Error("Não foi possível exportar os dados.");
  return response.blob();
}

export const restoreBackup = (snapshot: unknown) =>
  apiRequest<{
    lessons: number;
    exercises: number;
    songs: number;
    projects: number;
    message: string;
  }>("/data/restore", { method: "POST", body: JSON.stringify(snapshot) });

export async function importMusicFile(file: File) {
  const form = new FormData();
  form.append("file", file);
  const response = await fetch(`${API_URL}/data/imports`, { method: "POST", body: form });
  if (!response.ok) {
    const payload = (await response.json().catch(() => null)) as { message?: string } | null;
    throw new Error(payload?.message ?? "Não foi possível importar o arquivo.");
  }
  return response.json() as Promise<ImportedFile>;
}

export const updatePracticeSession = (
  id: string,
  body: {
    elapsedSeconds: number;
    currentActivityIndex: number;
    notes: string;
    status: "active" | "paused";
  },
) =>
  apiRequest<PracticeSession>(`/sessions/${id}`, {
    method: "PATCH",
    body: JSON.stringify(body),
  });

export const finishPracticeSession = (
  id: string,
  body: { elapsedSeconds: number; notes: string; difficulties?: string; improvements?: string },
) =>
  apiRequest<PracticeSession>(`/sessions/${id}/finish`, {
    method: "POST",
    body: JSON.stringify(body),
  });
