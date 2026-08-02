import { createFileRoute } from "@tanstack/react-router";
import { DiagnosticPage } from "@/features/diagnostics/DiagnosticWorkspace";

export const Route = createFileRoute("/diagnostico")({
  head: () => ({ meta: [{ title: "Diagnostico inicial - Muse Studio" }] }),
  component: DiagnosticPage,
});
