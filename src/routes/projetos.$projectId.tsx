import { createFileRoute } from "@tanstack/react-router";
import { ProjectPage } from "@/features/compose/ProjectWorkspace";

export const Route = createFileRoute("/projetos/$projectId")({
  head: () => ({ meta: [{ title: "Projetos - Muse Studio" }] }),
  component: ProjectPage,
});
