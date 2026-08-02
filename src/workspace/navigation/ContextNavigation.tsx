import { Link, useRouterState } from "@tanstack/react-router";
import { useEffect, useMemo, useState } from "react";
import { cn } from "@/shared/utils/cn";
import { contextForPath, entryMatchesPath, macroContexts, navigationGroups } from "./registry";
import type { MacroContextId } from "./types";

export function ContextNavigation() {
  const pathname = useRouterState({ select: (state) => state.location.pathname });
  const routeContext = contextForPath(pathname);
  const [selectedContext, setSelectedContext] = useState<MacroContextId>(routeContext);

  useEffect(() => setSelectedContext(routeContext), [routeContext]);

  const selectedDefinition = macroContexts.find((context) => context.id === selectedContext);
  const entries = useMemo(
    () => navigationGroups.find((group) => group.context === selectedContext)?.items ?? [],
    [selectedContext],
  );

  return (
    <nav aria-label="Navegação principal" className="flex h-full min-h-0 bg-background-rail">
      <div className="flex w-16 shrink-0 flex-col border-r border-border py-1">
        {macroContexts.map((context) => {
          const Icon = context.icon;
          const selected = context.id === selectedContext;
          return (
            <button
              key={context.id}
              type="button"
              onClick={() => setSelectedContext(context.id)}
              aria-pressed={selected}
              aria-label={`${context.label}: ${context.description}`}
              title={context.description}
              className={cn(
                "relative flex h-11 w-full items-center justify-center border-l-2 transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-inset focus-visible:ring-ring",
                selected
                  ? "border-signal bg-surface-card text-text-primary"
                  : "border-transparent text-text-muted hover:bg-surface-card/60 hover:text-text-primary",
              )}
            >
              <Icon className="size-4" aria-hidden="true" />
              <span className="sr-only">{context.label}</span>
            </button>
          );
        })}
      </div>

      <div className="flex min-w-0 flex-1 flex-col">
        <header className="flex h-10 shrink-0 items-center border-b border-border px-3">
          <div className="min-w-0">
            <div className="text-xs font-medium text-text-primary">{selectedDefinition?.label}</div>
            <div className="truncate text-2xs text-text-muted">
              {selectedDefinition?.description}
            </div>
          </div>
        </header>
        <div className="min-h-0 flex-1 overflow-y-auto py-1">
          {entries.map((entry) => {
            const active = entryMatchesPath(entry, pathname);
            const Icon = entry.icon;
            return (
              <Link
                key={entry.id}
                to={entry.path}
                aria-current={active ? "page" : undefined}
                title={entry.hint}
                className={cn(
                  "flex min-h-10 items-center gap-2 border-l-2 px-3 py-1.5 text-xs transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-inset focus-visible:ring-ring",
                  active
                    ? "border-signal bg-surface-card text-text-primary"
                    : "border-transparent text-text-muted hover:bg-surface-card/60 hover:text-text-primary",
                )}
              >
                <Icon className="size-4 shrink-0" aria-hidden="true" />
                <span className="min-w-0">
                  <span className="block truncate">{entry.label}</span>
                  {entry.hint && (
                    <span className="block truncate text-2xs text-text-muted">{entry.hint}</span>
                  )}
                </span>
              </Link>
            );
          })}
        </div>
      </div>
    </nav>
  );
}
