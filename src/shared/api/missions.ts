import { apiRequest, useApiQuery } from "./client";
import type {
  AssessmentAttempt,
  AssessmentCriterionResult,
  AssessmentObserverType,
  InstrumentId,
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
