import { createFileRoute } from "@tanstack/react-router";
import { ExploreWorkspace } from "@/features/explore/ExploreWorkspace";

export const Route = createFileRoute("/explorar")({
  head: () => ({ meta: [{ title: "Explorar - Muse Studio" }] }),
  component: ExploreWorkspace,
});
