import { createFileRoute } from "@tanstack/react-router";
import { PlanPage } from "@/features/practice/PlanWorkspace";

export const Route = createFileRoute("/plano")({
  head: () => ({ meta: [{ title: "Plano de Estudos - Muse Studio" }] }),
  component: PlanPage,
});
