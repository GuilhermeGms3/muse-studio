import { createFileRoute } from "@tanstack/react-router";
import { JourneyWorkspace } from "@/features/journey/JourneyWorkspace";

export const Route = createFileRoute("/jornada")({
  head: () => ({ meta: [{ title: "Jornada - Muse Studio" }] }),
  component: JourneyWorkspace,
});
