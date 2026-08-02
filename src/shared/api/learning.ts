import { apiRequest, useApiQuery } from "./client";
import type { InstrumentId, Skill, SkillState, SongRecommendation } from "./contracts";

export const useSkills = (instrument: InstrumentId) =>
  useApiQuery<Skill[]>(["skills", instrument], `/skills?instrument=${instrument}`);

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

export const getExerciseRecommendations = (topic: string, instrument: InstrumentId) =>
  apiRequest<SongRecommendation[]>(
    `/recommendations/exercises?topic=${encodeURIComponent(topic)}&instrument=${instrument}`,
  );
