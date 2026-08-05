import { useEffect } from "react";
import { Link, useParams } from "@tanstack/react-router";
import { useQuery } from "@tanstack/react-query";
import {
  ArrowRight,
  BookOpen,
  CheckCircle2,
  CircleAlert,
  Clock3,
  Eye,
  FileCheck2,
  Lightbulb,
  Mic,
  PlaySquare,
} from "lucide-react";
import type { ReactNode } from "react";
import { getExerciseRecommendations } from "@/shared/api/learning";
import { useMission } from "@/shared/api/missions";
import { QueryState } from "@/shared/ui/query/QueryState";
import { PracticeRecorder } from "@/features/practice/recording/PracticeRecorder";
import { PracticeMediaPanel } from "@/features/practice-song/PracticeMediaPanel";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";

export function MissionWorkspace() {
  const { missionId } = useParams({ from: "/missoes/$missionId" });
  const { instrument, openTab } = useWorkspace();
  const missionQuery = useMission(missionId, instrument);
  const data = missionQuery.data;
  const videoSource = data?.exercises.find((exercise) => exercise.videoQuery);
  const video = useQuery({
    queryKey: ["mission-video", missionId, videoSource?.videoQuery, instrument],
    queryFn: () =>
      getExerciseRecommendations(
        videoSource?.videoQuery ?? videoSource?.technique ?? "",
        instrument,
      ),
    enabled: Boolean(videoSource),
    staleTime: 30 * 60 * 1000,
  });

  useEffect(() => {
    if (!data) return;
    openTab({
      path: `/missoes/${missionId}`,
      title: data.mission.title,
      type: "mission",
      objectId: missionId,
      context: "learning",
    });
  }, [data, missionId, openTab]);

  if (!data) return <QueryState error={missionQuery.error} />;

  const demonstrationSteps = data.lessons.flatMap((lesson) => lesson.material?.steps ?? []);

  return (
    <main className="h-full overflow-auto bg-background">
      <div className="mx-auto max-w-6xl p-5">
        <header className="border-b border-border pb-5">
          <div className="flex flex-wrap items-center gap-3 text-2xs text-muted-foreground">
            <span className="label-tech text-context-learn">Missão de aprendizagem</span>
            <span className="num flex items-center gap-1">
              <Clock3 className="size-3" /> {data.mission.estimatedMinutes} min
            </span>
            <span>{data.mission.stage.replaceAll("_", " ")}</span>
          </div>
          <h1 className="mt-2 text-2xl font-semibold">{data.mission.title}</h1>
          <p className="mt-2 max-w-3xl text-sm leading-relaxed text-muted-foreground">
            {data.mission.observableObjective}
          </p>
          <div className="mt-4 grid gap-px bg-border md:grid-cols-3">
            <Definition label="Contexto" value={data.mission.context} />
            <Definition label="Por que importa" value={data.mission.motivation} />
            <Definition label="Conclusão observável" value={data.mission.completionCriteria} />
          </div>
        </header>

        <div className="mt-5 grid items-start gap-4 lg:grid-cols-2">
          <Module icon={BookOpen} title="Teoria">
            {data.lessons.length ? (
              <div className="space-y-4">
                {data.lessons.map((lesson) => (
                  <article key={lesson.id}>
                    <h3 className="text-sm font-medium">{lesson.title}</h3>
                    <p className="mt-1 text-xs leading-relaxed text-muted-foreground">
                      {lesson.summary}
                    </p>
                    <div className="mt-3 space-y-2 text-xs leading-relaxed">
                      {lesson.content.split(/\n{2,}/).map((paragraph) => (
                        <p key={paragraph}>{paragraph}</p>
                      ))}
                    </div>
                  </article>
                ))}
              </div>
            ) : (
              <Unavailable text="Esta missão ainda não possui uma aula vinculada." />
            )}
          </Module>

          <Module icon={PlaySquare} title="Vídeo">
            {video.data?.length ? (
              <PracticeMediaPanel title="Referência em vídeo" items={video.data} />
            ) : (
              <Unavailable
                text={
                  videoSource
                    ? "A referência em vídeo está indisponível neste momento."
                    : "Nenhum vídeo foi vinculado a esta missão."
                }
              />
            )}
          </Module>

          <Module icon={Eye} title="Demonstração">
            {demonstrationSteps.length ||
            data.exercises.some((exercise) => exercise.instructions.length) ? (
              <ol className="space-y-3">
                {demonstrationSteps.map((step, index) => (
                  <li key={`${step.title}-${index}`} className="border-l border-context-learn pl-3">
                    <p className="text-xs font-medium">{step.title}</p>
                    <p className="mt-1 text-2xs leading-relaxed text-muted-foreground">
                      {step.explanation}
                    </p>
                    {step.tablature ? (
                      <pre className="num mt-2 overflow-auto border border-border bg-rail p-2 text-2xs">
                        {step.tablature}
                      </pre>
                    ) : null}
                  </li>
                ))}
                {data.exercises
                  .flatMap((exercise) => exercise.instructions)
                  .map((instruction, index) => (
                    <li key={`${instruction}-${index}`} className="flex gap-2 text-xs">
                      <span className="num text-signal">{index + 1}</span>
                      {instruction}
                    </li>
                  ))}
              </ol>
            ) : (
              <Unavailable text="Nenhuma demonstração foi vinculada a esta missão." />
            )}
          </Module>

          <Module icon={Lightbulb} title="Exemplos">
            {data.lessons.some((lesson) => lesson.examples.length) ? (
              <ul className="space-y-2 text-xs">
                {data.lessons
                  .flatMap((lesson) => lesson.examples)
                  .map((example) => (
                    <li key={example} className="border-l border-signal pl-3">
                      {example}
                    </li>
                  ))}
              </ul>
            ) : (
              <Unavailable text="Nenhum exemplo foi vinculado a esta missão." />
            )}
          </Module>

          <Module icon={PlaySquare} title="Exercícios" className="lg:col-span-2">
            {data.exercises.length ? (
              <div className="grid gap-px bg-border md:grid-cols-2">
                {data.exercises.map((exercise) => (
                  <Link
                    key={exercise.id}
                    to="/exercicios/$exerciseId"
                    params={{ exerciseId: exercise.id }}
                    className="group bg-surface p-3 outline-none hover:bg-surface-hover focus-visible:ring-1 focus-visible:ring-ring"
                  >
                    <div className="flex items-start justify-between gap-3">
                      <div>
                        <h3 className="text-xs font-medium group-hover:text-signal">
                          {exercise.name}
                        </h3>
                        <p className="mt-1 text-2xs text-muted-foreground">
                          {exercise.description}
                        </p>
                      </div>
                      <ArrowRight className="size-4 shrink-0 text-muted-foreground" />
                    </div>
                    <p className="num mt-3 text-2xs text-muted-foreground">
                      {exercise.minutes} min · {exercise.currentBpm}→{exercise.targetBpm} BPM
                    </p>
                  </Link>
                ))}
              </div>
            ) : (
              <Unavailable text="Esta missão ainda não possui exercícios vinculados." />
            )}
          </Module>

          <Module icon={Mic} title="Gravação">
            <PracticeRecorder contextType="mission" contextId={data.mission.id} />
          </Module>

          <Module icon={FileCheck2} title="Avaliação">
            {data.assessments.length ? (
              <div className="space-y-3">
                {data.assessments.map((assessment) => (
                  <article key={assessment.id} className="border border-border bg-rail p-3">
                    <div className="flex items-start justify-between gap-3">
                      <h3 className="text-xs font-medium">{assessment.title}</h3>
                      <span className="label-tech">{assessment.type}</span>
                    </div>
                    <p className="mt-2 text-xs leading-relaxed text-muted-foreground">
                      {assessment.purpose}
                    </p>
                    <p className="mt-2 text-xs">{assessment.instructions}</p>
                    <div className="mt-3 grid gap-2 text-2xs text-muted-foreground sm:grid-cols-2">
                      <p>
                        <strong className="text-foreground">Condições:</strong>{" "}
                        {assessment.conditions}
                      </p>
                      <p>
                        <strong className="text-foreground">Apoio permitido:</strong>{" "}
                        {assessment.allowedSupport}
                      </p>
                    </div>
                  </article>
                ))}
              </div>
            ) : (
              <Unavailable text="Nenhuma avaliação foi vinculada a esta missão." />
            )}
          </Module>

          <Module icon={CheckCircle2} title="Feedback">
            {data.feedback.length ? (
              <div className="divide-y divide-border border-y border-border">
                {data.feedback.map((item) => (
                  <div
                    key={item.id}
                    className="flex items-center justify-between gap-3 py-2 text-xs"
                  >
                    <span>{item.passed ? "Critério observado" : "Nova tentativa recomendada"}</span>
                    <span className="num text-muted-foreground">
                      {item.bpm} BPM · {item.repetitions} rep.
                    </span>
                  </div>
                ))}
              </div>
            ) : (
              <Unavailable text="O feedback aparecerá após uma tentativa registrada." />
            )}
          </Module>

          <Module icon={FileCheck2} title="Evidências">
            {data.evidence.length ? (
              <div className="space-y-3">
                {data.evidence.map((item) => (
                  <article key={item.id} className="border-l-2 border-context-review pl-3">
                    <div className="flex flex-wrap items-center gap-2 text-2xs text-muted-foreground">
                      <span className="label-tech">{item.state}</span>
                      <span>{item.reliability}</span>
                      <span className="num">{item.id}</span>
                    </div>
                    <p className="mt-1 text-xs">{item.observation}</p>
                    <p className="mt-1 text-2xs text-muted-foreground">{item.conditions}</p>
                  </article>
                ))}
              </div>
            ) : (
              <Unavailable text={`Evidência esperada: ${data.mission.expectedEvidence}`} />
            )}
          </Module>
        </div>
      </div>
    </main>
  );
}

function Module({
  icon: Icon,
  title,
  className = "",
  children,
}: {
  icon: typeof BookOpen;
  title: string;
  className?: string;
  children: ReactNode;
}) {
  return (
    <section className={`border border-border bg-surface-card ${className}`}>
      <header className="flex h-9 items-center gap-2 border-b border-border bg-panel px-3">
        <Icon className="size-3.5 text-context-learn" />
        <h2 className="label-tech text-foreground">{title}</h2>
      </header>
      <div className="p-4">{children}</div>
    </section>
  );
}

function Definition({ label, value }: { label: string; value: string }) {
  return (
    <div className="bg-surface p-3">
      <span className="label-tech">{label}</span>
      <p className="mt-1 text-xs leading-relaxed">{value}</p>
    </div>
  );
}

function Unavailable({ text }: { text: string }) {
  return (
    <p className="flex items-start gap-2 text-xs leading-relaxed text-muted-foreground">
      <CircleAlert className="mt-0.5 size-3.5 shrink-0 text-warn" />
      {text}
    </p>
  );
}
