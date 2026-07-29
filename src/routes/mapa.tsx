import { createFileRoute, Link } from "@tanstack/react-router";
import { useMemo, useState } from "react";
import { Panel, StateDot, StateTag } from "@/components/workspace/Panel";
import { Breadcrumb } from "@/components/workspace/TabBar";
import { skills, skillById, domains } from "@/data/skills";
import { library } from "@/data/library";
import { songs, exercises, projects } from "@/data/practice";
import { useWorkspace } from "@/lib/workspace-store";

export const Route = createFileRoute("/mapa")({
  head: () => ({
    meta: [
      { title: "Mapa do Conhecimento — Music OS" },
      { name: "description", content: "Grafo interativo ligando teoria, técnicas, músicas, exercícios e projetos." },
      { property: "og:title", content: "Mapa do Conhecimento — Music OS" },
      { property: "og:description", content: "Explore o conhecimento musical como um mapa conectado." },
    ],
  }),
  component: MapPage,
});

export default function noop() {}

function MapPage() {
  const { skillOverrides } = useWorkspace();
  const [selected, setSelected] = useState("harmonic-field");
  const [zoom, setZoom] = useState(1);

  const layout = useMemo(() => {
    const cols = domains.length;
    return skills.map((s) => {
      const col = domains.indexOf(s.domain);
      const idx = skills.filter((k) => k.domain === s.domain).findIndex((k) => k.id === s.id);
      return { skill: s, x: 90 + (col * 1180) / cols, y: 70 + idx * 62 };
    });
  }, []);

  const pos = new Map(layout.map((n) => [n.skill.id, n]));
  const sel = skillById.get(selected)!;
  const state = (id: string) => skillOverrides[id] ?? skillById.get(id)!.state;

  const relatedLibrary = library.filter((l) => l.skills?.includes(selected));
  const relatedSongs = songs.filter((s) =>
    [...s.techniques, ...s.scales].some((t) => sel.name.toLowerCase().includes(t.toLowerCase().slice(0, 5))),
  );
  const relatedExercises = exercises.filter((e) => e.skill === selected);

  return (
    <div className="flex h-full min-h-0 flex-col">
      <Breadcrumb trail={["Conhecimento", "Mapa do Conhecimento"]} />
      <div className="grid min-h-0 flex-1 grid-cols-1 gap-px overflow-hidden bg-border lg:grid-cols-[1fr_320px]">
        <Panel
          title="Grafo"
          bodyClassName="p-0"
          actions={
            <div className="flex gap-px">
              <button onClick={() => setZoom((z) => Math.max(0.5, z - 0.1))} className="border border-border px-1.5 text-2xs">−</button>
              <button onClick={() => setZoom((z) => Math.min(2, z + 0.1))} className="border border-border px-1.5 text-2xs">+</button>
            </div>
          }
        >
          <div className="h-full w-full overflow-auto bg-rail">
            <svg width={1300 * zoom} height={900 * zoom} viewBox="0 0 1300 900">
              {layout.map((n) =>
                n.skill.requires.map((r) => {
                  const from = pos.get(r);
                  if (!from) return null;
                  const active = selected === n.skill.id || selected === r;
                  return (
                    <line
                      key={`${r}-${n.skill.id}`}
                      x1={from.x}
                      y1={from.y}
                      x2={n.x}
                      y2={n.y}
                      stroke={active ? "var(--color-signal)" : "var(--color-border-strong)"}
                      strokeWidth={active ? 1.4 : 0.7}
                    />
                  );
                }),
              )}
              {domains.map((d, i) => (
                <text
                  key={d}
                  x={90 + (i * 1180) / domains.length}
                  y={28}
                  fill="var(--color-muted-foreground)"
                  fontSize="10"
                  fontFamily="var(--font-mono)"
                  textAnchor="middle"
                >
                  {d.toUpperCase()}
                </text>
              ))}
              {layout.map((n) => {
                const st = state(n.skill.id);
                const active = selected === n.skill.id;
                return (
                  <g key={n.skill.id} onClick={() => setSelected(n.skill.id)} className="cursor-pointer">
                    <circle
                      cx={n.x}
                      cy={n.y}
                      r={active ? 7 : 5}
                      fill={
                        st === "locked"
                          ? "var(--color-locked)"
                          : st === "available"
                            ? "var(--color-muted-foreground)"
                            : ["learning", "practicing"].includes(st)
                              ? "var(--color-info)"
                              : ["consistent", "mastered"].includes(st)
                                ? "var(--color-ok)"
                                : "var(--color-signal)"
                      }
                      stroke={active ? "var(--color-signal)" : "transparent"}
                      strokeWidth={2}
                    />
                    <text
                      x={n.x + 10}
                      y={n.y + 3}
                      fill={active ? "var(--color-foreground)" : "var(--color-muted-foreground)"}
                      fontSize="9"
                    >
                      {n.skill.name}
                    </text>
                  </g>
                );
              })}
            </svg>
          </div>
        </Panel>

        <div className="flex min-h-0 flex-col gap-px overflow-auto bg-border">
          <Panel title="Nó selecionado">
            <div className="flex items-center justify-between">
              <h2 className="text-sm">{sel.name}</h2>
              <StateTag state={state(sel.id)} />
            </div>
            <p className="text-2xs text-muted-foreground">{sel.domain}</p>
            <span className="label-tech mt-3 block">Pré-requisitos</span>
            {sel.requires.map((r) => (
              <button key={r} onClick={() => setSelected(r)} className="flex items-center gap-1.5 text-xs hover:text-signal">
                <StateDot state={state(r)} /> {skillById.get(r)?.name}
              </button>
            ))}
            <span className="label-tech mt-3 block">Próximos passos</span>
            {sel.unlocks.map((u) => (
              <button key={u} onClick={() => setSelected(u)} className="flex items-center gap-1.5 text-xs hover:text-signal">
                <StateDot state={state(u)} /> {skillById.get(u)?.name}
              </button>
            ))}
          </Panel>
          <Panel title="Conteúdos">
            {relatedLibrary.map((l) => (
              <Link key={l.id} to="/biblioteca/$nodeId" params={{ nodeId: l.id }} className="block text-xs hover:text-signal">
                → {l.title}
              </Link>
            ))}
            {!relatedLibrary.length && <p className="text-2xs text-muted-foreground">Nenhum conteúdo ligado.</p>}
          </Panel>
          <Panel title="Músicas e exercícios">
            {relatedSongs.map((s) => (
              <Link key={s.id} to="/repertorio/$songId" params={{ songId: s.id }} className="block text-xs hover:text-signal">
                ♪ {s.title}
              </Link>
            ))}
            {relatedExercises.map((e) => (
              <div key={e.id} className="num text-2xs text-muted-foreground">
                · {e.name} — {e.bpmCurrent}/{e.bpmTarget} BPM
              </div>
            ))}
          </Panel>
          <Panel title="Projetos">
            {projects.map((p) => (
              <Link key={p.id} to="/projetos/$projectId" params={{ projectId: p.id }} className="block text-xs hover:text-signal">
                ▣ {p.name}
              </Link>
            ))}
          </Panel>
        </div>
      </div>
    </div>
  );
}
