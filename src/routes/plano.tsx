import { createFileRoute } from "@tanstack/react-router";
import { Panel, Meter } from "@/components/workspace/Panel";
import { Breadcrumb } from "@/components/workspace/TabBar";
import { todayPlan, weekPlan } from "@/data/practice";
import { useWorkspace } from "@/lib/workspace-store";
import { skills } from "@/data/skills";

export const Route = createFileRoute("/plano")({
  head: () => ({
    meta: [
      { title: "Plano de Estudos — Music OS" },
      { name: "description", content: "Blocos diários de estudo reorganizados conforme desempenho." },
      { property: "og:title", content: "Plano de Estudos — Music OS" },
      { property: "og:description", content: "Rotina diária e semanal de prática musical." },
    ],
  }),
  component: PlanPage,
});

function PlanPage() {
  const { blocksDone, toggleBlock } = useWorkspace();
  const weak = skills
    .filter((s) => ["learning", "practicing"].includes(s.state))
    .sort((a, b) => a.accuracy - b.accuracy)
    .slice(0, 6);

  return (
    <div className="flex h-full min-h-0 flex-col">
      <Breadcrumb trail={["Estação", "Plano de Estudos"]} />
      <div className="grid min-h-0 flex-1 grid-cols-1 gap-px overflow-auto bg-border lg:grid-cols-[1fr_320px]">
        <Panel title="Hoje" bodyClassName="p-0">
          {todayPlan.map((b) => (
            <label
              key={b.id}
              className="flex cursor-pointer items-center gap-3 border-b border-border/60 px-3 py-2 text-xs hover:bg-surface/50"
            >
              <input
                type="checkbox"
                checked={!!blocksDone[b.id]}
                onChange={() => toggleBlock(b.id)}
                className="size-3 accent-[var(--color-ok)]"
              />
              <span className="num w-12 text-lg text-signal">{b.minutes}</span>
              <span className="flex-1">
                <span className={blocksDone[b.id] ? "text-muted-foreground line-through" : ""}>{b.title}</span>
                <span className="label-tech ml-2">{b.kind}</span>
              </span>
              <span className="num text-2xs text-muted-foreground">{b.target ?? "—"}</span>
            </label>
          ))}
        </Panel>
        <div className="flex min-h-0 flex-col gap-px bg-border">
          <Panel title="Semana">
            {weekPlan.map((d) => (
              <div key={d.day} className="mb-1">
                <div className="flex justify-between text-2xs">
                  <span>{d.day} · {d.focus}</span>
                  <span className="num text-muted-foreground">{d.minutes}m</span>
                </div>
                <Meter value={(d.minutes / 150) * 100} />
              </div>
            ))}
          </Panel>
          <Panel title="Reorganização automática" className="flex-1">
            <p className="mb-2 text-2xs text-muted-foreground">
              Prioridades calculadas por precisão baixa, revisão atrasada e meta de BPM não atingida.
            </p>
            {weak.map((s) => (
              <div key={s.id} className="mb-1">
                <div className="flex justify-between text-xs">
                  <span>{s.name}</span>
                  <span className="num text-2xs text-muted-foreground">{s.accuracy}%</span>
                </div>
                <Meter value={s.accuracy} tone={s.accuracy < 60 ? "signal" : "info"} />
              </div>
            ))}
          </Panel>
        </div>
      </div>
    </div>
  );
}
