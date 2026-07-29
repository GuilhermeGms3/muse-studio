import { createFileRoute, Link, notFound } from "@tanstack/react-router";
import { libraryById } from "@/data/library";
import type { LibraryNode } from "@/data/types";
import { skillById } from "@/data/skills";
import { Fretboard, KeyboardDiagram } from "@/components/music/Diagrams";
import { StateTag } from "@/components/workspace/Panel";

export const Route = createFileRoute("/biblioteca/$nodeId")({
  loader: ({ params }) => {
    const node = libraryById.get(params.nodeId);
    if (!node) throw notFound();
    return { node };
  },
  head: ({ loaderData }) => {
    if (!loaderData) {
      return { meta: [{ title: "Conteúdo indisponível — Music OS" }, { name: "robots", content: "noindex" }] };
    }
    const { node } = loaderData;
    return {
      meta: [
        { title: `${node.title} — Biblioteca | Music OS` },
        { name: "description", content: node.summary },
        { property: "og:title", content: `${node.title} — Biblioteca` },
        { property: "og:description", content: node.summary },
      ],
    };
  },
  component: LibraryNodePage,
});

function LibraryNodePage() {
  const { node } = Route.useLoaderData() as { node: LibraryNode };

  return (
    <article className="mx-auto max-w-3xl p-5">
      <span className="label-tech">{node.category}</span>
      <h1 className="text-xl font-semibold tracking-tight">{node.title}</h1>
      <p className="mt-1 text-xs text-muted-foreground">{node.summary}</p>

      <div className="mt-4 space-y-3 border-t border-border pt-4 text-[13px] leading-relaxed">
        {node.body.map((p) => (
          <p key={p}>{p}</p>
        ))}
      </div>

      {node.diagram && (
        <div className="mt-5">
          {node.diagram.type === "fretboard" ? (
            <Fretboard highlight={node.diagram.notes} label={node.diagram.label} />
          ) : (
            <KeyboardDiagram highlight={node.diagram.notes} label={node.diagram.label} />
          )}
        </div>
      )}

      {node.examples && (
        <div className="mt-5 border border-border bg-rail p-3">
          <span className="label-tech">Exemplos</span>
          <ul className="mt-1 space-y-1">
            {node.examples.map((e) => (
              <li key={e} className="num text-xs text-foreground/90">{e}</li>
            ))}
          </ul>
        </div>
      )}

      {node.skills && (
        <div className="mt-5">
          <span className="label-tech">Habilidades ligadas</span>
          <div className="mt-1 flex flex-wrap gap-2">
            {node.skills.map((id) => {
              const s = skillById.get(id);
              if (!s) return null;
              return (
                <Link key={id} to="/skills" className="flex items-center gap-1 text-xs hover:text-signal">
                  {s.name} <StateTag state={s.state} />
                </Link>
              );
            })}
          </div>
        </div>
      )}

      {node.related && (
        <div className="mt-5 border-t border-border pt-3">
          <span className="label-tech">Links internos</span>
          <div className="mt-1 flex flex-wrap gap-3">
            {node.related.map((r) => {
              const target = libraryById.get(r);
              if (!target) return null;
              return (
                <Link
                  key={r}
                  to="/biblioteca/$nodeId"
                  params={{ nodeId: r }}
                  className="text-xs text-info hover:text-signal"
                >
                  → {target.title}
                </Link>
              );
            })}
          </div>
        </div>
      )}
    </article>
  );
}
