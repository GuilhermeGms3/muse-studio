import { createFileRoute } from "@tanstack/react-router";
import { HistoryWorkspace } from "@/features/history/HistoryWorkspace";

export const Route = createFileRoute("/historico")({
  head: () => ({ meta: [{ title: "Histórico - Muse Studio" }] }),
  component: HistoryWorkspace,
});
