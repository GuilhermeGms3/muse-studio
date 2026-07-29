import { Link, useRouterState } from "@tanstack/react-router";
import { ChevronDown, Star, History as HistoryIcon } from "lucide-react";
import { useState } from "react";
import { navTree, titleForPath } from "@/lib/nav";
import { useWorkspace } from "@/lib/workspace-store";
import { cn } from "@/lib/utils";

export function WorkspaceSidebar() {
  const pathname = useRouterState({ select: (s) => s.location.pathname });
  const { favorites, history, toggleFavorite } = useWorkspace();
  const [collapsed, setCollapsed] = useState<Record<string, boolean>>({});

  return (
    <div className="flex h-full min-h-0 flex-col bg-rail">
      <div className="flex-1 overflow-auto py-1">
        {navTree.map((group) => {
          const isCollapsed = collapsed[group.label];
          return (
            <div key={group.label} className="mb-1">
              <button
                onClick={() => setCollapsed((c) => ({ ...c, [group.label]: !c[group.label] }))}
                className="flex w-full items-center gap-1 px-2 py-1 text-left hover:bg-surface"
              >
                <ChevronDown
                  className={cn("size-3 text-muted-foreground transition-transform", isCollapsed && "-rotate-90")}
                />
                <span className="label-tech">{group.label}</span>
              </button>
              {!isCollapsed &&
                group.items.map((item) => {
                  const active = pathname === item.path;
                  return (
                    <div key={item.path} className="group/item relative">
                      <Link
                        to={item.path}
                        className={cn(
                          "flex items-center gap-2 border-l-2 py-1 pl-5 pr-6 text-xs",
                          active
                            ? "border-signal bg-surface text-foreground"
                            : "border-transparent text-muted-foreground hover:bg-surface/60 hover:text-foreground",
                        )}
                      >
                        <item.icon className="size-3.5 shrink-0" />
                        <span className="truncate">{item.label}</span>
                      </Link>
                      <button
                        onClick={() => toggleFavorite(item.path)}
                        title="Favoritar"
                        className="absolute right-1 top-1/2 -translate-y-1/2 opacity-0 group-hover/item:opacity-100"
                      >
                        <Star
                          className={cn(
                            "size-3",
                            favorites.includes(item.path)
                              ? "fill-signal text-signal opacity-100"
                              : "text-muted-foreground",
                          )}
                        />
                      </button>
                      {favorites.includes(item.path) && (
                        <Star className="pointer-events-none absolute right-1 top-1/2 size-3 -translate-y-1/2 fill-signal text-signal group-hover/item:opacity-0" />
                      )}
                    </div>
                  );
                })}
            </div>
          );
        })}
      </div>

      <div className="border-t border-border">
        <div className="px-2 py-1">
          <span className="label-tech">Favoritos</span>
        </div>
        {favorites.length === 0 && <p className="px-3 pb-2 text-2xs text-muted-foreground">Nenhum</p>}
        {favorites.map((p) => (
          <Link
            key={p}
            to={p}
            className="flex items-center gap-2 px-3 py-0.5 text-xs text-muted-foreground hover:text-foreground"
          >
            <Star className="size-3 fill-signal text-signal" />
            {titleForPath(p)}
          </Link>
        ))}
        <div className="mt-1 border-t border-border px-2 py-1">
          <span className="label-tech">Histórico</span>
        </div>
        <div className="max-h-28 overflow-auto pb-2">
          {history.slice(0, 8).map((p) => (
            <Link
              key={p}
              to={p}
              className="flex items-center gap-2 px-3 py-0.5 text-xs text-muted-foreground hover:text-foreground"
            >
              <HistoryIcon className="size-3" />
              <span className="truncate">{titleForPath(p)}</span>
            </Link>
          ))}
        </div>
      </div>
    </div>
  );
}
