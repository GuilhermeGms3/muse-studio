import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { Panel, Meter } from "@/components/workspace/Panel";
import { QueryState } from "@/components/workspace/QueryState";
import { Breadcrumb } from "@/components/workspace/TabBar";
import { useExercises } from "@/lib/music-api";
import { useWorkspace } from "@/lib/workspace-store";
import { cn } from "@/lib/utils";
import { ExerciseRunner } from "@/components/music/ExerciseRunner";
import { CatalogEditor } from "@/components/music/CatalogEditor";

export const Route = createFileRoute("/exercicios")({
  head: () => ({ meta: [{ title: "Exercícios - Music OS" }] }),
  component: ExercisesPage,
});

function ExercisesPage() {
  const { instrument } = useWorkspace();
  const exercisesQuery = useExercises(instrument);
  const [filter, setFilter] = useState<string | null>(null);
  const [selectedId, setSelectedId] = useState<string | null>(null);
  if (!exercisesQuery.data) return <QueryState error={exercisesQuery.error} />;
  const exercises = exercisesQuery.data;
  const techniques = Array.from(new Set(exercises.map((exercise) => exercise.technique)));
  const list = filter ? exercises.filter((exercise) => exercise.technique === filter) : exercises;
  const selected = exercises.find((exercise) => exercise.id === selectedId);

  return (
    <div className="flex h-full min-h-0 flex-col">
      <Breadcrumb trail={["Praticar", "Exercícios"]} />
      <div className={cn(
        "grid min-h-0 flex-1 grid-cols-1 gap-px overflow-hidden bg-border",
        selected ? "lg:grid-cols-[180px_1fr_330px]" : "lg:grid-cols-[200px_1fr]",
      )}>
        <Panel title="Técnicas" bodyClassName="p-0">
          <button
            onClick={() => setFilter(null)}
            className={cn("block w-full border-b border-border/60 px-2 py-1 text-left text-xs", !filter && "bg-surface")}
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
        <Panel title="Exercícios" bodyClassName="p-0" actions={<CatalogEditor kind="exercise" instrument={instrument} />}>
          <table className="w-full text-xs">
            <thead>
              <tr className="border-b border-border bg-surface text-left">
                {["Exercício", "Técnica", "Min", "BPM atual", "Meta", "Progresso"].map((heading) => (
                  <th key={heading} className="label-tech px-2 py-1 font-normal">{heading}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {list.map((exercise) => (
                <tr
                  key={exercise.id}
                  onClick={() => setSelectedId(exercise.id)}
                  className={cn(
                    "cursor-pointer border-b border-border/60 align-top hover:bg-surface/50",
                    selectedId === exercise.id && "bg-surface",
                  )}
                >
                  <td className="px-2 py-1">
                    <div>{exercise.name}</div>
                    <div className="text-2xs text-muted-foreground">{exercise.description}</div>
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
        {selected && <ExerciseRunner exercise={selected} onClose={() => setSelectedId(null)} />}
      </div>
    </div>
  );
}
