import { useQueryClient } from "@tanstack/react-query";
import { Link, useNavigate } from "@tanstack/react-router";
import {
  Cable,
  Circle,
  FolderOpen,
  Gauge,
  Pause,
  Play,
  Repeat2,
  Save,
  Square,
  Star,
} from "lucide-react";
import { useEffect, useRef, useState } from "react";
import { PracticeRecorder } from "@/features/practice/recording/PracticeRecorder";
import {
  addStudioClip,
  addStudioTake,
  configureReaper,
  createStudioProject,
  disconnectReaper,
  openInReaper,
  saveStudioProject,
  studioAudioUrl,
  testReaper,
  updateStudioTake,
  uploadRecording,
  useReaperStatus,
  useStudioProject,
  useStudioProjects,
  type StudioProject,
} from "@/lib/music-api";
import { QueryState } from "@/shared/ui/query/QueryState";
import { Breadcrumb } from "@/workspace/navigation/Breadcrumb";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";
import { Waveform } from "./Waveform";
import { useWebStudio } from "./engine/use-web-studio";

function withAudioUrls(project: StudioProject): StudioProject {
  return {
    ...project,
    clips: project.clips.map((clip) => ({ ...clip, audioUrl: studioAudioUrl(clip.audioUrl) })),
    takes: project.takes.map((take) => ({
      ...take,
      audioUrl: studioAudioUrl(take.audioUrl) ?? "",
    })),
  };
}

export function StudioHome() {
  const { instrument } = useWorkspace();
  const navigate = useNavigate();
  const projects = useStudioProjects();
  const [creating, setCreating] = useState(false);

  if (!projects.data) return <QueryState error={projects.error} />;
  return (
    <div className="flex h-full min-h-0 flex-col">
      <Breadcrumb trail={["Prática", "Studio"]} />
      <main className="min-h-0 flex-1 overflow-auto bg-panel p-4">
        <div className="mx-auto max-w-5xl">
          <div className="flex items-start justify-between gap-4 border-b border-border pb-4">
            <div>
              <span className="label-tech">Ambiente musical</span>
              <h1 className="mt-1 text-xl font-semibold">Studio</h1>
              <p className="mt-1 max-w-xl text-xs text-muted-foreground">
                Prática, aplicação e captura de ideias. Produção aprofundada continua no REAPER.
              </p>
            </div>
            <button
              disabled={creating}
              onClick={async () => {
                setCreating(true);
                try {
                  const project = await createStudioProject({
                    instrument,
                    sourceKind: "FREE",
                    bpm: 80,
                  });
                  await navigate({
                    to: "/studio/$studioProjectId",
                    params: { studioProjectId: project.id },
                  });
                } finally {
                  setCreating(false);
                }
              }}
              className="h-9 border border-signal px-3 text-xs text-signal disabled:opacity-40"
            >
              {creating ? "Criando..." : "Nova sessão de Studio"}
            </button>
          </div>
          <div className="mt-4 grid gap-px bg-border md:grid-cols-2">
            {projects.data.map((project) => (
              <Link
                key={project.id}
                to="/studio/$studioProjectId"
                params={{ studioProjectId: project.id }}
                className="bg-surface p-3 hover:bg-surface-hover"
              >
                <div className="flex items-center justify-between gap-3">
                  <span className="text-sm">{project.title}</span>
                  <span className="label-tech">{project.sourceKind}</span>
                </div>
                <p className="num mt-2 text-2xs text-muted-foreground">
                  {project.bpm} BPM · {project.tracks.length} tracks · {project.takes.length} takes
                </p>
              </Link>
            ))}
            {!projects.data.length && (
              <div className="bg-surface p-5 text-xs text-muted-foreground">
                Nenhum Studio salvo. Comece uma prática livre ou abra a partir de uma Mission.
              </div>
            )}
          </div>
        </div>
      </main>
    </div>
  );
}

export function StudioProjectWorkspace({ id }: { id: string }) {
  const query = useStudioProject(id);
  const reaper = useReaperStatus();
  const queryClient = useQueryClient();
  const { metronome, setMetronome, openTab } = useWorkspace();
  const [project, setProject] = useState<StudioProject>();
  const [saveState, setSaveState] = useState<"saved" | "saving" | "error">("saved");
  const [message, setMessage] = useState<string>();
  const [reaperForm, setReaperForm] = useState({ executablePath: "", workspacePath: "" });
  const dirty = useRef(false);
  const hydratedVersion = useRef<string>();
  const setMetronomeRef = useRef(setMetronome);
  const openTabRef = useRef(openTab);
  setMetronomeRef.current = setMetronome;
  openTabRef.current = openTab;

  useEffect(() => {
    if (!query.data) return;
    const version = `${id}:${query.data.updatedAt}`;
    if (dirty.current || hydratedVersion.current === version) return;
    hydratedVersion.current = version;
    const next = withAudioUrls(query.data);
    setProject(next);
    setMetronomeRef.current({ bpm: next.bpm });
    openTabRef.current({
      path: `/studio/${id}`,
      title: next.title,
      type: "studio",
      context: "practice",
    });
  }, [id, query.data]);

  useEffect(() => {
    if (!project || !dirty.current) return;
    setSaveState("saving");
    const timer = window.setTimeout(() => {
      saveStudioProject(project)
        .then((saved) => {
          dirty.current = false;
          setProject(withAudioUrls(saved));
          setSaveState("saved");
        })
        .catch(() => setSaveState("error"));
    }, 700);
    return () => window.clearTimeout(timer);
  }, [project]);

  if (!project) return <QueryState error={query.error} />;
  return (
    <StudioSurface
      project={project}
      reaperStatus={reaper.data}
      saveState={saveState}
      message={message}
      reaperForm={reaperForm}
      onReaperForm={setReaperForm}
      onChange={(next) => {
        dirty.current = true;
        setProject(next);
      }}
      onRefresh={(next) => {
        dirty.current = false;
        setProject(withAudioUrls(next));
        void queryClient.invalidateQueries({ queryKey: ["studio-projects"] });
      }}
      onMessage={setMessage}
    />
  );
}

function StudioSurface({
  project,
  reaperStatus,
  saveState,
  message,
  reaperForm,
  onReaperForm,
  onChange,
  onRefresh,
  onMessage,
}: {
  project: StudioProject;
  reaperStatus: ReturnType<typeof useReaperStatus>["data"];
  saveState: "saved" | "saving" | "error";
  message?: string;
  reaperForm: { executablePath: string; workspacePath: string };
  onReaperForm: (value: { executablePath: string; workspacePath: string }) => void;
  onChange: (value: StudioProject) => void;
  onRefresh: (value: StudioProject) => void;
  onMessage: (value?: string) => void;
}) {
  const queryClient = useQueryClient();
  const engine = useWebStudio(project);
  const { metronome, setMetronome } = useWorkspace();
  const recordingTrack = project.tracks.find((track) => track.role === "RECORDING");
  const backingTrack = project.tracks.find((track) =>
    ["BACKING", "REFERENCE", "CREATIVE"].includes(track.role),
  );
  const [importing, setImporting] = useState(false);
  const loop = project.regions.find((region) => region.id === project.selectedRegionId);
  const returnPath = project.missionId
    ? `/missoes/${project.missionId}`
    : project.songId
      ? `/repertorio/${project.songId}`
      : project.musicProjectId
        ? `/projetos/${project.musicProjectId}`
        : "/studio";

  return (
    <main className="flex h-full min-h-0 flex-col bg-background">
      <header className="flex min-h-10 flex-wrap items-center justify-between gap-2 border-b border-border bg-rail px-3 py-1.5">
        <div className="min-w-0">
          <span className="label-tech">Studio · {project.sourceKind}</span>
          <input
            value={project.title}
            aria-label="Título do Studio"
            onChange={(event) => onChange({ ...project, title: event.target.value })}
            className="block w-full bg-transparent text-sm font-medium outline-none focus:text-signal"
          />
        </div>
        <div className="flex items-center gap-2 text-2xs text-muted-foreground">
          <Save className={`size-3 ${saveState === "error" ? "text-destructive" : ""}`} />
          {saveState === "saving"
            ? "Salvando"
            : saveState === "error"
              ? "Falha no autosave"
              : "Salvo"}
          <Link to={returnPath} className="border border-border px-2 py-1 hover:border-signal">
            Voltar ao contexto
          </Link>
        </div>
      </header>

      <div className="grid min-h-0 flex-1 grid-cols-1 overflow-auto lg:grid-cols-[minmax(0,1fr)_280px] lg:overflow-hidden">
        <section className="flex min-h-[520px] flex-col overflow-hidden border-r border-border lg:min-h-0">
          <div className="relative h-9 shrink-0 border-b border-border bg-surface px-3">
            {project.regions.map((region) => (
              <button
                key={region.id}
                onClick={() => engine.seek(region.startSeconds)}
                className={`absolute top-1 h-7 truncate border px-2 text-2xs ${region.id === project.selectedRegionId ? "border-signal bg-signal/10 text-signal" : "border-border bg-rail"}`}
                style={{
                  left: `${(region.startSeconds / engine.duration) * 100}%`,
                  width: `${Math.max(6, ((region.endSeconds - region.startSeconds) / engine.duration) * 100)}%`,
                }}
              >
                {region.name}
              </button>
            ))}
          </div>
          <div className="min-h-0 flex-1 overflow-auto">
            {project.tracks.map((track) => (
              <div
                key={track.id}
                className="grid min-h-20 grid-cols-[150px_minmax(500px,1fr)] border-b border-border"
              >
                <div className="border-r border-border bg-rail p-2">
                  <span className="label-tech">{track.role}</span>
                  <p className="mt-1 truncate text-xs">{track.name}</p>
                  <div className="mt-2 flex gap-1">
                    <TrackToggle
                      active={track.muted}
                      label="M"
                      onClick={() =>
                        onChange({
                          ...project,
                          tracks: project.tracks.map((item) =>
                            item.id === track.id ? { ...item, muted: !item.muted } : item,
                          ),
                        })
                      }
                    />
                    <TrackToggle
                      active={track.solo}
                      label="S"
                      onClick={() =>
                        onChange({
                          ...project,
                          tracks: project.tracks.map((item) =>
                            item.id === track.id ? { ...item, solo: !item.solo } : item,
                          ),
                        })
                      }
                    />
                  </div>
                </div>
                <div className="relative overflow-hidden bg-panel">
                  {project.clips
                    .filter((clip) => clip.trackId === track.id)
                    .map((clip) => (
                      <div
                        key={clip.id}
                        className="absolute top-2 h-14 overflow-hidden border border-signal/50 bg-signal/10"
                        style={{
                          left: `${(clip.startSeconds / engine.duration) * 100}%`,
                          width: `${Math.max(8, (clip.durationSeconds / engine.duration) * 100)}%`,
                        }}
                      >
                        <span className="absolute left-1 top-0 z-10 text-2xs">{clip.title}</span>
                        <Waveform url={clip.audioUrl} />
                      </div>
                    ))}
                  <div
                    className="pointer-events-none absolute inset-y-0 w-px bg-signal"
                    style={{ left: `${(engine.position / engine.duration) * 100}%` }}
                  />
                </div>
              </div>
            ))}
          </div>

          <div className="shrink-0 border-t border-border bg-rail p-2">
            <div className="flex flex-wrap items-center gap-2">
              <TransportButton
                label={engine.playing ? "Pause" : "Play"}
                onClick={engine.playing ? engine.pause : engine.play}
                disabled={project.engineMode === "REAPER"}
              >
                {engine.playing ? <Pause className="size-4" /> : <Play className="size-4" />}
              </TransportButton>
              <TransportButton
                label="Stop"
                onClick={engine.stop}
                disabled={project.engineMode === "REAPER"}
              >
                <Square className="size-3.5" />
              </TransportButton>
              <span className="num min-w-20 text-center text-xs">
                {formatTime(engine.position)}
              </span>
              <input
                aria-label="Posição do transporte"
                type="range"
                min={0}
                max={engine.duration}
                step={0.05}
                value={engine.position}
                onChange={(event) => engine.seek(Number(event.target.value))}
                className="min-w-32 flex-1 accent-[var(--color-signal)]"
              />
              <button
                onClick={() => onChange({ ...project, loopEnabled: !project.loopEnabled })}
                aria-pressed={project.loopEnabled}
                className={`inline-flex h-8 items-center gap-1 border px-2 text-xs ${project.loopEnabled ? "border-signal text-signal" : "border-border"}`}
              >
                <Repeat2 className="size-3.5" /> Loop {loop?.name ?? "região"}
              </button>
              <label className="flex items-center gap-1 text-xs">
                <Gauge className="size-3.5 text-signal" />
                <input
                  type="number"
                  min={30}
                  max={300}
                  value={project.bpm}
                  aria-label="BPM de prática"
                  onChange={(event) => {
                    const bpm = Number(event.target.value);
                    setMetronome({ bpm });
                    onChange({ ...project, bpm });
                  }}
                  className="num h-8 w-16 border border-border bg-surface px-2"
                />
              </label>
              <button
                onClick={() => setMetronome({ playing: !metronome.playing, bpm: project.bpm })}
                className={`h-8 border px-2 text-xs ${metronome.playing ? "border-signal text-signal" : "border-border"}`}
              >
                Metrônomo
              </button>
              <label className="flex items-center gap-1 text-xs">
                Count-in
                <input
                  type="number"
                  min={0}
                  max={8}
                  value={project.countInBars}
                  aria-label="Compassos de count-in"
                  onChange={(event) =>
                    onChange({ ...project, countInBars: Number(event.target.value) })
                  }
                  className="num h-8 w-12 border border-border bg-surface px-2"
                />
              </label>
              <div className="flex h-8 border border-border text-2xs" aria-label="Motor ativo">
                {(["WEB", "REAPER"] as const).map((mode) => (
                  <button
                    key={mode}
                    onClick={() => {
                      engine.stop();
                      onChange({ ...project, engineMode: mode });
                    }}
                    disabled={mode === "REAPER" && !reaperStatus?.configured}
                    className={`px-2 ${project.engineMode === mode ? "bg-signal text-background" : "text-muted-foreground"}`}
                  >
                    {mode}
                  </button>
                ))}
              </div>
            </div>
          </div>
        </section>

        <aside className="overflow-visible bg-panel p-3 lg:min-h-0 lg:overflow-auto">
          <section>
            <span className="label-tech">Regiões de prática</span>
            <div className="mt-2 space-y-1">
              {project.regions.map((region) => (
                <button
                  key={region.id}
                  onClick={() => onChange({ ...project, selectedRegionId: region.id })}
                  className={`flex w-full justify-between border px-2 py-1.5 text-xs ${region.id === project.selectedRegionId ? "border-signal" : "border-border"}`}
                >
                  <span>{region.name}</span>
                  <span className="num text-2xs text-muted-foreground">
                    {formatTime(region.startSeconds)}–{formatTime(region.endSeconds)}
                  </span>
                </button>
              ))}
              <button
                onClick={() => {
                  const seconds = (8 * project.timeSignatureNumerator * 60) / project.bpm;
                  const region = {
                    id: crypto.randomUUID(),
                    name: `Trecho ${project.regions.length + 1}`,
                    startSeconds: engine.position,
                    endSeconds: Math.min(engine.duration, engine.position + seconds),
                    origin: "USER" as const,
                  };
                  onChange({
                    ...project,
                    regions: [...project.regions, region],
                    markers: [
                      ...project.markers,
                      {
                        id: crypto.randomUUID(),
                        name: region.name,
                        positionSeconds: region.startSeconds,
                        origin: "USER",
                      },
                    ],
                    selectedRegionId: region.id,
                  });
                }}
                className="w-full border border-dashed border-border py-1.5 text-xs text-muted-foreground"
              >
                + Região de 8 compassos no cursor
              </button>
            </div>
          </section>

          {backingTrack && (
            <section className="mt-5 border-t border-border pt-3">
              <span className="label-tech">Backing / referência</span>
              <label className="mt-2 flex cursor-pointer items-center gap-2 border border-border p-2 text-xs hover:border-signal">
                <FolderOpen className="size-4 text-signal" />
                {importing ? "Importando..." : "Adicionar áudio gerenciado"}
                <input
                  type="file"
                  accept="audio/*"
                  hidden
                  disabled={importing}
                  onChange={async (event) => {
                    const file = event.target.files?.[0];
                    if (!file) return;
                    setImporting(true);
                    try {
                      const duration = await mediaDuration(file);
                      const recording = await uploadRecording(file, {
                        contextType: "studio-asset",
                        contextId: project.id,
                        durationMillis: Math.round(duration * 1000),
                      });
                      onRefresh(
                        await addStudioClip(
                          project.id,
                          backingTrack.id,
                          recording.id,
                          file.name,
                          duration,
                        ),
                      );
                    } finally {
                      setImporting(false);
                    }
                  }}
                />
              </label>
            </section>
          )}

          {recordingTrack && (
            <section className="mt-5 border-t border-border pt-3">
              <PracticeRecorder
                contextType="studio-project"
                contextId={project.id}
                targetBpm={project.bpm}
                countInSeconds={
                  (project.countInBars * project.timeSignatureNumerator * 60) / project.bpm
                }
                onCountInStart={() => setMetronome({ playing: true, bpm: project.bpm })}
                onSaved={async (recording) =>
                  onRefresh(await addStudioTake(project.id, recordingTrack.id, recording.id))
                }
              />
              <span className="label-tech mt-3 block">Takes ({project.takes.length})</span>
              <div className="mt-2 space-y-2">
                {project.takes.map((take) => (
                  <div key={take.id} className="border border-border bg-rail p-2">
                    <div className="flex items-center gap-2">
                      <input
                        value={take.title}
                        aria-label="Nome do take"
                        onChange={(event) =>
                          onChange({
                            ...project,
                            takes: project.takes.map((item) =>
                              item.id === take.id ? { ...item, title: event.target.value } : item,
                            ),
                          })
                        }
                        onBlur={async () => onRefresh(await updateStudioTake(project.id, take))}
                        className="min-w-0 flex-1 bg-transparent text-xs outline-none"
                      />
                      <button
                        title="Marcar take preferido"
                        onClick={async () =>
                          onRefresh(
                            await updateStudioTake(project.id, { ...take, preferred: true }),
                          )
                        }
                      >
                        <Star
                          className={`size-3.5 ${take.preferred ? "fill-signal text-signal" : "text-muted-foreground"}`}
                        />
                      </button>
                    </div>
                    <audio controls src={take.audioUrl} className="mt-2 h-7 w-full" />
                  </div>
                ))}
              </div>
            </section>
          )}

          <section className="mt-5 border-t border-border pt-3">
            <div className="flex items-center justify-between">
              <span className="label-tech">REAPER Integration</span>
              <span className="inline-flex items-center gap-1 text-2xs">
                <Circle
                  className={`size-2 fill-current ${reaperStatus?.status === "CONNECTED" ? "text-ok" : reaperStatus?.configured ? "text-signal" : "text-muted-foreground"}`}
                />
                {reaperStatus?.status ?? "NOT_CONFIGURED"}
              </span>
            </div>
            {!reaperStatus?.configured ? (
              <div className="mt-2 space-y-2">
                <input
                  placeholder="C:\Program Files\REAPER (x64)\reaper.exe"
                  value={reaperForm.executablePath}
                  onChange={(event) =>
                    onReaperForm({ ...reaperForm, executablePath: event.target.value })
                  }
                  className="h-8 w-full border border-border bg-surface px-2 text-2xs"
                />
                <input
                  placeholder="Pasta dos projetos Muse"
                  value={reaperForm.workspacePath}
                  onChange={(event) =>
                    onReaperForm({ ...reaperForm, workspacePath: event.target.value })
                  }
                  className="h-8 w-full border border-border bg-surface px-2 text-2xs"
                />
                <button
                  onClick={async () => {
                    try {
                      const status = await configureReaper(
                        reaperForm.executablePath,
                        reaperForm.workspacePath,
                      );
                      onMessage(status.message);
                      await queryClient.invalidateQueries({ queryKey: ["reaper-status"] });
                    } catch (reason) {
                      onMessage((reason as Error).message);
                    }
                  }}
                  className="h-8 w-full border border-border text-xs hover:border-signal"
                >
                  Configurar
                </button>
              </div>
            ) : (
              <div className="mt-2 grid grid-cols-2 gap-1">
                <button
                  onClick={async () => {
                    onMessage((await testReaper()).message);
                    await queryClient.invalidateQueries({ queryKey: ["reaper-status"] });
                  }}
                  className="h-8 border border-border text-xs"
                >
                  Testar
                </button>
                <button
                  onClick={async () => {
                    onMessage((await disconnectReaper()).message);
                    await queryClient.invalidateQueries({ queryKey: ["reaper-status"] });
                  }}
                  className="h-8 border border-border text-xs"
                >
                  Desconectar
                </button>
                <button
                  disabled={reaperStatus.status === "DISCONNECTED"}
                  onClick={async () => {
                    engine.stop();
                    onMessage((await openInReaper(project.id)).message);
                    onChange({ ...project, engineMode: "REAPER" });
                  }}
                  className="col-span-2 inline-flex h-9 items-center justify-center gap-2 border border-signal text-xs text-signal"
                >
                  <Cable className="size-4" /> Abrir no REAPER
                </button>
              </div>
            )}
            {message && (
              <p
                role="status"
                className="mt-2 border-l border-signal pl-2 text-2xs text-muted-foreground"
              >
                {message}
              </p>
            )}
          </section>
        </aside>
      </div>
    </main>
  );
}

function TrackToggle({
  active,
  label,
  onClick,
}: {
  active: boolean;
  label: string;
  onClick: () => void;
}) {
  return (
    <button
      onClick={onClick}
      aria-pressed={active}
      className={`size-6 border text-2xs ${active ? "border-signal text-signal" : "border-border"}`}
    >
      {label}
    </button>
  );
}

function TransportButton({
  label,
  onClick,
  disabled = false,
  children,
}: {
  label: string;
  onClick: () => void;
  disabled?: boolean;
  children: React.ReactNode;
}) {
  return (
    <button
      aria-label={label}
      onClick={onClick}
      disabled={disabled}
      className="flex size-8 items-center justify-center border border-border bg-surface hover:border-signal disabled:cursor-not-allowed disabled:opacity-40"
    >
      {children}
    </button>
  );
}

function formatTime(seconds: number) {
  const minutes = Math.floor(seconds / 60);
  return `${String(minutes).padStart(2, "0")}:${String(Math.floor(seconds % 60)).padStart(2, "0")}`;
}

function mediaDuration(file: File) {
  return new Promise<number>((resolve) => {
    const url = URL.createObjectURL(file);
    const audio = new Audio(url);
    audio.onloadedmetadata = () => {
      const duration = Number.isFinite(audio.duration) ? audio.duration : 0;
      URL.revokeObjectURL(url);
      resolve(duration);
    };
    audio.onerror = () => {
      URL.revokeObjectURL(url);
      resolve(0);
    };
  });
}
