import { useEffect, useMemo, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Link, useParams } from "@tanstack/react-router";
import { ExternalLink, Gauge, Library, LoaderCircle } from "lucide-react";
import { searchPracticeSong, type InstrumentId, type PracticeTabSection } from "@/lib/music-api";
import { InteractiveTab } from "@/shared/music/InteractiveTab";
import { QueryState } from "@/shared/ui/query/QueryState";
import { Panel } from "@/shared/ui/workspace/Panel";
import { Breadcrumb } from "@/workspace/navigation/Breadcrumb";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";
import { cn } from "@/shared/utils/cn";
import { PracticeMediaPanel } from "./PracticeMediaPanel";
import { ChordSheet } from "./ChordSheet";

export function SongPracticeWorkspace({ query }: { query?: string }) {
  const { songId, instrument: rawInstrument } = useParams({
    from: "/treino-musica/$songId/$instrument",
  });
  const instrument = rawInstrument as InstrumentId;
  const resolvedQuery =
    query?.trim() ||
    songId
      .replaceAll("-", " ")
      .replace(/\b(clipe oficial|official (video|audio)|lyrics?)\b/gi, "")
      .replace(/\s+/g, " ")
      .trim();
  const { openTab, setInstrument, setMetronome } = useWorkspace();
  const songQuery = useQuery({
    queryKey: ["practice-song", resolvedQuery, instrument],
    queryFn: () => searchPracticeSong(resolvedQuery, instrument),
  });
  const practice = songQuery.data?.instruments.find((item) => item.instrument === instrument);
  const [selectedTab, setSelectedTab] = useState(0);
  const [materialMode, setMaterialMode] = useState<"chords" | "tab">("chords");

  useEffect(() => {
    document.getElementById("workspace-content")?.scrollTo({ top: 0 });
  }, [instrument, songId]);

  useEffect(() => {
    if (!songQuery.data || !practice) return;
    const routePath = `/treino-musica/${songId}/${instrument}`;
    const path = `${routePath}?q=${encodeURIComponent(resolvedQuery)}`;
    openTab({
      id: routePath,
      path,
      title: `${songQuery.data.title} · ${practice.label}`,
      type: "song-practice",
      objectId: `${songId}:${instrument}`,
      context: "practice",
    });
    setInstrument(instrument);
  }, [instrument, openTab, practice, resolvedQuery, setInstrument, songId, songQuery.data]);

  const tabs = practice?.tablature ?? [];
  const activeTab = tabs[selectedTab] ?? tabs[0];
  const bpm = activeTab?.bpm ?? practice?.bpm ?? 80;
  const tabTutorials = useMemo(
    () =>
      practice?.videos.filter((video) =>
        /tab|tablature|tutorial|lesson|how to play|sheet music/i.test(video.title),
      ) ?? [],
    [practice?.videos],
  );

  useEffect(() => {
    setSelectedTab(0);
    setMaterialMode(tabs.length ? "tab" : "chords");
  }, [instrument, songId, tabs.length]);

  if (songQuery.isPending) return <PracticeSongLoading instrument={instrument} />;
  if (!songQuery.data) return <QueryState error={songQuery.error} />;
  if (!practice?.available) {
    return (
      <QueryState error={new Error("Este instrumento não está disponível para esta música.")} />
    );
  }

  return (
    <div className="min-h-full bg-background-workspace">
      <Breadcrumb trail={["Praticar", "Treinar música", songQuery.data.title, practice.label]} />

      <header className="border-b border-border bg-surface-panel p-3 md:px-4">
        <div className="flex min-w-0 items-center gap-3">
          {songQuery.data.thumbnailUrl && (
            <img
              src={songQuery.data.thumbnailUrl}
              alt=""
              className="hidden h-16 w-28 shrink-0 object-cover sm:block"
            />
          )}
          <div className="min-w-0 flex-1">
            <span className="label-tech">{practice.label}</span>
            <h1 className="truncate text-lg font-semibold">{songQuery.data.title}</h1>
            <p className="truncate text-xs text-text-muted">{songQuery.data.artist}</p>
          </div>
          <div className="flex shrink-0 flex-wrap justify-end gap-1">
            <button
              type="button"
              onClick={() => setMetronome({ bpm, playing: true })}
              className="inline-flex h-8 items-center gap-2 border border-border bg-surface-card px-2 text-2xs hover:border-signal"
            >
              <Gauge className="size-3 text-signal" aria-hidden="true" />
              {bpm} BPM
            </button>
            {practice.localSongId && (
              <Link
                to="/repertorio/$songId"
                params={{ songId: practice.localSongId }}
                className="inline-flex h-8 items-center gap-2 border border-border bg-surface-card px-2 text-2xs hover:border-signal"
              >
                <Library className="size-3" aria-hidden="true" />
                Repertório
              </Link>
            )}
          </div>
        </div>
      </header>

      <div className="grid gap-3 p-3 xl:grid-cols-[minmax(0,1.6fr)_minmax(340px,0.8fr)]">
        <Panel
          className="min-w-0 self-start"
          title="Material musical"
          actions={
            <a
              href={practice.tablatureUrl}
              target="_blank"
              rel="noreferrer"
              title="Abrir fonte de tablatura"
              className="flex size-7 items-center justify-center hover:bg-surface-hover focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
            >
              <ExternalLink className="size-3" aria-hidden="true" />
            </a>
          }
          bodyClassName="p-0"
        >
          <div className="flex border-b border-border bg-background-rail">
            <button
              type="button"
              onClick={() => setMaterialMode("chords")}
              className={cn(
                "h-8 border-r border-border px-3 text-2xs",
                materialMode === "chords"
                  ? "bg-surface-card text-text-primary"
                  : "text-text-muted hover:bg-surface-hover",
              )}
            >
              Cifra completa
            </button>
            <button
              type="button"
              onClick={() => setMaterialMode("tab")}
              disabled={!tabs.length}
              className={cn(
                "h-8 border-r border-border px-3 text-2xs disabled:cursor-not-allowed disabled:opacity-40",
                materialMode === "tab"
                  ? "bg-surface-card text-text-primary"
                  : "text-text-muted hover:bg-surface-hover",
              )}
            >
              Tablatura local
            </button>
          </div>

          {materialMode === "chords" ? (
            <ChordSheet
              storageKey={`${songId}:${instrument}`}
              instrument={instrument}
              sourceUrl={practice.tablatureUrl}
            />
          ) : activeTab ? (
            <>
              {tabs.length > 1 && (
                <div className="flex overflow-x-auto border-b border-border bg-background-rail">
                  {tabs.map((tab: PracticeTabSection, index: number) => (
                    <button
                      key={tab.id}
                      type="button"
                      onClick={() => setSelectedTab(index)}
                      className={cn(
                        "h-8 shrink-0 border-r border-border px-3 text-2xs",
                        index === selectedTab
                          ? "bg-surface-card text-text-primary"
                          : "text-text-muted hover:bg-surface-hover",
                      )}
                    >
                      {tab.name}
                    </button>
                  ))}
                </div>
              )}
              <div className="p-2">
                <InteractiveTab tablature={activeTab.tablature} initialBpm={bpm} />
              </div>
            </>
          ) : (
            <div className="min-h-64" />
          )}
        </Panel>

        <div className="grid min-w-0 content-start gap-3">
          <PracticeMediaPanel
            title="Vídeo de referência"
            items={tabTutorials.length ? tabTutorials : practice.videos}
          />
          <PracticeMediaPanel
            title={`Voz e base sem ${practice.label.toLowerCase()}`}
            items={practice.vocalTracks ?? []}
          />
          <PracticeMediaPanel title="Playback" items={practice.backingTracks} />
        </div>
      </div>
    </div>
  );
}

const instrumentNames: Record<InstrumentId, string> = {
  drums: "Bateria",
  guitar: "Guitarra",
  acoustic: "Violão",
  keys: "Teclado",
};

function PracticeSongLoading({ instrument }: { instrument: InstrumentId }) {
  const label = instrumentNames[instrument];

  return (
    <div className="min-h-full bg-background-workspace" aria-busy="true" aria-live="polite">
      <Breadcrumb trail={["Praticar", "Treinar música", label]} />
      <header className="flex min-h-24 items-center gap-3 border-b border-border bg-surface-panel p-4">
        <LoaderCircle className="size-5 animate-spin text-signal" aria-hidden="true" />
        <div>
          <p className="text-sm font-medium">Buscando material para {label}</p>
          <p className="mt-1 text-2xs text-text-muted">Preparando a área de treino...</p>
        </div>
      </header>

      <div className="grid gap-3 p-3 xl:grid-cols-[minmax(0,1.6fr)_minmax(340px,0.8fr)]">
        <LoadingPanel title="Material musical" className="min-h-[420px]" />
        <div className="grid min-w-0 content-start gap-3">
          <LoadingPanel title="Vídeo de referência" />
          <LoadingPanel title={`Voz e base sem ${label.toLowerCase()}`} />
          <LoadingPanel title="Playback" />
        </div>
      </div>
    </div>
  );
}

function LoadingPanel({ title, className = "" }: { title: string; className?: string }) {
  return (
    <section className={`min-w-0 border border-border bg-surface-panel ${className}`}>
      <div className="label-tech flex h-8 items-center border-b border-border px-2">{title}</div>
      <div className="flex aspect-video items-center justify-center bg-background-rail">
        <LoaderCircle className="size-4 animate-spin text-text-muted" aria-hidden="true" />
      </div>
    </section>
  );
}
