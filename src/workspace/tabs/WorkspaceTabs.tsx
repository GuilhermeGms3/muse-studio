import { Link, useNavigate, useRouterState } from "@tanstack/react-router";
import { Circle, LoaderCircle, Pin, TriangleAlert, X } from "lucide-react";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";
import { cn } from "@/shared/utils/cn";
import { tabKey } from "./tab-model";

export function WorkspaceTabs() {
  const pathname = useRouterState({ select: (state) => state.location.pathname });
  const navigate = useNavigate();
  const { tabs, closeTab } = useWorkspace();

  const close = (key: string, path: string, dirty?: boolean) => {
    if (dirty && !window.confirm("Esta aba tem alterações não salvas. Fechar mesmo assim?")) {
      return;
    }
    const index = tabs.findIndex((tab) => tabKey(tab) === key);
    const fallback = tabs[index + 1] ?? tabs[index - 1];
    closeTab(key);
    if (path.split("?")[0] === pathname) navigate({ to: fallback?.path ?? "/" });
  };

  return (
    <div
      role="tablist"
      aria-label="Abas do workspace"
      className="flex h-9 shrink-0 items-stretch overflow-x-auto border-b border-border bg-background-rail"
    >
      {tabs.map((tab) => {
        const key = tabKey(tab);
        const active = tab.path.split("?")[0] === pathname;
        return (
          <div
            key={key}
            role="presentation"
            className={cn(
              "group flex min-w-[120px] max-w-[220px] items-center gap-2 border-r border-border px-2 text-xs",
              tab.pinned && "min-w-10 max-w-12 justify-center px-1",
              active
                ? "border-t border-t-signal bg-surface-panel text-text-primary"
                : "bg-background-rail text-text-muted hover:bg-surface-card/60",
            )}
          >
            {tab.pinned && <Pin className="size-3 shrink-0" aria-hidden="true" />}
            <Link
              to={tab.path}
              role="tab"
              aria-selected={active}
              title={tab.title}
              className={cn(
                "min-w-0 flex-1 truncate py-2 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring",
                tab.pinned && "sr-only",
              )}
            >
              {tab.title}
            </Link>
            <span
              className="flex shrink-0 items-center gap-1"
              aria-label={
                tab.error
                  ? "Erro na aba"
                  : tab.loading
                    ? "Carregando aba"
                    : tab.dirty
                      ? "Alterações não salvas"
                      : undefined
              }
            >
              {tab.error && (
                <TriangleAlert className="size-3 text-destructive" aria-hidden="true" />
              )}
              {tab.loading && <LoaderCircle className="size-3 animate-spin" aria-hidden="true" />}
              {tab.dirty && !tab.loading && !tab.error && (
                <Circle className="size-2 fill-current" aria-hidden="true" />
              )}
            </span>
            {!tab.pinned && (
              <button
                type="button"
                onClick={() => close(key, tab.path, tab.dirty)}
                aria-label={`Fechar aba ${tab.title}`}
                title={`Fechar ${tab.title}`}
                className="flex size-6 shrink-0 items-center justify-center opacity-0 transition-opacity hover:bg-surface-hover focus-visible:opacity-100 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring group-hover:opacity-100"
              >
                <X className="size-3" aria-hidden="true" />
              </button>
            )}
          </div>
        );
      })}
    </div>
  );
}
