import { Link, useRouterState } from "@tanstack/react-router";
import { Dumbbell, History, Home, Map, Music2 } from "lucide-react";
import { cn } from "@/shared/utils/cn";
import { primaryNavigationPaths } from "@/features/product-model";

const icons = {
  "/": Home,
  "/jornada": Map,
  "/musicas": Music2,
  "/historico": History,
  "/pratica": Dumbbell,
};
const items = primaryNavigationPaths.map((item) => ({
  ...item,
  to: item.path,
  icon: icons[item.path],
}));

export function PrimaryNavigation({ mobile = false }: { mobile?: boolean }) {
  const pathname = useRouterState({ select: (state) => state.location.pathname });

  return (
    <nav
      aria-label="Navegação principal"
      style={mobile ? { gridTemplateColumns: "repeat(5, minmax(0, 1fr))" } : undefined}
      className={cn(
        mobile
          ? "grid h-16 w-full grid-cols-[repeat(5,minmax(0,1fr))] border-t border-border bg-background-rail/95 px-1 backdrop-blur"
          : "flex h-full w-56 flex-col border-r border-border bg-background-rail px-3 py-5",
      )}
    >
      {!mobile && (
        <Link to="/" className="mb-8 flex min-h-11 items-center gap-3 px-2">
          <span className="grid size-8 place-items-center border border-border-strong text-sm font-semibold text-signal">
            M
          </span>
          <span className="text-sm font-semibold tracking-tight">Muse Studio</span>
        </Link>
      )}

      <div className={cn(mobile ? "contents" : "space-y-1")}>
        {items.map((item) => {
          const active =
            item.to === "/"
              ? pathname === "/"
              : pathname === item.to ||
                (item.to === "/jornada" && pathname.startsWith("/missoes/"));
          const Icon = item.icon;
          return (
            <Link
              key={item.to}
              to={item.to}
              aria-current={active ? "page" : undefined}
              className={cn(
                "group flex min-h-11 min-w-0 items-center transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-inset focus-visible:ring-ring",
                mobile ? "flex-col justify-center gap-1 text-2xs" : "gap-3 border-l-2 px-3 text-sm",
                active
                  ? "border-signal bg-surface-card text-text-primary"
                  : "border-transparent text-text-muted hover:bg-surface-card/60 hover:text-text-primary",
              )}
            >
              <Icon className={cn("size-4", active && "text-signal")} aria-hidden="true" />
              <span className="truncate">{item.label}</span>
            </Link>
          );
        })}
      </div>

      {!mobile && (
        <div className="mt-auto border-t border-border px-2 pt-4 text-2xs leading-relaxed text-text-muted">
          Ferramentas e áreas clássicas estão em{" "}
          <Link to="/explorar" className="text-signal hover:underline">
            Explorar
          </Link>
          .
        </div>
      )}
    </nav>
  );
}
