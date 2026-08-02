import { Gauge, Pause, Play, X } from "lucide-react";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";
import { cn } from "@/shared/utils/cn";

export function DockHost() {
  const { dockOpen, toggleDock, metronome, setMetronome } = useWorkspace();
  if (!dockOpen) return null;

  return (
    <section
      aria-label="Dock de ferramentas"
      className="h-36 shrink-0 border-t border-border bg-background-rail"
    >
      <header className="flex h-8 items-center gap-2 border-b border-border bg-surface-card px-2">
        <Gauge className="size-3.5 text-signal" aria-hidden="true" />
        <span className="label-tech">Metrônomo</span>
        <button
          type="button"
          onClick={toggleDock}
          aria-label="Fechar dock"
          title="Fechar dock"
          className="ml-auto flex size-7 items-center justify-center hover:bg-surface-hover focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
        >
          <X className="size-3.5" aria-hidden="true" />
        </button>
      </header>
      <div className="flex h-[calc(100%-2rem)] items-center gap-4 px-4">
        <button
          type="button"
          onClick={() => setMetronome({ playing: !metronome.playing })}
          aria-label={metronome.playing ? "Pausar metrônomo" : "Iniciar metrônomo"}
          className={cn(
            "flex size-11 shrink-0 items-center justify-center border focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring",
            metronome.playing
              ? "border-signal bg-signal text-signal-foreground"
              : "border-border bg-surface-card hover:bg-surface-hover",
          )}
        >
          {metronome.playing ? <Pause className="size-4" /> : <Play className="size-4" />}
        </button>
        <label className="min-w-0 flex-1">
          <span className="label-tech">BPM</span>
          <div className="mt-1 flex items-center gap-3">
            <input
              type="range"
              min={30}
              max={260}
              value={metronome.bpm}
              onChange={(event) => setMetronome({ bpm: Number(event.target.value) })}
              className="min-w-0 flex-1 accent-[var(--color-signal)]"
            />
            <input
              aria-label="Batidas por minuto"
              type="number"
              min={30}
              max={260}
              value={metronome.bpm}
              onChange={(event) => setMetronome({ bpm: Number(event.target.value) || 40 })}
              className="num h-9 w-16 border border-border bg-surface-card px-2 text-sm outline-none focus:border-ring"
            />
          </div>
        </label>
        <div className="hidden border-l border-border pl-4 sm:block">
          <div className="num text-xl text-text-primary">{metronome.beats}/4</div>
          <div className="label-tech">Compasso</div>
        </div>
      </div>
    </section>
  );
}
