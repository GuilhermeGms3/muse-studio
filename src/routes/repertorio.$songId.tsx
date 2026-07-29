import { createFileRoute, notFound } from "@tanstack/react-router";
import { songs } from "@/data/practice";
import type { Song } from "@/data/types";
import { Meter, Row } from "@/components/workspace/Panel";

export const Route = createFileRoute("/repertorio/$songId")({
  loader: ({ params }) => {
    const song = songs.find((s) => s.id === params.songId);
    if (!song) throw notFound();
    return { song };
  },
  head: ({ loaderData }) => {
    if (!loaderData) {
      return { meta: [{ title: "Música indisponível — Music OS" }, { name: "robots", content: "noindex" }] };
    }
    const { song } = loaderData;
    return {
      meta: [
        { title: `${song.title} — Repertório | Music OS` },
        { name: "description", content: `${song.title} de ${song.artist}: seções, técnicas e progresso.` },
        { property: "og:title", content: `${song.title} — Repertório` },
        { property: "og:description", content: `${song.artist} · ${song.key} · ${song.bpm} BPM` },
      ],
    };
  },
  component: SongPage,
});

function SongPage() {
  const { song } = Route.useLoaderData() as { song: Song };

  return (
    <div className="p-4">
      <h1 className="text-lg font-semibold tracking-tight">{song.title}</h1>
      <p className="text-xs text-muted-foreground">{song.artist}</p>

      <div className="mt-3 grid grid-cols-2 gap-x-6 md:grid-cols-4">
        <Row label="Afinação" value={song.tuning} />
        <Row label="Tom" value={song.key} />
        <Row label="BPM" value={song.bpm} />
        <Row label="Dificuldade" value={"■".repeat(song.difficulty)} />
        <Row label="Instrumento" value={song.instrument} mono={false} />
        <Row label="Status" value={song.status} mono={false} />
        <Row label="Técnicas" value={song.techniques.join(", ")} mono={false} />
        <Row label="Escalas" value={song.scales.join(", ")} mono={false} />
      </div>

      <div className="mt-5">
        <span className="label-tech">Seções</span>
        <div className="mt-1 border border-border">
          {song.sections.map((sec) => (
            <div key={sec.id} className="flex items-center gap-3 border-b border-border/60 px-2 py-1.5 last:border-0">
              <span className="w-24 text-xs">{sec.name}</span>
              <div className="flex-1">
                <Meter value={sec.progress} tone={sec.progress > 85 ? "ok" : "info"} />
              </div>
              <span className="num w-10 text-right text-2xs">{sec.progress}%</span>
              <span className="num w-16 text-right text-2xs text-muted-foreground">
                {sec.bpm ? `${sec.bpm} BPM` : "—"}
              </span>
              <span className="w-64 truncate text-2xs text-muted-foreground">{sec.note ?? ""}</span>
            </div>
          ))}
        </div>
      </div>

      <div className="mt-5 border border-border bg-rail p-3">
        <span className="label-tech">Anotações</span>
        <p className="text-xs text-muted-foreground">{song.notes}</p>
      </div>

      <div className="mt-4 border border-dashed border-border p-3">
        <span className="label-tech">Gravações</span>
        <p className="text-2xs text-muted-foreground">
          Área preparada para takes vindos do Reaper (bridge ainda não implementada).
        </p>
      </div>
    </div>
  );
}
