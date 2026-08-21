import { Link } from "@tanstack/react-router";
import { ArrowRight, AudioLines, Dumbbell, Music2, Play } from "lucide-react";
import { OpenStudioButton } from "@/features/studio/OpenStudioButton";
import { useExercises } from "@/shared/api/exercises";
import { useHomeData } from "@/shared/api/home";
import { QueryState } from "@/shared/ui/query/QueryState";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";

export function PracticeWorkspace() {
  const { instrument } = useWorkspace();
  const home = useHomeData(instrument);
  const exercises = useExercises(instrument);

  if (!home.data || !exercises.data) {
    return <QueryState error={home.error ?? exercises.error} />;
  }

  const activeMission =
    home.data.learningExperience?.status !== "COMPLETED" ? home.data.learningExperience : undefined;
  const coachMission = home.data.coach?.recommendations.find(
    (item) => item.missionId && item.kind !== "REVIEW" && item.kind !== "REVALIDATION",
  );
  const missionId = activeMission?.missionId ?? coachMission?.missionId;

  return (
    <main className="mx-auto w-full max-w-6xl px-4 py-6 md:px-8 md:py-8">
      <header className="border-b border-border pb-5">
        <span className="label-tech">Prática</span>
        <h1 className="mt-1 text-2xl font-medium tracking-tight md:text-3xl">
          Toque com um objetivo claro
        </h1>
        <p className="mt-2 max-w-2xl text-sm text-text-muted">
          Continue a orientação do Coach, escolha uma tarefa executável ou abra o Studio para
          trabalhar com metrônomo, acompanhamento, loop e takes.
        </p>
      </header>

      <section className="mt-5 grid gap-px bg-border md:grid-cols-2">
        <div className="bg-background-workspace p-5">
          <Play className="size-5 text-signal" aria-hidden="true" />
          <span className="label-tech mt-4 block">Prática orientada</span>
          <h2 className="mt-2 text-lg font-medium">
            {activeMission ? "Continuar a missão em andamento" : "Seguir a recomendação do Coach"}
          </h2>
          <p className="mt-2 min-h-10 text-sm text-text-muted">
            {coachMission?.observableObjective ??
              "A Jornada reúne explicação, exercício, aplicação e avaliação quando há evidência adequada."}
          </p>
          {missionId ? (
            <Link
              to="/missoes/$missionId"
              params={{ missionId }}
              className="mt-5 inline-flex min-h-11 items-center gap-2 bg-signal px-4 text-sm font-semibold text-signal-foreground"
            >
              {activeMission ? "Continuar missão" : "Começar missão"}
              <ArrowRight className="size-4" />
            </Link>
          ) : (
            <Link
              to="/jornada"
              className="mt-5 inline-flex min-h-11 items-center gap-2 border border-signal px-4 text-sm text-signal"
            >
              Abrir Jornada <ArrowRight className="size-4" />
            </Link>
          )}
        </div>

        <div className="bg-background-workspace p-5">
          <AudioLines className="size-5 text-signal" aria-hidden="true" />
          <span className="label-tech mt-4 block">Studio</span>
          <h2 className="mt-2 text-lg font-medium">Praticar, gravar e comparar takes</h2>
          <p className="mt-2 min-h-10 text-sm text-text-muted">
            Use o motor Web ou conecte um projeto REAPER sem transformar uma gravação em prova de
            domínio.
          </p>
          <div className="mt-5 flex flex-wrap gap-2">
            <OpenStudioButton instrument={instrument} sourceKind="FREE" bpm={80} />
            <Link
              to="/studio"
              className="inline-flex h-8 items-center border border-border px-3 text-xs hover:border-signal"
            >
              Studios salvos
            </Link>
          </div>
        </div>
      </section>

      <section className="mt-7" aria-labelledby="practice-exercises">
        <div className="flex items-center justify-between gap-3 border-b border-border pb-3">
          <div>
            <span className="label-tech">Tarefas executáveis</span>
            <h2 id="practice-exercises" className="mt-1 text-lg font-medium">
              Exercícios do instrumento
            </h2>
          </div>
          <Link to="/exercicios" className="text-xs text-signal hover:underline">
            Ver todos
          </Link>
        </div>
        {exercises.data.length ? (
          <ol className="divide-y divide-border border-b border-border">
            {exercises.data.slice(0, 6).map((exercise) => (
              <li key={exercise.id}>
                <Link
                  to="/exercicios/$exerciseId"
                  params={{ exerciseId: exercise.id }}
                  className="group grid min-h-16 grid-cols-[24px_minmax(0,1fr)_auto] items-center gap-3 py-3"
                >
                  <Dumbbell className="size-4 text-text-muted group-hover:text-signal" />
                  <span className="min-w-0">
                    <span className="block truncate text-sm">{exercise.name}</span>
                    <span className="block truncate text-xs text-text-muted">
                      {exercise.observableObjective ?? exercise.description}
                    </span>
                  </span>
                  <span className="num text-2xs text-text-muted">
                    {exercise.minBpm}–{exercise.targetBpm} BPM
                  </span>
                </Link>
              </li>
            ))}
          </ol>
        ) : (
          <div className="border-b border-border py-10 text-center">
            <Music2 className="mx-auto size-5 text-text-muted" />
            <p className="mt-3 text-sm">Ainda não há exercícios para este instrumento.</p>
            <p className="mt-1 text-xs text-text-muted">
              A Jornada e o Studio continuam disponíveis sem inventar tarefas.
            </p>
          </div>
        )}
      </section>
    </main>
  );
}
