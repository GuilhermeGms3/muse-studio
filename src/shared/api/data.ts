import { API_URL, apiRequest, useApiQuery } from "./client";
import type { DataStatus, ImportedFile } from "./contracts";

export const useDataStatus = () => useApiQuery<DataStatus>(["data-status"], "/data/status");

export async function downloadDataFile(path: "/data/backup" | "/data/journal.csv") {
  const response = await fetch(`${API_URL}${path}`);
  if (!response.ok) throw new Error("Não foi possível exportar os dados.");
  return response.blob();
}

export const restoreBackup = (snapshot: unknown) =>
  apiRequest<{
    lessons: number;
    exercises: number;
    songs: number;
    projects: number;
    instrumentProfiles: number;
    learningGoals: number;
    learningPaths: number;
    evidence: number;
    assessmentAttempts: number;
    message: string;
  }>("/data/restore", { method: "POST", body: JSON.stringify(snapshot) });

export async function importMusicFile(file: File) {
  const form = new FormData();
  form.append("file", file);
  const response = await fetch(`${API_URL}/data/imports`, { method: "POST", body: form });
  if (!response.ok) {
    const payload = (await response.json().catch(() => null)) as { message?: string } | null;
    throw new Error(payload?.message ?? "Não foi possível importar o arquivo.");
  }
  return response.json() as Promise<ImportedFile>;
}
