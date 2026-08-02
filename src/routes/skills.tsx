import { createFileRoute } from "@tanstack/react-router";
import { SkillsPage } from "@/features/learning/SkillTreeWorkspace";

export const Route = createFileRoute("/skills")({
  head: () => ({
    meta: [
      { title: "Skill Tree - Muse Studio" },
      {
        name: "description",
        content: "Mapa progressivo do conhecimento e das habilidades musicais.",
      },
    ],
  }),
  component: SkillsPage,
});
