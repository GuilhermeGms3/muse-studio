import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { BookOpen, Check, ExternalLink, Gauge, Music2, Play } from "lucide-react";
import type { Exercise } from "@/lib/music-api";
import { recordExerciseAttempt, useExerciseHistory } from "@/lib/music-api";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";
import { PracticeRecorder } from "@/features/practice/recording/PracticeRecorder";
import { CatalogEditor } from "@/shared/catalog/CatalogEditor";
import { getExerciseRecommendations } from "@/shared/api/learning";
import { PracticeMediaPanel } from "@/features/practice-song/PracticeMediaPanel";
import { OpenStudioButton } from "@/features/studio/OpenStudioButton";

export function ExerciseRunner({ exercise }: { exercise: Exercise }) {
  const { setMetronome } = useWorkspace();
  const queryClient = useQueryClient();
  const [bpm, setBpm] = useState(exercise.currentBpm);
  const [accuracy, setAccuracy] = useState(85);
  const [repetitions, setRepetitions] = useState(exercise.passRepetitions);
  const [difficulty, setDifficulty] = useState(3);
  const history = useExerciseHistory(exercise.id);
  const media = useQuery({
    queryKey: ["exercise-video", exercise.videoQuery, exercise.instrument],
    queryFn: () =>
      getExerciseRecommendations(exercise.videoQuery || exercise.technique, exercise.instrument),
    staleTime: 30 * 60 * 1000,
  });
  const attempt = useMutation({
    mutationFn: () =>
      recordExerciseAttempt(exercise.id, {
        bpm,
        accuracy,
        repetitions,
        perceivedDifficulty: difficulty,
        durationSeconds: exercise.minutes * 60,
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["exercises"] });
      queryClient.invalidateQueries({ queryKey: ["skills"] });
      queryClient.invalidateQueries({ queryKey: ["plan"] });
      queryClient.invalidateQueries({ queryKey: ["exercise-attempts", exercise.id] });
    },
  });

  return (
    <main className="h-full min-h-0 overflow-auto bg-panel">
      <div className="flex items-center justify-between border-b border-border bg-surface px-3 py-2">
        <span className="label-tech">Prática guiada</span>
        <CatalogEditor kind="exercise" instrument={exercise.instrument} initial={exercise} />
      </div>
      <div className="mx-auto max-w-5xl space-y-4 p-5">
        <div>
          <h2 className="text-sm font-semibold">{exercise.name}</h2>
          <p className="mt-1 text-2xs leading-relaxed text-muted-foreground">
            {exercise.description}
          </p>
        </div>
        <div className="flex flex-wrap gap-1.5">
          <span className="border border-border px-2 py-1 text-2xs text-signal">
            {exercise.stage.replaceAll("_", " ")}
          </span>
          <span className="border border-border px-2 py-1 text-2xs text-muted-foreground">
            {exercise.activityType}
          </span>
        </div>
        {["record", "transfer", "context", "song", "execute"].includes(exercise.activityType) && (
          <OpenStudioButton
            instrument={exercise.instrument}
            sourceKind="PRACTICE"
            sourceId={`exercise:${exercise.id}`}
            exerciseId={exercise.id}
            bpm={bpm}
          />
        )}
        {media.data && <PracticeMediaPanel title="Vídeo de referência" items={media.data} />}
        {exercise.readingUrl && (
          <a
            href={exercise.readingUrl}
            target="_blank"
            rel="noreferrer"
            className="block border border-border bg-surface p-3 hover:border-signal"
          >
            <span className="flex items-center gap-2 text-xs font-medium">
              <BookOpen className="size-4 text-signal" />
              {exercise.readingTitle}
              <ExternalLink className="ml-auto size-3 text-muted-foreground" />
            </span>
            <span className="mt-1 block text-2xs leading-relaxed text-muted-foreground">
              {exercise.readingNote}
            </span>
          </a>
        )}
        {exercise.practiceSongQuery && (
          <a
            href={`/treino-musica?q=${encodeURIComponent(exercise.practiceSongQuery)}`}
            className="flex items-center gap-2 border border-border bg-surface p-3 text-xs hover:border-signal"
          >
            <Music2 className="size-4 text-signal" />
            Aplicar em uma música
          </a>
        )}
        <div>
          <span className="label-tech">Como fazer</span>
          <ol className="mt-2 space-y-2">
            {exercise.instructions.map((instruction, index) => (
              <li key={instruction} className="flex gap-2 text-xs">
                <span className="num text-signal">{index + 1}</span>
                {instruction}
              </li>
            ))}
          </ol>
        </div>
        {(exercise.observableObjective ||
          exercise.practiceConditions ||
          exercise.successCriteria) && (
          <div className="grid gap-px bg-border md:grid-cols-3">
            <Definition label="Objetivo observável" value={exercise.observableObjective} />
            <Definition label="Condições" value={exercise.practiceConditions} />
            <Definition label="Critério" value={exercise.successCriteria} />
          </div>
        )}
        <div className="border-y border-border py-3">
          <label className="flex items-center justify-between text-xs">
            <span className="label-tech">BPM</span>
            <input
              type="number"
              min={exercise.minBpm}
              max={exercise.targetBpm}
              value={bpm}
              onChange={(event) => setBpm(Number(event.target.value))}
              className="num h-7 w-20 border border-border bg-surface px-2"
            />
          </label>
          <button
            onClick={() => setMetronome({ bpm, playing: true })}
            className="mt-2 flex h-8 w-full items-center justify-center gap-2 border border-border bg-surface text-xs hover:border-signal"
          >
            <Play className="size-3 text-signal" />
            Usar no metrônomo
          </button>
        </div>
        <details>
          <summary className="label-tech cursor-pointer">Variações</summary>
          <div className="mt-2 space-y-2">
            {exercise.variations.map((variation) => (
              <button
                key={variation.name}
                onClick={() => setBpm(Math.max(exercise.minBpm, bpm + variation.bpmOffset))}
                className="block w-full border-l border-border pl-2 text-left"
              >
                <span className="text-xs">{variation.name}</span>
                <span className="block text-2xs text-muted-foreground">
                  {variation.instructions}
                </span>
              </button>
            ))}
          </div>
        </details>
        <PracticeRecorder
          contextType="exercise"
          contextId={exercise.id}
          targetBpm={bpm}
          targetNote={exercise.technique.toLowerCase().includes("bend") ? "D5" : undefined}
        />
        <div className="space-y-3 border-t border-border pt-3">
          <label className="block text-xs">
            <span className="flex justify-between">
              <span>Precisão percebida</span>
              <span className="num">{accuracy}%</span>
            </span>
            <input
              type="range"
              min="40"
              max="100"
              value={accuracy}
              onChange={(e) => setAccuracy(Number(e.target.value))}
              className="w-full"
            />
          </label>
          <label className="flex items-center justify-between text-xs">
            <span>Repetições limpas</span>
            <input
              type="number"
              min="1"
              max="20"
              value={repetitions}
              onChange={(e) => setRepetitions(Number(e.target.value))}
              className="num h-7 w-16 border border-border bg-surface px-2"
            />
          </label>
          <label className="block text-xs">
            <span className="flex justify-between">
              <span>Dificuldade percebida</span>
              <span className="num">{difficulty}/5</span>
            </span>
            <input
              type="range"
              min="1"
              max="5"
              value={difficulty}
              onChange={(e) => setDifficulty(Number(e.target.value))}
              className="w-full"
            />
          </label>
          <p className="flex items-center gap-2 text-2xs text-muted-foreground">
            <Gauge className="size-3" />
            Aprovação: {exercise.passAccuracy}% · {exercise.passRepetitions} repetições
          </p>
          <button
            onClick={() => attempt.mutate()}
            disabled={attempt.isPending}
            className="flex h-9 w-full items-center justify-center gap-2 bg-signal text-xs font-medium text-signal-foreground"
          >
            <Check className="size-4" />
            Registrar tentativa
          </button>
          {attempt.data && (
            <p className={attempt.data.passed ? "text-xs text-ok" : "text-xs text-warn"}>
              {attempt.data.passed
                ? "Critério atingido. O próximo BPM foi preparado."
                : "Registrado. Repita sem aumentar o BPM."}
            </p>
          )}
        </div>
        <section className="border-t border-border pt-3">
          <span className="label-tech">Feedback anterior</span>
          {history.data?.length ? (
            <div className="mt-2 divide-y divide-border border-y border-border">
              {history.data.slice(0, 5).map((item) => (
                <div key={item.id} className="flex items-center justify-between gap-3 py-2 text-xs">
                  <span>{item.passed ? "Critério observado" : "Nova tentativa recomendada"}</span>
                  <span className="num text-muted-foreground">
                    {item.bpm} BPM · {item.repetitions} rep.
                  </span>
                </div>
              ))}
            </div>
          ) : (
            <p className="mt-2 text-xs text-muted-foreground">
              Nenhuma tentativa registrada neste exercício.
            </p>
          )}
        </section>
      </div>
    </main>
  );
}

function Definition({ label, value }: { label: string; value?: string | null }) {
  return (
    <div className="bg-surface p-3">
      <span className="label-tech">{label}</span>
      <p className="mt-1 text-xs leading-relaxed text-muted-foreground">
        {value || "Ainda não especificado para este exercício."}
      </p>
    </div>
  );
}
