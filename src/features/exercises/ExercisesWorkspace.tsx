import { useState } from "react";
import { Link } from "@tanstack/react-router";
import { Panel, Meter } from "@/shared/ui/workspace/Panel";
import { QueryState } from "@/shared/ui/query/QueryState";
import { Breadcrumb } from "@/workspace/navigation/Breadcrumb";
import { useExercises } from "@/lib/music-api";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";
import { cn } from "@/shared/utils/cn";

export function ExercisesPage() {
  const { instrument } = useWorkspace();
  const exercisesQuery = useExercises(instrument);
  const [filter, setFilter] = useState<string | null>(null);
  if (!exercisesQuery.data) return <QueryState error={exercisesQuery.error} />;
  const exercises = exercisesQuery.data;
  const techniques = Array.from(new Set(exercises.map((exercise) => exercise.technique)));
  const list = filter ? exercises.filter((exercise) => exercise.technique === filter) : exercises;

  return (
    <div className="flex h-full min-h-0 flex-col">
      <Breadcrumb trail={["Praticar", "Exercícios"]} />
      <div className="grid min-h-0 flex-1 grid-cols-1 gap-px overflow-hidden bg-border lg:grid-cols-[200px_1fr]">
        <Panel title="Técnicas" bodyClassName="p-0">
          <button
            onClick={() => setFilter(null)}
            className={cn(
              "block w-full border-b border-border/60 px-2 py-1 text-left text-xs",
              !filter && "bg-surface",
            )}
          >
            Todas ({exercises.length})
          </button>
          {techniques.map((technique) => (
            <button
              key={technique}
              onClick={() => setFilter(technique)}
              className={cn(
                "block w-full border-b border-border/60 px-2 py-1 text-left text-xs text-muted-foreground hover:text-foreground",
                filter === technique && "bg-surface text-foreground",
              )}
            >
              {technique}
            </button>
          ))}
        </Panel>
        <Panel title="Exercícios" bodyClassName="p-0">
          <table className="w-full text-xs">
            <thead>
              <tr className="border-b border-border bg-surface text-left">
                {["Exercício", "Técnica", "Min", "BPM atual", "Meta", "Progresso"].map(
                  (heading) => (
                    <th key={heading} className="label-tech px-2 py-1 font-normal">
                      {heading}
                    </th>
                  ),
                )}
              </tr>
            </thead>
            <tbody>
              {list.map((exercise) => (
                <tr
                  key={exercise.id}
                  className="border-b border-border/60 align-top hover:bg-surface/50"
                >
                  <td className="px-2 py-1">
                    <Link
                      to="/exercicios/$exerciseId"
                      params={{ exerciseId: exercise.id }}
                      className="block outline-none hover:text-signal focus-visible:ring-1 focus-visible:ring-ring"
                    >
                      <span className="block">{exercise.name}</span>
                      <span className="block text-2xs text-muted-foreground">
                        {exercise.description}
                      </span>
                    </Link>
                  </td>
                  <td className="px-2 text-muted-foreground">{exercise.technique}</td>
                  <td className="num px-2">{exercise.minutes}</td>
                  <td className="num px-2 text-signal">{exercise.currentBpm}</td>
                  <td className="num px-2">{exercise.targetBpm}</td>
                  <td className="w-32 px-2 pt-2">
                    <Meter
                      value={(exercise.currentBpm / exercise.targetBpm) * 100}
                      tone={exercise.currentBpm >= exercise.targetBpm ? "ok" : "info"}
                    />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </Panel>
      </div>
    </div>
  );
}
