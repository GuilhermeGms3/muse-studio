import { createFileRoute } from "@tanstack/react-router";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { Panel, Meter } from "@/components/workspace/Panel";
import { QueryState } from "@/components/workspace/QueryState";
import { Breadcrumb } from "@/components/workspace/TabBar";
import { updateActivity, useSkills, useTodayPlan } from "@/lib/music-api";
import { useWorkspace } from "@/lib/workspace-store";

export const Route = createFileRoute("/plano")({
  head: () => ({ meta: [{ title: "Plano de Estudos - Music OS" }] }),
  component: PlanPage,
});

function PlanPage() {
  const { instrument } = useWorkspace();
  const queryClient = useQueryClient();
  const planQuery = useTodayPlan(instrument);
  const skillsQuery = useSkills(instrument);
  const mutation = useMutation({
    mutationFn: ({ id, done }: { id: string; done: boolean }) => updateActivity(id, done),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["plan", instrument] });
      queryClient.invalidateQueries({ queryKey: ["home", instrument] });
    },
  });
  if (!planQuery.data) return <QueryState error={planQuery.error} />;
  const weak = (skillsQuery.data ?? [])
    .filter((skill) => ["learning", "practicing"].includes(skill.state))
    .sort((a, b) => a.progress - b.progress)
    .slice(0, 5);

  return (
    <div className="flex h-full min-h-0 flex-col">
      <Breadcrumb trail={["Estação", "Plano de Estudos"]} />
      <div className="grid min-h-0 flex-1 grid-cols-1 gap-px overflow-auto bg-border lg:grid-cols-[1fr_320px]">
        <Panel title="Hoje" bodyClassName="p-0">
          {planQuery.data.map((activity) => (
            <label key={activity.id} className="flex cursor-pointer items-center gap-3 border-b border-border/60 px-3 py-2 text-xs hover:bg-surface/50">
              <input
                type="checkbox"
                checked={activity.done}
                onChange={() => mutation.mutate({ id: activity.id, done: !activity.done })}
                className="size-3 accent-[var(--color-ok)]"
              />
              <span className="num w-12 text-lg text-signal">{activity.minutes}</span>
              <span className="flex-1">
                <span className={activity.done ? "text-muted-foreground line-through" : ""}>{activity.title}</span>
                <span className="label-tech ml-2">{activity.kind}</span>
              </span>
              <span className="text-2xs text-muted-foreground">{activity.target ?? "—"}</span>
            </label>
          ))}
        </Panel>
        <Panel title="Prioridades automáticas">
          <p className="mb-3 text-2xs text-muted-foreground">
            O plano considera domínio, tempo sem revisão, precisão e aplicação prática.
          </p>
          {weak.map((skill) => (
            <div key={skill.id} className="mb-2">
              <div className="flex justify-between text-xs">
                <span>{skill.technicalName}</span>
                <span className="num text-2xs text-muted-foreground">{skill.progress}%</span>
              </div>
              <Meter value={skill.progress} tone={skill.progress < 40 ? "signal" : "info"} />
            </div>
          ))}
        </Panel>
      </div>
    </div>
  );
}
