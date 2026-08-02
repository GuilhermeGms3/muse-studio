import { useRouterState } from "@tanstack/react-router";
import { ChevronRight } from "lucide-react";
import { titleForRegisteredPath } from "./registry";

export function Breadcrumb({ trail }: { trail: string[] }) {
  const pathname = useRouterState({ select: (state) => state.location.pathname });
  const items = trail.length ? trail : [titleForRegisteredPath(pathname)];

  return (
    <nav
      aria-label="Trilha de navegação"
      className="flex min-h-7 shrink-0 items-center gap-1 overflow-x-auto border-b border-border bg-surface-panel px-3 text-2xs text-text-muted"
    >
      <span>Muse Studio</span>
      {items.map((item, index) => (
        <span key={`${item}-${index}`} className="flex shrink-0 items-center gap-1">
          <ChevronRight className="size-3" aria-hidden="true" />
          <span
            className="text-text-primary/80"
            aria-current={index === items.length - 1 ? "page" : undefined}
          >
            {item}
          </span>
        </span>
      ))}
    </nav>
  );
}
