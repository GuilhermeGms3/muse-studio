import { API_URL, apiRequest, useApiQuery } from "./client";
import { useQuery } from "@tanstack/react-query";
import type {
  InstrumentId,
  ReaperStatus,
  StudioProject,
  StudioSourceKind,
  StudioTake,
} from "./contracts";

export const useStudioProjects = () =>
  useApiQuery<StudioProject[]>(["studio-projects"], "/studio/projects");

export const useStudioProject = (id: string) =>
  useApiQuery<StudioProject>(["studio-project", id], `/studio/projects/${id}`);

export const createStudioProject = (values: {
  title?: string;
  instrument: InstrumentId;
  sourceKind: StudioSourceKind;
  sourceId?: string;
  bpm?: number;
  missionId?: string;
  missionExperienceId?: string;
  exerciseId?: string;
  practiceSessionId?: string;
  songId?: string;
  musicProjectId?: string;
}) =>
  apiRequest<StudioProject>("/studio/projects", { method: "POST", body: JSON.stringify(values) });

export const saveStudioProject = (project: StudioProject) =>
  apiRequest<StudioProject>(`/studio/projects/${project.id}`, {
    method: "PUT",
    body: JSON.stringify({
      title: project.title,
      bpm: project.bpm,
      timeSignatureNumerator: project.timeSignatureNumerator,
      timeSignatureDenominator: project.timeSignatureDenominator,
      countInBars: project.countInBars,
      loopEnabled: project.loopEnabled,
      selectedRegionId: project.selectedRegionId,
      engineMode: project.engineMode,
      tracks: project.tracks,
      clips: project.clips,
      regions: project.regions,
      markers: project.markers,
    }),
  });

export const addStudioTake = (projectId: string, trackId: string, recordingId: string) =>
  apiRequest<StudioProject>(`/studio/projects/${projectId}/takes`, {
    method: "POST",
    body: JSON.stringify({ trackId, recordingId }),
  });

export const addStudioClip = (
  projectId: string,
  trackId: string,
  recordingId: string,
  title: string,
  durationSeconds: number,
) =>
  apiRequest<StudioProject>(`/studio/projects/${projectId}/clips`, {
    method: "POST",
    body: JSON.stringify({ trackId, recordingId, title, startSeconds: 0, durationSeconds }),
  });

export const updateStudioTake = (
  projectId: string,
  take: Pick<StudioTake, "id" | "title" | "preferred">,
) =>
  apiRequest<StudioProject>(`/studio/projects/${projectId}/takes/${take.id}`, {
    method: "PATCH",
    body: JSON.stringify({ title: take.title, preferred: take.preferred }),
  });

export const studioAudioUrl = (path?: string) =>
  path ? `${new URL(API_URL).origin}${path}` : undefined;

export const useReaperStatus = () =>
  useQuery({
    queryKey: ["reaper-status"],
    queryFn: () => apiRequest<ReaperStatus>("/integrations/reaper"),
    refetchInterval: 3_000,
  });

export const configureReaper = (
  agentBaseUrl: string,
  containerMediaRoot: string,
  hostMediaRoot: string,
) =>
  apiRequest<ReaperStatus>("/integrations/reaper", {
    method: "PUT",
    body: JSON.stringify({ agentBaseUrl, containerMediaRoot, hostMediaRoot }),
  });

export const testReaper = () =>
  apiRequest<ReaperStatus>("/integrations/reaper/test", { method: "POST" });

export const disconnectReaper = () =>
  apiRequest<ReaperStatus>("/integrations/reaper", { method: "DELETE" });

export const openInReaper = (projectId: string) =>
  apiRequest<{ projectPath: string; status: string; message: string }>(
    `/studio/projects/${projectId}/open-in-reaper`,
    { method: "POST" },
  );

export const controlReaper = (action: "PLAY" | "PAUSE" | "STOP" | "RECORD") =>
  apiRequest<{ status: string; operation: string }>("/integrations/reaper/transport", {
    method: "POST",
    body: JSON.stringify({ action }),
  });

export const commandReaper = (
  projectId: string,
  operation: string,
  payload: Record<string, unknown>,
) =>
  apiRequest<{ status: string; commandId: string; operation: string }>(
    `/studio/projects/${projectId}/reaper/commands`,
    { method: "POST", body: JSON.stringify({ operation, payload }) },
  );
