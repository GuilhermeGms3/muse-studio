import { Link } from "@tanstack/react-router";
import { Plus, X } from "lucide-react";
import { useState } from "react";
import { Panel, Row, Meter, StateTag } from "./Panel";
import { useWorkspace, formatClock } from "@/lib/workspace-store";
import { useInstruments, useSkills, useSongs, useTodayPlan } from "@/lib/music-api";

export function Inspector() {
  const { instrument, session, notes, addNote, removeNote } = useWorkspace();
  const [draft, setDraft] = useState("");
  const instruments = useInstruments().data ?? [];
  const skills = useSkills(instrument).data ?? [];
  const songs = useSongs().data ?? [];
  const todayPlan = useTodayPlan(instrument).data ?? [];
  const currentInstrument = instruments.find((item) => item.id === instrument);
  const active = skills.filter((skill) => ["practicing", "learning"].includes(skill.state));
  const currentSong = songs.find((song) => song.instrument === instrument && song.status === "learning");
  const remaining = todayPlan.filter((activity) => !activity.done)
    .reduce((sum, activity) => sum + activity.minutes, 0);

  return (
    <div className="flex h-full min-h-0 flex-col gap-px bg-border">
      <Panel title="Contexto">
        <Row label="Instrumento" value={currentInstrument?.name ?? "—"} mono={false} />
        <Row label="Foco" value={currentInstrument?.focus.join(" · ") ?? "—"} mono={false} />
        <Row label="Sessão" value={formatClock(session.seconds)} />
        <Row label="Restante hoje" value={`${remaining} min`} />
      </Panel>

      <Panel title="Em desenvolvimento" bodyClassName="p-2 space-y-2">
        {active.slice(0, 5).map((skill) => (
          <div key={skill.id} className="space-y-1">
            <div className="flex items-center justify-between gap-2">
              <span className="truncate text-xs">{skill.technicalName}</span>
              <StateTag state={skill.state} />
            </div>
            <Meter value={skill.progress} tone={skill.progress > 80 ? "ok" : "info"} />
            <div className="flex justify-between">
              <span className="label-tech">{skill.hours.toFixed(1)}h</span>
              {skill.targetBpm && (
                <span className="num text-2xs text-muted-foreground">
                  {skill.currentBpm ?? 0}/{skill.targetBpm} BPM
                </span>
              )}
            </div>
          </div>
        ))}
        {!active.length && <p className="text-2xs text-muted-foreground">Nada em desenvolvimento.</p>}
      </Panel>

      {currentSong && (
        <Panel title="Música atual">
          <Link to="/repertorio/$songId" params={{ songId: currentSong.id }} className="text-xs hover:text-signal">
            {currentSong.title}
          </Link>
          <p className="text-2xs text-muted-foreground">{currentSong.artist}</p>
          <div className="mt-2 space-y-1">
            {currentSong.sections.slice(0, 4).map((section) => (
              <div key={section.id}>
                <div className="flex justify-between">
                  <span className="text-2xs text-muted-foreground">{section.name}</span>
                  <span className="num text-2xs">{section.progress}%</span>
                </div>
                <Meter value={section.progress} tone={section.progress > 85 ? "ok" : "info"} />
              </div>
            ))}
          </div>
        </Panel>
      )}

      <Panel title="Notas rápidas" className="flex-1">
        <form
          onSubmit={(event) => {
            event.preventDefault();
            if (draft.trim()) {
              addNote(draft.trim());
              setDraft("");
            }
          }}
          className="mb-2 flex items-center gap-1"
        >
          <input
            value={draft}
            onChange={(event) => setDraft(event.target.value)}
            placeholder="Nova nota..."
            className="h-6 w-full border border-border bg-surface px-1.5 text-xs outline-none focus:border-ring"
          />
          <button type="submit" className="flex size-6 items-center justify-center border border-border bg-surface hover:bg-surface-2">
            <Plus className="size-3" />
          </button>
        </form>
        <ul className="space-y-1">
          {notes.map((note) => (
            <li key={note} className="group flex items-start justify-between gap-2 border-l border-signal/60 pl-2 text-xs">
              <span className="text-muted-foreground">{note}</span>
              <button onClick={() => removeNote(note)} className="opacity-0 group-hover:opacity-100">
                <X className="size-3" />
              </button>
            </li>
          ))}
        </ul>
      </Panel>
    </div>
  );
}
