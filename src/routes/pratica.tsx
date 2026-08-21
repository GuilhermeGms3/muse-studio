import { createFileRoute } from "@tanstack/react-router";
import { PracticeWorkspace } from "@/features/practice/PracticeWorkspace";

export const Route = createFileRoute("/pratica")({
  head: () => ({ meta: [{ title: "Prática - Muse Studio" }] }),
  component: PracticeWorkspace,
});
