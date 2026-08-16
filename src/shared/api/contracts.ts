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
    | "listen"
    | "compare"
    | "execute";
  stage: LearningStage;
  videoQuery?: string | null;
  readingTitle?: string | null;
  readingUrl?: string | null;
  readingNote?: string | null;
  practiceSongQuery?: string | null;
  observableObjective?: string | null;
  practiceConditions?: string | null;
  successCriteria?: string | null;
  competencyIds: string[];
}

export interface MissionLesson {
  id: string;
  title: string;
  technicalName: string;
  category: string;
  summary: string;
  content: string;
  estimatedMinutes: number;
  stage: LearningStage;
  format: "TEXT" | "VIDEO" | "AUDIO" | "INTERACTIVE" | "MIXED";
  competencyIds: string[];
  objectives: string[];
  examples: string[];
  material?: LibraryContent;
}

export interface MissionAssessment {
  id: string;
  title: string;
  purpose: string;
  type: string;
  protocolVersion: string;
  instructions: string;
  conditions: string;
  allowedSupport: string;
  inconclusiveRule: string;
  estimatedMinutes: number;
  maximumAttempts: number;
  active: boolean;
  competencyIds: string[];
  criterionKeys: string[];
  rubricLevels: Array<{
    criterionKey: string;
    band: string;
    description: string;
  }>;
}

export type AssessmentObserverType = "SELF" | "EXTERNAL";
export type AssessmentCriterionResult = "SUPPORTS" | "CHALLENGES" | "INCONCLUSIVE";

export interface AssessmentAttemptResult {
  evidenceId: string;
  competencyId: string;
  criterionKey: string;
  result: AssessmentCriterionResult;
  state: string;
  reliability: string;
  confidence: string;
  nextObservation: string;
}

export interface AssessmentAttempt {
  id: string;
  assessmentId: string;
  observerType: AssessmentObserverType;
  completedAt: string;
  artifactReference?: string;
  results: AssessmentAttemptResult[];
}

export interface LearningEvidence {
  id: string;
  competencyId: string;
  criterionKey: string;
  type: string;
  state: string;
  reliability: string;
  result: string;
  sourceType: string;
  sourceId: string;
  challengeLevel: number;
  observation: string;
  conditions: string;
  occurredAt: string;
  validUntil?: string;
}

export interface MissionPrerequisite {
  competencyId: string;
  title: string;
  type: string;
  depth: number;
  direct: boolean;
  satisfied: boolean;
  blocking: boolean;
  reason: string;
}

export interface MissionCompetency {
  competencyId: string;
  title: string;
  observableAction: string;
  observationConditions: string;
  curriculumStatus: string;
  masteryState: string;
  unlocked: boolean;
  curriculumReason: string;
  evidenceConfidence: string;
  missingCriteria: string[];
  nextObservation: string;
}

export interface MissionCoach {
  missionStatus: string;
  whyMission: string;
  citedEvidence: CoachEvidence[];
  nextStatus: string;
  nextMessage: string;
  nextRecommendations: CoachRecommendation[];
}

export interface MissionWorkspaceData {
  mission: {
    id: string;
    curriculumId: string;
    title: string;
    observableObjective: string;
    context: string;
    motivation: string;
    estimatedMinutes: number;
    instrument?: InstrumentId;
    stage: LearningStage;
    status: string;
    completionCriteria: string;
    expectedEvidence: string;
    musicalApplication?: string;
    easierMissionId?: string;
    competencyIds: string[];
  };
  lessons: MissionLesson[];
  exercises: Exercise[];
  repertoire: Song[];
  assessments: MissionAssessment[];
  feedback: ExerciseAttempt[];
  evidence: LearningEvidence[];
  prerequisites: MissionPrerequisite[];
  competencies: MissionCompetency[];
  coach: MissionCoach;
  experience?: MissionExperience;
}

export type MissionActivityKind =
  "ORIENTATION" | "LESSON" | "EXERCISE" | "APPLICATION" | "REFLECTION";

export interface MissionExperience {
  id: string;
  missionId: string;
  instrumentProfileId: string;
  status: "IN_PROGRESS" | "PAUSED" | "COMPLETED";
  currentActivityKind: MissionActivityKind;
  currentActivityId: string;
  lastRecordingId?: string;
  assessmentAttemptId?: string;
  startedAt: string;
  updatedAt: string;
  pausedAt?: string;
  completedAt?: string;
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
  missionExperienceId?: string;
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
  continueFrom?: { type: "song" | "library"; id: string; title: string; subtitle: string };
  currentObjective?: {
    id: string;
    title: string;
    technicalName: string;
    progress: number;
    state: SkillState;
  };
  streakDays: number;
  coach?: CoachHome;
  learningExperience?: MissionExperience;
}

export interface JourneyMission {
  id: string;
  title: string;
  estimatedMinutes: number;
  stage: LearningStage;
  competencyIds: string[];
}

export interface JourneyCompetency {
  competencyId: string;
  title: string;
  pathPosition: number;
  stage: LearningStage;
  status: "BLOCKED" | "AVAILABLE" | "IN_PROGRESS" | "ESTABLISHED" | "REVIEW_DUE";
  masteryState: string;
  unlocked: boolean;
  reason: string;
  missions: JourneyMission[];
}

export interface JourneyData {
  instrument: InstrumentId;
  curriculumId: string;
  evaluatedAt: string;
  position: {
    totalCompetencies: number;
    establishedCompetencies: number;
    inProgressCompetencies: number;
    availableCompetencies: number;
    blockedCompetencies: number;
    reviewsDue: number;
    focusCompetencyId?: string;
    explanation: string;
  };
  competencies: JourneyCompetency[];
  reviews: Array<{
    competencyId: string;
    title: string;
    kind: string;
    pathPosition: number;
    reason: string;
  }>;
  nextSteps: Array<{
    competencyId: string;
    title: string;
    kind: string;
    pathPosition: number;
    reason: string;
  }>;
}

export interface LearningHistoryItem {
  id: string;
  kind: "MISSION" | "ATTEMPT" | "RECORDING" | "EVIDENCE" | "SESSION";
  title: string;
  detail: string;
  occurredAt: string;
  missionId?: string;
  sourceId: string;
  status: string;
}

export type CoachStatus = "GROUNDED" | "INSUFFICIENT_EVIDENCE" | "NO_ELIGIBLE_MISSION";

export interface CoachGoal {
  id: string;
  title: string;
  desiredOutcome: string;
  musicalContext: string;
  type: string;
  priority: number;
  targetDate?: string;
}

export interface CoachEvidence {
  id: string;
  competencyId: string;
  criterionKey: string;
  reliability: string;
  result: string;
  occurredAt: string;
  observation: string;
  conditions: string;
}

export interface CoachRecommendation {
  missionId?: string;
  title: string;
  competencyId: string;
  kind: string;
  estimatedMinutes?: number;
  observableObjective: string;
  expectedEvidence: string;
  goals: CoachGoal[];
  evidence: CoachEvidence[];
  explanation: string;
}

export interface CoachHome {
  status: CoachStatus;
  profile: {
    instrumentProfileId: string;
    instrument: InstrumentId;
    stage: LearningStage;
    curriculumId: string;
  };
  evaluatedAt: string;
  availableMinutes?: number;
  activeGoals: CoachGoal[];
  recommendations: CoachRecommendation[];
  message: string;
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

export type StudioSourceKind = "FREE" | "MISSION" | "REPERTOIRE" | "PRACTICE" | "CREATION";
export type StudioTrackRole = "BACKING" | "REFERENCE" | "RECORDING" | "CLICK" | "CREATIVE";
export type StudioRegionOrigin = "MISSION" | "EXERCISE" | "REPERTOIRE" | "USER";

export interface StudioTrack {
  id: string;
  name: string;
  role: StudioTrackRole;
  muted: boolean;
  solo: boolean;
  volume: number;
  pan: number;
  externalTrackId?: string;
}

export interface StudioClip {
  id: string;
  trackId: string;
  title: string;
  sourceKind: "RECORDING" | "MANAGED_AUDIO" | "EXTERNAL_REFERENCE";
  recordingId?: string;
  sourceReference?: string;
  audioUrl?: string;
  startSeconds: number;
  offsetSeconds: number;
  durationSeconds: number;
}

export interface StudioRegion {
  id: string;
  name: string;
  startSeconds: number;
  endSeconds: number;
  origin: StudioRegionOrigin;
}

export interface StudioMarker {
  id: string;
  name: string;
  positionSeconds: number;
  origin: StudioRegionOrigin;
}

export interface StudioTake {
  id: string;
  trackId: string;
  recordingId: string;
  title: string;
  preferred: boolean;
  createdAt: string;
  externalTakeId?: string;
  audioUrl: string;
}

export interface StudioProject {
  id: string;
  title: string;
  instrument: InstrumentId;
  sourceKind: StudioSourceKind;
  sourceId?: string;
  missionId?: string;
  missionExperienceId?: string;
  exerciseId?: string;
  practiceSessionId?: string;
  songId?: string;
  musicProjectId?: string;
  bpm: number;
  timeSignatureNumerator: number;
  timeSignatureDenominator: number;
  countInBars: number;
  loopEnabled: boolean;
  selectedRegionId?: string;
  engineMode: "WEB" | "REAPER";
  externalProjectId?: string;
  externalProjectPath?: string;
  createdAt: string;
  updatedAt: string;
  tracks: StudioTrack[];
  clips: StudioClip[];
  regions: StudioRegion[];
  markers: StudioMarker[];
  takes: StudioTake[];
}

export interface ReaperStatus {
  status: "NOT_CONFIGURED" | "AVAILABLE" | "CONNECTED" | "DISCONNECTED" | "ERROR";
  configured: boolean;
  executablePath?: string;
  workspacePath?: string;
  message: string;
  checkedAt: string;
}
