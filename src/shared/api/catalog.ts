import { apiRequest } from "./client";

export const deleteResource = (kind: "library" | "exercises" | "songs" | "projects", id: string) =>
  apiRequest<void>(`/${kind}/${id}`, { method: "DELETE" });
