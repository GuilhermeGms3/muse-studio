import {
  Gauge,
  Moon,
  PanelBottom,
  PanelLeft,
  PanelRight,
  Pause,
  Play,
  Search,
  Sun,
} from "lucide-react";
import type { Instrument } from "@/shared/api/contracts";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";
import { cn } from "@/shared/utils/cn";

export function WorkspaceHeader({ instruments }: { instruments: Instrument[] }) {
  const {
    dockOpen,
    inspectorOpen,
    instrument,
    metronome,
    setInstrument,
    setMetronome,
    setPaletteOpen,
    sidebarOpen,
    theme,
    toggleDock,
    toggleInspector,
    toggleSidebar,
    toggleTheme,
  } = useWorkspace();

  return (
    <header className="flex min-h-10 shrink-0 items-center gap-1 border-b border-border bg-background-rail px-1.5 sm:gap-2 sm:px-2">
      <div className="flex shrink-0 items-center gap-1.5 pr-1 sm:pr-2">
        <Gauge className="size-4 text-signal" aria-hidden="true" />
        <span className="hidden text-xs font-semibold sm:inline">MUSE STUDIO</span>
      </div>

      <div
        className="hidden items-stretch border border-border md:flex"
        aria-label="Instrumento ativo"
      >
        {instruments.map((item) => (
          <button
            key={item.id}
            type="button"
            onClick={() => setInstrument(item.id)}
            aria-pressed={instrument === item.id}
            className={cn(
              "num min-h-7 px-2 text-2xs transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-inset focus-visible:ring-ring",
              instrument === item.id
                ? "bg-signal text-signal-foreground"
                : "text-text-muted hover:bg-surface-card",
            )}
            title={item.name}
          >
            {item.shortName}
          </button>
        ))}
      </div>

      <button
        type="button"
        onClick={() => setPaletteOpen(true)}
        className="flex h-8 min-w-0 max-w-md flex-1 items-center gap-2 border border-border bg-surface-card px-2 text-2xs text-text-muted hover:border-border-strong focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
      >
        <Search className="size-3.5 shrink-0" aria-hidden="true" />
        <span className="truncate">Buscar em tudo</span>
        <kbd className="num ml-auto hidden sm:inline">Ctrl K</kbd>
      </button>

      <div className="ml-auto flex shrink-0 items-center gap-0.5">
        <div className="hidden h-8 items-center gap-1 border border-border bg-surface-card px-2 lg:flex">
          <span className="label-tech">BPM</span>
          <input
            aria-label="Batidas por minuto"
            type="number"
            min={30}
            max={260}
            value={metronome.bpm}
            onChange={(event) => setMetronome({ bpm: Number(event.target.value) || 40 })}
            className="num w-10 bg-transparent text-xs outline-none"
          />
          <button
            type="button"
            onClick={() => setMetronome({ playing: !metronome.playing })}
            aria-label={metronome.playing ? "Pausar metrônomo" : "Iniciar metrônomo"}
            className="flex size-7 items-center justify-center focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
          >
            {metronome.playing ? (
              <Pause className="size-3.5 text-signal" />
            ) : (
              <Play className="size-3.5" />
            )}
          </button>
        </div>
        <HeaderToggle label="Alternar sidebar" active={sidebarOpen} onClick={toggleSidebar}>
          <PanelLeft className="size-4" />
        </HeaderToggle>
        <HeaderToggle label="Alternar dock" active={dockOpen} onClick={toggleDock}>
          <PanelBottom className="size-4" />
        </HeaderToggle>
        <HeaderToggle
          label={theme === "dark" ? "Visualizar tema claro" : "Usar tema escuro"}
          active={theme === "light"}
          onClick={toggleTheme}
        >
          {theme === "dark" ? <Sun className="size-4" /> : <Moon className="size-4" />}
        </HeaderToggle>
        <HeaderToggle label="Alternar inspetor" active={inspectorOpen} onClick={toggleInspector}>
          <PanelRight className="size-4" />
        </HeaderToggle>
      </div>
    </header>
  );
}

function HeaderToggle({
  label,
  active,
  onClick,
  children,
}: {
  label: string;
  active: boolean;
  onClick: () => void;
  children: React.ReactNode;
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      aria-label={label}
      aria-pressed={active}
      title={label}
      className={cn(
        "flex size-8 items-center justify-center focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring",
        active ? "text-text-primary" : "text-text-muted hover:text-text-primary",
      )}
    >
      {children}
    </button>
  );
}
