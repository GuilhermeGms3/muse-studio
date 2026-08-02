import { Link } from "@tanstack/react-router";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { Panel, StateDot, StateTag, Meter } from "@/shared/ui/workspace/Panel";
import { QueryState } from "@/shared/ui/query/QueryState";
import {
  recordSkillEvidence,
  useExercises,
  useLibrary,
  useSkills,
  useSongs,
  type Skill,
  type SkillKind,
  type LearningTrack,
  type SkillState,
} from "@/lib/music-api";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";

const stateText: Record<string, string> = {
  locked: "Bloqueada",
  available: "Disponível",
  learning: "Estudando",
  practicing: "Praticando",
  consistent: "Consistente",
  mastered: "Dominada",
  natural: "Natural",
  expert: "Especialista",
};

const stateFill: Record<string, string> = {
  locked: "var(--color-locked)",
  available: "var(--color-muted-foreground)",
  learning: "var(--color-info)",
  practicing: "var(--color-signal)",
  mastered: "var(--color-ok)",
};

const stages = [
  ["first_steps", "Primeiros passos", "Oriento-me no instrumento e produzo som controlado."],
  ["beginner", "Iniciante", "Executo padrões básicos e minhas primeiras músicas."],
  ["beginner_advanced", "Iniciante avançado", "Toco cinco músicas completas mantendo o tempo."],
  ["early_intermediate", "Intermediário inicial", "Aplico teoria e técnica de forma consciente."],
  ["intermediate", "Intermediário", "Controlo vocabulário, leitura, ouvido e improvisação."],
  ["upper_intermediate", "Intermediário avançado", "Integro, interpreto, transcrevo e performo."],
  ["advanced", "Avançado", "Crio arranjos e adapto linguagem com alto controle."],
] as const;

const trackText: Record<LearningTrack, string> = {
  technique: "Técnica",
  rhythm: "Ritmo",
  ear: "Ouvido",
  reading: "Leitura",
  harmony: "Harmonia",
  repertoire: "Repertório",
  improvisation: "Improvisação",
  creation: "Criação",
  performance: "Performance",
};

const kindText: Record<SkillKind, string> = { knowledge: "Conhecimento", ability: "Habilidade" };

function simpleState(state: SkillState) {
  if (["consistent", "mastered", "natural", "expert"].includes(state)) return "mastered";
  return state;
}

export function SkillsPage() {
  const { instrument } = useWorkspace();
  const queryClient = useQueryClient();
  const skillsQuery = useSkills(instrument);
  const libraryQuery = useLibrary();
  const exercisesQuery = useExercises(instrument);
  const songsQuery = useSongs();
  const [selectedId, setSelectedId] = useState("bends");
  const [selectedTrack, setSelectedTrack] = useState<LearningTrack>("technique");
  const [selectedKind, setSelectedKind] = useState<SkillKind>("ability");
  const [registering, setRegistering] = useState(false);
  const [accuracy, setAccuracy] = useState(80);
  const [bpm, setBpm] = useState(80);

  useEffect(() => {
    const startingPoint = {
      guitar: "guitar-posture",
      acoustic: "acoustic-posture",
      keys: "piano-posture",
      drums: "drum-kit-map",
    } as const;
    setSelectedId(startingPoint[instrument]);
    setSelectedTrack("technique");
    setSelectedKind("ability");
  }, [instrument]);

  const mutation = useMutation({
    mutationFn: (skill: Skill) =>
      recordSkillEvidence(skill.id, {
        hours: 0.25,
        accuracy,
        bpm: skill.targetBpm ? bpm : undefined,
        exerciseCompleted: true,
        selfRating: Math.max(1, Math.min(5, Math.round(accuracy / 20))),
      }),
    onSuccess: () => {
      setRegistering(false);
      queryClient.invalidateQueries({ queryKey: ["skills"] });
      queryClient.invalidateQueries({ queryKey: ["home"] });
      queryClient.invalidateQueries({ queryKey: ["plan"] });
    },
  });

  if (!skillsQuery.data) return <QueryState error={skillsQuery.error} />;
  const skills = skillsQuery.data;
  const selected = skills.find((skill) => skill.id === selectedId) ?? skills[0];
  if (!selected) return <QueryState error={new Error("Nenhuma habilidade encontrada.")} />;
  const byId = new Map(skills.map((skill) => [skill.id, skill]));
  const focusedSkills = skills.filter(
    (skill) => skill.track === selectedTrack && skill.kind === selectedKind,
  );
  const layout = focusedSkills.map((skill) => {
    const column = stages.findIndex(([id]) => id === skill.stage);
    const stageSkills = focusedSkills.filter((candidate) => candidate.stage === skill.stage);
    const row = stageSkills.findIndex((item) => item.id === skill.id);
    return { skill, x: 30 + column * 215, y: 132 + row * 68 };
  });

  const nodeById = new Map(layout.map((node) => [node.skill.id, node]));
  const svgWidth = 1535;
  const svgHeight = Math.max(540, ...layout.map((node) => node.y + 70));
  const selectSkill = (id: string) => {
    const skill = byId.get(id);
    if (!skill) return;
    setSelectedId(id);
    setSelectedTrack(skill.track);
    setSelectedKind(skill.kind);
  };
  const relatedLibrary = (libraryQuery.data ?? []).filter((item) =>
    selected.contents.includes(item.id),
  );
  const relatedExercises = (exercisesQuery.data ?? []).filter(
    (exercise) => exercise.skillId === selected.id || selected.exercises.includes(exercise.id),
  );
  const relatedSongs = (songsQuery.data ?? []).filter(
    (song) =>
      selected.songs.includes(song.id) ||
      [...song.techniques, ...song.scales].some((term) =>
        selected.technicalName.toLowerCase().includes(term.toLowerCase().slice(0, 5)),
      ),
  );

  return (
    <div className="flex h-full min-h-0 flex-col">
      <div className="grid min-h-0 min-w-0 flex-1 grid-cols-1 gap-px overflow-auto bg-border lg:grid-cols-[minmax(0,1fr)_350px] lg:overflow-hidden">
        <Panel
          title={`Knowledge Tree · ${skills.length} skills`}
          bodyClassName="p-0"
          actions={
            <div className="flex items-center gap-2">
              <div className="flex border border-border" aria-label="Tipo de skill">
                {(Object.keys(kindText) as SkillKind[]).map((kind) => (
                  <button
                    key={kind}
                    type="button"
                    onClick={() => setSelectedKind(kind)}
                    className={`h-6 px-2 text-2xs ${selectedKind === kind ? "bg-signal text-signal-foreground" : "bg-surface text-muted-foreground"}`}
                  >
                    {kindText[kind]}
                  </button>
                ))}
              </div>
              <select
                value={selectedTrack}
                onChange={(event) => {
                  const track = event.target.value as LearningTrack;
                  setSelectedTrack(track);
                  const first =
                    skills.find(
                      (skill) =>
                        skill.track === track &&
                        skill.kind === selectedKind &&
                        simpleState(skill.state) !== "locked",
                    ) ??
                    skills.find((skill) => skill.track === track && skill.kind === selectedKind);
                  if (first) setSelectedId(first.id);
                }}
                aria-label="Trilha musical"
                className="h-6 border border-border bg-surface px-2 text-2xs outline-none focus:border-ring"
              >
                {(Object.keys(trackText) as LearningTrack[]).map((track) => (
                  <option key={track} value={track}>
                    {trackText[track]}
                  </option>
                ))}
              </select>
              <div className="hidden items-center gap-2 xl:flex">
                {Object.entries(stateText).map(([key, label]) => (
                  <span
                    key={key}
                    className="flex items-center gap-1 text-2xs text-muted-foreground"
                  >
                    <span
                      className="size-2"
                      style={{ background: stateFill[simpleState(key as SkillState)] }}
                    />
                    {label}
                  </span>
                ))}
              </div>
            </div>
          }
        >
          <div className="h-full w-full overflow-auto bg-rail">
            <svg width={svgWidth} height={svgHeight} viewBox={`0 0 ${svgWidth} ${svgHeight}`}>
              {stages.map(([id, label, outcome], index) => (
                <g key={id}>
                  <rect
                    x={20 + index * 215}
                    y="18"
                    width="204"
                    height="92"
                    rx="2"
                    fill="var(--color-surface)"
                    stroke="var(--color-border-strong)"
                  />
                  <text x={30 + index * 215} y="39" fill="var(--color-signal)" fontSize="10">
                    {label.toUpperCase()}
                  </text>
                  <foreignObject x={30 + index * 215} y="49" width="184" height="52">
                    <p className="text-2xs leading-relaxed text-muted-foreground">{outcome}</p>
                  </foreignObject>
                </g>
              ))}
              {layout.flatMap((node) =>
                node.skill.prerequisites.map((requirement) => {
                  const from = nodeById.get(requirement);
                  if (!from) return null;
                  const active = selected.id === node.skill.id || selected.id === requirement;
                  return (
                    <line
                      key={`${requirement}-${node.skill.id}`}
                      x1={from.x + 184}
                      y1={from.y + 22}
                      x2={node.x}
                      y2={node.y + 22}
                      stroke={active ? "var(--color-signal)" : "var(--color-border-strong)"}
                      strokeWidth={active ? 1.5 : 0.8}
                    />
                  );
                }),
              )}
              {layout.map((node) => {
                const active = selected.id === node.skill.id;
                const state = simpleState(node.skill.state);
                return (
                  <g
                    key={node.skill.id}
                    className="cursor-pointer"
                    onClick={() => selectSkill(node.skill.id)}
                  >
                    <title>
                      {node.skill.friendlyTitle} - {node.skill.technicalName}
                    </title>
                    <rect
                      x={node.x}
                      y={node.y}
                      width="184"
                      height="44"
                      rx="2"
                      fill="var(--color-surface)"
                      stroke={active ? "var(--color-signal)" : "var(--color-border-strong)"}
                      strokeWidth={active ? 1.5 : 1}
                      opacity={state === "locked" ? 0.58 : 1}
                    />
                    <circle cx={node.x + 13} cy={node.y + 14} r="4" fill={stateFill[state]} />
                    <text
                      x={node.x + 24}
                      y={node.y + 17}
                      fill="var(--color-foreground)"
                      fontSize="11"
                    >
                      {node.skill.friendlyTitle.length > 25
                        ? `${node.skill.friendlyTitle.slice(0, 24)}…`
                        : node.skill.friendlyTitle}
                    </text>
                    <text
                      x={node.x + 24}
                      y={node.y + 33}
                      fill="var(--color-muted-foreground)"
                      fontFamily="var(--font-mono)"
                      fontSize="9"
                    >
                      {node.skill.technicalName.toUpperCase()}
                    </text>
                  </g>
                );
              })}
            </svg>
          </div>
        </Panel>

        <aside className="flex min-h-0 flex-col gap-px overflow-auto bg-border">
          <Panel title="Habilidade">
            <div className="flex items-start justify-between gap-3">
              <div>
                <h1 className="text-base font-semibold">{selected.friendlyTitle}</h1>
                <p className="label-tech mt-1">{selected.technicalName}</p>
              </div>
              <StateTag state={selected.state} />
            </div>
            <p className="mt-3 text-xs leading-relaxed text-muted-foreground">
              {selected.description}
            </p>
            <div className="mt-3 flex flex-wrap gap-1.5">
              <span className="border border-border px-2 py-1 text-2xs text-signal">
                {stages.find(([id]) => id === selected.stage)?.[1]}
              </span>
              <span className="border border-border px-2 py-1 text-2xs text-muted-foreground">
                {kindText[selected.kind]}
              </span>
              <span className="border border-border px-2 py-1 text-2xs text-muted-foreground">
                {trackText[selected.track]}
              </span>
            </div>
            <div className="mt-3 flex items-center gap-3">
              <Meter
                value={selected.progress}
                tone={selected.state === "mastered" ? "ok" : "info"}
              />
              <span className="num text-xs">{selected.progress}%</span>
            </div>
            <div className="mt-2 flex justify-between text-2xs text-muted-foreground">
              <span>Retenção estimada</span>
              <span className="num">{selected.retention}%</span>
            </div>
            {selected.nextReviewAt && (
              <div className="mt-1 flex justify-between text-2xs text-muted-foreground">
                <span>Próxima revisão</span>
                <span className="num">
                  {new Date(selected.nextReviewAt).toLocaleDateString("pt-BR")}
                </span>
              </div>
            )}
            <button
              onClick={() => setRegistering((value) => !value)}
              disabled={selected.state === "locked"}
              className="mt-3 h-8 w-full border border-border bg-surface text-xs hover:border-signal disabled:opacity-40"
            >
              Registrar prática
            </button>
            {registering && (
              <div className="mt-2 border border-border bg-rail p-2">
                <label className="label-tech block">Precisão · {accuracy}%</label>
                <input
                  type="range"
                  min="0"
                  max="100"
                  value={accuracy}
                  onChange={(event) => setAccuracy(Number(event.target.value))}
                  className="w-full"
                />
                {selected.targetBpm && (
                  <label className="mt-2 flex items-center justify-between text-2xs">
                    <span className="label-tech">BPM alcançado</span>
                    <input
                      type="number"
                      value={bpm}
                      onChange={(event) => setBpm(Number(event.target.value))}
                      className="num h-6 w-16 border border-border bg-surface px-1"
                    />
                  </label>
                )}
                <button
                  onClick={() => mutation.mutate(selected)}
                  disabled={mutation.isPending}
                  className="mt-2 h-7 w-full bg-signal text-xs font-medium text-signal-foreground"
                >
                  Salvar 15 minutos
                </button>
              </div>
            )}
          </Panel>

          <Panel title="Próximo critério">
            {selected.nextRequirements.map((requirement) => (
              <p
                key={requirement}
                className="border-l border-signal pl-2 text-xs text-muted-foreground"
              >
                {requirement}
              </p>
            ))}
          </Panel>

          <Panel title="Pré-requisitos">
            {!selected.prerequisites.length && (
              <p className="text-2xs text-muted-foreground">Nenhum.</p>
            )}
            {selected.prerequisites.map((id) => (
              <button
                key={id}
                onClick={() => selectSkill(id)}
                className="flex items-center gap-2 text-xs hover:text-signal"
              >
                <StateDot state={byId.get(id)?.state ?? "locked"} />
                {byId.get(id)?.technicalName ?? id}
              </button>
            ))}
          </Panel>

          <Panel title="Conteúdos">
            {!relatedLibrary.length && (
              <p className="text-2xs text-muted-foreground">Nenhum conteúdo ligado.</p>
            )}
            {relatedLibrary.map((item) => (
              <Link
                key={item.id}
                to="/biblioteca/$nodeId"
                params={{ nodeId: item.id }}
                className="block text-xs hover:text-signal"
              >
                {item.friendlyTitle}
                <span className="label-tech ml-1">{item.technicalName}</span>
              </Link>
            ))}
          </Panel>

          <Panel title="Exercícios">
            {!relatedExercises.length && (
              <p className="text-2xs text-muted-foreground">Nenhum exercício ligado.</p>
            )}
            {relatedExercises.map((exercise) => (
              <div key={exercise.id} className="flex justify-between gap-2 text-xs">
                <span>{exercise.name}</span>
                <span className="num text-2xs text-muted-foreground">{exercise.minutes}m</span>
              </div>
            ))}
          </Panel>

          <Panel title="Músicas relacionadas">
            {!relatedSongs.length && (
              <p className="text-2xs text-muted-foreground">Nenhuma música ligada.</p>
            )}
            {relatedSongs.map((song) => (
              <Link
                key={song.id}
                to="/repertorio/$songId"
                params={{ songId: song.id }}
                className="block text-xs hover:text-signal"
              >
                {song.title}
              </Link>
            ))}
          </Panel>

          <Panel title="Próximas habilidades">
            {!selected.nextSkills.length && (
              <p className="text-2xs text-muted-foreground">Fim deste ramo.</p>
            )}
            {selected.nextSkills.map((id) => (
              <button
                key={id}
                onClick={() => selectSkill(id)}
                className="flex items-center gap-2 text-xs hover:text-signal"
              >
                <StateDot state={byId.get(id)?.state ?? "locked"} />
                {byId.get(id)?.technicalName ?? id}
              </button>
            ))}
          </Panel>
        </aside>
      </div>
    </div>
  );
}
