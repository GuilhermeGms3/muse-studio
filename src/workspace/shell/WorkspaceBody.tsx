import type { ReactNode } from "react";
import { useIsMobile } from "@/hooks/use-mobile";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";
import { ContextNavigation } from "@/workspace/navigation/ContextNavigation";
import { WorkspaceTabs } from "@/workspace/tabs/WorkspaceTabs";
import { InspectorHost } from "@/workspace/inspector/InspectorHost";
import { DockHost } from "@/workspace/dock/DockHost";
import { ResizableHandle, ResizablePanel, ResizablePanelGroup } from "@/components/ui/resizable";

function WorkspaceCenter({ children }: { children: ReactNode }) {
  return (
    <div className="flex h-full min-h-0 flex-col bg-background-workspace">
      <WorkspaceTabs />
      <main id="workspace-content" className="min-h-0 flex-1 overflow-auto">
        {children}
      </main>
      <DockHost />
    </div>
  );
}

export function WorkspaceBody({ children }: { children: ReactNode }) {
  const { inspectorOpen, sidebarOpen } = useWorkspace();
  const mobile = useIsMobile();

  if (mobile) {
    return (
      <div className="relative h-full min-h-0 overflow-hidden">
        <WorkspaceCenter>{children}</WorkspaceCenter>
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
