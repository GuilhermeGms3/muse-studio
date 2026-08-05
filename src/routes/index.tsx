import { createFileRoute } from "@tanstack/react-router";
import { Home } from "@/features/home/HomeWorkspace";

export const Route = createFileRoute("/")({
  head: () => ({
    meta: [
      { title: "Muse Studio - Centro de aprendizagem" },
      {
        name: "description",
        content: "Centro de aprendizagem orientado pelo Coach para decidir o que fazer hoje.",
      },
      { property: "og:title", content: "Muse Studio - Centro de aprendizagem" },
      {
        property: "og:description",
        content: "Orientacao diaria explicavel, contexto musical e caminhos de pratica.",
      },
    ],
  }),
  component: Home,
});
