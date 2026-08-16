import { useParams } from "@tanstack/react-router";
import { Row } from "@/shared/ui/workspace/Panel";
import { QueryState } from "@/shared/ui/query/QueryState";
import { useProjects } from "@/lib/music-api";
import { CatalogEditor } from "@/shared/catalog/CatalogEditor";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";
import { OpenStudioButton } from "@/features/studio/OpenStudioButton";

export function ProjectPage() {
  const { instrument } = useWorkspace();
  const { projectId } = useParams({ from: "/projetos/$projectId" });
  const projectsQuery = useProjects();
  if (!projectsQuery.data) return <QueryState error={projectsQuery.error} />;
  const project = projectsQuery.data.find((item) => item.id === projectId);
  if (!project) return <QueryState error={new Error("Projeto não encontrado.")} />;

  return (
    <div className="p-4">
      <div className="mb-2 flex justify-end">
        <CatalogEditor kind="project" instrument={instrument} initial={project} />
      </div>
      <div className="flex flex-wrap items-center justify-between gap-3">
        <h1 className="text-lg font-semibold">{project.name}</h1>
        <OpenStudioButton
          instrument={instrument}
          sourceKind="CREATION"
          sourceId={project.id}
          musicProjectId={project.id}
          bpm={project.bpm}
        />
      </div>
      <div className="mt-2 grid grid-cols-2 gap-x-6 md:grid-cols-4">
        <Row label="Tom" value={project.musicalKey} />
        <Row label="BPM" value={project.bpm} />
        <Row label="Status" value={project.status} mono={false} />
        <Row label="Versões" value={project.versions.length} />
      </div>
      <div className="mt-4 grid grid-cols-1 gap-4 lg:grid-cols-2">
        <div>
          <span className="label-tech">Riffs</span>
          {project.riffs.map((riff) => (
            <div key={riff.id} className="mt-1 border border-border bg-rail p-2">
              <div className="text-xs">{riff.name}</div>
              <pre className="num mt-1 overflow-x-auto text-2xs text-muted-foreground">
                {riff.tab}
              </pre>
            </div>
          ))}
        </div>
        <div>
          <span className="label-tech">Ideias</span>
          {project.ideas.map((idea) => (
            <div
              key={idea}
              className="mt-1 border-l border-signal pl-2 text-xs text-muted-foreground"
            >
              {idea}
            </div>
          ))}
          <span className="label-tech mt-4 block">Letra</span>
          <pre className="whitespace-pre-wrap text-xs text-muted-foreground">
            {project.lyrics || "—"}
          </pre>
          <span className="label-tech mt-4 block">Versões</span>
          {project.versions.map((version) => (
            <div key={version.id} className="flex justify-between text-xs text-muted-foreground">
              <span>{version.label}</span>
              <span className="num">{version.date}</span>
            </div>
          ))}
          <span className="label-tech mt-4 block">Referências</span>
          {project.references.map((reference) => (
            <div key={reference} className="text-xs text-muted-foreground">
              · {reference}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
