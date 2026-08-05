import type { MacroContextId } from "@/workspace/navigation/types";
import { contextForPath } from "@/workspace/navigation/registry";

export type WorkspaceTabType =
  | "home"
  | "session"
  | "plan"
  | "journal"
  | "skill-tree"
  | "library"
  | "lesson"
  | "mission"
  | "repertoire"
  | "song"
  | "song-practice"
  | "exercises"
  | "exercise"
  | "ear-training"
  | "metronome"
  | "projects"
  | "project"
  | "diagnostic"
  | "data";

export interface WorkspaceTab {
  path: string;
  title: string;
  id?: string;
  context?: MacroContextId;
  type?: WorkspaceTabType;
  objectId?: string;
  dirty?: boolean;
  pinned?: boolean;
  temporary?: boolean;
  loading?: boolean;
  error?: boolean;
}

export function tabKey(tab: WorkspaceTab) {
  return tab.id ?? tab.path;
}

export function tabTypeForPath(path: string): WorkspaceTabType {
  if (path === "/") return "home";
  if (path === "/sessao") return "session";
  if (path === "/plano") return "plan";
  if (path === "/diario") return "journal";
  if (path === "/skills" || path === "/mapa") return "skill-tree";
  if (path.startsWith("/biblioteca/")) return "lesson";
  if (path.startsWith("/missoes/")) return "mission";
  if (path === "/biblioteca") return "library";
  if (path.startsWith("/repertorio/")) return "song";
  if (path === "/repertorio") return "repertoire";
  if (path === "/treino-musica") return "song-practice";
  if (path.startsWith("/treino-musica/")) return "song-practice";
  if (path.startsWith("/exercicios/")) return "exercise";
  if (path === "/exercicios") return "exercises";
  if (path === "/ouvido") return "ear-training";
  if (path === "/metronomo") return "metronome";
  if (path.startsWith("/projetos/")) return "project";
  if (path === "/projetos") return "projects";
  if (path === "/diagnostico") return "diagnostic";
  return "data";
}

export function withTabDefaults(tab: WorkspaceTab): WorkspaceTab {
  return {
    ...tab,
    id: tab.id ?? tab.path,
    context: tab.context ?? contextForPath(tab.path),
    type: tab.type ?? tabTypeForPath(tab.path),
  };
}

export function isWorkspaceTab(value: unknown): value is WorkspaceTab {
  if (!value || typeof value !== "object") return false;
  const candidate = value as Partial<WorkspaceTab>;
  return typeof candidate.path === "string" && typeof candidate.title === "string";
}
