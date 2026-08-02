import { apiRequest, useApiQuery } from "./client";
import type {
  ActivityResult,
  InstrumentId,
  PlanActivity,
  PracticeSession,
  SessionSummary,
} from "./contracts";

export const useTodayPlan = (instrument: InstrumentId) =>
  useApiQuery<PlanActivity[]>(["plan", instrument], `/plans/today?instrument=${instrument}`);

export const updateActivity = (id: string, done: boolean) =>
  apiRequest<PlanActivity>(`/plans/activities/${id}`, {
    method: "PATCH",
    body: JSON.stringify({ done }),
  });

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
  body: {
    elapsedSeconds: number;
    notes: string;
    difficulties?: string;
    improvements?: string;
  },
) =>
  apiRequest<PracticeSession>(`/sessions/${id}/finish`, {
    method: "POST",
    body: JSON.stringify(body),
  });
