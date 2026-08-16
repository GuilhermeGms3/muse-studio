import { createFileRoute } from "@tanstack/react-router";
import { StudioHome } from "@/features/studio/StudioWorkspace";

export const Route = createFileRoute("/studio/")({
  component: StudioHome,
});
