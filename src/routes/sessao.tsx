import { createFileRoute } from "@tanstack/react-router";
import { Play, Pause, RotateCcw, Save } from "lucide-react";
import { useState } from "react";
import { Panel, Row, Meter } from "@/components/workspace/Panel";
import { Breadcrumb } from "@/components/workspace/TabBar";
import { useWorkspace, formatClock } from "@/lib/workspace-store";
import { exercises, todayPlan } from "@/data/practice";
import { instruments } from "@/data/skills";
import { cn } from "@/lib/utils";

export const Route = createFileRoute("/sessao")({
  head: () => ({
    meta: [
      { title: "Sessão de Prática — Music OS" },
      { name: "description", content: "Cronômetro, objetivos, BPM, exercícios e anotações da sessão atual." },
      { property: "og:title", content: "Sessão de Prática — Music OS" },
      { property: "og:description", content: "Execute a sessão do dia com cronômetro, BPM e registro." },
    ],
  }),
  component: SessionPage,
});

function SessionPage() {
  const { session, startSession, pauseSession, resetSession, updateSession, metronome, setMetronome } =
    useWorkspace();
  const [closing, setClosing] = useState(false);
  const [wrap, setWrap] = useState({ difficulties: "", achievements: "", next: "" });
  const [saved, setSaved] = useState(false);

  const list = exercises.filter((e) => e.instrument === session.instrument);
  const planned = todayPlan.filter((b) => b.instrument === session.instrument);

  return (
    <div className="flex h-full min-h-0 flex-col">
      <Breadcrumb trail={["Estação", "Sessão de Prática"]} />
      <div className="grid min-h-0 flex-1 grid-cols-1 gap-px overflow-auto bg-border lg:grid-cols-[320px_1fr_300px]">
        <Panel title="Transporte">
          <div className="num mb-3 text-5xl leading-none tracking-tighter">{formatClock(session.seconds)}</div>
          <div className="mb-3 flex gap-px">
            <button
              onClick={() => (session.running ? pauseSession() : startSession())}
              className={cn(
                "flex flex-1 items-center justify-center gap-1 border border-border py-1.5 text-xs",
                session.running ? "bg-signal text-signal-foreground" : "bg-surface hover:bg-surface-2",
              )}
            >
              {session.running ? <Pause className="size-3.5" /> : <Play className="size-3.5" />}
              {session.running ? "Pausar" : "Iniciar"}
            </button>
            <button
              onClick={resetSession}
              className="flex items-center justify-center border border-border bg-surface px-3 hover:bg-surface-2"
            >
              <RotateCcw className="size-3.5" />
            </button>
          </div>

          <Row label="Instrumento" value={""} />
          <div className="mb-2 flex gap-px">
            {instruments.map((i) => (
              <button
                key={i.id}
                onClick={() => updateSession({ instrument: i.id })}
                className={cn(
                  "flex-1 border border-border py-1 text-2xs",
                  session.instrument === i.id ? "bg-surface-2 text-foreground" : "text-muted-foreground",
                )}
              >
                {i.name}
              </button>
            ))}
          </div>

          <label className="label-tech">Objetivo da sessão</label>
          <input
            value={session.goal}
            onChange={(e) => updateSession({ goal: e.target.value })}
            className="mb-2 h-7 w-full border border-border bg-surface px-2 text-xs outline-none focus:border-ring"
          />

          <label className="label-tech">BPM de trabalho</label>
          <div className="flex items-center gap-2">
            <input
              type="range"
              min={40}
              max={220}
              value={metronome.bpm}
              onChange={(e) => setMetronome({ bpm: Number(e.target.value) })}
              className="w-full accent-[var(--color-signal)]"
            />
            <span className="num w-10 text-right text-sm">{metronome.bpm}</span>
          </div>
          <button
            onClick={() => setMetronome({ playing: !metronome.playing })}
            className="mt-2 w-full border border-border bg-surface py-1 text-2xs hover:border-signal hover:text-signal"
          >
            {metronome.playing ? "Parar metrônomo" : "Tocar metrônomo"}
          </button>
        </Panel>

        <Panel title="Roteiro da sessão" bodyClassName="p-0">
          <div className="border-b border-border px-2 py-1">
            <span className="label-tech">Blocos planejados</span>
          </div>
          {planned.map((b) => (
            <div key={b.id} className="flex items-center gap-2 border-b border-border/60 px-2 py-1 text-xs">
              <span className="num w-10 text-muted-foreground">{b.minutes}m</span>
              <span>{b.title}</span>
              <span className="num ml-auto text-2xs text-muted-foreground">{b.target ?? "—"}</span>
            </div>
          ))}
          <div className="border-b border-border bg-surface px-2 py-1">
            <span className="label-tech">Exercícios</span>
          </div>
          {list.map((e) => {
            const on = session.exercises.includes(e.id);
            return (
              <button
                key={e.id}
                onClick={() =>
                  updateSession({
                    exercises: on
                      ? session.exercises.filter((x) => x !== e.id)
                      : [...session.exercises, e.id],
                  })
                }
                className={cn(
                  "flex w-full items-center gap-2 border-b border-border/60 px-2 py-1 text-left text-xs",
                  on ? "bg-surface" : "hover:bg-surface/50",
                )}
              >
                <span className={cn("size-2", on ? "bg-signal" : "bg-surface-2")} />
                <span>{e.name}</span>
                <span className="label-tech ml-2">{e.technique}</span>
                <span className="num ml-auto text-2xs text-muted-foreground">
                  {e.bpmCurrent} → {e.bpmTarget}
                </span>
              </button>
            );
          })}
        </Panel>

        <div className="flex min-h-0 flex-col gap-px bg-border">
          <Panel title="Anotações ao vivo" className="flex-1">
            <textarea
              value={session.notes}
              onChange={(e) => updateSession({ notes: e.target.value })}
              placeholder="O que está funcionando, o que não está…"
              className="h-full min-h-32 w-full resize-none border border-border bg-surface p-2 text-xs outline-none focus:border-ring"
            />
          </Panel>
          <Panel title="Encerramento">
            {!closing ? (
              <button
                onClick={() => {
                  setClosing(true);
                  pauseSession();
                }}
                className="w-full border border-border bg-surface py-1 text-xs hover:border-signal hover:text-signal"
              >
                Finalizar sessão
              </button>
            ) : (
              <div className="space-y-2">
                <Row label="Tempo estudado" value={formatClock(session.seconds)} />
                <Meter value={Math.min(100, (session.seconds / 3600) * 100)} tone="ok" />
                {(["difficulties", "achievements", "next"] as const).map((k) => (
                  <div key={k}>
                    <label className="label-tech">
                      {k === "difficulties" ? "Dificuldades" : k === "achievements" ? "Conquistas" : "Próximos passos"}
                    </label>
                    <textarea
                      value={wrap[k]}
                      onChange={(e) => setWrap({ ...wrap, [k]: e.target.value })}
                      className="h-14 w-full resize-none border border-border bg-surface p-1.5 text-xs outline-none focus:border-ring"
                    />
                  </div>
                ))}
                <button
                  onClick={() => {
                    setSaved(true);
                    setClosing(false);
                    resetSession();
                  }}
                  className="flex w-full items-center justify-center gap-1 border border-border bg-surface py-1 text-xs hover:border-ok hover:text-ok"
                >
                  <Save className="size-3" /> Gravar no diário
                </button>
              </div>
            )}
            {saved && <p className="mt-2 text-2xs text-ok">Sessão registrada no diário.</p>}
          </Panel>
        </div>
      </div>
    </div>
  );
}
