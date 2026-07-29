import { useEffect, useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { ExternalLink, Music2, Play, Save, Settings2 } from "lucide-react";
import {
  getSongRecommendations,
  savePreferences,
  usePreferences,
  type InstrumentId,
  type SongRecommendation,
} from "@/lib/music-api";
import { QueryState } from "@/components/workspace/QueryState";

export function SongSuggestions({
  instrument,
  skill,
}: {
  instrument: InstrumentId;
  skill?: string;
}) {
  const preferencesQuery = usePreferences();
  const queryClient = useQueryClient();
  const [genres, setGenres] = useState("");
  const [artists, setArtists] = useState("");
  const [selected, setSelected] = useState<SongRecommendation | null>(null);
  useEffect(() => {
    if (!preferencesQuery.data) return;
    setGenres(preferencesQuery.data.favoriteGenres.join(", "));
    setArtists(preferencesQuery.data.favoriteArtists.join(", "));
  }, [preferencesQuery.data]);

  const recommendations = useMutation({
    mutationFn: () => getSongRecommendations(skill ?? "", instrument),
  });
  const save = useMutation({
    mutationFn: () =>
      savePreferences({
        ...preferencesQuery.data!,
        level: preferencesQuery.data?.level ?? "intermediate",
        sessionMinutes: preferencesQuery.data?.sessionMinutes ?? 60,
        favoriteGenres: genres
          .split(",")
          .map((value) => value.trim())
          .filter(Boolean),
        favoriteArtists: artists
          .split(",")
          .map((value) => value.trim())
          .filter(Boolean),
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["preferences"] });
      recommendations.reset();
    },
  });

  if (!preferencesQuery.data) return <QueryState error={preferencesQuery.error} />;

  return (
    <section className="border-t border-border bg-panel">
      <div className="flex items-center justify-between gap-3 px-3 py-2">
        <div>
          <span className="label-tech">Descobrir repertório</span>
          <p className="text-xs">Sugestões pelo seu gosto e pelo que está estudando.</p>
        </div>
        <button
          onClick={() => recommendations.mutate()}
          disabled={recommendations.isPending}
          className="inline-flex h-8 items-center gap-2 border border-border bg-surface px-3 text-xs hover:border-signal"
        >
          <Music2 className="size-3 text-signal" />
          {recommendations.isPending ? "Buscando..." : "Sugerir músicas"}
        </button>
      </div>

      <details className="border-t border-border">
        <summary className="flex cursor-pointer items-center gap-2 px-3 py-2 text-2xs text-muted-foreground">
          <Settings2 className="size-3" />
          Ajustar meu gosto musical
        </summary>
        <div className="grid gap-3 border-t border-border bg-rail p-3 md:grid-cols-2">
          <label className="text-2xs">
            Gêneros, separados por vírgula
            <input
              value={genres}
              onChange={(event) => setGenres(event.target.value)}
              className="mt-1 h-8 w-full border border-border bg-surface px-2 text-xs"
            />
          </label>
          <label className="text-2xs">
            Artistas, separados por vírgula
            <input
              value={artists}
              onChange={(event) => setArtists(event.target.value)}
              className="mt-1 h-8 w-full border border-border bg-surface px-2 text-xs"
            />
          </label>
          <button
            onClick={() => save.mutate()}
            className="inline-flex h-8 items-center justify-center gap-2 border border-border bg-surface px-3 text-xs md:col-span-2 md:justify-self-end"
          >
            <Save className="size-3" />
            Salvar preferências
          </button>
        </div>
      </details>

      {recommendations.data && (
        <div className="grid gap-px border-t border-border bg-border lg:grid-cols-[1fr_1fr]">
          <div className="bg-panel">
            {recommendations.data.map((item) => (
              <button
                key={`${item.videoId}-${item.title}`}
                onClick={() => setSelected(item)}
                className="grid w-full grid-cols-[72px_1fr] gap-2 border-b border-border/60 p-2 text-left hover:bg-surface"
              >
                <div className="flex h-10 items-center justify-center bg-rail">
                  {item.thumbnailUrl ? (
                    <img src={item.thumbnailUrl} alt="" className="h-full w-full object-cover" />
                  ) : (
                    <Play className="size-4 text-signal" />
                  )}
                </div>
                <div className="min-w-0">
                  <p className="truncate text-xs">{item.title}</p>
                  <p className="truncate text-2xs text-muted-foreground">{item.reason}</p>
                </div>
              </button>
            ))}
          </div>
          <div className="min-h-48 bg-rail p-3">
            {selected?.videoId ? (
              <iframe
                title={selected.title}
                src={`https://www.youtube-nocookie.com/embed/${selected.videoId}`}
                className="aspect-video w-full border border-border"
                allow="accelerometer; autoplay; encrypted-media; picture-in-picture"
                allowFullScreen
              />
            ) : selected ? (
              <a
                href={selected.youtubeUrl}
                target="_blank"
                rel="noreferrer"
                className="flex h-full items-center justify-center gap-2 text-xs hover:text-signal"
              >
                Abrir busca no YouTube <ExternalLink className="size-3" />
              </a>
            ) : (
              <p className="text-2xs text-muted-foreground">
                Selecione uma sugestão para ouvir sem sair do fluxo.
              </p>
            )}
          </div>
        </div>
      )}
      {recommendations.error && (
        <p className="border-t border-border p-3 text-xs text-warn">
          {recommendations.error.message}
        </p>
      )}
    </section>
  );
}
