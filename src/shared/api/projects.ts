import { apiRequest, useApiQuery } from "./client";
import type { MusicProject } from "./contracts";

export const useProjects = () => useApiQuery<MusicProject[]>(["projects"], "/projects");

export const saveProject = (project: MusicProject) =>
  apiRequest<MusicProject>(`/projects/${project.id}`, {
    method: "PUT",
    body: JSON.stringify(project),
  });
