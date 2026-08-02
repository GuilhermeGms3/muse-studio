import { type FormEvent, useState } from "react";
import { useMutation } from "@tanstack/react-query";
import { Link } from "@tanstack/react-router";
import { Drum, Guitar, KeyboardMusic, Search } from "lucide-react";
import { searchPracticeSong, type InstrumentId } from "@/lib/music-api";
import { Breadcrumb } from "@/workspace/navigation/Breadcrumb";
import { cn } from "@/shared/utils/cn";

const instrumentIcons = {
  drums: Drum,
  guitar: Guitar,
  acoustic: Guitar,
  keys: KeyboardMusic,
} satisfies Record<InstrumentId, typeof Guitar>;

export function PracticeSongSearch() {
  const [query, setQuery] = useState("");
  const search = useMutation({ mutationFn: (value: string) => searchPracticeSong(value) });

  const submit = (event: FormEvent) => {
    event.preventDefault();
    const value = query.trim();
    if (value) search.mutate(value);
  };

  return (
    <div className="min-h-full bg-background-workspace">
      <Breadcrumb trail={["Praticar", "Treinar música"]} />
      <div className="mx-auto w-full max-w-5xl p-3 md:p-5">
        <header className="border-b border-border pb-3">
          <h1 className="text-base font-semibold">Treinar uma música</h1>
          <p className="mt-1 text-xs text-text-muted">
            Vídeo, playback e material por instrumento.
          </p>
        </header>

        <form onSubmit={submit} className="mt-4 flex gap-2" role="search">
          <label className="sr-only" htmlFor="practice-song-search">
            Nome da música
          </label>
          <div className="relative min-w-0 flex-1">
            <Search
              className="pointer-events-none absolute left-3 top-1/2 size-4 -translate-y-1/2 text-text-muted"
              aria-hidden="true"
            />
            <input
              id="practice-song-search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Nome da música"
              autoComplete="off"
              className="h-10 w-full border border-border bg-surface-panel pl-9 pr-3 text-sm outline-none focus:border-signal focus:ring-2 focus:ring-ring"
            />
          </div>
          <button
            type="submit"
            disabled={!query.trim() || search.isPending}
            className="inline-flex h-10 items-center gap-2 border border-signal bg-signal px-4 text-xs font-medium text-signal-foreground disabled:cursor-not-allowed disabled:opacity-40"
          >
            <Search className="size-4" aria-hidden="true" />
            {search.isPending ? "Buscando..." : "Buscar"}
          </button>
        </form>

        {search.error && (
          <div
            role="alert"
            className="mt-3 border border-destructive/50 bg-destructive/10 p-3 text-xs"
          >
            {search.error.message}
          </div>
        )}

        {search.data && (
          <article className="mt-4 overflow-hidden border border-border bg-surface-panel">
            <div className="grid gap-0 sm:grid-cols-[200px_1fr]">
              <div className="aspect-video bg-background-rail sm:aspect-auto sm:min-h-44">
                {search.data.thumbnailUrl ? (
                  <img
                    src={search.data.thumbnailUrl}
                    alt=""
                    className="h-full w-full object-cover"
                  />
                ) : (
                  <div className="flex h-full items-center justify-center">
                    <Guitar className="size-8 text-text-muted" aria-hidden="true" />
                  </div>
                )}
              </div>
              <div className="min-w-0 p-3 md:p-4">
                <p className="label-tech">Resultado</p>
                <h2 className="mt-1 text-lg font-semibold">{search.data.title}</h2>
                <p className="text-xs text-text-muted">{search.data.artist}</p>

                <div className="mt-4 grid grid-cols-2 gap-2 lg:grid-cols-4">
                  {search.data.instruments.map((item) => {
                    const Icon = instrumentIcons[item.instrument];
                    return item.available ? (
                      <Link
                        key={item.instrument}
                        to="/treino-musica/$songId/$instrument"
                        params={{ songId: search.data.id, instrument: item.instrument }}
                        search={{ q: query.trim() }}
                        className="flex min-h-20 flex-col justify-between border border-border bg-surface-card p-2.5 hover:border-signal hover:bg-surface-hover focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
                      >
                        <Icon className="size-5 text-signal" aria-hidden="true" />
                        <span className="text-xs font-medium">{item.label}</span>
                      </Link>
                    ) : (
                      <div
                        key={item.instrument}
                        aria-disabled="true"
                        className={cn(
                          "flex min-h-20 flex-col justify-between border border-border bg-background-rail p-2.5",
                          "text-text-muted opacity-55",
                        )}
                      >
                        <Icon className="size-5" aria-hidden="true" />
                        <span className="text-xs">{item.label}</span>
                        <span className="text-2xs">Indisponível</span>
                      </div>
                    );
                  })}
                </div>
              </div>
            </div>
          </article>
        )}
      </div>
    </div>
  );
}
