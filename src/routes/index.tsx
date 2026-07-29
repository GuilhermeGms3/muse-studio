import { createFileRoute, Link } from "@tanstack/react-router";
import { Play, ArrowRight } from "lucide-react";
import { Panel, Row, Meter, StateTag, StateDot } from "@/components/workspace/Panel";
import { Breadcrumb } from "@/components/workspace/TabBar";
import { useWorkspace, formatClock } from "@/lib/workspace-store";
import { todayPlan, journal, songs, projects } from "@/data/practice";
import { skills, instruments } from "@/data/skills";

export const Route = createFileRoute("/")({
  head: () => ({
    meta: [
      { title: "Music OS — Estação de Trabalho Musical" },
      {
        name: "description",
        content:
          "Painel inicial da estação: sessão de hoje, plano de estudos, última prática, música atual e próximos objetivos.",
      },
      { property: "og:title", content: "Music OS — Estação de Trabalho Musical" },
      {
        property: "og:description",
        content: "Sessão de hoje, plano de estudos, repertório e evolução em um só lugar.",
      },
    ],
  }),
  component: Home,
});

function Home() {
  const { instrument, session, blocksDone, toggleBlock, startSession, skillOverrides } = useWorkspace();
  const inst = instruments.find((i) => i.id === instrument)!;
  const last = journal[0];
  const current = songs.find((s) => s.instrument === instrument && s.status === "learning") ?? songs[0];
  const nextSkill = skills.find((s) => (skillOverrides[s.id] ?? s.state) === "available");
  const totalMin = todayPlan.reduce((a, b) => a + b.minutes, 0);
  const doneMin = todayPlan.filter((b) => blocksDone[b.id]).reduce((a, b) => a + b.minutes, 0);

  return (
    <div className="flex h-full min-h-0 flex-col">
      <Breadcrumb trail={["Estação", "Início"]} />
      <div className="grid min-h-0 flex-1 grid-cols-1 gap-px overflow-auto bg-border xl:grid-cols-3">
        <Panel
          title="Sessão de hoje"
          actions={
            <Link
              to="/sessao"
              onClick={() => startSession({ instrument })}
              className="flex items-center gap-1 border border-border bg-surface px-1.5 py-0.5 text-2xs hover:border-signal hover:text-signal"
            >
              <Play className="size-3" /> Iniciar
            </Link>
          }
        >
          <div className="num mb-2 text-3xl tracking-tight">{formatClock(session.seconds)}</div>
          <Row label="Instrumento" value={inst.name} mono={false} />
          <Row label="Objetivo" value={session.goal} mono={false} />
          <Row label="Planejado" value={`${doneMin} / ${totalMin} min`} />
          <div className="mt-2">
            <Meter value={(doneMin / totalMin) * 100} tone="ok" />
          </div>
        </Panel>

        <Panel title="Plano do dia" className="xl:col-span-2" bodyClassName="p-0">
          <table className="w-full text-xs">
            <tbody>
              {todayPlan.map((b) => (
                <tr key={b.id} className="border-b border-border/60 last:border-0 hover:bg-surface/50">
                  <td className="w-8 pl-2">
                    <input
                      type="checkbox"
                      checked={!!blocksDone[b.id]}
                      onChange={() => toggleBlock(b.id)}
                      className="size-3 accent-[var(--color-ok)]"
                    />
                  </td>
                  <td className="num w-12 text-muted-foreground">{b.minutes}m</td>
                  <td className={blocksDone[b.id] ? "text-muted-foreground line-through" : ""}>{b.title}</td>
                  <td className="label-tech w-24">{b.kind}</td>
                  <td className="num w-40 pr-2 text-right text-2xs text-muted-foreground">{b.target ?? "—"}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </Panel>

        <Panel title="Última prática">
          <Row label="Data" value={last.date} />
          <Row label="Duração" value={last.duration} />
          <p className="mt-2 text-xs">{last.worked.join(" · ")}</p>
          <p className="mt-2 text-2xs text-muted-foreground">
            <span className="text-warn">Dificuldades: </span>
            {last.difficulties}
          </p>
          <p className="mt-1 text-2xs text-muted-foreground">
            <span className="text-ok">Melhorias: </span>
            {last.improvements}
          </p>
        </Panel>

        <Panel title="Música atual">
          <div className="flex items-baseline justify-between">
            <Link to="/repertorio/$songId" params={{ songId: current.id }} className="text-sm hover:text-signal">
              {current.title}
            </Link>
            <span className="num text-2xs text-muted-foreground">{current.bpm} BPM</span>
          </div>
          <p className="text-2xs text-muted-foreground">
            {current.artist} · {current.tuning} · {current.key}
          </p>
          <div className="mt-2 space-y-1">
            {current.sections.map((s) => (
              <div key={s.id}>
                <div className="flex justify-between text-2xs">
                  <span className="text-muted-foreground">{s.name}</span>
                  <span className="num">{s.progress}%</span>
                </div>
                <Meter value={s.progress} tone={s.progress > 85 ? "ok" : "info"} />
              </div>
            ))}
          </div>
        </Panel>

        <Panel title="Próximo objetivo">
          {nextSkill && (
            <>
              <div className="flex items-center justify-between">
                <span className="text-sm">{nextSkill.name}</span>
                <StateTag state={skillOverrides[nextSkill.id] ?? nextSkill.state} />
              </div>
              <p className="mt-1 text-2xs text-muted-foreground">Domínio: {nextSkill.domain}</p>
              <p className="mt-2 text-2xs text-muted-foreground">
                Pré-requisitos concluídos: {nextSkill.requires.join(", ") || "—"}
              </p>
              <Link
                to="/skills"
                className="mt-3 inline-flex items-center gap-1 border border-border bg-surface px-2 py-1 text-2xs hover:border-signal hover:text-signal"
              >
                Abrir Skill Tree <ArrowRight className="size-3" />
              </Link>
            </>
          )}
        </Panel>

        <Panel title="Skill Tree — em foco" className="xl:col-span-2" bodyClassName="p-2">
          <div className="grid grid-cols-2 gap-x-4 gap-y-1 md:grid-cols-3">
            {skills
              .filter((s) => s.instruments.includes(instrument))
              .slice(0, 18)
              .map((s) => {
                const state = skillOverrides[s.id] ?? s.state;
                return (
                  <Link
                    key={s.id}
                    to="/skills"
                    className="flex items-center gap-2 border-b border-border/40 py-0.5 text-xs text-muted-foreground hover:text-foreground"
                  >
                    <StateDot state={state} />
                    <span className="truncate">{s.name}</span>
                    <span className="num ml-auto text-2xs opacity-70">{s.hours}h</span>
                  </Link>
                );
              })}
          </div>
        </Panel>

        <Panel title="Projetos recentes">
          {projects.map((p) => (
            <Link
              key={p.id}
              to="/projetos/$projectId"
              params={{ projectId: p.id }}
              className="flex items-center justify-between border-b border-border/50 py-1 text-xs last:border-0 hover:text-signal"
            >
              <span>{p.name}</span>
              <span className="num text-2xs text-muted-foreground">
                {p.key} · {p.bpm} BPM · {p.status}
              </span>
            </Link>
          ))}
        </Panel>
      </div>
    </div>
  );
}
