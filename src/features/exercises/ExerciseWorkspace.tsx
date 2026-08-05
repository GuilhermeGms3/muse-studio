import { useEffect } from "react";
import { useParams } from "@tanstack/react-router";
import { QueryState } from "@/shared/ui/query/QueryState";
import { useExercise } from "@/shared/api/exercises";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";
import { ExerciseRunner } from "./ExerciseRunner";

export function ExerciseWorkspace() {
  const { exerciseId } = useParams({ from: "/exercicios/$exerciseId" });
  const query = useExercise(exerciseId);
  const { openTab } = useWorkspace();

  useEffect(() => {
    if (!query.data) return;
    openTab({
      path: `/exercicios/${exerciseId}`,
      title: query.data.name,
      type: "exercise",
      objectId: exerciseId,
      context: "practice",
    });
  }, [exerciseId, openTab, query.data]);

  if (!query.data) return <QueryState error={query.error} />;
  return <ExerciseRunner exercise={query.data} />;
}
