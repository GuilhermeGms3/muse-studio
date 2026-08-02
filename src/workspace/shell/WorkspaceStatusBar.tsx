import { Play, Square } from "lucide-react";
import type { Instrument } from "@/shared/api/contracts";
import { formatClock, useWorkspace } from "@/workspace/store/WorkspaceProvider";

export function WorkspaceStatusBar({ instruments }: { instruments: Instrument[] }) {
  const { instrument, metronome, session } = useWorkspace();
  return (
    <footer className="flex h-7 shrink-0 items-center gap-3 overflow-hidden border-t border-border bg-background-rail px-2 text-2xs text-text-muted">
      <span className="flex shrink-0 items-center gap-1">
        {session.running ? <Play className="size-3 text-ok" /> : <Square className="size-3" />}
        Sessão {session.running ? "ativa" : "parada"}
      </span>
      <span className="num shrink-0">{formatClock(session.seconds)}</span>
      <span className="num hidden shrink-0 sm:inline">
        {metronome.bpm} BPM · {metronome.beats}/4
      </span>
      <span className="hidden truncate md:inline">
        {instruments.find((item) => item.id === instrument)?.name}
      </span>
      <span className="num ml-auto hidden shrink-0 opacity-70 xl:inline">
        Ctrl K busca · Ctrl B sidebar · Ctrl J dock · Ctrl \ inspetor · Ctrl M metrônomo
      </span>
    </footer>
  );
}
