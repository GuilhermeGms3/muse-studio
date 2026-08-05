import { useApiQuery } from "./client";
import type { InstrumentId, MissionWorkspaceData } from "./contracts";

export const useMission = (id: string, instrument: InstrumentId) =>
  useApiQuery<MissionWorkspaceData>(
    ["mission", id, instrument],
    `/missions/${encodeURIComponent(id)}?instrument=${instrument}`,
  );
