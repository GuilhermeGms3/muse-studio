import { createFileRoute, notFound } from "@tanstack/react-router";
import { projects } from "@/data/practice";
import type { MusicProject } from "@/data/types";
import { Row } from "@/components/workspace/Panel";

export const Route = createFileRoute("/projetos/$projectId")({
  loader: ({ params }) => {
    const project = projects.find((p) => p.id === params.projectId);
    if (!project) throw notFound();
    return { project };
  },
  head: ({ loaderData }) => {
    if (!loaderData) {
      return { meta: [{ title: "Projeto indisponível — Music OS" }, { name: "robots", content: "noindex" }] };
    }
    const { project } = loaderData;
    return {
      meta: [
        { title: `${project.name} — Projetos | Music OS` },
        { name: "description", content: `Riffs, ideias e versões do projeto ${project.name}.` },
        { property: "og:title", content: `${project.name} — Projetos` },
        { property: "og:description", content: `${project.key} · ${project.bpm} BPM · ${project.status}` },
      ],
    };
  },
  component: ProjectPage,
});

function ProjectPage() {
  const { project } = Route.useLoaderData() as { project: MusicProject };

  return (
    <div className="p-4">
      <h1 className="text-lg font-semibold tracking-tight">{project.name}</h1>
      <div className="mt-2 grid grid-cols-2 gap-x-6 md:grid-cols-4">
        <Row label="Tom" value={project.key} />
        <Row label="BPM" value={project.bpm} />
        <Row label="Status" value={project.status} mono={false} />
        <Row label="Versões" value={project.versions.length} />
      </div>

      <div className="mt-4 grid grid-cols-1 gap-4 lg:grid-cols-2">
        <div>
          <span className="label-tech">Riffs</span>
          {project.riffs.map((r) => (
            <div key={r.id} className="mt-1 border border-border bg-rail p-2">
              <div className="text-xs">{r.name}</div>
              <pre className="num mt-1 overflow-x-auto text-2xs text-muted-foreground">{r.tab}</pre>
            </div>
          ))}
        </div>
        <div>
          <span className="label-tech">Ideias</span>
          <ul className="mt-1 space-y-1">
            {project.ideas.map((i) => (
              <li key={i} className="border-l border-signal pl-2 text-xs text-muted-foreground">{i}</li>
            ))}
          </ul>
          <span className="label-tech mt-4 block">Letra</span>
          <pre className="whitespace-pre-wrap text-xs text-muted-foreground">{project.lyrics || "—"}</pre>
          <span className="label-tech mt-4 block">Versões</span>
          {project.versions.map((v) => (
            <div key={v.id} className="flex justify-between text-xs text-muted-foreground">
              <span>{v.label}</span>
              <span className="num">{v.date}</span>
            </div>
          ))}
          <span className="label-tech mt-4 block">Referências</span>
          {project.references.map((r) => (
            <div key={r} className="text-xs text-muted-foreground">· {r}</div>
          ))}
          <div className="mt-4 border border-dashed border-border p-2">
            <span className="label-tech">Gravações (Reaper)</span>
            <p className="text-2xs text-muted-foreground">Arquitetura preparada; bridge não implementada.</p>
          </div>
        </div>
      </div>
    </div>
  );
}
