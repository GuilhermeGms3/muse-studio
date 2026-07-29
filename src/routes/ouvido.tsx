import { createFileRoute } from "@tanstack/react-router";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { Play, RotateCcw } from "lucide-react";
import { useRef, useState } from "react";
import { Panel, Row, Meter } from "@/components/workspace/Panel";
import { QueryState } from "@/components/workspace/QueryState";
import { Breadcrumb } from "@/components/workspace/TabBar";
import { recordEarAttempt, useEarStats } from "@/lib/music-api";
import { cn } from "@/lib/utils";
import { PracticeRecorder } from "@/components/music/PracticeRecorder";

export const Route = createFileRoute("/ouvido")({
  head: () => ({ meta: [{ title: "Treino de Ouvido - Music OS" }] }),
  component: EarPage,
});

type Drill = {
  id: string;
  name: string;
  context?: string;
  options: { label: string; semitones?: number[]; rhythm?: number[]; example?: string }[];
};

const drills: Drill[] = [
  {
    id: "intervals",
    name: "Intervalos",
    context: "Ouça como o início de uma frase, não como duas notas isoladas.",
    options: [
      { label: "2m", semitones: [0, 1] },
      { label: "2M", semitones: [0, 2] },
      { label: "3m", semitones: [0, 3] },
      { label: "3M", semitones: [0, 4] },
      { label: "4J", semitones: [0, 5] },
      { label: "5J", semitones: [0, 7] },
      { label: "6M", semitones: [0, 9] },
      { label: "8J", semitones: [0, 12] },
    ],
  },
  {
    id: "chords",
    name: "Acordes",
    options: [
      { label: "maior", semitones: [0, 4, 7] },
      { label: "menor", semitones: [0, 3, 7] },
      { label: "diminuto", semitones: [0, 3, 6] },
      { label: "aumentado", semitones: [0, 4, 8] },
      { label: "maj7", semitones: [0, 4, 7, 11] },
      { label: "m7", semitones: [0, 3, 7, 10] },
    ],
  },
  {
    id: "rhythms",
    name: "Ritmos",
    options: [
      { label: "semínimas", rhythm: [1, 1, 1, 1] },
      { label: "colcheias", rhythm: [0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5] },
      { label: "tercinas", rhythm: [0.333, 0.333, 0.334, 0.333, 0.333, 0.334] },
      { label: "síncope", rhythm: [0.5, 1, 0.5, 1, 1] },
    ],
  },
  {
    id: "progressions",
    name: "Progressões",
    context: "Reconheça o caminho harmônico usado em refrões e riffs conhecidos.",
    options: [
      { label: "I-V-vi-IV", semitones: [0, 7, 9, 5] },
      { label: "I-IV-V", semitones: [0, 5, 7] },
      { label: "ii-V-I", semitones: [2, 7, 0] },
      { label: "vi-IV-I-V", semitones: [9, 5, 0, 7] },
    ],
  },
];

function tone(context: AudioContext, frequency: number, start: number, duration: number) {
  const oscillator = context.createOscillator();
  const gain = context.createGain();
  oscillator.type = "triangle";
  oscillator.frequency.value = frequency;
  gain.gain.setValueAtTime(0.0001, start);
  gain.gain.exponentialRampToValueAtTime(0.14, start + 0.015);
  gain.gain.exponentialRampToValueAtTime(0.0001, start + duration);
  oscillator.connect(gain).connect(context.destination);
  oscillator.start(start);
  oscillator.stop(start + duration + 0.02);
}

function playStimulus(option: Drill["options"][number]) {
  const context = new AudioContext();
  const start = context.currentTime + 0.05;
  if (option.rhythm) {
    let cursor = start;
    option.rhythm.forEach((duration) => {
      tone(context, 880, cursor, 0.08);
      cursor += duration * 0.5;
    });
    return;
  }
  const notes = option.semitones ?? [0];
  notes.forEach((offset, index) => {
    const simultaneous =
      notes.length > 2 && !["I-V-vi-IV", "I-IV-V", "ii-V-I", "vi-IV-I-V"].includes(option.label);
    tone(context, 261.63 * 2 ** (offset / 12), start + (simultaneous ? 0 : index * 0.55), 0.48);
  });
}

function EarPage() {
  const statsQuery = useEarStats();
  const queryClient = useQueryClient();
  const [drill, setDrill] = useState(drills[0]);
  const [target, setTarget] = useState(drills[0].options[0]);
  const [answer, setAnswer] = useState<string | null>(null);
  const startedAt = useRef(Date.now());
  const moduleStats = statsQuery.data?.modules.find((item) => item.module === drill.id);
  const difficulty = moduleStats?.recommendedDifficulty ?? 1;
  const attempt = useMutation({
    mutationFn: (selected: string) =>
      recordEarAttempt({
        module: drill.id,
        prompt: target.label,
        answer: selected,
        correct: selected === target.label,
        responseMillis: Date.now() - startedAt.current,
        difficulty,
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["ear-stats"] });
      queryClient.invalidateQueries({ queryKey: ["skills"] });
    },
  });

  if (!statsQuery.data) return <QueryState error={statsQuery.error} />;

  const next = () => {
    const visible = drill.options.slice(0, Math.min(drill.options.length, 3 + difficulty));
    const focus = moduleStats?.focusPrompt
      ? visible.find((option) => option.label === moduleStats.focusPrompt)
      : undefined;
    const selected =
      focus && Math.random() < 0.55 ? focus : visible[Math.floor(Math.random() * visible.length)];
    setTarget(selected);
    setAnswer(null);
    startedAt.current = Date.now();
    playStimulus(selected);
  };

  const answerQuestion = (value: string) => {
    if (answer) return;
    setAnswer(value);
    attempt.mutate(value);
  };

  return (
    <div className="flex h-full min-h-0 flex-col">
      <Breadcrumb trail={["Praticar", "Treino de Ouvido"]} />
      <div className="grid min-h-0 flex-1 grid-cols-1 gap-px overflow-auto bg-border lg:grid-cols-[210px_1fr_250px]">
        <Panel title="Módulos" bodyClassName="p-0">
          {drills.map((item) => {
            const result = statsQuery.data.modules.find((stat) => stat.module === item.id);
            return (
              <button
                key={item.id}
                onClick={() => {
                  setDrill(item);
                  setTarget(item.options[0]);
                  setAnswer(null);
                }}
                className={cn(
                  "block w-full border-b border-border/60 px-2 py-2 text-left text-xs",
                  drill.id === item.id
                    ? "bg-surface"
                    : "text-muted-foreground hover:text-foreground",
                )}
              >
                <div className="flex justify-between">
                  <span>{item.name}</span>
                  <span className="num">{result?.accuracy ?? 0}%</span>
                </div>
                <Meter
                  value={result?.accuracy ?? 0}
                  tone={(result?.accuracy ?? 0) >= 80 ? "ok" : "info"}
                />
              </button>
            );
          })}
        </Panel>

        <Panel title={`Exercício · ${drill.name}`}>
          <div className="mx-auto max-w-xl py-8 text-center">
            <span className="label-tech">Dificuldade adaptativa · {difficulty}/5</span>
            <button
              onClick={() => playStimulus(target)}
              className="mx-auto mt-4 flex size-20 items-center justify-center rounded-full border border-border bg-surface hover:border-signal"
              title="Tocar novamente"
            >
              <Play className="size-7 text-signal" />
            </button>
            <p className="mt-3 text-xs text-muted-foreground">
              Ouça, cante mentalmente e escolha uma resposta.
            </p>
            {drill.context && (
              <p className="mx-auto mt-2 max-w-md border-l border-signal pl-2 text-left text-2xs text-muted-foreground">
                {drill.context}
              </p>
            )}
            <div className="mt-6 grid grid-cols-2 gap-1 md:grid-cols-3">
              {drill.options
                .slice(0, Math.min(drill.options.length, 3 + difficulty))
                .map((option) => (
                  <button
                    key={option.label}
                    onClick={() => answerQuestion(option.label)}
                    className={cn(
                      "h-10 border border-border bg-surface text-xs hover:border-border-strong",
                      answer === option.label &&
                        (option.label === target.label
                          ? "border-ok text-ok"
                          : "border-destructive text-destructive"),
                      answer && option.label === target.label && "border-ok text-ok",
                    )}
                  >
                    {option.label}
                  </button>
                ))}
            </div>
            {answer && (
              <div className="mt-4">
                <p className={answer === target.label ? "text-xs text-ok" : "text-xs text-warn"}>
                  {answer === target.label ? "Correto." : `Era ${target.label}.`}
                </p>
                <button
                  onClick={next}
                  className="mt-3 inline-flex items-center gap-2 border border-border bg-surface px-3 py-2 text-xs hover:border-signal"
                >
                  <RotateCcw className="size-3" />
                  Próximo
                </button>
              </div>
            )}
            {!answer && (
              <button
                onClick={next}
                className="mt-5 text-2xs text-muted-foreground hover:text-signal"
              >
                Novo estímulo
              </button>
            )}
            <div className="mt-7 text-left">
              <PracticeRecorder
                contextType="ear-training"
                contextId={`${drill.id}-singing`}
                targetNote={drill.id === "chords" ? "E4" : "C4"}
              />
            </div>
          </div>
        </Panel>

        <Panel title="Desempenho">
          <Row label="Tentativas" value={statsQuery.data.totalAttempts} />
          <Row label="Precisão geral" value={`${statsQuery.data.accuracy}%`} />
          <Row label="Precisão neste módulo" value={`${moduleStats?.accuracy ?? 0}%`} />
          <Row
            label="Resposta média"
            value={`${Math.round((moduleStats?.averageResponseMillis ?? 0) / 100) / 10}s`}
          />
          <p className="mt-4 border-l border-signal pl-2 text-2xs leading-relaxed text-muted-foreground">
            A dificuldade sobe após cinco tentativas com pelo menos 80% de acerto e recua quando a
            precisão cai.
          </p>
        </Panel>
      </div>
    </div>
  );
}
