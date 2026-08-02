import { useQuery } from "@tanstack/react-query";

export const API_URL = (
  import.meta.env.VITE_MUSIC_OS_API_URL ?? "http://127.0.0.1:8081/api/v1"
).replace(/\/$/, "");

export async function apiRequest<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${API_URL}${path}`, {
    ...init,
    headers: {
      "Content-Type": "application/json",
      ...init?.headers,
    },
  });
  if (!response.ok) {
    const payload = (await response.json().catch(() => null)) as { message?: string } | null;
    throw new Error(payload?.message ?? `A API respondeu com ${response.status}.`);
  }
  if (response.status === 204) return undefined as T;
  return response.json() as Promise<T>;
}

export const useApiQuery = <T>(key: readonly unknown[], path: string) =>
  useQuery({ queryKey: key, queryFn: () => apiRequest<T>(path) });
