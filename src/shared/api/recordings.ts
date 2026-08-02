import { API_URL, apiRequest } from "./client";
import type { PracticeRecording } from "./contracts";

export const getRecordings = (contextType: string, contextId: string) =>
  apiRequest<PracticeRecording[]>(
    `/recordings?contextType=${encodeURIComponent(contextType)}&contextId=${encodeURIComponent(contextId)}`,
  );

export const recordingAudioUrl = (recording: PracticeRecording) =>
  `${new URL(API_URL).origin}${recording.audioUrl}`;

export async function uploadRecording(
  file: Blob,
  values: {
    contextType: string;
    contextId: string;
    durationMillis: number;
    targetBpm?: number;
    measuredBpm?: number;
    timingOffsetMillis?: number;
    rhythmStability?: number;
    targetNote?: string;
    pitchOffsetCents?: number;
    bendStability?: number;
  },
) {
  const form = new FormData();
  form.append("file", file, `practice-${Date.now()}.webm`);
  Object.entries(values).forEach(([key, value]) => {
    if (value !== undefined) form.append(key, String(value));
  });
  const response = await fetch(`${API_URL}/recordings`, { method: "POST", body: form });
  if (!response.ok) throw new Error("Não foi possível salvar a gravação.");
  return response.json() as Promise<PracticeRecording>;
}
