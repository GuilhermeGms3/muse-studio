import { useEffect, useRef, type ReactNode } from "react";
import { useIsMobile } from "@/hooks/use-mobile";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";
import { ContextNavigation } from "@/workspace/navigation/ContextNavigation";
import { WorkspaceTabs } from "@/workspace/tabs/WorkspaceTabs";
import { InspectorHost } from "@/workspace/inspector/InspectorHost";
import { DockHost } from "@/workspace/dock/DockHost";
import { ResizableHandle, ResizablePanel, ResizablePanelGroup } from "@/components/ui/resizable";
import { PrimaryNavigation } from "@/workspace/navigation/PrimaryNavigation";

function WorkspaceCenter({ children }: { children: ReactNode }) {
  return (
    <div className="flex h-full min-h-0 w-full min-w-0 flex-col overflow-hidden bg-background-workspace">
      <WorkspaceTabs />
      <main
        id="workspace-content"
        className="min-h-0 w-full min-w-0 flex-1 overflow-y-auto overflow-x-hidden pb-16 md:pb-0"
      >
        {children}
      </main>
      <DockHost />
      <div className="absolute inset-x-0 bottom-0 z-40 md:hidden">
        <PrimaryNavigation mobile />
      </div>
    </div>
  );
}

export function WorkspaceBody({ children }: { children: ReactNode }) {
  const { inspectorOpen, sidebarOpen, toggleInspector, toggleSidebar } = useWorkspace();
  const mobile = useIsMobile();
  const normalizedMobilePanels = useRef(false);

  useEffect(() => {
    if (!mobile || normalizedMobilePanels.current) return;
    normalizedMobilePanels.current = true;
    if (sidebarOpen) toggleSidebar();
    if (inspectorOpen) toggleInspector();
  }, [inspectorOpen, mobile, sidebarOpen, toggleInspector, toggleSidebar]);

  if (mobile) {
    return (
      <div className="relative h-full min-h-0 overflow-hidden">
        <WorkspaceCenter>{children}</WorkspaceCenter>
        {(sidebarOpen || inspectorOpen) && (
          <button
            type="button"
            aria-label="Fechar painel lateral"
            onClick={sidebarOpen ? toggleSidebar : toggleInspector}
            className="absolute inset-0 z-20 bg-black/55"
          />
        )}
        {sidebarOpen && (
          <div className="absolute inset-y-0 left-0 z-30 w-[min(288px,88vw)] border-r border-border shadow-2xl">
            <ContextNavigation />
          </div>
        )}
        {inspectorOpen && (
          <aside className="absolute inset-y-0 right-0 z-30 w-[min(320px,92vw)] border-l border-border shadow-2xl">
            <InspectorHost />
          </aside>
        )}
      </div>
    );
  }

  return (
    <ResizablePanelGroup orientation="horizontal">
      {sidebarOpen && (
        <>
          <ResizablePanel defaultSize="22%" minSize="220px" maxSize="288px">
            <ContextNavigation />
          </ResizablePanel>
          <ResizableHandle className="w-px bg-border hover:bg-border-strong" />
        </>
      )}
      <ResizablePanel defaultSize={inspectorOpen ? "58%" : "78%"}>
        <WorkspaceCenter>{children}</WorkspaceCenter>
      </ResizablePanel>
      {inspectorOpen && (
        <>
          <ResizableHandle className="w-px bg-border hover:bg-border-strong" />
          <ResizablePanel defaultSize="20%" minSize="240px" maxSize="360px">
            <InspectorHost />
          </ResizablePanel>
        </>
      )}
    </ResizablePanelGroup>
  );
}
