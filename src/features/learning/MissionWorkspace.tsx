import { useEffect } from "react";
import { useParams } from "@tanstack/react-router";
import { DeepLearningExperience } from "@/features/learning/DeepLearningExperience";
import { useMission } from "@/shared/api/missions";
import { QueryState } from "@/shared/ui/query/QueryState";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";

export function MissionWorkspace() {
  const { missionId } = useParams({ from: "/missoes/$missionId" });
  const { instrument, openTab } = useWorkspace();
  const missionQuery = useMission(missionId, instrument);
  const data = missionQuery.data;

  useEffect(() => {
    if (!data) return;
    openTab({
      path: `/missoes/${missionId}`,
      title: data.mission.title,
      type: "mission",
      objectId: missionId,
      context: "learning",
    });
  }, [data, missionId, openTab]);

  if (!data) return <QueryState error={missionQuery.error} />;

  return <DeepLearningExperience data={data} instrument={instrument} />;
}
