import type { HTMLAttributes, ReactNode } from "react";
import { cn } from "@/shared/utils/cn";

export function WorkspaceCard({
  title,
  eyebrow,
  actions,
  selected = false,
  interactive = false,
  className,
  children,
  ...props
}: HTMLAttributes<HTMLDivElement> & {
  title?: string;
  eyebrow?: string;
  actions?: ReactNode;
  selected?: boolean;
  interactive?: boolean;
}) {
  return (
    <article
      className={cn(
        "border border-border-default bg-surface-card p-3 text-text-primary",
        selected && "border-border-context bg-surface-selected",
        interactive &&
          "transition-colors hover:border-border-strong hover:bg-surface-hover focus-within:border-border-focus",
        className,
      )}
      {...props}
    >
      {(eyebrow || title || actions) && (
        <header className="mb-2 flex min-w-0 items-start justify-between gap-2">
          <div className="min-w-0">
            {eyebrow && <div className="label-tech">{eyebrow}</div>}
            {title && <h3 className="truncate text-sm font-medium">{title}</h3>}
          </div>
          {actions && <div className="flex shrink-0 items-center gap-1">{actions}</div>}
        </header>
      )}
      {children}
    </article>
  );
}
