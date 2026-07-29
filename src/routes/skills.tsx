import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { Panel, Row, Meter, StateDot, StateTag } from "@/components/workspace/Panel";
import { Breadcrumb } from "@/components/workspace/TabBar";
import { skills, skillById, domains, skillStateOrder, skillStateLabel } from "@/data/skills";
import { useWorkspace } from "@/lib/workspace-store";
import { cn } from "@/lib/utils";

export const Route = createFileRoute("/skills")({
  head: () => ({
    meta: [
      { title: "Skill Tree — Music OS" },
      { name: "description", content: "Mapa de domínio musical com dependências e estados reais de habilidade." },
      { property: "og:title", content: "Skill Tree — Music OS" },
      { property: "og:description", content: "De bloqueada a especialista: progressão por domínio real." },
    ],
  }),
  component: SkillsPage,
});

function SkillsPage() {
  const { skillOverrides, setSkillState, instrument } = useWorkspace();
  const [selected, setSelected] = useState<string>("alternate-picking");
  const [onlyInstrument, setOnlyInstrument] = useState(false);
  const sel = skillById.get(selected)!;
  const state = (id: string) => skillOverrides[id] ?? skillById.get(id)!.state;

  return (
    <div className="flex h-full min-h-0 flex-col">
      <Breadcrumb trail={["Conhecimento", "Skill Tree"]} />
      <div className="grid min-h-0 flex-1 grid-cols-1 gap-px overflow-hidden bg-border lg:grid-cols-[1fr_320px]">
        <Panel
          title="Árvore de domínio"
          actions={
            <button
              onClick={() => setOnlyInstrument((v) => !v)}
              className={cn("border border-border px-1.5 text-2xs", onlyInstrument && "bg-surface-2")}
            >
              filtrar instrumento
            </button>
          }
        >
          <div className="space-y-4">
            {domains.map((d) => {
              const list = skills.filter(
                (s) => s.domain === d && (!onlyInstrument || s.instruments.includes(instrument)),
              );
              if (!list.length) return null;
              return (
                <div key={d}>
                  <div className="mb-1 flex items-center gap-2">
                    <span className="label-tech">{d}</span>
                    <span className="h-px flex-1 bg-border" />
                  </div>
                  <div className="grid grid-cols-2 gap-1 md:grid-cols-3 xl:grid-cols-4">
                    {list.map((s) => {
                      const st = state(s.id);
                      return (
                        <button
                          key={s.id}
                          onClick={() => setSelected(s.id)}
                          className={cn(
                            "border border-border bg-surface/40 p-1.5 text-left hover:border-border-strong",
                            selected === s.id && "border-signal",
                            st === "locked" && "opacity-50",
                          )}
                        >
                          <div className="flex items-center gap-1.5">
                            <StateDot state={st} />
                            <span className="truncate text-xs">{s.name}</span>
                          </div>
                          <div className="mt-1">
                            <Meter value={s.accuracy} tone={s.accuracy > 80 ? "ok" : "info"} />
                          </div>
                          <div className="mt-0.5 flex justify-between">
                            <span className="num text-2xs text-muted-foreground">{s.hours}h</span>
                            <span className="text-2xs text-muted-foreground">{skillStateLabel[st]}</span>
                          </div>
                        </button>
                      );
                    })}
                  </div>
                </div>
              );
            })}
          </div>
        </Panel>

        <div className="flex min-h-0 flex-col gap-px overflow-auto bg-border">
          <Panel title="Habilidade">
            <div className="flex items-center justify-between">
              <h2 className="text-sm">{sel.name}</h2>
              <StateTag state={state(sel.id)} />
            </div>
            <Row label="Domínio" value={sel.domain} mono={false} />
            <Row label="Horas" value={`${sel.hours}h`} />
            <Row label="Precisão" value={`${sel.accuracy}%`} />
            {sel.bpm && <Row label="BPM" value={`${sel.bpm.current} / ${sel.bpm.target}`} />}
            {sel.notes && <p className="mt-2 text-2xs text-warn">{sel.notes}</p>}
            <div className="mt-3">
              <span className="label-tech">Estado</span>
              <div className="mt-1 grid grid-cols-2 gap-px">
                {skillStateOrder.map((st) => (
                  <button
                    key={st}
                    onClick={() => setSkillState(sel.id, st)}
                    className={cn(
                      "border border-border px-1 py-0.5 text-2xs",
                      state(sel.id) === st ? "bg-surface-2 text-foreground" : "text-muted-foreground",
                    )}
                  >
                    {skillStateLabel[st]}
                  </button>
                ))}
              </div>
            </div>
          </Panel>
          <Panel title="Dependências">
            <span className="label-tech">Requer</span>
            {sel.requires.length === 0 && <p className="text-2xs text-muted-foreground">Nenhuma</p>}
            {sel.requires.map((r) => (
              <button
                key={r}
                onClick={() => setSelected(r)}
                className="flex w-full items-center gap-2 py-0.5 text-xs hover:text-signal"
              >
                <StateDot state={state(r)} /> {skillById.get(r)?.name}
              </button>
            ))}
            <span className="label-tech mt-3 block">Desbloqueia</span>
            {sel.unlocks.length === 0 && <p className="text-2xs text-muted-foreground">Fim de ramo</p>}
            {sel.unlocks.map((u) => (
              <button
                key={u}
                onClick={() => setSelected(u)}
                className="flex w-full items-center gap-2 py-0.5 text-xs hover:text-signal"
              >
                <StateDot state={state(u)} /> {skillById.get(u)?.name}
              </button>
            ))}
          </Panel>
          <Panel title="Critérios de evolução">
            <ul className="space-y-0.5 text-2xs text-muted-foreground">
              <li>· Horas praticadas com foco</li>
              <li>· BPM atingido com execução limpa</li>
              <li>· Precisão consistente em três sessões</li>
              <li>· Constância semanal</li>
              <li>· Músicas executadas usando a habilidade</li>
              <li>· Exercícios concluídos e revisões</li>
              <li>· Autoavaliação gravada</li>
            </ul>
          </Panel>
        </div>
      </div>
    </div>
  );
}
