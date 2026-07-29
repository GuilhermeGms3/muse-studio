import { createFileRoute, Link, Outlet, useRouterState } from "@tanstack/react-router";
import { Panel } from "@/components/workspace/Panel";
import { Breadcrumb } from "@/components/workspace/TabBar";
import { library, libraryCategories } from "@/data/library";
import { cn } from "@/lib/utils";

export const Route = createFileRoute("/biblioteca")({
  head: () => ({
    meta: [
      { title: "Biblioteca Musical — Music OS" },
      { name: "description", content: "Teoria, harmonia, escalas, técnicas e leitura em conteúdos interligados." },
      { property: "og:title", content: "Biblioteca Musical — Music OS" },
      { property: "og:description", content: "Base de conhecimento musical organizada e conectada." },
    ],
  }),
  component: LibraryLayout,
});

function LibraryLayout() {
  const pathname = useRouterState({ select: (s) => s.location.pathname });
  const isIndex = pathname === "/biblioteca";

  return (
    <div className="flex h-full min-h-0 flex-col">
      <Breadcrumb trail={["Conhecimento", "Biblioteca"]} />
      <div className="grid min-h-0 flex-1 grid-cols-1 gap-px overflow-hidden bg-border lg:grid-cols-[240px_1fr]">
        <Panel title="Índice" bodyClassName="p-0">
          {libraryCategories.map((cat) => {
            const items = library.filter((n) => n.category === cat);
            if (!items.length) return null;
            return (
              <div key={cat}>
                <div className="border-b border-border bg-surface px-2 py-0.5">
                  <span className="label-tech">{cat}</span>
                </div>
                {items.map((n) => (
                  <Link
                    key={n.id}
                    to="/biblioteca/$nodeId"
                    params={{ nodeId: n.id }}
                    className={cn(
                      "block border-b border-border/50 px-3 py-1 text-xs",
                      pathname.endsWith(n.id)
                        ? "bg-surface text-foreground"
                        : "text-muted-foreground hover:text-foreground",
                    )}
                  >
                    {n.title}
                  </Link>
                ))}
              </div>
            );
          })}
        </Panel>
        <div className="min-h-0 overflow-auto bg-panel">
          {isIndex ? (
            <div className="grid grid-cols-1 gap-px bg-border md:grid-cols-2 xl:grid-cols-3">
              {library.map((n) => (
                <Link
                  key={n.id}
                  to="/biblioteca/$nodeId"
                  params={{ nodeId: n.id }}
                  className="bg-panel p-3 hover:bg-surface"
                >
                  <span className="label-tech">{n.category}</span>
                  <h2 className="text-sm text-foreground">{n.title}</h2>
                  <p className="mt-1 text-2xs text-muted-foreground">{n.summary}</p>
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
