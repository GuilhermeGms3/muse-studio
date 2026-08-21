import { Link } from "@tanstack/react-router";
import { ArrowRight, Music2 } from "lucide-react";
import { useSongs } from "@/shared/api/repertoire";
import { QueryState } from "@/shared/ui/query/QueryState";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";

export function SongsWorkspace() {
  const { instrument } = useWorkspace();
  const query = useSongs(instrument);
  if (!query.data) return <QueryState error={query.error} />;

  return (
    <div className="mx-auto w-full max-w-6xl px-4 py-6 md:px-8 md:py-8">
      <header className="border-b border-border pb-5">
        <span className="label-tech">Aplicação musical</span>
        <h1 className="mt-1 text-2xl font-medium tracking-tight md:text-3xl">Músicas</h1>
        <p className="mt-2 max-w-2xl text-sm text-text-muted">
          Seu repertório é o lugar onde técnica, percepção e intenção se encontram.
        </p>
      </header>
      {query.data.length === 0 ? (
        <section className="py-16 text-center">
          <Music2 className="mx-auto size-7 text-text-muted" />
          <h2 className="mt-4 text-lg font-medium">
            Nenhuma música adicionada para este instrumento.
          </h2>
          <p className="mt-2 text-sm text-text-muted">
            Adicione repertório quando houver uma música que você realmente queira desenvolver.
          </p>
          <Link
            to="/explorar"
            className="mt-5 inline-flex min-h-11 items-center border border-signal px-4 text-signal"
          >
            Explorar possibilidades musicais
          </Link>
        </section>
      ) : (
        <ol className="mt-2 divide-y divide-border border-y border-border">
          {query.data.map((song, index) => (
            <Link
              key={song.id}
              to="/musicas/$songId"
              params={{ songId: song.id }}
              className="group grid min-h-24 grid-cols-[36px_minmax(0,1fr)_auto] items-center gap-4 py-4 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring md:grid-cols-[64px_minmax(220px,.8fr)_minmax(0,1fr)_auto]"
            >
              <span className="num text-xl text-text-muted">
                {String(index + 1).padStart(2, "0")}
              </span>
              <span>
                <span className="label-tech">{song.artist}</span>
                <h2 className="mt-1 text-base font-medium md:text-lg">{song.title}</h2>
              </span>
              <span className="hidden text-xs text-text-muted md:block">
                {song.sections.length} seção(ões) · <span className="num">{song.bpm} BPM</span> ·{" "}
                {song.techniques.join(" · ")}
              </span>
              <span className="inline-flex items-center gap-2 text-xs text-signal">
                Abrir{" "}
                <ArrowRight className="size-3.5 transition-transform group-hover:translate-x-0.5" />
              </span>
            </Link>
          ))}
        </ol>
      )}
    </div>
  );
}
