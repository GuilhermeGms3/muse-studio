import { useParams } from "@tanstack/react-router";
import { Link } from "@tanstack/react-router";
import { QueryState } from "@/shared/ui/query/QueryState";
import { LessonRenderer } from "@/shared/music/LessonRenderer";
import { useLibrary } from "@/lib/music-api";

export function LibraryNodePage() {
  const { nodeId } = useParams({ from: "/biblioteca/$nodeId" });
  const libraryQuery = useLibrary();
  if (!libraryQuery.data) return <QueryState error={libraryQuery.error} />;

  const node = libraryQuery.data.find((item) => item.id === nodeId);
  if (!node) return <QueryState error={new Error("Conteúdo não encontrado.")} />;

  return (
    <div>
      <LessonRenderer lesson={node} />
      {!!node.related.length && (
        <div className="mx-auto max-w-4xl border-t border-border px-5 py-4">
          <span className="label-tech">Próximos conteúdos</span>
          <div className="mt-1 flex flex-wrap gap-3">
            {node.related.map((id) => {
              const target = libraryQuery.data.find((item) => item.id === id);
              if (!target) return null;
              return (
                <Link
                  key={id}
                  to="/biblioteca/$nodeId"
                  params={{ nodeId: id }}
                  className="text-xs text-info hover:text-signal"
                >
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
