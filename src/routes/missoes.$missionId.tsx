import { createFileRoute } from "@tanstack/react-router";
import { MissionWorkspace } from "@/features/learning/MissionWorkspace";

export const Route = createFileRoute("/missoes/$missionId")({
  head: () => ({ meta: [{ title: "MissÃ£o - Muse Studio" }] }),
  component: MissionWorkspace,
});
