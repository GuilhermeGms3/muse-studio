import { Link, useRouterState } from "@tanstack/react-router";
import { ChevronDown } from "lucide-react";
import { useState } from "react";
import { navTree } from "@/lib/nav";
import { cn } from "@/lib/utils";

export function WorkspaceSidebar() {
  const pathname = useRouterState({ select: (s) => s.location.pathname });
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
                    </div>
                  );
                })}
            </div>
          );
        })}
      </div>
    </div>
  );
}
