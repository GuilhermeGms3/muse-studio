import { createFileRoute } from "@tanstack/react-router";
import { LibraryLayout } from "@/features/library/LibraryWorkspace";

export const Route = createFileRoute("/biblioteca")({
  head: () => ({
    meta: [
      { title: "Biblioteca Musical - Muse Studio" },
      {
        name: "description",
        content: "Teoria, harmonia, escalas, técnicas e leitura em conteúdos interligados.",
      },
    ],
  }),
  component: LibraryLayout,
});
