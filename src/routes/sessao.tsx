import { createFileRoute } from "@tanstack/react-router";
import { SessionPage } from "@/features/practice/SessionWorkspace";

export const Route = createFileRoute("/sessao")({
  head: () => ({ meta: [{ title: "Sessão - Muse Studio" }] }),
  component: SessionPage,
});
