import { createFileRoute } from "@tanstack/react-router";
import { RepertoireLayout } from "@/features/repertoire/RepertoireWorkspace";

export const Route = createFileRoute("/repertorio")({
  head: () => ({ meta: [{ title: "Repertório - Muse Studio" }] }),
  component: RepertoireLayout,
});
