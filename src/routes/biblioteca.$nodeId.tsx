import { createFileRoute, Link } from "@tanstack/react-router";
import { QueryState } from "@/components/workspace/QueryState";
import { StateTag } from "@/components/workspace/Panel";
import { LessonRenderer } from "@/components/music/LessonRenderer";
import { CatalogEditor } from "@/components/music/CatalogEditor";
import { useLibrary, useSkills } from "@/lib/music-api";
import { useWorkspace } from "@/lib/workspace-store";

export const Route = createFileRoute("/biblioteca/$nodeId")({
  head: () => ({ meta: [{ title: "Biblioteca - Music OS" }] }),
  component: LibraryNodePage,
});

function LibraryNodePage() {
  const { nodeId } = Route.useParams();
  const { instrument } = useWorkspace();
  const libraryQuery = useLibrary();
  const skillsQuery = useSkills(instrument);
  if (!libraryQuery.data) return <QueryState error={libraryQuery.error} />;

  const node = libraryQuery.data.find((item) => item.id === nodeId);
  if (!node) return <QueryState error={new Error("Conteúdo não encontrado.")} />;
  const relatedSkills = (skillsQuery.data ?? []).filter((skill) => skill.contents.includes(node.id));

  return (
    <div>
      <div className="mx-auto flex max-w-4xl justify-end px-5 pt-3">
        <CatalogEditor kind="lesson" instrument={instrument} initial={node} />
      </div>
      <LessonRenderer lesson={node} />
      {!!relatedSkills.length && (
        <div className="mx-auto max-w-4xl border-t border-border px-5 py-4">
          <span className="label-tech">Habilidades ligadas</span>
          <div className="mt-1 flex flex-wrap gap-2">
            {relatedSkills.map((skill) => (
              <Link key={skill.id} to="/skills" className="flex items-center gap-1 text-xs hover:text-signal">
                {skill.technicalName} <StateTag state={skill.state} />
              </Link>
            ))}
          </div>
        </div>
      )}

      {!!node.related.length && (
        <div className="mx-auto max-w-4xl border-t border-border px-5 py-4">
          <span className="label-tech">Próximos conteúdos</span>
          <div className="mt-1 flex flex-wrap gap-3">
            {node.related.map((id) => {
              const target = libraryQuery.data.find((item) => item.id === id);
              if (!target) return null;
              return (
                <Link key={id} to="/biblioteca/$nodeId" params={{ nodeId: id }} className="text-xs text-info hover:text-signal">
                  {target.friendlyTitle}
                </Link>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
}
