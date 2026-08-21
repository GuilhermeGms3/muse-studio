import type { HomeData } from "@/shared/api/contracts";

export const primaryNavigationPaths = [
  { label: "Hoje", path: "/" },
  { label: "Prática", path: "/pratica" },
  { label: "Jornada", path: "/jornada" },
  { label: "Músicas", path: "/musicas" },
  { label: "Histórico", path: "/historico" },
] as const;

const legacyWorkspaceTabDestinations: Record<string, { path: string; title: string }> = {
  "/sessao": { path: "/pratica", title: "Prática" },
  "/plano": { path: "/pratica", title: "Prática" },
  "/repertorio": { path: "/musicas", title: "Músicas" },
  "/treino-musica": { path: "/musicas", title: "Músicas" },
  "/skills": { path: "/jornada", title: "Jornada" },
  "/mapa": { path: "/jornada", title: "Jornada" },
};

export function consolidateWorkspaceTabs<
  T extends { path: string; title: string; id?: string; context?: unknown; type?: unknown },
>(tabs: T[]): T[] {
  const result: T[] = [];
  const paths = new Set<string>();

  for (const tab of tabs) {
    const destination = legacyWorkspaceTabDestinations[tab.path.split("?")[0]];
    const consolidated = destination
      ? ({
          ...tab,
          ...destination,
          id: destination.path,
          context: undefined,
          type: undefined,
        } as T)
      : tab;
    if (paths.has(consolidated.path)) continue;
    paths.add(consolidated.path);
    result.push(consolidated);
  }

  return result;
}

export type HomeFocus = "ACTIVE_EXPERIENCE" | "COACH_RECOMMENDATION" | "EVIDENCE_ACTION";

export function selectHomeFocus(home: HomeData): HomeFocus {
  if (home.learningExperience && home.learningExperience.status !== "COMPLETED") {
    return "ACTIVE_EXPERIENCE";
  }
  if (
    home.coach?.recommendations.some(
      (item) => item.kind !== "REVIEW" && item.kind !== "REVALIDATION",
    )
  ) {
    return "COACH_RECOMMENDATION";
  }
  return "EVIDENCE_ACTION";
}

export const journeyStatusCopy = {
  ESTABLISHED: "Consolidado",
  IN_PROGRESS: "Em desenvolvimento",
  AVAILABLE: "Disponível",
  REVIEW_DUE: "Revisão necessária",
  BLOCKED: "Bloqueado",
} as const;

export function isApplicationActivity(activityType: string) {
  return ["context", "song", "play_along", "record", "transfer", "create", "review"].includes(
    activityType,
  );
}

export function learningActivityLabel(activityType: string) {
  return (
    {
      understand: "Compreender",
      listen: "Escutar e distinguir",
      imitate: "Observar e imitar",
      slow: "Praticar com controle",
      context: "Aplicar em contexto",
      song: "Aplicar em repertório",
      recall: "Recordar sem apoio",
      record: "Gravar e comparar",
      transfer: "Transferir e criar",
      create: "Criar com restrições",
      play_along: "Aplicar com acompanhamento",
      review: "Recuperar em novo contexto",
      compare: "Comparar tentativas",
      execute: "Praticar",
      guided: "Prática guiada",
    }[activityType] ?? "Atividade musical"
  );
}

export function missionRequiresRecording(activityTypes: string[], expectedEvidence: string) {
  return (
    activityTypes.includes("record") || /grava|registro de áudio|áudio/i.test(expectedEvidence)
  );
}

export function visibleMissionPhases(input: {
  lessonCount: number;
  exerciseCount: number;
  hasMusicalApplication: boolean;
}) {
  return [
    "ORIENTATION",
    input.lessonCount > 0 ? "UNDERSTANDING" : "DIRECT_EXPERIMENTATION",
    input.exerciseCount > 0 ? "PRACTICE" : "NO_PRACTICE_CONTENT",
    input.hasMusicalApplication ? "APPLICATION" : "APPLICATION_REFLECTION",
    "CLOSING",
  ] as const;
}
