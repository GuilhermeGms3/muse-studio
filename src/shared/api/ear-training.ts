import { apiRequest, useApiQuery } from "./client";
import type { EarTrainingStats } from "./contracts";

export const useEarStats = () =>
  useApiQuery<EarTrainingStats>(["ear-stats"], "/ear-training/stats");

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
