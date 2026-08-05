import { useEffect, useState } from "react";
import type { ReactNode } from "react";
import { Link } from "@tanstack/react-router";
import { useQuery } from "@tanstack/react-query";
import {
  ArrowLeft,
  ArrowRight,
  BookOpen,
  Check,
  CircleAlert,
  Eye,
  FileCheck2,
  GraduationCap,
  Lightbulb,
  ListChecks,
  Mic,
  PlaySquare,
  RotateCcw,
  Sparkles,
  Target,
  TriangleAlert,
} from "lucide-react";
import type { LucideIcon } from "lucide-react";
import type { Exercise, InstrumentId, MissionWorkspaceData } from "@/shared/api/contracts";
import { getExerciseRecommendations } from "@/shared/api/learning";
import { PracticeRecorder } from "@/features/practice/recording/PracticeRecorder";
import { PracticeMediaPanel } from "@/features/practice-song/PracticeMediaPanel";
import { cn } from "@/shared/utils/cn";

type TeachingStep = {
  id: string;
  title: string;
  icon: LucideIcon;
};

const TEACHING_STEPS: TeachingStep[] = [
  { id: "introduction", title: "Introdução", icon: Sparkles },
  { id: "objectives", title: "Objetivos", icon: Target },
  { id: "capability", title: "Ao concluir", icon: GraduationCap },
  { id: "prerequisites", title: "Pré-requisitos", icon: ListChecks },
  { id: "explanation", title: "Explicação", icon: BookOpen },
  { id: "demonstration", title: "Demonstração", icon: Eye },
  { id: "examples", title: "Exemplos", icon: Lightbulb },
  { id: "mistakes", title: "Erros comuns", icon: TriangleAlert },
  { id: "guided", title: "Exercício guiado", icon: PlaySquare },
  { id: "assisted", title: "Prática assistida", icon: Target },
  { id: "recording", title: "Gravação", icon: Mic },
  { id: "feedback", title: "Feedback", icon: Check },
  { id: "assessment", title: "Assessment", icon: FileCheck2 },
  { id: "summary", title: "Resumo", icon: BookOpen },
  { id: "evidence", title: "Evidências", icon: FileCheck2 },
  { id: "next", title: "Próximos passos", icon: ArrowRight },
];

const progressKey = (missionId: string) => `muse:teaching-runner:v1:${missionId}`;

export function TeachingRunner({
  data,
  instrument,
}: {
  data: MissionWorkspaceData;
  instrument: InstrumentId;
}) {
  const [current, setCurrent] = useState(0);
  const step = TEACHING_STEPS[current];
  const videoSource = data.exercises.find((exercise) => exercise.videoQuery);
  const video = useQuery({
    queryKey: ["mission-demonstration", data.mission.id, videoSource?.videoQuery, instrument],
    queryFn: () =>
      getExerciseRecommendations(
        videoSource?.videoQuery ?? videoSource?.technique ?? "",
        instrument,
      ),
    enabled: step.id === "demonstration" && Boolean(videoSource),
    staleTime: 30 * 60 * 1000,
  });

  useEffect(() => {
    const saved = window.localStorage.getItem(progressKey(data.mission.id));
    const parsed = Number.parseInt(saved ?? "0", 10);
    if (Number.isInteger(parsed) && parsed >= 0 && parsed < TEACHING_STEPS.length) {
      setCurrent(parsed);
    }
  }, [data.mission.id]);

  const selectStep = (index: number) => {
    const bounded = Math.max(0, Math.min(TEACHING_STEPS.length - 1, index));
    setCurrent(bounded);
    window.localStorage.setItem(progressKey(data.mission.id), String(bounded));
  };

  const Icon = step.icon;
  const coach = coachContext(step.id, data);
  const coachEvidence =
    step.id === "introduction"
      ? data.coach.citedEvidence
      : step.id === "next"
        ? data.coach.nextRecommendations.flatMap((item) => item.evidence)
        : [];

  return (
    <main className="h-full min-h-0 overflow-auto bg-background">
      <div className="sticky top-0 z-10 border-b border-border bg-background/95 backdrop-blur">
        <div className="mx-auto max-w-6xl px-5 py-3">
          <div className="flex flex-wrap items-start justify-between gap-3">
            <div>
              <p className="label-tech text-context-learn">
                Teaching Runner · etapa {current + 1} de {TEACHING_STEPS.length}
              </p>
              <h1 className="mt-1 text-lg font-semibold">{data.mission.title}</h1>
            </div>
            <div className="num text-2xs text-muted-foreground">
              {data.mission.estimatedMinutes} min · {data.mission.stage.replaceAll("_", " ")}
            </div>
          </div>
          <ol className="mt-3 flex gap-1 overflow-x-auto pb-1" aria-label="Etapas da Mission">
            {TEACHING_STEPS.map((item, index) => (
              <li key={item.id} className="shrink-0">
                <button
                  type="button"
                  onClick={() => selectStep(index)}
                  aria-current={index === current ? "step" : undefined}
                  title={`${index + 1}. ${item.title}`}
                  className={cn(
                    "num flex h-7 min-w-7 items-center justify-center border px-2 text-2xs outline-none focus-visible:ring-1 focus-visible:ring-ring",
                    index === current
                      ? "border-context-learn bg-context-learn text-background"
                      : index < current
                        ? "border-context-learn/40 bg-context-learn/10 text-context-learn"
                        : "border-border bg-surface text-muted-foreground hover:border-border-strong",
                  )}
                >
                  {index < current ? <Check className="size-3" /> : index + 1}
                  <span className={cn("ml-1.5", index === current ? "inline" : "hidden xl:inline")}>
                    {item.title}
                  </span>
                </button>
              </li>
            ))}
          </ol>
        </div>
      </div>

      <div className="mx-auto max-w-5xl p-5">
        <section className="border border-context-learn/50 bg-context-learn/5">
          <header className="flex items-center gap-2 border-b border-context-learn/30 px-4 py-2">
            <GraduationCap className="size-4 text-context-learn" />
            <span className="label-tech text-context-learn">Coach · {coach.source}</span>
          </header>
          <p className="px-4 py-3 text-xs leading-relaxed">{coach.text}</p>
          {coachEvidence.length ? (
            <div className="grid gap-px border-t border-context-learn/30 bg-context-learn/20 sm:grid-cols-2">
              {coachEvidence.map((item) => (
                <div key={item.id} className="bg-background px-4 py-3 text-2xs">
                  <p className="num text-context-learn">{item.id}</p>
                  <p className="mt-1 leading-relaxed">{item.observation}</p>
                  <p className="mt-1 text-muted-foreground">
                    {item.reliability} · {item.conditions}
                  </p>
                </div>
              ))}
            </div>
          ) : null}
        </section>

        <section className="mt-4 min-h-[360px] border border-border bg-surface-card">
          <header className="flex items-center gap-3 border-b border-border bg-panel px-5 py-4">
            <span className="flex size-8 items-center justify-center border border-context-learn/40 bg-context-learn/10">
              <Icon className="size-4 text-context-learn" />
            </span>
            <div>
              <p className="label-tech">Etapa {current + 1}</p>
              <h2 className="text-base font-semibold">{step.title}</h2>
            </div>
          </header>
          <div className="p-5">
            <StepContent
              stepId={step.id}
              data={data}
              video={video.data}
              videoPending={video.isFetching}
            />
          </div>
        </section>

        <footer className="mt-4 flex items-center justify-between border-t border-border pt-4">
          <button
            type="button"
            disabled={current === 0}
            onClick={() => selectStep(current - 1)}
            className="inline-flex h-9 items-center gap-2 border border-border px-3 text-xs hover:border-border-strong disabled:cursor-not-allowed disabled:opacity-40"
          >
            <ArrowLeft className="size-3.5" /> Anterior
          </button>
          {current < TEACHING_STEPS.length - 1 ? (
            <button
              type="button"
              onClick={() => selectStep(current + 1)}
              className="inline-flex h-9 items-center gap-2 border border-context-learn bg-context-learn px-4 text-xs font-semibold text-background hover:brightness-110"
            >
              Continuar: {TEACHING_STEPS[current + 1].title}
              <ArrowRight className="size-3.5" />
            </button>
          ) : (
            <button
              type="button"
              onClick={() => selectStep(0)}
              className="inline-flex h-9 items-center gap-2 border border-border px-3 text-xs hover:border-border-strong"
            >
              <RotateCcw className="size-3.5" /> Rever a Mission
            </button>
          )}
        </footer>
      </div>
    </main>
  );
}

function StepContent({
  stepId,
  data,
  video,
  videoPending,
}: {
  stepId: string;
  data: MissionWorkspaceData;
  video?: Awaited<ReturnType<typeof getExerciseRecommendations>>;
  videoPending: boolean;
}) {
  const lessonExamples = data.lessons.flatMap((lesson) => lesson.examples);
  const lessonMistakes = data.lessons.flatMap((lesson) => lesson.material?.commonMistakes ?? []);
  const demonstration = data.lessons.flatMap((lesson) => lesson.material?.steps ?? []);

  switch (stepId) {
    case "introduction":
      return (
        <div className="space-y-5">
          <Lead>{data.mission.context}</Lead>
          <DefinitionGrid
            items={[
              ["Missão", data.mission.observableObjective],
              ["Por que importa", data.mission.motivation],
              ["Aplicação musical", data.mission.musicalApplication ?? "Ainda não declarada."],
            ]}
          />
        </div>
      );
    case "objectives": {
      const objectives = data.lessons.flatMap((lesson) => lesson.objectives);
      return (
        <NumberedList items={objectives.length ? objectives : [data.mission.observableObjective]} />
      );
    }
    case "capability":
      return (
        <DefinitionGrid
          items={[
            ["Você será capaz de", data.mission.observableObjective],
            ["Critério de conclusão", data.mission.completionCriteria],
            ["Em música real", data.mission.musicalApplication ?? "Aplicação ainda não declarada."],
          ]}
        />
      );
    case "prerequisites":
      return data.prerequisites.length ? (
        <div className="divide-y divide-border border-y border-border">
          {data.prerequisites.map((item) => (
            <div key={item.competencyId} className="grid gap-2 py-3 sm:grid-cols-[220px_1fr]">
              <div>
                <p className="text-xs font-medium">{item.title}</p>
                <p className="label-tech mt-1">{item.type.replaceAll("_", " ")}</p>
              </div>
              <div className="text-xs leading-relaxed text-muted-foreground">
                <p>{item.reason}</p>
                <p className={cn("mt-1", item.blocking ? "text-danger" : "text-context-learn")}>
                  {item.satisfied
                    ? "Evidência mínima disponível."
                    : item.blocking
                      ? "Bloqueio atual: colete evidência antes de exigir desempenho."
                      : "Lacuna não bloqueante; avance de forma guiada."}
                </p>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <EmptyTeachingState text="Esta Mission não possui pré-requisitos no caminho ativo." />
      );
    case "explanation":
      return (
        <div className="space-y-6">
          {data.lessons.map((lesson) => (
            <article key={lesson.id} className="max-w-3xl">
              <h3 className="text-sm font-semibold">{lesson.title}</h3>
              <p className="mt-2 text-xs leading-relaxed text-muted-foreground">{lesson.summary}</p>
              <div className="mt-4 space-y-3 text-sm leading-7">
                {lesson.content
                  .split(/\n{2,}/)
                  .filter(Boolean)
                  .map((paragraph) => (
                    <p key={paragraph}>{paragraph}</p>
                  ))}
              </div>
            </article>
          ))}
        </div>
      );
    case "demonstration":
      return (
        <div className="space-y-5">
          {demonstration.map((item, index) => (
            <article
              key={`${item.title}-${index}`}
              className="border-l-2 border-context-learn pl-4"
            >
              <p className="text-xs font-semibold">{item.title}</p>
              <p className="mt-1 text-xs leading-relaxed text-muted-foreground">
                {item.explanation}
              </p>
              {item.tablature ? (
                <pre className="num mt-3 overflow-auto border border-border bg-rail p-3 text-2xs">
                  {item.tablature}
                </pre>
              ) : null}
            </article>
          ))}
          {video?.length ? (
            <PracticeMediaPanel title="Referência demonstrada" items={video} />
          ) : videoPending ? (
            <p className="text-xs text-muted-foreground">Buscando demonstração vinculada…</p>
          ) : null}
          {!demonstration.length && !video?.length && !videoPending ? (
            <EmptyTeachingState text="Não há demonstração vinculada. Não avance exigindo imitação sem um modelo." />
          ) : null}
        </div>
      );
    case "examples":
      return lessonExamples.length ? (
        <NumberedList items={lessonExamples} />
      ) : (
        <EmptyTeachingState text="Nenhum exemplo editorial foi vinculado a esta Lesson." />
      );
    case "mistakes":
      return lessonMistakes.length ? (
        <div className="space-y-2">
          {lessonMistakes.map((mistake) => (
            <div key={mistake} className="flex gap-3 border-b border-border py-3 text-xs">
              <TriangleAlert className="mt-0.5 size-4 shrink-0 text-warn" />
              <span>{mistake}</span>
            </div>
          ))}
        </div>
      ) : (
        <EmptyTeachingState text="A Lesson ainda não declara erros comuns; não foram inventados pelo Runner." />
      );
    case "guided":
      return data.exercises[0] ? (
        <ExerciseTeachingCard exercise={data.exercises[0]} mode="guided" />
      ) : (
        <EmptyTeachingState text="Nenhum exercício guiado foi vinculado à Mission." />
      );
    case "assisted":
      return data.exercises[1] ? (
        <ExerciseTeachingCard exercise={data.exercises[1]} mode="assisted" />
      ) : data.exercises[0] ? (
        <ExerciseTeachingCard exercise={data.exercises[0]} mode="assisted" />
      ) : (
        <EmptyTeachingState text="Nenhuma prática assistida foi vinculada à Mission." />
      );
    case "recording":
      return (
        <div className="space-y-4">
          <Lead>{data.mission.expectedEvidence}</Lead>
          <PracticeRecorder contextType="mission" contextId={data.mission.id} />
        </div>
      );
    case "feedback":
      return data.feedback.length ? (
        <div className="divide-y divide-border border-y border-border">
          {data.feedback.map((item) => (
            <div key={item.id} className="grid gap-2 py-3 text-xs sm:grid-cols-[1fr_auto]">
              <div>
                <p className="font-medium">
                  {item.passed ? "Critério observado" : "Nova tentativa recomendada"}
                </p>
                <p className="mt-1 text-muted-foreground">
                  Precisão registrada: {item.accuracy} · dificuldade declarada: {item.difficulty}
                </p>
              </div>
              <span className="num text-muted-foreground">
                {item.bpm} BPM · {item.repetitions} rep.
              </span>
            </div>
          ))}
        </div>
      ) : (
        <div className="space-y-3">
          <EmptyTeachingState text="Ainda não existe tentativa observável para produzir feedback." />
          {data.competencies.map((item) => (
            <Definition key={item.competencyId} label={`Próxima observação · ${item.title}`}>
              {item.nextObservation}
            </Definition>
          ))}
        </div>
      );
    case "assessment":
      return data.assessments.length ? (
        <div className="space-y-4">
          {data.assessments.map((assessment) => (
            <article key={assessment.id} className="border border-border bg-rail p-4">
              <div className="flex flex-wrap items-start justify-between gap-3">
                <h3 className="text-sm font-semibold">{assessment.title}</h3>
                <span className="label-tech">{assessment.type}</span>
              </div>
              <p className="mt-2 text-xs text-muted-foreground">{assessment.purpose}</p>
              <p className="mt-4 text-sm leading-relaxed">{assessment.instructions}</p>
              <DefinitionGrid
                items={[
                  ["Condições", assessment.conditions],
                  ["Apoio permitido", assessment.allowedSupport],
                  ["Se for inconclusivo", assessment.inconclusiveRule],
                ]}
              />
            </article>
          ))}
        </div>
      ) : (
        <EmptyTeachingState text="A Mission não possui Assessment; ela não pode sustentar conclusão de domínio." />
      );
    case "summary":
      return (
        <div className="space-y-5">
          <Lead>{data.mission.observableObjective}</Lead>
          <DefinitionGrid
            items={[
              ["O que foi ensinado", data.lessons.map((lesson) => lesson.title).join("; ")],
              ["O que foi praticado", data.exercises.map((exercise) => exercise.name).join("; ")],
              ["O que precisa ser demonstrado", data.mission.completionCriteria],
            ]}
          />
        </div>
      );
    case "evidence":
      return data.evidence.length ? (
        <div className="space-y-3">
          {data.evidence.map((item) => (
            <article key={item.id} className="border-l-2 border-context-review pl-4">
              <div className="flex flex-wrap gap-2 text-2xs text-muted-foreground">
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
        <div className="space-y-3">
          <EmptyTeachingState text="Nenhuma evidência adquirida foi persistida para esta Mission." />
          {data.competencies.map((item) => (
            <Definition
              key={item.competencyId}
              label={`${item.title} · ${item.evidenceConfidence}`}
            >
              {item.nextObservation}
            </Definition>
          ))}
        </div>
      );
    case "next":
      return (
        <div className="space-y-4">
          <Lead>{data.coach.nextMessage}</Lead>
          {data.coach.nextRecommendations.map((recommendation) => (
            <Link
              key={`${recommendation.missionId}-${recommendation.competencyId}`}
              to="/missoes/$missionId"
              params={{ missionId: recommendation.missionId! }}
              className="block border border-border bg-rail p-4 hover:border-context-learn"
            >
              <p className="text-sm font-semibold">{recommendation.title}</p>
              <p className="mt-1 text-xs text-muted-foreground">
                {recommendation.observableObjective}
              </p>
              <p className="label-tech mt-3">{recommendation.kind.replaceAll("_", " ")}</p>
              {recommendation.evidence.length ? (
                <p className="num mt-2 text-2xs text-context-review">
                  Evidências citadas: {recommendation.evidence.map((item) => item.id).join(", ")}
                </p>
              ) : null}
            </Link>
          ))}
          {!data.coach.nextRecommendations.length ? (
            <EmptyTeachingState text="Nenhuma próxima Mission foi afirmada antes da coleta e avaliação das evidências atuais." />
          ) : null}
        </div>
      );
    default:
      return null;
  }
}

function ExerciseTeachingCard({
  exercise,
  mode,
}: {
  exercise: Exercise;
  mode: "guided" | "assisted";
}) {
  return (
    <article className="space-y-5">
      <div>
        <p className="label-tech text-context-practice">
          {mode === "guided" ? "Observe, tente e ajuste" : "Pratique com condições conhecidas"}
        </p>
        <h3 className="mt-1 text-base font-semibold">{exercise.name}</h3>
        <p className="mt-2 max-w-3xl text-xs leading-relaxed text-muted-foreground">
          {exercise.description}
        </p>
      </div>
      <DefinitionGrid
        items={[
          ["Por que este exercício existe", exercise.observableObjective ?? exercise.description],
          ["Condições", exercise.practiceConditions ?? "Condições não declaradas."],
          ["Critério", exercise.successCriteria ?? "Critério não declarado."],
        ]}
      />
      <NumberedList items={exercise.instructions} />
      {mode === "assisted" ? (
        <div className="grid gap-px bg-border sm:grid-cols-3">
          {exercise.variations.map((variation) => (
            <Definition key={variation.name} label={variation.name}>
              {variation.instructions}
            </Definition>
          ))}
        </div>
      ) : null}
      <Link
        to="/exercicios/$exerciseId"
        params={{ exerciseId: exercise.id }}
        className="inline-flex h-10 items-center gap-2 border border-context-practice bg-context-practice px-4 text-xs font-semibold text-background hover:brightness-110"
      >
        Abrir ambiente do exercício <ArrowRight className="size-3.5" />
      </Link>
    </article>
  );
}

function coachContext(stepId: string, data: MissionWorkspaceData) {
  const firstCompetency = data.competencies[0];
  const firstExercise = data.exercises[0];
  const assistedExercise = data.exercises[1] ?? firstExercise;
  const competencyNames = data.competencies.map((item) => item.title).join(", ");
  const evidenceGaps = data.competencies
    .map((item) => `${item.title}: ${item.nextObservation}`)
    .join(" ");
  switch (stepId) {
    case "introduction":
      return { source: "Coach", text: data.coach.whyMission };
    case "objectives":
    case "capability":
      return {
        source: "Curriculum Engine",
        text: firstCompetency?.curriculumReason ?? data.mission.observableObjective,
      };
    case "prerequisites":
      return {
        source: "Curriculum Engine",
        text: data.prerequisites.length
          ? data.prerequisites.map((item) => `${item.title}: ${item.reason}`).join(" ")
          : "Nenhum pré-requisito aparece no caminho ativo para esta competência.",
      };
    case "guided":
      return {
        source: "Definição do Exercise",
        text: `${firstExercise?.observableObjective ?? "O exercício ainda não possui objetivo observável."} Competências desenvolvidas: ${competencyNames || "não declaradas"}.`,
      };
    case "assisted":
      return {
        source: "Condições do Exercise",
        text: `${assistedExercise?.practiceConditions ?? "As condições da prática ainda não foram declaradas."} Competências desenvolvidas: ${competencyNames || "não declaradas"}.`,
      };
    case "recording":
      return { source: "Mission", text: data.mission.expectedEvidence };
    case "feedback":
    case "evidence":
      return {
        source: "Evidence Engine",
        text:
          evidenceGaps ||
          "O Evidence Engine ainda não possui uma próxima observação para esta competência.",
      };
    case "assessment":
      return {
        source: "Assessment",
        text: data.assessments[0]?.purpose ?? "Nenhum Assessment foi vinculado.",
      };
    case "next":
      return { source: "Coach", text: data.coach.nextMessage };
    default:
      return { source: "Mission", text: data.mission.motivation };
  }
}

function DefinitionGrid({ items }: { items: Array<[string, string]> }) {
  return (
    <div className="mt-4 grid gap-px bg-border md:grid-cols-3">
      {items.map(([label, value]) => (
        <Definition key={label} label={label}>
          {value}
        </Definition>
      ))}
    </div>
  );
}

function Definition({ label, children }: { label: string; children: ReactNode }) {
  return (
    <div className="bg-surface p-4">
      <p className="label-tech">{label}</p>
      <div className="mt-2 text-xs leading-relaxed">{children}</div>
    </div>
  );
}

function NumberedList({ items }: { items: string[] }) {
  return (
    <ol className="space-y-3">
      {items.map((item, index) => (
        <li
          key={`${index}-${item}`}
          className="grid grid-cols-[28px_1fr] gap-3 text-sm leading-relaxed"
        >
          <span className="num flex size-7 items-center justify-center border border-context-learn/40 text-context-learn">
            {index + 1}
          </span>
          <span className="pt-1">{item}</span>
        </li>
      ))}
    </ol>
  );
}

function Lead({ children }: { children: ReactNode }) {
  return <p className="max-w-3xl text-base leading-7 text-foreground">{children}</p>;
}

function EmptyTeachingState({ text }: { text: string }) {
  return (
    <p className="flex items-start gap-2 border border-dashed border-border p-4 text-xs leading-relaxed text-muted-foreground">
      <CircleAlert className="mt-0.5 size-4 shrink-0 text-warn" /> {text}
    </p>
  );
}
