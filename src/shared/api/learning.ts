import { apiRequest } from "./client";
import type { InstrumentId, SongRecommendation } from "./contracts";

export const getExerciseRecommendations = (topic: string, instrument: InstrumentId) =>
  apiRequest<SongRecommendation[]>(
    `/recommendations/exercises?topic=${encodeURIComponent(topic)}&instrument=${instrument}`,
  );
