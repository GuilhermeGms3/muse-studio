import { apiRequest, useApiQuery } from "./client";
import type {
  AssessmentAttempt,
  AssessmentCriterionResult,
  AssessmentObserverType,
  InstrumentId,
  MissionActivityKind,
  MissionExperience,
  MissionWorkspaceData,
} from "./contracts";

export const useMission = (id: string, instrument: InstrumentId) =>
  useApiQuery<MissionWorkspaceData>(
    ["mission", id, instrument],
    `/missions/${encodeURIComponent(id)}?instrument=${instrument}`,
  );

export const recordAssessmentAttempt = (
  id: string,
  request: {
    instrument: InstrumentId;
    observerType: AssessmentObserverType;
    challengeLevel: number;
    note?: string;
    observations: Array<{
      criterionKey: string;
      result: AssessmentCriterionResult;
      observation: string;
    }>;
  },
) =>
  apiRequest<AssessmentAttempt>(`/assessments/${encodeURIComponent(id)}/attempts`, {
    method: "POST",
    body: JSON.stringify(request),
  });

export const startMissionExperience = (id: string, instrument: InstrumentId) =>
  apiRequest<MissionExperience>(`/missions/${encodeURIComponent(id)}/experience`, {
    method: "POST",
    body: JSON.stringify({ instrument }),
  });

export const updateMissionExperience = (
  id: string,
  request: {
    instrument: InstrumentId;
    activityKind: MissionActivityKind;
    activityId: string;
    recordingId?: string;
    pause?: boolean;
  },
) =>
  apiRequest<MissionExperience>(`/missions/${encodeURIComponent(id)}/experience`, {
    method: "PATCH",
    body: JSON.stringify(request),
  });

export const completeMissionExperience = (
  id: string,
  request: {
    instrument: InstrumentId;
    observerType: AssessmentObserverType;
    challengeLevel: number;
    recordingId?: string;
    note?: string;
    observations: Array<{
      criterionKey: string;
      result: AssessmentCriterionResult;
      observation: string;
    }>;
  },
) =>
  apiRequest<MissionExperience>(`/missions/${encodeURIComponent(id)}/experience/complete`, {
    method: "POST",
    body: JSON.stringify(request),
  });
