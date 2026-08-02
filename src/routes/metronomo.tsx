import { createFileRoute } from "@tanstack/react-router";
import { MetronomePage } from "@/features/metronome/MetronomeWorkspace";

export const Route = createFileRoute("/metronomo")({
  head: () => ({
    meta: [
      { title: "Metrônomo — Muse Studio" },
      {
        name: "description",
        content: "BPM, compassos, subdivisões, presets e histórico de tempos de estudo.",
      },
      { property: "og:title", content: "Metrônomo — Muse Studio" },
      { property: "og:description", content: "Metrônomo integrado à sessão de prática." },
    ],
  }),
  component: MetronomePage,
});
