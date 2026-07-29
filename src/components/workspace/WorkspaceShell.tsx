import { useEffect, type ReactNode } from "react";
import { useNavigate, useRouterState } from "@tanstack/react-router";
import { PanelLeft, PanelRight, Search, Play, Pause, Square, CircleDot } from "lucide-react";
import { WorkspaceSidebar } from "./WorkspaceSidebar";
import { TabBar } from "./TabBar";
import { Inspector } from "./Inspector";
import { CommandPalette } from "./CommandPalette";
import { useWorkspace, formatClock } from "@/lib/workspace-store";
import { useMetronomeEngine } from "@/lib/use-metronome";
import { useInstruments } from "@/lib/music-api";
import { titleForPath } from "@/lib/nav";
import { cn } from "@/lib/utils";
import { ResizableHandle, ResizablePanel, ResizablePanelGroup } from "@/components/ui/resizable";

export function WorkspaceShell({ children }: { children: ReactNode }) {
  const pathname = useRouterState({ select: (s) => s.location.pathname });
  const navigate = useNavigate();
  const ws = useWorkspace();
  const { data: instruments = [] } = useInstruments();
  const {
    instrument,
    setInstrument,
    sidebarOpen,
    toggleSidebar,
    inspectorOpen,
    toggleInspector,
    setPaletteOpen,
    openTab,
    session,
    metronome,
    setMetronome,
  } = ws;

  useMetronomeEngine(metronome);

  useEffect(() => {
    openTab({ path: pathname, title: titleForPath(pathname) });
  }, [pathname, openTab]);

  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      const meta = e.metaKey || e.ctrlKey;
      if (meta && e.key.toLowerCase() === "k") {
        e.preventDefault();
        setPaletteOpen(true);
      } else if (meta && e.key.toLowerCase() === "b") {
        e.preventDefault();
        toggleSidebar();
      } else if (meta && e.key === "\\") {
        e.preventDefault();
        toggleInspector();
      } else if (meta && e.key.toLowerCase() === "m") {
        e.preventDefault();
        setMetronome({ playing: !metronome.playing });
      } else if (meta && e.shiftKey && e.key.toLowerCase() === "p") {
        e.preventDefault();
        navigate({ to: "/sessao" });
      }
    };
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [setPaletteOpen, toggleSidebar, toggleInspector, setMetronome, metronome.playing, navigate]);

  const focusMode = pathname === "/sessao";

  if (focusMode) {
    return (
      <div className="flex h-screen min-h-0 w-full flex-col bg-background text-foreground">
        <header className="flex h-9 shrink-0 items-center gap-2 border-b border-border bg-rail px-2">
          <div className="flex items-center gap-1.5 pr-2">
            <CircleDot className="size-3.5 text-signal" />
            <span className="text-xs font-semibold tracking-tight">MUSIC OS</span>
          </div>
          <span className="label-tech">Sessao em foco</span>
        </header>
        <div className="min-h-0 flex-1">{children}</div>
      </div>
    );
  }

  return (
    <div className="flex h-screen min-h-0 w-full flex-col bg-background text-foreground">
      {/* Title bar */}
      <header className="flex h-9 shrink-0 items-center gap-2 border-b border-border bg-rail px-2">
        <div className="flex items-center gap-1.5 pr-2">
          <CircleDot className="size-3.5 text-signal" />
          <span className="text-xs font-semibold tracking-tight">MUSIC OS</span>
        </div>

        <div className="flex items-stretch border border-border">
          {instruments.map((i) => (
            <button
              key={i.id}
              onClick={() => setInstrument(i.id)}
              className={cn(
                "num px-2 py-0.5 text-2xs tracking-wider transition-colors",
                instrument === i.id
                  ? "bg-signal text-signal-foreground"
                  : "text-muted-foreground hover:bg-surface",
              )}
              title={i.name}
            >
              {i.shortName}
            </button>
          ))}
        </div>

        <button
          onClick={() => setPaletteOpen(true)}
          className="flex h-6 max-w-md flex-1 items-center gap-2 border border-border bg-surface px-2 text-2xs text-muted-foreground hover:border-border-strong"
        >
          <Search className="size-3" />
          Buscar em tudo
          <span className="num ml-auto">⌘K</span>
        </button>

        <div className="ml-auto flex items-center gap-1">
          <div className="flex items-center gap-1 border border-border bg-surface px-2 py-0.5">
            <span className="label-tech">BPM</span>
            <input
              type="number"
              value={metronome.bpm}
              onChange={(e) => setMetronome({ bpm: Number(e.target.value) || 40 })}
              className="num w-10 bg-transparent text-xs outline-none"
            />
            <button onClick={() => setMetronome({ playing: !metronome.playing })}>
              {metronome.playing ? (
                <Pause className="size-3 text-signal" />
              ) : (
                <Play className="size-3" />
              )}
            </button>
          </div>
          <button
            onClick={toggleSidebar}
            className={cn("p-1", sidebarOpen ? "text-foreground" : "text-muted-foreground")}
          >
            <PanelLeft className="size-3.5" />
          </button>
          <button
            onClick={toggleInspector}
            className={cn("p-1", inspectorOpen ? "text-foreground" : "text-muted-foreground")}
          >
            <PanelRight className="size-3.5" />
          </button>
        </div>
      </header>

      {/* Body */}
      <div className="min-h-0 flex-1">
        <ResizablePanelGroup orientation="horizontal">
          {sidebarOpen && (
            <>
              <ResizablePanel
                defaultSize="16%"
                minSize="11%"
                maxSize="26%"
                className="min-w-[150px]"
              >
                <WorkspaceSidebar />
              </ResizablePanel>
              <ResizableHandle className="w-px bg-border hover:bg-border-strong" />
            </>
          )}

          <ResizablePanel defaultSize={inspectorOpen ? "63%" : "84%"}>
            <div className="flex h-full min-h-0 flex-col bg-background">
              <TabBar />
              <div className="min-h-0 flex-1 overflow-auto">{children}</div>
            </div>
          </ResizablePanel>

          {inspectorOpen && (
            <>
              <ResizableHandle className="w-px bg-border hover:bg-border-strong" />
              <ResizablePanel
                defaultSize="21%"
                minSize="14%"
                maxSize="34%"
                className="min-w-[210px]"
              >
                <Inspector />
              </ResizablePanel>
            </>
          )}
        </ResizablePanelGroup>
      </div>

      {/* Status bar */}
      <footer className="flex h-6 shrink-0 items-center gap-3 border-t border-border bg-rail px-2 text-2xs text-muted-foreground">
        <span className="flex items-center gap-1">
          {session.running ? <Play className="size-3 text-ok" /> : <Square className="size-3" />}
          Sessão {session.running ? "ativa" : "parada"}
        </span>
        <span className="num">{formatClock(session.seconds)}</span>
        <span className="num">
          {metronome.bpm} BPM · {metronome.beats}/4
        </span>
        <span>{instruments.find((i) => i.id === instrument)?.name}</span>
        <span className="ml-auto num opacity-70">
          ⌘K busca · ⌘B sidebar · ⌘\ inspetor · ⌘M metrônomo
        </span>
      </footer>

      <CommandPalette />
    </div>
  );
}
