import { createFileRoute, Link, Outlet, useRouterState } from "@tanstack/react-router";
import { Panel, Meter } from "@/components/workspace/Panel";
import { QueryState } from "@/components/workspace/QueryState";
import { Breadcrumb } from "@/components/workspace/TabBar";
import { useInstruments, useSongs } from "@/lib/music-api";
import { cn } from "@/lib/utils";
import { SongSuggestions } from "@/components/music/SongSuggestions";
import { useWorkspace } from "@/lib/workspace-store";
import { CatalogEditor } from "@/components/music/CatalogEditor";

export const Route = createFileRoute("/repertorio")({
  head: () => ({ meta: [{ title: "Repertório - Music OS" }] }),
  component: RepertoireLayout,
});

function RepertoireLayout() {
  const { instrument } = useWorkspace();
  const pathname = useRouterState({ select: (state) => state.location.pathname });
  const songsQuery = useSongs();
  const instrumentsQuery = useInstruments();
  if (!songsQuery.data) return <QueryState error={songsQuery.error} />;
  const songs = songsQuery.data;
  const instruments = instrumentsQuery.data ?? [];
  const isIndex = pathname === "/repertorio";

  return (
    <div className="flex h-full min-h-0 flex-col">
      <Breadcrumb trail={["Praticar", "Repertório"]} />
      <div className="grid min-h-0 flex-1 grid-cols-1 gap-px overflow-hidden bg-border lg:grid-cols-[260px_1fr]">
        <Panel title={`Músicas (${songs.length})`} bodyClassName="p-0" actions={<CatalogEditor kind="song" instrument={instrument} />}>
          {songs.map((song) => (
            <Link
              key={song.id}
              to="/repertorio/$songId"
              params={{ songId: song.id }}
              className={cn(
                "block border-b border-border/60 px-2 py-1.5 text-xs",
                pathname.endsWith(song.id) ? "bg-surface" : "hover:bg-surface/50",
              )}
            >
              <div className="flex justify-between">
                <span>{song.title}</span>
                <span className="label-tech">
                  {instruments.find((item) => item.id === song.instrument)?.shortName}
                </span>
              </div>
              <span className="text-2xs text-muted-foreground">{song.artist}</span>
            </Link>
          ))}
        </Panel>
        <div className="min-h-0 overflow-auto bg-panel">
          {isIndex ? (
            <div>
            <table className="w-full text-xs">
              <thead>
                <tr className="border-b border-border bg-surface text-left">
                  {["Música", "Artista", "Afinação", "Tom", "BPM", "Dif.", "Status", "Progresso"].map((heading) => (
                    <th key={heading} className="label-tech px-2 py-1 font-normal">{heading}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {songs.map((song) => (
                  <tr key={song.id} className="border-b border-border/60 hover:bg-surface/50">
                    <td className="px-2 py-1">
                      <Link to="/repertorio/$songId" params={{ songId: song.id }} className="hover:text-signal">
                        {song.title}
                      </Link>
                    </td>
                    <td className="px-2 text-muted-foreground">{song.artist}</td>
                    <td className="num px-2 text-muted-foreground">{song.tuning}</td>
                    <td className="num px-2">{song.musicalKey}</td>
                    <td className="num px-2">{song.bpm}</td>
                    <td className="num px-2">{"■".repeat(song.difficulty)}</td>
                    <td className="px-2 text-muted-foreground">{song.status}</td>
                    <td className="w-32 px-2"><Meter value={song.progress} tone={song.progress > 85 ? "ok" : "info"} /></td>
                  </tr>
                ))}
              </tbody>
            </table>
            <SongSuggestions instrument={instrument} />
            </div>
          ) : <Outlet />}
        </div>
      </div>
    </div>
  );
}
