import { useApiQuery } from "./client";
import type { InstrumentId, JourneyData, LearningHistoryItem } from "./contracts";

export const useJourney = (instrument: InstrumentId) =>
  useApiQuery<JourneyData>(["journey", instrument], `/journey?instrument=${instrument}`);

export const useLearningHistory = (instrument: InstrumentId) =>
  useApiQuery<LearningHistoryItem[]>(["history", instrument], `/history?instrument=${instrument}`);
