import { createFileRoute } from "@tanstack/react-router";
import { DataPage } from "@/features/tools/DataWorkspace";

export const Route = createFileRoute("/dados")({
  head: () => ({ meta: [{ title: "Dados e integrações - Muse Studio" }] }),
  component: DataPage,
});
