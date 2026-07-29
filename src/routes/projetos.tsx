import { createFileRoute, Link, Outlet, useRouterState } from "@tanstack/react-router";
import { Panel } from "@/components/workspace/Panel";
import { Breadcrumb } from "@/components/workspace/TabBar";
import { projects } from "@/data/practice";
import { cn } from "@/lib/utils";

export const Route = createFileRoute("/projetos")({
  head: () => ({
    meta: [
      { title: "Projetos Musicais — Music OS" },
      { name: "description", content: "Riffs, letras, ideias, versões e referências das composições." },
      { property: "og:title", content: "Projetos Musicais — Music OS" },
      { property: "og:description", content: "Área de criação: riffs, letras e versões." },
    ],
  }),
  component: ProjectsLayout,
});

function ProjectsLayout() {
  const pathname = useRouterState({ select: (s) => s.location.pathname });
  const isIndex = pathname === "/projetos";

  return (
    <div className="flex h-full min-h-0 flex-col">
      <Breadcrumb trail={["Criação", "Projetos Musicais"]} />
      <div className="grid min-h-0 flex-1 grid-cols-1 gap-px overflow-hidden bg-border lg:grid-cols-[220px_1fr]">
        <Panel title="Projetos" bodyClassName="p-0">
          {projects.map((p) => (
            <Link
              key={p.id}
              to="/projetos/$projectId"
              params={{ projectId: p.id }}
              className={cn(
                "block border-b border-border/60 px-2 py-1.5 text-xs",
                pathname.endsWith(p.id) ? "bg-surface" : "hover:bg-surface/50",
              )}
            >
              <div>{p.name}</div>
              <span className="num text-2xs text-muted-foreground">
                {p.key} · {p.bpm} BPM · {p.status}
              </span>
            </Link>
          ))}
        </Panel>
        <div className="min-h-0 overflow-auto bg-panel">
          {isIndex ? (
            <div className="p-3">
              <p className="text-xs text-muted-foreground">Selecione um projeto para abrir riffs, letras e versões.</p>
            </div>
          ) : (
            <Outlet />
          )}
        </div>
      </div>
    </div>
  );
}
