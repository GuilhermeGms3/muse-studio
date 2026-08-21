import { createFileRoute } from "@tanstack/react-router";
import { ExerciseWorkspace } from "@/features/exercises/ExerciseWorkspace";

export const Route = createFileRoute("/exercicios/$exerciseId")({
  head: () => ({ meta: [{ title: "Exercício - Muse Studio" }] }),
  component: ExerciseWorkspace,
});
