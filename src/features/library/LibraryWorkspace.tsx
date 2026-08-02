import { Link, Outlet, useRouterState } from "@tanstack/react-router";
import { Panel } from "@/shared/ui/workspace/Panel";
import { QueryState } from "@/shared/ui/query/QueryState";
import { Breadcrumb } from "@/workspace/navigation/Breadcrumb";
import { useLibrary } from "@/lib/music-api";
import { cn } from "@/shared/utils/cn";
import { CatalogEditor } from "@/shared/catalog/CatalogEditor";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";

export function LibraryLayout() {
  const { instrument } = useWorkspace();
  const pathname = useRouterState({ select: (state) => state.location.pathname });
  const libraryQuery = useLibrary();
  if (!libraryQuery.data) return <QueryState error={libraryQuery.error} />;

  const library = libraryQuery.data;
  const categories = Array.from(new Set(library.map((item) => item.category)));
  const isIndex = pathname === "/biblioteca";

  return (
    <div className="flex h-full min-h-0 flex-col">
      <Breadcrumb trail={["Biblioteca"]} />
      <div className="grid min-h-0 flex-1 grid-cols-1 gap-px overflow-hidden bg-border lg:grid-cols-[240px_1fr]">
        <Panel
          title="Índice"
          bodyClassName="p-0"
          actions={<CatalogEditor kind="lesson" instrument={instrument} />}
        >
          {categories.map((category) => (
            <div key={category}>
              <div className="border-b border-border bg-surface px-2 py-0.5">
                <span className="label-tech">{category}</span>
              </div>
              {library
                .filter((item) => item.category === category)
                .map((item) => (
                  <Link
                    key={item.id}
                    to="/biblioteca/$nodeId"
                    params={{ nodeId: item.id }}
                    className={cn(
                      "block border-b border-border/50 px-3 py-1.5 text-xs",
                      pathname.endsWith(item.id)
                        ? "bg-surface text-foreground"
                        : "text-muted-foreground hover:text-foreground",
                    )}
                  >
                    <span className="block text-foreground">{item.friendlyTitle}</span>
                    <span className="label-tech">{item.technicalName}</span>
                  </Link>
                ))}
            </div>
          ))}
        </Panel>
        <div className="min-h-0 overflow-auto bg-panel">
          {isIndex ? (
            <div className="grid grid-cols-1 gap-px bg-border md:grid-cols-2 xl:grid-cols-3">
              {library.map((item) => (
                <Link
                  key={item.id}
                  to="/biblioteca/$nodeId"
                  params={{ nodeId: item.id }}
                  className="bg-panel p-3 hover:bg-surface"
                >
                  <span className="label-tech">{item.category}</span>
                  <h2 className="mt-1 text-sm">{item.friendlyTitle}</h2>
                  <p className="label-tech mt-1">{item.technicalName}</p>
                  <p className="mt-1 text-2xs text-muted-foreground">{item.summary}</p>
                </Link>
              ))}
            </div>
          ) : (
            <Outlet />
          )}
        </div>
      </div>
    </div>
  );
}
