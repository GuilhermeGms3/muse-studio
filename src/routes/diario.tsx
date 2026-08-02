import { createFileRoute } from "@tanstack/react-router";
import { JournalPage } from "@/features/review/JournalWorkspace";

export const Route = createFileRoute("/diario")({
  head: () => ({ meta: [{ title: "Diário - Muse Studio" }] }),
  component: JournalPage,
});
