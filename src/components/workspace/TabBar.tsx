import { Link, useRouterState } from "@tanstack/react-router";
import { X, ChevronRight } from "lucide-react";
import { useWorkspace } from "@/lib/workspace-store";
import { cn } from "@/lib/utils";
import { titleForPath } from "@/lib/nav";

export function TabBar() {
  const pathname = useRouterState({ select: (s) => s.location.pathname });
  const { tabs, closeTab } = useWorkspace();

  return (
    <div className="flex h-8 shrink-0 items-stretch overflow-x-auto border-b border-border bg-rail">
      {tabs.map((tab) => {
        const active = tab.path === pathname;
        return (
          <div
            key={tab.path}
            className={cn(
              "group flex items-center gap-2 border-r border-border px-3 text-xs",
              active
                ? "border-t border-t-signal bg-panel text-foreground"
                : "bg-rail text-muted-foreground hover:bg-surface/60",
            )}
          >
            <Link to={tab.path} className="whitespace-nowrap py-1">
              {tab.title}
            </Link>
            <button
              onClick={() => closeTab(tab.path)}
              className="opacity-0 transition-opacity group-hover:opacity-100"
            >
              <X className="size-3" />
            </button>
          </div>
        );
      })}
    </div>
  );
}

export function Breadcrumb({ trail }: { trail: string[] }) {
  const pathname = useRouterState({ select: (s) => s.location.pathname });
  const items = trail.length ? trail : [titleForPath(pathname)];
  return (
    <div className="flex h-6 shrink-0 items-center gap-1 border-b border-border bg-panel px-3 text-2xs text-muted-foreground">
      <span>Music OS</span>
      {items.map((t) => (
        <span key={t} className="flex items-center gap-1">
          <ChevronRight className="size-3" />
          <span className="text-foreground/80">{t}</span>
        </span>
      ))}
    </div>
  );
}
