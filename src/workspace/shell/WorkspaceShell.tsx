import { useEffect, type ReactNode } from "react";
import { useRouterState } from "@tanstack/react-router";
import { Gauge } from "lucide-react";
import { useInstruments } from "@/shared/api/home";
import { useMetronomeEngine } from "@/features/metronome/engine/use-metronome";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";
import { titleForRegisteredPath } from "@/workspace/navigation/registry";
import { CommandPalette } from "@/workspace/command/CommandPalette";
import { WorkspaceBody } from "./WorkspaceBody";
import { WorkspaceHeader } from "./WorkspaceHeader";
import { WorkspaceStatusBar } from "./WorkspaceStatusBar";
import { useWorkspaceShortcuts } from "./useWorkspaceShortcuts";

export function WorkspaceShell({ children }: { children: ReactNode }) {
  const pathname = useRouterState({ select: (state) => state.location.pathname });
  const { data: instruments = [] } = useInstruments();
  const { metronome, openTab, theme } = useWorkspace();

  useMetronomeEngine(metronome);
  useWorkspaceShortcuts();

  useEffect(() => {
    openTab({ path: pathname, title: titleForRegisteredPath(pathname) });
  }, [openTab, pathname]);

  if (pathname === "/sessao") {
    return (
      <div
        data-theme={theme}
        className="flex h-dvh min-h-0 w-full flex-col bg-background-app text-text-primary"
      >
        <header className="flex min-h-10 shrink-0 items-center gap-2 border-b border-border bg-background-rail px-3">
          <Gauge className="size-4 text-signal" aria-hidden="true" />
          <span className="text-xs font-semibold">MUSE STUDIO</span>
          <span className="label-tech">Sessão em foco</span>
        </header>
        <main className="min-h-0 flex-1">{children}</main>
      </div>
    );
  }

  return (
    <div
      data-theme={theme}
      className="flex h-dvh min-h-0 w-full flex-col bg-background-app text-text-primary"
    >
      <a
        href="#workspace-content"
        className="sr-only z-50 bg-signal px-3 py-2 text-signal-foreground focus:not-sr-only focus:absolute focus:left-2 focus:top-2"
      >
        Pular para o workspace
      </a>
      <WorkspaceHeader instruments={instruments} />
      <div className="min-h-0 flex-1">
        <WorkspaceBody>{children}</WorkspaceBody>
      </div>
      <WorkspaceStatusBar instruments={instruments} />
      <CommandPalette />
    </div>
  );
}
