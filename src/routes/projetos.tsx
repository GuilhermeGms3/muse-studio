import { createFileRoute } from "@tanstack/react-router";
import { ProjectsLayout } from "@/features/compose/ProjectsWorkspace";

export const Route = createFileRoute("/projetos")({
  head: () => ({ meta: [{ title: "Projetos - Muse Studio" }] }),
  component: ProjectsLayout,
});
