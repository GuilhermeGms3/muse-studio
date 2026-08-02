import { useQueryClient } from "@tanstack/react-query";
import { ChevronLeft, ChevronRight, Gauge, Square, TrendingUp } from "lucide-react";
import { useEffect, useRef, useState } from "react";
import { QueryState } from "@/shared/ui/query/QueryState";
import {
  finishPracticeSession,
  getSessionSummary,
  recordSessionActivity,
  startPracticeSession,
  updatePracticeSession,
  usePreferences,
  type PracticeSession,
  type SessionSummary,
} from "@/lib/music-api";
import { useWorkspace, formatClock } from "@/workspace/store/WorkspaceProvider";

function targetBpm(target?: string) {
  return Number(target?.match(/\d{2,3}/)?.[0] ?? 60);
}

export function SessionPage() {
  const { instrument, session, startSession, pauseSession, updateSession } = useWorkspace();
  const preferences = usePreferences();
  const queryClient = useQueryClient();
  const startedRef = useRef(false);
  const [remote, setRemote] = useState<PracticeSession | null>(null);
  const [loadError, setLoadError] = useState<Error | null>(null);
  const [index, setIndex] = useState(0);
  const [finished, setFinished] = useState(false);
  const [summary, setSummary] = useState<SessionSummary | null>(null);
  const [feedbackOpen, setFeedbackOpen] = useState<"next" | "finish" | null>(null);
  const [feedback, setFeedback] = useState<"easy" | "adequate" | "hard">("adequate");
  const [accuracy, setAccuracy] = useState(80);
  const [bpm, setBpm] = useState(60);
  const [activityStartedAt, setActivityStartedAt] = useState(0);
  const [savingFeedback, setSavingFeedback] = useState(false);

  useEffect(() => {
    if (startedRef.current || !preferences.data) return;
    startedRef.current = true;
    startPracticeSession(instrument, preferences.data.sessionMinutes)
      .then((created) => {
        setRemote(created);
        setBpm(targetBpm(created.activities[0]?.target));
        startSession({
          instrument,
          seconds: 0,
          notes: "",
          goal: created.activities[0]?.title ?? "Prática guiada",
        });
      })
      .catch((error: Error) => setLoadError(error));
  }, [instrument, preferences.data, startSession]);

  const plan = remote?.activities ?? [];
  const current = plan[index] ?? plan[0];
  const next = plan[index + 1];
  const totalSeconds = plan.reduce((sum, activity) => sum + activity.minutes * 60, 0);
  const activityElapsed = Math.max(0, session.seconds - activityStartedAt);
  const activityRemaining = Math.max(0, (current?.minutes ?? 0) * 60 - activityElapsed);
  const totalRemaining = Math.max(0, totalSeconds - session.seconds);

  useEffect(() => {
    if (!remote || finished || session.seconds === 0 || session.seconds % 15 !== 0) return;
    updatePracticeSession(remote.id, {
      elapsedSeconds: session.seconds,
      currentActivityIndex: index,
      notes: session.notes,
      status: session.running ? "active" : "paused",
    }).catch(() => undefined);
  }, [finished, index, remote, session.notes, session.running, session.seconds]);

  const move = (nextIndex: number) => {
    const safeIndex = Math.max(0, Math.min(plan.length - 1, nextIndex));
    setIndex(safeIndex);
    setActivityStartedAt(session.seconds);
    setFeedback("adequate");
    setAccuracy(80);
    setBpm(targetBpm(plan[safeIndex]?.target));
    if (remote) {
      updatePracticeSession(remote.id, {
        elapsedSeconds: session.seconds,
        currentActivityIndex: safeIndex,
        notes: session.notes,
        status: "active",
      }).catch(() => undefined);
    }
  };

  const finish = async () => {
    if (!remote) return;
    const result = await finishPracticeSession(remote.id, {
      elapsedSeconds: session.seconds,
      notes: session.notes,
    });
    setRemote(result);
    pauseSession();
    setFinished(true);
    setSummary(await getSessionSummary(remote.id));
    ["journal", "home", "skills", "plan"].forEach((key) => {
      queryClient.invalidateQueries({ queryKey: [key] });
    });
  };

  const submitFeedback = async () => {
    if (!remote || !current || !feedbackOpen) return;
    setSavingFeedback(true);
    try {
      await recordSessionActivity(remote.id, current.id, {
        feedback,
        bpm,
        accuracy,
        durationSeconds: Math.max(1, activityElapsed),
        timingOffsetMillis: 0,
      });
      if (feedbackOpen === "finish") await finish();
      else move(index + 1);
      setFeedbackOpen(null);
    } catch (error) {
      setLoadError(error as Error);
    } finally {
      setSavingFeedback(false);
    }
  };

  if (!remote) return <QueryState error={loadError ?? preferences.error} />;

  if (summary) {
    return (
      <main className="flex h-full min-h-0 flex-col bg-panel">
        <div className="flex h-10 shrink-0 items-center justify-between border-b border-border bg-rail px-3">
          <span className="label-tech">Sessão concluída</span>
          <span className="num text-2xs text-muted-foreground">
            {formatClock(summary.practicedSeconds)}
          </span>
        </div>
        <section className="mx-auto w-full max-w-3xl flex-1 overflow-auto p-5">
          <TrendingUp className="size-5 text-ok" />
          <h1 className="mt-2 text-xl font-semibold">
            Boa prática. Este é o ponto de partida de amanhã.
          </h1>
          <div className="mt-5 grid gap-px bg-border md:grid-cols-2">
            <div className="bg-surface p-4">
              <span className="label-tech">Precisão média</span>
              <p className="num mt-2 text-3xl">{summary.averageAccuracy}%</p>
            </div>
            <div className="bg-surface p-4">
              <span className="label-tech">Maior andamento</span>
              <p className="num mt-2 text-3xl">{summary.peakBpm || "—"} BPM</p>
            </div>
          </div>
          <div className="mt-5 grid gap-5 md:grid-cols-2">
            <SummaryList
              title="O que melhorou"
              tone="ok"
              values={summary.improvements}
              empty="A sessão criou uma nova referência de desempenho."
            />
            <SummaryList
              title="Onde houve dificuldade"
              tone="warn"
              values={summary.difficulties}
              empty="Nenhum bloqueio forte apareceu hoje."
            />
          </div>
          <section className="mt-6 border border-signal/50 bg-signal/5 p-4">
            <span className="label-tech">Uma recomendação</span>
            <p className="mt-2 text-sm">{summary.recommendation}</p>
          </section>
        </section>
      </main>
    );
  }

  return (
    <main className="flex h-full min-h-0 flex-col bg-background">
      <div className="flex h-10 shrink-0 items-center justify-between border-b border-border bg-rail px-3">
        <span className="label-tech">Modo foco</span>
        <span className="num text-2xs text-muted-foreground">
          Restante: {formatClock(totalRemaining)}
        </span>
      </div>
      <div className="min-h-0 flex-1 bg-panel">
        <section className="mx-auto flex h-full min-h-0 max-w-6xl flex-col p-5">
          <div className="flex items-start justify-between gap-4">
            <div>
              <span className="label-tech">Exercício atual</span>
              <h1 className="mt-2 text-2xl font-semibold">{current?.title ?? "Prática livre"}</h1>
              {current?.target && (
                <p className="mt-1 text-xs text-muted-foreground">{current.target}</p>
              )}
            </div>
            <div className="text-right">
              <span className="label-tech">Cronômetro</span>
              <p className="num mt-1 text-5xl leading-none">{formatClock(session.seconds)}</p>
            </div>
          </div>
          <div className="mt-7 grid gap-px bg-border md:grid-cols-2">
            <div className="bg-surface p-4">
              <span className="label-tech">Tempo restante</span>
              <p className="num mt-2 text-4xl leading-none">{formatClock(activityRemaining)}</p>
            </div>
            <div className="bg-surface p-4">
              <span className="label-tech">Próxima atividade</span>
              <p className="mt-2 text-base">{next?.title ?? "Encerrar sessão"}</p>
            </div>
          </div>
          <label className="label-tech mt-7 block">Notas</label>
          <textarea
            value={session.notes}
            onChange={(event) => updateSession({ notes: event.target.value })}
            placeholder="O que aconteceu nesta atividade?"
            className="mt-2 min-h-20 flex-1 resize-none border border-border bg-surface p-3 text-sm outline-none focus:border-ring"
          />

          {feedbackOpen && (
            <section className="mt-3 border border-border bg-rail p-3">
              <div className="flex items-center justify-between gap-3">
                <div>
                  <span className="label-tech">Como foi esta atividade?</span>
                  <p className="mt-1 text-xs text-muted-foreground">
                    Isso ajusta o próximo BPM e a revisão.
                  </p>
                </div>
                <Gauge className="size-4 text-signal" />
              </div>
              <div className="mt-3 grid grid-cols-3 gap-px bg-border">
                {(
                  [
                    ["easy", "Fácil"],
                    ["adequate", "Adequado"],
                    ["hard", "Difícil"],
                  ] as const
                ).map(([value, label]) => (
                  <button
                    key={value}
                    onClick={() => setFeedback(value)}
                    className={`h-9 text-xs ${feedback === value ? "bg-signal text-signal-foreground" : "bg-surface"}`}
                  >
                    {label}
                  </button>
                ))}
              </div>
              <div className="mt-3 grid gap-3 md:grid-cols-2">
                <label className="text-2xs text-muted-foreground">
                  BPM alcançado
                  <input
                    type="number"
                    min={30}
                    max={300}
                    value={bpm}
                    onChange={(event) => setBpm(Number(event.target.value))}
                    className="num mt-1 h-8 w-full border border-border bg-surface px-2 text-xs text-foreground"
                  />
                </label>
                <label className="text-2xs text-muted-foreground">
                  Precisão percebida · {accuracy}%
                  <input
                    type="range"
                    min={20}
                    max={100}
                    step={5}
                    value={accuracy}
                    onChange={(event) => setAccuracy(Number(event.target.value))}
                    className="mt-2 w-full accent-[var(--color-signal)]"
                  />
                </label>
              </div>
              <button
                disabled={savingFeedback}
                onClick={submitFeedback}
                className="mt-3 h-9 w-full border border-signal bg-signal text-xs font-semibold text-signal-foreground disabled:opacity-40"
              >
                {savingFeedback
                  ? "Ajustando..."
                  : feedbackOpen === "finish"
                    ? "Registrar e finalizar"
                    : "Registrar e continuar"}
              </button>
            </section>
          )}

          <div className="mt-4 grid grid-cols-3 gap-px bg-border p-px">
            <button
              onClick={() => move(index - 1)}
              disabled={index === 0 || Boolean(feedbackOpen)}
              className="flex h-10 items-center justify-center gap-1 bg-surface text-xs disabled:opacity-40"
            >
              <ChevronLeft className="size-4" />
              Anterior
            </button>
            <button
              onClick={() => setFeedbackOpen("next")}
              disabled={index >= plan.length - 1 || Boolean(feedbackOpen)}
              className="flex h-10 items-center justify-center gap-1 bg-surface text-xs disabled:opacity-40"
            >
              Próximo
              <ChevronRight className="size-4" />
            </button>
            <button
              onClick={() => setFeedbackOpen("finish")}
              disabled={finished || Boolean(feedbackOpen)}
              className="flex h-10 items-center justify-center gap-1 bg-surface text-xs hover:text-signal disabled:opacity-40"
            >
              <Square className="size-3.5" />
              Finalizar
            </button>
          </div>
        </section>
      </div>
    </main>
  );
}

function SummaryList({
  title,
  tone,
  values,
  empty,
}: {
  title: string;
  tone: "ok" | "warn";
  values: string[];
  empty: string;
}) {
  return (
    <section>
      <span className="label-tech">{title}</span>
      <ul className="mt-2 space-y-2 text-xs">
        {values.length ? (
          values.map((item) => (
            <li
              key={item}
              className={`border-l pl-2 ${tone === "ok" ? "border-ok" : "border-warn"}`}
            >
              {item}
            </li>
          ))
        ) : (
          <li className="text-muted-foreground">{empty}</li>
        )}
      </ul>
    </section>
  );
}
