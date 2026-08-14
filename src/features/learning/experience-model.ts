export type ExperienceStatus = "NOT_STARTED" | "IN_PROGRESS" | "PAUSED" | "COMPLETED";

export function experienceAction(status: ExperienceStatus) {
  if (status === "NOT_STARTED") return "start";
  if (status === "PAUSED" || status === "IN_PROGRESS") return "resume";
  return "review";
}

export function completionMessage(
  completed: boolean,
  competencies: Array<{ masteryState: string; evidenceConfidence: string }>,
  error?: string,
) {
  if (error) return { tone: "error" as const, title: "Não foi possível salvar", detail: error };
  if (!completed) {
    return {
      tone: "progress" as const,
      title: "Experiência em andamento",
      detail: "Seu ponto de retomada está salvo.",
    };
  }
  const sufficientlyGrounded = competencies.some(
    (item) =>
      item.masteryState === "MASTERED" ||
      item.masteryState === "PROBABLE_MASTERY" ||
      item.evidenceConfidence === "SUFFICIENT",
  );
  return sufficientlyGrounded
    ? {
        tone: "success" as const,
        title: "Aplicação concluída",
        detail: "A evidência observada já sustenta avanço nesta competência.",
      }
    : {
        tone: "insufficient" as const,
        title: "Prática concluída; domínio ainda em construção",
        detail:
          "Sua autoavaliação foi preservada como evidência provisória. Uma nova observação independente é necessária para confirmar domínio.",
      };
}
