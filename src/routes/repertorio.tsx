import { createFileRoute, Link, Outlet, useRouterState } from "@tanstack/react-router";
import { Panel, Meter } from "@/components/workspace/Panel";
import { Breadcrumb } from "@/components/workspace/TabBar";
import { songs } from "@/data/practice";
import { instruments } from "@/data/skills";
import { cn } from "@/lib/utils";

export const Route = createFileRoute("/repertorio")({
  head: () => ({
    meta: [
      { title: "Repertório — Music OS" },
      { name: "description", content: "Músicas com afinação, tom, BPM, técnicas e progresso por seção." },
      { property: "og:title", content: "Repertório — Music OS" },
      { property: "og:description", content: "Gerencie músicas e o progresso de cada seção." },
    ],
  }),
  component: RepertoireLayout,
});

function RepertoireLayout() {
  const pathname = useRouterState({ select: (s) => s.location.pathname });
  const isIndex = pathname === "/repertorio";

  return (
    <div className="flex h-full min-h-0 flex-col">
      <Breadcrumb trail={["Prática", "Repertório"]} />
      <div className="grid min-h-0 flex-1 grid-cols-1 gap-px overflow-hidden bg-border lg:grid-cols-[260px_1fr]">
        <Panel title={`Músicas (${songs.length})`} bodyClassName="p-0">
          {songs.map((s) => (
            <Link
              key={s.id}
              to="/repertorio/$songId"
              params={{ songId: s.id }}
              className={cn(
                "block border-b border-border/60 px-2 py-1.5 text-xs",
                pathname.endsWith(s.id) ? "bg-surface" : "hover:bg-surface/50",
              )}
            >
              <div className="flex justify-between">
                <span>{s.title}</span>
                <span className="label-tech">{instruments.find((i) => i.id === s.instrument)?.short}</span>
              </div>
              <span className="text-2xs text-muted-foreground">{s.artist}</span>
            </Link>
          ))}
        </Panel>
        <div className="min-h-0 overflow-auto bg-panel">
          {isIndex ? (
            <table className="w-full text-xs">
              <thead>
                <tr className="border-b border-border bg-surface text-left">
                  {["Música", "Artista", "Afinação", "Tom", "BPM", "Dif.", "Status", "Progresso"].map((h) => (
                    <th key={h} className="label-tech px-2 py-1 font-normal">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {songs.map((s) => {
                  const avg = Math.round(s.sections.reduce((a, x) => a + x.progress, 0) / s.sections.length);
                  return (
                    <tr key={s.id} className="border-b border-border/60 hover:bg-surface/50">
                      <td className="px-2 py-1">
                        <Link to="/repertorio/$songId" params={{ songId: s.id }} className="hover:text-signal">
                          {s.title}
                        </Link>
                      </td>
                      <td className="px-2 text-muted-foreground">{s.artist}</td>
                      <td className="num px-2 text-muted-foreground">{s.tuning}</td>
                      <td className="num px-2">{s.key}</td>
                      <td className="num px-2">{s.bpm}</td>
                      <td className="num px-2">{"■".repeat(s.difficulty)}</td>
                      <td className="px-2 text-muted-foreground">{s.status}</td>
                      <td className="w-32 px-2">
                        <Meter value={avg} tone={avg > 85 ? "ok" : "info"} />
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          ) : (
            <Outlet />
          )}
        </div>
      </div>
    </div>
  );
}
