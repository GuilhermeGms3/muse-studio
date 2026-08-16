import { createFileRoute, useParams } from "@tanstack/react-router";
import { StudioProjectWorkspace } from "@/features/studio/StudioWorkspace";

function StudioRoute() {
  const { studioProjectId } = useParams({ from: "/studio/$studioProjectId" });
  return <StudioProjectWorkspace id={studioProjectId} />;
}

export const Route = createFileRoute("/studio/$studioProjectId")({
  head: () => ({ meta: [{ title: "Studio - Muse Studio" }] }),
  component: StudioRoute,
});
