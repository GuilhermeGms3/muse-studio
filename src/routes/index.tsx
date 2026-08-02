import { createFileRoute } from "@tanstack/react-router";
import { Home } from "@/features/home/HomeWorkspace";

export const Route = createFileRoute("/")({
  head: () => ({
    meta: [
      { title: "Muse Studio - Inicio" },
      {
        name: "description",
        content:
          "Inicio simples para decidir o que praticar agora, por quanto tempo e qual e o proximo passo.",
      },
      { property: "og:title", content: "Muse Studio - Inicio" },
      {
        property: "og:description",
        content: "Sessao de hoje, continuidade, objetivo atual e sequencia de estudo.",
      },
    ],
  }),
  component: Home,
});
