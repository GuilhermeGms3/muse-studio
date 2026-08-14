import type { HomeData } from "@/shared/api/contracts";

export const primaryNavigationPaths = [
  { label: "Hoje", path: "/" },
  { label: "Jornada", path: "/jornada" },
  { label: "Músicas", path: "/musicas" },
  { label: "Histórico", path: "/historico" },
  { label: "Explorar", path: "/explorar" },
] as const;

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
  return ["context", "song", "record", "transfer"].includes(activityType);
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
