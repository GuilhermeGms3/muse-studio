import { useParams } from "@tanstack/react-router";
import { useNavigate } from "@tanstack/react-router";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { ListMusic, Play } from "lucide-react";
import { CatalogEditor } from "@/shared/catalog/CatalogEditor";
import { InteractiveTab } from "@/shared/music/InteractiveTab";
import { QueryState } from "@/shared/ui/query/QueryState";
import { Meter, Row } from "@/shared/ui/workspace/Panel";
import { createSongPracticePlan, usePreferences, useSongs } from "@/lib/music-api";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";
import { OpenStudioButton } from "@/features/studio/OpenStudioButton";

export function SongPage() {
  const { setMetronome } = useWorkspace();
  const { songId } = useParams({ from: "/repertorio/$songId" });
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const songsQuery = useSongs();
  const preferences = usePreferences();
  const song = songsQuery.data?.find((item) => item.id === songId);
  const plan = useMutation({
    mutationFn: () => createSongPracticePlan(songId, preferences.data?.sessionMinutes ?? 45),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["plan"] });
      queryClient.invalidateQueries({ queryKey: ["home"] });
      navigate({ to: "/sessao" });
    },
  });

  if (!songsQuery.data) return <QueryState error={songsQuery.error} />;
  if (!song) return <QueryState error={new Error("Música não encontrada.")} />;

  return (
    <div className="p-4">
      <div className="mb-2 flex justify-end">
        <CatalogEditor kind="song" instrument={song.instrument} initial={song} />
      </div>
      <h1 className="text-lg font-semibold">{song.title}</h1>
      <p className="text-xs text-muted-foreground">{song.artist}</p>
      <div className="mt-2 flex flex-wrap gap-1">
        <button
          onClick={() => setMetronome({ bpm: song.bpm, playing: true })}
          className="inline-flex h-7 items-center gap-2 border border-border bg-surface px-2 text-2xs hover:border-signal"
        >
          <Play className="size-3 text-signal" />
          Metrônomo em {song.bpm} BPM
        </button>
        <button
          onClick={() => plan.mutate()}
          disabled={plan.isPending}
          className="inline-flex h-7 items-center gap-2 border border-signal bg-signal px-2 text-2xs text-signal-foreground disabled:opacity-40"
        >
          <ListMusic className="size-3" />
          {plan.isPending ? "Montando..." : "Praticar esta música"}
        </button>
        <OpenStudioButton
          instrument={song.instrument}
          sourceKind="REPERTOIRE"
          sourceId={song.id}
          songId={song.id}
          bpm={song.bpm}
        />
      </div>

      <div className="mt-3 grid grid-cols-2 gap-x-6 md:grid-cols-4">
        <Row label="Afinação" value={song.tuning} />
        <Row label="Tom" value={song.musicalKey} />
        <Row label="BPM" value={song.bpm} />
        <Row label="Dificuldade" value={difficultyText(song.difficulty)} mono={false} />
        <Row label="Instrumento" value={song.instrument} mono={false} />
        <Row label="Status" value={song.status} mono={false} />
        <Row label="Técnicas" value={song.techniques.join(", ")} mono={false} />
        <Row label="Escalas" value={song.scales.join(", ")} mono={false} />
      </div>

      <div className="mt-5">
        <span className="label-tech">Seções</span>
        <div className="mt-1 border border-border">
          {song.sections.map((section) => (
            <div key={section.id} className="border-b border-border/60 px-2 py-2 last:border-0">
              <div className="flex items-center gap-3">
                <span className="w-24 text-xs">{section.name}</span>
                <div className="flex-1">
                  <Meter value={section.progress} tone={section.progress > 85 ? "ok" : "info"} />
                </div>
                <span className="num w-10 text-right text-2xs">{section.progress}%</span>
                <span className="num w-16 text-right text-2xs text-muted-foreground">
                  {section.bpm ? `${section.bpm} BPM` : "—"}
                </span>
                <span className="w-64 truncate text-2xs text-muted-foreground">
                  {section.note ?? ""}
                </span>
              </div>
              {section.skillIds?.length > 0 && (
                <p className="mt-1 pl-[6.75rem] text-2xs text-muted-foreground">
                  Habilidades: {section.skillIds.join(", ")}
                </p>
              )}
              {section.tonePreset && (
                <p className="mt-1 pl-[6.75rem] text-2xs text-muted-foreground">
                  Cube Baby: <span className="text-foreground">{section.tonePreset}</span>
                </p>
              )}
              {section.tablature && (
                <div className="mt-2">
                  <InteractiveTab
                    tablature={section.tablature}
                    initialBpm={section.bpm ?? song.bpm}
                  />
                </div>
              )}
            </div>
          ))}
          {!song.sections.length && (
            <div className="px-2 py-2 text-xs text-muted-foreground">Nenhuma seção cadastrada.</div>
          )}
        </div>
      </div>

      <div className="mt-5 border border-border bg-rail p-3">
        <span className="label-tech">Anotações</span>
        <p className="text-xs text-muted-foreground">{song.notes}</p>
      </div>
    </div>
  );
}

function difficultyText(difficulty: number) {
  if (difficulty <= 1) return "Primeiro trecho";
  if (difficulty === 2) return "Base";
  if (difficulty === 3) return "Intermediária";
  if (difficulty === 4) return "Avançada";
  return "Projeto longo";
}
