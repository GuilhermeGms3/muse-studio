import type { ReactNode } from "react";
import type { SkillState } from "@/shared/api/contracts";
import { cn } from "@/shared/utils/cn";

export const skillStateLabel: Record<SkillState, string> = {
  locked: "Bloqueada",
  available: "Disponível",
  learning: "Estudando",
  practicing: "Praticando",
  consistent: "Consistente",
  mastered: "Dominada",
  natural: "Natural",
  expert: "Especialista",
};

export function Panel({
  title,
  actions,
  children,
  className,
  bodyClassName,
}: {
  title?: string;
  actions?: ReactNode;
  children: ReactNode;
  className?: string;
  bodyClassName?: string;
}) {
  return (
    <section
      className={cn("flex min-h-0 flex-col border border-border bg-surface-panel", className)}
    >
      {title && (
        <header className="flex min-h-8 shrink-0 items-center justify-between border-b border-border bg-surface-card px-2">
          <span className="label-tech">{title}</span>
          <div className="flex items-center gap-1">{actions}</div>
        </header>
      )}
      <div className={cn("min-h-0 flex-1 overflow-auto p-2", bodyClassName)}>{children}</div>
    </section>
  );
}

export function Row({
  label,
  value,
  mono = true,
}: {
  label: string;
  value: ReactNode;
  mono?: boolean;
}) {
  return (
    <div className="flex items-baseline justify-between gap-2 border-b border-border/60 py-1 last:border-0">
      <span className="label-tech">{label}</span>
      <span className={cn("text-xs text-text-primary", mono && "num")}>{value}</span>
    </div>
  );
}

const stateColor: Record<SkillState, string> = {
  locked: "bg-locked",
  available: "bg-muted-foreground",
  learning: "bg-info/60",
  practicing: "bg-info",
  consistent: "bg-ok/70",
  mastered: "bg-ok",
  natural: "bg-signal",
  expert: "bg-signal",
};

export function StateDot({ state, className }: { state: SkillState; className?: string }) {
  return (
    <span
      className={cn("inline-block size-2 shrink-0 rounded-[1px]", stateColor[state], className)}
      aria-hidden="true"
    />
  );
}

export function StateTag({ state }: { state: SkillState }) {
  return (
    <span className="inline-flex items-center gap-1 border border-border bg-surface-card px-1.5 py-0.5 text-2xs uppercase text-text-muted">
      <StateDot state={state} />
      {skillStateLabel[state]}
    </span>
  );
}

export function Meter({
  value,
  tone = "info",
}: {
  value: number;
  tone?: "info" | "ok" | "signal";
}) {
  const normalized = Math.min(100, Math.max(0, value));
  const bar = tone === "ok" ? "bg-ok" : tone === "signal" ? "bg-signal" : "bg-info";
  return (
    <div
      className="h-1 w-full bg-surface-hover"
      role="progressbar"
      aria-valuemin={0}
      aria-valuemax={100}
      aria-valuenow={Math.round(normalized)}
    >
      <div className={cn("h-full", bar)} style={{ width: `${normalized}%` }} />
    </div>
  );
}
