import { useMemo, useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { Check, CircleAlert, Pause, Play, Target } from "lucide-react";
import { PracticeRecorder } from "@/features/practice/recording/PracticeRecorder";
import { useMetronomeEngine } from "@/lib/use-metronome";
import { recordExerciseAttempt } from "@/shared/api/exercises";
import {
  completeMissionExperience,
  startMissionExperience,
  updateMissionExperience,
} from "@/shared/api/missions";
import type {
  AssessmentCriterionResult,
  Exercise,
  InstrumentId,
  MissionExperience,
  MissionWorkspaceData,
} from "@/shared/api/contracts";
import { AudioCuePlayer } from "@/shared/music/AudioCuePlayer";
import { InteractiveScore } from "@/shared/music/InteractiveScore";
import { InteractiveTab } from "@/shared/music/InteractiveTab";
import { completionMessage } from "./experience-model";
import {
  isApplicationActivity,
  learningActivityLabel,
  missionRequiresRecording,
} from "@/features/product-model";

const criterionQuestion = (key: string) => `Como foi ${key.replaceAll("-", " ")} nesta aplicação?`;

export function DeepLearningExperience({
  data,
  instrument,
}: {
  data: MissionWorkspaceData;
  instrument: InstrumentId;
}) {
  const queryClient = useQueryClient();
  const [experience, setExperience] = useState<MissionExperience | undefined>(data.experience);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string>();
  const [recordingId, setRecordingId] = useState(data.experience?.lastRecordingId);
  const [challengeLevel, setChallengeLevel] = useState<number | "">("");
  const completed = experience?.status === "COMPLETED";
  const assessment = data.assessments[0];
  const priorReviewEvidence =
    data.mission.id.includes("-review") || ["REVIEW", "RETENTION"].includes(assessment?.type ?? "")
      ? data.evidence[0]
      : undefined;
  const practiceExercises = data.exercises.filter(
    (exercise) => !isApplicationActivity(exercise.activityType),
  );
  const applicationExercises = data.exercises.filter((exercise) =>
    isApplicationActivity(exercise.activityType),
  );
  const requiresRecording = missionRequiresRecording(
    data.exercises.map((exercise) => exercise.activityType),
    data.mission.expectedEvidence,
  );
  const attempted = useMemo(
    () =>
      new Set(
        data.feedback
          .filter((item) => item.missionExperienceId === experience?.id)
          .map((item) => item.exerciseId),
      ),
    [data.feedback, experience?.id],
  );
  const [localAttempts, setLocalAttempts] = useState(attempted);
  const [reflections, setReflections] = useState<
    Record<
      string,
      {
        result: AssessmentCriterionResult | "";
        observation: string;
      }
    >
  >(() =>
    Object.fromEntries(
      (assessment?.criterionKeys ?? []).map((key) => [key, { result: "", observation: "" }]),
    ),
  );

  const save = async (operation: () => Promise<MissionExperience>) => {
    setBusy(true);
    setError(undefined);
    try {
      const next = await operation();
      setExperience(next);
      await queryClient.invalidateQueries({ queryKey: ["mission", data.mission.id, instrument] });
      await queryClient.invalidateQueries({ queryKey: ["home", instrument] });
      return next;
    } catch (reason) {
      setError((reason as Error).message);
      throw reason;
    } finally {
      setBusy(false);
    }
  };

  const start = () =>
    save(() => startMissionExperience(data.mission.id, instrument)).catch(() => undefined);
  const statusMessage = completionMessage(completed, data.competencies, error);

  if (!experience) {
    return (
      <main className="h-full overflow-auto bg-background">
        <section className="mx-auto max-w-3xl px-5 py-10">
          <span className="label-tech">Experiência recomendada</span>
          <h1 className="mt-3 text-2xl font-semibold">{data.mission.title}</h1>
          <p className="mt-3 max-w-2xl text-sm leading-relaxed text-muted-foreground">
            {data.mission.context} {data.mission.motivation}
          </p>
          <div className="mt-6 border-l-2 border-signal bg-surface-card p-4">
            <strong className="text-sm">Ao final, você vai conseguir</strong>
            <p className="mt-1 text-xs text-muted-foreground">{data.mission.observableObjective}</p>
          </div>
          {error && <p className="mt-4 text-xs text-destructive">{error}</p>}
          <button
            onClick={start}
            disabled={busy}
            className="mt-6 inline-flex h-10 items-center gap-2 bg-signal px-5 text-xs font-semibold text-signal-foreground disabled:opacity-50"
          >
            <Play className="size-4" /> {busy ? "Preparando..." : "Começar experiência"}
          </button>
        </section>
      </main>
    );
  }

  return (
    <main className="h-full overflow-auto bg-background">
      <div className="mx-auto max-w-7xl px-4 py-6 md:px-8">
        <header className="grid gap-4 border-b border-border pb-5 lg:grid-cols-[minmax(0,1fr)_320px] lg:items-end">
          <div>
            <span className="label-tech">
              {experience.status === "PAUSED"
                ? "Retomar de onde parou"
                : "Experiência de aprendizagem"}
            </span>
            <h1 className="mt-2 text-xl font-semibold">{data.mission.title}</h1>
            <p className="mt-2 max-w-3xl text-sm leading-relaxed text-muted-foreground">
              {data.mission.observableObjective}
            </p>
          </div>
          <div className="lg:text-right">
            {experience.status !== "COMPLETED" && (
              <button
                disabled={busy}
                onClick={() =>
                  save(() =>
                    updateMissionExperience(data.mission.id, {
                      instrument,
                      activityKind: experience.currentActivityKind,
                      activityId: experience.currentActivityId,
                      recordingId,
                      pause: true,
                    }),
                  ).catch(() => undefined)
                }
                className="mt-3 inline-flex h-8 items-center gap-2 border border-border px-3 text-2xs text-muted-foreground"
              >
                <Pause className="size-3" /> Pausar e continuar depois
              </button>
            )}
          </div>
        </header>

        <nav
          aria-label="Fases da experiência"
          className="sticky top-0 z-10 -mx-4 mt-4 flex gap-1 overflow-x-auto border-y border-border bg-background/95 px-4 py-2 backdrop-blur md:-mx-8 md:px-8"
        >
          {[
            ["orientacao", "Orientação"],
            ["entendimento", "Entendimento"],
            ["pratica", "Experimentação e prática"],
            ["aplicacao", "Aplicação"],
            ["fechamento", "Fechamento"],
          ].map(([id, label]) => (
            <a
              key={id}
              href={`#${id}`}
              className="inline-flex min-h-9 shrink-0 items-center border border-border px-3 text-2xs text-text-muted hover:border-signal hover:text-signal"
            >
              {label}
            </a>
          ))}
        </nav>

        <section id="orientacao" className="scroll-mt-16 border-b border-border py-7">
          <span className="label-tech">Orientação</span>
          <h2 className="mt-2 text-lg font-semibold">O que observar nesta missão</h2>
          <p className="mt-2 text-sm leading-relaxed text-muted-foreground">
            {data.mission.context} {data.mission.motivation}
          </p>
          <p className="mt-4 border-l-2 border-signal pl-3 text-xs leading-relaxed">
            {data.mission.observableObjective}
          </p>
          {priorReviewEvidence && (
            <div className="mt-4 border border-border bg-surface-card p-3 text-xs">
              <span className="label-tech">Registro anterior disponível</span>
              <p className="mt-2 text-muted-foreground">{priorReviewEvidence.observation}</p>
              <p className="mt-1 text-2xs text-text-muted">
                Condição registrada: {priorReviewEvidence.conditions} · confiança{" "}
                {priorReviewEvidence.reliability.toLowerCase()}
              </p>
            </div>
          )}
        </section>

        <div id="entendimento" className="scroll-mt-16">
          {data.lessons.map((lesson) => (
            <section key={lesson.id} className="border-b border-border py-7">
              <span className="label-tech">Entenda e escute</span>
              <h2 className="mt-2 text-lg font-semibold">{lesson.title}</h2>
              <p className="mt-2 text-sm leading-relaxed text-muted-foreground">{lesson.content}</p>
              <div className="mt-5 divide-y divide-border border-y border-border">
                {lesson.material?.steps.map((step) => (
                  <article
                    key={step.title}
                    className="grid gap-5 py-5 lg:grid-cols-[minmax(220px,.65fr)_minmax(0,1.35fr)]"
                  >
                    <div>
                      <h3 className="text-sm font-medium">{step.title}</h3>
                      <p className="mt-2 text-xs leading-relaxed text-muted-foreground">
                        {step.explanation}
                      </p>
                      {step.musicalExample && (
                        <p className="mt-3 border-l-2 border-signal pl-3 text-xs">
                          {step.musicalExample}
                        </p>
                      )}
                    </div>
                    <div className="space-y-3">
                      {step.audioNotes && (
                        <AudioCuePlayer notes={step.audioNotes} label="Ouvir o movimento" />
                      )}
                      {step.notation && <InteractiveScore notes={step.notation} bpm={68} />}
                      {step.tablature && (
                        <InteractiveTab tablature={step.tablature} initialBpm={68} />
                      )}
                    </div>
                  </article>
                ))}
              </div>
            </section>
          ))}
          {data.lessons.length === 0 && (
            <section className="border-b border-border py-7">
              <span className="label-tech">Entendimento</span>
              <p className="mt-2 text-sm text-text-muted">
                Esta missão começa diretamente pela experimentação; não há material editorial
                separado.
              </p>
            </section>
          )}
        </div>

        <section id="pratica" className="scroll-mt-16 border-b border-border py-7">
          <span className="label-tech">Experimentação e prática</span>
          <h2 className="mt-2 text-lg font-semibold">Teste, ajuste e construa controle</h2>
          <p className="mt-2 text-xs text-muted-foreground">
            Use o pulso dentro de cada prática. Seu resultado fica associado a esta experiência.
          </p>
          <div className="mt-5 divide-y divide-border border-y border-border">
            {practiceExercises.map((exercise) => (
              <ExercisePractice
                key={exercise.id}
                exercise={exercise}
                experience={experience}
                instrument={instrument}
                done={localAttempts.has(exercise.id)}
                onDone={async () => {
                  setLocalAttempts((current) => new Set([...current, exercise.id]));
                  await save(() =>
                    updateMissionExperience(data.mission.id, {
                      instrument,
                      activityKind: "EXERCISE",
                      activityId: exercise.id,
                    }),
                  );
                }}
              />
            ))}
          </div>
          {practiceExercises.length === 0 && (
            <p className="mt-4 text-xs text-text-muted">
              A tarefa observável desta Mission já acontece em contexto musical e aparece na seção
              seguinte.
            </p>
          )}
        </section>

        <section id="aplicacao" className="scroll-mt-16 border-b border-border py-7">
          <span className="label-tech">Aplicação musical</span>
          <h2 className="mt-2 text-lg font-semibold">
            {applicationExercises.length > 0
              ? "Use a habilidade em um contexto musical"
              : "A prática já contém o contexto necessário"}
          </h2>
          {data.mission.musicalApplication && (
            <p className="mt-2 text-xs text-muted-foreground">{data.mission.musicalApplication}</p>
          )}
          {applicationExercises.length > 0 && (
            <div className="mt-5 divide-y divide-border border-y border-border">
              {applicationExercises.map((exercise) => (
                <ExercisePractice
                  key={exercise.id}
                  exercise={exercise}
                  experience={experience}
                  instrument={instrument}
                  done={localAttempts.has(exercise.id)}
                  onDone={async () => {
                    setLocalAttempts((current) => new Set([...current, exercise.id]));
                    await save(() =>
                      updateMissionExperience(data.mission.id, {
                        instrument,
                        activityKind: "APPLICATION",
                        activityId: exercise.id,
                      }),
                    );
                  }}
                />
              ))}
            </div>
          )}
          {data.repertoire.length > 0 && (
            <div className="mt-4 grid gap-px border border-border bg-border sm:grid-cols-2">
              {data.repertoire.map((song) => (
                <article key={song.id} className="bg-surface-card p-3">
                  <span className="label-tech">Trecho de repertório</span>
                  <h3 className="mt-1 text-sm font-medium">{song.title}</h3>
                  <p className="mt-1 text-2xs text-muted-foreground">
                    {song.artist} · {song.bpm} BPM · {song.musicalKey}
                  </p>
                  <p className="mt-2 text-xs text-muted-foreground">{song.notes}</p>
                </article>
              ))}
            </div>
          )}
          {requiresRecording ? (
            <div className="mt-4">
              <PracticeRecorder
                contextType="mission-experience"
                contextId={experience.id}
                targetBpm={data.exercises.at(-1)?.currentBpm}
                onSaved={(recording) => {
                  setRecordingId(recording.id);
                  save(() =>
                    updateMissionExperience(data.mission.id, {
                      instrument,
                      activityKind: "APPLICATION",
                      activityId: data.exercises.at(-1)?.id ?? data.mission.id,
                      recordingId: recording.id,
                    }),
                  ).catch(() => undefined);
                }}
              />
            </div>
          ) : (
            <p className="mt-4 border-l-2 border-border-strong pl-3 text-xs text-text-muted">
              Esta missão não exige uma gravação. As práticas e a reflexão final formam o registro
              desta experiência.
            </p>
          )}
        </section>

        <section id="fechamento" className="scroll-mt-16 py-7">
          <span className="label-tech">Fechamento</span>
          <h2 className="mt-2 text-lg font-semibold">O que você realmente observou?</h2>
          <p className="mt-2 text-xs text-muted-foreground">
            {assessment
              ? "Isto é uma autoavaliação: ajuda o Coach, mas não prova domínio sozinha."
              : "Esta Mission termina com registro de conclusão e desafio percebido; não produz Assessment formal."}
          </p>
          {assessment && (
            <div className="mt-4 grid gap-3 border border-border bg-surface-card p-4 text-xs sm:grid-cols-2">
              <div>
                <span className="label-tech">Protocolo</span>
                <p className="mt-1 text-muted-foreground">{assessment.instructions}</p>
              </div>
              <div>
                <span className="label-tech">Suporte permitido</span>
                <p className="mt-1 text-muted-foreground">{assessment.allowedSupport}</p>
              </div>
            </div>
          )}
          <div className="mt-5 divide-y divide-border border-y border-border">
            {(assessment?.criterionKeys ?? []).map((key) => (
              <label
                key={key}
                className="grid gap-3 py-4 text-xs lg:grid-cols-[minmax(220px,.6fr)_minmax(0,1fr)_minmax(0,1fr)] lg:items-start"
              >
                <span className="font-medium">{criterionQuestion(key)}</span>
                <select
                  value={reflections[key]?.result}
                  onChange={(event) =>
                    setReflections((current) => ({
                      ...current,
                      [key]: {
                        ...current[key],
                        result: event.target.value as AssessmentCriterionResult,
                      },
                    }))
                  }
                  className="h-10 w-full border border-border bg-surface px-2"
                >
                  <option value="">Escolha após observar</option>
                  <option value="SUPPORTS">Consegui de forma consistente</option>
                  <option value="INCONCLUSIVE">Ainda não consigo afirmar</option>
                  <option value="CHALLENGES">Este ponto precisa de trabalho</option>
                </select>
                <textarea
                  value={reflections[key]?.observation}
                  onChange={(event) =>
                    setReflections((current) => ({
                      ...current,
                      [key]: { ...current[key], observation: event.target.value },
                    }))
                  }
                  placeholder="Descreva um momento concreto da aplicação"
                  className="min-h-20 w-full border border-border bg-surface p-2"
                />
                {assessment?.rubricLevels.some((level) => level.criterionKey === key) && (
                  <div className="lg:col-start-2 lg:col-span-2 grid gap-2 sm:grid-cols-3">
                    {assessment.rubricLevels
                      .filter((level) => level.criterionKey === key)
                      .map((level) => (
                        <div key={level.band} className="border border-border p-2">
                          <strong className="text-2xs">{level.band.replaceAll("_", " ")}</strong>
                          <p className="mt-1 text-2xs text-muted-foreground">{level.description}</p>
                        </div>
                      ))}
                  </div>
                )}
              </label>
            ))}
          </div>
          <label className="mt-4 block max-w-sm text-xs text-text-muted">
            Desafio percebido nesta experiência
            <select
              value={challengeLevel}
              onChange={(event) =>
                setChallengeLevel(event.target.value === "" ? "" : Number(event.target.value))
              }
              className="mt-2 h-10 w-full border border-border bg-surface px-2 text-text-primary"
            >
              <option value="">Escolha de 1 a 5</option>
              {[1, 2, 3, 4, 5].map((value) => (
                <option key={value} value={value}>
                  {value}/5
                </option>
              ))}
            </select>
          </label>
          {error && (
            <p className="mt-4 flex items-center gap-2 text-xs text-destructive">
              <CircleAlert className="size-4" />
              {error}
            </p>
          )}
          {completed ? (
            <div className="mt-5 border-l-2 border-signal bg-surface-card p-4">
              <strong className="text-sm">{statusMessage.title}</strong>
              <p className="mt-1 text-xs text-muted-foreground">{statusMessage.detail}</p>
            </div>
          ) : (
            <button
              disabled={
                busy ||
                (requiresRecording && !recordingId) ||
                challengeLevel === "" ||
                localAttempts.size < data.exercises.length ||
                Object.values(reflections).some((item) => !item.result || !item.observation.trim())
              }
              onClick={() =>
                save(() =>
                  completeMissionExperience(data.mission.id, {
                    instrument,
                    observerType: "SELF",
                    challengeLevel: Number(challengeLevel),
                    recordingId,
                    observations: Object.entries(reflections).map(([criterionKey, value]) => ({
                      criterionKey,
                      result: value.result as AssessmentCriterionResult,
                      observation: value.observation,
                    })),
                  }),
                ).catch(() => undefined)
              }
              className="mt-5 inline-flex h-10 items-center gap-2 bg-signal px-5 text-xs font-semibold text-signal-foreground disabled:opacity-40"
            >
              <Check className="size-4" />{" "}
              {assessment ? "Concluir e preservar evidências" : "Concluir experiência"}
            </button>
          )}
        </section>
      </div>
    </main>
  );
}

function ExercisePractice({
  exercise,
  experience,
  done,
  onDone,
}: {
  exercise: Exercise;
  experience: MissionExperience;
  instrument: InstrumentId;
  done: boolean;
  onDone: () => Promise<void>;
}) {
  const [bpm, setBpm] = useState(exercise.currentBpm);
  const [playing, setPlaying] = useState(false);
  const [accuracy, setAccuracy] = useState<number | "">("");
  const [difficulty, setDifficulty] = useState<number | "">("");
  const [repetitions, setRepetitions] = useState<number | "">("");
  const [practicedMinutes, setPracticedMinutes] = useState<number | "">("");
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string>();
  useMetronomeEngine({ bpm, playing, beats: 4, subdivision: 1 });

  return (
    <article className="py-5">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div>
          <span className="label-tech">{learningActivityLabel(exercise.activityType)}</span>
          <h3 className="mt-1 text-sm font-medium">{exercise.name}</h3>
          <p className="mt-1 text-xs text-muted-foreground">
            {exercise.observableObjective ?? exercise.description}
          </p>
        </div>
        {done && (
          <span className="inline-flex items-center gap-1 text-2xs text-ok">
            <Check className="size-3" /> registrada
          </span>
        )}
      </div>
      <ol className="mt-3 list-decimal space-y-1 pl-4 text-xs text-muted-foreground">
        {exercise.instructions.map((instruction) => (
          <li key={instruction}>{instruction}</li>
        ))}
      </ol>
      <div className="mt-4 flex flex-wrap items-center gap-2 border-y border-border py-3">
        <button
          onClick={() => setPlaying((value) => !value)}
          className="inline-flex h-8 items-center gap-2 border border-border px-3 text-xs"
        >
          {playing ? <Pause className="size-3" /> : <Play className="size-3" />} Pulso
        </button>
        <label className="text-2xs text-muted-foreground">
          BPM configurado{" "}
          <input
            type="number"
            min={exercise.minBpm}
            max={exercise.targetBpm}
            value={bpm}
            onChange={(event) => setBpm(Number(event.target.value))}
            className="ml-1 h-8 w-16 border border-border bg-surface px-2"
          />
        </label>
        <label className="text-2xs text-muted-foreground">
          Precisão informada por você{" "}
          <input
            type="number"
            min="0"
            max="100"
            value={accuracy}
            placeholder="0–100"
            onChange={(event) =>
              setAccuracy(event.target.value === "" ? "" : Number(event.target.value))
            }
            className="ml-1 h-8 w-20 border border-border bg-surface px-2"
          />
        </label>
        <label className="text-2xs text-muted-foreground">
          Esforço percebido{" "}
          <select
            value={difficulty}
            onChange={(event) =>
              setDifficulty(event.target.value === "" ? "" : Number(event.target.value))
            }
            className="ml-1 h-8 border border-border bg-surface px-2"
          >
            <option value="">Escolha</option>
            {[1, 2, 3, 4, 5].map((value) => (
              <option key={value} value={value}>
                {value}/5
              </option>
            ))}
          </select>
        </label>
        <label className="text-2xs text-muted-foreground">
          Repetições informadas{" "}
          <input
            type="number"
            min="1"
            value={repetitions}
            onChange={(event) =>
              setRepetitions(event.target.value === "" ? "" : Number(event.target.value))
            }
            className="ml-1 h-8 w-16 border border-border bg-surface px-2"
          />
        </label>
        <label className="text-2xs text-muted-foreground">
          Minutos informados{" "}
          <input
            type="number"
            min="1"
            value={practicedMinutes}
            onChange={(event) =>
              setPracticedMinutes(event.target.value === "" ? "" : Number(event.target.value))
            }
            className="ml-1 h-8 w-16 border border-border bg-surface px-2"
          />
        </label>
      </div>
      <p className="mt-2 text-2xs text-text-muted">
        Estes valores são configurados ou autorrelatados; o Muse não mediu sua execução.
      </p>
      {exercise.practiceConditions && (
        <p className="mt-3 text-2xs text-muted-foreground">
          <Target className="mr-1 inline size-3" />
          {exercise.practiceConditions}
        </p>
      )}
      {error && <p className="mt-2 text-2xs text-destructive">{error}</p>}
      <button
        disabled={
          saving ||
          done ||
          accuracy === "" ||
          difficulty === "" ||
          repetitions === "" ||
          practicedMinutes === ""
        }
        onClick={async () => {
          setSaving(true);
          setError(undefined);
          try {
            await recordExerciseAttempt(exercise.id, {
              bpm,
              accuracy: Number(accuracy),
              durationSeconds: Number(practicedMinutes) * 60,
              repetitions: Number(repetitions),
              perceivedDifficulty: Number(difficulty),
              missionExperienceId: experience.id,
            });
            await onDone();
          } catch (reason) {
            setError((reason as Error).message);
          } finally {
            setSaving(false);
          }
        }}
        className="mt-3 h-9 border border-signal px-4 text-xs font-medium text-signal disabled:opacity-40"
      >
        {done ? "Prática registrada" : saving ? "Salvando..." : "Registrar esta prática"}
      </button>
    </article>
  );
}
