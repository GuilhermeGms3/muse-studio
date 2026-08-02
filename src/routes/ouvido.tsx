import { createFileRoute } from "@tanstack/react-router";
import { EarPage } from "@/features/ear-training/EarTrainingWorkspace";

export const Route = createFileRoute("/ouvido")({
  head: () => ({ meta: [{ title: "Treino de Ouvido - Muse Studio" }] }),
  component: EarPage,
});
