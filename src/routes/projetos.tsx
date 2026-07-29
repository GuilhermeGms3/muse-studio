import { createFileRoute, Link, Outlet, useRouterState } from "@tanstack/react-router";
import { Panel } from "@/components/workspace/Panel";
import { QueryState } from "@/components/workspace/QueryState";
import { Breadcrumb } from "@/components/workspace/TabBar";
import { useProjects } from "@/lib/music-api";
import { cn } from "@/lib/utils";
import { CatalogEditor } from "@/components/music/CatalogEditor";
import { useWorkspace } from "@/lib/workspace-store";

export const Route = createFileRoute("/projetos")({
  head: () => ({ meta: [{ title: "Projetos - Music OS" }] }),
  component: ProjectsLayout,
});

function ProjectsLayout() {
  const { instrument } = useWorkspace();
  const pathname = useRouterState({ select: (state) => state.location.pathname });
  const projectsQuery = useProjects();
  if (!projectsQuery.data) return <QueryState error={projectsQuery.error} />;
  const projects = projectsQuery.data;
  const isIndex = pathname === "/projetos";

  return (
    <div className="flex h-full min-h-0 flex-col">
      <Breadcrumb trail={["Criar", "Projetos"]} />
      <div className="grid min-h-0 flex-1 grid-cols-1 gap-px overflow-hidden bg-border lg:grid-cols-[220px_1fr]">
        <Panel title="Projetos" bodyClassName="p-0" actions={<CatalogEditor kind="project" instrument={instrument} />}>
          {projects.map((project) => (
            <Link
              key={project.id}
              to="/projetos/$projectId"
              params={{ projectId: project.id }}
              className={cn(
                "block border-b border-border/60 px-2 py-1.5 text-xs",
                pathname.endsWith(project.id) ? "bg-surface" : "hover:bg-surface/50",
              )}
            >
              <div>{project.name}</div>
              <span className="num text-2xs text-muted-foreground">
                {project.musicalKey} · {project.bpm} BPM · {project.status}
              </span>
            </Link>
          ))}
        </Panel>
        <div className="min-h-0 overflow-auto bg-panel">
          {isIndex
            ? <div className="p-3 text-xs text-muted-foreground">Selecione um projeto para continuar criando.</div>
            : <Outlet />}
        </div>
      </div>
    </div>
  );
}
