import { createFileRoute, Outlet, useRouterState } from "@tanstack/react-router";
import { ExercisesPage } from "@/features/exercises/ExercisesWorkspace";

export const Route = createFileRoute("/exercicios")({
  head: () => ({ meta: [{ title: "Exercícios - Muse Studio" }] }),
  component: ExercisesRoute,
});

function ExercisesRoute() {
  const pathname = useRouterState({ select: (state) => state.location.pathname });
  return pathname === "/exercicios" ? <ExercisesPage /> : <Outlet />;
}
