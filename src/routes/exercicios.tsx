import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { Panel, Meter } from "@/components/workspace/Panel";
import { Breadcrumb } from "@/components/workspace/TabBar";
import { exercises } from "@/data/practice";
import { cn } from "@/lib/utils";

export const Route = createFileRoute("/exercicios")({
  head: () => ({
    meta: [
      { title: "Banco de Exercícios — Music OS" },
      { name: "description", content: "Exercícios por técnica com BPM atual, meta e tempo sugerido." },
      { property: "og:title", content: "Banco de Exercícios — Music OS" },
      { property: "og:description", content: "Alternate, sweep, legato, bends e muito mais." },
    ],
  }),
  component: ExercisesPage,
});

function ExercisesPage() {
  const techniques = Array.from(new Set(exercises.map((e) => e.technique)));
  const [filter, setFilter] = useState<string | null>(null);
  const list = filter ? exercises.filter((e) => e.technique === filter) : exercises;

  return (
    <div className="flex h-full min-h-0 flex-col">
      <Breadcrumb trail={["Prática", "Exercícios"]} />
      <div className="grid min-h-0 flex-1 grid-cols-1 gap-px overflow-hidden bg-border lg:grid-cols-[200px_1fr]">
        <Panel title="Técnicas" bodyClassName="p-0">
          <button
            onClick={() => setFilter(null)}
            className={cn("block w-full border-b border-border/60 px-2 py-1 text-left text-xs", !filter && "bg-surface")}
          >
            Todas ({exercises.length})
          </button>
          {techniques.map((t) => (
            <button
              key={t}
              onClick={() => setFilter(t)}
              className={cn(
                "block w-full border-b border-border/60 px-2 py-1 text-left text-xs text-muted-foreground hover:text-foreground",
                filter === t && "bg-surface text-foreground",
              )}
            >
              {t}
            </button>
          ))}
        </Panel>
        <Panel title="Exercícios" bodyClassName="p-0">
          <table className="w-full text-xs">
            <thead>
              <tr className="border-b border-border bg-surface text-left">
                {["Exercício", "Técnica", "Min", "BPM atual", "Meta", "Progresso"].map((h) => (
                  <th key={h} className="label-tech px-2 py-1 font-normal">{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {list.map((e) => (
                <tr key={e.id} className="border-b border-border/60 align-top hover:bg-surface/50">
                  <td className="px-2 py-1">
                    <div>{e.name}</div>
                    <div className="text-2xs text-muted-foreground">{e.description}</div>
                  </td>
                  <td className="px-2 text-muted-foreground">{e.technique}</td>
                  <td className="num px-2">{e.minutes}</td>
                  <td className="num px-2 text-signal">{e.bpmCurrent}</td>
                  <td className="num px-2">{e.bpmTarget}</td>
                  <td className="w-32 px-2 pt-2">
                    <Meter value={(e.bpmCurrent / e.bpmTarget) * 100} tone={e.bpmCurrent >= e.bpmTarget ? "ok" : "info"} />
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
