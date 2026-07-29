import { cn } from "@/lib/utils";
import type { ReactNode } from "react";
import type { SkillState } from "@/lib/music-api";

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
    <section className={cn("flex min-h-0 flex-col border border-border bg-panel", className)}>
      {title && (
        <header className="flex h-7 shrink-0 items-center justify-between border-b border-border bg-surface px-2">
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
      <span className={cn("text-xs text-foreground", mono && "num")}>{value}</span>
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
  return <span className={cn("inline-block size-2 shrink-0 rounded-[1px]", stateColor[state], className)} />;
}

export function StateTag({ state }: { state: SkillState }) {
  return (
    <span className="inline-flex items-center gap-1 border border-border bg-surface px-1.5 py-0.5 text-2xs uppercase tracking-wider text-muted-foreground">
      <StateDot state={state} />
      {skillStateLabel[state]}
    </span>
  );
}

export function Meter({ value, tone = "info" }: { value: number; tone?: "info" | "ok" | "signal" }) {
  const bar = tone === "ok" ? "bg-ok" : tone === "signal" ? "bg-signal" : "bg-info";
  return (
    <div className="h-1 w-full bg-surface-2">
      <div className={cn("h-full", bar)} style={{ width: `${Math.min(100, Math.max(0, value))}%` }} />
    </div>
  );
}
