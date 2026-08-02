import { createFileRoute } from "@tanstack/react-router";
import { ExercisesPage } from "@/features/exercises/ExercisesWorkspace";

export const Route = createFileRoute("/exercicios")({
  head: () => ({ meta: [{ title: "Exercícios - Muse Studio" }] }),
  component: ExercisesPage,
});
