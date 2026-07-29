import { Link } from "@tanstack/react-router";
import { Plus, X } from "lucide-react";
import { useState } from "react";
import { Panel, Row, Meter, StateTag } from "./Panel";
import { useWorkspace, formatClock } from "@/lib/workspace-store";
import { instruments, skills } from "@/data/skills";
import { songs, todayPlan } from "@/data/practice";

export function Inspector() {
  const { instrument, session, notes, addNote, removeNote, blocksDone, skillOverrides } = useWorkspace();
  const [draft, setDraft] = useState("");
  const inst = instruments.find((i) => i.id === instrument)!;

  const active = skills.filter(
    (s) => s.instruments.includes(instrument) && ["practicing", "learning"].includes(skillOverrides[s.id] ?? s.state),
  );
  const currentSong = songs.find((s) => s.instrument === instrument && s.status === "learning");
  const remaining = todayPlan.filter((b) => !blocksDone[b.id]).reduce((a, b) => a + b.minutes, 0);

  return (
    <div className="flex h-full min-h-0 flex-col gap-px bg-border">
      <Panel title="Contexto">
        <Row label="Instrumento" value={inst.name} mono={false} />
        <Row label="Foco" value={inst.focus.join(" · ")} mono={false} />
        <Row label="Sessão" value={formatClock(session.seconds)} />
        <Row label="Restante hoje" value={`${remaining} min`} />
      </Panel>

      <Panel title="Em desenvolvimento" bodyClassName="p-2 space-y-2">
        {active.slice(0, 6).map((s) => (
          <div key={s.id} className="space-y-1">
            <div className="flex items-center justify-between gap-2">
              <span className="truncate text-xs">{s.name}</span>
              <StateTag state={skillOverrides[s.id] ?? s.state} />
            </div>
            <Meter value={s.accuracy} tone={s.accuracy > 80 ? "ok" : "info"} />
            <div className="flex justify-between">
              <span className="label-tech">{s.hours}h</span>
              {s.bpm && (
                <span className="num text-2xs text-muted-foreground">
                  {s.bpm.current}/{s.bpm.target} BPM
                </span>
              )}
            </div>
          </div>
        ))}
        {active.length === 0 && <p className="text-2xs text-muted-foreground">Nada em desenvolvimento.</p>}
      </Panel>

      {currentSong && (
        <Panel title="Música atual">
          <Link to="/repertorio/$songId" params={{ songId: currentSong.id }} className="text-xs hover:text-signal">
            {currentSong.title}
          </Link>
          <p className="text-2xs text-muted-foreground">{currentSong.artist}</p>
          <div className="mt-2 space-y-1">
            {currentSong.sections.map((sec) => (
              <div key={sec.id}>
                <div className="flex justify-between">
                  <span className="text-2xs text-muted-foreground">{sec.name}</span>
                  <span className="num text-2xs">{sec.progress}%</span>
                </div>
                <Meter value={sec.progress} tone={sec.progress > 85 ? "ok" : "info"} />
              </div>
            ))}
          </div>
        </Panel>
      )}

      <Panel title="Notas rápidas" className="flex-1">
        <form
          onSubmit={(e) => {
            e.preventDefault();
            if (draft.trim()) {
              addNote(draft.trim());
              setDraft("");
            }
          }}
          className="mb-2 flex items-center gap-1"
        >
          <input
            value={draft}
            onChange={(e) => setDraft(e.target.value)}
            placeholder="Nova nota…"
            className="h-6 w-full border border-border bg-surface px-1.5 text-xs outline-none focus:border-ring"
          />
          <button type="submit" className="flex size-6 items-center justify-center border border-border bg-surface hover:bg-surface-2">
            <Plus className="size-3" />
          </button>
        </form>
        <ul className="space-y-1">
          {notes.map((n) => (
            <li key={n} className="group flex items-start justify-between gap-2 border-l border-signal/60 pl-2 text-xs">
              <span className="text-muted-foreground">{n}</span>
              <button onClick={() => removeNote(n)} className="opacity-0 group-hover:opacity-100">
                <X className="size-3" />
              </button>
            </li>
          ))}
        </ul>
      </Panel>
    </div>
  );
}
